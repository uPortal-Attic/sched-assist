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
 * Test bench for {@link AvailableBlockFormBackingObjectValidator}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableBlockFormBackingObjectValidatorTest.java 2305 2010-07-28 17:18:54Z npblair $
 */
@SuppressWarnings("rawtypes")
public class AvailableBlockFormBackingObjectValidatorTest {

	/**
	 * Test valid form backing object, no errors.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testControl() throws Exception {
		AvailableBlockFormBackingObject fbo = new AvailableBlockFormBackingObject();
		fbo.setStartTimePhrase("20090306-1200");
		fbo.setEndTimePhrase("20090306-1230");
		
		Errors errors = new BindException(fbo, "command");
		
		AvailableBlockFormBackingObjectValidator validator = new AvailableBlockFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(0, errorCodes.size());
	}
	
	/**
	 * Leave start time uninitialized, assert 1 error.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEmptyStartTime() throws Exception {
		AvailableBlockFormBackingObject fbo = new AvailableBlockFormBackingObject();
		Errors errors = new BindException(fbo, "command");
		
		AvailableBlockFormBackingObjectValidator validator = new AvailableBlockFormBackingObjectValidator();
		validator.validate(fbo, errors);
		
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(1, errorCodes.size());
		FieldError startTimeFieldError = errors.getFieldError("startTimePhrase");
		Assert.assertNotNull(startTimeFieldError);
		Assert.assertEquals("field.required", startTimeFieldError.getCode());
	}
	
	/**
	 * Invalid start time, assert 1 error.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidStartTime() throws Exception {
		AvailableBlockFormBackingObject fbo = new AvailableBlockFormBackingObject();
		fbo.setStartTimePhrase("abcde");
		Errors errors = new BindException(fbo, "command");
		
		AvailableBlockFormBackingObjectValidator validator = new AvailableBlockFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(1, errorCodes.size());
		FieldError startTimeFieldError = errors.getFieldError("startTimePhrase");
		Assert.assertNotNull(startTimeFieldError);
		Assert.assertEquals("field.parseexception", startTimeFieldError.getCode());
	}
	
	/**
	 * Invalid end time, assert 1 error.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidEndTime() throws Exception {
		AvailableBlockFormBackingObject fbo = new AvailableBlockFormBackingObject();
		fbo.setStartTimePhrase("20090306-1200");
		fbo.setEndTimePhrase("abcde");
		Errors errors = new BindException(fbo, "command");
		
		AvailableBlockFormBackingObjectValidator validator = new AvailableBlockFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(1, errorCodes.size());
		FieldError endTimeFieldError = errors.getFieldError("endTimePhrase");
		Assert.assertNotNull(endTimeFieldError);
		Assert.assertEquals("field.parseexception", endTimeFieldError.getCode());
	}
	
	/**
	 * Both fields invalid, assert 2 error.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidBoth() throws Exception {
		AvailableBlockFormBackingObject fbo = new AvailableBlockFormBackingObject();
		fbo.setStartTimePhrase("abcde");
		fbo.setEndTimePhrase("abcde");
		Errors errors = new BindException(fbo, "command");
		
		AvailableBlockFormBackingObjectValidator validator = new AvailableBlockFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List errorCodes = errors.getAllErrors();
		Assert.assertEquals(2, errorCodes.size());
		FieldError startTimeFieldError = errors.getFieldError("startTimePhrase");
		Assert.assertNotNull(startTimeFieldError);
		Assert.assertEquals("field.parseexception", startTimeFieldError.getCode());
		
		FieldError endTimeFieldError = errors.getFieldError("endTimePhrase");
		Assert.assertNotNull(endTimeFieldError);
		Assert.assertEquals("field.parseexception", endTimeFieldError.getCode());
	}
}
