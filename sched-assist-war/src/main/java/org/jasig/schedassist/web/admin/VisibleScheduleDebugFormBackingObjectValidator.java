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


package org.jasig.schedassist.web.admin;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.IDelegateCalendarAccountDao;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.impl.visitor.VisitorDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * {@link Validator} for {@link VisibleScheduleDebugFormBackingObject}s.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleScheduleDebugFormBackingObjectValidator.java 2978 2011-01-25 19:20:51Z npblair $
 */
@Component
public class VisibleScheduleDebugFormBackingObjectValidator implements
		Validator {

	private Log LOG = LogFactory.getLog(this.getClass());
	private ICalendarAccountDao calendarAccountDao;
	private IDelegateCalendarAccountDao delegateCalendarAccountDao;
	private OwnerDao ownerDao;
	private VisitorDao visitorDao;
	
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(@Qualifier("people") ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param delegateCalendarAccountDao the delegateCalendarAccountDao to set
	 */
	@Autowired
	public void setDelegateCalendarAccountDao(
			@Qualifier("delegates") IDelegateCalendarAccountDao delegateCalendarAccountDao) {
		this.delegateCalendarAccountDao = delegateCalendarAccountDao;
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

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	//@Override
	public boolean supports(Class<?> clazz) {
		return VisibleScheduleDebugFormBackingObject.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	//@Override
	public void validate(Object target, Errors errors) {
		VisibleScheduleDebugFormBackingObject command = (VisibleScheduleDebugFormBackingObject) target;

		IScheduleVisitor visitor = locateVisitor(command.getVisitorLookup());
		if(visitor == null) {
			errors.rejectValue("visitorLookup", "visitor.notfound", "Schedule Visitor not found");
		} else {
			command.setScheduleVisitor(visitor);
		}
		
		IScheduleOwner owner = locateOwner(command.getOwnerLookup());
		if(owner == null) {
			errors.rejectValue("ownerLookup", "owner.notfound", "Schedule Owner not found");
		} else {
			command.setScheduleOwner(owner);
		}
	}

	/**
	 * 
	 * @param lookupObject
	 * @return the corresponding {@link IScheduleVisitor}, or null if not found
	 */
	protected IScheduleVisitor locateVisitor(AccountLookupFormBackingObject lookupObject) {
		ICalendarAccount account = null;
		if(StringUtils.isNotBlank(lookupObject.getUsername())) {
			account = this.calendarAccountDao.getCalendarAccount(lookupObject.getUsername());
			
		} else if(StringUtils.isNotBlank(lookupObject.getCtcalxitemid())) {
			account = this.calendarAccountDao.getCalendarAccountFromUniqueId(lookupObject.getCtcalxitemid());
			
		} 
		
		if(null != account) {
			try {
				return this.visitorDao.toVisitor(account);
			} catch (NotAVisitorException e) {
				LOG.debug(account + " not a visitor", e);
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param lookupObject
	 * @return the corresponding {@link IScheduleOwner}, or null if not found
	 */
	protected IScheduleOwner locateOwner(AccountLookupFormBackingObject lookupObject) {
		ICalendarAccount account = null;
		if(StringUtils.isNotBlank(lookupObject.getUsername())) {
			account = this.calendarAccountDao.getCalendarAccount(lookupObject.getUsername());
			
		} else if(StringUtils.isNotBlank(lookupObject.getCtcalxitemid())) {
			account = this.calendarAccountDao.getCalendarAccountFromUniqueId(lookupObject.getCtcalxitemid());
		} else if (StringUtils.isNotBlank(lookupObject.getResourceName())) {
			account = this.delegateCalendarAccountDao.getDelegate(lookupObject.getResourceName());
		}
		
		if(null != account) {
			IScheduleOwner owner = this.ownerDao.locateOwner(account);
			return owner;
		}
		
		return null;
	}
}
