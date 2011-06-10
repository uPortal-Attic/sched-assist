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

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.impl.reminder.IReminder;
import org.jasig.schedassist.impl.reminder.ReminderService;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.Reminders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * {@link ApplicationListener} for {@link AbstractAppointmentEvent}s that
 * integrates with the {@link ReminderService}.
 * 
 * @author Nicholas Blair
 * @version $Id: ReminderServiceApplicationListener.java 3101 2011-02-28 18:42:05Z npblair $
 */
@Component
public class ReminderServiceApplicationListener implements
ApplicationListener<AbstractAppointmentEvent> {

	private ReminderService reminderService;

	/**
	 * @param reminderService the reminderService to set
	 */
	@Autowired
	public void setReminderService(ReminderService reminderService) {
		this.reminderService = reminderService;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Async
	@Override
	public void onApplicationEvent(AbstractAppointmentEvent event) {
		final IScheduleOwner owner = event.getOwner();
		final Reminders reminderPreference = owner.getRemindersPreference();
		if(reminderPreference.isEnabled()) {
			if(event instanceof AppointmentCreatedEvent || event instanceof AppointmentJoinedEvent) {
				final Date sendTime = DateUtils.addHours(event.getBlock().getStartTime(), -reminderPreference.getHours());
				if(sendTime.after(new Date())) {
					// only create the reminder if and only if the "sendTime" is in the future
					this.reminderService.createEventReminder(event.getOwner(), event.getVisitor().getCalendarAccount(), event.getBlock(), event.getEvent(), sendTime);
				}
			} else if (event instanceof AppointmentCancelledEvent || event instanceof AppointmentLeftEvent) {
				final IReminder reminder = this.reminderService.getReminder(owner, event.getVisitor().getCalendarAccount(), event.getBlock());
				if(reminder != null) {
					this.reminderService.deleteEventReminder(reminder);
				}
			} 
		}
	}

}
