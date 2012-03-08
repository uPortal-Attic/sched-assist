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

/**
 * 
 */
package org.jasig.schedassist.web.visitor;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.VisibleWindow;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link CreateAppointmentFormController}.
 * 
 * @author Nicholas Blair
 * @version $Id$
 */
public class CreateAppointmentFormControllerTest {

	@Test
	public void testValidateChosenStartTime() throws InputFormatException {
		CreateAppointmentFormController controller = new CreateAppointmentFormController();
		
		VisibleWindow window = VisibleWindow.fromKey("1,1");
		try {
			Date now = new Date();
			controller.validateChosenStartTime(window, now);
			Assert.fail("expected SchedulingException not thrown");
		} catch (SchedulingException e) {
			// expected, success
		}
		
		try {
			controller.validateChosenStartTime(window, DateUtils.addHours(new Date(), 2));
		} catch (SchedulingException e) {
			Assert.fail("expected SchedulingException not thrown for date 1 hour after window start");
		}
		
		try {
			// subtract an hour from the end - can't assume it's 167 hours, or this test will fail if start/end wrap a daylight savings boundary
			controller.validateChosenStartTime(window, DateUtils.addHours(window.calculateCurrentWindowEnd(), -1));
		} catch (SchedulingException e) {
			Assert.fail("expected SchedulingException not thrown for date 1 hour before window end");
		}
		
		try {
			controller.validateChosenStartTime(window, DateUtils.addHours(window.calculateCurrentWindowEnd(), 1));
			Assert.fail("expected SchedulingException not thrown for date 1 hour after window end");
		} catch (SchedulingException e) {
			// expected, success
		}

	}
}
