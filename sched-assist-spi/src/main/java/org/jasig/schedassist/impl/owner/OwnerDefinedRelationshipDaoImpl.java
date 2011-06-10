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

	/**
	 * 
	 * @param dataSource
	 */
	@Autowired(required=true)
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired(required=true)
	public void setCalendarAccountDao(@Qualifier("composite") ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired(required=true)
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param visitorDao the visitorDao to set
	 */
	@Autowired(required=true)
	public void setVisitorDao(VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.RelationshipDao#forOwner(org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public List<Relationship> forOwner(final IScheduleOwner owner) {
		List<OwnerDefinedRelationship> relationships = this.simpleJdbcTemplate.query(
				"select * from owner_adhoc_authz where owner_username = ?", 
				new OwnerDefinedRelationshipRowMapper(), 
				owner.getCalendarAccount().getUsername());

		List<Relationship> results = new ArrayList<Relationship>();
		for(OwnerDefinedRelationship stored : relationships) {
			ICalendarAccount calendarUser = calendarAccountDao.getCalendarAccount(stored.getVisitorUsername());
			if(null == calendarUser) {
				LOG.warn("calendarUser not found for owner in " + stored);
				continue;
			}
			try {
				IScheduleVisitor visitor = visitorDao.toVisitor(calendarUser);
				if(null != owner) {
					Relationship relationship = new Relationship();
					relationship.setOwner(owner);
					relationship.setVisitor(visitor);
					relationship.setDescription(stored.getRelationship());

					results.add(relationship);
				} else {
					LOG.warn("owner not registered for record " + stored);
				}
			} catch (NotAVisitorException e) {
				LOG.warn("calendarUser found but not a visitor " + stored);
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
		List<OwnerDefinedRelationship> relationships = this.simpleJdbcTemplate.query(
				"select * from owner_adhoc_authz where visitor_username = ?", 
				new OwnerDefinedRelationshipRowMapper(), 
				visitor.getCalendarAccount().getUsername());

		List<Relationship> results = new ArrayList<Relationship>();
		for(OwnerDefinedRelationship stored : relationships) {
			ICalendarAccount calendarUser = calendarAccountDao.getCalendarAccount(stored.getOwnerUsername());
			if(null == calendarUser) {
				LOG.warn("calendarUser not found for owner in " + stored);
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
				LOG.warn("owner not registered for record " + stored);
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
		OwnerDefinedRelationship stored = internalRetrieveRelationship(owner.getCalendarAccount().getUsername(), visitor.getCalendarAccount().getUsername());	
		if(null == stored) {
			this.simpleJdbcTemplate.update(
					"insert into owner_adhoc_authz (owner_username, relationship, visitor_username) values (?, ?, ?)",
					owner.getCalendarAccount().getUsername(),
					relationship,
					visitor.getCalendarAccount().getUsername());
			LOG.info("stored owner defined relationship: " + owner.getCalendarAccount().getUsername() + ", " + relationship + ", " + visitor.getCalendarAccount().getUsername());
		} else {
			this.simpleJdbcTemplate.update("update owner_adhoc_authz set relationship = ? where owner_username = ? and visitor_username = ?",
					relationship,
					owner.getCalendarAccount().getUsername(),
					visitor.getCalendarAccount().getUsername());
			LOG.debug("proposed authorization already stored for " + visitor + ", updated relationship description");
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
		internalDeleteRelationship(owner.getCalendarAccount().getUsername(), visitor.getCalendarAccount().getUsername());
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
