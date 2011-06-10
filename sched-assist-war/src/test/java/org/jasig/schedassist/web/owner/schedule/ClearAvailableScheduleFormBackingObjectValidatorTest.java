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

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * Test bench for {@link ClearAvailableScheduleFormBackingObjectValidator}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ClearAvailableScheduleFormBackingObjectValidatorTest.java 2305 2010-07-28 17:18:54Z npblair $
 */
public class ClearAvailableScheduleFormBackingObjectValidatorTest {

	/**
	 * Test valid inputs, assert no errors.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testControl() throws Exception {
		// first test with "confirmedCancelAll" = cancel the whole schedule
		ClearAvailableScheduleFormBackingObject fbo = new ClearAvailableScheduleFormBackingObject();
		fbo.setConfirmedCancelAll(true);
		Errors errors = new BindException(fbo, "command");
		ClearAvailableScheduleFormBackingObjectValidator validator = new ClearAvailableScheduleFormBackingObjectValidator();
		validator.validate(fbo, errors);
		
		Assert.assertEquals(0, errors.getAllErrors().size());
		
		// second test with "confirmedCancelWeek" = cancel only 1 specified week
		fbo = new ClearAvailableScheduleFormBackingObject();
		fbo.setConfirmedCancelWeek(true);
		fbo.setWeekOfPhrase("20090301");
		errors = new BindException(fbo, "command");
		validator = new ClearAvailableScheduleFormBackingObjectValidator();
		validator.validate(fbo, errors);
		
		Assert.assertEquals(0, errors.getAllErrors().size());
	}
	
	/**
	 * Test invalid weekOf value, assert 1 error.
	 * 
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testInvalidWeekOf() throws Exception {
		ClearAvailableScheduleFormBackingObject fbo = new ClearAvailableScheduleFormBackingObject();
		fbo.setConfirmedCancelWeek(true);
		fbo.setWeekOfPhrase("abcdde");
		Errors errors = new BindException(fbo, "command");
		ClearAvailableScheduleFormBackingObjectValidator validator = new ClearAvailableScheduleFormBackingObjectValidator();
		validator.validate(fbo, errors);
		List fieldErrors = errors.getAllErrors();
		Assert.assertEquals(1, fieldErrors.size());
		FieldError weekOfError = errors.getFieldError("weekOfPhrase");
		Assert.assertNotNull(weekOfError);
		Assert.assertEquals("field.weekofphraseformat", weekOfError.getCode());
	}
}
