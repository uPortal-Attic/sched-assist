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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * Test bench for {@link BlockBuilderFormBackingObjectValidator}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: BlockBuilderFormBackingObjectValidatorTest.java 2713 2010-09-24 20:13:27Z npblair $
 */
@SuppressWarnings("rawtypes")
public class BlockBuilderFormBackingObjectValidatorTest {

	/**
	 * Test valid form backing object, no errors.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testControl() throws Exception {
		BlockBuilderFormBackingObject fbo = new BlockBuilderFormBackingObject();
		fbo.setDaysOfWeekPhrase("NMTWRFS");
		fbo.setStartTimePhrase("9:00 AM");
		fbo.setEndTimePhrase("3:59 PM");
		fbo.setStartDatePhrase("1/1/2009");
		fbo.setEndDatePhrase("6/1/2009");
		fbo.setVisitorsPerAppointment(1);
		
		Errors errors = new BindException(fbo, "command");
		
		BlockBuilderFormBackingObjectValidator validator = new BlockBuilderFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(0, errorCodes.size());
	}
	
	/**
	 * Test valid form backing object using lower case text, no errors.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLowercase() throws Exception {
		BlockBuilderFormBackingObject fbo = new BlockBuilderFormBackingObject();
		fbo.setDaysOfWeekPhrase("nmtwrfs");
		fbo.setStartTimePhrase("9:00 am");
		fbo.setEndTimePhrase("3:59 pm");
		fbo.setStartDatePhrase("1/1/2009");
		fbo.setEndDatePhrase("6/1/2009");
		fbo.setVisitorsPerAppointment(1);
		
		Errors errors = new BindException(fbo, "command");
		
		BlockBuilderFormBackingObjectValidator validator = new BlockBuilderFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(0, errorCodes.size());
	}
	
	/**
	 * Leave all fields empty, expect 6 errors.
	 * @throws Exception
	 */
	@Test
	public void testAllFieldsEmpty() throws Exception {
		BlockBuilderFormBackingObject fbo = new BlockBuilderFormBackingObject();
		
		Errors errors = new BindException(fbo, "command");
		
		BlockBuilderFormBackingObjectValidator validator = new BlockBuilderFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(6, errorCodes.size());
	}
	
