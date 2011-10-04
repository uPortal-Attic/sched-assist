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
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.Attendee;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.NullAffiliationSourceImpl;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Test bench for {@link VisibleScheduleBuilder}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleScheduleBuilderTest.java 2934 2010-12-09 16:21:58Z npblair $
 */
public class VisibleScheduleBuilderTest {

	private Log LOG = LogFactory.getLog(this.getClass());

	private DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
	private VisibleScheduleBuilder builder = new VisibleScheduleBuilder(eventUtils);

	/**
	 * Assert empty calendar and empty Advising Schedule throw no errors
	 * @throws Exception
	 */
	@Test
	public void testEmptyCalendar() throws Exception {
		Calendar calendar = new Calendar(new ComponentList());
		AvailableSchedule schedule = new AvailableSchedule(new TreeSet<AvailableBlock>());

		MockScheduleOwner owner = new MockScheduleOwner(new MockCalendarAccount(), 1);

		VisibleScheduleBuilder builder = new VisibleScheduleBuilder();
		VisibleSchedule newCalendar = builder.calculateVisibleSchedule(new Date(),
				DateUtils.addDays(new Date(), 7),
				calendar,
				schedule, 
				owner);
		Assert.assertEquals(0, newCalendar.getSize());
	}

	/**
	 * Create visible schedule for Aug 4 2008.
	 * Create a calendar with an appointment on Aug 5 2008.
	 * Send the two into VisibleScheduleBuilder, and assert no conflicts found.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalendarNoConflict() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("someowner@wisc.edu");
		person.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person, 1);
		
		// Aug 4 2008 9:30 AM to 3:30 PM
		AvailableBlock block = AvailableBlockBuilder.createBlock("20080804-0930", "20080804-1530");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.expand(block, 30);
		AvailableSchedule schedule = new AvailableSchedule(blocks);

		// Aug 5 2008 10:30 AM to 11:30 AM 
		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20080805-1030")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20080805-1130")),
		"some event");
		ParameterList parameterList = new ParameterList();
		parameterList.add(PartStat.ACCEPTED);
		parameterList.add(CuType.INDIVIDUAL);
		parameterList.add(Rsvp.FALSE);
		parameterList.add(new Cn(person.getDisplayName()));
		Attendee attendee = new Attendee(parameterList, "mailto:" + person.getEmailAddress());
		someEvent.getProperties().add(attendee);
		ComponentList components = new ComponentList();
		components.add(someEvent);
		
		// week start is Sun Aug 3
		VisibleSchedule visibleSchedule = builder.calculateVisibleSchedule(makeDateTime("20080803-0000"),
				makeDateTime("20080810-0000"),
				new Calendar(components), 
				schedule,
				owner);

		ComponentList componentList = visibleSchedule.getCalendar().getComponents(Component.VEVENT);
		// there should be 12 events in the result
		Assert.assertEquals(12, componentList.size());
		Assert.assertEquals(12, visibleSchedule.getFreeCount());
		Assert.assertEquals(0, visibleSchedule.getBusyCount());
		Assert.assertEquals(0, visibleSchedule.getAttendingCount());
		
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimpleConflict() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("someowner@wisc.edu");
		person.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person, 1);
		
		AvailableBlock block = AvailableBlockBuilder.createBlock("20091104-0900", "20091104-1000");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.expand(block, 30);
		Assert.assertEquals(2, blocks.size());
		AvailableSchedule schedule = new AvailableSchedule(blocks);

		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-0900")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20091104-0930")),
		"conflict event");
		
		ParameterList parameterList = new ParameterList();
		parameterList.add(PartStat.ACCEPTED);
		parameterList.add(CuType.INDIVIDUAL);
		parameterList.add(Rsvp.FALSE);
		parameterList.add(new Cn(person.getDisplayName()));
		Attendee attendee = new Attendee(parameterList, "mailto:" + person.getEmailAddress());
		someEvent.getProperties().add(attendee);
		
		ComponentList components = new ComponentList();
		components.add(someEvent);

		

		VisibleSchedule visibleSchedule = builder.calculateVisibleSchedule(makeDateTime("20091104-0830"),
				makeDateTime("20091104-1030"),
				new Calendar(components), 
				schedule,
				owner);

		ComponentList componentList = visibleSchedule.getCalendar().getComponents(Component.VEVENT);
		// free from 9:30 to 10:30, busy from 10:30 to 11:30, free from 11:30 to 3:30 PM

		Assert.assertEquals(2, componentList.size());

		Assert.assertEquals(1, visibleSchedule.getFreeCount());
		Assert.assertEquals(1, visibleSchedule.getBusyCount());
		Assert.assertEquals(0, visibleSchedule.getAttendingCount());
	}

	/**
	 * Create an advising schedule for Aug 4 2008.
	 * Create a calendar with an appointment on Aug 4 2008.
	 * Send the two into FreeBusyBuilder, and assert correct conflicts found.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOneAppointmentConflictsTwoBlocks() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("someowner@wisc.edu");
		person.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person, 1);
		
		AvailableBlock block = AvailableBlockBuilder.createBlock("20091109-0930", "20091109-1230");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.expand(block, 30);
		AvailableSchedule schedule = new AvailableSchedule(blocks);

		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20091109-1030")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20091109-1130")),
				"some event");
		ParameterList parameterList = new ParameterList();
		parameterList.add(PartStat.ACCEPTED);
		parameterList.add(CuType.INDIVIDUAL);
		parameterList.add(Rsvp.FALSE);
		parameterList.add(new Cn(person.getDisplayName()));
		Attendee attendee = new Attendee(parameterList, "mailto:" + person.getEmailAddress());
		someEvent.getProperties().add(attendee);
		ComponentList components = new ComponentList();
		components.add(someEvent);

		
		VisibleSchedule calendar = builder.calculateVisibleSchedule(makeDateTime("20091109-0000"),
				makeDateTime("20091110-0000"),
				new Calendar(components), 
				schedule,
				owner);
		Assert.assertEquals(6, calendar.getSize());

		Assert.assertEquals(4, calendar.getFreeCount());

		Assert.assertEquals(2, calendar.getBusyCount());
		for(AvailableBlock busy : calendar.getBusyList()) {
			if(!busy.getStartTime().equals(makeDateTime("20091109-1030"))
					&& !busy.getStartTime().equals(makeDateTime("20091109-1100"))
			) {
				Assert.fail("busyEvents contains unexpected block " + busy);
			}
		}	
	}

	/**
	 * Same test as testCalendarConflict1, however all dates are incremented by 1.
	 * Week start (argument to {@link VisibleScheduleBuilder} constructor) is set to Mon Aug 4.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalendarConflictShiftWeekStart() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("someowner@wisc.edu");
		person.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person, 1);
		
		// Aug 5 2008 9:30 AM to 3:30 PM
		AvailableBlock block = AvailableBlockBuilder.createBlock("20080805-0930", "20080805-1530");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.expand(block, 30);
		AvailableSchedule schedule = new AvailableSchedule(blocks);

		// Aug 5 2008 10:30 AM to 11:30 AM 
		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20080805-1030")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20080805-1130")),
		"some event");
		ParameterList parameterList = new ParameterList();
		parameterList.add(PartStat.ACCEPTED);
		parameterList.add(CuType.INDIVIDUAL);
		parameterList.add(Rsvp.FALSE);
		parameterList.add(new Cn(person.getDisplayName()));
		Attendee attendee = new Attendee(parameterList, "mailto:" + person.getEmailAddress());
		someEvent.getProperties().add(attendee);
		ComponentList components = new ComponentList();
		components.add(someEvent);

		// week start is Mon Aug 4!
		VisibleSchedule calendar = builder.calculateVisibleSchedule(makeDateTime("20080804-0000"),
				makeDateTime("20080810-0000"),
				new Calendar(components), 
				schedule,
				owner);
		ComponentList componentList = calendar.getCalendar().getComponents(Component.VEVENT);
		// free from 9:30 to 10:30, busy from 10:30 to 11:30, free from 11:30 to 3:30 PM

		Assert.assertEquals(12, componentList.size());
		Assert.assertEquals(10, calendar.getFreeCount());
		Assert.assertEquals(2, calendar.getBusyCount());
		Assert.assertEquals(0, calendar.getAttendingCount());
		// 9-9:30 AM, 9:30-10 AM, 10-10:30 AM should be "free"
		// 10:30-11 AM and 11-11:30AM should be "busy"
		// 11:30 AM on should be free
		for(AvailableBlock busy : calendar.getBusyList()) {
			if(!busy.getStartTime().equals(makeDateTime("20080805-1030"))
					&& !busy.getStartTime().equals(makeDateTime("20080805-1100"))
			) {
				Assert.fail("busyEvents contains startdate of " + busy.getStartTime());
			}
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFindAttending() throws Exception {
		// construct visitor
		MockCalendarAccount person1 = new MockCalendarAccount();
		person1.setEmailAddress("somevisitor@wisc.edu");
		person1.setDisplayName("Some Visitor");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person1);

		// construct owner
		MockCalendarAccount person2 = new MockCalendarAccount();
		person2.setEmailAddress("someowner@wisc.edu");
		person2.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person2, 1);
		

		// Aug 4 2008 9:30 AM to 3:30 PM
		AvailableBlock block = AvailableBlockBuilder.createBlock("20080804-0930", "20080804-1530");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.expand(block, 30);
		AvailableSchedule schedule = new AvailableSchedule(blocks);

		// some other non-available appointment Aug 4 2008 10:30 AM to 11:30 AM 
		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20080804-1030")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20080804-1130")),
		"some event");
		ParameterList parameterList = new ParameterList();
		parameterList.add(PartStat.ACCEPTED);
		parameterList.add(CuType.INDIVIDUAL);
		parameterList.add(Rsvp.FALSE);
		parameterList.add(new Cn(person2.getDisplayName()));
		Attendee attendee = new Attendee(parameterList, "mailto:" + person2.getEmailAddress());
		someEvent.getProperties().add(attendee);
		ComponentList components = new ComponentList();
		components.add(someEvent);

		// make available appointment with visitor from 1:00 PM to 1:30 PM
		VEvent availableAppointment = this.eventUtils.constructAvailableAppointment(
				AvailableBlockBuilder.createBlock("20080804-1300", "20080804-1330"), 
				owner, visitor, "advising appt");
		components.add(availableAppointment);

		// week start is Sun Aug 3
		VisibleSchedule calendar = this.builder.calculateVisibleSchedule(makeDateTime("20080803-0000"),
				makeDateTime("20080810-0000"),
				new Calendar(components), 
				schedule,
				owner,
				visitor);
		
		ComponentList componentList = calendar.getCalendar().getComponents(Component.VEVENT);
		// free from 9:30 to 10:30, busy from 10:30 to 11:30, free from 11:30 to 1:00 PM, student appt from 1-1:30 PM, free to 3:30 PM

		Assert.assertEquals(12, componentList.size());
		// 9-9:30 AM, 9:30-10 AM, 10-10:30 AM should be "free"
		// 10:30-11 AM and 11-11:30AM should be "busy"
		// 11:30 AM on should be free
		
		Assert.assertEquals(9, calendar.getFreeCount());

		Assert.assertEquals(2, calendar.getBusyCount());
		for(AvailableBlock busy : calendar.getBusyList()) {
			if(!busy.getStartTime().equals(makeDateTime("20080804-1030"))
					&& !busy.getStartTime().equals(makeDateTime("20080804-1100"))
			) {
				Assert.fail("busyEvents contains startdate of " + busy.getStartTime());
			}
		}	

		Assert.assertEquals(1, calendar.getAttendingCount());
		AvailableBlock attendingEvent = calendar.getAttendingList().get(0);
		Assert.assertEquals(attendingEvent.getStartTime(), makeDateTime("20080804-1300"));
		Assert.assertEquals(attendingEvent.getEndTime(), makeDateTime("20080804-1330"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFindAttendingOwnerVisitorSamePerson() throws Exception {
		// construct visitor and owner from same attributes
		MockCalendarAccount person1 = new MockCalendarAccount();
		person1.setEmailAddress("somevisitor@wisc.edu");
		person1.setDisplayName("Some Visitor");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person1);
		MockScheduleOwner owner = new MockScheduleOwner(person1, 1);

		// Aug 4 2008 9:30 AM to 3:30 PM
		AvailableBlock block = AvailableBlockBuilder.createBlock("20080804-0930", "20080804-1530");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.expand(block, 30);
		AvailableSchedule schedule = new AvailableSchedule(blocks);

		// Aug 4 2008 10:30 AM to 11:30 AM 
		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20080804-1030")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20080804-1130")),
		"some event");
		ParameterList parameterList = new ParameterList();
		parameterList.add(PartStat.ACCEPTED);
		parameterList.add(CuType.INDIVIDUAL);
		parameterList.add(Rsvp.FALSE);
		parameterList.add(new Cn(person1.getDisplayName()));
		Attendee attendee = new Attendee(parameterList, "mailto:" + person1.getEmailAddress());
		someEvent.getProperties().add(attendee);
		ComponentList components = new ComponentList();
		components.add(someEvent);

		// make available appointment with visitor from 1:00 PM to 1:30 PM
		VEvent availableAppointment = this.eventUtils.constructAvailableAppointment(
				AvailableBlockBuilder.createBlock("20080804-1300", "20080804-1330"), 
				owner, visitor, "advising appt");
		components.add(availableAppointment);


		// week start is Sun Aug 3
		VisibleSchedule calendar = this.builder.calculateVisibleSchedule(makeDateTime("20080803-0000"),
				makeDateTime("20080810-0000"),
				new Calendar(components), 
				schedule,
				owner,
				visitor);
		ComponentList componentList = calendar.getCalendar().getComponents(Component.VEVENT);
		// free from 9:30 to 10:30, busy from 10:30 to 11:30, free from 11:30 to 1:00 PM, student appt from 1-1:30 PM, free to 3:30 PM

		Assert.assertEquals(12, componentList.size());
		// 9-9:30 AM, 9:30-10 AM, 10-10:30 AM should be "free"
		// 10:30-11 AM and 11-11:30AM should be "busy"
		// 11:30 AM on should be free

		Assert.assertEquals(9, calendar.getFreeCount());

		Assert.assertEquals(2, calendar.getBusyCount());
		for(AvailableBlock busy: calendar.getBusyList()) {
			if(!busy.getStartTime().equals(makeDateTime("20080804-1030"))
					&& !busy.getStartTime().equals(makeDateTime("20080804-1100"))
			) {
				Assert.fail("busyEvents contains startdate of " + busy.getStartTime());
			}
		}	

		Assert.assertEquals(1, calendar.getAttendingCount());
		AvailableBlock attendingEvent = calendar.getAttendingList().get(0);
		Assert.assertEquals(attendingEvent.getStartTime(), makeDateTime("20080804-1300"));
		Assert.assertEquals(attendingEvent.getEndTime(), makeDateTime("20080804-1330"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAlternateMeetingDurations() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("someowner@wisc.edu");
		person.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person, 1);
		
		// 4 week available schedule
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", 
				"12:00 PM",
				"MRF",
				makeDateTime("20091102-0000"),
				makeDateTime("20091127-1800"));

		TreeSet<AvailableBlock> expanded = new TreeSet<AvailableBlock>(AvailableBlockBuilder.expand(blocks, 30));
		LOG.info("expanded set first: " + expanded.first() + ", last: " + expanded.last());
		AvailableSchedule schedule = new AvailableSchedule(expanded);

		// event conflict is thursday of 2nd week
		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20091112-1030")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20091112-1130")),
				"some event");
		ParameterList parameterList = new ParameterList();
		parameterList.add(PartStat.ACCEPTED);
		parameterList.add(CuType.INDIVIDUAL);
		parameterList.add(Rsvp.FALSE);
		parameterList.add(new Cn(person.getDisplayName()));
		Attendee attendee = new Attendee(parameterList, "mailto:" + person.getEmailAddress());
		someEvent.getProperties().add(attendee);
		ComponentList components = new ComponentList();
		components.add(someEvent);

		
		VisibleSchedule visible = this.builder.calculateVisibleSchedule(
				makeDateTime("20091102-0000"),
				makeDateTime("20091130-0000"),
				new Calendar(components), 
				schedule, 
				owner);

		Assert.assertEquals(2, visible.getBusyCount());
		Assert.assertEquals(70, visible.getFreeCount());
		Assert.assertEquals(72, visible.getSize());

		owner.setPreference(Preferences.DURATIONS, MeetingDurations.FIFTEEN.getKey());
		TreeSet<AvailableBlock> expanded2 = new TreeSet<AvailableBlock>(AvailableBlockBuilder.expand(blocks, 15));
		VisibleSchedule visible2 = this.builder.calculateVisibleSchedule(
				makeDateTime("20091102-0000"),
				makeDateTime("20091130-0000"),
				new Calendar(components), 
				new AvailableSchedule(expanded2), 
				owner);
		Assert.assertEquals(4, visible2.getBusyCount());
		Assert.assertEquals(140, visible2.getFreeCount());
		Assert.assertEquals(144, visible2.getSize());

		owner.setPreference(Preferences.DURATIONS, MeetingDurations.FORTYFIVE.getKey());
		TreeSet<AvailableBlock> expanded3 = new TreeSet<AvailableBlock>(AvailableBlockBuilder.expand(blocks, 45));
		VisibleSchedule visible3 = this.builder.calculateVisibleSchedule(
				makeDateTime("20091102-0000"),
				makeDateTime("20091130-0000"),
				new Calendar(components), 
				new AvailableSchedule(expanded3), 
				owner);

		Assert.assertEquals(2, visible3.getBusyCount());
		Assert.assertEquals(46, visible3.getFreeCount());
		Assert.assertEquals(48, visible3.getSize());

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConflictEventShorterThanPreferredDuration() throws Exception {
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("someowner@wisc.edu");
		person.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person, 1);
		
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", 
				"12:00 PM",
				"MTWRF",
				makeDateTime("20091102-0830"),
				makeDateTime("20091106-1600"));

		AvailableSchedule schedule = new AvailableSchedule(blocks);

		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20091103-1000")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20091103-1030")),
				"some conflicting appointment");
		ParameterList parameterList = new ParameterList();
		parameterList.add(PartStat.ACCEPTED);
		parameterList.add(CuType.INDIVIDUAL);
		parameterList.add(Rsvp.FALSE);
		parameterList.add(new Cn(person.getDisplayName()));
		Attendee attendee = new Attendee(parameterList, "mailto:" + person.getEmailAddress());
		someEvent.getProperties().add(attendee);
		ComponentList components = new ComponentList();
		components.add(someEvent);

		
		owner.setPreference(Preferences.DURATIONS, MeetingDurations.FORTYFIVE.getKey());
		VisibleSchedule visible =this.builder.calculateVisibleSchedule(makeDateTime("20091102-0830"),
				makeDateTime("20091106-1600"),
				new Calendar(components), 
				schedule, 
				owner);

		Assert.assertEquals(20, visible.getSize());
		Assert.assertEquals(19, visible.getFreeCount());
		Assert.assertEquals(1, visible.getBusyCount());
		AvailableBlock busyBlock = visible.getBusyList().get(0);
		Assert.assertEquals(makeDateTime("20091103-0945"), busyBlock.getStartTime());
		Assert.assertEquals(makeDateTime("20091103-1030"), busyBlock.getEndTime());

	}

	/**
	 * Verify that start and end times of blocks stay consistent for calculateVisibleSchedule.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOffsetStart() throws Exception {
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", 
				"1:00 PM",
				"MWF",
				makeDateTime("20090720-0800"),
				makeDateTime("20090724-1400"));

		TreeSet<AvailableBlock> expanded = new TreeSet<AvailableBlock>(AvailableBlockBuilder.expand(blocks, 30));
		Assert.assertEquals(makeDateTime("20090720-0900"), expanded.first().getStartTime());
		Assert.assertEquals(makeDateTime("20090720-0930"), expanded.first().getEndTime());
		Assert.assertEquals(makeDateTime("20090724-1230"), expanded.last().getStartTime());
		Assert.assertEquals(makeDateTime("20090724-1300"), expanded.last().getEndTime());
		AvailableSchedule schedule = new AvailableSchedule(expanded);

		Calendar emptyCalendar = new Calendar();

		MockScheduleOwner owner = new MockScheduleOwner(new MockCalendarAccount(), 1);
		owner.setPreference(Preferences.DURATIONS, MeetingDurations.THIRTY.getKey());
		VisibleSchedule control = this.builder.calculateVisibleSchedule(
				makeDateTime("20090719-0000"), 
				makeDateTime("20090726-0000"), 
				emptyCalendar, 
				schedule, 
				owner);
		Assert.assertEquals(24, control.getFreeCount());
		// verify that all the free blocks start on the half hour
		SortedMap<AvailableBlock,AvailableStatus> blockMap = control.getBlockMap();
		for(Entry<AvailableBlock,AvailableStatus> entry : blockMap.entrySet()) {
			if(AvailableStatus.FREE.equals(entry.getValue())) {
				AvailableBlock freeBlock = entry.getKey();
				java.util.Calendar startTimeCal = java.util.Calendar.getInstance();
				startTimeCal.setTime(freeBlock.getStartTime());
				Assert.assertTrue(startTimeCal.get(java.util.Calendar.MINUTE) % 30 == 0);
				java.util.Calendar endTimeCal = java.util.Calendar.getInstance();
				endTimeCal.setTime(freeBlock.getEndTime());
				Assert.assertTrue(endTimeCal.get(java.util.Calendar.MINUTE) % 30 == 0);
			}
		}


		// now start the offset in between a block
		VisibleSchedule startsInBlock = this.builder.calculateVisibleSchedule(
				makeDateTime("20090720-1005"), 
				makeDateTime("20090727-1005"), 
				emptyCalendar, 
				schedule, 
				owner);
		Assert.assertEquals(21, startsInBlock.getFreeCount());
		// verify that all the free blocks start on the half hour
		SortedMap<AvailableBlock,AvailableStatus> blockMap2 = startsInBlock.getBlockMap();
		for(Entry<AvailableBlock,AvailableStatus> entry : blockMap2.entrySet()) {
			if(AvailableStatus.FREE.equals(entry.getValue())) {
				AvailableBlock freeBlock = entry.getKey();
				java.util.Calendar startTimeCal = java.util.Calendar.getInstance();
				startTimeCal.setTime(freeBlock.getStartTime());
				Assert.assertTrue(startTimeCal.get(java.util.Calendar.MINUTE) % 30 == 0);
				java.util.Calendar endTimeCal = java.util.Calendar.getInstance();
				endTimeCal.setTime(freeBlock.getEndTime());
				Assert.assertTrue(endTimeCal.get(java.util.Calendar.MINUTE) % 30 == 0);
			}
		}
	}

	/**
	 * Same as {@link #testOffsetStart()}, only use 40 minute blocks.
	 */
	@Test
	public void testOffsetStart40() throws Exception {
		MeetingDurations fortyMinuteDurations = new MeetingDurations("40", 40, 40);

		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", 
				"1:00 PM",
				"MWF",
				makeDateTime("20090720-0800"),
				makeDateTime("20090724-1400"));

