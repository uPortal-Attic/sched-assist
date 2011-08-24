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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test bench for {@link AvailableBlockBuilder}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableBlockBuilderTest.java 2516 2010-09-09 18:32:23Z npblair $
 */
public class AvailableBlockBuilderTest {

	/**
	 * Validate IllegalArgumentException thrown for all invalid arguments to createBlock.
	 * @throws Exception
	 */
	@Test
	public void testCreateBlockInvalid() throws Exception {
		Date nullDate = null;
		try {
			AvailableBlockBuilder.createBlock(nullDate, nullDate, 1);
			fail("expected IllegalArgumentException not thrown for null startTime");
		} catch (IllegalArgumentException e) {
			//success
		}
		
		try {
			AvailableBlockBuilder.createBlock(new Date(), nullDate, 1);
			fail("expected IllegalArgumentException not thrown for null endTime");
		} catch (IllegalArgumentException e) {
			//success
		}
		
		Date now = new Date();
		try {
			AvailableBlockBuilder.createBlock(now, now, 1);
			fail("expected IllegalArgumentException not thrown for equivalent start/endtime");
		} catch (IllegalArgumentException e) {
			//success
		}
		
		try {
			AvailableBlockBuilder.createBlock(now, DateUtils.addMinutes(now, 1), 0);
			fail("expected IllegalArgumentException not thrown for visitorLimit less than 1");
		} catch (IllegalArgumentException e) {
			//success
		}
	}
	
