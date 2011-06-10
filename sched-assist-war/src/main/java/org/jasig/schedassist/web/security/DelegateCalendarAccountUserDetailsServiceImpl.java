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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.IDelegateCalendarAccountDao;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * {@link UserDetailsService} that returns {@link DelegateCalendarAccountUserDetailsImpl}
 * instances.
 * 
 * Requires that a valid {@link CalendarAccountUserDetailsImpl} be in the
 * current {@link SecurityContext}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateCalendarAccountUserDetailsServiceImpl.java 2045 2010-04-30 15:55:52Z npblair $
 */
public class DelegateCalendarAccountUserDetailsServiceImpl implements
		UserDetailsService {

	private static final String NONE_PROVIDED = "NONE_PROVIDED";
	private IDelegateCalendarAccountDao delegateCalendarAccountDao;
	private OwnerDao ownerDao;
	protected final Log LOG = LogFactory.getLog(this.getClass());
	
	/**
	 * @param delegateCalendarAccountDao the delegateCalendarAccountDao to set
	 */
	@Autowired
	public void setDelegateCalendarAccountDao(
			IDelegateCalendarAccountDao delegateCalendarAccountDao) {
		this.delegateCalendarAccountDao = delegateCalendarAccountDao;
	}
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public UserDetails loadUserByUsername(final String username)
			throws UsernameNotFoundException, DataAccessException {
		if(NONE_PROVIDED.equals(username)) {
			LOG.debug("caught NONE_PROVIDED being passed into loadUserByUsername");
			throw new UsernameNotFoundException(NONE_PROVIDED);
		}
		
		CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IDelegateCalendarAccount delegate = this.delegateCalendarAccountDao.getDelegate(username, currentUser.getCalendarAccount());
		if(null == delegate) {
			throw new UsernameNotFoundException("no delegate account found with name " + username);
		}
		IScheduleOwner delegateOwner = ownerDao.locateOwner(delegate);
		DelegateCalendarAccountUserDetailsImpl result = new DelegateCalendarAccountUserDetailsImpl(delegate, delegateOwner);
		return result;
	}

}