		TreeSet<AvailableBlock> expanded = new TreeSet<AvailableBlock>(AvailableBlockBuilder.expand(blocks, 40));
		Assert.assertEquals(makeDateTime("20090720-0900"), expanded.first().getStartTime());
		Assert.assertEquals(makeDateTime("20090720-0940"), expanded.first().getEndTime());
		Assert.assertEquals(makeDateTime("20090724-1220"), expanded.last().getStartTime());
		Assert.assertEquals(makeDateTime("20090724-1300"), expanded.last().getEndTime());
		AvailableSchedule schedule = new AvailableSchedule(expanded);

		Calendar emptyCalendar = new Calendar();

		MockScheduleOwner owner = new MockScheduleOwner(new MockCalendarAccount(), 1);
		owner.setPreference(Preferences.DURATIONS, fortyMinuteDurations.getKey());
		VisibleSchedule control = this.builder.calculateVisibleSchedule(
				makeDateTime("20090719-0000"), 
				makeDateTime("20090726-0000"), 
				emptyCalendar, 
				schedule, 
				owner);
		Assert.assertEquals(18, control.getFreeCount());
		// verify that all the free blocks start on the twenty minute marks
		for(AvailableBlock freeBlock : control.getFreeList()) {
			java.util.Calendar startTimeCal = java.util.Calendar.getInstance();
			startTimeCal.setTime(freeBlock.getStartTime());
			Assert.assertTrue(startTimeCal.get(java.util.Calendar.MINUTE) % 20 == 0);
			java.util.Calendar endTimeCal = java.util.Calendar.getInstance();
			endTimeCal.setTime(freeBlock.getEndTime());
			Assert.assertTrue(endTimeCal.get(java.util.Calendar.MINUTE) % 20 == 0);
		}


