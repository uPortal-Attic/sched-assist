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

/**
 * 
 */
package org.jasig.schedassist.impl.caldav;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link DefaultCaldavDialectImpl}.
 * 
 * @author Nicholas Blair
 * @version $ Id: DefaultCaldavDialectImplTest.java $
 */
public class DefaultCaldavDialectImplTest {

	private Log log = LogFactory.getLog(this.getClass());
	
	@Test
	public void testFormatDateTime() throws ParseException  {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateTimeFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
		Date date = dateFormat.parse("20110502-1602");
		DefaultCaldavDialectImpl dialect = new DefaultCaldavDialectImpl();
		
		//yyyyMMdd'T'HHmmssZ
		Assert.assertEquals("20110502T210200Z", dialect.formatDateTime(date));
	}
	
	@Test
	public void testGetCalendarAccountHome() throws URISyntaxException {
		DefaultCaldavDialectImpl dialect = new DefaultCaldavDialectImpl();
		dialect.setCaldavHost(new URI("http://localhost:8080"));
		
		MockCalendarAccount calendarAccount = new MockCalendarAccount();
		calendarAccount.setUsername("somebody");
		Assert.assertEquals("http://localhost:8080/ucaldav/user/somebody/calendar/", dialect.getCalendarAccountHome(calendarAccount));
	}
	
	@Test
	public void testGenerateGetCalendarRequestEntity() throws InputFormatException {
		Date startDate = CommonDateOperations.parseDatePhrase("20110502");
		Date endDate = CommonDateOperations.parseDatePhrase("20110509");
		
		DefaultCaldavDialectImpl dialect = new DefaultCaldavDialectImpl();
		String requestXml = dialect.generateGetCalendarRequestXML(startDate, endDate);
		log.info(requestXml);
	}
}
