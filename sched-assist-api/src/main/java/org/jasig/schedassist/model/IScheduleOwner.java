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

import java.io.Serializable;
import java.util.Map;

/**
 * Interface that represents
 * a registered "Schedule Owner" within the Scheduling Assistant.
 * 
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: IScheduleOwner.java 2983 2011-01-26 21:52:38Z npblair $
 */
public interface IScheduleOwner extends Serializable {

	/**
	 * 
	 * @return the {@link ICalendarAccount} that registered this account
	 */
	ICalendarAccount getCalendarAccount();
	
	/**
	 * Internal identifier within the Scheduling Assistant for an {@link IScheduleOwner}.
	 * @return the owner's id value
	 */
	long getId();
	
	/**
	 * 
	 * @return a map of the owner's {@link String} values for their chosen {@link Preferences}
	 */
	Map<Preferences, String> getPreferences();
	
	/**
	 * 
	 * @param preference
	 * @return the owner's {@link String} value for the specified {@link Preferences}
	 */
	String getPreference(Preferences preference);
	
	/**
	 * Short cut method to return the owner's value for {@link Preferences#LOCATION}.
	 * @return the owner's preferred location preference
	 */
	String getPreferredLocation();
	
	/**
	 * Short cut method to return the owner's value for {@link Preferences#DURATIONS}.
	 * @return the owner's preferred {@link MeetingDurations}
	 */
	MeetingDurations getPreferredMeetingDurations();
	
	/**
	 * Short cut method to return the owner's value for {@link Preferences#REMINDERS}.
	 * @return the owner's preferred {@link Reminders}
	 */
	Reminders getRemindersPreference();
	
	/**
	 * Short cut method to return the owner's min value for {@link Preferences#DURATIONS} as an integer.
	 * @return the owner's min value for {@link Preferences#DURATIONS} as an integer.
	 */
	int getPreferredMinimumDuration();
	/**
	 * Short cut method to return the owner's value for {@link Preferences#VISIBLE_WINDOW}.
	 * @return the owner's preferred {@link VisibleWindow}
	 */
	VisibleWindow getPreferredVisibleWindow();
	
	/**
	 * 
	 * @param visitor
	 * @return true if this instance and the {@link IScheduleVisitor} represent the same {@link ICalendarAccount}
	 */
	boolean isSamePerson(IScheduleVisitor visitor);
	
	/**
	 * Short cut method to determine whether this owner restricts the number of appointments
	 * that a visitor can create within a visible window.
	 * 
	 * @see Preferences#MEETING_LIMIT
	 * @return true if the owner's value for {@link Preferences#MEETING_LIMIT} is not equal to -1
	 */
	boolean hasMeetingLimit();
	
	/**
	 * Short cut method to check if an attendee count for an event exceeds the owner's preferences.
	 * 
	 * @param visibleScheduleAttendingCount the number of available attendees for an event
	 * @return true if the arguments equals or exceeds this owner's value for {@link Preferences#MEETING_LIMIT}
	 */
	boolean isExceedingMeetingLimit(int visibleScheduleAttendingCount);
	
	/**
	 * Short cut method backed by {@link Preferences#REFLECT_SCHEDULE}.
	 * 
	 * @return true if the owner has enabled the schedule reflection preference.
	 */
	boolean isReflectSchedule();
	
	/**
	 * 
	 * @return this owner's preference for {@link Preferences#DEFAULT_VISITOR_LIMIT}.
	 */
	int getPreferredDefaultVisitorLimit();
}