		// now start the offset in between a block
		VisibleSchedule startsInBlock = this.builder.calculateVisibleSchedule(
				makeDateTime("20090720-1005"), 
				makeDateTime("20090727-1005"), 
				emptyCalendar, 
				schedule, 
				owner);
		Assert.assertEquals(16, startsInBlock.getFreeList().size());
		// verify that all the free blocks start on the twenty minute marks
		for(AvailableBlock freeBlock : startsInBlock.getFreeList()) {
			java.util.Calendar startTimeCal = java.util.Calendar.getInstance();
			startTimeCal.setTime(freeBlock.getStartTime());
			Assert.assertTrue(startTimeCal.get(java.util.Calendar.MINUTE) % 20 == 0);
			java.util.Calendar endTimeCal = java.util.Calendar.getInstance();
			endTimeCal.setTime(freeBlock.getEndTime());
			Assert.assertTrue(endTimeCal.get(java.util.Calendar.MINUTE) % 20 == 0);
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateVisibleScheduleVisitorLimit4WithAttending() throws Exception {
		// construct owner
		MockCalendarAccount person1 = new MockCalendarAccount();
		person1.setEmailAddress("someowner@wisc.edu");
		person1.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person1, 1);
		
		MockCalendarAccount person2 = new MockCalendarAccount();
		person2.setEmailAddress("somevisitor@wisc.edu");
		person2.setDisplayName("Some Visitor");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person2);
		
