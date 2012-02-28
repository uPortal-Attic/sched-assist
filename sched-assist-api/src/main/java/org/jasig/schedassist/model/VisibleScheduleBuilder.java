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
import java.util.SortedSet;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.NullAffiliationSourceImpl;


/**
 * This class implements the mechanism of merging the {@link IScheduleOwner}'s {@link AvailableSchedule}
 * and the {@link IScheduleOwner}'s {@link Calendar} for an {@link IScheduleVisitor}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleScheduleBuilder.java 2530 2010-09-10 20:21:16Z npblair $
 */
public class VisibleScheduleBuilder implements IVisibleScheduleBuilder {

	private static Log LOG = LogFactory.getLog(VisibleScheduleBuilder.class);

	public static final String FREE = "free";
	public static final String BUSY = "busy";
	public static final String ATTENDING = "attending";

	private IEventUtils eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
	
	/**
	 * Default Constructor, will set the eventUtils field to {@link DefaultEventUtilsImpl}.
	 */
	public VisibleScheduleBuilder() {
	}
	/**
	 * @param eventUtils
	 */
	public VisibleScheduleBuilder(IEventUtils eventUtils) {
		this.eventUtils = eventUtils;
	}

	/**
	 * @param eventUtils the eventUtils to set
	 */
	public void setEventUtils(IEventUtils eventUtils) {
		this.eventUtils = eventUtils;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IVisibleScheduleBuilder#calculateVisibleSchedule(java.util.Date, java.util.Date, net.fortuna.ical4j.model.Calendar, org.jasig.schedassist.model.AvailableSchedule, org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public VisibleSchedule calculateVisibleSchedule(final Date startTime, final Date endTime,
			final Calendar calendar, final AvailableSchedule schedule, final IScheduleOwner owner) {
		return calculateVisibleScheduleNoAttendingCheck(startTime, endTime, calendar, schedule, owner.getPreferredMeetingDurations(), owner.getCalendarAccount());
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IVisibleScheduleBuilder#calculateVisitorConflicts(java.util.Date, java.util.Date, net.fortuna.ical4j.model.Calendar, org.jasig.schedassist.model.AvailableSchedule, org.jasig.schedassist.model.MeetingDurations, org.jasig.schedassist.model.IScheduleVisitor)
	 */
	@Override
	public VisibleSchedule calculateVisitorConflicts(Date startTime,
			Date endTime, Calendar calendar, AvailableSchedule schedule,
			MeetingDurations meetingDurations, IScheduleVisitor visitor) {
		return calculateVisibleScheduleNoAttendingCheck(startTime, endTime, calendar, schedule, meetingDurations, visitor.getCalendarAccount());
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IVisibleScheduleBuilder#calculateVisibleSchedule(java.util.Date, java.util.Date, net.fortuna.ical4j.model.Calendar, org.jasig.schedassist.model.AvailableSchedule, org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.IScheduleVisitor)
	 */
	@Override
	public VisibleSchedule calculateVisibleSchedule(final Date startTime, final Date endTime, 
			final Calendar calendar, final AvailableSchedule schedule, final IScheduleOwner owner, final IScheduleVisitor visitor) {
		Validate.notNull(startTime, "startTime cannot be null");
		Validate.notNull(endTime, "endTime cannot be null");
		Validate.notNull(calendar, "calendar cannot be null");
		Validate.notNull(schedule, "available schedule cannot be null");
		Validate.notNull(owner, "owner cannot be null");

		ICalendarAccount visitorCalendarAccount = null;
		if(visitor != null) {
			visitorCalendarAccount = visitor.getCalendarAccount();
		}
		if(endTime.before(startTime)) {
			throw new IllegalArgumentException("cannot pass end time (" + endTime +") that is before start time (" + startTime + ")");
		}
		LOG.debug("startTime: " + startTime + "; endTime: " + endTime);

		final MeetingDurations durations = owner.getPreferredMeetingDurations();
		
		// expand the passed in schedule's availableBlocks
		SortedSet<AvailableBlock> availableBlocks = AvailableBlockBuilder.expand(schedule.getAvailableBlocks(), durations.getMinLength());

		// create endpoints for the subset of availableBlocks
		AvailableBlock availabilityStartBlock = AvailableBlockBuilder.createPreferredMinimumDurationBlock(startTime, durations);
		AvailableBlock availabilityEndBlock = AvailableBlockBuilder.createPreferredMinimumDurationBlock(endTime, durations);

		// trim the availableBlocks set to within startTime/endTime
		availableBlocks = availableBlocks.subSet(availabilityStartBlock, availabilityEndBlock);
		
		// construct our return value
		VisibleSchedule visibleSchedule = new VisibleSchedule(durations);
		// add the trimmed availableSchedule to the visibleSchedule as "FREE" blocks
		visibleSchedule.addFreeBlocks(availableBlocks);
		
		// now iterate through the schedule and construct blocks to overwrite in the visibleSchedul
		ComponentList events = calendar.getComponents(Component.VEVENT);
		for(Object component : events) {
			VEvent event = (VEvent) component;

			boolean causesConflict = this.eventUtils.willEventCauseConflict(owner.getCalendarAccount(), event);
			if(!causesConflict) {
				if(LOG.isDebugEnabled()) {
					LOG.debug("event will not cause conflict, skipping: " + event);
				}
				continue;
			}

			// if we reach this point, this event is not skippable,
			// it's going to be either BUSY, FREE with visitors, or ATTENDING
			if(eventUtils.isEventRecurring(event)) {
				// expand the recurrence rules
				PeriodList recurrenceList = this.eventUtils.calculateRecurrence(event, startTime, endTime);
				for(Object o : recurrenceList) {
					Period period = (Period) o;
					mutateAppropriateBlockInVisibleSchedule(visibleSchedule, event, owner.getCalendarAccount(), visitorCalendarAccount, period.getStart(), period.getEnd(), true);
				}
			} else {	
				// event is not recurring, just check block on start/end
				Date startDate = event.getStartDate().getDate();
				Date endDate = event.getEndDate(true).getDate();
				mutateAppropriateBlockInVisibleSchedule(visibleSchedule, event, owner.getCalendarAccount(), visitorCalendarAccount, startDate, endDate, true);
			}
		}
		
		return visibleSchedule;
	}
	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @param calendar
	 * @param schedule
	 * @param meetingDurations
	 * @param calendarAccount
	 * @return an appropriate {@link VisibleSchedule}
	 */
	protected VisibleSchedule calculateVisibleScheduleNoAttendingCheck(Date startTime,
			Date endTime, Calendar calendar, AvailableSchedule schedule, MeetingDurations meetingDurations, ICalendarAccount calendarAccount) {
		
		Validate.notNull(startTime, "startTime cannot be null");
		Validate.notNull(endTime, "endTime cannot be null");
		Validate.notNull(calendar, "calendar cannot be null");
		Validate.notNull(meetingDurations, "MeetingDurations argument cannot be null");
		Validate.notNull(schedule, "AvailableSchedule argument cannot be null");
		Validate.notNull(calendarAccount, "calendarAccount cannot be null");

		if(endTime.before(startTime)) {
			throw new IllegalArgumentException("cannot pass end time (" + endTime +") that is before start time (" + startTime + ")");
		}
		LOG.debug("startTime: " + startTime + "; endTime: " + endTime);
		
		// expand the passed in schedule's availableBlocks
		SortedSet<AvailableBlock> availableBlocks = AvailableBlockBuilder.expand(schedule.getAvailableBlocks(), meetingDurations.getMinLength());

		// create endpoints for the subset of availableBlocks
		AvailableBlock availabilityStartBlock = AvailableBlockBuilder.createPreferredMinimumDurationBlock(startTime, meetingDurations);
		AvailableBlock availabilityEndBlock = AvailableBlockBuilder.createPreferredMinimumDurationBlock(endTime, meetingDurations);

		// trim the availableBlocks set to within startTime/endTime
		availableBlocks = availableBlocks.subSet(availabilityStartBlock, availabilityEndBlock);
		
		// construct our return value
		VisibleSchedule visibleSchedule = new VisibleSchedule(meetingDurations);
		// add the trimmed availableSchedule to the visibleSchedule as "FREE" blocks
		visibleSchedule.addFreeBlocks(availableBlocks);
		
		// now iterate through the schedule and construct blocks to overwrite in the visibleSchedul
		ComponentList events = calendar.getComponents(Component.VEVENT);
		for(Object component : events) {
			VEvent event = (VEvent) component;

			boolean causesConflict = this.eventUtils.willEventCauseConflict(calendarAccount, event);
			if(!causesConflict) {
				if(LOG.isDebugEnabled()) {
					LOG.debug("event will not cause conflict, skipping: " + event);
				}
				continue;
			}

			// if we reach this point, this event is not skippable,
			// it's going to be either BUSY, FREE with visitors, or ATTENDING
			// whether event is recurring or not, check block on start/end
			Date startDate = event.getStartDate().getDate();
			Date endDate = event.getEndDate(true).getDate();
			mutateAppropriateBlockInVisibleSchedule(visibleSchedule, event, calendarAccount, null, startDate, endDate, false);
						
			if(eventUtils.isEventRecurring(event)) {
				// expand the recurrence rules
				PeriodList recurrenceList = this.eventUtils.calculateRecurrence(event, startTime, endTime);
				for(Object o : recurrenceList) {
					Period period = (Period) o;
					mutateAppropriateBlockInVisibleSchedule(visibleSchedule, event, calendarAccount, null, period.getStart(), period.getEnd(), false);
				}
			} 
		}
		
		return visibleSchedule;
		
	}
	
	/**
	 * Mutative method to alter the {@link VisibleSchedule} in an appropriate fashion according to the {@link VEvent}'s properties.
	 * 
	 * @param visibleSchedule
	 * @param event
	 * @param owner
	 * @param visitor
	 * @param eventInstanceStartDate
	 * @param eventInstanceEndDate
	 * @param performAttendingCheck
	 */
	void mutateAppropriateBlockInVisibleSchedule(VisibleSchedule visibleSchedule, VEvent event,
			ICalendarAccount owner, ICalendarAccount visitor, Date eventInstanceStartDate, Date eventInstanceEndDate, boolean performAttendingCheck) {
		int visitorLimit = safeVisitorLimit(event);
		final AvailableBlock eventBlock = AvailableBlockBuilder.createBlock(eventInstanceStartDate, eventInstanceEndDate, visitorLimit);
		// test to see if this appointment is an available appointment
		Property availableEventMarker = event.getProperty(SchedulingAssistantAppointment.AVAILABLE_APPOINTMENT);
		if(null == availableEventMarker || !SchedulingAssistantAppointment.TRUE.equals(availableEventMarker)) {
			// non available appointments will ALWAYS simply be busy
			visibleSchedule.setBusyBlock(eventBlock);
		} else {
			// the event is an available appointment
			// first test if it's an ATTENDING match
			if(performAttendingCheck && null != visitor && this.eventUtils.isAttendingAsOwner(event, owner) && this.eventUtils.isAttendingAsVisitor(event, visitor)) {
				visibleSchedule.setAttendingBlock(eventBlock);
			} else if (this.eventUtils.isAttendingAsOwner(event, owner)) {
				// not an attending match, check visitorLimit exceeded
				int availableVisitorCount = this.eventUtils.getScheduleVisitorCount(event);
				if(availableVisitorCount >= visitorLimit) {
					// busy
					visibleSchedule.setBusyBlock(eventBlock);
				} else {
					// visitor count is less than limit - this is still free
					// amend the block to represent current visitor count 
					eventBlock.setVisitorsAttending(availableVisitorCount);
					visibleSchedule.overwriteFreeBlockOnlyIfPresent(eventBlock);
				}
			} else {
				// the event is an available appointment, but does not match attending criteria and should
				// be considered busy
				visibleSchedule.setBusyBlock(eventBlock);
			}
		}
	}

	/**
	 * Safely return the value of the {@link VisitorLimit} of the event.
	 * If it's not set, this returns 1.
	 * 
	 * @param event
	 * @return the value of the {@link VisitorLimit}, or 1 if not set.
	 */
	int safeVisitorLimit(VEvent event) {
		Integer visitorLimit = eventUtils.getEventVisitorLimit(event);
		if(visitorLimit == null) {
			return 1;
		}
		 
		return visitorLimit;
	}
}
