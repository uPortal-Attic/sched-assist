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

import net.fortuna.ical4j.model.Calendar;

/**
 * Interface defining operations for calculating {@link VisibleSchedule}s.
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: IVisibleScheduleBuilder.java 2326 2010-07-30 21:20:14Z npblair $
 */
public interface IVisibleScheduleBuilder {

	/**
	 * This method should simply delegate to {@link #calculateVisibleSchedule(Date, Date, Calendar, AvailableSchedule, IScheduleOwner, IScheduleVisitor)}, 
	 * passing null in for the {@link IScheduleVisitor} argument.
	 * 
	 * As such, it will never return appointments with "ATTENDING" status (only FREE
	 * or BUSY).
	 * 
	 * @param startTime
	 * @param endTime
	 * @param calendar
	 * @param schedule
	 * @param owner
	 * @return an appropriate {@link VisibleSchedule}
	 */
	VisibleSchedule calculateVisibleSchedule(Date startTime, Date endTime, Calendar calendar, 
			AvailableSchedule schedule, IScheduleOwner owner);
	
	/**
	 * Core algorithm for calculating a {@link VisibleSchedule} for an {@link IScheduleVisitor} viewing
	 * an {@link IScheduleOwner}'s account.
	 * 
	 * @param startTime
	 * @param endTime
	 * @param calendar
	 * @param schedule
	 * @param owner
	 * @param visitor
	 * @return an appropriate {@link VisibleSchedule}
	 */
	VisibleSchedule calculateVisibleSchedule(Date startTime, Date endTime, Calendar calendar, 
			AvailableSchedule schedule, IScheduleOwner owner, IScheduleVisitor visitor);
	
	/**
	 * This method is intended to provide a means for incorporating the {@link IScheduleVisitor}'s own calendar data
	 * within the display of an {@link IScheduleOwner}s schedule.
	 * 
	 * The {@link MeetingDurations} argument is required and represents the preference of the {@link IScheduleOwner} in
	 * context.
	 * 
	 * @param startTime
	 * @param endTime
	 * @param calendar
	 * @param schedule
	 * @param meetingDurations
	 * @param visitor
	 * @return an appropriate {@link VisibleSchedule}
	 */
	VisibleSchedule calculateVisitorConflicts(Date startTime, Date endTime, Calendar calendar, 
			AvailableSchedule schedule, MeetingDurations meetingDurations, IScheduleVisitor visitor);
}