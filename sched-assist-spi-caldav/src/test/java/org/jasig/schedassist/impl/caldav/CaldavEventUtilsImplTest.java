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

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.TzId;

import org.jasig.schedassist.NullAffiliationSourceImpl;
import org.jasig.schedassist.model.AppointmentRole;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.VisitorLimit;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.jasig.schedassist.model.mock.MockScheduleOwner;
import org.jasig.schedassist.model.mock.MockScheduleVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests for {@link CaldavEventUtilsImpl}.
 * 
 * @author Nicholas Blair
 * @version $ Id: CaldavEventUtilsImplTest.java $
 */
public class CaldavEventUtilsImplTest {

	@Test
	public void testEnableExplicitSetTimeZone() {
		CaldavEventUtilsImpl eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
		// pass 1 default (disabled)
		eventUtils.setExplicitSetTimeZone(false);
		
		try {
			eventUtils.afterPropertiesSet();
		} catch (Exception e) {
			fail("afterPropertiesSet threw unexpected exception for default " + e);
		}
		// pass 2 enabled, but forgot timezone
		eventUtils.setExplicitSetTimeZone(true);
		try {
			eventUtils.afterPropertiesSet();
			fail("afterPropertiesSet did not throw expected exception for enabled without timeZone");
		} catch (IllegalArgumentException e) {
			//success 
		} catch (Exception e) {
			fail("afterPropertiesSet threw unexpected exception " + e);
		}
		// pass 3 enabled but invalid timezone
		eventUtils.setTimeZone("invalid");
		try {
			eventUtils.afterPropertiesSet();
		} catch (IllegalStateException e) {
			// success
		} catch (Exception e) {
			fail("afterPropertiesSet threw unexpected exception for enabled with invalid timeZone " + e);
		}
		// pass 4 enabled with valid timezone
		eventUtils.setTimeZone("America/Chicago");
		try {
			eventUtils.afterPropertiesSet();
		} catch (Exception e) {
			fail("afterPropertiesSet threw unexpected exception for enabled with valid timeZone " + e);
		}
	}
	/**
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void testConstructOrganizer() throws URISyntaxException  {
		CaldavEventUtilsImpl eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
		MockCalendarAccount calendarAccount = new MockCalendarAccount();
		calendarAccount.setDisplayName("DISPLAY NAME");
		calendarAccount.setEmailAddress("someone@wherever.org");
		
		Organizer expected = new Organizer("mailto:someone@wherever.org");
		expected.getParameters().add(new Cn("DISPLAY NAME"));
		expected.getParameters().add(AppointmentRole.OWNER);
		
		Organizer generated = eventUtils.constructOrganizer(calendarAccount);
		
		Assert.assertEquals(expected, generated);
	}
	
	/**
	 * Construct an appointment for an "individual" appointment (e.g. 1 visitor).
	 * Compare with 'vevent-examples/example-individual-appointment.ics'.
	 * 
	 * @throws InputFormatException
	 * @throws IOException
	 * @throws ParserException
	 * @throws ParseException 
	 */
	@Test
	public void testConstructIndividualAppointment() throws InputFormatException, IOException, ParserException, ParseException {
		CaldavEventUtilsImpl eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
		eventUtils.setExplicitSetTimeZone(false);
		
		SimpleDateFormat dateFormat = CommonDateOperations.getDateTimeFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
		Date start = dateFormat.parse("20110503-0800");
		Date end = dateFormat.parse("20110503-0900");
		AvailableBlock block = AvailableBlockBuilder.createBlock(start, end);
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setDisplayName("OWNER NAME");
		ownerAccount.setEmailAddress("someone@wherever.org");
		MockScheduleOwner owner = new MockScheduleOwner(ownerAccount, 1L);
		owner.setPreference(Preferences.LOCATION, "123 University Building");
		
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setDisplayName("VISITOR NAME");
		visitorAccount.setEmailAddress("somevisitor@wherever.org");
		MockScheduleVisitor visitor = new MockScheduleVisitor(visitorAccount);
		VEvent generated = eventUtils.constructAvailableAppointment(block, owner, visitor, "test reason.");
		
		
		Resource example = new ClassPathResource("vevent-examples/example-individual-appointment.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar expectedCalendar = builder.build(example.getInputStream());
		VEvent expectedEvent = (VEvent) expectedCalendar.getComponents(VEvent.VEVENT).get(0);
		
		Assert.assertEquals(generated.getOrganizer(), expectedEvent.getOrganizer());
		Assert.assertEquals(generated.getStartDate(), expectedEvent.getStartDate());
		Assert.assertEquals(generated.getEndDate(), expectedEvent.getEndDate());
		Assert.assertEquals(generated.getSummary(), expectedEvent.getSummary());
		Assert.assertEquals(generated.getDescription(), expectedEvent.getDescription());
		Assert.assertEquals(generated.getProperty(VisitorLimit.VISITOR_LIMIT), expectedEvent.getProperty(VisitorLimit.VISITOR_LIMIT));
		
		// verify owner and visitor have correct roles in the example
		Assert.assertTrue(eventUtils.isAttendingAsOwner(expectedEvent, ownerAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(expectedEvent, visitorAccount));
		// verify owner and visitor have same roles in the generated event
		Assert.assertTrue(eventUtils.isAttendingAsOwner(generated, ownerAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(generated, visitorAccount));
	}
	
	@Test
	public void testConstructIndividualAppointmentSetTimeZone() throws Exception {
		CaldavEventUtilsImpl eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
		eventUtils.setExplicitSetTimeZone(true);
		eventUtils.setTimeZone("America/Chicago");
		eventUtils.afterPropertiesSet();
		
		SimpleDateFormat dateFormat = CommonDateOperations.getDateTimeFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
		Date start = dateFormat.parse("20110503-0800");
		Date end = dateFormat.parse("20110503-0900");
		AvailableBlock block = AvailableBlockBuilder.createBlock(start, end);
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setDisplayName("OWNER NAME");
		ownerAccount.setEmailAddress("someone@wherever.org");
		MockScheduleOwner owner = new MockScheduleOwner(ownerAccount, 1L);
		owner.setPreference(Preferences.LOCATION, "123 University Building");
		
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setDisplayName("VISITOR NAME");
		visitorAccount.setEmailAddress("somevisitor@wherever.org");
		MockScheduleVisitor visitor = new MockScheduleVisitor(visitorAccount);
		VEvent generated = eventUtils.constructAvailableAppointment(block, owner, visitor, "test reason.");
		
		
		Resource example = new ClassPathResource("vevent-examples/example-individual-appointment-with-explicit-timezone.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar expectedCalendar = builder.build(example.getInputStream());
		VEvent expectedEvent = (VEvent) expectedCalendar.getComponents(VEvent.VEVENT).get(0);
		
		Assert.assertEquals(generated.getOrganizer(), expectedEvent.getOrganizer());
		Assert.assertEquals(generated.getStartDate(), expectedEvent.getStartDate());
		Assert.assertEquals(generated.getEndDate(), expectedEvent.getEndDate());
		Assert.assertEquals(generated.getSummary(), expectedEvent.getSummary());
		Assert.assertEquals(generated.getDescription(), expectedEvent.getDescription());
		Assert.assertEquals(generated.getProperty(VisitorLimit.VISITOR_LIMIT), expectedEvent.getProperty(VisitorLimit.VISITOR_LIMIT));
		
		// verify owner and visitor have correct roles in the example
		Assert.assertTrue(eventUtils.isAttendingAsOwner(expectedEvent, ownerAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(expectedEvent, visitorAccount));
		// verify owner and visitor have same roles in the generated event
		Assert.assertTrue(eventUtils.isAttendingAsOwner(generated, ownerAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(generated, visitorAccount));
	}
	
	/**
	 * Construct an appointment for a "group" appointment (e.g. multiple visitor3).
	 * Compare with 'vevent-examples/example-group-appointment.ics'.
	 * 
	 * @throws InputFormatException
	 * @throws IOException
	 * @throws ParserException
	 * @throws ParseException 
	 */
	@Test
	public void testConstructGroupAppointment() throws InputFormatException, IOException, ParserException, ParseException {
		CaldavEventUtilsImpl eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
		eventUtils.setExplicitSetTimeZone(false);
		
		SimpleDateFormat dateFormat = CommonDateOperations.getDateTimeFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
		Date start = dateFormat.parse("20110503-0800");
		Date end = dateFormat.parse("20110503-0900");
		
		AvailableBlock block = AvailableBlockBuilder.createBlock(start, end, 5);
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setDisplayName("OWNER NAME");
		ownerAccount.setEmailAddress("someone@wherever.org");
		MockScheduleOwner owner = new MockScheduleOwner(ownerAccount, 1L);
		owner.setPreference(Preferences.MEETING_PREFIX, "Group Appointment");
		owner.setPreference(Preferences.LOCATION, "123 University Building");
		
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setDisplayName("VISITOR NAME");
		visitorAccount.setEmailAddress("somevisitor@wherever.org");
		MockScheduleVisitor visitor = new MockScheduleVisitor(visitorAccount);
		VEvent generated = eventUtils.constructAvailableAppointment(block, owner, visitor, "Test");
		
		MockCalendarAccount visitorAccount2 = new MockCalendarAccount();
		visitorAccount2.setDisplayName("VISITOR TWO NAME");
		visitorAccount2.setEmailAddress("somevisitor2@wherever.org");
		generated.getProperties().add(eventUtils.constructVisitorAttendee(visitorAccount2));
		
		MockCalendarAccount visitorAccount3 = new MockCalendarAccount();
		visitorAccount3.setDisplayName("VISITOR THREE NAME");
		visitorAccount3.setEmailAddress("somevisitor3@wherever.org");
		generated.getProperties().add(eventUtils.constructVisitorAttendee(visitorAccount3));
		
		Resource example = new ClassPathResource("vevent-examples/example-group-appointment.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar expectedCalendar = builder.build(example.getInputStream());
		VEvent expectedEvent = (VEvent) expectedCalendar.getComponents(VEvent.VEVENT).get(0);
		
		Assert.assertEquals(generated.getOrganizer(), expectedEvent.getOrganizer());
		Assert.assertEquals(generated.getStartDate(), expectedEvent.getStartDate());
		Assert.assertEquals(generated.getEndDate(), expectedEvent.getEndDate());
		Assert.assertEquals(generated.getSummary(), expectedEvent.getSummary());
		Assert.assertEquals(generated.getDescription(), expectedEvent.getDescription());
		Assert.assertEquals(generated.getProperty(VisitorLimit.VISITOR_LIMIT), expectedEvent.getProperty(VisitorLimit.VISITOR_LIMIT));
		
		// verify owner and visitors have correct roles in the example
		Assert.assertTrue(eventUtils.isAttendingAsOwner(expectedEvent, ownerAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(expectedEvent, visitorAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(expectedEvent, visitorAccount2));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(expectedEvent, visitorAccount3));
		// verify owner and visitors have same roles in the generated event
		Assert.assertTrue(eventUtils.isAttendingAsOwner(generated, ownerAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(generated, visitorAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(generated, visitorAccount2));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(generated, visitorAccount3));
	}
	
	@Test
	public void testConstructGroupAppointmentSetTimeZone() throws Exception {
		CaldavEventUtilsImpl eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
		eventUtils.setExplicitSetTimeZone(true);
		eventUtils.setTimeZone("America/Chicago");
		eventUtils.afterPropertiesSet();
		
		SimpleDateFormat dateFormat = CommonDateOperations.getDateTimeFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
		Date start = dateFormat.parse("20110503-0800");
		Date end = dateFormat.parse("20110503-0900");
		
		AvailableBlock block = AvailableBlockBuilder.createBlock(start, end, 5);
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setDisplayName("OWNER NAME");
		ownerAccount.setEmailAddress("someone@wherever.org");
		MockScheduleOwner owner = new MockScheduleOwner(ownerAccount, 1L);
		owner.setPreference(Preferences.MEETING_PREFIX, "Group Appointment");
		owner.setPreference(Preferences.LOCATION, "123 University Building");
		
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setDisplayName("VISITOR NAME");
		visitorAccount.setEmailAddress("somevisitor@wherever.org");
		MockScheduleVisitor visitor = new MockScheduleVisitor(visitorAccount);
		VEvent generated = eventUtils.constructAvailableAppointment(block, owner, visitor, "Test");
		
		MockCalendarAccount visitorAccount2 = new MockCalendarAccount();
		visitorAccount2.setDisplayName("VISITOR TWO NAME");
		visitorAccount2.setEmailAddress("somevisitor2@wherever.org");
		generated.getProperties().add(eventUtils.constructVisitorAttendee(visitorAccount2));
		
		MockCalendarAccount visitorAccount3 = new MockCalendarAccount();
		visitorAccount3.setDisplayName("VISITOR THREE NAME");
		visitorAccount3.setEmailAddress("somevisitor3@wherever.org");
		generated.getProperties().add(eventUtils.constructVisitorAttendee(visitorAccount3));
		
		Resource example = new ClassPathResource("vevent-examples/example-group-appointment-with-explicit-timezone.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar expectedCalendar = builder.build(example.getInputStream());
		VEvent expectedEvent = (VEvent) expectedCalendar.getComponents(VEvent.VEVENT).get(0);
		
		Assert.assertEquals(generated.getOrganizer(), expectedEvent.getOrganizer());
		Assert.assertEquals(generated.getStartDate(), expectedEvent.getStartDate());
		Assert.assertEquals(generated.getEndDate(), expectedEvent.getEndDate());
		Assert.assertEquals(generated.getSummary(), expectedEvent.getSummary());
		Assert.assertEquals(generated.getDescription(), expectedEvent.getDescription());
		Assert.assertEquals(generated.getProperty(VisitorLimit.VISITOR_LIMIT), expectedEvent.getProperty(VisitorLimit.VISITOR_LIMIT));
		
		// verify owner and visitors have correct roles in the example
		Assert.assertTrue(eventUtils.isAttendingAsOwner(expectedEvent, ownerAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(expectedEvent, visitorAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(expectedEvent, visitorAccount2));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(expectedEvent, visitorAccount3));
		// verify owner and visitors have same roles in the generated event
		Assert.assertTrue(eventUtils.isAttendingAsOwner(generated, ownerAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(generated, visitorAccount));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(generated, visitorAccount2));
		Assert.assertTrue(eventUtils.isAttendingAsVisitor(generated, visitorAccount3));
	}
	
	@Test
	public void testWrapEventInCalendarControl() throws IOException, ParserException {
		CaldavEventUtilsImpl eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
		
		Resource example = new ClassPathResource("vevent-examples/example-individual-appointment.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(example.getInputStream());
		VEvent event = (VEvent) calendar.getComponents(VEvent.VEVENT).get(0);
		
		Calendar wrapped = eventUtils.wrapEventInCalendar(event);
		Assert.assertEquals(1, wrapped.getComponents().size());
	}
	
	@Test
	public void testWrapEventInCalendarSetTimeZone() throws Exception {
		CaldavEventUtilsImpl eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
		eventUtils.setExplicitSetTimeZone(true);
		eventUtils.setTimeZone("America/Chicago");
		eventUtils.afterPropertiesSet();
		
		Resource example = new ClassPathResource("vevent-examples/example-individual-appointment-with-explicit-timezone.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(example.getInputStream());
		VEvent event = (VEvent) calendar.getComponents(VEvent.VEVENT).get(0);
		
		Calendar wrapped = eventUtils.wrapEventInCalendar(event);
		Assert.assertEquals(2, wrapped.getComponents().size());
		for(Object o: wrapped.getComponents()) {
			Component c = (Component) o;
			if(VEvent.VEVENT.equals(c.getName())) {
				//ok
			} else if (VTimeZone.VTIMEZONE.equals(c.getName())) {
				Assert.assertEquals("America/Chicago", c.getProperty(TzId.TZID).getValue());
			} else {
				fail("unexpected component " + c);
			}
		}
	}
}
