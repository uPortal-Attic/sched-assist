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

import java.util.HashMap;
import java.util.Map;

import org.jasig.schedassist.model.AbstractScheduleOwner;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.Preferences;

/**
 * Default implementation of {@link AbstractScheduleOwner}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DefaultScheduleOwnerImpl.java 2996 2011-01-27 17:38:54Z npblair $
 */
public class DefaultScheduleOwnerImpl extends AbstractScheduleOwner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	private final long id;
	private Map<Preferences, String> preferences;
	
	/**
	 * 
	 * @param calendarAccount
	 * @param id
	 */
	public DefaultScheduleOwnerImpl(ICalendarAccount calendarAccount, long id) {
		super(calendarAccount);
		this.id = id;
		preferences = Preferences.getDefaultPreferences();
	}
	/**
	 * @param calendarAccount
	 * @param id
	 * @param preferences
	 */
	public DefaultScheduleOwnerImpl(ICalendarAccount calendarAccount, long id,
			Map<Preferences, String> preferences) {
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
		return preferences.get(preference);
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
	 * @param preferences the preferences to set
	 */
	public void setPreferences(Map<Preferences, String> preferences) {
		this.preferences = preferences;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((preferences == null) ? 0 : preferences.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultScheduleOwnerImpl other = (DefaultScheduleOwnerImpl) obj;
		if (id != other.id)
			return false;
		if (preferences == null) {
			if (other.preferences != null)
				return false;
		} else if (!preferences.equals(other.preferences))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DefaultScheduleOwnerImpl [id=");
		builder.append(id);
		builder.append(", preferences=");
		builder.append(preferences);
		builder.append(", calendarAccount=");
		builder.append(getCalendarAccount());
		builder.append("]");
		return builder.toString();
	}

}
