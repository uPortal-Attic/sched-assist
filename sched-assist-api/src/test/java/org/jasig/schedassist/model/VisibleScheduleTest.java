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

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests for {@link VisibleSchedule}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleScheduleTest.java 2298 2010-07-28 15:23:04Z npblair $
 */
public class VisibleScheduleTest {

	@Test
	public void testLocateConflicting() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20091105-1100", "20091105-1130");
		
		VisibleSchedule schedule = new VisibleSchedule(MeetingDurations.THIRTY);
		schedule.addFreeBlock(block);
		
		Assert.assertEquals(0, 
				schedule.locateConflicting(AvailableBlockBuilder.createBlock("20091105-1055", "20091105-1059")).size());
		
		Set<AvailableBlock> conflicts = schedule.locateConflicting(AvailableBlockBuilder.createBlock("20091105-1100", "20091105-1101"));
		Assert.assertTrue(conflicts.contains(block));
		conflicts = schedule.locateConflicting(AvailableBlockBuilder.createBlock("20091105-1101", "20091105-1103"));
		Assert.assertTrue(conflicts.contains(block));
		conflicts = schedule.locateConflicting(AvailableBlockBuilder.createBlock("20091105-1101", "20091105-1129"));
		Assert.assertTrue(conflicts.contains(block));
		
		conflicts = schedule.locateConflicting(AvailableBlockBuilder.createBlock("20091105-1100", "20091105-1130"));
		Assert.assertTrue(conflicts.contains(block));
		
		conflicts = schedule.locateConflicting(AvailableBlockBuilder.createBlock("20091105-1101", "20091105-1131"));
		Assert.assertTrue(conflicts.contains(block));
		conflicts = schedule.locateConflicting(AvailableBlockBuilder.createBlock("20091105-1125", "20091105-1130"));
		Assert.assertTrue(conflicts.contains(block));
		conflicts = schedule.locateConflicting(AvailableBlockBuilder.createBlock("20091105-1129", "20091105-1130"));
		Assert.assertTrue(conflicts.contains(block));
		conflicts = schedule.locateConflicting(AvailableBlockBuilder.createBlock("20091105-1129", "20091105-1131"));
		Assert.assertTrue(conflicts.contains(block));
		conflicts = schedule.locateConflicting(AvailableBlockBuilder.createBlock("20091105-1129", "20091105-1330"));
		Assert.assertTrue(conflicts.contains(block));
		conflicts = schedule.locateConflicting(AvailableBlockBuilder.createBlock("20091105-1131", "20091105-1200"));
		Assert.assertFalse(conflicts.contains(block));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddFree() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20091105-1100", "20091105-1130");
		
		VisibleSchedule schedule = new VisibleSchedule(MeetingDurations.THIRTY);
		schedule.addFreeBlock(block);
		
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091105-1100"), schedule.getScheduleStart());
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091105-1130"), schedule.getScheduleEnd());
		Assert.assertEquals(0, schedule.getAttendingCount());
		Assert.assertEquals(0, schedule.getBusyCount());
		Assert.assertEquals(1, schedule.getFreeCount());
		
		List<AvailableBlock> list = schedule.getFreeList();
		Assert.assertFalse(list.isEmpty());
		AvailableBlock returned = list.get(0);
		Assert.assertEquals(block, returned);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddFreeMultiple() throws Exception {
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091101"), 
				CommonDateOperations.parseDatePhrase("20091130"), 
				1);
		
		// reinitialize schedule, this time expand the blocks into 30 minute increments
		VisibleSchedule schedule = new VisibleSchedule(MeetingDurations.THIRTY);
		schedule.addFreeBlocks(blocks);
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091102-0900"), schedule.getScheduleStart());
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091130-1700"), schedule.getScheduleEnd());
		Assert.assertEquals(0, schedule.getAttendingCount());
		Assert.assertEquals(0, schedule.getBusyCount());
		Assert.assertEquals(208, schedule.getFreeCount());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddBusy() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20091105-1100", "20091105-1130");
		
