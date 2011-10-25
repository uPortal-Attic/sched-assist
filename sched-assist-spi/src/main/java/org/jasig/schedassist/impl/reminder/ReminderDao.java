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
 * Package internal interface for CRUD operations on reminder records
 * in the scheduling assistant database.
 * 
 * @author Nicholas Blair
 * @version $Id: ReminderDao.java $
 */
interface ReminderDao {

	/**
	 * 
	 * @param owner
	 * @param recipient
	 * @param appointmentBlock
	 * @param event
	 * @param sendTime
	 * @return
	 */
	IReminder createEventReminder(IScheduleOwner owner,
			ICalendarAccount recipient, AvailableBlock appointmentBlock,
			VEvent event, Date sendTime);

	/**
	 * 
	 * @param reminder
	 */
	void deleteEventReminder(IReminder reminder);

	List<PersistedReminderImpl> getPendingReminders();

	PersistedReminderImpl getReminder(IScheduleOwner owner,
			ICalendarAccount recipient, AvailableBlock appointmentBlock);

}