	/**
	 * Test invalid value for daysOfWeekPhrase.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidDaysOfWeek() throws Exception {
		BlockBuilderFormBackingObject fbo = new BlockBuilderFormBackingObject();
		fbo.setDaysOfWeekPhrase("NMTWRFX");
		fbo.setStartTimePhrase("9:11 AM");
		fbo.setEndTimePhrase("3:00 PM");
		fbo.setStartDatePhrase("1/1/2009");
		fbo.setEndDatePhrase("6/1/2009");
		fbo.setVisitorsPerAppointment(1);
		
		Errors errors = new BindException(fbo, "command");
		
		BlockBuilderFormBackingObjectValidator validator = new BlockBuilderFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(1, errorCodes.size());
		FieldError daysFieldError = errors.getFieldError("daysOfWeekPhrase");
		Assert.assertNotNull(daysFieldError);
		Assert.assertEquals("invalid.daysOfWeekPhrase", daysFieldError.getCode());
	}
	
	/**
	 * Test invalid values for startTimePhrase.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidStartTime() throws Exception {
		BlockBuilderFormBackingObject fbo = new BlockBuilderFormBackingObject();
		fbo.setDaysOfWeekPhrase("NMTWRFS");
		fbo.setStartTimePhrase("13:00 AM");
		fbo.setEndTimePhrase("3:22 PM");
		fbo.setStartDatePhrase("1/1/2009");
		fbo.setEndDatePhrase("6/1/2009");
		fbo.setVisitorsPerAppointment(1);
		
		Errors errors = new BindException(fbo, "command");
		
		BlockBuilderFormBackingObjectValidator validator = new BlockBuilderFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(1, errorCodes.size());
		FieldError startTimeError = errors.getFieldError("startTimePhrase");
		Assert.assertNotNull(startTimeError);
		Assert.assertEquals("field.militarytime", startTimeError.getCode());
		
		fbo.setStartTimePhrase("abcde");
		errors = new BindException(fbo, "command");
		validator.validate(fbo, errors);
		Assert.assertEquals(1, errorCodes.size());
		FieldError startTimeError2 = errors.getFieldError("startTimePhrase");
		Assert.assertNotNull(startTimeError2);
		Assert.assertEquals("field.timeparseexception", startTimeError2.getCode());
		
		// reset fields for testing AVAIL-117
		fbo = new BlockBuilderFormBackingObject();
		fbo.setDaysOfWeekPhrase("NMTWRFS");
		fbo.setStartTimePhrase("10:90 AM");
		fbo.setEndTimePhrase("3:00 PM");
		fbo.setStartDatePhrase("1/1/2009");
		fbo.setEndDatePhrase("6/1/2009");
		fbo.setVisitorsPerAppointment(1);
		
		errors = new BindException(fbo, "command");
		validator.validate(fbo, errors);
		errorCodes = errors.getAllErrors();
		Assert.assertEquals(1, errorCodes.size());
		startTimeError = errors.getFieldError("startTimePhrase");
		Assert.assertNotNull(startTimeError);
		Assert.assertEquals("field.timeparseexception", startTimeError.getCode());
	}
	
	/**
	 * Test invalid values for endTimePhrase.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidEndTime() throws Exception {
		BlockBuilderFormBackingObject fbo = new BlockBuilderFormBackingObject();
		fbo.setDaysOfWeekPhrase("NMTWRFS");
		fbo.setStartTimePhrase("9:33 AM");
		fbo.setEndTimePhrase("13:00 PM");
		fbo.setStartDatePhrase("1/1/2009");
		fbo.setEndDatePhrase("6/1/2009");
		fbo.setVisitorsPerAppointment(1);
		
		Errors errors = new BindException(fbo, "command");
		
		BlockBuilderFormBackingObjectValidator validator = new BlockBuilderFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(1, errorCodes.size());
		FieldError endTimeError = errors.getFieldError("endTimePhrase");
		Assert.assertNotNull(endTimeError);
		Assert.assertEquals("field.militarytime", endTimeError.getCode());
		
		fbo.setEndTimePhrase("abcde");
		errors = new BindException(fbo, "command");
		validator.validate(fbo, errors);
		Assert.assertEquals(1, errorCodes.size());
		FieldError endTimeError2 = errors.getFieldError("endTimePhrase");
		Assert.assertNotNull(endTimeError2);
		Assert.assertEquals("field.timeparseexception", endTimeError2.getCode());
		
		// reset fields for testing AVAIL-117
		fbo.setDaysOfWeekPhrase("NMTWRFS");
		fbo.setStartTimePhrase("9:44 AM");
		fbo.setEndTimePhrase("10:65 AM");
		fbo.setStartDatePhrase("1/1/2009");
		fbo.setEndDatePhrase("6/1/2009");
		fbo.setVisitorsPerAppointment(1);
		
		errors = new BindException(fbo, "command");
		validator.validate(fbo, errors);
		errorCodes = errors.getAllErrors();
		Assert.assertEquals(1, errorCodes.size());
		endTimeError2 = errors.getFieldError("endTimePhrase");
		Assert.assertNotNull(endTimeError2);
		Assert.assertEquals("field.timeparseexception", endTimeError2.getCode());
	}
	
	/**
	 * Test invalid values for startDatePhrase.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidStartDate() throws Exception {
		BlockBuilderFormBackingObject fbo = new BlockBuilderFormBackingObject();
		fbo.setDaysOfWeekPhrase("NMTWRFS");
		fbo.setStartTimePhrase("9:55 AM");
		fbo.setEndTimePhrase("3:00 PM");
		fbo.setStartDatePhrase("x/1/2009");
		fbo.setEndDatePhrase("6/1/2009");
		fbo.setVisitorsPerAppointment(1);
		
		Errors errors = new BindException(fbo, "command");
		
		BlockBuilderFormBackingObjectValidator validator = new BlockBuilderFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(2, errorCodes.size());
	}
	
	/**
	 * Test invalid values for endDatePhrase.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidEndDate() throws Exception {
		BlockBuilderFormBackingObject fbo = new BlockBuilderFormBackingObject();
		fbo.setDaysOfWeekPhrase("NMTWRFS");
		fbo.setStartTimePhrase("9:06 AM");
		fbo.setEndTimePhrase("3:09 PM");
		fbo.setStartDatePhrase("1/1/2009");
		fbo.setEndDatePhrase("x/1/2009");
		fbo.setVisitorsPerAppointment(1);
		
		Errors errors = new BindException(fbo, "command");
		
		BlockBuilderFormBackingObjectValidator validator = new BlockBuilderFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(2, errorCodes.size());
	}
	
	/**
	 * Test invalid values for visitorsPerAppointment
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidVisitorsPerAppointment() throws Exception {
		BlockBuilderFormBackingObject fbo = new BlockBuilderFormBackingObject();
		fbo.setDaysOfWeekPhrase("NMTWRFS");
		fbo.setStartTimePhrase("9:07 AM");
		fbo.setEndTimePhrase("3:08 PM");
		fbo.setStartDatePhrase("1/1/2009");
		fbo.setEndDatePhrase("6/1/2009");
		fbo.setVisitorsPerAppointment(-1);
		
		Errors errors = new BindException(fbo, "command");
		
		BlockBuilderFormBackingObjectValidator validator = new BlockBuilderFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(1, errorCodes.size());
		
		errors = new BindException(fbo, "command");
		fbo.setVisitorsPerAppointment(100);
		validator.validate(fbo, errors);
		errorCodes = errors.getAllErrors();
		Assert.assertEquals(1, errorCodes.size());
	}
}
