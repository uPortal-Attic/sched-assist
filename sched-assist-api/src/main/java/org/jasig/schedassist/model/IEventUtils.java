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

package org.jasig.schedassist.model;

import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Uid;

import org.jasig.schedassist.ICalendarDataDao;

/**
 * This interface provides methods to construct iCal4j {@link VEvent}s and
 * {@link Attendee}s for the Scheduling Assistant stack.
 * It also provides short-hand inspection methods for these same objects.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: IEventUtils.java 28 2011-05-04 15:18:26Z nblair $
 */
public interface IEventUtils {

	/**
	 * Construct an iCalendar EVENT for the Scheduling Assistant system.
	 * 
	 * The SUMMARY of the EVENT will start with the owner's MEETING_PREFIX preference and end with the full name of the visitor.
	 * The LOCATION of the EVENT will be set to the owner's location preference.
	 * The CLASS property will be set to "NORMAL".
	 * The STATUS property will be set to "CONFIRMED".
	 * 
	 * If the owner and visitor represent the same person, only one ATTENDEE will be added, and will be marked with 
	 * {@link AppointmentRole#BOTH}.
	 * Otherwise, owner and visitor will be added as ATTENDEEs with the corresponding {@link AppointmentRole}.
	 * 
	 * The eventDescription argument will be added to the DESCRIPTION of the event. If the owner is detected as an academic advisor, and 
	 * the visitor is a student, the student's "wiscedustudentid" value will be appended to the DESCRIPTION.
	 *
	 * @param block the selected {@link AvailableBlock} 
	 * @param owner the owner of the appointment
	 * @param visitor the visitor to the appointment
	 * @param eventDescription text to enter into the DESCRIPTION property for the appointment
	 * @return the new event
	 * @throws IllegalArgumentException if any of the arguments (except the guids) are null, or if the data is not parsed properly by iCal4j
	 */
	VEvent constructAvailableAppointment(AvailableBlock block, IScheduleOwner owner, IScheduleVisitor visitor, 
			String eventDescription);
	
	/**
	 * Construct an {@link Attendee} appropriate for the specified {@link ICalendarAccount}
	 * in the visitor role.
	 * 
	 * @see AppointmentRole#VISITOR
	 * @param calendarAccount
	 * @param role
	 * @return an appropriate attendee property
	 */
	Attendee constructVisitorAttendee(ICalendarAccount calendarAccount);
	
	/**
	 * Walk through the attendee list in the {@link VEvent} argument.
	 * Return the matching {@link Attendee} for the {@link ICalendarAccount} argument, or null
	 * if the {@link ICalendarAccount} is not in the attendee list.
	 * 
	 * @see #attendeeMatchesPerson(Property, ICalendarAccount)
	 * @param event
	 * @param calendarUser
	 * @return a matching attendee Property for the calendar account in the event, or null if not found.
	 */
	Property getAttendeeForUserFromEvent(VEvent event, ICalendarAccount calendarUser);
	
	/**
	 * If the event is a Scheduling Assistant event, retrieve only the attendees on the event set by the Available system.
	 * Otherwise, return an empty {@link PropertyList}.
	 * 
	 * @param event
	 * @return only the Available attendees from the provided event
	 */
	PropertyList getAttendeeListFromEvent(VEvent event);
	
	/**
	 * 
	 * @param attendee
	 * @param calendarAccount
	 * @return true if the {@link Property} is an attendee that matches the {@link ICalendarAccount}
	 */
	boolean attendeeMatchesPerson(Property attendee, ICalendarAccount calendarAccount);
	/**
	 * Return the number of {@link Attendee}s in the event
	 * that have the role {@link AppointmentRole#VISITOR}.
	 * 
	 * @param event
	 * @return a count of schedule visitors in the attendee list of the event argument
	 */
	int getScheduleVisitorCount(VEvent event);
	
	/**
	 * This method defines our criteria for which {@link VEvent}s will cause a conflict
	 * (either a red/busy block in the visible schedule or cause ConflictExistsExceptions).
	 * 
	 * @param calendarAccount
	 * @param event an VEvent, including non-scheduling assistant appointments
	 * @return true if the specified {@link VEvent} will cause a conflict for the {@link ICalendarAccount}
	 */
	boolean willEventCauseConflict(ICalendarAccount calendarAccount, VEvent event);
	
	/**
	 * Returns true if the visitor and owner are in the {@link VEvent}'s attendee list, 
	 * the visitor argument has VISITOR role in the event, and the owner argument has OWNER role in the event.
	 * 
	 * @param event
	 * @param visitor
	 * @param owner
	 * @return true if the visitor and owner are in the {@link VEvent}'s attendee list, the visitor argument has VISITOR role in the event, and the owner argument has OWNER role in the event
	 */
	boolean isAttendingMatch(VEvent event, IScheduleVisitor visitor, IScheduleOwner owner);
	
	/**
	 * 
	 * @param event
	 * @param proposedVisitor
	 * @return true if proposedVisitor is in the {@link VEvent}'s attendee list as an {@link IScheduleVisitor}
	 */
	boolean isAttendingAsVisitor(VEvent event, ICalendarAccount proposedVisitor);
	/**
	 * 
	 * @param event
	 * @param proposedOwner
	 * @return true if proposedOwner is in the {@link VEvent}'s attendee list as an {@link IScheduleOwner}
	 */
	boolean isAttendingAsOwner(VEvent event, ICalendarAccount proposedOwner);
	
	/**
	 * Convert the {@link AvailableSchedule} into an iCalendar {@link Calendar}
	 * for the purposes of reflection back into the calendar system.
	 * 
	 * @see ICalendarDataDao#reflectAvailableSchedule(IScheduleOwner, AvailableSchedule)
	 * @param availableSchedule
	 * @return the owner's availability schedule as a list of Calendars appropriate for storing in the calendar system
	 */
	List<Calendar> convertScheduleForReflection(AvailableSchedule availableSchedule);
	
	/**
	 * Generate a new {@link Uid}, intended for use with Scheduling Assistant {@link VEvent}s.
	 * 
	 * @return a new {@link Uid}
	 */
	Uid generateNewUid();
	
	/**
	 * Wrap the {@link VEvent} argument in a {@link Calendar}.
	 * 
	 * @param event
	 * @return
	 */
	Calendar wrapEventInCalendar(VEvent event);
	
	/**
	 * If the event argument is an event created by the Scheduling Assistant, return the value of
	 * it's {@link VisitorLimit} property.
	 * If the event argument is not a Scheduling Assistant event, this method returns null.
	 * 
	 * @param event
	 * @return the value of the {@link VisitorLimit} if the event is a scheduling assistant appointment; if not return null
	 */
	Integer getEventVisitorLimit(VEvent event);
	
	/**
	 * 
	 * @param event
	 * @return true if the event recurs (either by RRULE or RDATE)
	 */
	boolean isEventRecurring(VEvent event);
	
	/**
	 * Calculate recurrence dates for the specified events between the 2 date boundaries.
	 * 
	 * @param event
	 * @param startBoundary
	 * @param endBoundary
	 * @return a never null, but possibly empty, {@link PeriodList}
	 */
	PeriodList calculateRecurrence(VEvent event, Date startBoundary, Date endBoundary);
	
	/**
	 * If the {@link Calendar} contains one and only one event (series), return the {@link Uid}.
	 * 
	 * @param calendar
	 * @return the distinct {@link Uid} contains by the event in this calendar
	 */
	Uid extractUid(Calendar calendar);
}