		final Date targetMeetingStart = CommonDateOperations.parseDateTimePhrase("20091117-1300");
		final Date targetMeetingEnd = CommonDateOperations.parseDateTimePhrase("20091117-1330");
		
		VEvent availableAppointment = this.eventUtils.constructAvailableAppointment(
				AvailableBlockBuilder.createBlock(targetMeetingStart, targetMeetingEnd, 4),
				owner, 
				visitor, 
				"test event description");
		
		ComponentList components = new ComponentList();
		components.add(availableAppointment);
		Calendar calendar = new Calendar(components);
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("1:00 PM", "2:00 PM", "TR", 
				CommonDateOperations.parseDatePhrase("20091115"),
				CommonDateOperations.parseDatePhrase("20091121"),
				4);
		
		AvailableSchedule schedule = new AvailableSchedule(blocks);
		VisibleSchedule visibleSchedule = this.builder.calculateVisibleSchedule(
				CommonDateOperations.parseDatePhrase("20091115"), 
				CommonDateOperations.parseDatePhrase("20091121"), 
				calendar, 
				schedule, 
				owner,
				visitor);
		
		Assert.assertNotNull(visibleSchedule);
		// verify attending shows up
		List<AvailableBlock> visitor1AttendingList = visibleSchedule.getAttendingList();
		Assert.assertEquals(1, visitor1AttendingList.size());
		AvailableBlock visitor1Block = visitor1AttendingList.get(0);
		Assert.assertEquals(targetMeetingStart, visitor1Block.getStartTime());
		Assert.assertEquals(targetMeetingEnd, visitor1Block.getEndTime());
		Assert.assertEquals(AvailableStatus.ATTENDING, visibleSchedule.getBlockMap().get(visitor1Block));
		
