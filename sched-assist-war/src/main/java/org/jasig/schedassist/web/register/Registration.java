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


package org.jasig.schedassist.web.register;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.MeetingDurations;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.VisibleWindow;
import org.jasig.schedassist.web.owner.preferences.PreferencesFormBackingObject;
import org.jasig.schedassist.web.owner.preferences.PreferencesFormBackingObjectValidator;
import org.jasig.schedassist.web.owner.schedule.BlockBuilderFormBackingObject;
import org.jasig.schedassist.web.owner.schedule.BlockBuilderFormBackingObjectValidator;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

/**
 * Model object for {@link ICalendarAccount} registration webflow.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: Registration.java 2695 2010-09-24 13:20:05Z npblair $
 */
public class Registration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;
	private static final String COMMA = ",";
	
	private String location = Preferences.LOCATION.getDefaultValue();
	private String titlePrefix = Preferences.MEETING_PREFIX.getDefaultValue();
	private String meetingLength = Integer.toString(MeetingDurations.THIRTY.getMinLength());
	private boolean allowDoubleLength = false;
	private int windowHoursStart = VisibleWindow.DEFAULT.getWindowHoursStart();
	private int windowWeeksEnd = VisibleWindow.DEFAULT.getWindowWeeksEnd();
	private String noteboard = null;
	private int defaultVisitorsPerAppointment = 1;
	private boolean enableMeetingLimit = false;
	private int meetingLimitValue = -1;
	private boolean reflectSchedule = false;
	private boolean enableEmailReminders = false;
	private boolean emailReminderIncludeOwner = false;
	private int emailReminderHours = 24;
	
	private String startTimePhrase;
	private String endTimePhrase;
	private String daysOfWeekPhrase;
	private String startDatePhrase;
	private String endDatePhrase;
	
	private boolean scheduleSet = false;
	
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
	 * @return the noteboard
	 */
	public String getNoteboard() {
		return noteboard;
	}
	/**
	 * Get the noteboard as an array of sentences (noteboard split on newline characters).
	 *
	 * @return a possibly empty, but never null noteboard as an array of sentences
	 */
	public String [] getNoteboardSentences() {
		if(StringUtils.isBlank(noteboard)) {
			return new String[]{};
		} else {
			String [] noteboardSentences = noteboard.split("\n");
			return noteboardSentences;
		}
	}
	/**
	 * @param noteboard the noteboard to set
	 */
	public void setNoteboard(String noteboard) {
		this.noteboard = noteboard;
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
	 * @return the startTimePhrase
	 */
	public String getStartTimePhrase() {
		return startTimePhrase;
	}
	/**
	 * @param startTimePhrase the startTimePhrase to set
	 */
	public void setStartTimePhrase(String startTimePhrase) {
		this.startTimePhrase = startTimePhrase;
	}
	/**
	 * @return the endTimePhrase
	 */
	public String getEndTimePhrase() {
		return endTimePhrase;
	}
	/**
	 * @param endTimePhrase the endTimePhrase to set
	 */
	public void setEndTimePhrase(String endTimePhrase) {
		this.endTimePhrase = endTimePhrase;
	}
	/**
	 * @return the daysOfWeekPhrase
	 */
	public String getDaysOfWeekPhrase() {
		return daysOfWeekPhrase;
	}
	/**
	 * @param daysOfWeekPhrase the daysOfWeekPhrase to set
	 */
	public void setDaysOfWeekPhrase(String daysOfWeekPhrase) {
		this.daysOfWeekPhrase = daysOfWeekPhrase;
	}
	/**
	 * @return the startDatePhrase
	 */
	public String getStartDatePhrase() {
		return startDatePhrase;
	}
	/**
	 * @param startDatePhrase the startDatePhrase to set
	 */
	public void setStartDatePhrase(String startDatePhrase) {
		this.startDatePhrase = startDatePhrase;
	}
	/**
	 * @return the endDatePhrase
	 */
	public String getEndDatePhrase() {
		return endDatePhrase;
	}
	/**
	 * @param endDatePhrase the endDatePhrase to set
	 */
	public void setEndDatePhrase(String endDatePhrase) {
		this.endDatePhrase = endDatePhrase;
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
	 * @return the scheduleSet
	 */
	public boolean isScheduleSet() {
		return scheduleSet;
	}
	/**
	 * 
	 * @return
	 */
	public String durationPreferenceValue() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.meetingLength);
		if(this.allowDoubleLength) {
			int duration = Integer.parseInt(this.meetingLength);
			builder.append(",");
			builder.append(2*duration);
		}
		return builder.toString();
	}

	/**
	 * 
	 * @return
	 */
	public String visibleWindowPreferenceKey() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.windowHoursStart);
		builder.append(",");
		builder.append(this.windowWeeksEnd);
		return builder.toString();
	}
	
	/**
	 * Construct a {@link BlockBuilderFormBackingObject} from the fields
	 * in this bean.
	 * 
	 * @return
	 */
	protected BlockBuilderFormBackingObject toBlockBuilderFormBackingObject() {
		BlockBuilderFormBackingObject command = new BlockBuilderFormBackingObject();
		command.setDaysOfWeekPhrase(daysOfWeekPhrase);
		command.setEndDatePhrase(endDatePhrase);
		command.setEndTimePhrase(endTimePhrase);
		command.setStartDatePhrase(startDatePhrase);
		command.setStartTimePhrase(startTimePhrase);
		command.setVisitorsPerAppointment(defaultVisitorsPerAppointment);
		return command;
	}
	
	/**
	 * Construct a {@link PreferencesFormBackingObject} from the fields
	 * in this bean.
	 * 
	 * @return
	 */
	protected PreferencesFormBackingObject toPreferencesFormBackingObject() {
		PreferencesFormBackingObject command = new PreferencesFormBackingObject();
		command.setAllowDoubleLength(allowDoubleLength);
		command.setDefaultVisitorsPerAppointment(defaultVisitorsPerAppointment);
		command.setEnableMeetingLimit(enableMeetingLimit);
		command.setLocation(location);
		command.setMeetingLength(meetingLength);
		command.setMeetingLimitValue(meetingLimitValue);
		command.setNoteboard(noteboard);
		command.setReflectSchedule(reflectSchedule);
		command.setTitlePrefix(titlePrefix);
		command.setWindowHoursStart(windowHoursStart);
		command.setWindowWeeksEnd(windowWeeksEnd);
		return command;
	}
	
	/**
	 * Validate after the preferences related fields have been set.
	 * 
	 * Delegates to a {@link PreferencesFormBackingObjectValidator}.
	 * @param context
	 */
	public void validateSetPreferences(final ValidationContext context) {
		MessageContext messages = context.getMessageContext();
		
		PreferencesFormBackingObject command = this.toPreferencesFormBackingObject();
		PreferencesFormBackingObjectValidator validator = new PreferencesFormBackingObjectValidator();
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(command, "registration");
		validator.validate(command, errors);
		
		if(errors.hasErrors()) {
			for(FieldError error: errors.getFieldErrors()){
				messages.addMessage(new MessageBuilder().error().source(error.getField())
						.defaultText(error.getDefaultMessage()).build());	
			}
		} 
	}

	/**
	 * Validate schedule related fields.
	 * 
	 * Delegates to a {@link BlockBuilderFormBackingObject}.
	 * @param context
	 */
	public void validateSetSchedule(final ValidationContext context) {
		MessageContext messages = context.getMessageContext();
		
		BlockBuilderFormBackingObject command = this.toBlockBuilderFormBackingObject();
		BlockBuilderFormBackingObjectValidator validator = new BlockBuilderFormBackingObjectValidator();
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(command, "registration");
		validator.validate(command, errors);
		
		if(errors.hasErrors()) {
			for(FieldError error: errors.getFieldErrors()){
				messages.addMessage(new MessageBuilder().error().source(error.getField())
						.defaultText(error.getDefaultMessage()).build());	
			}
		} else {
			this.scheduleSet = true;
		}
	}
}
