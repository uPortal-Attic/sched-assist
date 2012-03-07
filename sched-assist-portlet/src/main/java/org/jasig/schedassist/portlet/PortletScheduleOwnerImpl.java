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

package org.jasig.schedassist.portlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jasig.schedassist.messaging.PreferencesElement;
import org.jasig.schedassist.messaging.ScheduleOwnerElement;
import org.jasig.schedassist.model.AbstractScheduleOwner;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.Preferences;

/**
 * Subclass of {@link AbstractScheduleOwner} used within the portlet.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PortletScheduleOwnerImpl.java $
 */
class PortletScheduleOwnerImpl extends AbstractScheduleOwner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	private final long id;
	private final Map<Preferences, String> preferences;
	
	/**
	 * 
	 * @param element
	 */
	PortletScheduleOwnerImpl(ScheduleOwnerElement element) {
		super(new CalendarAccountImpl(element));
		this.id = element.getId();
		
		Map<Preferences, String> prefs = new HashMap<Preferences, String>();
		for(PreferencesElement prefElement : element.getPreferencesSet().getPreferencesElement()) {
			Preferences pref = Preferences.fromKey(prefElement.getKey());
			if(pref != null) {
				prefs.put(pref, prefElement.getValue());
			}
		}
		
		this.preferences = prefs;
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
		return new HashMap<Preferences, String>(this.preferences);
	}

	/**
	 * Inner implementation of {@link ICalendarAccount} that only
	 * provides the displayName and username fields (all that's available
	 * in portlet environment).
	 *
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: PortletScheduleOwnerImpl.java $
	 */
	static class CalendarAccountImpl implements ICalendarAccount {

		/**
		 * 
		 */
		private static final long serialVersionUID = 53706L;
		
		private String displayName;
		private String username;
		
		/**
		 * @param displayName
		 * @param username
		 */
		public CalendarAccountImpl(ScheduleOwnerElement element) {
			this.displayName = element.getFullName();
			this.username = element.getNetid();
		}

		/**
		 * @param displayName the displayName to set
		 */
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}

		@Override
		public String getAttributeValue(String attributeName) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Map<String, List<String>> getAttributes() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getCalendarLoginId() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getCalendarUniqueId() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getDisplayName() {
			return this.displayName;
		}

		@Override
		public String getEmailAddress() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getUsername() {
			return this.username;
		}

		@Override
		public boolean isEligible() {
			return true;
		}

		@Override
		public List<String> getAttributeValues(String attributeName) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.jasig.schedassist.model.ICalendarAccount#isDelegate()
		 */
		@Override
		public boolean isDelegate() {
			throw new UnsupportedOperationException();
		}
		
	}
}