	/**
	 * Every monday from June 1 2008 to July 1 2008.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateBlocksExample1() throws Exception {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("10:30 AM", 
				"1:00 PM", 
				"M", 
				dateFormat.parse("20080601"), 
				dateFormat.parse("20080701"));
		// there are 5 mondays in between June 1 2008 and July 1 2008
		assertEquals(5, blocks.size());	
		for(AvailableBlock block : blocks) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(block.getStartTime());
			assertEquals(10, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(30, cal.get(Calendar.MINUTE));
			cal.setTime(block.getEndTime());
			assertEquals(13, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, cal.get(Calendar.MINUTE));
			assertEquals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(1, block.getVisitorLimit());
		}
		
		blocks = AvailableBlockBuilder.createBlocks("10:30 AM", 
				"1:00 PM", 
				"M", 
				dateFormat.parse("20080601"), 
				dateFormat.parse("20080701"),
				5);
		// there are 5 mondays in between June 1 2008 and July 1 2008
		assertEquals(5, blocks.size());	
		for(AvailableBlock block : blocks) {
			assertEquals(150, block.getDurationInMinutes());
			Calendar cal = Calendar.getInstance();
			cal.setTime(block.getStartTime());
			assertEquals(10, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(30, cal.get(Calendar.MINUTE));
			cal.setTime(block.getEndTime());
			assertEquals(13, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, cal.get(Calendar.MINUTE));
			assertEquals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(5, block.getVisitorLimit());
		}
	}
	
	/**
	 * Every week day from June 24 2008 to August 6 2008.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateBlocksExample2() throws Exception {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		Date startDate = dateFormat.parse("20080624");
		Date endDate = dateFormat.parse("20080806");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", 
				"11:30 AM", 
				"MTWRF", 
				startDate, 
				endDate);
		// 32 weekdays between June 24 2008 and August 6 2008 (including Aug 6 2008)
		assertEquals(32, blocks.size());
		for(AvailableBlock block : blocks) {
			assertEquals(150, block.getDurationInMinutes());
			Calendar cal = Calendar.getInstance();
			cal.setTime(block.getStartTime());
			assertEquals(9, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, cal.get(Calendar.MINUTE));
			cal.setTime(block.getEndTime());
			assertEquals(11, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(30, cal.get(Calendar.MINUTE));
			assertNotSame(Calendar.SATURDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertNotSame(Calendar.SUNDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(1, block.getVisitorLimit());
		}
		
		blocks = AvailableBlockBuilder.createBlocks("9:00 AM", 
				"11:30 AM", 
				"MTWRF", 
				startDate, 
				endDate,
				64);
		// 32 weekdays between June 24 2008 and August 6 2008 (including Aug 6 2008)
		assertEquals(32, blocks.size());
		for(AvailableBlock block : blocks) {
			assertEquals(150, block.getDurationInMinutes());
			Calendar cal = Calendar.getInstance();
			cal.setTime(block.getStartTime());
			assertEquals(9, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, cal.get(Calendar.MINUTE));
			cal.setTime(block.getEndTime());
			assertEquals(11, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(30, cal.get(Calendar.MINUTE));
			assertNotSame(Calendar.SATURDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertNotSame(Calendar.SUNDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(64, block.getVisitorLimit());
		}
	}
	
	/** 
	 * Test times near midnight.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateBlocksExample3() throws Exception {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		Date startDate = dateFormat.parse("20080624");
		Date endDate = dateFormat.parse("20080630");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("12:00 AM", 
				"12:59 AM", 
				"MTWRF", 
				startDate, 
				endDate);
		
		assertEquals(5, blocks.size());
		for(AvailableBlock block : blocks) {
			assertEquals(59, block.getDurationInMinutes());
			Calendar cal = Calendar.getInstance();
			cal.setTime(block.getStartTime());
			assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, cal.get(Calendar.MINUTE));
			cal.setTime(block.getEndTime());
			assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(59, cal.get(Calendar.MINUTE));
			assertNotSame(Calendar.SATURDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertNotSame(Calendar.SUNDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(1, block.getVisitorLimit());
		}
		
		blocks = AvailableBlockBuilder.createBlocks("12:00 AM", 
				"12:59 AM", 
				"MTWRF", 
				startDate, 
				endDate,
				27);
		
		assertEquals(5, blocks.size());
		for(AvailableBlock block : blocks) {
			assertEquals(59, block.getDurationInMinutes());
			Calendar cal = Calendar.getInstance();
			cal.setTime(block.getStartTime());
			assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, cal.get(Calendar.MINUTE));
			cal.setTime(block.getEndTime());
			assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(59, cal.get(Calendar.MINUTE));
			assertNotSame(Calendar.SATURDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertNotSame(Calendar.SUNDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(27, block.getVisitorLimit());
		}
	}
	
	/**
	 * Create blocks that span noon.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateBlocksExample4() throws Exception {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		Date startDate = dateFormat.parse("20080601");
		Date endDate = dateFormat.parse("20080630");
		
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("11:45 AM", 
				"12:15 PM", 
				"MTWRF", 
				startDate, 
				endDate);
		assertEquals(21, blocks.size());
		for(AvailableBlock block : blocks) {
			assertEquals(30, block.getDurationInMinutes());
			Calendar cal = Calendar.getInstance();
			cal.setTime(block.getStartTime());
			assertEquals(11, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(45, cal.get(Calendar.MINUTE));
			cal.setTime(block.getEndTime());
			assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(15, cal.get(Calendar.MINUTE));
			assertNotSame(Calendar.SATURDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertNotSame(Calendar.SUNDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(1, block.getVisitorLimit());
		}
		
		blocks = AvailableBlockBuilder.createBlocks("11:45 AM", 
				"12:15 PM", 
				"MTWRF", 
				startDate, 
				endDate,
				2);
		assertEquals(21, blocks.size());
		for(AvailableBlock block : blocks) {
			assertEquals(30, block.getDurationInMinutes());
			Calendar cal = Calendar.getInstance();
			cal.setTime(block.getStartTime());
			assertEquals(11, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(45, cal.get(Calendar.MINUTE));
			cal.setTime(block.getEndTime());
			assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(15, cal.get(Calendar.MINUTE));
			assertNotSame(Calendar.SATURDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertNotSame(Calendar.SUNDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(2, block.getVisitorLimit());
		}
	}
	
	/**
	 * Create {@link AvailableBlock}s around daylight savings conversions, verify
	 * expected behavior.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateBlocksOverDaylightSavings() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20091101-0130", "20091101-0200");
		assertNotNull(block);
		assertEquals(30, block.getDurationInMinutes());
		
		block = AvailableBlockBuilder.createBlock("20091101-0200", "20091101-0230");
		assertNotNull(block);
		assertEquals(30, block.getDurationInMinutes());
		
		block = AvailableBlockBuilder.createBlock("20091101-0145", "20091101-0215");
		assertNotNull(block);
		assertEquals(30, block.getDurationInMinutes());
		
		block = AvailableBlockBuilder.createBlock("20090308-0130", "20090308-0200");
		assertNotNull(block);
		assertEquals(30, block.getDurationInMinutes());
		block = AvailableBlockBuilder.createBlock("20090308-0200", "20090308-0230");
		assertNotNull(block);
		assertEquals(30, block.getDurationInMinutes());
		block = AvailableBlockBuilder.createBlock("20090308-0145", "20090308-0215");
		assertNotNull(block);
		assertEquals(30, block.getDurationInMinutes());
	}
	
	/**
	 * Expand an {@link AvailableBlock} that was created with 9:00 AM and 3:00 PM as
	 * start and end times, respectively.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExpand() throws Exception {
		SimpleDateFormat dateTimeFormat = CommonDateOperations.getDateTimeFormat();
		Date startTime = dateTimeFormat.parse("20080601-0900");
		Date endTime = dateTimeFormat.parse("20080601-1500");
		
		AvailableBlock original = AvailableBlockBuilder.createBlock(startTime, endTime);
		assertNotNull(original);
		Set<AvailableBlock> expanded = AvailableBlockBuilder.expand(original, 30);
		assertEquals(12, expanded.size());
		
		Date currentStart = startTime;
		for(AvailableBlock block: expanded) {
			assertEquals(30, block.getDurationInMinutes());
			assertEquals(currentStart, block.getStartTime());
			currentStart = DateUtils.addMinutes(currentStart, 30);
			assertEquals(currentStart, block.getEndTime());
		}
	}
	
	/**
	 * Asserts expand called on an {@link AvailableBlock} from 9:00 AM to 9:30 AM returns
	 * only 1 block, equivalent to the original.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExpand2() throws Exception {
		SimpleDateFormat dateTimeFormat = CommonDateOperations.getDateTimeFormat();
		Date startTime = dateTimeFormat.parse("20080601-0900");
		Date endTime = dateTimeFormat.parse("20080601-0930");
		
		AvailableBlock original = AvailableBlockBuilder.createBlock(startTime, endTime);
		assertNotNull(original);
		Set<AvailableBlock> expanded = AvailableBlockBuilder.expand(original, 30);
		assertEquals(1, expanded.size());
		assertTrue(expanded.contains(original));
	}
	
	/**
	 * Assert expand function preserves original visitorLimit.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExpandPreserveVisitorLimit() throws Exception {
		SimpleDateFormat dateTimeFormat = CommonDateOperations.getDateTimeFormat();
		Date startTime = dateTimeFormat.parse("20080601-0900");
		Date endTime = dateTimeFormat.parse("20080601-1500");
		
		AvailableBlock toExpand = AvailableBlockBuilder.createBlock(startTime, endTime, 10);
		
		SortedSet<AvailableBlock> expanded20 = AvailableBlockBuilder.expand(toExpand, 20);
		assertEquals(18, expanded20.size());
		Date currentStart = startTime;
		for(AvailableBlock block: expanded20) {
			assertEquals(20, block.getDurationInMinutes());
			assertEquals(10, block.getVisitorLimit());
			assertEquals(currentStart, block.getStartTime());
			currentStart = DateUtils.addMinutes(currentStart, 20);
			assertEquals(currentStart, block.getEndTime());
		}
		
		SortedSet<AvailableBlock> expanded40 = AvailableBlockBuilder.expand(toExpand, 40);
		assertEquals(9, expanded40.size());
		currentStart = startTime;
		for(AvailableBlock block: expanded40) {
			assertEquals(40, block.getDurationInMinutes());
			assertEquals(10, block.getVisitorLimit());
			assertEquals(currentStart, block.getStartTime());
			currentStart = DateUtils.addMinutes(currentStart, 40);
			assertEquals(currentStart, block.getEndTime());
		}
		
		SortedSet<AvailableBlock> expanded60 = AvailableBlockBuilder.expand(toExpand, 60);
		assertEquals(6, expanded60.size());
		currentStart = startTime;
		for(AvailableBlock block: expanded60) {
			assertEquals(60, block.getDurationInMinutes());
			assertEquals(10, block.getVisitorLimit());
			assertEquals(currentStart, block.getStartTime());
			currentStart = DateUtils.addMinutes(currentStart, 60);
			assertEquals(currentStart, block.getEndTime());
		}
	}
	
	/**
	 * Create a series of (combined) blocks. 
	 * Expand that series, then re-combined.
	 * Assert the re-combined set matches the original set.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCombine() throws Exception {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		Date startDate = dateFormat.parse("20080624");
		Date endDate = dateFormat.parse("20080806");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", 
				"11:30 AM", 
				"MTWRF", 
				startDate, 
				endDate);
		
		// 32 weekdays between June 24 2008 and August 6 2008
		assertEquals(32, blocks.size());
		
		SortedSet<AvailableBlock> expandedBlocks = AvailableBlockBuilder.expand(blocks, 30);
		assertEquals(160, expandedBlocks.size());
		
		Set<AvailableBlock> recombinedBlocks = AvailableBlockBuilder.combine(expandedBlocks);
		Iterator<AvailableBlock> originalIterator = blocks.iterator();
		Iterator<AvailableBlock> recombinedIterator = recombinedBlocks.iterator();
		for(int i = 0; i < 32; i++) {
			assertEquals(originalIterator.next(), recombinedIterator.next());
		}
		
		assertEquals(32, recombinedBlocks.size());
	}
	
	/** 
	 * Create 1 years worth of (combined) blocks.
	 * Expand the set.
	 * Re-combine the expanded set, assert matches original set.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCombine2() throws Exception {		
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		Date startDate = dateFormat.parse("20080101");
		Date endDate = dateFormat.parse("20081231");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("12:00 AM", 
				"11:30 PM", 
				"NMTWRFS", 
				startDate, 
				endDate);
		
		// 366 days in 2008
		assertEquals(366, blocks.size());
		
		SortedSet<AvailableBlock> expandedBlocks = AvailableBlockBuilder.expand(blocks, 30);
		assertEquals(17202, expandedBlocks.size());
		
		Set<AvailableBlock> recombinedBlocks = AvailableBlockBuilder.combine(expandedBlocks);
		Iterator<AvailableBlock> originalIterator = blocks.iterator();
		Iterator<AvailableBlock> recombinedIterator = recombinedBlocks.iterator();
		for(int i = 0; i < 366; i++) {
			assertEquals(originalIterator.next(), recombinedIterator.next());
		}
		
		assertEquals(366, recombinedBlocks.size());
	}
	
	/**
	 * Create 50 years worth of (combined) blocks.
	 * Expand the set.
	 * Re-combine the expanded set, assert matches original set.
	 * 
	 * @throws Exception
	 */
	//@Test
	public void testCombine50Years() throws Exception {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		Date startDate = dateFormat.parse("20080101");
		Date endDate = dateFormat.parse("20581231");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("12:00 AM", 
				"11:30 PM", 
				"NMTWRFS", 
				startDate, 
				endDate);
		
		assertEquals(18628, blocks.size());
		
		SortedSet<AvailableBlock> expandedBlocks = AvailableBlockBuilder.expand(blocks, 30);
		assertEquals(875516, expandedBlocks.size());
		
		Set<AvailableBlock> recombinedBlocks = AvailableBlockBuilder.combine(expandedBlocks);
		Iterator<AvailableBlock> originalIterator = blocks.iterator();
		Iterator<AvailableBlock> recombinedIterator = recombinedBlocks.iterator();
		for(int i = 0; i < 18628; i++) {
			assertEquals(originalIterator.next(), recombinedIterator.next());
		}
		
		assertEquals(18628, recombinedBlocks.size());
	}
	
