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

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.Status;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.NullAffiliationSourceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Test harness for {@link OracleEventUtilsImpl}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DefaultEventUtilsImplTest.java 2828 2010-11-01 22:18:51Z npblair $
 */
public class DefaultEventUtilsImplTest {

	static {
		// Many of the values in the assertions expect America/Chicago
		TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"));
	}
	
	private DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
	/**
	 * TODO depends on VM's timezone
	 * @throws Exception
	 */
	@Test
	public void testConvertToICalendarFormatControl() throws Exception {
		String output = DefaultEventUtilsImpl.convertToICalendarFormat(makeDateTime("20091006-1243"));
		Assert.assertEquals("20091006T174300Z", output);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvertToICalendarFormatInvalid() throws Exception {
		try {
			DefaultEventUtilsImpl.convertToICalendarFormat(null);
			Assert.fail("expected IllegalArgumentException not thrown");
		} catch (IllegalArgumentException e) {
			// success
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEmailToURIControl() throws Exception {
		URI result = DefaultEventUtilsImpl.emailToURI("user@host.com");
		Assert.assertEquals("mailto:user@host.com", result.toString());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEmailToURIInvalid() throws Exception {
		try {
			DefaultEventUtilsImpl.emailToURI(null);
			Assert.fail("expected IllegalArgumentException not thrown");
		} catch (IllegalArgumentException e) {
			// success
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConstructOrganizerOwnerControl() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("someowner@wisc.edu");
		person.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person, 1);
		
		Organizer organizer = this.eventUtils.constructOrganizer(owner.getCalendarAccount());
		AppointmentRole role = (AppointmentRole) organizer.getParameter(AppointmentRole.APPOINTMENT_ROLE);
		Assert.assertEquals(AppointmentRole.OWNER, role);
		Assert.assertEquals("mailto:someowner@wisc.edu", organizer.getValue());
		Assert.assertEquals("Some Owner", organizer.getParameter("CN").getValue());
		
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConstructAttendeeVisitorControl() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("somevisitor@wisc.edu");
		person.setDisplayName("Some Visitor");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person);
		
		
		Attendee attendee = this.eventUtils.constructSchedulingAssistantAttendee(visitor.getCalendarAccount(), AppointmentRole.VISITOR);
		Assert.assertEquals(PartStat.ACCEPTED, attendee.getParameter(PartStat.PARTSTAT));
		Assert.assertEquals(Rsvp.FALSE, attendee.getParameter(Rsvp.RSVP));
		AppointmentRole role = (AppointmentRole) attendee.getParameter(AppointmentRole.APPOINTMENT_ROLE);
		Assert.assertEquals(AppointmentRole.VISITOR, role);
		Assert.assertEquals("mailto:somevisitor@wisc.edu", attendee.getValue());
		Assert.assertEquals("Some Visitor", attendee.getParameter("CN").getValue());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConstructAttendeeVisitorDelegateAccount() throws Exception {
		MockDelegateCalendarAccount person = new MockDelegateCalendarAccount();
		person.setEmailAddress("somevisitor@wisc.edu");
		person.setDisplayName("Some Visitor");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person);
		
		
		Attendee attendee = this.eventUtils.constructSchedulingAssistantAttendee(visitor.getCalendarAccount(), AppointmentRole.VISITOR);
		Assert.assertEquals(PartStat.ACCEPTED, attendee.getParameter(PartStat.PARTSTAT));
		Assert.assertEquals(Rsvp.FALSE, attendee.getParameter(Rsvp.RSVP));
		AppointmentRole role = (AppointmentRole) attendee.getParameter(AppointmentRole.APPOINTMENT_ROLE);
		Assert.assertEquals(AppointmentRole.VISITOR, role);
		Assert.assertEquals("mailto:somevisitor@wisc.edu", attendee.getValue());
		Assert.assertEquals("Some Visitor", attendee.getParameter("CN").getValue());
	}
	
	@Test
	public void testAttendeeMatchesPersonInvalids() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("somevisitor@wisc.edu");
		person.setDisplayName("Some Visitor");
		// test null Property
		Assert.assertFalse(this.eventUtils.attendeeMatchesPerson(null, person));
		
		// test non attendee property
		net.fortuna.ical4j.model.property.Location location = new net.fortuna.ical4j.model.property.Location("some office");
		Assert.assertFalse(this.eventUtils.attendeeMatchesPerson(location, person));
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAttendingMatchesPerson() throws Exception {
		// construct visitor
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("somevisitor@wisc.edu");
		person.setDisplayName("Some Visitor");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person);
		
		// construct owner
		MockCalendarAccount person2 = new MockCalendarAccount();
		person2.setEmailAddress("someowner@wisc.edu");
		person2.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person2, 1);
		
		Attendee visitorAttendee = this.eventUtils.constructSchedulingAssistantAttendee(visitor.getCalendarAccount(), AppointmentRole.VISITOR);
		Assert.assertTrue(this.eventUtils.attendeeMatchesPerson(visitorAttendee, visitor.getCalendarAccount()));
		Assert.assertFalse(this.eventUtils.attendeeMatchesPerson(visitorAttendee, owner.getCalendarAccount()));
		
		Organizer ownerAttendee = this.eventUtils.constructOrganizer(owner.getCalendarAccount());
		Assert.assertFalse(this.eventUtils.attendeeMatchesPerson(ownerAttendee, visitor.getCalendarAccount()));
		Assert.assertTrue(this.eventUtils.attendeeMatchesPerson(ownerAttendee, owner.getCalendarAccount()));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConstructAvailableAppointmentControl() throws Exception {
		// construct visitor
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("somevisitor@wisc.edu");
		person.setDisplayName("Some Visitor");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person);
		
		// construct owner
		MockCalendarAccount person2 = new MockCalendarAccount();
		person2.setEmailAddress("someowner@wisc.edu");
		person2.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person2, 1);
		owner.setPreference(Preferences.LOCATION, "Owner's office");
		
		AvailableBlock block = AvailableBlockBuilder.createBlock(makeDateTime("20091006-1300"), makeDateTime("20091006-1330"));
		
		VEvent availableAppointment = this.eventUtils.constructAvailableAppointment(
				block,
				owner, 
				visitor, 
				"test event description");
		
		Assert.assertEquals("Appointment with Some Visitor", availableAppointment.getSummary().getValue());
		Assert.assertEquals("test event description", availableAppointment.getDescription().getValue());
		Assert.assertEquals("Owner's office", availableAppointment.getLocation().getValue());
		Assert.assertEquals(makeDateTime("20091006-1300"), availableAppointment.getStartDate().getDate());
		Assert.assertEquals(makeDateTime("20091006-1330"), availableAppointment.getEndDate().getDate());
		Assert.assertEquals("TRUE", availableAppointment.getProperty(SchedulingAssistantAppointment.AVAILABLE_APPOINTMENT).getValue());
		Assert.assertEquals("1", availableAppointment.getProperty(VisitorLimit.VISITOR_LIMIT).getValue());
		Assert.assertEquals(Status.VEVENT_CONFIRMED, availableAppointment.getProperty(Status.STATUS));
		Assert.assertEquals(Clazz.CONFIDENTIAL, availableAppointment.getClassification());
		PropertyList attendeePropertyList = availableAppointment.getProperties(Attendee.ATTENDEE);
		for(Object o : attendeePropertyList) {
			Property attendee = (Property) o;
			Assert.assertEquals(PartStat.ACCEPTED, attendee.getParameter(PartStat.PARTSTAT));
			Assert.assertEquals(Rsvp.FALSE, attendee.getParameter(Rsvp.RSVP));
			Parameter appointmentRole = attendee.getParameter(AppointmentRole.APPOINTMENT_ROLE);
			if("VISITOR".equals(appointmentRole.getValue())) {
				Assert.assertEquals("mailto:somevisitor@wisc.edu", attendee.getValue());
				Assert.assertEquals("Some Visitor", attendee.getParameter("CN").getValue());
			} else if ("OWNER".equals(appointmentRole.getValue())) {
				Assert.assertEquals("mailto:someowner@wisc.edu", attendee.getValue());
				Assert.assertEquals("Some Owner", attendee.getParameter("CN").getValue());
			} else {
				Assert.fail("unexpected value for appointment role: " + appointmentRole.getValue());
			}
			
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConstructAvailableAppointmentOwnerIsResource() throws Exception {
		// construct visitor
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("somevisitor@wisc.edu");
		person.setDisplayName("Some Visitor");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person);
		
		// construct owner
		MockDelegateCalendarAccount person2 = new MockDelegateCalendarAccount();
		person2.setEmailAddress("someowner@wisc.edu");
		person2.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person2, 1);
		owner.setPreference(Preferences.LOCATION, "Owner's office");
		
		AvailableBlock block = AvailableBlockBuilder.createBlock(makeDateTime("20091006-1300"), makeDateTime("20091006-1330"));
		
		VEvent availableAppointment = this.eventUtils.constructAvailableAppointment(
				block,
				owner, 
				visitor, 
				"test event description");
		
		Assert.assertEquals("Appointment with Some Visitor", availableAppointment.getSummary().getValue());
		Assert.assertEquals("test event description", availableAppointment.getDescription().getValue());
		Assert.assertEquals("Owner's office", availableAppointment.getLocation().getValue());
		Assert.assertEquals(makeDateTime("20091006-1300"), availableAppointment.getStartDate().getDate());
		Assert.assertEquals(makeDateTime("20091006-1330"), availableAppointment.getEndDate().getDate());
		Assert.assertEquals("TRUE", availableAppointment.getProperty(SchedulingAssistantAppointment.AVAILABLE_APPOINTMENT).getValue());
		Assert.assertEquals("1", availableAppointment.getProperty(VisitorLimit.VISITOR_LIMIT).getValue());
		Assert.assertEquals(Status.VEVENT_CONFIRMED, availableAppointment.getProperty(Status.STATUS));
		Assert.assertEquals(Clazz.PUBLIC, availableAppointment.getClassification());
		PropertyList attendeePropertyList = availableAppointment.getProperties(Attendee.ATTENDEE);
		for(Object o : attendeePropertyList) {
			Property attendee = (Property) o;
			Assert.assertEquals(PartStat.ACCEPTED, attendee.getParameter(PartStat.PARTSTAT));
			Assert.assertEquals(Rsvp.FALSE, attendee.getParameter(Rsvp.RSVP));
			Parameter appointmentRole = attendee.getParameter(AppointmentRole.APPOINTMENT_ROLE);
			if("VISITOR".equals(appointmentRole.getValue())) {
				Assert.assertEquals("mailto:somevisitor@wisc.edu", attendee.getValue());
				Assert.assertEquals("Some Visitor", attendee.getParameter("CN").getValue());
			} else if ("OWNER".equals(appointmentRole.getValue())) {
				Assert.assertEquals("mailto:someowner@wisc.edu", attendee.getValue());
				Assert.assertEquals("Some Owner", attendee.getParameter("CN").getValue());
			} else {
				Assert.fail("unexpected value for appointment role: " + appointmentRole.getValue());
			}
			
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConstructAvailableAppointmentBlockOverridesPreferredLocation() throws Exception {
		// construct visitor
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("somevisitor@wisc.edu");
		person.setDisplayName("Some Visitor");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person);
		
		// construct owner
		MockCalendarAccount person2 = new MockCalendarAccount();
		person2.setEmailAddress("someowner@wisc.edu");
		person2.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person2, 1);
		owner.setPreference(Preferences.LOCATION, "Owner's office");
		
		AvailableBlock block = AvailableBlockBuilder.createBlock(makeDateTime("20091006-1300"), makeDateTime("20091006-1330"), 1, "alternate location");
		
		VEvent availableAppointment = this.eventUtils.constructAvailableAppointment(
				block,
				owner, 
				visitor, 
				"test event description");
		
		Assert.assertEquals("Appointment with Some Visitor", availableAppointment.getSummary().getValue());
		Assert.assertEquals("test event description", availableAppointment.getDescription().getValue());
		Assert.assertEquals("alternate location", availableAppointment.getLocation().getValue());
		Assert.assertEquals(makeDateTime("20091006-1300"), availableAppointment.getStartDate().getDate());
		Assert.assertEquals(makeDateTime("20091006-1330"), availableAppointment.getEndDate().getDate());
		Assert.assertEquals("TRUE", availableAppointment.getProperty(SchedulingAssistantAppointment.AVAILABLE_APPOINTMENT).getValue());
		Assert.assertEquals("1", availableAppointment.getProperty(VisitorLimit.VISITOR_LIMIT).getValue());
		Assert.assertEquals(Status.VEVENT_CONFIRMED, availableAppointment.getProperty(Status.STATUS));
		PropertyList attendeePropertyList = availableAppointment.getProperties(Attendee.ATTENDEE);
		for(Object o : attendeePropertyList) {
			Property attendee = (Property) o;
			Assert.assertEquals(PartStat.ACCEPTED, attendee.getParameter(PartStat.PARTSTAT));
			Assert.assertEquals(Rsvp.FALSE, attendee.getParameter(Rsvp.RSVP));
			Parameter appointmentRole = attendee.getParameter(AppointmentRole.APPOINTMENT_ROLE);
			if("VISITOR".equals(appointmentRole.getValue())) {
				Assert.assertEquals("mailto:somevisitor@wisc.edu", attendee.getValue());
				Assert.assertEquals("Some Visitor", attendee.getParameter("CN").getValue());
			} else if ("OWNER".equals(appointmentRole.getValue())) {
				Assert.assertEquals("mailto:someowner@wisc.edu", attendee.getValue());
				Assert.assertEquals("Some Owner", attendee.getParameter("CN").getValue());
			} else {
				Assert.fail("unexpected value for appointment role: " + appointmentRole.getValue());
			}
			
		}
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConstructAvailableAppointmentVisitorIsStudentOwnerNotAdvisor() throws Exception {
		// construct visitor
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("somevisitor@wisc.edu");
		person.setDisplayName("Some Visitor");
		person.setAttributeValue("wiscedustudentid", "studentidnumber");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person);
		
		// construct owner
		MockCalendarAccount person2 = new MockCalendarAccount();
		person2.setEmailAddress("someowner@wisc.edu");
		person2.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person2, 1);
		owner.setPreference(Preferences.LOCATION, "Owner's office");
		
