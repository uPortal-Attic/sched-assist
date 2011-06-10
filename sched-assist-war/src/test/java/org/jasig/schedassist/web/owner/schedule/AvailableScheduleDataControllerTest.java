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
	public void testConvertBlockControl() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20090408-0800", "20090408-0815");
		String blockFormatted = AvailableScheduleDataController.convertBlock(block);
		Assert.assertEquals("Wed0800 x 1 x 1", blockFormatted);
	}
	/**
	 * Test a 15 minute block.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvertBlockControlVisitorLimit() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20090408-0800", "20090408-0815", 10);
		String blockFormatted = AvailableScheduleDataController.convertBlock(block);
		Assert.assertEquals("Wed0800 x 1 x 10", blockFormatted);
	}
	
	/**
	 * Test a 4 hour long block.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test4HourBlock() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20090408-0900", "20090408-1300");
		String blockFormatted = AvailableScheduleDataController.convertBlock(block);
		Assert.assertEquals("Wed0900 x 16 x 1", blockFormatted);
	}
	
	/**
	 * Test a 4 hour long block.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test4HourBlockVisitorLimit() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20090408-0900", "20090408-1300", 10);
		String blockFormatted = AvailableScheduleDataController.convertBlock(block);
		Assert.assertEquals("Wed0900 x 16 x 10", blockFormatted);
	}
	
	/**
	 * Test an 18 hour long block.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test18HourBlock() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20090408-0500", "20090408-2300");
		String blockFormatted = AvailableScheduleDataController.convertBlock(block);
		Assert.assertEquals("Wed0500 x 72 x 1", blockFormatted);
	}
	
	/**
	 * Test an 18 hour long block.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test18HourBlockVisitorLimit() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20090408-0500", "20090408-2300", 5);
		String blockFormatted = AvailableScheduleDataController.convertBlock(block);
		Assert.assertEquals("Wed0500 x 72 x 5", blockFormatted);
	}
	
	/**
	 * Test a block that doesn't start on a multiple of 15.
	 * See https://jira.doit.wisc.edu/jira/browse/AVAIL-106
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBlockStartsOffThe15s() throws Exception {
		AvailableBlock block = AvailableBlockBuilder.createBlock("20100830-1320", "20100830-1600", 1);
		String blockFormatted = AvailableScheduleDataController.convertBlock(block);
		Assert.assertEquals("Mon1315 x 11 x 1", blockFormatted);
	}
	
}
