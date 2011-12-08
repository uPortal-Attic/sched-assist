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
import java.util.Locale;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.jasig.schedassist.model.mock.MockScheduleOwner;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;

/**
 * Tests for {@link EmailNotificationApplicationListener}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: EmailNotificationApplicationListenerTest.java 3071 2011-02-09 17:41:20Z npblair $
 */
public class EmailNotificationApplicationListenerTest {
	
	private Log LOG = LogFactory.getLog(this.getClass());
	private StaticMessageSource messageSource = new StaticMessageSource();
	
	/**
	 * Sets up the message source.
	 */
	public EmailNotificationApplicationListenerTest() {
		messageSource.addMessage("notify.email.footer", Locale.getDefault(), "Footer - link");
		messageSource.addMessage("notify.email.introduction", Locale.getDefault(), "Notify meeting with {0}");
		messageSource.addMessage("notify.email.cancel", Locale.getDefault(), "Notify cancel meeting with {0}");
		messageSource.addMessage("notify.email.cancel.reason", Locale.getDefault(), "Reason for cancellation: {0}");
		messageSource.addMessage("notify.email.location", Locale.getDefault(), "Location: {0}");
		messageSource.addMessage("notify.email.reason", Locale.getDefault(), "Reason: {0}");
		messageSource.addMessage("notify.email.time", Locale.getDefault(), "Time: {0} to {1}");
		messageSource.addMessage("notify.email.title", Locale.getDefault(), "Title: {0}");
	}
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
		
		MockCalendarAccount account = new MockCalendarAccount();
		account.setDisplayName("Some Person");
		MockScheduleOwner owner = new MockScheduleOwner(account, 1L);
		
		EmailNotificationApplicationListener listener = new EmailNotificationApplicationListener();
		listener.setMessageSource(messageSource);
		
		String messageBody = listener.createMessageBody(event, null, owner);
		LOG.debug("testCreateMessageBodyEmptyDescription: " + messageBody);
		Assert.assertTrue(messageBody.contains("Notify meeting with Some Person"));
		Assert.assertTrue(messageBody.contains("Title: test appointment with student name"));
		Assert.assertTrue(messageBody.contains("Location: some office"));
		Assert.assertTrue(messageBody.contains("Tue, May 12, 2009"));
		Assert.assertTrue(messageBody.contains("Time: 1:00 PM to 2:00 PM"));
		Assert.assertTrue(messageBody.contains("Footer - link"));
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
		event.getProperties().add(new Description("test appointment"));
		event.getProperties().add(new Location("some office"));
		MockCalendarAccount account = new MockCalendarAccount();
		account.setDisplayName("Some Person");
		MockScheduleOwner owner = new MockScheduleOwner(account, 1L);
		
		EmailNotificationApplicationListener listener = new EmailNotificationApplicationListener();
		listener.setMessageSource(messageSource);
		// default value for #isUseOriginalEventDescription (false) causes String argument to be ignored
		String messageBody = listener.createMessageBody(event, "not used", owner);
		LOG.debug("testCreateMessageBodyWithDescription: " + messageBody);
		Assert.assertTrue(messageBody.contains("Notify meeting with Some Person"));
		Assert.assertTrue(messageBody.contains("Title: test appointment with student name"));
		Assert.assertTrue(messageBody.contains("Location: some office"));
		Assert.assertTrue(messageBody.contains("Tue, May 12, 2009"));
		Assert.assertTrue(messageBody.contains("Time: 1:00 PM to 2:00 PM"));
		Assert.assertTrue(messageBody.contains("Footer - link"));
		Assert.assertTrue(messageBody.contains("Reason: test appointment"));
	}
	
	/**
	 * Verify expected output for {@link EmailNotificationApplicationListener#createMessageBody(VEvent)}
	 * when {@link EmailNotificationApplicationListener#isUseOriginalEventDescription()} is true.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateMessageBodyWithOriginalDescription() throws Exception {
		Date startDate = CommonDateOperations.getDateTimeFormat().parse("20090512-1300");
		Date endDate = CommonDateOperations.getDateTimeFormat().parse("20090512-1400");
		VEvent event = new VEvent(new net.fortuna.ical4j.model.DateTime(startDate), 
				new net.fortuna.ical4j.model.DateTime(endDate), "test appointment with student name");
		event.getProperties().add(new Description("test appointment (modified by eventutils)"));
		event.getProperties().add(new Location("some office"));
		MockCalendarAccount account = new MockCalendarAccount();
		account.setDisplayName("Some Person");
		MockScheduleOwner owner = new MockScheduleOwner(account, 1L);
		
		EmailNotificationApplicationListener listener = new EmailNotificationApplicationListener();
		listener.setUseOriginalEventDescription(true);
		listener.setMessageSource(messageSource);
		
		String messageBody = listener.createMessageBody(event, "test appointment", owner);
		LOG.debug("testCreateMessageBodyWithDescription: " + messageBody);
		Assert.assertTrue(messageBody.contains("Notify meeting with Some Person"));
		Assert.assertTrue(messageBody.contains("Title: test appointment with student name"));
		Assert.assertTrue(messageBody.contains("Location: some office"));
		Assert.assertTrue(messageBody.contains("Tue, May 12, 2009"));
		Assert.assertTrue(messageBody.contains("Time: 1:00 PM to 2:00 PM"));
		Assert.assertTrue(messageBody.contains("Footer - link"));
		Assert.assertTrue(messageBody.contains("Reason: test appointment"));
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
		MockCalendarAccount account = new MockCalendarAccount();
		account.setDisplayName("Some Person");
		MockScheduleOwner owner = new MockScheduleOwner(account, 1L);
		
		EmailNotificationApplicationListener listener = new EmailNotificationApplicationListener();
		listener.setMessageSource(messageSource);
		
		String messageBody = listener.cancelMessageBody(event, "I can't make it.", owner);
		LOG.debug("testCancelMessageBody: " + messageBody);
		Assert.assertTrue(messageBody.contains("Notify cancel meeting with Some Person"));
		Assert.assertTrue(messageBody.contains("Title: test appointment with student name"));
		Assert.assertTrue(messageBody.contains("Tue, May 12, 2009"));
		Assert.assertTrue(messageBody.contains("Time: 1:00 PM to 2:00 PM"));
		Assert.assertTrue(messageBody.contains("Footer - link"));
		Assert.assertTrue(messageBody.contains("Reason for cancellation: I can't make it."));
	}
	
	/**
	 * Oracle Calendar accounts sometimes have invalid email addresses.
	 * Test {@link EmailNotificationApplicationListener#isEmailAddressValid(String)} with some examples.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidScheduleOwnerEmail() throws Exception {
		
		Assert.assertTrue(EmailNotificationApplicationListener.isEmailAddressValid("someone@wisc.edu"));
		
		Assert.assertFalse(EmailNotificationApplicationListener.isEmailAddressValid("85778BD1FE003418E04400144FAD412A@email.invalid"));
		
		Assert.assertFalse(EmailNotificationApplicationListener.isEmailAddressValid(null));
	}
}
