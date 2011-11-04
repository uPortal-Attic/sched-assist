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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.NullAffiliationSourceImpl;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.reminder.IReminder;
import org.jasig.schedassist.impl.reminder.ReminderService;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.DefaultEventUtilsImpl;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.jasig.schedassist.model.mock.MockScheduleOwner;
import org.jasig.schedassist.model.mock.MockScheduleVisitor;
import org.junit.Test;

/**
 * Tests for {@link AutomaticAppointmentCancellationApplicationListener}.
 * 
 * @author Nicholas Blair
 * @version $Id: AutomaticAppointmentCancellationApplicationListenerTest.java $
 */
public class AutomaticAppointmentCancellationApplicationListenerTest {

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
		Date startDate = new Date();
		Date endDate = DateUtils.addHours(startDate, 1);
		AvailableBlock block = AvailableBlockBuilder.createBlock(startDate, endDate);
		
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
		VEvent event = eventUtils.constructAvailableAppointment(block, owner, visitor, "test appointment");
		OwnerDao ownerDao = mock(OwnerDao.class);
		ReminderService reminderService = mock(ReminderService.class);
		
		when(ownerDao.locateOwner(ownerAccount)).thenReturn(owner);
		AvailableBlock expectedBlock = AvailableBlockBuilder.createBlock(startDate, endDate);
		List<IReminder> reminders = new ArrayList<IReminder>();
		when(reminderService.getReminders(owner, expectedBlock)).thenReturn(reminders);
		
		
		AutomaticAppointmentCancellationApplicationListener listener = new AutomaticAppointmentCancellationApplicationListener();
		listener.setOwnerDao(ownerDao);
		listener.setReminderService(reminderService);
		listener.deleteEventReminder(ownerAccount, event);
		
		verify(reminderService, never()).deleteEventReminder(isA(IReminder.class));
	}
	
	@Test
	public void testDeleteEventReminderOneReminder() throws URISyntaxException {
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setDisplayName("Some Owner");
		ownerAccount.setEmailAddress("owner@wisc.edu");
		MockScheduleOwner owner = new MockScheduleOwner(ownerAccount, 1L);
		
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setDisplayName("Some Visitor");
		visitorAccount.setEmailAddress("visitor@wisc.edu");
		MockScheduleVisitor visitor = new MockScheduleVisitor(visitorAccount);
		Date startDate = new Date();
		Date endDate = DateUtils.addHours(startDate, 1);
		AvailableBlock block = AvailableBlockBuilder.createBlock(startDate, endDate);
		
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
		VEvent event = eventUtils.constructAvailableAppointment(block, owner, visitor, "test appointment");
		OwnerDao ownerDao = mock(OwnerDao.class);
		ReminderService reminderService = mock(ReminderService.class);
		
		when(ownerDao.locateOwner(ownerAccount)).thenReturn(owner);
		AvailableBlock expectedBlock = AvailableBlockBuilder.createBlock(startDate, endDate);
		List<IReminder> reminders = new ArrayList<IReminder>();
		IReminder reminder = mock(IReminder.class);
		reminders.add(reminder);
		when(reminderService.getReminders(owner, expectedBlock)).thenReturn(reminders);
		
		AutomaticAppointmentCancellationApplicationListener listener = new AutomaticAppointmentCancellationApplicationListener();
		listener.setOwnerDao(ownerDao);
		listener.setReminderService(reminderService);
		listener.deleteEventReminder(ownerAccount, event);
		
		verify(reminderService, times(1)).deleteEventReminder(reminder);
	}
	
	@Test
	public void testDeleteEventReminderMultiples() throws URISyntaxException {
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setDisplayName("Some Owner");
		ownerAccount.setEmailAddress("owner@wisc.edu");
		MockScheduleOwner owner = new MockScheduleOwner(ownerAccount, 1L);
		
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setDisplayName("Some Visitor");
		visitorAccount.setEmailAddress("visitor@wisc.edu");
		MockScheduleVisitor visitor = new MockScheduleVisitor(visitorAccount);
		Date startDate = new Date();
		Date endDate = DateUtils.addHours(startDate, 1);
		AvailableBlock block = AvailableBlockBuilder.createBlock(startDate, endDate);
		
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
		VEvent event = eventUtils.constructAvailableAppointment(block, owner, visitor, "test appointment");
		OwnerDao ownerDao = mock(OwnerDao.class);
		ReminderService reminderService = mock(ReminderService.class);
		
		when(ownerDao.locateOwner(ownerAccount)).thenReturn(owner);
		AvailableBlock expectedBlock = AvailableBlockBuilder.createBlock(startDate, endDate);
		List<IReminder> reminders = new ArrayList<IReminder>();
		IReminder reminder1 = mock(IReminder.class);
		reminders.add(reminder1);
		IReminder reminder2 = mock(IReminder.class);
		reminders.add(reminder2);
		when(reminderService.getReminders(owner, expectedBlock)).thenReturn(reminders);
		
		AutomaticAppointmentCancellationApplicationListener listener = new AutomaticAppointmentCancellationApplicationListener();
		listener.setOwnerDao(ownerDao);
		listener.setReminderService(reminderService);
		listener.deleteEventReminder(ownerAccount, event);
		
		verify(reminderService, times(1)).deleteEventReminder(reminder1);
		verify(reminderService, times(1)).deleteEventReminder(reminder2);
	}
}
