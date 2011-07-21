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

package org.jasig.schedassist.impl.relationship.advising;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.impl.relationship.RelationshipDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring {@link SimpleJdbcDaoSupport} backed mechanism for
 * loading and refreshing the AdvisorList data.
 * 
 * The AdvisorList is a semi-colon delimited text file that is automatically sent
 * every 6 hours to a location on the file system.
 * 
 * It's format is the following:
 <pre>
 student1@wisc.edu;One,Test Student;0000000001;9010000001;Graduate;L&S;College of Letters and Science;4.000;1092;Fall 2008-2009;87.000;PHD 922L&S;;NWD;G922L;Sociology - LS;PHD 922L&S;Sociology PHD-L&S;Sociology;advisor1@wisc.edu;One,Advisor;1000000001;9020000001;ADVR;Academic
 </pre>
 *
 * For the purposes of this application we are only interested in the following fields:
 * <ul>
 <li>Student ISIS Emplid (field 3)</li>
 <li>Term ID number (field 9)</li>
 <li>Term description (field 10)</li>
 <li>Academic Program description (field 16)</li>
 <li>Advisor ISIS Emplid (field 22)</li>
 <li>Advisor type (field 25)</li>
 <li>Committee Role (field 26)</li>
 </ul>
 *
 * The {@link #parseLine(String)} method of this class creates a {@link StudentAdvisorAssignment}
 * from such a line. The Academic Program description is stored as the advisorRelationshipDescription
 * on the returned object, as a student may have multiple advisors, and this field provides
 * the distinction.
 * 
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AdvisorListDataSourceImpl.java 2905 2010-11-18 21:05:33Z npblair $
 */
public class AdvisorListRelationshipDataSourceImpl implements RelationshipDataSource, InitializingBean {

	private Log LOG = LogFactory.getLog(this.getClass());
	public static final String CONFIG = System.getProperty(
			AdvisorListRelationshipDataSourceImpl.class.getPackage().getName() + ".CONFIG",
			"advisorlist-dataSource.xml");
	
	private Resource advisorListResource;
	private Long resourceLastModified = -1L;
	private Date lastReloadTimestamp;
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * @param advisorListResource the advisorListResource to set
	 */
	public void setAdvisorListResource(Resource advisorListResource) {
		this.advisorListResource = advisorListResource;
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
		if(advisorListResource == null) {
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
	 -Dorg.jasig.schedassist.impl.relationship.advising.AdvisorListRelationshipDataSourceImpl.CONFIG
	 </pre>
	 * The default value for this property is "advisorlist-dataSource.xml" (in the default package).
	 * This Spring applicationContext must contain a fully configured {@link RelationshipDataSource}
	 * bean.
	 * 
	 * @param args
	 */
	public static void main(String [] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);

		RelationshipDataSource advisorListDataSource = (RelationshipDataSource) context.getBean("advisorListDataSource");

		advisorListDataSource.reloadData();
	}

	/**
	 * This method deletes all existing rows from the isis_records table, then invokes
	 * {@link #batchLoadData(Resource)} to refresh it.
	 * 
	 * This method is marked with Spring's {@link Transactional} annotation, and if
	 * the available application is running should only be executed when transactional 
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
			String currentTerm = TermCalculator.getCurrentTerm();
			if(isResourceUpdated(advisorListResource)) {
				LOG.info("resource updated, reloading advisorList data");
				List<StudentAdvisorAssignment> records = readResource(advisorListResource, currentTerm);

				LOG.info("deleting all existing records from advisorlist table");
				StopWatch stopWatch = new StopWatch();
				stopWatch.start();
				this.getJdbcTemplate().execute("delete from advisorlist");
				long deleteTime = stopWatch.getTime();
				LOG.info("finished deleting existing (" + deleteTime + " msec), starting batch insert");
				stopWatch.reset();
				stopWatch.start();
				SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(records.toArray());
				this.getSimpleJdbcTemplate().batchUpdate(
						"insert into advisorlist (advisor_emplid, advisor_relationship, student_emplid, term_description, term_number, advisor_type, committee_role) values (:advisorEmplid, :advisorRelationshipDescription, :studentEmplid, :termDescription, :termNumber, :advisorType, :committeeRole)",
						batch);
				long insertTime = stopWatch.getTime();
				stopWatch.stop();
				LOG.info("batch insert complete (" + insertTime + " msec)");
				LOG.info("reloadData complete (total time: " + (insertTime + deleteTime) + " msec)");
				this.lastReloadTimestamp = new Date();
				try {
					this.resourceLastModified = advisorListResource.lastModified();
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
	 * Converts a semi-colon delimited line of text into a {@link StudentAdvisorAssignment}.
	 * 
	 * Uses {@link String#split(String)}.
	 * 
	 <ol>
	 <li>{@link StudentAdvisorAssignment#setAdvisorEmplid(String)} - field #22 (index 21)</li>
	 <li>{@link StudentAdvisorAssignment#setAdvisorRelationshipDescription(String)} - field #16 (index 15)</li>
	 <li>{@link StudentAdvisorAssignment#setStudentEmplid(String)} - field #3 (index 2)</li>
	 <li>{@link StudentAdvisorAssignment#setTermDescription(String)} - field #11 (index 10)</li>
	 <li>{@link StudentAdvisorAssignment#setTermNumber(String)} - field #10 (index 9)</li>
	 <li>{@link StudentAdvisorAssignment#setAdvisorType(String)} - field #25 (index 24)</li>
	 <li>{@link StudentAdvisorAssignment#setCommitteeRole(String)} - field #26 (index 25)</li>
	 </ol>
	 *
	 * @param line
	 * @return
	 */
	protected StudentAdvisorAssignment parseLine(final String line) {
		LOG.debug("parseLine: " + line);
		String[] tokens = line.split(";");

		if(tokens.length != 25 && tokens.length != 26) {
			LOG.debug("returning null for malformed line (tokens length " + tokens.length +"): " + line);
			return null;
		}
		StudentAdvisorAssignment record = new StudentAdvisorAssignment();
		record.setAdvisorEmplid(tokens[21]);
		record.setAdvisorRelationshipDescription(tokens[15]);
		record.setStudentEmplid(tokens[2]);
		record.setTermNumber(tokens[8]);
		record.setTermDescription(tokens[9]);
		record.setAdvisorType(tokens[24]);
		if(tokens.length == 26) {
			record.setCommitteeRole(tokens[25]);
		}
		LOG.debug("parseLine result: " + record);
		return record;
	}

	/**
	 * Read each line from the supplied {@link Resource}, invokes
	 * {@link #parseLine(String)}, and return a {@link List} of the 
	 * corresponding {@link StudentAdvisorAssignment}s.
	 * 
	 * If the {@link StudentAdvisorAssignment#getTermNumber()} returned from {@link #parseLine(String)} is not 
	 * equal to or greater than the term argument, it is not added to the returned {@link List}.
	 * 
	 * @see TermCalculator#termGreaterThanOrEquals(String, String)
	 * @param resource
	 * @param term
	 * @return
	 * @throws IOException
	 */
	protected List<StudentAdvisorAssignment> readResource(final Resource resource, final String term)  {
		List<StudentAdvisorAssignment> results = new ArrayList<StudentAdvisorAssignment>();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(resource.getInputStream()));

			String currentLine = reader.readLine();
			while(null != currentLine) {
				StudentAdvisorAssignment record = parseLine(currentLine);
				if(null != record) {
					if(TermCalculator.termGreaterThanOrEquals(record.getTermNumber(), term)) {
						results.add(record);
					}
				}
				currentLine = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			throw new AdvisorListDataException("IOException in readResource", e);
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.relationship.RelationshipDataSource#getLastReloadTimestamp()
	 */
	@Override
	public Date getLastReloadTimestamp() {
		return null == this.lastReloadTimestamp ? null : new Date(this.lastReloadTimestamp.getTime());
	}

	
}
