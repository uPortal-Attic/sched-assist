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

import java.util.List;

import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.IDelegateCalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * {@link ICalendarAccountDao} that wraps another {@link ICalendarAccountDao} AND an
 * {@link IDelegateCalendarAccountDao}.
 * The wrapped {@link ICalendarAccountDao} is consulted first. if it returns null, 
 * the {@link IDelegateCalendarAccountDao} is called.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CompositeCalendarAccountDaoImpl.java 2032 2010-04-30 12:57:36Z npblair $
 */
@Service
@Qualifier("composite")
public final class CompositeCalendarAccountDaoImpl implements ICalendarAccountDao {

	private ICalendarAccountDao calendarAccountDao;
	private IDelegateCalendarAccountDao delegateCalendarAccountDao;
	
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param delegateCalendarAccountDao the delegateCalendarAccountDao to set
	 */
	@Autowired
	public void setDelegateCalendarAccountDao(
			IDelegateCalendarAccountDao delegateCalendarAccountDao) {
		this.delegateCalendarAccountDao = delegateCalendarAccountDao;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarAccountDao#getCalendarAccount(java.lang.String)
	 */
	@Override
	public ICalendarAccount getCalendarAccount(final String username) {
		ICalendarAccount person = this.calendarAccountDao.getCalendarAccount(username);
		if(null == person) {
			IDelegateCalendarAccount delegate = this.delegateCalendarAccountDao.getDelegate(username);
			if(null != delegate) {
				
				return delegate;
			}
		}
		return person;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarAccountDao#getCalendarAccount(java.lang.String, java.lang.String)
	 */
	@Override
	public ICalendarAccount getCalendarAccount(final String attributeName,
			final String attributeValue) {
		ICalendarAccount person = this.calendarAccountDao.getCalendarAccount(attributeName, attributeValue);
		return person;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarAccountDao#getCalendarAccountFromUniqueId(java.lang.String)
	 */
	@Override
	public ICalendarAccount getCalendarAccountFromUniqueId(
			final String calendarUniqueId) {
		ICalendarAccount person = this.calendarAccountDao.getCalendarAccountFromUniqueId(calendarUniqueId);
		if(null == person) {
			IDelegateCalendarAccount delegate = this.delegateCalendarAccountDao.getDelegateByUniqueId(calendarUniqueId);
			if(null != delegate) {
				return delegate;
			}
		}
		return person;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarAccountDao#searchForCalendarAccounts(java.lang.String)
	 */
	@Override
	public List<ICalendarAccount> searchForCalendarAccounts(final String searchText) {
		List<ICalendarAccount> people = this.calendarAccountDao.searchForCalendarAccounts(searchText);
		return people;
	}

}
