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

package org.jasig.schedassist.impl.caldav;

import java.net.URI;
import java.util.Date;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.http.HttpEntity;
import org.jasig.schedassist.model.ICalendarAccount;

/**
 * This interface specifies a number of methods focused on dialect with the CalDAV server.
 * 
 * @author Nicholas Blair
 * @version $ Id: CaldavDialect.java $
 */
public interface CaldavDialect {	
	
	/**
	 * Return a {@link URI} that resolves to the root address of the CalDAV server,
	 * e.g. 'http://localhost:8080'
	 * 
	 * @return never null {@link URI} for the CalDAV server
	 */
	URI getCaldavHost();
	
	/**
	 * Return a {@link URI} that represents the full URI to the URI field within the {@link CalendarWithURI}.
	 * It's common for CalDAV servers to respond with just a path (e.g. /caldav/some/user/file.ics) in the href
	 * attribute for a calendar-query method response. 
	 * 
	 * The intent of this method is to return a string that is composed of:
	 <pre>
	 caldavhost + calendarWithUri.getUri()
	 </pre>
	 * 
	 * Implementations may need to adjust from above, but the primary responsibility of this method is to return
	 * a string that looks like:
	 <pre>
	 http://hostname.somewhere.org:8080/caldav/some/user/file.ics
	 </pre>
	 *
	 * @param calendar
	 * @return the resolved complete URI to the calendar on the CalDAV server 
	 */
	URI resolveCalendarURI(CalendarWithURI calendar);
	/**
	 * Returns a {@link String} that represents the "Path" the {@link ICalendarAccount}'s
	 * calendar home directory.
	 * The calendar home directory is used as the base path for retrieving/storing calendar
	 * data for that calendar account.
	 * 
	 * The return value MUST be terminated with a "/".
	 * 
	 * @param calendarAccount
	 * @return the calendar home directory path for the specified calendar account.
	 */
	String getCalendarAccountHome(ICalendarAccount calendarAccount);
	
	/**
	 * Generate an appropriate {@link RequestEntity} body for storing the specified {@link VEvent}.
	 * 
	 * Per the CalDAV RFC:
	 <pre> 
	 	PUT /home/lisa/calendars/events/qwue23489.ics HTTP/1.1
   		If-None-Match: *
   		Host: cal.example.com
   		Content-Type: text/calendar
   		Content-Length: xxxx

   		BEGIN:VCALENDAR
   		VERSION:2.0
   		PRODID:-//Example Corp.//CalDAV Client//EN
   		BEGIN:VEVENT
   		UID:20010712T182145Z-123401@example.com
   		DTSTAMP:20060712T182145Z
   		DTSTART:20060714T170000Z
   		DTEND:20060715T040000Z
   		SUMMARY:Bastille Day Party
   		END:VEVENT
   		END:VCALENDAR
	 </pre>
	 *
	 * This method is focused on all of the content in the above example with the exception of
	 * the very first line ('PUT /home/lisa...').
	 * 
	 * @param event
	 * @return a {@link RequestEntity} used with the PUT request to store the specified {@link VEvent}
	 */
	HttpEntity generatePutAppointmentRequestEntity(VEvent event);
	
	/**
	 * Generate an appropriate {@link RequestEntity} body for storing the specified {@link Calendar}.
	 * 
	 * Per the CalDAV RFC:
	 <pre> 
	 	PUT /home/lisa/calendars/events/qwue23489.ics HTTP/1.1
   		If-None-Match: *
   		Host: cal.example.com
   		Content-Type: text/calendar
   		Content-Length: xxxx

   		BEGIN:VCALENDAR
   		VERSION:2.0
   		PRODID:-//Example Corp.//CalDAV Client//EN
   		BEGIN:VEVENT
   		UID:20010712T182145Z-123401@example.com
   		DTSTAMP:20060712T182145Z
   		DTSTART:20060714T170000Z
   		DTEND:20060715T040000Z
   		SUMMARY:Bastille Day Party
   		END:VEVENT
   		END:VCALENDAR
	 </pre>
	 *
	 * This method is focused on all of the content in the above example with the exception of
	 * the very first line ('PUT /home/lisa...').
	 * 
	 * @param event
	 * @return a {@link RequestEntity} used with the PUT request to store the specified {@link VEvent}
	 */
	HttpEntity generatePutAppointmentRequestEntity(Calendar calendar);
	
	/**
	 * Generate an appropriate {@link RequestEntity} body for retrieving Calendar data between
	 * the specified dates.
	 * 
	 * @param startDate
	 * @param endDate
	 * @return a {@link RequestEntity} used with the REPORT request to retrieve an account's Calendar data between the 2 {@link Date}s
	 */
	HttpEntity generateGetCalendarRequestEntity(Date startDate, Date endDate);
}