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

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.RDate;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Generic tests related to the iCalendar data format and/or ical4j.
 *
 * @author Nicholas Blair
 * @version $Id: ICalendarDataTest.java $
 */
public class ICalendarDataTest {

	@Test
	public void testRDateWithDateValue() throws ParseException, ValidationException {
		ParameterList params = new ParameterList();
		params.add(Value.DATE);
		RDate rdate = new RDate(params, "20110921");
		rdate.validate();
		Assert.assertEquals("20110921", rdate.getValue());
	}
	
	@Test
	public void testParseCalendarDataWithRDate() throws IOException, ParserException {
		Resource exampleData = new ClassPathResource("org/jasig/schedassist/model/example-reflection.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar result = builder.build(new InputStreamReader(exampleData.getInputStream()));
		Assert.assertNotNull(result);
		ComponentList components = result.getComponents(VEvent.VEVENT);
		Assert.assertEquals(1, components.size());
		VEvent event = (VEvent) components.get(0);
		
		PropertyList rdates = event.getProperties(RDate.RDATE);
		Assert.assertEquals(5, rdates.size());
		Set<String> rdateValues = new HashSet<String>();
		for(Object o: rdates) {
			Property rdate = (Property) o;
			Assert.assertEquals(net.fortuna.ical4j.model.parameter.Value.DATE, rdate.getParameter(net.fortuna.ical4j.model.parameter.Value.VALUE));
			rdateValues.add(rdate.getValue());
		}
		
		Assert.assertTrue(rdateValues.contains("20110921"));
		Assert.assertTrue(rdateValues.contains("20110923"));
		Assert.assertTrue(rdateValues.contains("20110926"));
		Assert.assertTrue(rdateValues.contains("20110928"));
		Assert.assertTrue(rdateValues.contains("20110930"));
	}
	
	@Test
	public void testParseCalendarDataWithRDateList() throws IOException, ParserException {
		Resource exampleData = new ClassPathResource("org/jasig/schedassist/model/example-reflection-2.ics");
		CalendarBuilder builder = new CalendarBuilder();
		Calendar result = builder.build(new InputStreamReader(exampleData.getInputStream()));
		Assert.assertNotNull(result);
		ComponentList components = result.getComponents(VEvent.VEVENT);
		Assert.assertEquals(1, components.size());
		VEvent event = (VEvent) components.get(0);
		
		Property rdate = event.getProperty(RDate.RDATE);
		Assert.assertNotNull(rdate);
		Assert.assertEquals("20110921,20110923,20110926,20110928,20110930", rdate.getValue());
		
	}
}
