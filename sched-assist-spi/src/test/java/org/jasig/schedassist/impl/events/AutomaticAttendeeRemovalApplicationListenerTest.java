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

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.NullAffiliationSourceImpl;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.reminder.IReminder;
import org.jasig.schedassist.impl.reminder.ReminderService;
import org.jasig.schedassist.model.AppointmentRole;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.DefaultEventUtilsImpl;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.jasig.schedassist.model.mock.MockScheduleOwner;
import org.jasig.schedassist.model.mock.MockScheduleVisitor;
import org.junit.Test;

/**
 * @author Nicholas Blair
 * @version $Id: AutomaticAttendeeRemovalApplicationListenerTest.java $
 */
public class AutomaticAttendeeRemovalApplicationListenerTest {

	@Test
	public void testDeleteEventReminderEmptyReminders() throws URISyntaxException {
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setDisplayName("Some Owner");
		ownerAccount.setEmailAddress("owner@wisc.edu");
		MockScheduleOwner owner = new MockScheduleOwner(ownerAccount, 1L);
		
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setDisplayName("Some Visitor");
		visitorAccount.setEmailAddress("visitor@wisc.edu");
		MockScheduleVisitor visitor = new MockScheduleVisitor(visitorAccount);
		
		MockCalendarAccount visitorAccount2 = new MockCalendarAccount();
		visitorAccount2.setDisplayName("Some Visitor 2");
		visitorAccount2.setEmailAddress("visitor2@wisc.edu");
		
		Date startDate = new Date();
		Date endDate = DateUtils.addHours(startDate, 1);
		AvailableBlock block = AvailableBlockBuilder.createBlock(startDate, endDate, 3);
		
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
		VEvent event = eventUtils.constructAvailableAppointment(block, owner, visitor, "test appointment");
		Attendee visitor2attendee = eventUtils.constructSchedulingAssistantAttendee(visitorAccount2, AppointmentRole.VISITOR);
		event.getProperties().add(visitor2attendee);
		
		OwnerDao ownerDao = mock(OwnerDao.class);
		ReminderService reminderService = mock(ReminderService.class);
		
		when(ownerDao.locateOwner(ownerAccount)).thenReturn(owner);
		AvailableBlock expectedBlock = AvailableBlockBuilder.createBlock(startDate, endDate);
		List<IReminder> reminders = new ArrayList<IReminder>();
		when(reminderService.getReminders(owner, expectedBlock)).thenReturn(reminders);
		
		AutomaticAttendeeRemovalApplicationListener listener = new AutomaticAttendeeRemovalApplicationListener();
		listener.setOwnerDao(ownerDao);
		listener.setReminderService(reminderService);
		listener.deleteEventReminder(ownerAccount, event, "visitor3@wisc.edu");
		
		verify(reminderService, never()).deleteEventReminder(isA(IReminder.class));
	}
	
	/**
	 * Simulate workflow when "visitor3@wisc.edu" leaves group appointment and  
	 * matching reminder is found.
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void testDeleteEventReminderControl() throws URISyntaxException {
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setDisplayName("Some Owner");
		ownerAccount.setEmailAddress("owner@wisc.edu");
		MockScheduleOwner owner = new MockScheduleOwner(ownerAccount, 1L);
		
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setDisplayName("Some Visitor");
		visitorAccount.setEmailAddress("visitor@wisc.edu");
		MockScheduleVisitor visitor = new MockScheduleVisitor(visitorAccount);
		
		MockCalendarAccount visitorAccount2 = new MockCalendarAccount();
		visitorAccount2.setDisplayName("Some Visitor 2");
		visitorAccount2.setEmailAddress("visitor2@wisc.edu");
		
		Date startDate = new Date();
		Date endDate = DateUtils.addHours(startDate, 1);
		AvailableBlock block = AvailableBlockBuilder.createBlock(startDate, endDate, 3);
		
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
		VEvent event = eventUtils.constructAvailableAppointment(block, owner, visitor, "test appointment");
		Attendee visitor2attendee = eventUtils.constructSchedulingAssistantAttendee(visitorAccount2, AppointmentRole.VISITOR);
		event.getProperties().add(visitor2attendee);
		
		OwnerDao ownerDao = mock(OwnerDao.class);
		ReminderService reminderService = mock(ReminderService.class);
		
		when(ownerDao.locateOwner(ownerAccount)).thenReturn(owner);
		AvailableBlock expectedBlock = AvailableBlockBuilder.createBlock(startDate, endDate);
		List<IReminder> reminders = new ArrayList<IReminder>();
		when(reminderService.getReminders(owner, expectedBlock)).thenReturn(reminders);
		
		AutomaticAttendeeRemovalApplicationListener listener = new AutomaticAttendeeRemovalApplicationListener();
		listener.setOwnerDao(ownerDao);
		listener.setReminderService(reminderService);
		listener.deleteEventReminder(ownerAccount, event, "visitor3@wisc.edu");
		
		verify(reminderService, never()).deleteEventReminder(isA(IReminder.class));
	}
}
