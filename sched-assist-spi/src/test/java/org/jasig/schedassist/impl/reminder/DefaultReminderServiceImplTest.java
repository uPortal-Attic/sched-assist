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

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;

import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.InputFormatException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Nicholas Blair
 * @version $Id: DefaultReminderServiceImplTest.java $
 */
public class DefaultReminderServiceImplTest {

	@Test
	public void testCreateMessageBody() throws InputFormatException {
		VEvent event = new VEvent(new Date(CommonDateOperations.parseDateTimePhrase("20110830-1200")), 
				new Date(CommonDateOperations.parseDateTimePhrase("20110830-1300")), 
				"some summary");

		event.getProperties().add(new Location("somewhere"));
		
		String messageBody = DefaultReminderServiceImpl.createMessageBody(event);
		Assert.assertTrue(messageBody.contains("Title: some summary"));
		Assert.assertTrue(messageBody.contains("Location: somewhere"));
	}
	
	@Test
	public void testCreateMessageBodyNoLocation() throws InputFormatException {
		VEvent event = new VEvent(new Date(CommonDateOperations.parseDateTimePhrase("20110830-1200")), 
				new Date(CommonDateOperations.parseDateTimePhrase("20110830-1300")), 
				"some summary");
		
		String messageBody = DefaultReminderServiceImpl.createMessageBody(event);
		Assert.assertTrue(messageBody.contains("Title: some summary"));
		Assert.assertFalse(messageBody.contains("Location"));
	}
}
