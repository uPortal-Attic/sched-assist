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

package org.jasig.schedassist.impl;

import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.NoAppointmentExistsException;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.impl.events.AppointmentCancelledEvent;
import org.jasig.schedassist.impl.events.AppointmentCreatedEvent;
import org.jasig.schedassist.impl.events.AppointmentJoinedEvent;
import org.jasig.schedassist.impl.events.AppointmentLeftEvent;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.AvailableVersion;
import org.jasig.schedassist.model.IEventUtils;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.IVisibleScheduleBuilder;
import org.jasig.schedassist.model.VisibleSchedule;
import org.jasig.schedassist.model.VisibleWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link SchedulingAssistantService}.
 * 
 * Note that the scheduleAppointment method is synchronized, as there is
 * no guarantees that the {@link CalendarDao} will reject event creation in case of conflict.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableServiceImpl.java 2891 2010-11-11 16:19:39Z npblair $
 */
@Service("schedulingAssistantService")
public final class SchedulingAssistantServiceImpl implements SchedulingAssistantService, ApplicationEventPublisherAware {

	private ICalendarDataDao calendarDao;
	private AvailableScheduleDao availableScheduleDao;
	private ApplicationEventPublisher applicationEventPublisher;
	private IVisibleScheduleBuilder visibleScheduleBuilder;
	private IEventUtils eventUtils;
	private Log LOG = LogFactory.getLog(this.getClass());

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(org.springframework.context.ApplicationEventPublisher)
	 */
	@Autowired
	@Override
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(final AvailableScheduleDao availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
	}
	/**
	 * @param calendarDataDao the calendarDataDao to set
	 */
	@Autowired
	public void setCalendarDataDao(final ICalendarDataDao calendarDataDao) {
		this.calendarDao = calendarDataDao;
	}
	/**
	 * @param visibleScheduleBuilder the visibleScheduleBuilder to set
	 */
	@Autowired
	public void setVisibleScheduleBuilder(
			IVisibleScheduleBuilder visibleScheduleBuilder) {
		this.visibleScheduleBuilder = visibleScheduleBuilder;
	}
	/**
	 * @param eventUtils the eventUtils to set
	 */
	@Autowired
	public void setEventUtils(IEventUtils eventUtils) {
		this.eventUtils = eventUtils;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.SchedulingAssistantService#getExistingAppointment(org.jasig.schedassist.model.AvailableBlock, org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public VEvent getExistingAppointment(AvailableBlock targetBlock,
			IScheduleOwner owner) {
		VEvent result = calendarDao.getExistingAppointment(owner, targetBlock);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.SchedulingAssistantService#getExistingAppointment(org.jasig.schedassist.model.AvailableBlock, org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.IScheduleVisitor)
	 */
	@Override
	public VEvent getExistingAppointment(AvailableBlock targetBlock,
			IScheduleOwner owner, IScheduleVisitor visitor) {
		VEvent event = getExistingAppointment(targetBlock, owner);		
		if(event != null && this.eventUtils.isAttendingAsVisitor(event, visitor.getCalendarAccount())) {
			return event;
		}
		
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.SchedulingAssistantService#getVisibleSchedule(org.jasig.schedassist.model.IScheduleVisitor, org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public VisibleSchedule getVisibleSchedule(IScheduleVisitor visitor, IScheduleOwner owner) {
		Date [] windowBoundaries = calculateOwnerWindowBounds(owner);
		VisibleSchedule result = getVisibleSchedule(visitor, owner, windowBoundaries[0], windowBoundaries[1]);
		return result;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.SchedulingAssistantService#getVisibleSchedule(org.jasig.schedassist.model.IScheduleVisitor, org.jasig.schedassist.model.IScheduleOwner, java.util.Date, java.util.Date)
	 */
	@Override
	public VisibleSchedule getVisibleSchedule(final IScheduleVisitor visitor,
			final IScheduleOwner owner, final Date start, final Date end) {
		Validate.notNull(start, "start parameter cannot be null");
		Validate.notNull(end, "start parameter cannot be null");
		
		Date [] windowBoundaries = calculateOwnerWindowBounds(owner);
		
		Date localStart = start;
		if(start.before(windowBoundaries[0]) || start.after(windowBoundaries[1])) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("ignoring submitted start for getVisibleSchedule: " + start + " (using windowBoundary of " + windowBoundaries[0] + ")");
			}
			localStart = windowBoundaries[0];
		}
		Date localEnd = end;
		if(end.after(windowBoundaries[1])) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("ignoring submitted end for getVisibleSchedule: " + end + " (using windowBoundary of " + windowBoundaries[1] + ")");
			}
			localEnd = windowBoundaries[1];
		}

		Calendar calendar = calendarDao.getCalendar(owner.getCalendarAccount(), localStart, localEnd);
		AvailableSchedule schedule = availableScheduleDao.retrieve(owner);

		VisibleSchedule result = this.visibleScheduleBuilder.calculateVisibleSchedule(
				localStart,
				localEnd,
				calendar, 
				schedule, 
				owner,
				visitor);
		return result;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.SchedulingAssistantService#calculateVisitorConflicts(org.jasig.schedassist.model.IScheduleVisitor, org.jasig.schedassist.model.IScheduleOwner, java.util.Date, java.util.Date)
	 */
	@Override
	public List<AvailableBlock> calculateVisitorConflicts(
			IScheduleVisitor visitor, IScheduleOwner owner, Date start, Date end) {
		
		Date [] windowBoundaries = calculateOwnerWindowBounds(owner);
		
		Date localStart = start;
		if(start.before(windowBoundaries[0])) {
			localStart = windowBoundaries[0];
		}
		Date localEnd = end;
		if(end.after(windowBoundaries[1])) {
			localEnd = windowBoundaries[1];
		}
		
		AvailableSchedule availableSchedule = this.availableScheduleDao.retrieve(owner, localStart, localEnd);
		
		// get the VISITOR's Calendar data
		Calendar calendar = calendarDao.getCalendar(visitor.getCalendarAccount(), localStart, localEnd);
		
		// calculate a VisibleSchedule using the owner's availability but the Visitor's calendar data
		VisibleSchedule result = this.visibleScheduleBuilder.calculateVisitorConflicts(
				availableSchedule.getScheduleStartTime(),
				availableSchedule.getScheduleEndTime(),
				calendar, 
				availableSchedule, 
				owner.getPreferredMeetingDurations(), visitor);
		// return only the conflicts (the busy list)
		List<AvailableBlock> visitorConflicts = result.getBusyList();
		return visitorConflicts;
	}

	
	/**
	 * 
	 * @param owner
	 * @return an array containing 2 {@link Date}s that represent the start and end date/times per the owner's preference
	 */
	protected Date[] calculateOwnerWindowBounds(IScheduleOwner owner) {
		VisibleWindow window = owner.getPreferredVisibleWindow();

		Date now = new Date();
		Date startTime = DateUtils.addHours(now, window.getWindowHoursStart());
		Date boundary = DateUtils.addWeeks(now, window.getWindowWeeksEnd());
		
		return new Date[] { startTime, boundary };
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.SchedulingAssistantService#cancelAppointment(org.jasig.schedassist.model.IScheduleVisitor, org.jasig.schedassist.model.IScheduleOwner, net.fortuna.ical4j.model.component.VEvent, org.jasig.schedassist.model.AvailableBlock, java.lang.String)
	 */
	@Override
	public void cancelAppointment(IScheduleVisitor visitor, IScheduleOwner owner, VEvent event, AvailableBlock block, final String cancelReason) throws SchedulingException {
		if(owner.isSamePerson(visitor)) {
			LOG.warn("ignoring request to cancelAppointment for owner/visitor same person: " + owner);
			return;
		}
		
		VEvent availableAppointment = calendarDao.getExistingAppointment(owner, block);
		if(null == availableAppointment || this.eventUtils.isAttendingMatch(availableAppointment, visitor, owner)) {
			// if this is a 1.0 appointment (no available version set) or visitor limit is 1
			if(null == availableAppointment.getProperty(AvailableVersion.AVAILABLE_VERSION) || block.getVisitorLimit() == 1) {
				calendarDao.cancelAppointment(visitor, owner, availableAppointment);
				if(null !=  applicationEventPublisher) {
					applicationEventPublisher.publishEvent(new AppointmentCancelledEvent(availableAppointment, owner, visitor, block, cancelReason));
				}
				return;
			} else {
				int currentVisitorCount = this.eventUtils.getScheduleVisitorCount(availableAppointment);
				if(currentVisitorCount == 1) {
					// this attendee is the last one, cancel
					calendarDao.cancelAppointment(visitor, owner, availableAppointment);
				} else {
					// there are other attendees, just leave
					calendarDao.leaveAppointment(visitor, owner, availableAppointment);
				}
				if(null !=  applicationEventPublisher) {
					applicationEventPublisher.publishEvent(new AppointmentLeftEvent(availableAppointment, owner, visitor, block));
				}
				return;
			}
		} else {
			LOG.error("no appointment found within block " + block);
			throw new NoAppointmentExistsException("no matching appointment can be found");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.SchedulingAssistantService#scheduleAppointment(org.jasig.schedassist.model.IScheduleVisitor, org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.AvailableBlock, java.lang.String)
	 */
	@Override
	public VEvent scheduleAppointment(IScheduleVisitor visitor, IScheduleOwner owner, 
			AvailableBlock block, String eventDescription) throws SchedulingException {
		if(owner.isSamePerson(visitor)) {
			LOG.warn("ignoring request to scheduleAppointment for owner/visitor same person: " + owner);
			return null;
		}
		
		// assert the requested block is within the owner's current schedule
		AvailableBlock ownerPersistedBlock = availableScheduleDao.retrieveTargetBlock(owner, block.getStartTime());
		if(null == ownerPersistedBlock) {
			throw new SchedulingException("requested time is not available in schedule: " + block);
		}

		if(ownerPersistedBlock.getVisitorLimit() == 1) {
			// check to see if there is a conflict
			calendarDao.checkForConflicts(owner, block);
			// no conflicts, create the appointment
			VEvent event = calendarDao.createAppointment(visitor, owner, block, eventDescription);
			if(null !=  applicationEventPublisher) {
				applicationEventPublisher.publishEvent(new AppointmentCreatedEvent(event, owner, visitor, block, eventDescription));
			}
			return event;
		} else {
			// owner supports multiple visitors
			// look for an existing appointment
			VEvent existingAppointment = calendarDao.getExistingAppointment(owner, block);
			if(null == existingAppointment) {
				// check to see if there is a conflict
				calendarDao.checkForConflicts(owner, block);
				// lets create it
				VEvent event = calendarDao.createAppointment(visitor, owner, block, eventDescription);
				if(null !=  applicationEventPublisher) {
					applicationEventPublisher.publishEvent(new AppointmentJoinedEvent(event, owner, visitor, block));
				}
				return event;
			} else {
				// try to join if attendee count hasn't been exceeded
				int visitorCount = this.eventUtils.getScheduleVisitorCount(existingAppointment);
				if(visitorCount < ownerPersistedBlock.getVisitorLimit()) {
					// join!
					VEvent event = calendarDao.joinAppointment(visitor, owner, existingAppointment);
					if(null !=  applicationEventPublisher) {
						applicationEventPublisher.publishEvent(new AppointmentJoinedEvent(event, owner, visitor, block));
					}
					return event;
				} else {
					// visitor limit exceeded
					throw new SchedulingException("visitor limit for this appointment has been met");
				}
			}
		}
	}
	
	
}
