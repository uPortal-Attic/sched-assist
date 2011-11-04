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

package org.jasig.schedassist.impl.owner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.MeetingDurations;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test harness for {@link SpringJDBCAvailableScheduleDaoImpl}.
 * 
 * Depends on spring configuration in database-test.xml (on classpath, src/test/resources).
 * 
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SpringJDBCAvailableScheduleDaoImplTest.java 2536 2010-09-13 15:53:56Z npblair $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:database-test.xml"})
public class SpringJDBCAvailableScheduleDaoImplTest extends
		AbstractJUnit4SpringContextTests {

	private SpringJDBCAvailableScheduleDaoImpl availableScheduleDao;
	private IScheduleOwner[] sampleOwners = new IScheduleOwner[5];
	
	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(
			SpringJDBCAvailableScheduleDaoImpl availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRetrieveEmpty() throws Exception {
		for(int i = 0; i < sampleOwners.length; i++) {
			AvailableSchedule schedule = availableScheduleDao.retrieve(sampleOwners[i]);
			Assert.assertEquals(0, schedule.getAvailableBlocks().size());
		}
	}
	/**
	 * Make sure no exceptions are thrown when calling clear on empty schedules
	 * @throws Exception
	 */
	@Test
	public void testClearEmpty() throws Exception {
		for(int i = 0; i < sampleOwners.length; i++) {
			availableScheduleDao.clearAllBlocks(sampleOwners[i]);
		}
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddToSchedule() throws Exception {
		AvailableBlock single = AvailableBlockBuilder.createBlock("20091102-1330", "20091102-1400");
		AvailableSchedule schedule = availableScheduleDao.addToSchedule(sampleOwners[0], single);
		SortedSet<AvailableBlock> stored = schedule.getAvailableBlocks();
		Assert.assertTrue(stored.contains(single));
		
		schedule = availableScheduleDao.retrieve(sampleOwners[0]);
		stored = schedule.getAvailableBlocks();
		Assert.assertTrue(stored.contains(single));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddToSchedule10Visitors() throws Exception {
		AvailableBlock single = AvailableBlockBuilder.createBlock("20091102-1330", "20091102-1400", 10);
		AvailableSchedule schedule = availableScheduleDao.addToSchedule(sampleOwners[0], single);
		SortedSet<AvailableBlock> stored = schedule.getAvailableBlocks();
		Assert.assertTrue(stored.contains(single));
		
		schedule = availableScheduleDao.retrieve(sampleOwners[0]);
		stored = schedule.getAvailableBlocks();
		Assert.assertTrue(stored.contains(single));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddToScheduleOverrideMeetingLocation() throws Exception {
		AvailableBlock single = AvailableBlockBuilder.createBlock("20091102-1330", "20091102-1400", 1, "alternate location");
		AvailableSchedule schedule = availableScheduleDao.addToSchedule(sampleOwners[0], single);
		SortedSet<AvailableBlock> stored = schedule.getAvailableBlocks();
		Assert.assertTrue(stored.contains(single));
		
		schedule = availableScheduleDao.retrieve(sampleOwners[0]);
		stored = schedule.getAvailableBlocks();
		Assert.assertTrue(stored.contains(single));
		Assert.assertEquals(1, stored.size());
		Assert.assertEquals("alternate location", stored.first().getMeetingLocation());
	}
	
	/**
	 * 
	 * @throws InputFormatException
	 * @throws ParseException
	 */
	@Test
	public void testAddAdjacentBlocksCombined() throws InputFormatException, ParseException {
		Date start = CommonDateOperations.getDateFormat().parse("20110807");
		Date end =  CommonDateOperations.getDateFormat().parse("20110813");
		SortedSet<AvailableBlock> set1 = AvailableBlockBuilder.createBlocks("9:00 AM", "10:00 AM", "MWF", 
				start, end);
		
		availableScheduleDao.addToSchedule(sampleOwners[0], set1);
		
		SortedSet<AvailableBlock> set2 = AvailableBlockBuilder.createBlocks("10:00 AM", "11:00 AM", "MWF", 
				start, end);
		
		availableScheduleDao.addToSchedule(sampleOwners[0], set2);
		
		AvailableSchedule schedule = availableScheduleDao.retrieve(sampleOwners[0], start, end);
		Assert.assertEquals(3, schedule.getAvailableBlocks().size());
		for(AvailableBlock block : schedule.getAvailableBlocks()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(block.getStartTime());
			Assert.assertEquals(9, cal.get(Calendar.HOUR_OF_DAY));
			
			cal.setTime(block.getEndTime());
			Assert.assertEquals(11, cal.get(Calendar.HOUR_OF_DAY));
		}
	}
	@Test
	public void testAddAdjacentBlocksDifferentLocationNotCombined() throws InputFormatException, ParseException {
		Date start = CommonDateOperations.getDateFormat().parse("20110807");
		Date end =  CommonDateOperations.getDateFormat().parse("20110813");
		SortedSet<AvailableBlock> set1 = AvailableBlockBuilder.createBlocks("9:00 AM", "10:00 AM", "MWF", 
				start, end);
		
		availableScheduleDao.addToSchedule(sampleOwners[0], set1);
		
		SortedSet<AvailableBlock> set2 = AvailableBlockBuilder.createBlocks("10:00 AM", "11:00 AM", "MWF", 
				start, end, 1, "alternate location");
		
		availableScheduleDao.addToSchedule(sampleOwners[0], set2);
		
		AvailableSchedule schedule = availableScheduleDao.retrieve(sampleOwners[0], start, end);
		Assert.assertEquals(6, schedule.getAvailableBlocks().size());
		for(AvailableBlock block : schedule.getAvailableBlocks()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(block.getStartTime());
			if(block.getMeetingLocation() == null) {
				cal.setTime(block.getStartTime());
				Assert.assertEquals(9, cal.get(Calendar.HOUR_OF_DAY));
				cal.setTime(block.getEndTime());
				Assert.assertEquals(10, cal.get(Calendar.HOUR_OF_DAY));
			} else {
				cal.setTime(block.getStartTime());
				Assert.assertEquals(10, cal.get(Calendar.HOUR_OF_DAY));
				cal.setTime(block.getEndTime());
				Assert.assertEquals(11, cal.get(Calendar.HOUR_OF_DAY));
			}
		}
	}
	
	@Test
	public void testStoreBlocksWithLocationOverwriteExisting() throws InputFormatException, ParseException  {
		Date start = CommonDateOperations.getDateFormat().parse("20110807");
		Date end =  CommonDateOperations.getDateFormat().parse("20110813");
		SortedSet<AvailableBlock> set1 = AvailableBlockBuilder.createBlocks("9:00 AM", "4:00 PM", "MWF", 
				start, end);
		
		availableScheduleDao.addToSchedule(sampleOwners[0], set1);
		
		SortedSet<AvailableBlock> set2 = AvailableBlockBuilder.createBlocks("12:00 PM", "4:00 PM", "MWF", 
				start, end, 1, "alternate location");
		
		availableScheduleDao.addToSchedule(sampleOwners[0], set2);
		
		AvailableSchedule schedule = availableScheduleDao.retrieve(sampleOwners[0], start, end);
		Assert.assertEquals(6, schedule.getAvailableBlocks().size());
		for(AvailableBlock block : schedule.getAvailableBlocks()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(block.getStartTime());
			if(block.getMeetingLocation() == null) {
				cal.setTime(block.getStartTime());
				Assert.assertEquals(9, cal.get(Calendar.HOUR_OF_DAY));
				cal.setTime(block.getEndTime());
				Assert.assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
			} else {
				cal.setTime(block.getStartTime());
				Assert.assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
				cal.setTime(block.getEndTime());
				Assert.assertEquals(16, cal.get(Calendar.HOUR_OF_DAY));
			}
		}
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddRemoveScheduleMultiples() throws Exception {
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091102"), 
				CommonDateOperations.parseDatePhrase("20091127"));
		AvailableSchedule schedule = availableScheduleDao.addToSchedule(sampleOwners[0], blocks);
		SortedSet<AvailableBlock> stored = schedule.getAvailableBlocks();
		Assert.assertEquals(12, stored.size());
		
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091102-0900", "20091102-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091104-0900", "20091104-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091106-0900", "20091106-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091109-0900", "20091109-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091111-0900", "20091111-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091113-0900", "20091113-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091116-0900", "20091116-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091118-0900", "20091118-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091120-0900", "20091120-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091123-0900", "20091123-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091125-0900", "20091125-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091127-0900", "20091127-1700")));

		schedule = availableScheduleDao.retrieve(sampleOwners[0]);
		stored = schedule.getAvailableBlocks();
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091102-0900", "20091102-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091104-0900", "20091104-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091106-0900", "20091106-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091109-0900", "20091109-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091111-0900", "20091111-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091113-0900", "20091113-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091116-0900", "20091116-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091118-0900", "20091118-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091120-0900", "20091120-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091123-0900", "20091123-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091125-0900", "20091125-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091127-0900", "20091127-1700")));
		
		
		// remove some blocks from the middle of a few days
		schedule = availableScheduleDao.removeFromSchedule(sampleOwners[0], 
				AvailableBlockBuilder.createBlock("20091111-1200", "20091111-1300"));
		stored = schedule.getAvailableBlocks();
		Assert.assertFalse(stored.contains(AvailableBlockBuilder.createBlock("20091111-0900", "20091111-1700")));
		Assert.assertFalse(stored.contains(AvailableBlockBuilder.createBlock("20091111-1200", "20091111-1300")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091111-0900", "20091111-1200")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091111-1300", "20091111-1700")));
		
		schedule = availableScheduleDao.removeFromSchedule(sampleOwners[0], 
				AvailableBlockBuilder.createBlock("20091116-0900", "20091116-1200"));
		stored = schedule.getAvailableBlocks();
		Assert.assertFalse(stored.contains(AvailableBlockBuilder.createBlock("20091116-0900", "20091116-1200")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091116-1200", "20091116-1700")));
		
		schedule = availableScheduleDao.removeFromSchedule(sampleOwners[0], 
				AvailableBlockBuilder.createBlock("20091127-1600", "20091127-1800"));
		stored = schedule.getAvailableBlocks();
		Assert.assertFalse(stored.contains(AvailableBlockBuilder.createBlock("20091127-0900", "20091127-1700")));
		Assert.assertTrue(stored.contains(AvailableBlockBuilder.createBlock("20091127-0900", "20091127-1600")));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRetrieveTargetBlock() throws Exception {
		// first week of November, all blocks have visitorLimit 1
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091102"), 
				CommonDateOperations.parseDatePhrase("20091106"), 1);
		availableScheduleDao.addToSchedule(sampleOwners[0], blocks);
		// second week of November, all blocks have visitorLimit 2
		blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091109"), 
				CommonDateOperations.parseDatePhrase("20091113"), 2);
		availableScheduleDao.addToSchedule(sampleOwners[0], blocks);
		// third week of November, all blocks have visitorLimit 4
		blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091116"), 
				CommonDateOperations.parseDatePhrase("20091120"), 4);
		availableScheduleDao.addToSchedule(sampleOwners[0], blocks);
		// fourth week of November, all blocks have visitorLimit 20
		blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091123"), 
				CommonDateOperations.parseDatePhrase("20091127"), 20);
		availableScheduleDao.addToSchedule(sampleOwners[0], blocks);
		
		// wrong owner, assert returns null
		AvailableBlock result = availableScheduleDao.retrieveTargetBlock(sampleOwners[1], CommonDateOperations.parseDateTimePhrase("20091102-0900"));
		Assert.assertNull(result);
		
		// right owner, but assert time outside stored schedule returns null
		result = availableScheduleDao.retrieveTargetBlock(sampleOwners[0], CommonDateOperations.parseDateTimePhrase("20091101-0900"));
		Assert.assertNull(result);
		result = availableScheduleDao.retrieveTargetBlock(sampleOwners[0], CommonDateOperations.parseDateTimePhrase("20091102-0830"));
		Assert.assertNull(result);
		result = availableScheduleDao.retrieveTargetBlock(sampleOwners[0], CommonDateOperations.parseDateTimePhrase("20091102-1700"));
		Assert.assertNull(result);
		
		// assert proper block for a number of times
		Date expectedStart = CommonDateOperations.parseDateTimePhrase("20091102-0900");
		Date expectedEnd = DateUtils.addMinutes(expectedStart, sampleOwners[0].getPreferredMeetingDurations().getMinLength());
		result = availableScheduleDao.retrieveTargetBlock(sampleOwners[0], expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		// assert retrieveTargetBlock variant with endTime expected behavior
		result = availableScheduleDao.retrieveTargetBlock(sampleOwners[0], expectedStart, expectedEnd);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		// try again with wrong end time
		result = availableScheduleDao.retrieveTargetBlock(sampleOwners[0], expectedStart, DateUtils.addMinutes(expectedEnd, -1));
		Assert.assertNull(result);
		
		expectedStart = CommonDateOperations.parseDateTimePhrase("20091102-1200");
		expectedEnd = DateUtils.addMinutes(expectedStart, sampleOwners[0].getPreferredMeetingDurations().getMinLength());
		result = availableScheduleDao.retrieveTargetBlock(sampleOwners[0], expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		
		// special case - this block butts up to the end, it'll only be minLength long
		expectedStart = CommonDateOperations.parseDateTimePhrase("20091102-1630");
		expectedEnd = DateUtils.addMinutes(expectedStart, sampleOwners[0].getPreferredMeetingDurations().getMinLength());
		result = availableScheduleDao.retrieveTargetBlock(sampleOwners[0], expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		
		expectedStart = CommonDateOperations.parseDateTimePhrase("20091109-1200");
		expectedEnd = DateUtils.addMinutes(expectedStart, sampleOwners[0].getPreferredMeetingDurations().getMinLength());
		result = availableScheduleDao.retrieveTargetBlock(sampleOwners[0], expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		
		expectedStart = CommonDateOperations.parseDateTimePhrase("20091116-1200");
		expectedEnd = DateUtils.addMinutes(expectedStart, sampleOwners[0].getPreferredMeetingDurations().getMinLength());
		result = availableScheduleDao.retrieveTargetBlock(sampleOwners[0], expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(4, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		
		expectedStart = CommonDateOperations.parseDateTimePhrase("20091123-1200");
		expectedEnd = DateUtils.addMinutes(expectedStart, sampleOwners[0].getPreferredMeetingDurations().getMinLength());
		result = availableScheduleDao.retrieveTargetBlock(sampleOwners[0], expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(20, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
	}
	
	/**
	 * Similar to {@link #testRetrieveTargetBlock()}, only exercises
	 * {@link SpringJDBCAvailableScheduleDaoImpl#retrieveTargetDoubleLengthBlock(IScheduleOwner, Date)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRetrieveDoubleLengthTargetBlock() throws Exception {
		// get owner with DURATIONS preference set to one that supports double length 
		IScheduleOwner owner = sampleOwners[4];
		
		// first week of November, all blocks have visitorLimit 1
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091102"), 
				CommonDateOperations.parseDatePhrase("20091106"), 1);
		availableScheduleDao.addToSchedule(owner, blocks);
		// second week of November, all blocks have visitorLimit 2
		blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091109"), 
				CommonDateOperations.parseDatePhrase("20091113"), 2);
		availableScheduleDao.addToSchedule(owner, blocks);
		// third week of November, all blocks have visitorLimit 4
		blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091116"), 
				CommonDateOperations.parseDatePhrase("20091120"), 4);
		availableScheduleDao.addToSchedule(owner, blocks);
		// fourth week of November, all blocks have visitorLimit 20
		blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "5:00 PM", "MWF", 
				CommonDateOperations.parseDatePhrase("20091123"), 
				CommonDateOperations.parseDatePhrase("20091127"), 20);
		availableScheduleDao.addToSchedule(owner, blocks);
		
		// wrong owner, assert returns null
		AvailableBlock result = availableScheduleDao.retrieveTargetDoubleLengthBlock(sampleOwners[1], CommonDateOperations.parseDateTimePhrase("20091102-0900"));
		Assert.assertNull(result);
		
		// right owner, but assert time outside stored schedule returns null
		result = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, CommonDateOperations.parseDateTimePhrase("20091101-0900"));
		Assert.assertNull(result);
		result = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, CommonDateOperations.parseDateTimePhrase("20091102-0830"));
		Assert.assertNull(result);
		result = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, CommonDateOperations.parseDateTimePhrase("20091102-1700"));
		Assert.assertNull(result);
		
		// assert proper block for a number of times
		Date expectedStart = CommonDateOperations.parseDateTimePhrase("20091102-0900");
		Date expectedEnd = DateUtils.addMinutes(expectedStart, owner.getPreferredMeetingDurations().getMaxLength());
		result = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		// assert retrieveTargetBlock with endTime argument can return the expected double length block
		result = availableScheduleDao.retrieveTargetBlock(owner, expectedStart, expectedEnd);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		
		expectedStart = CommonDateOperations.parseDateTimePhrase("20091102-0930");
		expectedEnd = DateUtils.addMinutes(expectedStart, owner.getPreferredMeetingDurations().getMaxLength());
		result = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		
		expectedStart = CommonDateOperations.parseDateTimePhrase("20091102-1200");
		expectedEnd = DateUtils.addMinutes(expectedStart, owner.getPreferredMeetingDurations().getMaxLength());
		result = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		
		// special case - this block butts up to the end
		// since we are asking for an end outside the schedule, should return null
		expectedStart = CommonDateOperations.parseDateTimePhrase("20091102-1630");
		expectedEnd = DateUtils.addMinutes(expectedStart, owner.getPreferredMeetingDurations().getMaxLength());
		result = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, expectedStart);
		Assert.assertNull(result);
		
		expectedStart = CommonDateOperations.parseDateTimePhrase("20091109-1200");
		expectedEnd = DateUtils.addMinutes(expectedStart, owner.getPreferredMeetingDurations().getMaxLength());
		result = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		
		expectedStart = CommonDateOperations.parseDateTimePhrase("20091116-1200");
		expectedEnd = DateUtils.addMinutes(expectedStart, owner.getPreferredMeetingDurations().getMaxLength());
		result = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(4, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
		
		expectedStart = CommonDateOperations.parseDateTimePhrase("20091123-1200");
		expectedEnd = DateUtils.addMinutes(expectedStart, owner.getPreferredMeetingDurations().getMaxLength());
		result = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, expectedStart);
		Assert.assertNotNull(result);
		Assert.assertEquals(20, result.getVisitorLimit());
		Assert.assertEquals(expectedStart, result.getStartTime());
		Assert.assertEquals(expectedEnd, result.getEndTime());
	}
	
	/**
	 * @throws ParseException 
	 * @throws InputFormatException 
	 * 
	 */
	@Test
	public void testAvailable104() throws InputFormatException, ParseException {
		// get owner with meeting durations preference of 20 minutes
		IScheduleOwner owner = sampleOwners[3];
		
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:00 AM", "11:40 AM", "MW", 
				dateFormat.parse("20100830"), dateFormat.parse("20100903"), 1);
		availableScheduleDao.addToSchedule(owner, blocks);
		
		AvailableSchedule stored = availableScheduleDao.retrieve(owner);
		SortedSet<AvailableBlock> storedBlocks = stored.getAvailableBlocks();
		Assert.assertEquals(2, storedBlocks.size());
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100830-0900"), storedBlocks.first().getStartTime());
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100830-1140"), storedBlocks.first().getEndTime());
		
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100901-0900"), storedBlocks.last().getStartTime());
		Assert.assertEquals(CommonDateOperations.getDateTimeFormat().parse("20100901-1140"), storedBlocks.last().getEndTime());
		
		SortedSet<AvailableBlock> expanded = AvailableBlockBuilder.expand(storedBlocks, 20);
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
	
	@Test
	public void testClearWeekUnevenSchedule() throws InputFormatException, ParseException {
		// get owner with meeting durations preference of 17 minutes
		IScheduleOwner owner = sampleOwners[2];
		
		SimpleDateFormat dateFormat = CommonDateOperations.getDateFormat();
		SortedSet<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks("9:03 AM", "3:19 PM", "MWF", 
				dateFormat.parse("20100830"), dateFormat.parse("20101001"), 1);
		availableScheduleDao.addToSchedule(owner, blocks);
		
		AvailableSchedule stored = availableScheduleDao.retrieve(owner);
		SortedSet<AvailableBlock> storedBlocks = stored.getAvailableBlocks();
		Assert.assertEquals(15, storedBlocks.size());
		
		SortedSet<AvailableBlock> expanded = AvailableBlockBuilder.expand(storedBlocks, 17);
		Assert.assertEquals(330, expanded.size());
		
		AvailableSchedule weekToRemove = availableScheduleDao.retrieveWeeklySchedule(owner, dateFormat.parse("20100912"));
		Assert.assertEquals(3, weekToRemove.getAvailableBlocks().size());
		Assert.assertEquals(66, AvailableBlockBuilder.expand(weekToRemove.getAvailableBlocks(), 17).size());
		availableScheduleDao.removeFromSchedule(owner, weekToRemove.getAvailableBlocks());
		
		AvailableSchedule storedAfterRemove = availableScheduleDao.retrieve(owner);
		Assert.assertEquals(12, storedAfterRemove.getAvailableBlocks().size());
	}
	
	/**
	 * Creates the database.
	 * Also pulls the {@link OwnerDao} from the configuration and registers a few 
	 * sample {@link IScheduleOwner}s.
	 * 
	 * @throws Exception
	 */
	@Before
	public void createDatabase() throws Exception {
		Resource createDdl = (Resource) this.applicationContext.getBean("createDdl");
		
		String sql = IOUtils.toString(createDdl.getInputStream());
		JdbcTemplate template = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		template.execute(sql);
		
		OwnerDao ownerDao = (OwnerDao) this.applicationContext.getBean("ownerDao");
		for(int i = 0; i < sampleOwners.length; i++) {
			
			MockCalendarAccount calendarAccount = new MockCalendarAccount();
			calendarAccount.setUsername("user"+i);
			calendarAccount.setCalendarUniqueId("10000:0000" + i);
			calendarAccount.setEmailAddress("email"+ i + "@domain.com");
			calendarAccount.setDisplayName("User Name" + i);
			sampleOwners[i] = ownerDao.register(calendarAccount);
		}
		// give 3rd owner a 17 minute meeting duration preference
		sampleOwners[2] = ownerDao.updatePreference(sampleOwners[3], Preferences.DURATIONS, MeetingDurations.fromKey("20").getKey());
		
		// give 4th owner a 20 minute meeting duration preference
		sampleOwners[3] = ownerDao.updatePreference(sampleOwners[3], Preferences.DURATIONS, MeetingDurations.fromKey("20").getKey());
		
		// give 5th owner the double length preference
		sampleOwners[4] = ownerDao.updatePreference(sampleOwners[4], Preferences.DURATIONS, MeetingDurations.THIRTY_SIXTY.getKey());
	}
	
	/**
	 * Destroy the database.
	 * 
	 * @throws Exception
	 */
	@After
	public void destroyDatabase() throws Exception {
		Resource destroyDdl = (Resource) this.applicationContext.getBean("destroyDdl");
		
		String sql = IOUtils.toString(destroyDdl.getInputStream());
		JdbcTemplate template = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		template.execute(sql);
	}
	
}
