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

/**
 * 
 */
package org.jasig.schedassist.impl.caldav;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;

import org.apache.commons.lang.Validate;
import org.jasig.schedassist.IAffiliationSource;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.DefaultEventUtilsImpl;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * Subclass of {@link DefaultEventUtilsImpl} specific for 
 * caldav.
 * 
 * The explicitSetTimeZone field is of significance. If set to true, one must also set
 * the timeZone property. The {@link InitializingBean#afterPropertiesSet()} implementation
 * on this class should be called to verify state (this is automatically called by the Spring IoC
 * container; only call this method explicitly specifically if not being constructed by Spring).
 * 
 * When true and the timezone can be resolved, the behavior of {@link #wrapEventInCalendar(VEvent)} and
 * {@link #constructAvailableAppointment(AvailableBlock, IScheduleOwner, IScheduleVisitor, String)} will change.
 * 
 * The explicitSetTimeZone and timeZone properties are encouraged when integrating with an Oracle Communications Suite 
 * environment.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 * @version $Id: CaldavEventUtilsImpl.java 51 2011-05-06 14:35:33Z nblair $
 */
public class CaldavEventUtilsImpl extends DefaultEventUtilsImpl implements InitializingBean {

	private boolean explicitSetTimeZone = false;
	private String timeZone;
	private TimeZone _timeZone;
	/**
	 * 
	 * @param affiliationSource
	 */
	public CaldavEventUtilsImpl(IAffiliationSource affiliationSource) {
		super(affiliationSource);
	}
	/**
	 * @return the explicitSetTimeZone
	 */
	public boolean isExplicitSetTimeZone() {
		return explicitSetTimeZone;
	}
	/**
	 * @param explicitSetTimeZone the explicitSetTimeZone to set
	 */
	@Value("${caldav.explicitSetTimeZone:false}")
	public void setExplicitSetTimeZone(boolean explicitSetTimeZone) {
		this.explicitSetTimeZone = explicitSetTimeZone;
	}
	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}
	/**
	 * @param timeZone the timeZone to set
	 */
	@Value("${caldav.systemTimeZone:America/Chicago}")
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if(isExplicitSetTimeZone()) {
			Validate.notEmpty(this.timeZone, "timeZone field cannot be empty if explicitSetTimeZone is true");
			TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
			_timeZone = registry.getTimeZone(this.timeZone);
			if(null == _timeZone) {
				throw new IllegalStateException("no timezone found for " + timeZone);
			}
		}
	}
	/**
	 * Calls the super implementation, and adds an {@link Organizer} and an {@link Uid}.
	 * If the explicitSetTimeZone field is true and the corresponding timeZone can be resolved, 
	 * the {@link DtStart} and {@link DtEnd} are modified to include the corresponding TZID parameter.
	 * 
	 * @see #constructOrganizer(ICalendarAccount)
	 * @see #generateNewUid()
	 *  (non-Javadoc)
	 * @see org.jasig.schedassist.model.DefaultEventUtilsImpl#constructAvailableAppointment(org.jasig.schedassist.model.AvailableBlock, org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.IScheduleVisitor, java.lang.String)
	 */
	@Override
	public VEvent constructAvailableAppointment(AvailableBlock block,
			IScheduleOwner owner, IScheduleVisitor visitor,
			String eventDescription) {
		VEvent event = super.constructAvailableAppointment(block, owner, visitor,
				eventDescription);
		if(isExplicitSetTimeZone() && _timeZone != null) {
			DtStart start = event.getStartDate();
			start.setTimeZone(_timeZone);
			
			DtEnd end = event.getEndDate();
			end.setTimeZone(_timeZone);
		}
		return event;
	}
	
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.DefaultEventUtilsImpl#willEventCauseConflict(org.jasig.schedassist.model.ICalendarAccount, net.fortuna.ical4j.model.component.VEvent)
	 */
	@Override
	public boolean willEventCauseConflict(ICalendarAccount calendarAccount,
			VEvent event) {
		if(event == null) {
			return false;
		}
		
		Status status = event.getStatus();
		if(status != null && Status.VEVENT_CANCELLED.equals(status)) {
			return false;
		}
		
		Transp transp = event.getTransparency();
		if(null == transp) {
			return true;
		} else {
			return Transp.OPAQUE.equals(transp);
		}	
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.DefaultEventUtilsImpl#constructVisitorAttendee(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public Attendee constructVisitorAttendee(
			ICalendarAccount calendarAccount) {
		Attendee attendee = super.constructVisitorAttendee(calendarAccount);
		attendee.getParameters().add(Role.REQ_PARTICIPANT);
		return attendee;
	}
	
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.DefaultEventUtilsImpl#convertBlockToReflectionEvent(org.jasig.schedassist.model.AvailableBlock)
	 */
	@Override
	protected VEvent convertBlockToReflectionEvent(AvailableBlock block) {
		VEvent reflection = super.convertBlockToReflectionEvent(block);
		reflection.getProperties().add(this.generateNewUid());
		return reflection;
	}
	/**
	 * If the explicitSetTimeZone field is true and the corresponding timeZone can be resolved, 
	 * the corresponding {@link VTimeZone} is added to the returned {@link Calendar}.
	 * 
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.DefaultEventUtilsImpl#wrapEventInCalendar(net.fortuna.ical4j.model.component.VEvent)
	 */
	@Override
	public Calendar wrapEventInCalendar(VEvent event) {
		Calendar calendar = super.wrapEventInCalendar(event);
		if(isExplicitSetTimeZone() && this._timeZone != null) {
			calendar.getComponents().add(this._timeZone.getVTimeZone());
		}
		
		return calendar;
	}

}
