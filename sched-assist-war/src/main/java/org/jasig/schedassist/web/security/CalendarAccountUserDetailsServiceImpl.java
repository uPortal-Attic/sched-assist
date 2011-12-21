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


package org.jasig.schedassist.web.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.impl.visitor.VisitorDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * {@link UserDetailsService} for person {@link ICalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarAccountUserDetailsServiceImpl.java 2979 2011-01-25 19:24:44Z npblair $
 */
@Service("userDetailsService")
public class CalendarAccountUserDetailsServiceImpl implements
		UserDetailsService {

	private static final String NONE_PROVIDED = "NONE_PROVIDED";
	private ICalendarAccountDao calendarAccountDao;
	private VisitorDao visitorDao;
	private OwnerDao ownerDao;
	private List<String> administrators = new ArrayList<String>();
	private String activeDisplayNameAttribute = "mail";
	protected final Log LOG = LogFactory.getLog(this.getClass());
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param visitorDao the visitorDao to set
	 */
	@Autowired
	public void setVisitorDao(VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param administrators the administrators to set
	 */
	public void setAdministratorListProperty(String propertyValue) {
		String [] admins = StringUtils.commaDelimitedListToStringArray(propertyValue);
		this.administrators = Arrays.asList(admins);
	}
	/**
	 * @param activeDisplayNameAttribute the activeDisplayNameAttribute to set
	 */
	public void setActiveDisplayNameAttribute(String activeDisplayNameAttribute) {
		this.activeDisplayNameAttribute = activeDisplayNameAttribute;
	}
	/**
	 * @return the calendarAccountDao
	 */
	public ICalendarAccountDao getCalendarAccountDao() {
		return calendarAccountDao;
	}
	/**
	 * @return the visitorDao
	 */
	public VisitorDao getVisitorDao() {
		return visitorDao;
	}
	/**
	 * @return the ownerDao
	 */
	public OwnerDao getOwnerDao() {
		return ownerDao;
	}
	/**
	 * @return the activeDisplayNameAttribute
	 */
	public String getActiveDisplayNameAttribute() {
		return activeDisplayNameAttribute;
	}
	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public final UserDetails loadUserByUsername(final String username)
			throws UsernameNotFoundException, DataAccessException {
		if(NONE_PROVIDED.equals(username)) {
			LOG.debug("caught NONE_PROVIDED being passed into loadUserByUsername");
			throw new UsernameNotFoundException(NONE_PROVIDED);
		}
		ICalendarAccount calendarAccount = getCalendarAccount(username);
		if(null == calendarAccount) {
			throw new UsernameNotFoundException("no calendar account found for " + username);
		}
		CalendarAccountUserDetailsImpl result = new CalendarAccountUserDetailsImpl(calendarAccount);
		result.setActiveDisplayNameAttribute(this.activeDisplayNameAttribute);
		checkForVisitorAndOwner(result);
		
		if(this.administrators.contains(username)) {
			result.setAdministrator(true);
		}
		return result;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	protected ICalendarAccount getCalendarAccount(final String value) {
		ICalendarAccount calendarAccount = calendarAccountDao.getCalendarAccount(value);
		return calendarAccount;
	}

	/**
	 * Mutate the {@link CalendarAccountUserDetailsImpl} argument, calling
	 * {@link CalendarAccountUserDetailsImpl#setScheduleOwner(IScheduleOwner)} and 
	 * {@link CalendarAccountUserDetailsImpl#setScheduleVisitor(IScheduleVisitor)} where appropriate.
	 * 
	 * @param accountUserDetails the instance to mutate
	 */
	protected void checkForVisitorAndOwner(CalendarAccountUserDetailsImpl accountUserDetails) {
		try {
			IScheduleVisitor scheduleVisitor = visitorDao.toVisitor(accountUserDetails.getCalendarAccount());
			accountUserDetails.setScheduleVisitor(scheduleVisitor);
		} catch (NotAVisitorException e) {
			LOG.debug(accountUserDetails.getUsername() + " is not a visitor");
		}
		
		IScheduleOwner scheduleOwner = ownerDao.locateOwner(accountUserDetails.getCalendarAccount());
		accountUserDetails.setScheduleOwner(scheduleOwner);
	}
}
