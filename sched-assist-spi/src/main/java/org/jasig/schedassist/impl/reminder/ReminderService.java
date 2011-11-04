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

import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.component.VEvent;

import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;

/**
 * Interface defining operations for providing the Scheduling Assistant's internal reminder service.
 * 
 * @author Nicholas Blair
 * @version $Id: ReminderService.java 3051 2011-02-04 16:34:14Z npblair $
 */
public interface ReminderService {

	/**
	 * Create a new {@link IReminder} for the appointment 
	 * 
	 * @param owner
	 * @param recipient
	 * @param appointmentBlock
	 * @param event
	 * @param reminderSendTime
	 * @return
	 */
	IReminder createEventReminder(IScheduleOwner owner, ICalendarAccount recipient, AvailableBlock appointmentBlock, VEvent event, Date reminderSendTime);
	
	/**
	 * 
	 * @param reminder
	 */
	void deleteEventReminder(IReminder reminder);
	
	/**
	 * Retrieve the list of existing {@link IReminder}s for the {@link IScheduleOwner} and {@link AvailableBlock}.
	 * If the {@link AvailableBlock} points to a single visitor (one on one) appointment, this method will return
	 * at most 1 {@link IReminder}.
	 * If the {@link AvailableBlock} points to a multiple visitor (group) appointment, this method may
	 * return more than 1 {@link IReminder}.
	 * 
	 * @param owner
	 * @param appointmentBlock
	 * @return a possibly empty but never null {@link List} of {@link IReminder}s for the event
	 */
	List<IReminder> getReminders(IScheduleOwner owner, AvailableBlock appointmentBlock);
	
	/**
	 * Retrieve the existing {@link IReminder}, or null if it doesn't exist.
	 * 
	 * @param owner
	 * @param recipient
	 * @param appointmentBlock
	 * @return the corresponding {@link IReminder} or null
	 */
	IReminder getReminder(IScheduleOwner owner, ICalendarAccount recipient, AvailableBlock appointmentBlock);
	
	/**
	 * 
	 * @return
	 */
	List<IReminder> getPendingReminders();
	
	/**
	 * 
	 */
	void processPendingReminders();
	/**
	 * TODO delete reminders necessary?
	 * @param owner
	 */
	//void deletePendingReminders(IScheduleOwner owner);
}
