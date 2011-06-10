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

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link VisibleScheduleRequestConstraints}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleScheduleRequestTest.java $
 */
public class VisibleScheduleRequestConstraintsTest {

	@Test
	public void testConstrainWeekStart() {
		VisibleWindow window = VisibleWindow.DEFAULT;
		Assert.assertEquals(0, VisibleScheduleRequestConstraints.constrainWeekStartToWindow(window, -100));
		Assert.assertEquals(0, VisibleScheduleRequestConstraints.constrainWeekStartToWindow(window, -1));
		Assert.assertEquals(0, VisibleScheduleRequestConstraints.constrainWeekStartToWindow(window, 0));
		Assert.assertEquals(1, VisibleScheduleRequestConstraints.constrainWeekStartToWindow(window, 1));
		Assert.assertEquals(3, VisibleScheduleRequestConstraints.constrainWeekStartToWindow(window, 3));
		Assert.assertEquals(3, VisibleScheduleRequestConstraints.constrainWeekStartToWindow(window, 4));
		Assert.assertEquals(3, VisibleScheduleRequestConstraints.constrainWeekStartToWindow(window, 10));
	}
	
	@Test 
	public void testResolveStartDate() {
		Date referencePoint = new Date();
		
		Assert.assertEquals(referencePoint, VisibleScheduleRequestConstraints.resolveStartDate(referencePoint, 0));
		Assert.assertEquals(referencePoint, VisibleScheduleRequestConstraints.resolveStartDate(referencePoint, -1));
		Assert.assertEquals(referencePoint, VisibleScheduleRequestConstraints.resolveStartDate(referencePoint, -100));
		Date upcomingSunday = DateUtils.addDays(referencePoint, CommonDateOperations.numberOfDaysUntilSunday(referencePoint));
		Assert.assertEquals(upcomingSunday, VisibleScheduleRequestConstraints.resolveStartDate(referencePoint, 1));
		Assert.assertEquals(DateUtils.addWeeks(upcomingSunday, 1), VisibleScheduleRequestConstraints.resolveStartDate(referencePoint, 2));
	}
	
	@Test 
	public void testResolveEndDate() {
		Date referencePoint = new Date();
		
		Assert.assertEquals(DateUtils.addWeeks(referencePoint, 1), VisibleScheduleRequestConstraints.resolveEndDate(VisibleWindow.fromKey("1,1"), referencePoint));
		Assert.assertEquals(DateUtils.addWeeks(referencePoint, 2), VisibleScheduleRequestConstraints.resolveEndDate(VisibleWindow.fromKey("1,2"), referencePoint));
		Assert.assertEquals(DateUtils.addWeeks(referencePoint, 3), VisibleScheduleRequestConstraints.resolveEndDate(VisibleWindow.DEFAULT, referencePoint));
		Assert.assertEquals(DateUtils.addWeeks(referencePoint, 4), VisibleScheduleRequestConstraints.resolveEndDate(VisibleWindow.fromKey("1,4"), referencePoint));
		Assert.assertEquals(DateUtils.addWeeks(referencePoint, 4), VisibleScheduleRequestConstraints.resolveEndDate(VisibleWindow.fromKey("1,5"), referencePoint));
		Assert.assertEquals(DateUtils.addWeeks(referencePoint, 4), VisibleScheduleRequestConstraints.resolveEndDate(VisibleWindow.fromKey("1,8"), referencePoint));
		Assert.assertEquals(DateUtils.addWeeks(referencePoint, 4), VisibleScheduleRequestConstraints.resolveEndDate(VisibleWindow.fromKey("1,26"), referencePoint));
	}
	
