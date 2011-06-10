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

package org.jasig.schedassist.impl;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;



/**
 * Simple class to help initialize the database.
 * 
 * The main method of this class attempts to load a Spring
 * {@link ApplicationContext} from the filename on the classpath
 * specified in the <i>org.jasig.schedassist.impl.InitializeAvailableDatabase.CONFIG</i>
 * {@link System} property.
 * The default value for this property is "database-SAMPLE.xml" (root package on the classpath).
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: InitializeAvailableDatabase.java 2104 2010-05-11 17:46:01Z npblair $
 */
public final class InitializeSchedulingAssistantDatabase {

	public static final String CONFIG = System.getProperty(
			InitializeSchedulingAssistantDatabase.class.getPackage().getName() + ".InitializeAvailableDatabase.CONFIG", 
			"database.xml");
	private static Log LOG = LogFactory.getLog(InitializeSchedulingAssistantDatabase.class);
	private JdbcTemplate jdbcTemplate;
	/**
	 * 
	 * @param dataSource
	 */
	public void setDataSource(final DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * Optionally accepts 1 argument.
	 * If the argument evaluates to true ({@link Boolean#parseBoolean(String)}, the main method
	 * will NOT run the SQL statements in the "destroyDdl" Resource.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		LOG.info("loading applicationContext: " + CONFIG);
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
		
		boolean firstRun = false;
		if(args.length == 1) {
			firstRun = Boolean.parseBoolean(args[0]);
		}
		InitializeSchedulingAssistantDatabase init = new InitializeSchedulingAssistantDatabase();
		init.setDataSource((DataSource) context.getBean("dataSource"));

		if(!firstRun) {
			Resource destroyDdl = (Resource) context.getBean("destroyDdl");
			if(null != destroyDdl) {
				String destroySql = IOUtils.toString(destroyDdl.getInputStream());
				init.executeDdl(destroySql);
				LOG.warn("existing tables removed");
			}
		}
		
		Resource createDdl = (Resource) context.getBean("createDdl");
		String createSql = IOUtils.toString(createDdl.getInputStream());
		
		init.executeDdl(createSql);
		LOG.info("database initialization complete");
	}

	/**
	 * 
	 * @param sql
	 */
	@Transactional
	protected void executeDdl(final String sql) {
		LOG.debug("attempting to execute: " + sql);
		this.jdbcTemplate.execute(sql);
	}
	
}