	/**
	 * Create a set of blocks with createBlocks.
	 * expand, then remove some of the expanded elements.
	 * call combine and verify blocks are created properly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCombine3() throws Exception {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		Date startDate = dateFormat.parse("20080720");
		Date endDate = dateFormat.parse("20080726");
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("8:00 AM", 
				"4:00 PM", 
				"MWF", 
				startDate, 
				endDate);
		
		// Mon/Wed/Fri from July 20 to July 26
		// 3 days in this span
		assertEquals(3, blocks.size());
		// 16 blocks per day (8 hours)
		SortedSet<AvailableBlock> expandedBlocks = AvailableBlockBuilder.expand(blocks, 30);
		assertEquals(48, expandedBlocks.size());
		
		// remove 12:00 to 12:30 on Wednesday
		expandedBlocks.remove(AvailableBlockBuilder.createBlock("20080723-1200", "20080723-1230"));
		
		assertEquals(47, expandedBlocks.size());
		
		SortedSet<AvailableBlock> recombinedBlocks = AvailableBlockBuilder.combine(expandedBlocks);
		assertEquals(4, recombinedBlocks.size());
		
		SortedSet<AvailableBlock> reexpandedBlocks = AvailableBlockBuilder.expand(recombinedBlocks, 30);
		assertEquals(47, reexpandedBlocks.size());
		
		// remove 3:30 to 4:00 on Friday
		reexpandedBlocks.remove(AvailableBlockBuilder.createBlock("20080725-1530", "20080725-1600"));
		assertEquals(46, reexpandedBlocks.size());
		// recombining shouldn't change the size; we're just trimming the length of the last large block
		assertEquals(4, AvailableBlockBuilder.combine(reexpandedBlocks).size());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCombine15s() throws Exception {
		SimpleDateFormat timeFormat = CommonDateOperations.getDateTimeFormat();
		Date time1 = timeFormat.parse("20090211-1100");
		
		Date time2 = timeFormat.parse("20090211-1105");
		AvailableBlock block1 = AvailableBlockBuilder.createSmallestAllowedBlock(time1);
		assertEquals(time2, block1.getEndTime());
		
		AvailableBlock block2 = AvailableBlockBuilder.createSmallestAllowedBlock(time2);
		SortedSet<AvailableBlock> smallBlocks = new TreeSet<AvailableBlock>();
		smallBlocks.add(block1);
		smallBlocks.add(block2);
		
		Set<AvailableBlock> combined1 = AvailableBlockBuilder.combine(smallBlocks);
		assertEquals(1, combined1.size());
		AvailableBlock expected = AvailableBlockBuilder.createBlock(block1.getStartTime(), block2.getEndTime());
		assertTrue(combined1.contains(expected));
	}
	
	/**
	 * Create a regular schedule and add a single 15 minute outlier.
	 * Combine and verify the outlier is not lost.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCombinePreservesOutliersSmall() throws Exception {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		SimpleDateFormat timeFormat = CommonDateOperations.getDateTimeFormat();
		Set<AvailableBlock> regularSchedule = AvailableBlockBuilder.createBlocks("9:00 AM", 
				"3:00 PM", 
				"MWF", 
				dateFormat.parse("20090309"), 
				dateFormat.parse("20090313"));
		
		assertEquals(3, regularSchedule.size());
		SortedSet<AvailableBlock> regularScheduleExpanded = AvailableBlockBuilder.expand(regularSchedule, 15);
		assertEquals(72, regularScheduleExpanded.size());
		Set<AvailableBlock> assertCombine = AvailableBlockBuilder.combine(regularScheduleExpanded);
		assertEquals(3, assertCombine.size());
		
		AvailableBlock outlier = AvailableBlockBuilder.createBlock(
				timeFormat.parse("20090312-1200"), 
				timeFormat.parse("20090312-1215"));
		
		regularScheduleExpanded.add(outlier);
		assertEquals(73, regularScheduleExpanded.size());
		
		Set<AvailableBlock> recombined = AvailableBlockBuilder.combine(regularScheduleExpanded);
		assertEquals(4, recombined.size());
		
		assertTrue(recombined.contains(outlier));
	}
	/**
	 * Create a regular schedule and add a single 15 minute outlier.
	 * Combine and verify the outlier is not lost.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCombinePreservesOutliersLarge() throws Exception {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		SimpleDateFormat timeFormat = CommonDateOperations.getDateTimeFormat();
		Set<AvailableBlock> regularSchedule = AvailableBlockBuilder.createBlocks("9:00 AM", 
				"3:00 PM", 
				"MWF", 
				dateFormat.parse("20090101"), 
				dateFormat.parse("20090601"));
		
		assertEquals(65, regularSchedule.size());
		SortedSet<AvailableBlock> regularScheduleExpanded = AvailableBlockBuilder.expand(regularSchedule, 15);
		assertEquals(1560, regularScheduleExpanded.size());
		Set<AvailableBlock> assertCombine = AvailableBlockBuilder.combine(regularScheduleExpanded);
		assertEquals(65, assertCombine.size());
		
		AvailableBlock outlier = AvailableBlockBuilder.createBlock(
				timeFormat.parse("20090312-1200"), 
				timeFormat.parse("20090312-1215"));
		
		regularScheduleExpanded.add(outlier);
		assertEquals(1561, regularScheduleExpanded.size());
		
		Set<AvailableBlock> recombined = AvailableBlockBuilder.combine(regularScheduleExpanded);
		assertEquals(66, recombined.size());
		
		assertTrue(recombined.contains(outlier));
	}
	
	/**
	 * Create 2 adjacent blocks visitor limit set to 10.
	 * Pass them into combine, assert they come out 1 combined block.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCombineVisitorLimit10() throws Exception {
		AvailableBlock block1 = AvailableBlockBuilder.createBlock("20091007-1200", "20091007-1230", 10);
		assertEquals(30, block1.getDurationInMinutes());
		AvailableBlock block2 = AvailableBlockBuilder.createBlock("20091007-1230", "20091007-1300", 10);
		assertEquals(30, block2.getDurationInMinutes());
		SortedSet<AvailableBlock> smallBlocks = new TreeSet<AvailableBlock>();
		smallBlocks.add(block1);
		smallBlocks.add(block2);
		SortedSet<AvailableBlock> resultCombined = AvailableBlockBuilder.combine(smallBlocks);
		assertEquals(resultCombined.size(), 1);
		AvailableBlock expectedCombined = AvailableBlockBuilder.createBlock("20091007-1200", "20091007-1300", 10);
		assertTrue(resultCombined.contains(expectedCombined));
		assertEquals(60, expectedCombined.getDurationInMinutes());
	}
	/**
	 * Create 2 adjacent blocks with different visitor limits.
	 * Pass them into combine, assert they come out as 2 separate blocks.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCombineMismatchedVisitorLimit() throws Exception {
		AvailableBlock block1 = AvailableBlockBuilder.createBlock("20091007-1200", "20091007-1230", 10);
		AvailableBlock block2 = AvailableBlockBuilder.createBlock("20091007-1230", "20091007-1300", 9);
		SortedSet<AvailableBlock> smallBlocks = new TreeSet<AvailableBlock>();
		smallBlocks.add(block1);
		smallBlocks.add(block2);
		SortedSet<AvailableBlock> resultCombined = AvailableBlockBuilder.combine(smallBlocks);
		assertEquals(resultCombined.size(), 2);
		assertTrue(resultCombined.contains(block1));
		assertTrue(resultCombined.contains(block2));
	}
	
	/**
	 * Create 2 adjacent blocks with different meeting locations.
	 * Pass them into combine, assert they come out as 2 separate blocks.
	 * @throws Exception
	 */
	@Test
	public void testCombineMismatchedMeetingLocation() throws Exception {
		AvailableBlock block1 = AvailableBlockBuilder.createBlock("20091007-1200", "20091007-1230", 1, "alternate location");
		AvailableBlock block2 = AvailableBlockBuilder.createBlock("20091007-1230", "20091007-1300", 1, null);
		SortedSet<AvailableBlock> smallBlocks = new TreeSet<AvailableBlock>();
		smallBlocks.add(block1);
		smallBlocks.add(block2);
		SortedSet<AvailableBlock> resultCombined = AvailableBlockBuilder.combine(smallBlocks);
		assertEquals(resultCombined.size(), 2);
		assertTrue(resultCombined.contains(block1));
		assertTrue(resultCombined.contains(block2));
	}
	
