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


package org.jasig.schedassist.web.owner.schedule;

import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.web.owner.schedule.AvailableScheduleDataController.AvailableBlockJsonRepresentation;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test bench for static methods in {@link AvailableScheduleDataController}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableScheduleDataControllerTest.java 2424 2010-08-30 20:57:23Z npblair $
 */
public class AvailableScheduleDataControllerTest {

	/**
	 * Test a 15 minute block.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJsonRepresentationControl() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20090408-0800", "20090408-0815");
		AvailableBlockJsonRepresentation jsonBlock = new AvailableBlockJsonRepresentation(block);
		
		Assert.assertEquals("Wed0800", jsonBlock.getStartTime());
		Assert.assertEquals(1, jsonBlock.getDurationIn15Mins());
		Assert.assertEquals(1, jsonBlock.getVisitorLimit());
		Assert.assertNull(jsonBlock.getMeetingLocation());
	}
	/**
	 * Test a 15 minute block.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJsonRepresentationVisitorLimit() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20090408-0800", "20090408-0815", 10);
		AvailableBlockJsonRepresentation jsonBlock = new AvailableBlockJsonRepresentation(block);
		
		Assert.assertEquals("Wed0800", jsonBlock.getStartTime());
		Assert.assertEquals(1, jsonBlock.getDurationIn15Mins());
		Assert.assertEquals(10, jsonBlock.getVisitorLimit());
		Assert.assertNull(jsonBlock.getMeetingLocation());
	}
	
	/**
	 * Test a 4 hour long block.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test4HourBlock() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20090408-0900", "20090408-1300");
		AvailableBlockJsonRepresentation jsonBlock = new AvailableBlockJsonRepresentation(block);
		
		Assert.assertEquals("Wed0900", jsonBlock.getStartTime());
		Assert.assertEquals(16, jsonBlock.getDurationIn15Mins());
		Assert.assertEquals(1, jsonBlock.getVisitorLimit());
		Assert.assertNull(jsonBlock.getMeetingLocation());
	}
	
	/**
	 * Test an 18 hour long block.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test18HourBlock() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20090408-0500", "20090408-2300");
		AvailableBlockJsonRepresentation jsonBlock = new AvailableBlockJsonRepresentation(block);
		
		Assert.assertEquals("Wed0500", jsonBlock.getStartTime());
		Assert.assertEquals(72, jsonBlock.getDurationIn15Mins());
		Assert.assertEquals(1, jsonBlock.getVisitorLimit());
		Assert.assertNull(jsonBlock.getMeetingLocation());
	}
	
	
	/**
	 * Test a block that doesn't start on a multiple of 15.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBlockStartsOffThe15s() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20100830-1320", "20100830-1600", 1);
		AvailableBlockJsonRepresentation jsonBlock = new AvailableBlockJsonRepresentation(block);
		
		Assert.assertEquals("Mon1315", jsonBlock.getStartTime());
		Assert.assertEquals(11, jsonBlock.getDurationIn15Mins());
		Assert.assertEquals(1, jsonBlock.getVisitorLimit());
		Assert.assertNull(jsonBlock.getMeetingLocation());
	}
	
	@Test
	public void testMeetingLocation() throws InputFormatException {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20090408-0800", "20090408-0815", 1, "alternate location");
		AvailableBlockJsonRepresentation jsonBlock = new AvailableBlockJsonRepresentation(block);
		
		Assert.assertEquals("Wed0800", jsonBlock.getStartTime());
		Assert.assertEquals(1, jsonBlock.getDurationIn15Mins());
		Assert.assertEquals(1, jsonBlock.getVisitorLimit());
		Assert.assertEquals("alternate location", jsonBlock.getMeetingLocation());
	}
	
}
