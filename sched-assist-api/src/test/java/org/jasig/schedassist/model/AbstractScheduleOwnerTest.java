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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link AbstractScheduleOwner}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AbstractScheduleOwnerTest.java 2502 2010-09-08 14:27:02Z npblair $
 */
public class AbstractScheduleOwnerTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHasMeetingLimit() throws Exception {
		MockScheduleOwner o = new MockScheduleOwner(new MockCalendarAccount(), 1);
		Assert.assertFalse(o.hasMeetingLimit());
		o.setPreference(Preferences.MEETING_LIMIT, "1");
		Assert.assertTrue(o.hasMeetingLimit());
		o.setPreference(Preferences.MEETING_LIMIT, "50");
		Assert.assertTrue(o.hasMeetingLimit());
		o.setPreference(Preferences.MEETING_LIMIT, "-1");
		Assert.assertFalse(o.hasMeetingLimit());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIsExceedingMeetingLimit() throws Exception {
		MockScheduleOwner o = new MockScheduleOwner(new MockCalendarAccount(), 1);
		Assert.assertFalse(o.isExceedingMeetingLimit(0));
		Assert.assertFalse(o.isExceedingMeetingLimit(1));
		Assert.assertFalse(o.isExceedingMeetingLimit(10));
		
		o.setPreference(Preferences.MEETING_LIMIT, "1");
		Assert.assertFalse(o.isExceedingMeetingLimit(0));
		Assert.assertTrue(o.isExceedingMeetingLimit(1));
		Assert.assertTrue(o.isExceedingMeetingLimit(10));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetPreferredLocation() throws Exception {
		MockScheduleOwner o = new MockScheduleOwner(new MockCalendarAccount(), 1);
		Assert.assertEquals(Preferences.LOCATION.getDefaultValue(), o.getPreferredLocation());
		o.setPreference(Preferences.LOCATION, "my office in the building");
		Assert.assertEquals("my office in the building", o.getPreferredLocation());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetPreferredMinimum() throws Exception {
		MockScheduleOwner o = new MockScheduleOwner(new MockCalendarAccount(), 1);
		MeetingDurations defaultDurations = MeetingDurations.fromKey(Preferences.DURATIONS.getDefaultValue());
		Assert.assertEquals(defaultDurations.getMinLength(), o.getPreferredMinimumDuration());
		o.setPreference(Preferences.DURATIONS, MeetingDurations.FORTYFIVE.getKey());
		Assert.assertEquals(MeetingDurations.FORTYFIVE.getMinLength(), o.getPreferredMinimumDuration());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetPreferredDefaultVisitorLimit() throws Exception {
		MockScheduleOwner o = new MockScheduleOwner(new MockCalendarAccount(), 1);
		int defaultVisitorLimit = Integer.parseInt(Preferences.DEFAULT_VISITOR_LIMIT.getDefaultValue());
		Assert.assertEquals(defaultVisitorLimit, o.getPreferredDefaultVisitorLimit());
		o.setPreference(Preferences.DEFAULT_VISITOR_LIMIT, "10");
		Assert.assertEquals(10, o.getPreferredDefaultVisitorLimit());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIsReflectSchedule() throws Exception {
		MockScheduleOwner o = new MockScheduleOwner(new MockCalendarAccount(), 1);
		Assert.assertFalse(o.isReflectSchedule());
		o.setPreference(Preferences.REFLECT_SCHEDULE, "true");
		Assert.assertTrue(o.isReflectSchedule());
	}
}
