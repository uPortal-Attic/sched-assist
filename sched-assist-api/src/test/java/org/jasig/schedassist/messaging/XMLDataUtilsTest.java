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

package org.jasig.schedassist.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

/**
 * Tests for {@link XMLDataUtils}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: XMLDataUtilsTest.java 2974 2011-01-25 13:44:23Z npblair $
 */
public class XMLDataUtilsTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvertDate() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
		Date example = dateFormat.parse("20090122-1401");
		XMLGregorianCalendar calendar = XMLDataUtils.convertDateToXMLGregorianCalendar(example);
		assertNotNull(calendar);
		assertEquals(14, calendar.getHour());
		assertEquals(1, calendar.getMinute());
		assertEquals(2009, calendar.getYear());
		assertEquals(1, calendar.getMonth());
		assertEquals(22, calendar.getDay());
	}
}
