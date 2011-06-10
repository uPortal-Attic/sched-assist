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


package org.jasig.schedassist.web.visitor;

import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.MeetingDurations;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Test bench for {@link CreateAppointmentFormBackingObjectValidator}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CreateAppointmentFormBackingObjectValidatorTest.java 2059 2010-04-30 16:12:20Z npblair $
 */
public class CreateAppointmentFormBackingObjectValidatorTest {

	/**
	 * Test valid input, assert no errors.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testControl() throws Exception {
		final String startTimePhrase = "20090306-1200";
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock(startTimePhrase,"20090306-1230", 1);
		CreateAppointmentFormBackingObject fbo = new CreateAppointmentFormBackingObject(targetBlock, MeetingDurations.THIRTY);
		fbo.setReason("This is my reason for the appointment.");

		Errors errors = new BindException(fbo, "command");
		CreateAppointmentFormBackingObjectValidator validator = new CreateAppointmentFormBackingObjectValidator();
		validator.validate(fbo, errors);
		
		Assert.assertEquals(0, errors.getAllErrors().size());
	}
	
	/**
	 * Leave reason field blank, assert 1 error.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEmptyReason() throws Exception {
		final String startTimePhrase = "20090306-1200";
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock(startTimePhrase,"20090306-1230", 1);
		CreateAppointmentFormBackingObject fbo = new CreateAppointmentFormBackingObject(targetBlock, MeetingDurations.THIRTY);

		Errors errors = new BindException(fbo, "command");
		CreateAppointmentFormBackingObjectValidator validator = new CreateAppointmentFormBackingObjectValidator();
		validator.validate(fbo, errors);
		
		Assert.assertEquals(1, errors.getAllErrors().size());
		Assert.assertEquals("field.required", errors.getFieldError("reason").getCode());
	}
	
	/**
	 * Set invalid value for duration, assert 1 errror.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidDuration() throws Exception {
		final String startTimePhrase = "20090306-1200";
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock(startTimePhrase,"20090306-1230", 1);
		CreateAppointmentFormBackingObject fbo = new CreateAppointmentFormBackingObject(targetBlock, MeetingDurations.THIRTY);
		fbo.setSelectedDuration(37);
		fbo.setReason("This is my reason for the appointment.");

		Errors errors = new BindException(fbo, "command");
		CreateAppointmentFormBackingObjectValidator validator = new CreateAppointmentFormBackingObjectValidator();
		validator.validate(fbo, errors);
		Assert.assertEquals(1, errors.getAllErrors().size());
		Assert.assertEquals("selectedDuration.outofbounds", errors.getFieldError("selectedDuration").getCode());
	}
	
}