		// construct 2nd visitor
		MockCalendarAccount person3 = new MockCalendarAccount();
		person3.setEmailAddress("othervisitor@wisc.edu");
		person3.setDisplayName("Other Visitor");
		MockScheduleVisitor visitor2 = new MockScheduleVisitor(person3);
		
		VisibleSchedule visibleSchedule2 = this.builder.calculateVisibleSchedule(
				CommonDateOperations.parseDatePhrase("20091115"), 
				CommonDateOperations.parseDatePhrase("20091121"), 
				calendar, 
				schedule, 
				owner,
				visitor2);
		
		// visibleSchedule2 should show the Tue Nov 17 1:00 PM slot as FREE, but with 1 spot taken
		Assert.assertEquals(0, visibleSchedule2.getAttendingCount());
		List<AvailableBlock> visitor2FreeList = visibleSchedule2.getFreeList();
		for(AvailableBlock visitor2Block : visitor2FreeList) {
			if(visitor2Block.getStartTime().equals(targetMeetingStart) && 
					visitor2Block.getEndTime().equals(targetMeetingEnd)) {
				Assert.assertEquals(1, visitor2Block.getVisitorsAttending());
			} else {
				Assert.assertEquals(0, visitor2Block.getVisitorsAttending());
			}
			Assert.assertEquals(4, visitor2Block.getVisitorLimit());
			Assert.assertEquals(AvailableStatus.FREE, visibleSchedule2.getBlockMap().get(visitor2Block)); 
		}
		
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOddAppointmentMultipleAttendees() throws Exception {
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", 
				"12:00 PM",
				"MTRF",
				makeDateTime("20090202-0830"),
				makeDateTime("20090206-1600"));

