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

package org.jasig.schedassist.model.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jasig.schedassist.model.AbstractCalendarAccount;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;

/**
 * Mock {@link IDelegateCalendarAccount} implementation.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: MockDelegateCalendarAccount.java $
 */
public class MockDelegateCalendarAccount extends AbstractCalendarAccount
		implements IDelegateCalendarAccount {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7608804462307913973L;

	private ICalendarAccount accountOwner;
	private String location;
	private String contactInformation;
	private Map<String, List<String>> attributes = new HashMap<String, List<String>>();
	private String calendarLoginId;
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IDelegateCalendarAccount#getAccountOwner()
	 */
	@Override
	public ICalendarAccount getAccountOwner() {
		return accountOwner;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IDelegateCalendarAccount#getAccountOwnerUsername()
	 */
	@Override
	public String getAccountOwnerUsername() {
		return accountOwner.getUsername();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IDelegateCalendarAccount#getLocation()
	 */
	@Override
	public String getLocation() {
		return this.location;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IDelegateCalendarAccount#getContactInformation()
	 */
	@Override
	public String getContactInformation() {
		return this.contactInformation;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.AbstractCalendarAccount#getAttributes()
	 */
	@Override
	public Map<String, List<String>> getAttributes() {
		return this.attributes;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.AbstractCalendarAccount#getCalendarLoginId()
	 */
	@Override
	public String getCalendarLoginId() {
		return this.calendarLoginId;
	}

	/**
	 * @param accountOwner the accountOwner to set
	 */
	public void setAccountOwner(ICalendarAccount accountOwner) {
		this.accountOwner = accountOwner;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @param contactInformation the contactInformation to set
	 */
	public void setContactInformation(String contactInformation) {
		this.contactInformation = contactInformation;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Map<String, List<String>> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param calendarLoginId the calendarLoginId to set
	 */
	public void setCalendarLoginId(String calendarLoginId) {
		this.calendarLoginId = calendarLoginId;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.AbstractCalendarAccount#isDelegate()
	 */
	@Override
	public boolean isDelegate() {
		return true;
	}

}
