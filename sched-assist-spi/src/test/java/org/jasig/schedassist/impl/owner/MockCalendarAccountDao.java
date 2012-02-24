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
import java.util.Collection;
import java.util.List;

import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.mock.MockCalendarAccount;

/**
 * Mock {@link ICalendarAccountDao} implementation useful for tests in this package.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: MockCalendarAccountDao.java 3100 2011-02-28 18:41:40Z npblair $
 */
class MockCalendarAccountDao implements ICalendarAccountDao {

	protected static final String CUSTOM_ATTRIBUTE_NAME = "customattribute";
	private final List<MockCalendarAccount> accounts = new ArrayList<MockCalendarAccount>();
	
	/**
	 * Initialize 10 mock accounts
	 */
	MockCalendarAccountDao() {
		this(10);
	}
	/**
	 * 
	 * @param numberOfAccounts the number of mock accounts to create
	 */
	MockCalendarAccountDao(int numberOfAccounts) {
		for(int i = 0; i < numberOfAccounts; i++) {
			MockCalendarAccount user = new MockCalendarAccount();
			user.setUsername("user" + i);
			user.setCalendarUniqueId("10000:0000" + i);
			user.setEligible(true);
			user.setEmailAddress("email" + i + "@domain.com");
			user.setDisplayName("First Last" + i);
			user.setAttributeValue(CUSTOM_ATTRIBUTE_NAME, "custom"+i);
			// set the 'mail' and 'username' attributes explicitly as some dao tests depend on it
			user.setAttributeValue("mail", "email" + i + "@domain.com");
			user.setAttributeValue("uid", "user" + i);
			accounts.add(user);
		}
	}
	
	/**
	 * Initialize the dao with a pre-populated set of accounts.
	 * @param accounts
	 */
	MockCalendarAccountDao(Collection<MockCalendarAccount> accounts) {
		this.accounts.addAll(accounts);
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarAccountDao#getCalendarAccount(java.lang.String)
	 */
	@Override
	public ICalendarAccount getCalendarAccount(String username) {
		for(MockCalendarAccount a : accounts) {
			if(a.getUsername().equals(username)) {
				return a;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarAccountDao#getCalendarAccount(java.lang.String, java.lang.String)
	 */
	@Override
	public ICalendarAccount getCalendarAccount(String attributeName,
			String attributeValue) {
		for(MockCalendarAccount current : accounts) {
			final String currentValue = current.getAttributeValue(attributeName);
			if(currentValue != null && currentValue.equals(attributeValue)) {
				return current;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarAccountDao#getCalendarAccountFromUniqueId(java.lang.String)
	 */
	@Override
	public ICalendarAccount getCalendarAccountFromUniqueId(
			String calendarUniqueId) {
		for(MockCalendarAccount a : accounts) {
			if(a.getCalendarUniqueId().equals(calendarUniqueId)) {
				return a;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarAccountDao#searchForCalendarAccounts(java.lang.String)
	 */
	@Override
	public List<ICalendarAccount> searchForCalendarAccounts(String searchText) {
		List<ICalendarAccount> results = new ArrayList<ICalendarAccount>();
		for(MockCalendarAccount a : accounts) {
			if(a.getDisplayName().contains(searchText) || a.getUsername().contains(searchText)) {
				results.add(a);
			}
		}
		return results;
	}
	
	/**
	 * Alter the unique id of an existing account.
	 * 
	 * @param existingAccount
	 * @param newUniqueId
	 * @return the altered account, or null if the {@link ICalendarAccount} didn't correspond with an existing account
	 */
	public ICalendarAccount changeAccountUniqueId(ICalendarAccount existingAccount, String newUniqueId) {
		for(MockCalendarAccount a : accounts) {
			if(a.getUsername().equals(existingAccount.getUsername())) {
				// found the account, alter it
				a.setCalendarUniqueId(newUniqueId);
				return a;
			}
		}
		
		return null;
	}
	
	/**
	 * Alter the username of an existing account.
	 * 
	 * @param existingAccount
	 * @param newUsername
	 * @return the altered account, or null if the {@link ICalendarAccount} didn't correspond with an existing account
	 */
	public ICalendarAccount changeAccountUsername(ICalendarAccount existingAccount, String newUsername) {
		for(MockCalendarAccount a : accounts) {
			if(a.getCalendarUniqueId().equals(existingAccount.getCalendarUniqueId())) {
				// found the account, alter it
				a.setUsername(newUsername);
				return a;
			}
		}
		
		return null;
	}
	
	/**
	 * Add a {@link MockCalendarAccount} to this dao.
	 * @param account
	 */
	public void addCalendarAccount(MockCalendarAccount account) {
		accounts.add(account);
	}

}
