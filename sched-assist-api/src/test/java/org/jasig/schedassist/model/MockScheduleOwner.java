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

package org.jasig.schedassist.model;

import java.util.HashMap;
import java.util.Map;

import org.jasig.schedassist.model.AbstractScheduleOwner;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.Preferences;

/**
 * Mock {@link AbstractScheduleOwner} implementation.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: MockScheduleOwner.java 1899 2010-04-14 21:08:06Z npblair $
 */
class MockScheduleOwner extends AbstractScheduleOwner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;
	
	private final Map<Preferences, String> preferences;
	private final long id;
	
	/**
	 * 
	 * @param calendarAccount
	 * @param id
	 */
	public MockScheduleOwner(ICalendarAccount calendarAccount, long id) {
		super(calendarAccount);
		this.id = id;
		this.preferences = Preferences.getDefaultPreferences();
	}
	/**
	 * 
	 * @param calendarAccount
	 * @param preferences
	 * @param id
	 */
	public MockScheduleOwner(ICalendarAccount calendarAccount, long id, Map<Preferences, String> preferences) {
		super(calendarAccount);
		this.id = id;
		this.preferences = preferences;
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.AbstractScheduleOwner#getId()
	 */
	@Override
	public long getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.AbstractScheduleOwner#getPreference(org.jasig.schedassist.model.Preferences)
	 */
	@Override
	public String getPreference(Preferences preference) {
		return this.preferences.get(preference);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.AbstractScheduleOwner#getPreferences()
	 */
	@Override
	public Map<Preferences, String> getPreferences() {
		return new HashMap<Preferences, String>(preferences);
	}

	/**
	 * 
	 * @param preferenceKey
	 * @param preferenceValue
	 */
	public void setPreference(final Preferences preference, final String preferenceValue) {
		this.preferences.put(preference, preferenceValue);
	}
}