	/**
	 * Test the overloaded createSmallestAllowedBlock methods.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateSmallestAllowedBlock() throws Exception {
		Date date = CommonDateOperations.parseDateTimePhrase("20091103-1500");
		AvailableBlock block = AvailableBlockBuilder.createSmallestAllowedBlock(date);
		assertEquals(date, block.getStartTime());
		assertEquals(DateUtils.addMinutes(date, AvailableBlockBuilder.MINIMUM_MINUTES), block.getEndTime());
		assertEquals(1, block.getVisitorLimit());
		
		block = AvailableBlockBuilder.createSmallestAllowedBlock(date, 10);
		assertEquals(date, block.getStartTime());
		assertEquals(DateUtils.addMinutes(date, AvailableBlockBuilder.MINIMUM_MINUTES), block.getEndTime());
		assertEquals(10, block.getVisitorLimit());
		
		block = AvailableBlockBuilder.createSmallestAllowedBlock("20091103-1500");
		assertEquals(date, block.getStartTime());
		assertEquals(DateUtils.addMinutes(date, AvailableBlockBuilder.MINIMUM_MINUTES), block.getEndTime());
		assertEquals(1, block.getVisitorLimit());
	}
	
	/**
	 * @throws ParseException 
	 * @throws InputFormatException 
	 * 
	 */
	@Test
	public void testTwentyMinuteDuration() throws InputFormatException, ParseException {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "11:40 AM", "MW", dateFormat.parse("20100830"), dateFormat.parse("20100903"));
		Assert.assertEquals(2, blocks.size());
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100830-0900"), blocks.first().getStartTime());
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100830-1140"), blocks.first().getEndTime());
		
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100901-0900"), blocks.last().getStartTime());
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100901-1140"), blocks.last().getEndTime());
		
