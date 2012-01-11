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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Test bench for {@link PreferencesFormBackingObjectValidator}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PreferencesFormBackingObjectValidatorTest.java 3039 2011-02-03 14:55:17Z npblair $
 */
@ContextConfiguration(locations={"classpath:preferences-bean-simulation.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PreferencesFormBackingObjectValidatorTest {

	@Autowired
	private PreferencesFormBackingObjectValidator validator;
	
	@Test
	public void testAfterPropertiesSet() {
		try {
			validator.afterPropertiesSet();
		} catch (Exception e) {
			Assert.fail("default properties caused Exception in afterPropertiesSet: " + e);
		}
		
		validator.setLocationMaxLength(513);
		try {
			validator.afterPropertiesSet();
			Assert.fail("expected Exception in afterPropertiesSet not thrown for locationMaxLength beyond hard limit");
		} catch (Exception e) {
			// expected
		}
		
		validator.setLocationMaxLength(511);
		validator.setTitlePrefixMaxLength(513);
		try {
			validator.afterPropertiesSet();
			Assert.fail("expected Exception in afterPropertiesSet not thrown for titlePrefixMaxLength beyond hard limit");
		} catch (Exception e) {
			// expected
		}
		validator.setTitlePrefixMaxLength(511);
		validator.setNoteboardMaxLength(513);
		try {
			validator.afterPropertiesSet();
			Assert.fail("expected Exception in afterPropertiesSet not thrown for noteboardMaxLength beyond hard limit");
		} catch (Exception e) {
			// expected
		}
		validator.setNoteboardMaxLength(511);
		validator.setMaxMeetingLength(721);
		try {
			validator.afterPropertiesSet();
			Assert.fail("expected Exception in afterPropertiesSet not thrown for maxMeetingLength beyond hard limit");
		} catch (Exception e) {
			// expected
		}
		validator.setMaxMeetingLength(719);
		validator.setMinMeetingLength(9);
		try {
			validator.afterPropertiesSet();
			Assert.fail("expected Exception in afterPropertiesSet not thrown for minMeetingLength under hard limit");
		} catch (Exception e) {
			// expected
		}
		validator.setMinMeetingLength(10);
		try {
			validator.afterPropertiesSet();
		} catch (Exception e) {
			Assert.fail("final afterPropertiesSet invocation caused Exception: " + e);
		}
		
	}
	/**
	 * Test valid input, assert no errors.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testControl() throws Exception {
		PreferencesFormBackingObject fbo = new PreferencesFormBackingObject();
		fbo.setMeetingLength("30");
		fbo.setAllowDoubleLength(true);
		fbo.setLocation("My Office");
		fbo.setNoteboard("My noteboard text.");
		fbo.setTitlePrefix("Meeting title");
		Errors errors = new BindException(fbo, "command");
		
		validator.validate(fbo, errors);
		
		Assert.assertEquals(0, errors.getAllErrors().size());
	}
	
	/**
	 * Leave all fields empty, assert 4 errors.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEmptyFields() throws Exception {
		PreferencesFormBackingObject fbo = new PreferencesFormBackingObject();
		Errors errors = new BindException(fbo, "command");
		
		validator.validate(fbo, errors);
		Assert.assertEquals(4, errors.getAllErrors().size());
		Assert.assertEquals("meetingLength.required", errors.getFieldError("meetingLength").getCode());
		Assert.assertEquals("location.required", errors.getFieldError("location").getCode());
		Assert.assertEquals("titlePrefix.required", errors.getFieldError("titlePrefix").getCode());
		Assert.assertEquals("noteboard.required", errors.getFieldError("noteboard").getCode());
	}
	
	/**
	 * Test invalid value for durations field, assert 1 error.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidDurations() throws Exception {
		PreferencesFormBackingObject fbo = new PreferencesFormBackingObject();
		fbo.setMeetingLength("abc");
		fbo.setLocation("My Office");
		fbo.setNoteboard("My noteboard text.");
		fbo.setTitlePrefix("Meeting title");
		Errors errors = new BindException(fbo, "command");
		
		validator.validate(fbo, errors);
		Assert.assertEquals(1, errors.getAllErrors().size());
		Assert.assertEquals("meetingLength.invalid", errors.getFieldError("meetingLength").getCode());
	}
	
	/**
	 * Test invalid value for noteboard field, assert 1 error.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidNoteboard() throws Exception {
		PreferencesFormBackingObject fbo = new PreferencesFormBackingObject();
		fbo.setMeetingLength("30");
		fbo.setLocation("My Office");
		// build a noteboard with 201 characters.
		StringBuilder noteboard = new StringBuilder();
		for(int i = 0; i < PreferencesFormBackingObjectValidator.COLUMN_HARD_LIMIT + 1; i++) {
			noteboard.append("a");
		}
		fbo.setNoteboard(noteboard.toString());
		fbo.setTitlePrefix("Meeting title");
		Errors errors = new BindException(fbo, "command");
		
		validator.validate(fbo, errors);
		Assert.assertEquals(1, errors.getAllErrors().size());
		Assert.assertEquals("noteboard.toolarge", errors.getFieldError("noteboard").getCode());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMeetingLimit() throws Exception {
		PreferencesFormBackingObject fbo = new PreferencesFormBackingObject();
		fbo.setMeetingLength("30");
		fbo.setLocation("My Office");
		fbo.setTitlePrefix("Meeting title");
		fbo.setNoteboard("Valid noteboard");
		Errors errors = new BindException(fbo, "command");
		
		
		fbo.setMeetingLimitValue(1);
		Assert.assertTrue(fbo.isEnableMeetingLimit());
		validator.validate(fbo, errors);
		Assert.assertEquals(0, errors.getErrorCount());
		
		fbo.setMeetingLimitValue(-1);
		Assert.assertFalse(fbo.isEnableMeetingLimit());
		errors = new BindException(fbo, "command");
		validator.validate(fbo, errors);
		Assert.assertEquals(0, errors.getErrorCount());
		
		
		fbo.setMeetingLimitValue(10);
		Assert.assertTrue(fbo.isEnableMeetingLimit());
		errors = new BindException(fbo, "command");
		validator.validate(fbo, errors);
		Assert.assertEquals(0, errors.getErrorCount());
		
		fbo.setMeetingLimitValue(11);
		Assert.assertTrue(fbo.isEnableMeetingLimit());
		errors = new BindException(fbo, "command");
		validator.validate(fbo, errors);
		Assert.assertEquals(1, errors.getErrorCount());
	}
	
	/**
	 * Test that validator accurately removes '\r' from noteboard input.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWindowsControlMInNoteboard() throws Exception {
		PreferencesFormBackingObject fbo = new PreferencesFormBackingObject();
		fbo.setMeetingLength("30");
		fbo.setAllowDoubleLength(true);
		fbo.setLocation("My Office");
		final String windowsCreatedNoteboard = "My noteboard text.\r\nWith Windows newline.";
		fbo.setNoteboard(windowsCreatedNoteboard);
		
		fbo.setTitlePrefix("Meeting title");
		Errors errors = new BindException(fbo, "command");
		
		validator.validate(fbo, errors);
		
		Assert.assertEquals(0, errors.getAllErrors().size());
		Assert.assertNotSame(fbo.getNoteboard(), windowsCreatedNoteboard);
		Assert.assertEquals("My noteboard text.\nWith Windows newline.", fbo.getNoteboard());
		Assert.assertEquals(windowsCreatedNoteboard.length() - 1, fbo.getNoteboard().length());
	}
}
