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

package org.jasig.schedassist.impl.reminder;

import java.util.Locale;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.jasig.schedassist.model.mock.MockScheduleOwner;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;

/**
 * @author Nicholas Blair
 * @version $Id: DefaultReminderServiceImplTest.java $
 */
public class DefaultReminderServiceImplTest {

	private Log LOG = LogFactory.getLog(this.getClass());
	private StaticMessageSource messageSource = new StaticMessageSource();
	public DefaultReminderServiceImplTest() {
		messageSource.addMessage("reminder.email.footer", Locale.getDefault(), "Footer - link");
		messageSource.addMessage("reminder.email.introduction", Locale.getDefault(), "Reminder, meeting with {0}");
		messageSource.addMessage("reminder.email.location", Locale.getDefault(), "Location: {0}");
		messageSource.addMessage("reminder.email.time", Locale.getDefault(), "Time: {0} to {1}");
		messageSource.addMessage("reminder.email.title", Locale.getDefault(), "Title: {0}");
	}
	@Test
	public void testCreateMessageBodyControl() throws InputFormatException {
		VEvent event = new VEvent(new Date(CommonDateOperations.parseDateTimePhrase("20110830-1200")), 
				new Date(CommonDateOperations.parseDateTimePhrase("20110830-1300")), 
				"some summary");

		event.getProperties().add(new Location("somewhere"));
		
		DefaultReminderServiceImpl reminderService = new DefaultReminderServiceImpl();
		reminderService.setMessageSource(messageSource);
		MockCalendarAccount account = new MockCalendarAccount();
		account.setDisplayName("Some Person");
		MockScheduleOwner owner = new MockScheduleOwner(account, 1L);
		String messageBody = reminderService.createMessageBody(event, owner);
		LOG.debug("testCreateMessageBodyControl: " + messageBody);
		Assert.assertTrue(messageBody.contains("Title: some summary"));
		Assert.assertTrue(messageBody.contains("Location: somewhere"));
	}
	
	@Test
	public void testCreateMessageBodyNoLocation() throws InputFormatException {
		VEvent event = new VEvent(new Date(CommonDateOperations.parseDateTimePhrase("20110830-1200")), 
				new Date(CommonDateOperations.parseDateTimePhrase("20110830-1300")), 
				"some summary");
		
		DefaultReminderServiceImpl reminderService = new DefaultReminderServiceImpl();
		reminderService.setMessageSource(messageSource);

		MockCalendarAccount account = new MockCalendarAccount();
		account.setDisplayName("Some Person");
		MockScheduleOwner owner = new MockScheduleOwner(account, 1L);
		String messageBody = reminderService.createMessageBody(event, owner);
		LOG.debug("testCreateMessageBodyNoLocation: " + messageBody);
		Assert.assertTrue(messageBody.contains("Title: some summary"));
		Assert.assertFalse(messageBody.contains("Location"));
	}
}
