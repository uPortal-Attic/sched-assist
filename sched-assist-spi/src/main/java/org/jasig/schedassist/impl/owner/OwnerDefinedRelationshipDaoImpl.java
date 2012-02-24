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

package org.jasig.schedassist.impl.owner;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.MutableRelationshipDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.impl.visitor.VisitorDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * {@link MutableRelationshipDao} implementation that is backed by the {@link IScheduleOwner}
 * defined lists (stored in the owner_adhoc_authz table).
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: OwnerDefinedRelationshipDaoImpl.java 2034 2010-04-30 13:35:32Z npblair $
 */
@Service
@Qualifier("adhoc")
public class OwnerDefinedRelationshipDaoImpl implements MutableRelationshipDao {

	private Log LOG = LogFactory.getLog(this.getClass());
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private ICalendarAccountDao calendarAccountDao;
	private OwnerDao ownerDao;
	private VisitorDao visitorDao;
	private String identifyingAttributeName = "uid";

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
	public void setCalendarAccountDao(@Qualifier("composite") ICalendarAccountDao calendarAccountDao) {
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
	 * @param visitorDao the visitorDao to set
	 */
	@Autowired
	public void setVisitorDao(VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}
	/**
	 * 
	 * @param identifyingAttributeName
	 */
	@Value("${users.visibleIdentifierAttributeName:uid}")
	public void setIdentifyingAttributeName(String identifyingAttributeName) {
		this.identifyingAttributeName = identifyingAttributeName;
	}
	/**
	 * 
	 * @return the attribute used to commonly uniquely identify an account
	 */
	public String getIdentifyingAttributeName() {
		return identifyingAttributeName;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.RelationshipDao#forOwner(org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public List<Relationship> forOwner(final IScheduleOwner owner) {
		final String ownerIdentifier = getIdentifyingAttribute(owner.getCalendarAccount());
		
		List<OwnerDefinedRelationship> relationships = this.simpleJdbcTemplate.query(
				"select * from owner_adhoc_authz where owner_username = ?", 
				new OwnerDefinedRelationshipRowMapper(), 
				ownerIdentifier);

		List<Relationship> results = new ArrayList<Relationship>();
		for(OwnerDefinedRelationship stored : relationships) {
			ICalendarAccount calendarUser = calendarAccountDao.getCalendarAccount(identifyingAttributeName, stored.getVisitorUsername());
			if(null == calendarUser) {
				LOG.info("calendarAccount not found for owner in " + stored);
				continue;
			}
			try {
				IScheduleVisitor visitor = visitorDao.toVisitor(calendarUser);
				
				Relationship relationship = new Relationship();
				relationship.setOwner(owner);
				relationship.setVisitor(visitor);
				relationship.setDescription(stored.getRelationship());

				results.add(relationship);
			} catch (NotAVisitorException e) {
				LOG.info("calendarAccount found but not a visitor " + stored);
			}
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.RelationshipDao#forVisitor(org.jasig.schedassist.model.IScheduleVisitor)
	 */
	@Override
	public List<Relationship> forVisitor(final IScheduleVisitor visitor) {
		final String visitorIdentifier = getIdentifyingAttribute(visitor.getCalendarAccount());
		List<OwnerDefinedRelationship> relationships = this.simpleJdbcTemplate.query(
				"select * from owner_adhoc_authz where visitor_username = ?", 
				new OwnerDefinedRelationshipRowMapper(), 
				visitorIdentifier);

		List<Relationship> results = new ArrayList<Relationship>();
		for(OwnerDefinedRelationship stored : relationships) {
			ICalendarAccount calendarUser = calendarAccountDao.getCalendarAccount(identifyingAttributeName, stored.getOwnerUsername());
			if(null == calendarUser) {
				LOG.info("calendarAccount not found for owner in " + stored);
				continue;
			}

			IScheduleOwner owner = ownerDao.locateOwner(calendarUser);
			if(null != owner) {
				Relationship relationship = new Relationship();
				relationship.setOwner(owner);
				relationship.setVisitor(visitor);
				relationship.setDescription(stored.getRelationship());

				results.add(relationship);
			} else {
				LOG.warn("no ScheduleOwner registered for record " + stored);
			}
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.MutableRelationshipDao#createRelationship(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.IScheduleVisitor, java.lang.String)
	 */
	@Override
	public Relationship createRelationship(IScheduleOwner owner, IScheduleVisitor visitor,
			String relationship) {
		final String ownerIdentifier = getIdentifyingAttribute(owner.getCalendarAccount());
		final String visitorIdentifier = getIdentifyingAttribute(visitor.getCalendarAccount());
		OwnerDefinedRelationship stored = internalRetrieveRelationship(ownerIdentifier, visitorIdentifier);	
		if(null == stored) {
			this.simpleJdbcTemplate.update(
					"insert into owner_adhoc_authz (owner_username, relationship, visitor_username) values (?, ?, ?)",
					ownerIdentifier,
					relationship,
					visitorIdentifier);
			LOG.info("stored owner defined relationship: " + owner + ", " + relationship + ", " + visitor);
		} else {
			this.simpleJdbcTemplate.update("update owner_adhoc_authz set relationship = ? where owner_username = ? and visitor_username = ?",
					relationship,
					ownerIdentifier,
					visitorIdentifier);
			LOG.info("relationship already exists for " + owner + " and " + visitor + ", updated description");
		}
		Relationship result = new Relationship();
		result.setOwner(owner);
		result.setVisitor(visitor);
		result.setDescription(relationship);	
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.MutableRelationshipDao#destroyRelationship(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.IScheduleVisitor)
	 */
	@Override
	public void destroyRelationship(IScheduleOwner owner, IScheduleVisitor visitor) {
		final String ownerIdentifier = getIdentifyingAttribute(owner.getCalendarAccount());
		final String visitorIdentifier = getIdentifyingAttribute(visitor.getCalendarAccount());
		internalDeleteRelationship(ownerIdentifier, visitorIdentifier);
	}

	/**
	 * 
	 * @param account
	 * @return the value of {@link OwnerDefinedRelationshipDaoImpl#getIdentifyingAttributeName()} for the account
	 * @throws IllegalStateException if the account does not have a value for that attribute.
	 */
	protected String getIdentifyingAttribute(ICalendarAccount account) {
		final String ownerIdentifier = account.getAttributeValue(identifyingAttributeName);
		if(StringUtils.isBlank(ownerIdentifier)) {
			LOG.error(identifyingAttributeName + " attribute not present for calendarAccount " + account + "; this scenario suggests either a problem with the account, or a deployment configuration problem. Please set the 'users.visibleIdentifierAttributeName' appropriately.");
			throw new IllegalStateException(identifyingAttributeName + " attribute not present for calendarAccount " + account);
		}
		return ownerIdentifier;
	}
	/**
	 * 
	 * @param ownerUsername
	 * @param visitorUsername
	 */
	protected void internalDeleteRelationship(final String ownerUsername, final String visitorUsername) {
		int rowsAffected = this.simpleJdbcTemplate.update(
				"delete from owner_adhoc_authz where owner_username = ? and visitor_username = ?", 
				ownerUsername,
				visitorUsername);
		if(rowsAffected > 0 ){
			LOG.info("removed owner defined relationship: " + ownerUsername + ", " + visitorUsername);
		} else {
			LOG.debug("no authorization stored for visitor " + visitorUsername + " and owner " + ownerUsername);
		}
	}

	/**
	 * 
	 * @param ownerUsername
	 * @param visitorUsername
	 * @return
	 */
	protected OwnerDefinedRelationship internalRetrieveRelationship(final String ownerUsername, final String visitorUsername) {
		List<OwnerDefinedRelationship> relationships = this.simpleJdbcTemplate.query(
				"select * from owner_adhoc_authz where owner_username = ? and visitor_username = ?", 
				new OwnerDefinedRelationshipRowMapper(), 
				ownerUsername,
				visitorUsername);

		return (OwnerDefinedRelationship) DataAccessUtils.singleResult(relationships);
	}
}
