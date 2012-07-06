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

package org.jasig.schedassist.impl.caldav;

import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Uid;

import org.apache.commons.lang.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.jasig.schedassist.model.DefaultEventUtilsImpl;
import org.jasig.schedassist.model.ICalendarAccount;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests for {@link CaldavCalendarDataDaoImpl}.
 * 
 * @author Nicholas Blair
 */
public class CaldavCalendarDataDaoImplTest {

	@Test
	public void testMergeEmpty() {
		Calendar left = new Calendar();
		Calendar right = new Calendar();

		CaldavCalendarDataDaoImpl calendarDataDao = new CaldavCalendarDataDaoImpl();
		Calendar result = calendarDataDao.merge(left, right);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.getComponents().size());
	}

	@Test
	public void testMergeControl() {
		java.util.Date now = new java.util.Date();
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl();

		VEvent event1 = new VEvent(new DateTime(now), new DateTime(DateUtils.addHours(now, 1)), "event 1");
		event1.getProperties().add(new Uid("abcde12345"));
		Calendar left = eventUtils.wrapEventInCalendar(event1);

		VEvent event2 = new VEvent(new DateTime(DateUtils.addHours(now, 1)), new DateTime(DateUtils.addHours(now, 2)), "event 2");
		event2.getProperties().add(new Uid("abcde12346"));
		Calendar right = eventUtils.wrapEventInCalendar(event2);

		CaldavCalendarDataDaoImpl calendarDataDao = new CaldavCalendarDataDaoImpl();
		Calendar result = calendarDataDao.merge(left, right);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.getComponents().size());
	}

	@Test
	public void testMergeWithTimezones() {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone _timeZone = registry.getTimeZone("America/Chicago");

		java.util.Date now = new java.util.Date();
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl();

		VEvent event1 = new VEvent(new DateTime(now), new DateTime(DateUtils.addHours(now, 1)), "event 1");
		event1.getProperties().add(new Uid("abcde12345"));
		Calendar left = eventUtils.wrapEventInCalendar(event1);
		left.getComponents().add(_timeZone.getVTimeZone());

		VEvent event2 = new VEvent(new DateTime(DateUtils.addHours(now, 1)), new DateTime(DateUtils.addHours(now, 2)), "event 2");
		event2.getProperties().add(new Uid("abcde12346"));
		Calendar right = eventUtils.wrapEventInCalendar(event2);
		right.getComponents().add(_timeZone.getVTimeZone());

		CaldavCalendarDataDaoImpl calendarDataDao = new CaldavCalendarDataDaoImpl();
		Calendar result = calendarDataDao.merge(left, right);
		Assert.assertNotNull(result);
		Assert.assertEquals(3, result.getComponents().size());
		Assert.assertEquals(2, result.getComponents(VEvent.VEVENT).size());
		Assert.assertEquals(1, result.getComponents(VTimeZone.VTIMEZONE).size());
	}

	@Test
	public void testMergeWithDifferentTimezones() {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone _timeZone1 = registry.getTimeZone("America/Chicago");
		TimeZone _timeZone2 = registry.getTimeZone("America/New_York");

		java.util.Date now = new java.util.Date();
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl();

		VEvent event1 = new VEvent(new DateTime(now), new DateTime(DateUtils.addHours(now, 1)), "event 1");
		event1.getProperties().add(new Uid("abcde12345"));
		Calendar left = eventUtils.wrapEventInCalendar(event1);
		left.getComponents().add(_timeZone1.getVTimeZone());

		VEvent event2 = new VEvent(new DateTime(DateUtils.addHours(now, 1)), new DateTime(DateUtils.addHours(now, 2)), "event 2");
		event2.getProperties().add(new Uid("abcde12346"));
		Calendar right = eventUtils.wrapEventInCalendar(event2);
		right.getComponents().add(_timeZone2.getVTimeZone());

		CaldavCalendarDataDaoImpl calendarDataDao = new CaldavCalendarDataDaoImpl();
		Calendar result = calendarDataDao.merge(left, right);
		Assert.assertNotNull(result);
		Assert.assertEquals(4, result.getComponents().size());
		Assert.assertEquals(2, result.getComponents(VEvent.VEVENT).size());
		Assert.assertEquals(2, result.getComponents(VTimeZone.VTIMEZONE).size());
	}

	@Test
	public void testConsolidateEmpty() {
		List<CalendarWithURI> list = Collections.emptyList();
		CaldavCalendarDataDaoImpl calendarDataDao = new CaldavCalendarDataDaoImpl();
		Calendar result = calendarDataDao.consolidate(list);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.getComponents().size());
	}
	@Test
	public void testConsolidateSize1List() {
		List<CalendarWithURI> list = new ArrayList<CalendarWithURI>();
		java.util.Date now = new java.util.Date();
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl();
		VEvent event = new VEvent(new DateTime(DateUtils.addHours(now, 1)), new DateTime(DateUtils.addHours(now, 2)), "event 2");
		event.getProperties().add(new Uid("abcde12345"));
		Calendar cal = eventUtils.wrapEventInCalendar(event);
		CalendarWithURI withUri = new CalendarWithURI(cal, "/path/to/abcde12345");
		list.add(withUri);


		CaldavCalendarDataDaoImpl calendarDataDao = new CaldavCalendarDataDaoImpl();
		Calendar result = calendarDataDao.consolidate(list);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.getComponents().size());
	}
	@Test
	public void testConsolidateSize2List() {
		List<CalendarWithURI> list = new ArrayList<CalendarWithURI>();
		java.util.Date now = new java.util.Date();
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl();
		for(int i = 0; i < 2; i++) {
			VEvent event = new VEvent(new DateTime(DateUtils.addHours(now, i)), new DateTime(DateUtils.addHours(now, i + 1)), "event 2");
			event.getProperties().add(new Uid("abcde1234" + i));
			Calendar cal = eventUtils.wrapEventInCalendar(event);
			CalendarWithURI withUri = new CalendarWithURI(cal, "/path/to/abcde1234" + i);
			list.add(withUri);
		}

		CaldavCalendarDataDaoImpl calendarDataDao = new CaldavCalendarDataDaoImpl();
		Calendar result = calendarDataDao.consolidate(list);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.getComponents().size());
	}
	@Test
	public void testConsolidateSize3List() {
		List<CalendarWithURI> list = new ArrayList<CalendarWithURI>();
		java.util.Date now = new java.util.Date();
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl();
		for(int i = 0; i < 3; i++) {
			VEvent event = new VEvent(new DateTime(DateUtils.addHours(now, i)), new DateTime(DateUtils.addHours(now, i + 1)), "event 2");
			event.getProperties().add(new Uid("abcde1234" + i));
			Calendar cal = eventUtils.wrapEventInCalendar(event);
			CalendarWithURI withUri = new CalendarWithURI(cal, "/path/to/abcde1234" + i);
			list.add(withUri);
		}

		CaldavCalendarDataDaoImpl calendarDataDao = new CaldavCalendarDataDaoImpl();
		Calendar result = calendarDataDao.consolidate(list);
		Assert.assertNotNull(result);
		Assert.assertEquals(3, result.getComponents().size());
	}
	@Test
	public void testConsolidateEvenSizeList() {
		List<CalendarWithURI> list = new ArrayList<CalendarWithURI>();
		java.util.Date now = new java.util.Date();
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl();
		for(int i = 0; i < 10; i++) {
			VEvent event = new VEvent(new DateTime(DateUtils.addHours(now, i)), new DateTime(DateUtils.addHours(now, i + 1)), "event 2");
			event.getProperties().add(new Uid("abcde1234" + i));
			Calendar cal = eventUtils.wrapEventInCalendar(event);
			CalendarWithURI withUri = new CalendarWithURI(cal, "/path/to/abcde1234" + i);
			list.add(withUri);
		}

		CaldavCalendarDataDaoImpl calendarDataDao = new CaldavCalendarDataDaoImpl();
		Calendar result = calendarDataDao.consolidate(list);
		Assert.assertNotNull(result);
		Assert.assertEquals(10, result.getComponents().size());
	}

	@Test
	public void testConsolidateOddSizeList() {
		List<CalendarWithURI> list = new ArrayList<CalendarWithURI>();
		java.util.Date now = new java.util.Date();
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl();
		for(int i = 0; i < 11; i++) {
			VEvent event = new VEvent(new DateTime(DateUtils.addHours(now, i)), new DateTime(DateUtils.addHours(now, i + 1)), "event 2");
			event.getProperties().add(new Uid("abcde1234" + i));
			Calendar cal = eventUtils.wrapEventInCalendar(event);
			CalendarWithURI withUri = new CalendarWithURI(cal, "/path/to/abcde1234" + i);
			list.add(withUri);
		}

		CaldavCalendarDataDaoImpl calendarDataDao = new CaldavCalendarDataDaoImpl();
		Calendar result = calendarDataDao.consolidate(list);
		Assert.assertNotNull(result);
		Assert.assertEquals(11, result.getComponents().size());
	}

	@Test
	public void testGetCalendarProblem1() throws ClientProtocolException, IOException {
		java.util.Date now = new java.util.Date();
		java.util.Date later = DateUtils.addDays(now, 1);

		CaldavCalendarDataDaoImpl calendarDataDao = new CaldavCalendarDataDaoImpl();
		ICalendarAccount calendarAccount = mock(ICalendarAccount.class);
		when(calendarAccount.getUsername()).thenReturn("username");
		when(calendarAccount.getEmailAddress()).thenReturn("username@server.edu");
		DefaultCaldavDialectImpl dialect = new DefaultCaldavDialectImpl();
		dialect.setCaldavHost(URI.create("http://localhost:8080/"));
		calendarDataDao.setCaldavDialect(dialect);
		DefaultCredentialsProviderFactoryImpl credentialsProviderFactory = new DefaultCredentialsProviderFactoryImpl();
		credentialsProviderFactory.setCaldavAdminUsername("username");
		credentialsProviderFactory.setCaldavAdminPassword("password");
		calendarDataDao.setCredentialsProviderFactory(credentialsProviderFactory);
		
		Resource problemResponse1 = new ClassPathResource("caldav-examples/report-response-problem1.xml");
		
		HttpResponse response = mock(HttpResponse.class);
		HttpEntity entity = mock(HttpEntity.class);
		when(entity.getContent()).thenReturn(problemResponse1.getInputStream());
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(207);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(response.getEntity()).thenReturn(entity);
		
		HttpClient httpClient = mock(HttpClient.class);
		when(httpClient.execute((HttpHost) eq(null), isA(HttpRequest.class), isA(HttpContext.class))).thenReturn(response);
		calendarDataDao.setHttpClient(httpClient);
		Calendar calendar = calendarDataDao.getCalendar(calendarAccount, now, later);
		Assert.assertNotNull(calendar);
		
	}
}