		VisibleSchedule schedule = new VisibleSchedule(MeetingDurations.THIRTY);
		schedule.addFreeBlock(block);
		schedule.setBusyBlock(block);
		
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091105-1100"), schedule.getScheduleStart());
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091105-1130"), schedule.getScheduleEnd());
		Assert.assertEquals(0, schedule.getAttendingCount());
		Assert.assertEquals(0, schedule.getFreeCount());
		Assert.assertEquals(1, schedule.getBusyCount());
		
		List<AvailableBlock> list = schedule.getBusyList();
		Assert.assertFalse(list.isEmpty());
		AvailableBlock returned = list.get(0);
		Assert.assertEquals(block, returned);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddBusyMultiple() throws Exception {
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091101"), 
				CommonDateOperations.parseDatePhrase("20091130"), 
				1);
		
		// reinitialize schedule, this time expand the blocks into 30 minute increments
		VisibleSchedule schedule = new VisibleSchedule(MeetingDurations.THIRTY);
		schedule.addFreeBlocks(blocks);
		schedule.setBusyBlocks(blocks);
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091102-0900"), schedule.getScheduleStart());
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091130-1700"), schedule.getScheduleEnd());
		Assert.assertEquals(0, schedule.getAttendingCount());
		Assert.assertEquals(0, schedule.getFreeCount());
		Assert.assertEquals(208, schedule.getBusyCount());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddAttending() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20091105-1100", "20091105-1130");
		
		VisibleSchedule schedule = new VisibleSchedule(MeetingDurations.THIRTY);
		schedule.addFreeBlock(block);
		schedule.setAttendingBlock(block);
		
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091105-1100"), schedule.getScheduleStart());
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091105-1130"), schedule.getScheduleEnd());
		Assert.assertEquals(0, schedule.getBusyCount());
		Assert.assertEquals(0, schedule.getFreeCount());
		Assert.assertEquals(1, schedule.getAttendingCount());
		
