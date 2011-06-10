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
package org.jasig.schedassist.impl.caldav.xml;

import java.io.IOException;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;

import org.apache.commons.io.IOUtils;
import org.jasig.schedassist.impl.caldav.CalendarWithURI;
import org.jasig.schedassist.model.SchedulingAssistantAppointment;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests for {@link ReportResponseHandlerImpl}.
 * 
 * @author Nicholas Blair
 * @version $ Id: ReportResponseHandlerImplTest.java $
 */
public class ReportResponseHandlerImplTest {

	@Test
	public void testControl() throws IOException {
		Resource controlExample = new ClassPathResource("caldav-examples/report-response-single-calendar.xml");
		
		ReportResponseHandlerImpl handler = new ReportResponseHandlerImpl();
		List<CalendarWithURI> calendars = handler.extractCalendars(controlExample.getInputStream());
		Assert.assertEquals(1, calendars.size());
		
		CalendarWithURI withUri = calendars.get(0);
		Assert.assertEquals("http://cal.example.com/bernard/work/abcd2.ics", withUri.getUri());
		Assert.assertEquals("\"fffff-abcd2\"", withUri.getEtag());
		Calendar cal = withUri.getCalendar();
		ProdId prodId = cal.getProductId();
		Assert.assertNotNull(prodId);
		Assert.assertEquals("-//CalendarKey 2.0//iCal4j 1.0//EN", prodId.getValue());
		
		ComponentList components = cal.getComponents(VEvent.VEVENT);
		Assert.assertEquals(1, components.size());
		VEvent event = (VEvent) components.get(0);
		Assert.assertEquals("regular 10 am meeting", event.getSummary().getValue());
	}
	
	@Test
	public void testBedworkSingleCalendar() throws IOException {
		Resource controlExample = new ClassPathResource("caldav-examples/report-response-bedework-single-calendar.xml");
		
		ReportResponseHandlerImpl handler = new ReportResponseHandlerImpl();
		List<CalendarWithURI> calendars = handler.extractCalendars(controlExample.getInputStream());
		Assert.assertEquals(1, calendars.size());
		
		CalendarWithURI withUri = calendars.get(0);
		Assert.assertEquals("/ucaldav/user/schwag/calendar/CAL-00e8903c-2fbc6e9b-012f-bc6f2d42-00000002.ics", withUri.getUri());
		Assert.assertEquals("\"20110505T151112Z-0\"", withUri.getEtag());
		Calendar cal = withUri.getCalendar();
		ProdId prodId = cal.getProductId();
		Assert.assertNotNull(prodId);
		Assert.assertEquals("//Bedework.org//BedeWork V3.7//EN", prodId.getValue());
		
		ComponentList components = cal.getComponents(VEvent.VEVENT);
		Assert.assertEquals(1, components.size());
		VEvent event = (VEvent) components.get(0);
		Assert.assertEquals("dentist appointment", event.getSummary().getValue());
	}
	
	@Test
	public void testBedworkSchedulingAssistant() throws IOException {
		Resource controlExample = new ClassPathResource("caldav-examples/report-response-bedework-scheduling-assistant.xml");
		
		ReportResponseHandlerImpl handler = new ReportResponseHandlerImpl();
		List<CalendarWithURI> calendars = handler.extractCalendars(controlExample.getInputStream());
		Assert.assertEquals(1, calendars.size());
		
		CalendarWithURI withUri = calendars.get(0);
		Assert.assertEquals("/ucaldav/user/schwag/calendar/68b9d022-7a39-41ec-97c7-0e0e7fca74a6.ics", withUri.getUri());
		Assert.assertEquals("\"20110505T173152Z-0\"", withUri.getEtag());
		Calendar cal = withUri.getCalendar();
		ProdId prodId = cal.getProductId();
		Assert.assertNotNull(prodId);
		Assert.assertEquals("//Bedework.org//BedeWork V3.7//EN", prodId.getValue());
		
		ComponentList components = cal.getComponents(VEvent.VEVENT);
		Assert.assertEquals(1, components.size());
		VEvent event = (VEvent) components.get(0);
		Assert.assertEquals("test appointment with Johnson, Arlen", event.getSummary().getValue());
		Organizer organizer = event.getOrganizer();
		Assert.assertNotNull(organizer);
		Assert.assertEquals("mailto:schwag@mysite.org", organizer.getValue());
		Assert.assertEquals(1, organizer.getParameters().size());
		Assert.assertEquals("Schwartz, Gary", organizer.getParameter(Cn.CN).getValue());
		Assert.assertEquals("need help picking classes", event.getDescription().getValue());
		Assert.assertEquals(SchedulingAssistantAppointment.TRUE, event.getProperty(SchedulingAssistantAppointment.AVAILABLE_APPOINTMENT));
	}
	
	@Test
	public void testMultipleCalendarResponse() throws IOException {
		Resource controlExample = new ClassPathResource("caldav-examples/report-response-multiple-calendars.xml");
		
		ReportResponseHandlerImpl handler = new ReportResponseHandlerImpl();
		List<CalendarWithURI> calendars = handler.extractCalendars(controlExample.getInputStream());
		Assert.assertEquals(2, calendars.size());
		
		CalendarWithURI withUri = calendars.get(0);
		Assert.assertEquals("http://cal.example.com/bernard/work/abcd2.ics", withUri.getUri());
		Assert.assertEquals("\"fffff-abcd2\"", withUri.getEtag());
		Calendar cal = withUri.getCalendar();
		ProdId prodId = cal.getProductId();
		Assert.assertNotNull(prodId);
		Assert.assertEquals("-//CalendarKey 2.0//iCal4j 1.0//EN", prodId.getValue());
		
		ComponentList components = cal.getComponents(VEvent.VEVENT);
		Assert.assertEquals(1, components.size());
		VEvent event = (VEvent) components.get(0);
		Assert.assertEquals("regular 10 am meeting", event.getSummary().getValue());
		
		CalendarWithURI withUri2 = calendars.get(1);
		Assert.assertEquals("http://cal.example.com/bernard/work/abcd3.ics", withUri2.getUri());
		Assert.assertEquals("\"fffff-abcd3\"", withUri2.getEtag());
		Calendar cal2 = withUri2.getCalendar();
		ComponentList components2 = cal2.getComponents(VEvent.VEVENT);
		Assert.assertEquals(2, components2.size());
	}
	
	@Test
	public void testExtractCalendarScheduleStatus() throws IOException {
		Resource scheduleStatusExample = new ClassPathResource("vevent-examples/example-individual-appointment-schedule-status.ics");
		ReportResponseHandlerImpl handler = new ReportResponseHandlerImpl();
		Calendar cal = handler.extractCalendar(IOUtils.toString(scheduleStatusExample.getInputStream()));
		Assert.assertNotNull(cal);
		Assert.assertEquals(1, cal.getComponents(VEvent.VEVENT).size());
	}
}
