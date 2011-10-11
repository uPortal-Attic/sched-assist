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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
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


/**
 * Implementation of {@link RelationshipDao} that returns a given 
 * student's ({@link IScheduleVisitor}) UW-Madison assigned advisors.
 * 
 * This implementation is backed by the data stored in the Scheduling Assistant
 * database by the {@link AdvisorListRelationshipDataSourceImpl}.
 * 
 * The returned {@link IScheduleOwner}s have the following data stored in 
 * the "relationship" field"
 <pre>
 Advisor, academic program, term description
 </pre>
 * Examples: 
 <pre>
 Advisor, Academic, Spring 2008-2009
 Advisor, Honors - Letters & Science, Spring 2008-2009
 </pre>
 *  
 * Requires an {@link OwnerDao}, a {@link VisitorDao}, and a {@link ICalendarAccountDao} be set.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: StudentAdvisorRelationshipDaoImpl.java 2905 2010-11-18 21:05:33Z npblair $
 */
public class StudentAdvisorRelationshipDaoImpl 
implements RelationshipDao {

	private Log LOG = LogFactory.getLog(this.getClass());
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private String studentEmplidAttributeName = "wisceduisisstudentemplid";
	private String advisorEmplidAttributeName = "wisceduisisadvisoremplid";
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
	 * @param studentEmplidAttributeName the studentEmplidAttributeName to set
	 */
	public void setStudentEmplidAttributeName(String studentEmplidAttributeName) {
		this.studentEmplidAttributeName = studentEmplidAttributeName;
	}
	/**
	 * @param advisorEmplidAttributeName the advisorEmplidAttributeName to set
	 */
	public void setAdvisorEmplidAttributeName(String advisorEmplidAttributeName) {
		this.advisorEmplidAttributeName = advisorEmplidAttributeName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.RelationshipDao#forOwner(org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public List<Relationship> forOwner(IScheduleOwner owner) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("enter StudentAdvisorRelationshipDaoImpl#forOwner " + owner);
		}
		ICalendarAccount ownerCalendarAccount = owner.getCalendarAccount();
		String advisorEmplid = ownerCalendarAccount.getAttributeValue(advisorEmplidAttributeName);
		if(StringUtils.isBlank(advisorEmplid)) {
			return Collections.emptyList();
		}
		List<StudentAdvisorAssignment> isisRecords = this.simpleJdbcTemplate.query(
				"select * from advisorlist where advisor_emplid = ?",
				new StudentAdvisorAssignmentRowMapper(),
				advisorEmplid);
		List<Relationship> results = new ArrayList<Relationship>();

		for(StudentAdvisorAssignment record : isisRecords) {
			ICalendarAccount calUser = calendarAccountDao.getCalendarAccount("wisceduisisstudentemplid", record.getStudentEmplid());
			if(null == calUser) {
				LOG.debug("no calendar user found for " + record);
				continue;
			}
			try {
				IScheduleVisitor visitor = visitorDao.toVisitor(calUser);
				if(null != visitor) {			
					Relationship relationship = new Relationship();
					relationship.setOwner(owner);
					relationship.setVisitor(visitor);
					relationship.setDescription(buildDescription(record));
					results.add(relationship);

					if(LOG.isDebugEnabled()) {
						LOG.debug("found advisor " + owner + " for student " + visitor);
					}
				} else if(LOG.isDebugEnabled()) {
					LOG.debug("advisor assigned, but not registered as owner " + record);
				}
			} catch (NotAVisitorException e) {
				LOG.debug("calendar user found but not a visitor " + record);
			} 
		}

		return results;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.RelationshipDao#forVisitor(org.jasig.schedassist.model.IScheduleVisitor)
	 */
	@Override
	public List<Relationship> forVisitor(IScheduleVisitor visitor) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("enter StudentAdvisorRelationshipDaoImpl#forVisitor " + visitor);
		}
		String studentEmplid = visitor.getCalendarAccount().getAttributeValue(studentEmplidAttributeName);
		if(StringUtils.isBlank(studentEmplid)) {
			return Collections.emptyList();
		}
		List<StudentAdvisorAssignment> isisRecords = this.simpleJdbcTemplate.query(
				"select * from advisorlist where student_emplid = ?",
				new StudentAdvisorAssignmentRowMapper(),
				studentEmplid);
		List<Relationship> results = new ArrayList<Relationship>();
		for(StudentAdvisorAssignment record : isisRecords) {
			ICalendarAccount calUser = calendarAccountDao.getCalendarAccount("wisceduisisadvisoremplid", record.getAdvisorEmplid());
			if(null == calUser) {
				LOG.debug("no calendar user found for " + record);
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
					relationship.setDescription(buildDescription(record));
					results.add(relationship);

					if(LOG.isDebugEnabled()) {
						LOG.debug("found advisor " + owner + " for student " + visitor);
					}
				} else if(LOG.isDebugEnabled()) {
					LOG.debug("advisor assigned, but not sharing with students " + record);
				}
			} else {
				LOG.debug("advisor assigned, but not registered as owner " + record);
			}
		}
		
		return results;
	}

	/**
	 * Build the description {@link String} from the {@link StudentAdvisorAssignment}.
	 * This is formatted like the following:
	 <pre>
	 [CommitteeRole ]Advisor, <assignment.getAdvisorRelationshipDescription()>, <assignment.getTermDescription()>
	 </pre>
	 * Example:
	 <pre>
	 Academic Advisor, Electrical Engineering, Spring 2008-2009
	 </pre>
	 * @param assignment
	 * @return
	 */
	protected String buildDescription(StudentAdvisorAssignment assignment) {
		StringBuilder description = new StringBuilder();
		CommitteeRole committeeRole = CommitteeRole.fromValue(assignment.getCommitteeRole());
		if(CommitteeRole.UNDEFINED.equals(committeeRole)) {
			// do nothing with committeerole
		} else if(CommitteeRole.CAREER.equals(committeeRole) || !committeeRole.getValue().equals(assignment.getAdvisorType())) {
			// if committeeRole is "Career" or doesn't match the advisor type, inject it at the front
			description.append(assignment.getCommitteeRole());
			description.append(" ");
		}
		description.append(assignment.getAdvisorType());
		description.append(" Advisor, ");
		description.append(assignment.getAdvisorRelationshipDescription());
		description.append(", ");
		description.append(assignment.getTermDescription());
		return description.toString();
	}
}
