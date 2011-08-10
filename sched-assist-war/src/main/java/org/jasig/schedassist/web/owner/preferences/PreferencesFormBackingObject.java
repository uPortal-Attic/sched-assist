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


package org.jasig.schedassist.web.owner.preferences;

import org.jasig.schedassist.model.VisibleWindow;

/**
 * Form backing Object for {@link PreferencesFormController}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PreferencesFormBackingObject.java 2985 2011-01-26 21:58:45Z npblair $
 */
public class PreferencesFormBackingObject {

	private static final String COMMA = ",";
	private String location;
	private String titlePrefix;
	private String meetingLength;
	private String noteboard;
	private boolean allowDoubleLength = false;
	private int windowHoursStart = 24;
	private int windowWeeksEnd = 3;
	private int defaultVisitorsPerAppointment = 1;
	private boolean enableMeetingLimit = false;
	private int meetingLimitValue = -1;
	private boolean reflectSchedule = false;
	private boolean enableEmailReminders = false;
	private boolean emailReminderIncludeOwner = false;
	private int emailReminderHours = 24;
	
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the titlePrefix
	 */
	public String getTitlePrefix() {
		return titlePrefix;
	}
	/**
	 * @param titlePrefix the titlePrefix to set
	 */
	public void setTitlePrefix(String titlePrefix) {
		this.titlePrefix = titlePrefix;
	}
	/**
	 * @return the meetingLength
	 */
	public String getMeetingLength() {
		return meetingLength;
	}
	/**
	 * @param meetingLength the meetingLength to set
	 */
	public void setMeetingLength(String meetingLength) {
		this.meetingLength = meetingLength;
	}
	/**
	 * @return the noteboard
	 */
	public String getNoteboard() {
		return noteboard;
	}
	/**
	 * @param noteboard the noteboard to set
	 */
	public void setNoteboard(String noteboard) {
		this.noteboard = noteboard;
	}
	/**
	 * @return the allowDoubleLength
	 */
	public boolean isAllowDoubleLength() {
		return allowDoubleLength;
	}
	/**
	 * @param allowDoubleLength the allowDoubleLength to set
	 */
	public void setAllowDoubleLength(boolean allowDoubleLength) {
		this.allowDoubleLength = allowDoubleLength;
	}
	/**
	 * @return the windowHoursStart
	 */
	public int getWindowHoursStart() {
		return windowHoursStart;
	}
	/**
	 * @param windowHoursStart the windowHoursStart to set
	 */
	public void setWindowHoursStart(int windowHoursStart) {
		this.windowHoursStart = windowHoursStart;
	}
	/**
	 * @return the windowWeeksEnd
	 */
	public int getWindowWeeksEnd() {
		return windowWeeksEnd;
	}
	/**
	 * @param windowWeeksEnd the windowWeeksEnd to set
	 */
	public void setWindowWeeksEnd(int windowWeeksEnd) {
		this.windowWeeksEnd = windowWeeksEnd;
	}
	/**
	 * @return the defaultVisitorsPerAppointment
	 */
	public int getDefaultVisitorsPerAppointment() {
		return defaultVisitorsPerAppointment;
	}
	/**
	 * @param defaultVisitorsPerAppointment the defaultVisitorsPerAppointment to set
	 */
	public void setDefaultVisitorsPerAppointment(int defaultVisitorsPerAppointment) {
		this.defaultVisitorsPerAppointment = defaultVisitorsPerAppointment;
	}
	/**
	 * @return the enableMeetingLimit
	 */
	public boolean isEnableMeetingLimit() {
		return enableMeetingLimit;
	}
	/**
	 * @param enableMeetingLimit the enableMeetingLimit to set
	 */
	public void setEnableMeetingLimit(boolean enableMeetingLimit) {
		this.enableMeetingLimit = enableMeetingLimit;
	}
	/**
	 * @return the meetingLimitValue
	 */
	public int getMeetingLimitValue() {
		return meetingLimitValue;
	}
	/**
	 * @param meetingLimitValue the meetingLimitValue to set
	 */
	public void setMeetingLimitValue(int meetingLimitValue) {
		this.meetingLimitValue = meetingLimitValue;
		if(meetingLimitValue == -1) {
			setEnableMeetingLimit(false);
		} else {
			setEnableMeetingLimit(true);
		}
	}
	/**
	 * @return the reflectSchedule
	 */
	public boolean isReflectSchedule() {
		return reflectSchedule;
	}
	/**
	 * @param reflectSchedule the reflectSchedule to set
	 */
	public void setReflectSchedule(boolean reflectSchedule) {
		this.reflectSchedule = reflectSchedule;
	}
	/**
	 * Return the corresponding Preference value for this bean, based
	 * from the values of the durations and allowDoubleLength fields.
	 * 
	 * If allowDoubleLength is false, this returns simply the value in the durations field.
	 * If allowDoubleLength is true, this returns:
	 <pre>
	 durations,(2*durations)
	 </pre>
	 * Examples:
	 <pre>
	 30,60
	 31,62
	 45,90
	 </pre>
	 *
	 * @return
	 */
	public String durationPreferenceValue() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.meetingLength);
		if(this.allowDoubleLength) {
			int duration = Integer.parseInt(this.meetingLength);
			builder.append(COMMA);
			builder.append(2*duration);
		}
		return builder.toString();
	}
	
	/**
	 * Return a properly formatted key for the values of windowHoursStart
	 * and windowWeeksEnd that can be used to generate a {@link VisibleWindow}.
	 * 
	 * @return
	 */
	public String visibleWindowPreferenceKey() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.windowHoursStart);
		builder.append(COMMA);
		builder.append(this.windowWeeksEnd);
		return builder.toString();
	}
	
	/**
	 * Return a properly formatted preference key for the values of
	 * enableEmailReminders, emailReminderIncludeOwner, and emailReminderHours.
	 * 
	 * @return
	 */
	public String emailReminderPreferenceKey() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.enableEmailReminders);
		builder.append(COMMA);
		builder.append(this.emailReminderIncludeOwner);
		builder.append(COMMA);
		builder.append(this.emailReminderHours);
		return builder.toString();
	}
	/**
	 * @return the enableEmailReminders
	 */
	public boolean isEnableEmailReminders() {
		return enableEmailReminders;
	}
	/**
	 * @param enableEmailReminders the enableEmailReminders to set
	 */
	public void setEnableEmailReminders(boolean enableEmailReminders) {
		this.enableEmailReminders = enableEmailReminders;
	}
	/**
	 * @return the emailReminderIncludeOwner
	 */
	public boolean isEmailReminderIncludeOwner() {
		return emailReminderIncludeOwner;
	}
	/**
	 * @param emailReminderIncludeOwner the emailReminderIncludeOwner to set
	 */
	public void setEmailReminderIncludeOwner(boolean emailReminderIncludeOwner) {
		this.emailReminderIncludeOwner = emailReminderIncludeOwner;
	}
	/**
	 * @return the emailReminderHours
	 */
	public int getEmailReminderHours() {
		return emailReminderHours;
	}
	/**
	 * @param emailReminderHours the emailReminderHours to set
	 */
	public void setEmailReminderHours(int emailReminderHours) {
		this.emailReminderHours = emailReminderHours;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PreferencesFormBackingObject [location=");
		builder.append(location);
		builder.append(", titlePrefix=");
		builder.append(titlePrefix);
		builder.append(", meetingLength=");
		builder.append(meetingLength);
		builder.append(", noteboard=");
		builder.append(noteboard);
		builder.append(", allowDoubleLength=");
		builder.append(allowDoubleLength);
		builder.append(", windowHoursStart=");
		builder.append(windowHoursStart);
		builder.append(", windowWeeksEnd=");
		builder.append(windowWeeksEnd);
		builder.append(", defaultVisitorsPerAppointment=");
		builder.append(defaultVisitorsPerAppointment);
		builder.append(", enableMeetingLimit=");
		builder.append(enableMeetingLimit);
		builder.append(", meetingLimitValue=");
		builder.append(meetingLimitValue);
		builder.append(", reflectSchedule=");
		builder.append(reflectSchedule);
		builder.append(", enableEmailReminders=");
		builder.append(enableEmailReminders);
		builder.append(", emailReminderIncludeOwner=");
		builder.append(emailReminderIncludeOwner);
		builder.append(", emailReminderHours=");
		builder.append(emailReminderHours);
		builder.append("]");
		return builder.toString();
	}
	
	
}
