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

package org.jasig.schedassist.impl.relationship.advising;



import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test bench for {@link TermCalculator}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: TermCalculatorTest.java 1711 2010-02-15 16:20:17Z npblair $
 */
public class TermCalculatorTest {

	/**
	 * 
	 */
	@Test
	public void testTwentiethCentury() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.YEAR, 1999);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		
		String term = TermCalculator.calculateTerm(calendar);
		Assert.assertEquals("0994", term);
		
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		term = TermCalculator.calculateTerm(calendar);
		Assert.assertEquals("0994", term);
		
		calendar.set(Calendar.MONTH, Calendar.MAY);
		term = TermCalculator.calculateTerm(calendar);
		Assert.assertEquals("0994", term);
		
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		term = TermCalculator.calculateTerm(calendar);
		Assert.assertEquals("0996", term);
		
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		term = TermCalculator.calculateTerm(calendar);
		Assert.assertEquals("0996", term);
		
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		term = TermCalculator.calculateTerm(calendar);
		Assert.assertEquals("1002", term);
		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		term = TermCalculator.calculateTerm(calendar);
		Assert.assertEquals("1002", term);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		term = TermCalculator.calculateTerm(calendar);
		Assert.assertEquals("1002", term);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLessThan() throws Exception {
		Assert.assertTrue(TermCalculator.termLessThanOrEquals("1094", "1096"));
		Assert.assertTrue(TermCalculator.termLessThanOrEquals("1094", "1102"));
		Assert.assertTrue(TermCalculator.termLessThanOrEquals("1094", "1094"));
		Assert.assertFalse(TermCalculator.termLessThanOrEquals("1094", "1092"));
		Assert.assertFalse(TermCalculator.termLessThanOrEquals("1094", "1086"));
		Assert.assertFalse(TermCalculator.termLessThanOrEquals("1094", "1084"));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGreaterThan() throws Exception {
		Assert.assertFalse(TermCalculator.termGreaterThanOrEquals("1094", "1096"));
		Assert.assertFalse(TermCalculator.termGreaterThanOrEquals("1094", "1102"));
		Assert.assertTrue(TermCalculator.termGreaterThanOrEquals("1094", "1094"));
		Assert.assertTrue(TermCalculator.termGreaterThanOrEquals("1094", "1092"));
		Assert.assertTrue(TermCalculator.termGreaterThanOrEquals("1094", "1086"));
		Assert.assertTrue(TermCalculator.termGreaterThanOrEquals("1094", "1084"));
	}
}
