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

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.impl.visitor.VisitorDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Spring JDBC {@link RelationshipDao} backed by the same table that is
 * populated by the {@link CSVRelationshipDataSourceImpl}.
 * 
 * @author Nicholas Blair
 * @version $Id: CSVRelationshipDaoImpl.java 147 2011-06-10 15:03:02Z npblair $
 */
@Service
@Qualifier("academicAdvisor")
public class CSVRelationshipDaoImpl implements RelationshipDao {

	private Log LOG = LogFactory.getLog(this.getClass());
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private String visitorAttributeName = "uid";
	private String ownerAttributeName = "uid";
	private ICalendarAccountDao calendarAccountDao;
	private OwnerDao ownerDao;
	private VisitorDao visitorDao;
	
	/**
	 * 
	 * @param dataSource
	 */
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(@Qualifier("people") ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param visitorAttributeName the studentEmplidAttributeName to set
	 */
	public void setVisitorAttributeName(String visitorAttributeName) {
		this.visitorAttributeName = visitorAttributeName;
	}
	/**
	 * @param ownerAttributeName the advisorEmplidAttributeName to set
	 */
	public void setOwnerAttributeName(String ownerAttributeName) {
		this.ownerAttributeName = ownerAttributeName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.RelationshipDao#forOwner(org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public List<Relationship> forOwner(IScheduleOwner owner) {
		ICalendarAccount ownerCalendarAccount = owner.getCalendarAccount();
		String ownerId = ownerCalendarAccount.getAttributeValue(ownerAttributeName);
		List<CSVRelationship> csvRecords = this.simpleJdbcTemplate.query(
				"select * from csv_relationships where owner_id = ?",
				new CSVRelationshipRowMapper(),
				ownerId);
		List<Relationship> results = new ArrayList<Relationship>();

		for(CSVRelationship record : csvRecords) {
			ICalendarAccount calUser = calendarAccountDao.getCalendarAccount(this.visitorAttributeName, record.getVisitorIdentifier());
			if(null == calUser) {
				LOG.debug("no visitor calendarAccount found for " + record);
				continue;
			}
			try {
				IScheduleVisitor visitor = visitorDao.toVisitor(calUser);
				if(null != visitor) {			
					Relationship relationship = new Relationship();
					relationship.setOwner(owner);
					relationship.setVisitor(visitor);
					relationship.setDescription(record.getRelationshipDescription());
					results.add(relationship);

					if(LOG.isDebugEnabled()) {
						LOG.debug("found relationship " + relationship);
					}
				} else if(LOG.isDebugEnabled()) {
					LOG.debug("owner assigned, but not registered as owner in " + record);
				}
			} catch (NotAVisitorException e) {
				LOG.debug("calendarAccount found but not a visitor in " + record);
			} 
		}

		return results;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.RelationshipDao#forVisitor(org.jasig.schedassist.model.IScheduleVisitor)
	 */
	@Override
	public List<Relationship> forVisitor(IScheduleVisitor visitor) {
		String visitorId = visitor.getCalendarAccount().getAttributeValue(visitorAttributeName);
		List<CSVRelationship> csvRecords = this.simpleJdbcTemplate.query(
				"select * from csv_relationships where visitor_id = ?",
				new CSVRelationshipRowMapper(),
				visitorId);
		List<Relationship> results = new ArrayList<Relationship>();
		for(CSVRelationship record : csvRecords) {
			ICalendarAccount calUser = calendarAccountDao.getCalendarAccount(this.ownerAttributeName, record.getOwnerIdentifier());
			if(null == calUser) {
				LOG.debug("no owner calendarAccount found for " + record);
				continue;
			}
			IScheduleOwner owner = ownerDao.locateOwner(calUser);
			if(null != owner) {
				String preferenceValue = owner.getPreference(Preferences.ADVISOR_SHARE_WITH_STUDENTS);
				boolean sharedWithStudents = Boolean.valueOf(preferenceValue);
				if(sharedWithStudents) {
					Relationship relationship = new Relationship();
					relationship.setOwner(owner);
					relationship.setVisitor(visitor);
					relationship.setDescription(record.getRelationshipDescription());
					results.add(relationship);

					if(LOG.isDebugEnabled()) {
						LOG.debug("found relationship " + relationship);
					}
				} else if(LOG.isDebugEnabled()) {
					LOG.debug("owner assigned, but not sharing with students for " + record);
				}
			} else {
				LOG.debug("owner assigned, but not registered as owner for " + record);
			}
		}
		
		return results;
	}

}
