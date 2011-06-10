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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.jasig.schedassist.model.Relationship;

/**
 * Configurable implementation of {@link RelationshipDao}.
 * A {@link ICalendarAccountDao} and an {@link OwnerDao} are required.
 * 
 * The next key dependency is a {@link Map} of 
 * {@link String}s that map visitorAttributeValue to a {@link List} of ownerAttributeValue.
 * By default, the names of the attributes that these correspond to are
 * "uid"->"uid". 
 * These attributeNames can be modified.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: StaticRelationshipDaoImpl.java 2007 2010-04-26 15:16:30Z npblair $
 */
public class StaticRelationshipDaoImpl implements RelationshipDao {

	private Log LOG = LogFactory.getLog(this.getClass());

	private ICalendarAccountDao calendarAccountDao;
	private OwnerDao ownerDao;
	private VisitorDao visitorDao;
	private String ownerAttributeName = "uid";
	private String visitorAttributeName = "uid";
	private final Map<String, List<String>> visitorOwnerAttributePairings;
	private final Map<String, List<String>> mapReverse;

	private String relationshipDescription;

	/**
	 * 
	 * @param visitorOwnerAttributePairings
	 */
	public StaticRelationshipDaoImpl(final Map<String, List<String>> visitorOwnerAttributePairings) {
		this.visitorOwnerAttributePairings = Collections.unmodifiableMap(visitorOwnerAttributePairings);
		mapReverse = new HashMap<String, List<String>>();
		for(String visitorUsername : this.visitorOwnerAttributePairings.keySet()) {
			List<String> ownerUsernames = this.visitorOwnerAttributePairings.get(visitorUsername);		
			for(String ownerUsername : ownerUsernames) {
				List<String> visitorList = mapReverse.get(ownerUsername);
				if(null == visitorList) {
					visitorList = new ArrayList<String>();
					visitorList.add(visitorUsername);

				} else {
					if(!visitorList.contains(visitorUsername)) {
						visitorList.add(visitorUsername);
					}
				}
				mapReverse.put(ownerUsername, visitorList);
			}
		}
	}
	/**
	 * @return the calendarAccountDao
	 */
	public ICalendarAccountDao getCalendarAccountDao() {
		return calendarAccountDao;
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param ownerDao the ownerDao to set
	 */
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param visitorDao the visitorDao to set
	 */
	public void setVisitorDao(VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}
	/**
	 * @param ownerAttributeName the ownerAttributeName to set
	 */
	public void setOwnerAttributeName(String ownerAttributeName) {
		this.ownerAttributeName = ownerAttributeName;
	}
	/**
	 * @param visitorAttributeName the visitorAttributeName to set
	 */
	public void setVisitorAttributeName(String visitorAttributeName) {
		this.visitorAttributeName = visitorAttributeName;
	}
	/**
	 * @param relationshipDescription the relationshipDescription to set
	 */
	public void setRelationshipDescription(String relationshipDescription) {
		this.relationshipDescription = relationshipDescription;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.RelationshipDao#forOwner(org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public List<Relationship> forOwner(IScheduleOwner owner) {
		ICalendarAccount ownerCalendarAccount = owner.getCalendarAccount();
		String ownerAttributeValue = ownerCalendarAccount.getAttributeValue(this.ownerAttributeName);

		List<String> assignedVisitorValues = this.mapReverse.get(ownerAttributeValue);
		if(null == assignedVisitorValues || assignedVisitorValues.size() == 0) {
			return Collections.emptyList();
		}

		List<Relationship> results = new ArrayList<Relationship>();
		for(String assignedVisitorValue : assignedVisitorValues ) {
			ICalendarAccount visitorCalendarUser = calendarAccountDao.getCalendarAccount(ownerAttributeName, assignedVisitorValue);
			if(null == visitorCalendarUser) {
				LOG.debug("no calendar account found for " + ownerAttributeName + "=" + assignedVisitorValue);
				continue;
			}
			try {
				IScheduleVisitor visitor = visitorDao.toVisitor(visitorCalendarUser);
				if(null != owner) {
					Relationship relationship = new Relationship();
					relationship.setOwner(owner);
					relationship.setVisitor(visitor);
					relationship.setDescription(relationshipDescription);

					LOG.info("found owner " + owner + " for visitor " + visitor);
					results.add(relationship);
				} 
			} catch (NotAVisitorException e) {
				LOG.debug(assignedVisitorValue + " not registered as owner");
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
		String vistorAttributeValue = visitor.getCalendarAccount().getAttributeValue(this.visitorAttributeName);

		List<String> assignedOwnerValues = this.visitorOwnerAttributePairings.get(vistorAttributeValue);
		if(null == assignedOwnerValues || assignedOwnerValues.size() == 0) {
			return Collections.emptyList();
		}

		List<Relationship> results = new ArrayList<Relationship>();
		for(String assignedOwnerValue : assignedOwnerValues ) {

			ICalendarAccount ownerCalendarUser = calendarAccountDao.getCalendarAccount(ownerAttributeName, assignedOwnerValue);
			if(null == ownerCalendarUser) {
				LOG.debug("no calendar account found for " + ownerAttributeName + "=" + assignedOwnerValue);
				continue;
			}

			IScheduleOwner owner = ownerDao.locateOwner(ownerCalendarUser);
			if(null != owner) {
				Relationship relationship = new Relationship();
				relationship.setOwner(owner);
				relationship.setVisitor(visitor);
				relationship.setDescription(relationshipDescription);

				LOG.info("found owner " + owner + " for visitor " + visitor);
				results.add(relationship);
			} else {
				LOG.debug(assignedOwnerValue + " not registered as owner");
			}
		}

		return results;
	}

}