	@Test
	public void testCalculateNextWeekIndexDefaultWindow() {
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(VisibleWindow.DEFAULT, 0));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(VisibleWindow.DEFAULT, -1));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(VisibleWindow.DEFAULT, 1));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(VisibleWindow.DEFAULT, 2));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(VisibleWindow.DEFAULT, 3));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(VisibleWindow.DEFAULT, 10));
	}
	
	@Test
	public void testCalculatePrevWeekIndexDefaultWindow() {
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(VisibleWindow.DEFAULT, 0));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(VisibleWindow.DEFAULT, -1));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(VisibleWindow.DEFAULT, 1));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(VisibleWindow.DEFAULT, 2));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(VisibleWindow.DEFAULT, 3));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(VisibleWindow.DEFAULT, 10));
	}
	
	@Test
	public void testCalculateNextWeekIndex4Weeks() {
		VisibleWindow window = VisibleWindow.fromKey("1,4");
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 0));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, -1));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 1));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 2));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 3));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 4));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 10));
	}
	
	@Test
	public void testCalculatePrevWeekIndex4Weeks() {
		VisibleWindow window = VisibleWindow.fromKey("1,4");
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 0));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, -1));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 1));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 2));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 3));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 4));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 10));
	}
	
	@Test
	public void testCalculateNextWeekIndex5Weeks() {
		VisibleWindow window = VisibleWindow.fromKey("1,5");
		Assert.assertEquals(new Integer(4),VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 0));
		Assert.assertEquals(new Integer(4),VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, -1));
		Assert.assertEquals(new Integer(5),VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 1));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 2));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 3));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 4));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 10));
	}
	
	@Test
	public void testCalculatePrevWeekIndex5Weeks() {
		VisibleWindow window = VisibleWindow.fromKey("1,5");
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 0));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, -1));
		Assert.assertEquals(new Integer(0),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 1));
		Assert.assertEquals(new Integer(0),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 2));
		Assert.assertEquals(new Integer(0),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 3));
		Assert.assertEquals(new Integer(0),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 4));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 10));
	}
	
	@Test
	public void testCalculateNextWeekIndex8Weeks() {
		VisibleWindow window = VisibleWindow.fromKey("1,8");
		Assert.assertEquals(new Integer(4),VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 0));
		Assert.assertEquals(new Integer(4),VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, -1));
		Assert.assertEquals(new Integer(5),VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 1));
		Assert.assertEquals(new Integer(6),VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 2));
		Assert.assertEquals(new Integer(7),VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 3));
		Assert.assertEquals(new Integer(8),VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 4));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 10));
	}
	
	@Test
	public void testCalculatePrevWeekIndex8Weeks() {
		VisibleWindow window = VisibleWindow.fromKey("1,8");
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 0));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, -1));
		Assert.assertEquals(new Integer(0),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 1));
		Assert.assertEquals(new Integer(0),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 2));
		Assert.assertEquals(new Integer(0),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 3));
		Assert.assertEquals(new Integer(0),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 4));
		Assert.assertEquals(new Integer(1),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 5));
		Assert.assertEquals(new Integer(2),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 6));
		Assert.assertEquals(new Integer(3),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 7));
		Assert.assertEquals(new Integer(4),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 8));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 10));
	}
	
	@Test
	public void testCalculateNextWeekIndex26Weeks() {
		VisibleWindow window = VisibleWindow.fromKey("1,26");
		Assert.assertEquals(new Integer(4),VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, -1));
		for(int i = 0; i <= 22; i++) {
			Assert.assertEquals(new Integer(i+VisibleScheduleRequestConstraints.WEEKS_PER_PAGE),VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, i));
		}
		
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 23));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 24));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 25));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 26));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculateNextWeekIndex(window, 27));
	}
	
	@Test
	public void testCalculatePrevWeekIndex26Weeks() {
		VisibleWindow window = VisibleWindow.fromKey("1,26");
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 0));
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, -1));
		Assert.assertEquals(new Integer(0),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 1));
		Assert.assertEquals(new Integer(0),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 2));
		Assert.assertEquals(new Integer(0),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 3));
		
		for(int i = 4; i <= 26; i++) {
			Assert.assertEquals(new Integer(i-VisibleScheduleRequestConstraints.WEEKS_PER_PAGE),VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, i));
		}
		
		Assert.assertNull(VisibleScheduleRequestConstraints.calculatePrevWeekIndex(window, 27));
	}
}