		TreeSet<AvailableBlock> expanded = new TreeSet<AvailableBlock>(AvailableBlockBuilder.expand(blocks, 45));
		LOG.info("expanded set first: " + expanded.first() + ", last: " + expanded.last());
		AvailableSchedule schedule = new AvailableSchedule(expanded);

		VEvent someEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(makeDateTime("20090203-1000")),
				new net.fortuna.ical4j.model.DateTime(makeDateTime("20090203-1030")),
		"some conflicting appointment");
		Attendee attendee = new Attendee();
		attendee.setValue("mailto:person@domain.edu");
		attendee.getParameters().add(new XParameter("X-ORACLE-SHOWASFREE", "FREE"));
		attendee.getParameters().add(new Cn("GIVEN SURNAME"));
		
		Attendee attendee2 = new Attendee();
		attendee2.setValue("mailto:person2@domain.edu");
		attendee2.getParameters().add(new Cn("GIVEN2 SURNAME2"));
		
		someEvent.getProperties().add(attendee);
		someEvent.getProperties().add(attendee2);
		ComponentList components = new ComponentList();
		components.add(someEvent);

		MockCalendarAccount person1 = new MockCalendarAccount();
		person1.setEmailAddress("someowner@wisc.edu");
		person1.setDisplayName("Some Owner");
		MockScheduleOwner owner = new MockScheduleOwner(person1, 1);
		owner.setPreference(Preferences.DURATIONS, MeetingDurations.FORTYFIVE.getKey());
		VisibleSchedule visible = this.builder.calculateVisibleSchedule(makeDateTime("20090202-0830"),
				makeDateTime("20090206-1600"),
				new Calendar(components), 
				schedule, 
				owner);

		Assert.assertEquals(16, visible.getSize());
		List<AvailableBlock> free = visible.getFreeList();
		Assert.assertEquals(16, free.size());
		List<AvailableBlock> busy = visible.getBusyList();
		Assert.assertEquals(0, busy.size());
	}
	
	/**
	 * Simulates the following scenario:
	 * <ul>
	 * <li>IScheduleOwner (owner1) has empty available schedule.</li>
	 * <li>IScheduleOwner's (owner1) calendar agenda has an available appointment, but the owner is a visitor to someone else's agenda (person1 is visitor to owner2).</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test
	public void testVisibleScheduleCalendarHasAvailableAppointmentVisitor() throws Exception {
		AvailableSchedule emptyAvailableSchedule = new AvailableSchedule(new TreeSet<AvailableBlock>());
		
		MockCalendarAccount person1 = new MockCalendarAccount();
		person1.setEmailAddress("someowner@wisc.edu");
		person1.setDisplayName("Some Owner");
		MockScheduleOwner owner1 = new MockScheduleOwner(person1, 1);
		MockScheduleVisitor visitor1 = new MockScheduleVisitor(person1);
		
		MockCalendarAccount person2 = new MockCalendarAccount();
		person2.setEmailAddress("someotherowner@wisc.edu");
		person2.setDisplayName("Some Other Owner");
		MockScheduleOwner owner2 = new MockScheduleOwner(person2, 2);
		
		AvailableBlock block = AvailableBlockBuilder.createBlock("20100911-0646", "20100911-0701", 3);
		VEvent event = this.eventUtils.constructAvailableAppointment(block, owner2, visitor1, "test appointment");
		
		ComponentList components = new ComponentList();
		components.add(event);
		Calendar owner1Calendar = new Calendar(components);
		
		MockCalendarAccount person3 = new MockCalendarAccount();
		person3.setEmailAddress("unsuspecting-visitor@wisc.edu");
		person3.setDisplayName("Unsuspecting Visitor");
		MockScheduleVisitor visitor3 = new MockScheduleVisitor(person3);
		
		VisibleSchedule visibleSchedule = this.builder.calculateVisibleSchedule(CommonDateOperations.getDateTimeFormat().parse("20100910-1200"),
				CommonDateOperations.getDateTimeFormat().parse("20100917-1200"), 
				owner1Calendar, emptyAvailableSchedule, owner1, visitor3);
		
		Assert.assertEquals(0, visibleSchedule.getAttendingCount());
		Assert.assertEquals(0, visibleSchedule.getBusyCount());
		Assert.assertEquals(0, visibleSchedule.getFreeCount());
		
	}
	
	/**
	 * TODO depends on VM's timezone
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws ParserException
	 * @throws InputFormatException
	 */
	//@Test
	public void testVisitorConflictsMisaligned() throws ParseException, IOException, ParserException, InputFormatException {
		InputStream calendarData = new ClassPathResource("org/jasig/schedassist/model/misaligned-conflicts-test.ics").getInputStream();
		
		Assert.assertNotNull(calendarData);
		CalendarBuilder builder = new CalendarBuilder();
		Calendar calendar = builder.build(calendarData);
		
		Date startTime = makeDateTime("20101118-0000");
		Date endTime = makeDateTime("20101121-0000");
		
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("10:00 AM", "3:00 PM", "F", startTime, endTime);
		AvailableSchedule schedule = new AvailableSchedule(blocks);
		
		MockCalendarAccount person = new MockCalendarAccount();
		person.setEmailAddress("mesdjian@wisctest.wisc.edu");
		person.setDisplayName("ARA MESDJIAN");
		MockScheduleVisitor visitor = new MockScheduleVisitor(person);
		
		VisibleSchedule visibleSchedule = this.builder.calculateVisitorConflicts(startTime, endTime, calendar, schedule, MeetingDurations.fromKey("17"), visitor);
	
		Assert.assertEquals(3, visibleSchedule.getBusyCount());
		List<AvailableBlock> busyBlocks = visibleSchedule.getBusyList();
		Assert.assertTrue(busyBlocks.contains(AvailableBlockBuilder.createBlock("20101119-1324", "20101119-1341")));
		Assert.assertTrue(busyBlocks.contains(AvailableBlockBuilder.createBlock("20101119-1341", "20101119-1358")));
		Assert.assertTrue(busyBlocks.contains(AvailableBlockBuilder.createBlock("20101119-1358", "20101119-1415")));
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
		Date time = dateFormat.parse(dateTimePhrase);
		return time;
	}

}
