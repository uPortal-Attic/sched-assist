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

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.model.Reminders;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * {@link Validator} implementation for {@link PreferencesFormBackingObject}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PreferencesFormBackingObjectValidator.java 3039 2011-02-03 14:55:17Z npblair $
 */
public class PreferencesFormBackingObjectValidator implements Validator {

	private int noteboardMaxLength = 500;
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(final Class<?> clazz) {
		return PreferencesFormBackingObject.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(final Object target, final Errors errors) {
		PreferencesFormBackingObject fbo = (PreferencesFormBackingObject) target;

		// location
		if(StringUtils.isBlank(fbo.getLocation())) {
			errors.rejectValue("location", "location.required", "Location field must not be empty.");
		} else if (fbo.getLocation().length() > 100) {
			errors.rejectValue("location", "location.toolong", "Location field is too long (" + fbo.getLocation().length() + "); maximum length is 100 characters.");
		}
		
		// meeting title prefix
		if(StringUtils.isBlank(fbo.getTitlePrefix())) {
			errors.rejectValue("titlePrefix", "titlePrefix.required", "Meeting Title Prefix field must not be empty.");
		} else if (fbo.getTitlePrefix().length() > 100) {
			errors.rejectValue("titlePrefix", "titlePrefix.toolong", "Meeting Title Prefix field is too long (" + fbo.getTitlePrefix().length() + "); maximum length is 100 characters.");
		}
		
		// meeting duration
		if(StringUtils.isBlank(fbo.getMeetingLength())) {
			errors.rejectValue("meetingLength", "meetingLength.required", "Meeting Length field must not be empty.");
		} else {
			try {
				int meetingLength = Integer.parseInt(fbo.getMeetingLength());
				if(meetingLength < 15 || meetingLength > 240) {
					errors.rejectValue("meetingLength", "meetingLength.outofbounds", "Meeting Length must be greater than 15 minutes and less than 240 minutes.");
				}
			} catch (NumberFormatException e) {
				errors.rejectValue("meetingLength", "meetingLength.invalid", "Invalid value for meeting duration.");
			}
		}
		
		// default visitors per appointment
		if(fbo.getDefaultVisitorsPerAppointment() < 1) {
			errors.rejectValue("defaultVisitorsPerAppointment", "defaultVisitors.toosmall", "Default visitors per appointment must be greater than or equal to 1.");
		}
		if(fbo.getDefaultVisitorsPerAppointment() > 99) {
			errors.rejectValue("defaultVisitorsPerAppointment", "defaultVisitors.toosmall", "Maximum allowed value for default visitors per appointment is 99.");
		}
		
		// visible window
		if(fbo.getWindowHoursStart() < 1) {
			errors.rejectValue("windowHoursStart", "windowhoursstart.toosmall", "Your visible window start must be at least 1 hour.");
		}
		if(fbo.getWindowHoursStart() >= 168) {
			errors.rejectValue("windowHoursStart", "windowhoursstart.toolarge", "Your visible window start must be less than 168 hours (1 week).");
		}
		if(fbo.getWindowWeeksEnd() < 1) {
			errors.rejectValue("windowWeeksEnd", "windowweeksend.toosmall", "Your visible window end must be at least 1 week.");
		}
		if(fbo.getWindowWeeksEnd() > 26) {
			errors.rejectValue("windowWeeksEnd", "windowweeksend.toosmall", "Your visible window end cannot be greater than 26 weeks.");
		}
		
		// meeting limit
		if(fbo.isEnableMeetingLimit()) {
			if(fbo.getMeetingLimitValue() < 1) {
				errors.rejectValue("meetingLimitValue", "meetinglimitvalue.toosmall", "Meeting Limit value must be greater than or equal to 1.");
			} else if(fbo.getMeetingLimitValue() > 10) {
				errors.rejectValue("meetingLimitValue", "meetinglimitvalue.toolarge", "Meeting Limit value greater than 10 is not much of a limit; you should just disable meeting limits.");
			}
		} else {
			// if meeting limit is disabled, forcibly set meetingLimitValue to -1
			fbo.setMeetingLimitValue(-1);
		}
		
		// noteboard
		if(StringUtils.isBlank(fbo.getNoteboard())) {
			errors.rejectValue("noteboard", "noteboard.required", "Noteboard field is required.");
		} else {
			// strip Control+Ms  from noteboard
			final String noteboardNoControlM = fbo.getNoteboard().replaceAll("\\r", "");
			fbo.setNoteboard(noteboardNoControlM);
			if(null != fbo.getNoteboard() && fbo.getNoteboard().length() > this.noteboardMaxLength) {
				errors.rejectValue("noteboard", "noteboard.toolarge", "Noteboard is too long (" + fbo.getNoteboard().length() + " characters); maximum length is " + this.noteboardMaxLength + " characters.");
			}
		}
		
		// email reminders
		if(fbo.isEnableEmailReminders()) {
			if(fbo.getEmailReminderHours() < 1 || fbo.getEmailReminderHours() > 99) {
				errors.rejectValue("emailReminderHours", "emailReminderHours.outofbounds", "Email reminder hours must be between 1 and 99.");
			}
		} else {
			// forcibly overwrite emailreminder hours to default
			fbo.setEmailReminderHours(Reminders.DEFAULT.getHours());
			// forcibly set include owner to default
			fbo.setEmailReminderIncludeOwner(Reminders.DEFAULT.isIncludeOwner());
		}
	}

}
