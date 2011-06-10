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

import java.util.Map;
import org.jasig.schedassist.model.MeetingDurations;

/**
 * Abstract {@link IScheduleOwner} implementation.
 * All {@link ICalendarAccount} methods are implemented by delegating to the 
 * internal {@link ICalendarAccount} required by the sole constructor.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AbstractScheduleOwner.java 2983 2011-01-26 21:52:38Z npblair $
 */
public abstract class AbstractScheduleOwner implements IScheduleOwner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final ICalendarAccount calendarAccount;

	/**
	 * 
	 * @param calendarAccount
	 */
	public AbstractScheduleOwner(final ICalendarAccount calendarAccount) {
		this.calendarAccount = calendarAccount;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#getId()
	 */
	@Override
	public abstract long getId();

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#getPreference(org.jasig.schedassist.model.Preferences)
	 */
	@Override
	public abstract String getPreference(Preferences preference);

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#getPreferences()
	 */
	@Override
	public abstract Map<Preferences, String> getPreferences();

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#getCalendarAccount()
	 */
	@Override
	public ICalendarAccount getCalendarAccount() {
		return calendarAccount;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#getPreferredLocation()
	 */
	@Override
	public final String getPreferredLocation() {
		return getPreferences().get(Preferences.LOCATION);

	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#getRemindersPreference()
	 */
	@Override
	public final Reminders getRemindersPreference() {
		String emailPreferencesValue = getPreferences().get(Preferences.REMINDERS);
		Reminders result = Reminders.fromKey(emailPreferencesValue);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#getPreferredMeetingDurations()
	 */
	@Override
	public final MeetingDurations getPreferredMeetingDurations() {
		String meetingDurationsValue = getPreferences().get(Preferences.DURATIONS);
		MeetingDurations result = MeetingDurations.fromKey(meetingDurationsValue);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#getPreferredVisibleWindow()
	 */
	@Override
	public final VisibleWindow getPreferredVisibleWindow() {
		String visibleWindowValue = getPreferences().get(Preferences.VISIBLE_WINDOW);
		VisibleWindow result = VisibleWindow.fromKey(visibleWindowValue);
		return result;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#getPreferredMinimumDuration()
	 */
	@Override
	public final int getPreferredMinimumDuration() {
		MeetingDurations preferredDurations = getPreferredMeetingDurations();
		return preferredDurations.getMinLength();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#isSamePerson(org.jasig.schedassist.model.IScheduleVisitor)
	 */
	@Override
	public final boolean isSamePerson(IScheduleVisitor visitor) {
		if(null == visitor) {
			return false;
		}
		return this.calendarAccount.equals(visitor.getCalendarAccount());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#hasMeetingLimit()
	 */
	@Override
	public final boolean hasMeetingLimit() {
		String prefValue = getPreference(Preferences.MEETING_LIMIT);
		int limit = Integer.parseInt(prefValue);
		return limit > 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#isExceedingMeetingLimit(int)
	 */
	@Override
	public final boolean isExceedingMeetingLimit(int visibleScheduleAttendingCount) {
		String prefValue = getPreference(Preferences.MEETING_LIMIT);
		int limit = Integer.parseInt(prefValue);
		if(limit == -1) {
			return false;
		} else {
			return visibleScheduleAttendingCount >= limit;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#getPreferredDefaultVisitorLimit()
	 */
	@Override
	public final int getPreferredDefaultVisitorLimit() {
		String prefValue = getPreference(Preferences.DEFAULT_VISITOR_LIMIT);
		int limit = Integer.parseInt(prefValue);
		return limit;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleOwner#isReflectSchedule()
	 */
	@Override
	public final boolean isReflectSchedule() {
		String prefValue = getPreference(Preferences.REFLECT_SCHEDULE);
		boolean result = Boolean.parseBoolean(prefValue);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((calendarAccount == null) ? 0 : calendarAccount.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractScheduleOwner other = (AbstractScheduleOwner) obj;
		if (calendarAccount == null) {
			if (other.calendarAccount != null)
				return false;
		} else if (!calendarAccount.equals(other.calendarAccount))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractScheduleOwner [calendarAccount=");
		builder.append(calendarAccount);
		builder.append("]");
		return builder.toString();
	}



}
