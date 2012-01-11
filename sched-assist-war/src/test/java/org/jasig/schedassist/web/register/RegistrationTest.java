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

import java.util.ArrayList;

import org.jasig.schedassist.web.owner.preferences.PreferencesFormBackingObjectValidator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.binding.mapping.impl.DefaultMappingResults;
import org.springframework.binding.message.Message;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.webflow.test.MockRequestContext;
import org.springframework.webflow.validation.DefaultValidationContext;

/**
 * Test bench for {@link Registration} validation.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: RegistrationTest.java 2988 2011-01-27 16:57:45Z npblair $
 */
public class RegistrationTest {
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void validateDefaultPreferences() throws Exception {
		Registration registration = new Registration(new PreferencesFormBackingObjectValidator());
		
		// noteboard is only field that must be set
		registration.setNoteboard("Some noteboard value");
		ValidationContext context = new DefaultValidationContext(new MockRequestContext(), 
				"event", 
				new DefaultMappingResults(registration, registration, new ArrayList<Object>()));
		
		registration.validateSetPreferences(context);
		Assert.assertFalse(context.getMessageContext().hasErrorMessages());		
		
		// validate proper setting of meetingLimit fields
		Assert.assertFalse(registration.isEnableMeetingLimit());
		Assert.assertEquals(-1, registration.getMeetingLimitValue());
		
		registration.setMeetingLimitValue(1);
		Assert.assertTrue(registration.isEnableMeetingLimit());
		
		registration.setMeetingLimitValue(-1);
		Assert.assertFalse(registration.isEnableMeetingLimit());
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void validateEmptyNoteboard() throws Exception {
		Registration registration = new Registration(new PreferencesFormBackingObjectValidator());
		
		
		ValidationContext context = new DefaultValidationContext(new MockRequestContext(), 
				"event", 
				new DefaultMappingResults(registration, registration, new ArrayList<Object>()));
		
		registration.validateSetPreferences(context);
		Assert.assertTrue(context.getMessageContext().hasErrorMessages());		
		Message [] messages= context.getMessageContext().getAllMessages();
		Assert.assertEquals(1, messages.length);
		Assert.assertEquals("Noteboard field is required.", messages[0].getText());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void validateLargeNoteboard() throws Exception {
		Registration registration = new Registration(new PreferencesFormBackingObjectValidator());
		StringBuilder noteboard = new StringBuilder();
		final int pastLimit = PreferencesFormBackingObjectValidator.COLUMN_HARD_LIMIT + 1;
		for(int i = 0; i < pastLimit; i++) {
			noteboard.append("a");
		}
		registration.setNoteboard(noteboard.toString());
		
		ValidationContext context = new DefaultValidationContext(new MockRequestContext(), 
				"event", 
				new DefaultMappingResults(registration, registration, new ArrayList<Object>()));
		
		registration.validateSetPreferences(context);
		Assert.assertTrue(context.getMessageContext().hasErrorMessages());		
		Message [] messages= context.getMessageContext().getAllMessages();
		Assert.assertEquals(1, messages.length);
		Assert.assertEquals("Noteboard is too long (" + pastLimit + " characters); maximum length is 500 characters.", messages[0].getText());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void validateControlSchedule() throws Exception {
		Registration registration = new Registration(new PreferencesFormBackingObjectValidator());
		registration.setStartTimePhrase("9:00 AM");
		registration.setEndTimePhrase("10:00 AM");
		registration.setDaysOfWeekPhrase("MWF");
		registration.setStartDatePhrase("07/01/2009");
		registration.setEndDatePhrase("09/01/2009");
		
		ValidationContext context = new DefaultValidationContext(new MockRequestContext(), 
				"event", 
				new DefaultMappingResults(registration, registration, new ArrayList<Object>()));
		
		registration.validateSetSchedule(context);
		Assert.assertFalse(context.getMessageContext().hasErrorMessages());		
		Assert.assertTrue(registration.isScheduleSet());
	}
	
	@Test
	public void testValidateStartEndDateDifference() throws Exception {
		Registration registration = new Registration(new PreferencesFormBackingObjectValidator());
		registration.setStartTimePhrase("9:00 AM");
		registration.setEndTimePhrase("10:00 AM");
		registration.setDaysOfWeekPhrase("MWF");
		registration.setStartDatePhrase("07/01/2009");
		registration.setEndDatePhrase("09/01/2011");
		
		ValidationContext context = new DefaultValidationContext(new MockRequestContext(), 
				"event", 
				new DefaultMappingResults(registration, registration, new ArrayList<Object>()));
		
		registration.validateSetSchedule(context);
		Assert.assertTrue(context.getMessageContext().hasErrorMessages());		
		Message [] messages= context.getMessageContext().getAllMessages();
		Assert.assertEquals(1, messages.length);
		Assert.assertEquals("End date is more than 2 years after startDate; please scale back your end date.", messages[0].getText());
	}
	
	@Test
	public void testValidateMinimumTimeDifference() throws Exception {
		Registration registration = new Registration(new PreferencesFormBackingObjectValidator());
		registration.setStartTimePhrase("9:00 AM");
		registration.setEndTimePhrase("9:10 AM");
		registration.setDaysOfWeekPhrase("MWF");
		registration.setStartDatePhrase("07/01/2010");
		registration.setEndDatePhrase("09/01/2011");
		
		ValidationContext context = new DefaultValidationContext(new MockRequestContext(), 
				"event", 
				new DefaultMappingResults(registration, registration, new ArrayList<Object>()));
		
		registration.validateSetSchedule(context);
		Assert.assertTrue(context.getMessageContext().hasErrorMessages());		
		Message [] messages= context.getMessageContext().getAllMessages();
		Assert.assertEquals(1, messages.length);
		Assert.assertEquals("End time has to be at least 15 minutes later than the start time.", messages[0].getText());
	}
}
