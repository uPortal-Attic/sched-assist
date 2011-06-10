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

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test bench for {@link CommonDateOperations}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CommonDateOperationsTest.java 2293 2010-07-27 16:53:55Z npblair $
 */
public class CommonDateOperationsTest {

	@Test
	public void testCalculateSundayPrior() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		Date testDate = df.parse("20090211-1119");
		Date expectedSundayPrior = df.parse("20090208-0000");
		Assert.assertEquals(expectedSundayPrior, CommonDateOperations.calculateSundayPrior(testDate));
	}
	
	/**
	 * Use a date right after DST begins (March 8 2009).
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateSundayPriorDSTBegin() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		Date testDate = df.parse("20090311-1119");
		Date expectedSundayPrior = df.parse("20090308-0000");
		Assert.assertEquals(expectedSundayPrior, CommonDateOperations.calculateSundayPrior(testDate));
	}
	
	/**
	 * Use a date right after DST ends (Nov 1 2009).
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateSundayAfterDSTEnd() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		Date testDate = df.parse("20091103-1119");
		Date expectedSundayPrior = df.parse("20091101-0000");
		Assert.assertEquals(expectedSundayPrior, CommonDateOperations.calculateSundayPrior(testDate));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParseDateTimePhrase() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		Date expectedDate = df.parse("20080614-1200");
		Assert.assertEquals(expectedDate, CommonDateOperations.parseDateTimePhrase("20080614-1200"));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParseDatePhrase() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateFormat();
		Date expectedDate = df.parse("20080614");
		Assert.assertEquals(expectedDate, CommonDateOperations.parseDatePhrase("20080614"));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEqualsOrAfter() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		Date date1 = df.parse("20080614-1200");
		Date date2 = df.parse("20080614-1200");
		Assert.assertEquals(date1, date2);
		// date1 equals date2, return true
		Assert.assertTrue(CommonDateOperations.equalsOrAfter(date1, date2));
		date2 = df.parse("20080614-1201");
		Assert.assertNotSame(date1, date2);
		// date1 not after date2, return false
		Assert.assertFalse(CommonDateOperations.equalsOrAfter(date1, date2));
		date1 = df.parse("20080614-1202");
		Assert.assertNotSame(date1, date2);
		// date1 after date2, return true
		Assert.assertTrue(CommonDateOperations.equalsOrAfter(date1, date2));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEqualsOrAfterNearDSTBegin() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		Date date1 = df.parse("20090308-0159");
		Date date2 = df.parse("20090308-0159");
		Assert.assertEquals(date1, date2);
		// date1 equals date2, return true
		Assert.assertTrue(CommonDateOperations.equalsOrAfter(date1, date2));
		date2 = df.parse("20090308-0300");
		Assert.assertNotSame(date1, date2);
		// date1 not after date2, return false
		Assert.assertFalse(CommonDateOperations.equalsOrAfter(date1, date2));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEqualsOrAfterNearDSTEnd() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		Date date1 = df.parse("20091101-0200");
		Date date2 = df.parse("20091101-0200");
		Assert.assertEquals(date1, date2);
		// date1 equals date2, return true
		Assert.assertTrue(CommonDateOperations.equalsOrAfter(date1, date2));
		date2 = df.parse("20091101-0159");
		Assert.assertNotSame(date1, date2);
		// date1 after date2, return true
		Assert.assertTrue(CommonDateOperations.equalsOrAfter(date1, date2));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEqualsOrBefore() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		Date date1 = df.parse("20080614-1200");
		Date date2 = df.parse("20080614-1200");
		Assert.assertEquals(date1, date2);
		// date1 equals date2, return true
		Assert.assertTrue(CommonDateOperations.equalsOrBefore(date1, date2));
		date2 = df.parse("20080614-1201");
		Assert.assertNotSame(date1, date2);
		// date1 before date2, return true
		Assert.assertTrue(CommonDateOperations.equalsOrBefore(date1, date2));
		date1 = df.parse("20080614-1202");
		Assert.assertNotSame(date1, date2);
		// date1 not before date2, return false
		Assert.assertFalse(CommonDateOperations.equalsOrBefore(date1, date2));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEqualsOrBeforeNearDSTBegin() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		Date date1 = df.parse("20090308-0159");
		Date date2 = df.parse("20090308-0159");
		Assert.assertEquals(date1, date2);
		// date1 equals date2, return true
		Assert.assertTrue(CommonDateOperations.equalsOrBefore(date1, date2));
		date2 = df.parse("20090308-0300");
		Assert.assertNotSame(date1, date2);
		// date1 before date2, return true
		Assert.assertTrue(CommonDateOperations.equalsOrBefore(date1, date2));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEqualsOrBeforeNearDSTEnd() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		Date date1 = df.parse("20091101-0200");
		Date date2 = df.parse("20091101-0200");
		Assert.assertEquals(date1, date2);
		// date1 equals date2, return true
		Assert.assertTrue(CommonDateOperations.equalsOrBefore(date1, date2));
		date2 = df.parse("20091101-0159");
		Assert.assertNotSame(date1, date2);
		// date1 not before date2, return true
		Assert.assertFalse(CommonDateOperations.equalsOrBefore(date1, date2));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDifference() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		Date date1 = df.parse("20090308-0159");
		Date date2 = df.parse("20090308-0159");
		Assert.assertEquals(0, CommonDateOperations.approximateDifference(date1, date2));
		
		date2 = df.parse("20090309-0159");
		Assert.assertEquals(1, CommonDateOperations.approximateDifference(date1, date2));
		date2 = df.parse("20090309-0459");
		Assert.assertEquals(1, CommonDateOperations.approximateDifference(date1, date2));
		
		date2 = df.parse("20090409-0259");
		Assert.assertEquals(32, CommonDateOperations.approximateDifference(date1, date2));
		
		date2 = df.parse("20100308-0159");
		Assert.assertEquals(365, CommonDateOperations.approximateDifference(date1, date2));
		
		date2 = df.parse("20110308-0159");
		Assert.assertEquals(730, CommonDateOperations.approximateDifference(date1, date2));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNumberOfDaysUntilSunday() throws Exception {
		SimpleDateFormat df = CommonDateOperations.getDateFormat();
		Assert.assertEquals(7, CommonDateOperations.numberOfDaysUntilSunday(df.parse("20100725")));
		Assert.assertEquals(6, CommonDateOperations.numberOfDaysUntilSunday(df.parse("20100726")));
		Assert.assertEquals(5, CommonDateOperations.numberOfDaysUntilSunday(df.parse("20100727")));
		Assert.assertEquals(4, CommonDateOperations.numberOfDaysUntilSunday(df.parse("20100728")));
		Assert.assertEquals(3, CommonDateOperations.numberOfDaysUntilSunday(df.parse("20100729")));
		Assert.assertEquals(2, CommonDateOperations.numberOfDaysUntilSunday(df.parse("20100730")));
		Assert.assertEquals(1, CommonDateOperations.numberOfDaysUntilSunday(df.parse("20100731")));
	}
}
