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

import java.io.IOException;
import java.net.URISyntaxException;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.property.Organizer;

import org.jasig.schedassist.NullAffiliationSourceImpl;
import org.jasig.schedassist.model.AppointmentRole;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
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
	public void testConstructOrganizer() throws URISyntaxException  {
		CaldavEventUtilsImpl eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
		MockCalendarAccount calendarAccount = new MockCalendarAccount();
		calendarAccount.setDisplayName("DISPLAY NAME");
		calendarAccount.setEmailAddress("someone@wherever.org");
		
		Organizer expected = new Organizer("mailto:someone@wherever.org");
		expected.getParameters().add(new Cn("DISPLAY NAME"));
		
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
	 */
	@Test
	public void testConstructIndividualAppointment() throws InputFormatException, IOException, ParserException {
		CaldavEventUtilsImpl eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
		
		AvailableBlock block = AvailableBlockBuilder.createBlock("20110503-0800", "20110503-0900");
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
	
	/**
	 * Construct an appointment for a "group" appointment (e.g. multiple visitor3).
	 * Compare with 'vevent-examples/example-group-appointment.ics'.
	 * 
	 * @throws InputFormatException
	 * @throws IOException
	 * @throws ParserException
	 */
	@Test
	public void testConstructGroupAppointment() throws InputFormatException, IOException, ParserException {
		CaldavEventUtilsImpl eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
		
		AvailableBlock block = AvailableBlockBuilder.createBlock("20110503-0800", "20110503-0900", 5);
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
		generated.getProperties().add(eventUtils.constructAvailableAttendee(visitorAccount2, AppointmentRole.VISITOR));
		
		MockCalendarAccount visitorAccount3 = new MockCalendarAccount();
		visitorAccount3.setDisplayName("VISITOR THREE NAME");
		visitorAccount3.setEmailAddress("somevisitor3@wherever.org");
		generated.getProperties().add(eventUtils.constructAvailableAttendee(visitorAccount3, AppointmentRole.VISITOR));
		
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
}
