/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.schedassist.impl.relationship;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.com.bytecode.opencsv.CSVReader;


/**
 * Simple {@link RelationshipDataSource} backed by a CSV file.
 * The CSV file should contain 3 columns, in this order:
 <pre>
 ownerIdentifier, visitorIdentifier, relationshipDescription
 </pre>
 * 
 * 
 * @author Nicholas Blair
 * @version $Id: CSVRelationshipDataSourceImpl.java 147 2011-06-10 15:03:02Z npblair $
 */
public class CSVRelationshipDataSourceImpl implements RelationshipDataSource, InitializingBean {

	private Log LOG = LogFactory.getLog(this.getClass());
	/**
	 * System property to specify the path (on the classpath) to the Spring ApplicationContext for the {@link #main(String[])} method.
	 */
	public static final String CONFIG = System.getProperty(
			CSVRelationshipDataSourceImpl.class.getPackage().getName() + ".CONFIG",
			"csv-relationship-dataSource.xml");
	
	private Resource csvResource;
	private Long resourceLastModified = -1L;
	private Date lastReloadTimestamp;
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * @param csvResource the csvResource to set
	 */
	public void setCsvResource(Resource csvResource) {
		this.csvResource = csvResource;
	}
	/**
	 * @return the simpleJdbcTemplate
	 */
	protected SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}
	/**
	 * @return the jdbcTemplate
	 */
	protected JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if(csvResource == null) {
			throw new IllegalStateException("advisorListResource is required");
		}
		
		if(simpleJdbcTemplate == null) {
			throw new IllegalStateException("dataSource is required");
		}
	}

	/**
	 * Main method to allow command line invocation of the {@link #reloadData(Resource)} method.
	 * This method attempts to load a {@link ClassPathXmlApplicationContext} from the 
	 * location specified in the System property:
	 <pre>
	 -Dorg.jasig.schedassist.impl.relationship.CSVRelationshipDataSourceImpl.CONFIG
	 </pre>
	 * The default value for this property is "csv-relationship-dataSource.xml" (in the default package).
	 * This Spring applicationContext must contain a fully configured {@link RelationshipDataSource}
	 * bean.
	 * 
	 * @param args
	 */
	public static void main(String [] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);

		RelationshipDataSource csvDataSource = (RelationshipDataSource) context.getBean("csvDataSource");

		csvDataSource.reloadData();
	}

	/**
	 * This method deletes all existing rows from the isis_records table, then invokes
	 * {@link #batchLoadData(Resource)} to refresh it.
	 * 
	 * This method is marked with Spring's {@link Transactional} annotation, and if
	 * the Scheduling Assistant application is running should only be executed when transactional 
	 * support is available.
	 * 
	 * @see Transactional
	 * @param resource
	 * @throws IOException
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public synchronized void reloadData() {
		final String propertyValue = System.getProperty("org.jasig.schedassist.runScheduledTasks", "true");
		if(Boolean.parseBoolean(propertyValue)) {
			if(isResourceUpdated(csvResource)) {
				LOG.info("resource updated, reloading advisorList data");
				//List<StudentAdvisorAssignment> records = readResource(advisorListResource, currentTerm);
				List<CSVRelationship> records = new ArrayList<CSVRelationship>();
				try {
					records = readCSVResource(csvResource);
				} catch (IOException e) {
					LOG.error("caught IOException reading csv data source", e);
					return;
				}

				if(records.isEmpty()) {
					LOG.warn("resource returned empty set, skipping reloadData");
					return;
				}

				LOG.info("deleting all existing records from csv_relationships table");
				StopWatch stopWatch = new StopWatch();
				stopWatch.start();
				this.getJdbcTemplate().execute("delete from csv_relationships");
				long deleteTime = stopWatch.getTime();
				LOG.info("finished deleting existing (" + deleteTime + " msec), starting batch insert");
				stopWatch.reset();
				stopWatch.start();
				SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(records.toArray());
				this.getSimpleJdbcTemplate().batchUpdate(
						"insert into csv_relationships (owner_id, visitor_id, rel_description) values (:ownerIdentifier, :visitorIdentifier, :relationshipDescription)",
						batch);
				long insertTime = stopWatch.getTime();
				stopWatch.stop();
				LOG.info("batch insert complete (" + insertTime + " msec)");
				LOG.info("reloadData complete (total time: " + (insertTime + deleteTime) + " msec)");
				this.lastReloadTimestamp = new Date();
				try {
					this.resourceLastModified = csvResource.lastModified();
				} catch (IOException e) {
					LOG.debug("ignoring IOException from accessing Resource.lastModified()");
				}
			} else {
				LOG.info("resource not modified since last reload, skipping");
			}
		} else {
			LOG.debug("ignoring reloadData as 'org.jasig.schedassist.runScheduledTasks' set to false");
		}
	}

	/**
	 * 
	 * @param resource
	 * @return
	 */
	protected boolean isResourceUpdated(final Resource resource) {
		boolean result = true;
		try {
			result = (this.resourceLastModified == -1L) || (resource.lastModified() > this.resourceLastModified);
		} catch (IOException e) {
			// this exception will occur if the Resource is not representable as a File
			// in this case - always return true?
		}
		return result;
	}
	
	/**
	 * Read the {@link Resource} argument as a CSV file and extract a {@link List}
	 * of {@link CSVRelationship}s.
	 * 
	 * @see CSVReader
	 * @param resource
	 * @return a never null, but potentially empty list of {@link CSVRelationship}s.
	 */
	protected List<CSVRelationship> readCSVResource(final Resource resource) throws IOException {
		Set<CSVRelationship> results = new HashSet<CSVRelationship>();
		CSVReader lineReader = new CSVReader(new InputStreamReader(resource.getInputStream()));
		String [] tokens = lineReader.readNext();
		while(null != tokens) {
			if(tokens.length == 3) {
				CSVRelationship relationship = new CSVRelationship();
				relationship.setOwnerIdentifier(tokens[0]);
				relationship.setVisitorIdentifier(tokens[1]);
				relationship.setRelationshipDescription(tokens[2]);
				results.add(relationship);
			} else {
				LOG.debug("skipping CSV line with tokens.length != 3, " + Arrays.toString(tokens));
			}
			
			tokens = lineReader.readNext();
		}
		
		return new ArrayList<CSVRelationship>(results);
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.relationship.RelationshipDataSource#getLastReloadTimestamp()
	 */
	@Override
	public Date getLastReloadTimestamp() {
		return null == this.lastReloadTimestamp ? null : new Date(this.lastReloadTimestamp.getTime());
	}

}