		SortedSet<AvailableBlock> expanded = AvailableBlockBuilder.expand(blocks, 20);
		Assert.assertEquals(16, expanded.size());
		
		Date originalStart = CommonDateOperations.getDateTimeFormat().parse("20100830-0900");
		
		Date currentStart = originalStart;
		for(AvailableBlock e: expanded) {
			if(!DateUtils.isSameDay(e.getStartTime(), currentStart)){
				currentStart = DateUtils.addDays(originalStart, 2);
			} 
			Assert.assertEquals(currentStart, e.getStartTime());
			currentStart = DateUtils.addMinutes(currentStart, 20);
			Assert.assertEquals(currentStart, e.getEndTime());
		}
	}
	
	/**
	 * Test that highlights the problem when a customer specifies a start/end range
	 * for blocks and a meeting duration that leaves a remainder.
	 * 
	 * @throws ParseException 
	 * @throws InputFormatException 
	 */
	@Test
	public void testDurationLeavesRemainder() throws InputFormatException, ParseException {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:03 AM", "3:19 PM", "MWF", 
				dateFormat.parse("20100830"), dateFormat.parse("20100903"));
		Assert.assertEquals(3, blocks.size());
		
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100830-0903"), blocks.first().getStartTime());
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100830-1519"), blocks.first().getEndTime());
		
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100903-0903"), blocks.last().getStartTime());
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100903-1519"), blocks.last().getEndTime());
		
		SortedSet<AvailableBlock> expanded = AvailableBlockBuilder.expand(blocks, 17);
		AvailableBlock last = null;
		
		long millisIn17Minutes = 1020000L;
		for(AvailableBlock e: expanded) {
			if(last != null) {
				if(DateUtils.isSameDay(e.getStartTime(), last.getStartTime())) {
					Assert.assertEquals(last.getEndTime(), e.getStartTime());
					Assert.assertEquals(e.getEndTime().getTime() - last.getEndTime().getTime(), millisIn17Minutes);
				} else {
					Calendar cal = Calendar.getInstance();
					cal.setTime(e.getStartTime());
					Assert.assertEquals(9, cal.get(Calendar.HOUR_OF_DAY));
					Assert.assertEquals(3, cal.get(Calendar.MINUTE));
					
					cal.setTime(e.getEndTime());
					Assert.assertEquals(9, cal.get(Calendar.HOUR_OF_DAY));
					Assert.assertEquals(20, cal.get(Calendar.MINUTE));
					
					// double check yesterday's endpoint
					cal.setTime(last.getStartTime());
					Assert.assertEquals(15, cal.get(Calendar.HOUR_OF_DAY));
					Assert.assertEquals(0, cal.get(Calendar.MINUTE));
					
					cal.setTime(last.getEndTime());
					Assert.assertEquals(15, cal.get(Calendar.HOUR_OF_DAY));
					Assert.assertEquals(17, cal.get(Calendar.MINUTE));
				}
			} else {
				// first block in the series
				Calendar cal = Calendar.getInstance();
				cal.setTime(e.getStartTime());
				Assert.assertEquals(9, cal.get(Calendar.HOUR_OF_DAY));
				Assert.assertEquals(3, cal.get(Calendar.MINUTE));
				
				cal.setTime(e.getEndTime());
				Assert.assertEquals(9, cal.get(Calendar.HOUR_OF_DAY));
				Assert.assertEquals(20, cal.get(Calendar.MINUTE));
			}
			last = e;
		}
		
		Assert.assertEquals(66, expanded.size());
		
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100830-0903"), expanded.first().getStartTime());
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100830-0920"), expanded.first().getEndTime());
		
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100903-1500"), expanded.last().getStartTime());
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100903-1517"), expanded.last().getEndTime());
	
	}
	
	@Test
	public void testMeetingLocationEffectHashCodeEquals() throws InputFormatException {
		AvailableBlock blockNoLocation = AvailableBlockBuilder.createBlock("20110810-0900", "20110810-0930");
		AvailableBlock blockWithLocation = AvailableBlockBuilder.createBlock("20110810-0900", "20110810-0930", 1, "different location");
	
		Assert.assertEquals(blockNoLocation, blockWithLocation);
		Assert.assertEquals(blockNoLocation.hashCode(), blockWithLocation.hashCode());
	}
	
	@Test
	public void textExpandPreserveLocation() throws InputFormatException, ParseException {
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "12:00 PM", "MWF", 
				CommonDateOperations.getDateFormat().parse("20110801"),
				CommonDateOperations.getDateFormat().parse("20110831"), 
				1,
				"alternate location");
		
		for(AvailableBlock block: blocks) {
			Assert.assertEquals("alternate location", block.getMeetingLocation());
		}
	}
	
	/**
	 * Create adjacent available blocks but set different meeting locations.
	 * Confirm blocks not combined by combine method.
	 * @throws InputFormatException 
	 * 
	 */
	@Test
	public void testDifferentMeetingLocationNotCombined() throws InputFormatException {
		AvailableBlock blockNoLocation = AvailableBlockBuilder.createBlock("20110810-0900", "20110810-0930", 1, "some location");
		AvailableBlock blockWithLocation = AvailableBlockBuilder.createBlock("20110810-0930", "20110810-1000", 1, "different location");
	
		SortedSet<AvailableBlock> smallBlocks = new TreeSet<AvailableBlock>();
		smallBlocks.add(blockNoLocation);
		smallBlocks.add(blockWithLocation);
		
		SortedSet<AvailableBlock> combined = AvailableBlockBuilder.combine(smallBlocks);
		Assert.assertEquals(2, combined.size());
		Assert.assertEquals(blockNoLocation, combined.first());
		Assert.assertEquals(blockWithLocation, combined.last());
	}
	
	/**
	 * 
	 * @throws InputFormatException
	 */
	@Test
	public void testSafeMeetingLocationEquals() throws InputFormatException {
		AvailableBlock blockNoLocation = AvailableBlockBuilder.createBlock("20110810-0900", "20110810-0930", 1);
		AvailableBlock blockNoLocation2 = AvailableBlockBuilder.createBlock("20110810-0900", "20110810-0930", 1);
		Assert.assertTrue(AvailableBlockBuilder.safeMeetingLocationEquals(blockNoLocation, blockNoLocation2));
		AvailableBlock blockWithLocation = AvailableBlockBuilder.createBlock("20110810-0930", "20110810-1000", 1, "different location");
		Assert.assertFalse(AvailableBlockBuilder.safeMeetingLocationEquals(blockNoLocation, blockWithLocation));
		AvailableBlock blockWithLocation2 = AvailableBlockBuilder.createBlock("20110810-0930", "20110810-1000", 1, "different location");
		Assert.assertTrue(AvailableBlockBuilder.safeMeetingLocationEquals(blockWithLocation, blockWithLocation2));
	}
}
