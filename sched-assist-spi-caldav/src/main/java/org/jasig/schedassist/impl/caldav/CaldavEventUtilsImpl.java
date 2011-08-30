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

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.XProperty;

import org.jasig.schedassist.IAffiliationSource;
import org.jasig.schedassist.model.AppointmentRole;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.DefaultEventUtilsImpl;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.stereotype.Component;

/**
 * Subclass of {@link DefaultEventUtilsImpl} specific for 
 * caldav.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 * @version $Id: CaldavEventUtilsImpl.java 51 2011-05-06 14:35:33Z nblair $
 */
@Component
public class CaldavEventUtilsImpl extends DefaultEventUtilsImpl {

	/**
	 * 
	 * @param affiliationSource
	 */
	public CaldavEventUtilsImpl(IAffiliationSource affiliationSource) {
		super(affiliationSource);
	}

	/**
	 * Calls the super implementation, and adds an {@link Organizer} and an {@link Uid}.
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
		event.getProperties().add(constructOrganizer(owner.getCalendarAccount()));
		event.getProperties().add(this.generateNewUid());
		event.getProperties().add(new XProperty("X-BEDEWORK-SUBMITTEDBY", owner.getCalendarAccount().getUsername()));
		return event;
	}

	/**
	 * Construct an {@link Organizer} property for the specified {@link ICalendarAccount}.
	 * 
	 * @param calendarAccount
	 * @return an {@link Organizer} property for the {@link ICalendarAccount}
	 */
	public Organizer constructOrganizer(ICalendarAccount calendarAccount) {
		ParameterList parameterList = new ParameterList();
		parameterList.add(new Cn(calendarAccount.getDisplayName()));
		Organizer organizer = new Organizer(parameterList, emailToURI(calendarAccount.getEmailAddress()));
		return organizer;
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

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.DefaultEventUtilsImpl#constructAvailableAttendee(org.jasig.schedassist.model.ICalendarAccount, org.jasig.schedassist.model.AppointmentRole)
	 */
	@Override
	public Attendee constructAvailableAttendee(
			ICalendarAccount calendarAccount, AppointmentRole role) {
		Attendee attendee = super.constructAvailableAttendee(calendarAccount, role);
		// add iCalendar ROLE parameter to the attendee
		if(AppointmentRole.OWNER.equals(role)) {
			attendee.getParameters().add(Role.CHAIR);
		} else if (AppointmentRole.VISITOR.equals(role)) {
			attendee.getParameters().add(Role.REQ_PARTICIPANT);
		}
		return attendee;
	}

}
