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

package org.jasig.schedassist.impl.events;

import java.util.Date;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;

import org.jasig.schedassist.model.CommonDateOperations;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link EmailNotificationApplicationListener}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: EmailNotificationApplicationListenerTest.java 3071 2011-02-09 17:41:20Z npblair $
 */
public class EmailNotificationApplicationListenerTest {
	
	/**
	 * Verify expected output for {@link EmailNotificationApplicationListener#createMessageBody(VEvent)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateMessageBodyEmptyDescription() throws Exception {
		Date startDate = CommonDateOperations.getDateTimeFormat().parse("20090512-1300");
		Date endDate = CommonDateOperations.getDateTimeFormat().parse("20090512-1400");
		VEvent event = new VEvent(new net.fortuna.ical4j.model.DateTime(startDate), 
				new net.fortuna.ical4j.model.DateTime(endDate), "test appointment with student name");
		event.getProperties().add(new Location("some office"));
		String messageBody = EmailNotificationApplicationListener.createMessageBody(event, null);
		StringBuilder expected = new StringBuilder();
		final String newline = System.getProperty("line.separator");
		expected.append("The following meeting has been added to your agenda:");
		expected.append(newline);
		expected.append(newline);
		expected.append("Title: test appointment with student name");
		expected.append(newline);
		expected.append("Tue, May 12, 2009");
		expected.append(newline);
		expected.append("Time: 1:00 PM to 2:00 PM");
		expected.append(newline);
		expected.append("Location: some office");
		expected.append(newline);
		expected.append(newline);
		expected.append("This appointment was scheduled via the WiscCal Scheduling Assistant - https://tools.wisccal.wisc.edu/available/");
		Assert.assertEquals(expected.toString(), messageBody);
	}
	
	/**
	 * Verify expected output for {@link EmailNotificationApplicationListener#createMessageBody(VEvent)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateMessageBodyWithDescription() throws Exception {
		Date startDate = CommonDateOperations.getDateTimeFormat().parse("20090512-1300");
		Date endDate = CommonDateOperations.getDateTimeFormat().parse("20090512-1400");
		VEvent event = new VEvent(new net.fortuna.ical4j.model.DateTime(startDate), 
				new net.fortuna.ical4j.model.DateTime(endDate), "test appointment with student name");
		event.getProperties().add(new Location("some office"));
		String messageBody = EmailNotificationApplicationListener.createMessageBody(event, "test appointment");
		StringBuilder expected = new StringBuilder();
		final String newline = System.getProperty("line.separator");
		expected.append("The following meeting has been added to your agenda:");
		expected.append(newline);
		expected.append(newline);
		expected.append("Title: test appointment with student name");
		expected.append(newline);
		expected.append("Tue, May 12, 2009");
		expected.append(newline);
		expected.append("Time: 1:00 PM to 2:00 PM");
		expected.append(newline);
		expected.append("Location: some office");
		expected.append(newline);
		expected.append("Reason: test appointment");
		expected.append(newline);
		expected.append(newline);
		expected.append("This appointment was scheduled via the WiscCal Scheduling Assistant - https://tools.wisccal.wisc.edu/available/");
		Assert.assertEquals(expected.toString(), messageBody);
	}
	
	/**
	 * Verify expected output for {@link EmailNotificationApplicationListener#cancelMessageBody(VEvent)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCancelMessageBody() throws Exception {
		Date startDate = CommonDateOperations.getDateTimeFormat().parse("20090512-1300");
		Date endDate = CommonDateOperations.getDateTimeFormat().parse("20090512-1400");
		VEvent event = new VEvent(new net.fortuna.ical4j.model.DateTime(startDate), 
				new net.fortuna.ical4j.model.DateTime(endDate), "test appointment with student name");
		event.getProperties().add(new Location("some office"));
		String messageBody = EmailNotificationApplicationListener.cancelMessageBody(event, "I can't make it.");
		StringBuilder expected = new StringBuilder();
		final String newline = System.getProperty("line.separator");
		expected.append("The following meeting has been removed from your agenda:");
		expected.append(newline);
		expected.append(newline);
		expected.append("Title: test appointment with student name");
		expected.append(newline);
		expected.append("Tue, May 12, 2009");
		expected.append(newline);
		expected.append("Time: 1:00 PM to 2:00 PM");
		expected.append(newline);
		expected.append("Reason for cancelling: I can't make it.");
		expected.append(newline);
		expected.append(newline);
		expected.append("This appointment was scheduled via the WiscCal Scheduling Assistant - https://tools.wisccal.wisc.edu/available/");
		Assert.assertEquals(expected.toString(), messageBody);
	}
	
	@Test
	public void testInvalidScheduleOwnerEmail() throws Exception {
		
		Assert.assertTrue(EmailNotificationApplicationListener.isEmailAddressValid("someone@wisc.edu"));
		
		Assert.assertFalse(EmailNotificationApplicationListener.isEmailAddressValid("85778BD1FE003418E04400144FAD412A@email.invalid"));
		
		Assert.assertFalse(EmailNotificationApplicationListener.isEmailAddressValid(null));
	}
}
