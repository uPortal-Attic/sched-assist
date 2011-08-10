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


package org.jasig.schedassist.web.owner.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.InputFormatException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * {@link Validator} implementation for {@link BlockBuilderFormBackingObject}s.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: BlockBuilderFormBackingObjectValidator.java 2713 2010-09-24 20:13:27Z npblair $
 */
public class BlockBuilderFormBackingObjectValidator implements Validator {

	private static final long MILLISECS_PER_MIN = 60*1000L;
	
	protected static final String DAYS_OF_WEEK_REGEX = "[nmtwrfs]+";
	protected static final String DATE_REGEX = "(\\d{1,2})/\\d{1,2}/\\d{4}";
	protected static final String TIME_REGEX = "(\\d{1,2}):([0-5]\\d{1}) ([AP]M)";
	
	protected static final Pattern DAYS_OF_WEEK_PATTERN = Pattern.compile(DAYS_OF_WEEK_REGEX, Pattern.CASE_INSENSITIVE);
	protected static final Pattern DATE_PATTERN = Pattern.compile(DATE_REGEX, Pattern.CASE_INSENSITIVE);
	protected static final Pattern TIME_PATTERN = Pattern.compile(TIME_REGEX, Pattern.CASE_INSENSITIVE);
	
	private static final String AM = "AM";
	private static final String PM = "PM";
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class<?> clazz) {
		return BlockBuilderFormBackingObject.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object command, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startTimePhrase", "field.required", "Start time field is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endTimePhrase", "field.required", "End time field is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "daysOfWeekPhrase", "field.required", "Days of week field is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDatePhrase", "field.required", "Start date field is required.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endDatePhrase", "field.required", "End date field is required.");

		BlockBuilderFormBackingObject fbo = (BlockBuilderFormBackingObject) command;

		if(!StringUtils.isBlank(fbo.getDaysOfWeekPhrase())) {
			Matcher daysMatcher = DAYS_OF_WEEK_PATTERN.matcher(fbo.getDaysOfWeekPhrase());
			if(!daysMatcher.matches()) {
				errors.rejectValue("daysOfWeekPhrase", "invalid.daysOfWeekPhrase", "Days of week must contain only 'NMTWRFS' (N is Sunday, S is Saturday).");
			}
		}
		
		if(!StringUtils.isBlank(fbo.getStartDatePhrase())) {
			Matcher m = DATE_PATTERN.matcher(fbo.getStartDatePhrase());
			if(!m.matches()) {
				errors.rejectValue("startDatePhrase", "startDatePhrase.invalidFormat", "Start Date must contain 2 digit month, 2 digit day, and 4 digit year (mm/dd/yyyy).");
			} 
		}
		if(!StringUtils.isBlank(fbo.getEndDatePhrase())) {
			Matcher m = DATE_PATTERN.matcher(fbo.getEndDatePhrase());
			if(!m.matches()) {
				errors.rejectValue("endDatePhrase", "endDatePhrase.invalidFormat", "End Date must contain 2 digit month, 2 digit day, and 4 digit year (mm/dd/yyyy).");
			} 
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date startDate = null;
		Date endDate = null;
		if(!StringUtils.isBlank(fbo.getStartDatePhrase())) {
			try {
				startDate = dateFormat.parse(fbo.getStartDatePhrase());
			} catch (ParseException e) {
				errors.rejectValue("startDatePhrase", "field.parseexception", "Start date does not match expected format (mm/dd/yyyy).");
			}
		}
		if(!StringUtils.isBlank(fbo.getEndDatePhrase())) {
			try {
				endDate = dateFormat.parse(fbo.getEndDatePhrase());
			} catch (ParseException e) {
				errors.rejectValue("endDatePhrase", "field.parseexception", "End date does not match expected format (mm/dd/yyyy).");
			}
		}

		if(null != startDate && null != endDate) {
			if(CommonDateOperations.approximateDifference(startDate, endDate) > 730) {
				errors.rejectValue("endDatePhrase", "endDatePhrase.toofarout", "End date is more than 2 years after startDate; please scale back your end date.");
			}
		}
		
		boolean startTimePhraseValid = true;
		boolean endTimePhraseValid = true;
		Matcher startTimeMatcher = null;
		if(!StringUtils.isBlank(fbo.getStartTimePhrase())) {
			startTimeMatcher = TIME_PATTERN.matcher(fbo.getStartTimePhrase());
			if(!startTimeMatcher.matches()) {
				errors.rejectValue("startTimePhrase", "field.timeparseexception", "Start time does not match expected format (hh:mm am|pm).");
				startTimePhraseValid = false;
			} else if(Integer.parseInt(startTimeMatcher.group(1)) > 12) {
				errors.rejectValue("startTimePhrase", "field.militarytime", "Start time should start with a number between 1 and 12; do not use military time.");
				startTimePhraseValid = false;
			}
		} else {
			startTimePhraseValid = false;
		}
		Matcher endTimeMatcher = null;
		if(!StringUtils.isBlank(fbo.getEndTimePhrase())) {
			endTimeMatcher = TIME_PATTERN.matcher(fbo.getEndTimePhrase());
			if(!endTimeMatcher.matches()) {
				errors.rejectValue("endTimePhrase", "field.timeparseexception", "End time does not match expected format (hh:mm am|pm).");
				endTimePhraseValid = false;
			} else if(Integer.parseInt(endTimeMatcher.group(1)) > 12) {
				errors.rejectValue("endTimePhrase", "field.militarytime", "End time should start with a number between 1 and 12; do not use military time.");
				endTimePhraseValid = false;
			}
		} else {
			endTimePhraseValid = false;
		}
		// TODO validate difference between start and end time phrase (>= 15 minutes)not 
		if(startTimePhraseValid && endTimePhraseValid) {
			long minutesDifference = approximateMinutesDifference(startTimeMatcher, endTimeMatcher);
			if(minutesDifference < 15) {
				errors.rejectValue("endTimePhrase", "endTimePhrase.tooshort", "End time has to be at least 15 minutes later than the start time.");
			}
		}

		if(fbo.getVisitorsPerAppointment() < 1) {
			errors.rejectValue("visitorsPerAppointment", "visitors.toosmall", "Visitors per appointment must be greater than or equal to 1.");
		}
		if(fbo.getVisitorsPerAppointment() > 99) {
			errors.rejectValue("visitorsPerAppointment", "visitors.toosmall", "Maximum allowed value for visitors per appointment is 99.");
		}
		
		if(StringUtils.isBlank(fbo.getMeetingLocation())) {
			// forcibly set to null to guarantee proper storage
			fbo.setMeetingLocation(null);
		} else {
			if(fbo.getMeetingLocation().length() > 100) {
				errors.rejectValue("location", "location.toolong", "Location field is too long (" + fbo.getMeetingLocation().length() + "); maximum length is 100 characters.");
			}
		}
		if(!errors.hasErrors()) {
			// try to run the block builder and report any inputformatexceptions
			try {
				if(null != startDate && null != endDate) {
					AvailableBlockBuilder.createBlocks(fbo.getStartTimePhrase(), 
							fbo.getEndTimePhrase(),
							fbo.getDaysOfWeekPhrase(),
							startDate,
							endDate);
				}
			} catch (InputFormatException e) {
				errors.reject("createBlocksFailed", e.getMessage());
			}
		}
	}

	private long approximateMinutesDifference(Matcher startTimeMatcher, Matcher endTimeMatcher) {
		Date now = new Date();
		
		Calendar s = Calendar.getInstance();
		s.setTime(now);
		
		s.set(Calendar.HOUR_OF_DAY, convertHourOfDay(startTimeMatcher));
		s.set(Calendar.MINUTE, Integer.parseInt(startTimeMatcher.group(2)));
		
		Calendar e = Calendar.getInstance();
		e.setTime(now);
		
		e.set(Calendar.HOUR_OF_DAY, convertHourOfDay(endTimeMatcher));
		e.set(Calendar.MINUTE, Integer.parseInt(endTimeMatcher.group(2)));

		long endL   =  e.getTimeInMillis() +  e.getTimeZone().getOffset(e.getTimeInMillis());
		long startL = s.getTimeInMillis() + s.getTimeZone().getOffset(s.getTimeInMillis());
		
		return (endL - startL) / MILLISECS_PER_MIN;
	}
	
	private int convertHourOfDay(Matcher m) {
		String hourString = m.group(1);
		int hour = Integer.parseInt(hourString);
		String ampm = m.group(3);
		if(hour == 12 && AM.equalsIgnoreCase(ampm)) {
			return 0;
		} else if (hour != 12 && PM.equalsIgnoreCase(ampm)) {
			return hour + 12;
		} else {
			return hour;
		}
	}
}
