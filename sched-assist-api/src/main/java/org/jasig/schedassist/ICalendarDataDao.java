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

package org.jasig.schedassist;

import java.util.Date;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;

/**
 * Interface for interacting with the back end calendar system.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarDao.java 2509 2010-09-08 15:44:30Z npblair $
 */
public interface ICalendarDataDao {

	/**
	 * Retrieve the Calendar for the {@link ICalendarAccount} between the specified dates.
	 * 
	 * @param calendarAccount
	 * @param startDate
	 * @param endDate
	 * @return the corresponding {@link Calendar} data
	 */
	Calendar getCalendar(ICalendarAccount calendarAccount, Date startDate, Date endDate);
	/**
	 * Lookup the {@link VEvent} that corresponds with the information in the {@link AvailableBlock}
	 * in the {@link IScheduleOwner}'s schedule.
	 * 
	 * Will only return Scheduling Assistant appointments (e.g. events created with this software); if no
	 * {@link VEvent} is stored in the owner's schedule this method returns null.
	 * 
	 * @param owner
	 * @param block
	 * @return the existing scheduling asssitant appointment, or null if none found
	 */
	VEvent getExistingAppointment(IScheduleOwner owner, AvailableBlock block);
	
	/**
	 * Create an appointment.
	 * The {@link IScheduleOwner} must be the "owner" of the appointment.
	 * The {@link IScheduleVisitor} must be invited to the appointment, and should automatically
	 * accept the invitation.
	 * 
	 * @param visitor
	 * @param owner
	 * @param block the target {@link AvailableBlock}
	 * @param eventDescription text that should be added to the DESCRIPTION property of the event
	 * @return the newly created event
	 */
	VEvent createAppointment(IScheduleVisitor visitor, IScheduleOwner owner, AvailableBlock block, String eventDescription);
	/**
	 * Cancel the specified appointment in the {@link IScheduleOwner}'s schedule.
	 * 
	 * @param visitor
	 * @param owner
	 * @param event
	 */
	void cancelAppointment(IScheduleVisitor visitor, IScheduleOwner owner, VEvent event);
	
	
	/**
	 * Add the specified {@link IScheduleVisitor} as an attendee to the {@link VEvent} in
	 * the {@link IScheduleOwner}'s schedule.
	 * 
	 * @param visitor
	 * @param owner
	 * @param appointment
	 * @return the appointment
	 * @throws SchedulingException 
	 */
	VEvent joinAppointment(IScheduleVisitor visitor, IScheduleOwner owner, VEvent appointment) throws SchedulingException;
	
	/**
	 * Remove the specified {@link IScheduleVisitor} from the attendees in the {@link VEvent} in
	 * the {@link IScheduleOwner}'s schedule.
	 * 
	 * @param visitor
	 * @param owner
	 * @param appointment
	 * @return the appointment
	 * @throws SchedulingException
	 */
	VEvent leaveAppointment(IScheduleVisitor visitor, IScheduleOwner owner, VEvent appointment) throws SchedulingException;
	
	/**
	 * Check the {@link IScheduleOwner}'s schedule for events within the times
	 * defined by the {@link AvailableBlock}.
	 * If a conflict exists, throw the {@link ConflictExistsException}.
	 * 
	 * @param owner
	 * @param block
	 * @throws ConflictExistsException
	 */
	void checkForConflicts(IScheduleOwner owner, AvailableBlock block) throws ConflictExistsException;
	
	/**
	 * The purpose of this method is to reflect a copy of the {@link IScheduleOwner}'s 
	 * current {@link AvailableSchedule} in the owner's calendar account.
	 * 
	 * If the implementation takes any action on invocation, it SHOULD replace any records
	 * found for days listed in the schedule argument.
	 * 
	 * @param owner
	 * @param schedule
	 */
	void reflectAvailableSchedule(IScheduleOwner owner, AvailableSchedule schedule);
	
	/**
	 * Purge any available schedule reflections from the specified {@link IScheduleOwner}'s
	 * account between the specified dates.
	 * 
	 * @param owner
	 * @param startDate
	 * @param endDate
	 */
	void purgeAvailableScheduleReflections(IScheduleOwner owner, Date startDate, Date endDate);
}