		VEvent availableAppointment = this.eventUtils.constructAvailableAppointment(
				AvailableBlockBuilder.createBlock(makeDateTime("20091006-1300"), makeDateTime("20091006-1330")),
				owner, 
				visitor, 
				"test event description");
		
		Assert.assertEquals("Appointment with Some Visitor", availableAppointment.getSummary().getValue());
		Assert.assertEquals("test event description", availableAppointment.getDescription().getValue());
		Assert.assertEquals("Owner's office", availableAppointment.getLocation().getValue());
		Assert.assertEquals(makeDateTime("20091006-1300"), availableAppointment.getStartDate().getDate());
		Assert.assertEquals(makeDateTime("20091006-1330"), availableAppointment.getEndDate().getDate());
		Assert.assertEquals("TRUE", availableAppointment.getProperty(SchedulingAssistantAppointment.AVAILABLE_APPOINTMENT).getValue());
		Assert.assertEquals("1", availableAppointment.getProperty(VisitorLimit.VISITOR_LIMIT).getValue());
		Assert.assertEquals(Status.VEVENT_CONFIRMED, availableAppointment.getProperty(Status.STATUS));
		PropertyList attendeePropertyList = availableAppointment.getProperties(Attendee.ATTENDEE);
		for(Object o : attendeePropertyList) {
			Property attendee = (Property) o;
			Assert.assertEquals(PartStat.ACCEPTED, attendee.getParameter(PartStat.PARTSTAT));
			Assert.assertEquals(Rsvp.FALSE, attendee.getParameter(Rsvp.RSVP));
			Parameter appointmentRole = attendee.getParameter(AppointmentRole.APPOINTMENT_ROLE);
			if("VISITOR".equals(appointmentRole.getValue())) {
				Assert.assertEquals("mailto:somevisitor@wisc.edu", attendee.getValue());
				Assert.assertEquals("Some Visitor", attendee.getParameter("CN").getValue());
			} else if ("OWNER".equals(appointmentRole.getValue())) {
				Assert.assertEquals("mailto:someowner@wisc.edu", attendee.getValue());
				Assert.assertEquals("Some Owner", attendee.getParameter("CN").getValue());
			} else {
				Assert.fail("unexpected value for appointment role: " + appointmentRole.getValue());
			}
		}
	}
	
	/**
	 * Call {@link OracleEventUtilsImpl#getAttendeeForUserFromEvent(VEvent, CalendarUser)} on an 
	 * event with 0 {@link Attendee}s, assert null return.
	 * @throws Exception
	 */
	@Test
	public void testGetAttendeeForUserFromEventNoAttendees() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("someowner@wisc.edu");
		person.setDisplayName("Some Owner");
		
		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1000")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1030")),
				"some appointment");
		
		Assert.assertNull(this.eventUtils.getAttendeeForUserFromEvent(someEvent, person));
	}
	
	/**
	 * Call {@link OracleEventUtilsImpl#getAttendeeForUserFromEvent(VEvent, CalendarUser)} on an 
	 * event with 0 {@link Attendee}s, assert null return.
	 * @throws Exception
	 */
	@Test
	public void testGetAttendeeForUserFromEventControl() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("someowner@wisc.edu");
		person.setDisplayName("Some Owner");
		
		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1000")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1030")),
				"some appointment");
		Attendee attendee = new Attendee();
		attendee.setValue("mailto:someowner@wisc.edu");
		attendee.getParameters().add(new Cn("Some Owner"));
		
		Attendee attendee2 = new Attendee();
		attendee2.setValue("mailto:person2@domain.edu");
		attendee2.getParameters().add(new Cn("Other Person"));
		
		someEvent.getProperties().add(attendee);
		someEvent.getProperties().add(attendee2);
		
		Property result = this.eventUtils.getAttendeeForUserFromEvent(someEvent, person);
		Assert.assertNotNull(result);
		Assert.assertEquals("mailto:someowner@wisc.edu", result.getValue());
		Assert.assertEquals("Some Owner", result.getParameter(Cn.CN).getValue());
		
	}
	
	/**
	 * Call {@link OracleEventUtilsImpl#getScheduleVisitorCount(someEvent)} on an 
	 * event with 0 {@link Attendee}s, assert 0 return.
	 * @throws Exception
	 */
	@Test
	public void testGetAvailableVisitorCountFromEventNoAttendees() throws Exception {
		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1000")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1030")),
				"some appointment");
		
		Assert.assertEquals(0, this.eventUtils.getScheduleVisitorCount(someEvent));
	}
	
	/**
	 * Call {@link OracleEventUtilsImpl#getScheduleVisitorCount(someEvent)} on an 
	 * event with 2 {@link Attendee}s (one owner, one visitor), assert return 1.
	 * @throws Exception
	 */
	@Test
	public void testGetAvailableVisitorCountFromEventControl() throws Exception {
		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1000")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1030")),
				"some appointment");
		
		Attendee owner = new Attendee();
		owner.setValue("mailto:person@domain.edu");
		owner.getParameters().add(new Cn("OWNER SURNAME"));
		owner.getParameters().add(AppointmentRole.OWNER);
		
		Attendee visitor = new Attendee();
		visitor.setValue("mailto:visitor@domain.edu");
		visitor.getParameters().add(new Cn("VISITOR SURNAME"));
		visitor.getParameters().add(AppointmentRole.VISITOR);
		
		someEvent.getProperties().add(owner);
		someEvent.getProperties().add(visitor);
		
		Assert.assertEquals(1, this.eventUtils.getScheduleVisitorCount(someEvent));
		Assert.assertEquals(2, someEvent.getProperties(Attendee.ATTENDEE).size());
	}
	
	/**
	 * Call {@link OracleEventUtilsImpl#getScheduleVisitorCount(someEvent)} on an 
	 * event with 41 {@link Attendee}s (one owner, 40 visitors), assert return 40.
	 * @throws Exception
	 */
	@Test
	public void testGetAvailableVisitorCountFromEventLarge() throws Exception {
		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1000")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1030")),
				"some appointment");
		
		Attendee owner = new Attendee();
		owner.setValue("mailto:person@domain.edu");
		owner.getParameters().add(new Cn("OWNER SURNAME"));
		owner.getParameters().add(AppointmentRole.OWNER);
		someEvent.getProperties().add(owner);
		
		for(int i = 1; i <= 40; i++) {
			Attendee visitor = new Attendee();
			visitor.setValue("mailto:visitor" + i +"@domain.edu");
			visitor.getParameters().add(new Cn("VISITOR SURNAME"+i));
			visitor.getParameters().add(AppointmentRole.VISITOR);
			someEvent.getProperties().add(visitor);
		}
		Assert.assertEquals(40, this.eventUtils.getScheduleVisitorCount(someEvent));
		Assert.assertEquals(41,  someEvent.getProperties(Attendee.ATTENDEE).size());
	}
	
	/**
	 * Call {@link OracleEventUtilsImpl#getScheduleVisitorCount(someEvent)} on an 
	 * event with 41 {@link Attendee}s (one owner, 40 visitors). 
	 * 20 of the visitors will have PartStat set to DECLINED.
	 * 
	 * Assert return value of 20.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetAvailableVisitorCountFromEventPartStat() throws Exception {
		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1000")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-1030")),
				"some appointment");
		
		Attendee owner = new Attendee();
		owner.setValue("mailto:person@domain.edu");
		owner.getParameters().add(new Cn("OWNER SURNAME"));
		owner.getParameters().add(AppointmentRole.OWNER);
		someEvent.getProperties().add(owner);
		
		for(int i = 1; i <= 40; i++) {
			Attendee visitor = new Attendee();
			visitor.setValue("mailto:visitor" + i +"@domain.edu");
			visitor.getParameters().add(new Cn("VISITOR SURNAME"+i));
			visitor.getParameters().add(AppointmentRole.VISITOR);
			if(i % 2 == 0) {
				visitor.getParameters().add(PartStat.DECLINED);
			}
			someEvent.getProperties().add(visitor);
		}
		Assert.assertEquals(40, this.eventUtils.getScheduleVisitorCount(someEvent));
		Assert.assertEquals(41,  someEvent.getProperties(Attendee.ATTENDEE).size());
	}
	

	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWillCauseConflictControl() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("someowner@wisc.edu");
		person.setDisplayName("Some Owner");
		
		VEvent event = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20100405-1000")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20100405-1030")),
				"some conflicting appointment");
		Attendee attendee = new Attendee();
		attendee.setValue("mailto:someowner@wisc.edu");
		attendee.getParameters().add(new Cn("Some Owner"));
		attendee.getParameters().add(PartStat.ACCEPTED);
		
		Attendee attendee2 = new Attendee();
		attendee2.setValue("mailto:person2@domain.edu");
		attendee2.getParameters().add(new Cn("Other Person"));
		
		event.getProperties().add(attendee);
		event.getProperties().add(attendee2);
		
		Assert.assertTrue(this.eventUtils.willEventCauseConflict(person, event));
	}

	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWillCauseConflictPartStatNeedsAction() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("someowner@wisc.edu");
		person.setDisplayName("Some Owner");
		
		VEvent event = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20100405-1000")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20100405-1030")),
		"some conflicting appointment");
		Attendee attendee = new Attendee();
		attendee.setValue("mailto:someowner@domain.edu");
		attendee.getParameters().add(PartStat.NEEDS_ACTION);
		attendee.getParameters().add(new Cn("Some Owner"));
		
		Attendee attendee2 = new Attendee();
		attendee2.setValue("mailto:person2@domain.edu");
		attendee2.getParameters().add(new Cn("Other Person"));
		
		event.getProperties().add(attendee);
		event.getProperties().add(attendee2);
		
		Assert.assertFalse(this.eventUtils.willEventCauseConflict(person, event));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIsAttendingMatch() throws Exception {
		MockCalendarAccount person1 = new MockCalendarAccount();
		person1.setEmailAddress("someperson1@wisc.edu");
		person1.setDisplayName("Some Person");
		MockScheduleOwner owner = new MockScheduleOwner(person1, 1);
		MockScheduleVisitor visitor1 = new MockScheduleVisitor(person1);
		
		MockCalendarAccount person2 = new MockCalendarAccount();
		person2.setEmailAddress("someperson2@wisc.edu");
		person2.setDisplayName("Some Person2");
		MockScheduleVisitor visitor2 = new MockScheduleVisitor(person2);
		
		MockCalendarAccount person3 = new MockCalendarAccount();
		person3.setEmailAddress("someperson3@wisc.edu");
		person3.setDisplayName("Other Person3");
		MockScheduleVisitor visitor3 = new MockScheduleVisitor(person3);


		// test same person first
		VEvent event = this.eventUtils.constructAvailableAppointment(
				AvailableBlockBuilder.createBlock("20091006-1200", "20091006-1300"),
				owner, 
				visitor1, 
		"test same person");
		Assert.assertTrue(this.eventUtils.isAttendingMatch(event, visitor1, owner));
		Assert.assertFalse(this.eventUtils.isAttendingMatch(event, visitor2, owner));
		Assert.assertFalse(this.eventUtils.isAttendingMatch(event, visitor3, owner));

		// test 2 different people
		event = this.eventUtils.constructAvailableAppointment(
				AvailableBlockBuilder.createBlock("20091006-1200", "20091006-1300"), 
				owner, 
				visitor2, 
		"test 2 different people");
		Assert.assertTrue(this.eventUtils.isAttendingMatch(event, visitor2, owner));
		Assert.assertFalse(this.eventUtils.isAttendingMatch(event, visitor3, owner));
		Assert.assertFalse(this.eventUtils.isAttendingMatch(event, visitor1, owner));

	}
	
	/**
	 * 
	 * @throws InputFormatException
	 * @throws ParseException 
	 */
	@Test
	public void testConvertBlockToReflectionEvent() throws InputFormatException, ParseException {
		AvailableBlock block = AvailableBlockBuilder.createBlock(makeDateTime("20100812-0900"),
				makeDateTime("20100812-1700"));
		
		VEvent event = this.eventUtils.convertBlockToReflectionEvent(block);
		Assert.assertEquals("Available 9:00 AM - 5:00 PM", event.getSummary().getValue());
		Assert.assertEquals("20100812", event.getStartDate().getValue());
		
		block = AvailableBlockBuilder.createBlock(makeDateTime("20100812-0930"),
				makeDateTime("20100812-1730"));
			
		event = this.eventUtils.convertBlockToReflectionEvent(block);
		Assert.assertEquals("Available 9:30 AM - 5:30 PM", event.getSummary().getValue());
		Assert.assertEquals("20100812", event.getStartDate().getValue());
	}
	
	@Test
	public void testScheduleForReflection() throws InputFormatException, ParseException {
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks(
				"9:00 AM", "3:00 PM", "MWF", 
				makeDateTime("20100808-0000"), 
				makeDateTime("20100814-0000"));
		
		// expand to 30 minute blocks first to verify they are combined properly
		blocks = AvailableBlockBuilder.expand(blocks, 30);
		List<net.fortuna.ical4j.model.Calendar> calendars = this.eventUtils.convertScheduleForReflection(new AvailableSchedule(blocks));
		
		Assert.assertEquals(1, calendars.size());
		net.fortuna.ical4j.model.Calendar calendar = calendars.get(0);
		Assert.assertEquals(1, calendar.getComponents().size());
		ComponentList components = calendar.getComponents(VEvent.VEVENT);
		for(Object o : components) {
			VEvent event = (VEvent) o;
			Assert.assertEquals("Available 9:00 AM - 3:00 PM", event.getSummary().getValue());
			Assert.assertEquals(AvailabilityReflection.TRUE, event.getProperty(AvailabilityReflection.AVAILABILITY_REFLECTION));
			Assert.assertEquals(2, event.getProperties(RDate.RDATE).size());
		}
	}
	
	@Test
	public void testGetAttendeeListFromEventNull() {
		PropertyList result = this.eventUtils.getAttendeeListFromEvent(null);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}
	@Test
	public void testGetAttendeeListFromEventNotAvailable() throws ParseException {
		VEvent event = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20100816-1200")), "test");
		PropertyList result = this.eventUtils.getAttendeeListFromEvent(event);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}
	
	@Test
	public void testGetAttendeeListFromEventControl() throws InputFormatException {
		// construct visitor
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("somevisitor@wisc.edu");
		person.setDisplayName("Some Visitor");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person);
		
		// construct owner
		MockCalendarAccount person2 = new MockCalendarAccount();
		person2.setEmailAddress("someowner@wisc.edu");
		person2.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person2, 1);
		owner.setPreference(Preferences.LOCATION, "Owner's office");
		
		VEvent availableAppointment = this.eventUtils.constructAvailableAppointment(
				AvailableBlockBuilder.createBlock("20091006-1300", "20091006-1330"),
				owner, 
				visitor, 
				"test event description");
		
		PropertyList attendeeList = this.eventUtils.getAttendeeListFromEvent(availableAppointment);
		Assert.assertNotNull(attendeeList);
		Assert.assertEquals(2, attendeeList.size());
		for(Iterator<?> i = attendeeList.iterator(); i.hasNext(); ) {
			Attendee attendee = (Attendee) i.next();
			if(AppointmentRole.VISITOR.equals(attendee.getParameter(AppointmentRole.APPOINTMENT_ROLE))) {
				Assert.assertEquals("mailto:somevisitor@wisc.edu", attendee.getValue());
			} else if (AppointmentRole.OWNER.equals(attendee.getParameter(AppointmentRole.APPOINTMENT_ROLE))) {
				Assert.assertEquals("mailto:someowner@wisc.edu", attendee.getValue());
			} else {
				Assert.fail("unexpected attendee: " + attendee);
			}
		}
		Property attendee = (Property) attendeeList.get(0);
		Assert.assertEquals("mailto:somevisitor@wisc.edu", attendee.getValue());
		
		Organizer organizer = (Organizer) availableAppointment.getProperty(Organizer.ORGANIZER);
		Assert.assertEquals("mailto:someowner@wisc.edu", organizer.getValue());
		
	}
	/**
	 * Test {@link DefaultEventUtilsImpl#convertScheduleForReflection(AvailableSchedule)} on a 
	 * single block schedule.
	 * 
	 * @throws InputFormatException
	 */
	@Test
	public void testConvertScheduleForReflectionControl() throws InputFormatException {	
		AvailableBlock block = AvailableBlockBuilder.createBlock("20110503-0800", "20110503-0900");
		
		Set<AvailableBlock> availableBlocks = new HashSet<AvailableBlock>();
		availableBlocks.add(block);
		AvailableSchedule availableSchedule = new AvailableSchedule(availableBlocks);
		List<Calendar> calendars = eventUtils.convertScheduleForReflection(availableSchedule);
		Assert.assertEquals(1, calendars.size());
		Calendar calendar = calendars.get(0);
		
		ComponentList components = calendar.getComponents(VEvent.VEVENT);
		Assert.assertEquals(1, components.size());
		for(Object o: components) {
			VEvent event = (VEvent) o;
			Assert.assertEquals("Available 8:00 AM - 9:00 AM", event.getSummary().getValue());
			Assert.assertEquals("20110503", event.getStartDate().getValue());
			Assert.assertTrue(event.getProperties().contains(AvailabilityReflection.TRUE));
		}
	}
	
	/**
	 * Test {@link DefaultEventUtilsImpl#convertScheduleForReflection(AvailableSchedule)} on a 
	 * series of blocks that all have the same time phrase (8:00 AM to 9:00 AM).
	 * 
	 * @throws InputFormatException
	 */
	@Test
	public void testConvertScheduleForReflectionSingleSeries() throws InputFormatException {
		Set<AvailableBlock> availableBlocks = AvailableBlockBuilder.createBlocks("8:00 AM", "9:00 AM", "MWF", 
				CommonDateOperations.parseDatePhrase("20110919"), CommonDateOperations.parseDatePhrase("20111028"));

		AvailableSchedule availableSchedule = new AvailableSchedule(availableBlocks);
		List<Calendar> calendars = eventUtils.convertScheduleForReflection(availableSchedule);
		Assert.assertEquals(1, calendars.size());
		Calendar calendar = calendars.get(0);
		
		ComponentList components = calendar.getComponents(VEvent.VEVENT);
		Assert.assertEquals(1, components.size());
		for(Object o: components) {
			VEvent event = (VEvent) o;
			Assert.assertEquals("Available 8:00 AM - 9:00 AM", event.getSummary().getValue());
			// start date will match first block in the series
			Assert.assertEquals("20110919", event.getStartDate().getValue());
			Assert.assertTrue(event.getProperties().contains(AvailabilityReflection.TRUE));
			PropertyList rDateList = event.getProperties(RDate.RDATE);
			Assert.assertEquals(17, rDateList.size());
		}
	}
	
	/**
	 * Test {@link DefaultEventUtilsImpl#convertScheduleForReflection(AvailableSchedule)} on a schedule
	 * that contains multiple availability series.
	 * 
	 * @throws InputFormatException
	 */
	@Test
	public void testConvertScheduleForReflectionMultipleSeries() throws InputFormatException {
		Set<AvailableBlock> series1 = AvailableBlockBuilder.createBlocks("8:00 AM", "9:00 AM", "MWF", 
				CommonDateOperations.parseDatePhrase("20110919"), CommonDateOperations.parseDatePhrase("20111028"));

		Set<AvailableBlock> series2 = AvailableBlockBuilder.createBlocks("1:00 PM", "3:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20110919"), CommonDateOperations.parseDatePhrase("20111028"));
		
		
		Set<AvailableBlock> series3 = AvailableBlockBuilder.createBlocks("11:00 AM", "2:00 PM", "TR", 
				CommonDateOperations.parseDatePhrase("20110919"), CommonDateOperations.parseDatePhrase("20111028"));
		
		AvailableSchedule availableSchedule = new AvailableSchedule(series1);
		availableSchedule.addAvailableBlocks(series2);
		availableSchedule.addAvailableBlocks(series3);
		
		List<Calendar> calendars = eventUtils.convertScheduleForReflection(availableSchedule);
		Assert.assertEquals(3, calendars.size());
		for(Calendar calendar: calendars) {
			
			ComponentList components = calendar.getComponents(VEvent.VEVENT);
			Assert.assertEquals(1, components.size());
			
			VEvent event = (VEvent) components.get(0);
			if("Available 8:00 AM - 9:00 AM".equals(event.getSummary().getValue())) {
				// start date will match first block in the series
				Assert.assertEquals("20110919", event.getStartDate().getValue());
				Assert.assertTrue(event.getProperties().contains(AvailabilityReflection.TRUE));
				PropertyList rDateList = event.getProperties(RDate.RDATE);
				Assert.assertEquals(17, rDateList.size());
			} else if ("Available 1:00 PM - 3:00 PM".equals(event.getSummary().getValue())) {
				// start date will match first block in the series
				Assert.assertEquals("20110919", event.getStartDate().getValue());
				Assert.assertTrue(event.getProperties().contains(AvailabilityReflection.TRUE));
				PropertyList rDateList = event.getProperties(RDate.RDATE);
				Assert.assertEquals(17, rDateList.size());
			} else if ("Available 11:00 AM - 2:00 PM".equals(event.getSummary().getValue())) {
				// start date will match first block in the series
				Assert.assertEquals("20110920", event.getStartDate().getValue());
				Assert.assertTrue(event.getProperties().contains(AvailabilityReflection.TRUE));
				PropertyList rDateList = event.getProperties(RDate.RDATE);
				Assert.assertEquals(11, rDateList.size());
			} else {
				Assert.fail("unexpected event" + event);
			}
		}
	}
	
	/**
	 * Verify correct {@link Clazz} property set.
	 */
	@Test
	public void testDetermineAppropriateClassProperty() {
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
		
		Assert.assertEquals(Clazz.CONFIDENTIAL, eventUtils.determineAppropriateClassProperty(new MockCalendarAccount()));
		Assert.assertEquals(Clazz.PUBLIC, eventUtils.determineAppropriateClassProperty(new MockDelegateCalendarAccount()));
		
		eventUtils.setEventClassForPersonOwners("SOMETHING-STRANGE");
		Assert.assertEquals(new Clazz("SOMETHING-STRANGE"), eventUtils.determineAppropriateClassProperty(new MockCalendarAccount()));
	}
	
	@Test
	public void testCalculateRecurrenceDayEvent() throws IOException, ParserException, InputFormatException, ParseException {
		Resource resource = new ClassPathResource("org/jasig/schedassist/model/recurring-allDay-event.ics");
	
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(resource.getInputStream());
		
		VEvent event = (VEvent) calendar.getComponent(VEvent.VEVENT);
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
		// should return 1 instance on Oct 5 2012
		PeriodList list = eventUtils.calculateRecurrence(event, CommonDateOperations.parseDatePhrase("20121004"), CommonDateOperations.parseDatePhrase("20121006"));
		Assert.assertEquals(1, list.size());
		
		for(Object o: list) {
			Period p = (Period) o;
			Assert.assertEquals(makeDateTime("20121005-0000").getTime(), p.getRangeStart().getTime());
			Assert.assertEquals(makeDateTime("20121006-0000").getTime(), p.getRangeEnd().getTime());
		}
	}
	
	/**
	 * helper method to create java.util.Date objects from a String
	 * 
	 * @param dateTimePhrase format is "yyyyMMdd-HHmm"
	 * @return
	 * @throws ParseException
	 */
	private Date makeDateTime(String dateTimePhrase) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
		Date time = dateFormat.parse(dateTimePhrase);
		time = DateUtils.truncate(time, java.util.Calendar.SECOND);
		return time;
	}
	
}