		List<AvailableBlock> list = schedule.getAttendingList();
		Assert.assertFalse(list.isEmpty());
		AvailableBlock returned = list.get(0);
		Assert.assertEquals(block, returned);
	}
	
	/**
	 * Attending blocks
	 * @throws Exception
	 */
	@Test
	public void testAddAttendingMultiple() throws Exception {
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091101"), 
				CommonDateOperations.parseDatePhrase("20091130"), 
				1);
		
		VisibleSchedule schedule = new VisibleSchedule(MeetingDurations.THIRTY);
		schedule.addFreeBlocks(blocks);
		schedule.setAttendingBlocks(blocks);
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091102-0900"), schedule.getScheduleStart());
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091130-1700"), schedule.getScheduleEnd());
		Assert.assertEquals(0, schedule.getBusyCount());
		Assert.assertEquals(0, schedule.getFreeCount());
		Assert.assertEquals(13, schedule.getAttendingCount());
	}
	
	/**
	 * 
	 */
	@Test
	public void testOverwriteBlocks() throws Exception {
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091101"), 
				CommonDateOperations.parseDatePhrase("20091130"), 
				1);
		
		VisibleSchedule schedule = new VisibleSchedule(MeetingDurations.THIRTY);
		schedule.addFreeBlocks(blocks);
		
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091102-0900"), schedule.getScheduleStart());
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20091130-1700"), schedule.getScheduleEnd());
		Assert.assertEquals(0, schedule.getAttendingCount());
		Assert.assertEquals(0, schedule.getBusyCount());
		Assert.assertEquals(208, schedule.getFreeCount());
		
		// overwrite a block in the schedule with a BUSY block
		AvailableBlock busySingle = AvailableBlockBuilder.createBlock("20091102-1130", "20091102-1200");
		schedule.setBusyBlock(busySingle);
		Assert.assertEquals(0, schedule.getAttendingCount());
		Assert.assertEquals(1, schedule.getBusyCount());
		Assert.assertEquals(207, schedule.getFreeCount());
		
		// overwrite a block in the schedule with a ATTENDING block
		AvailableBlock busyAttending = AvailableBlockBuilder.createBlock("20091109-1100", "20091109-1130");
		schedule.setAttendingBlock(busyAttending);
		Assert.assertEquals(1, schedule.getAttendingCount());
		Assert.assertEquals(1, schedule.getBusyCount());
		Assert.assertEquals(206, schedule.getFreeCount());
		
		// overwrite several hours
		AvailableBlock busyMultiple = AvailableBlockBuilder.createBlock("20091125-0900", "20091125-1400");
		schedule.setBusyBlock(busyMultiple);
		Assert.assertEquals(1, schedule.getAttendingCount());
		Assert.assertEquals(11, schedule.getBusyCount());
		Assert.assertEquals(196, schedule.getFreeCount());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubset() throws Exception {
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20100801"), 
				CommonDateOperations.parseDatePhrase("20100828"), 
				1);
		
		VisibleSchedule schedule = new VisibleSchedule(MeetingDurations.THIRTY);
		schedule.addFreeBlocks(blocks);
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20100802-0900"), schedule.getScheduleStart());
		Assert.assertEquals(CommonDateOperations.parseDateTimePhrase("20100827-1700"), schedule.getScheduleEnd());
		Assert.assertEquals(0, schedule.getAttendingCount());
		Assert.assertEquals(0, schedule.getBusyCount());
		Assert.assertEquals(192, schedule.getFreeCount());
		
		// first test just with free blocks
		VisibleSchedule subset = schedule.subset(CommonDateOperations.parseDatePhrase("20100801"), CommonDateOperations.parseDatePhrase("20100807"));
		Assert.assertEquals(48, subset.getFreeCount());
		Assert.assertEquals(0, subset.getBusyCount());
		Assert.assertEquals(0, subset.getAttendingCount());
		subset = schedule.subset(CommonDateOperations.parseDatePhrase("20100803"), CommonDateOperations.parseDatePhrase("20100819"));
		Assert.assertEquals(112, subset.getFreeCount());
		Assert.assertEquals(0, subset.getBusyCount());
		Assert.assertEquals(0, subset.getAttendingCount());
		
		// add 1 busy to original schedule
		AvailableBlock busySingle = AvailableBlockBuilder.createBlock("20100806-1000", "20100806-1030");
		schedule.setBusyBlock(busySingle);
		Assert.assertEquals(0, schedule.getAttendingCount());
		Assert.assertEquals(1, schedule.getBusyCount());
		Assert.assertEquals(191, schedule.getFreeCount());
		
		// find subset that includes the busy
		subset = schedule.subset(CommonDateOperations.parseDatePhrase("20100801"), CommonDateOperations.parseDatePhrase("20100807"));
		Assert.assertEquals(47, subset.getFreeCount());
		Assert.assertEquals(1, subset.getBusyCount());
		Assert.assertEquals(0, subset.getAttendingCount());
		
		// verify subset not including busy 
		subset = schedule.subset(CommonDateOperations.parseDatePhrase("20100808"), CommonDateOperations.parseDatePhrase("20100814"));
		Assert.assertEquals(48, subset.getFreeCount());
		Assert.assertEquals(0, subset.getBusyCount());
		Assert.assertEquals(0, subset.getAttendingCount());
		
		// add 1 attending to original schedule
		AvailableBlock busyAttending = AvailableBlockBuilder.createBlock("20100818-1100", "20100818-1130");
		schedule.setAttendingBlock(busyAttending);
		Assert.assertEquals(1, schedule.getAttendingCount());
		Assert.assertEquals(1, schedule.getBusyCount());
		Assert.assertEquals(190, schedule.getFreeCount());
		
		// find subset including attending
		subset = schedule.subset(CommonDateOperations.parseDatePhrase("20100815"), CommonDateOperations.parseDatePhrase("20100821"));
		Assert.assertEquals(47, subset.getFreeCount());
		Assert.assertEquals(0, subset.getBusyCount());
		Assert.assertEquals(1, subset.getAttendingCount());
		
		// find subset including both
		subset = schedule.subset(CommonDateOperations.parseDatePhrase("20100801"), CommonDateOperations.parseDatePhrase("20100821"));
		Assert.assertEquals(1, subset.getAttendingCount());
		Assert.assertEquals(1, subset.getBusyCount());
		Assert.assertEquals(142, subset.getFreeCount());
	}
}
