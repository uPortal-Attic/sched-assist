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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Test bench JAXB annotated objects in messaging package.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ObjectMarshallingTest.java 2974 2011-01-25 13:44:23Z npblair $
 */
public class ObjectMarshallingTest {
	
	private final JAXBContext context;
	
	public ObjectMarshallingTest() {
		try {
			context = JAXBContext.newInstance(ObjectFactory.class);
		} catch (JAXBException e) {
			throw new IllegalStateException("unable to initialize JAXBContext", e);
		}
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCancelAppointmentRequest() throws Exception {
		CancelAppointmentRequest request = new CancelAppointmentRequest();
		request.setEndTime(XMLDataUtils.convertDateToXMLGregorianCalendar(toDateTime("20091117-1300")));
		request.setOwnerId(42L);
		request.setReason("Some reason to cancel");
		request.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(toDateTime("20091117-1230")));
		request.setVisitorNetid("visitor");
		
	
		// marshal to System.out
		context.createMarshaller().marshal(request, System.out);
		System.out.println();
		
		// marshal to a ByteArrayOutputStream so we can unmarshal it back
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		context.createMarshaller().marshal(request, output);
		CancelAppointmentRequest unmarshalled = (CancelAppointmentRequest) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(output.toByteArray()));
		Assert.assertNotNull(unmarshalled);
		Assert.assertEquals(request.getOwnerId(), unmarshalled.getOwnerId());
		Assert.assertEquals(request.getReason(), unmarshalled.getReason());
		Assert.assertEquals(request.getVisitorNetid(), unmarshalled.getVisitorNetid());
		Assert.assertEquals(request.getEndTime(), unmarshalled.getEndTime());
		Assert.assertEquals(request.getStartTime(), unmarshalled.getStartTime());
	}
	 
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCancelAppointmentResponse() throws Exception {
		CancelAppointmentResponse response = new CancelAppointmentResponse();
		response.setEndTime(XMLDataUtils.convertDateToXMLGregorianCalendar(toDateTime("20091117-1300")));
		response.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(toDateTime("20091117-1230")));
		
		// marshal to System.out
		context.createMarshaller().marshal(response, System.out);
		System.out.println();
		
		// marshal to a ByteArrayOutputStream so we can unmarshal it back
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		context.createMarshaller().marshal(response, output);
		CancelAppointmentResponse unmarshalled = (CancelAppointmentResponse) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(output.toByteArray()));
		Assert.assertNotNull(unmarshalled);
		Assert.assertEquals(response.getEndTime(), unmarshalled.getEndTime());
		Assert.assertEquals(response.getStartTime(), unmarshalled.getStartTime());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateAppointmentRequest() throws Exception {
		CreateAppointmentRequest request = new CreateAppointmentRequest();
		request.setSelectedDuration(60);
		request.setOwnerId(42);
		request.setEventDescription("Some reason to meet");
		request.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(toDateTime("20091117-1230")));
		request.setVisitorNetid("visitor");
		
		// marshal to System.out
		context.createMarshaller().marshal(request, System.out);
		System.out.println();
		
		// marshal to a ByteArrayOutputStream so we can unmarshal it back
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		context.createMarshaller().marshal(request, output);
		CreateAppointmentRequest unmarshalled = (CreateAppointmentRequest) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(output.toByteArray()));
		Assert.assertNotNull(unmarshalled);
		Assert.assertEquals(request.getOwnerId(), unmarshalled.getOwnerId());
		Assert.assertEquals(request.getEventDescription(), unmarshalled.getEventDescription());
		Assert.assertEquals(request.getVisitorNetid(), unmarshalled.getVisitorNetid());
		Assert.assertEquals(request.getSelectedDuration(), unmarshalled.getSelectedDuration());
		Assert.assertEquals(request.getStartTime(), unmarshalled.getStartTime());
	}
	 
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateAppointmentResponse() throws Exception {
		CreateAppointmentResponse response = new CreateAppointmentResponse();
		response.setEventLocation("Some location");
		response.setEventTitle("Some meeting title");
		response.setEndTime(XMLDataUtils.convertDateToXMLGregorianCalendar(toDateTime("20091117-1300")));
		response.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(toDateTime("20091117-1230")));
		
		// marshal to System.out
		context.createMarshaller().marshal(response, System.out);
		System.out.println();
		
		// marshal to a ByteArrayOutputStream so we can unmarshal it back
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		context.createMarshaller().marshal(response, output);
		CreateAppointmentResponse unmarshalled = (CreateAppointmentResponse) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(output.toByteArray()));
		Assert.assertNotNull(unmarshalled);
		Assert.assertEquals(response.getEndTime(), unmarshalled.getEndTime());
		Assert.assertEquals(response.getStartTime(), unmarshalled.getStartTime());
		Assert.assertEquals(response.getEventLocation(), unmarshalled.getEventLocation());
		Assert.assertEquals(response.getEventTitle(), unmarshalled.getEventTitle());
	}
	
	/**
	 * Marshal a {@link GetRelationshipsRequest} to a byte array stream, unmarshal the {@link GetRelationshipsRequest}
	 * back out from the byte array stream, assert it comes back intact.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetRelationshipsRequest() throws Exception {
		GetRelationshipsRequest request = new GetRelationshipsRequest();
		request.setVisitorNetid("somenetid");
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		context.createMarshaller().marshal(request, output);
		context.createMarshaller().marshal(request, System.out);
		System.out.println();
		
		GetRelationshipsRequest result = (GetRelationshipsRequest) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(output.toByteArray()));
		Assert.assertNotNull(result);
		Assert.assertEquals("somenetid", result.getVisitorNetid());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetRelationshipsRequestFromSample() throws Exception {
		String requestString = "<ava:GetRelationshipsRequest xmlns:ava=\"https://source.jasig.org/schemas/sched-assist\">" +
         				"<ava:visitorNetid>someperson</ava:visitorNetid>" +
         				"</ava:GetRelationshipsRequest>";
		
		GetRelationshipsRequest request = (GetRelationshipsRequest) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(requestString.getBytes()));
		Assert.assertNotNull(request);
		Assert.assertEquals("someperson", request.getVisitorNetid());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetRelationshipsResponseFromSample() throws Exception {
		String responseString = "<ns2:GetRelationshipsResponse xmlns:ns2=\"https://source.jasig.org/schemas/sched-assist\">" +
        		"<ns2:RelationshipList>" +
        		"<ns2:RelationshipElement>" +
        		"<ns2:ScheduleOwnerElement>" +
        		"<ns2:id>12</ns2:id>" +
        		"<ns2:fullName>FIRST M LAST</ns2:fullName>" +
        		"<ns2:netid>fmlast</ns2:netid>" +
        		"</ns2:ScheduleOwnerElement>" +
        		"<ns2:description>wisccal team</ns2:description>" +
        		"</ns2:RelationshipElement>" +
        		"</ns2:RelationshipList>" +
        		"</ns2:GetRelationshipsResponse>";
		
		GetRelationshipsResponse response = (GetRelationshipsResponse) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(responseString.getBytes()));
		Assert.assertNotNull(response);
		RelationshipElement element = response.getRelationshipList().getRelationshipElement().get(0);
		Assert.assertEquals(12, element.getScheduleOwnerElement().getId());
		Assert.assertEquals("FIRST M LAST", element.getScheduleOwnerElement().getFullName());
		Assert.assertEquals("fmlast", element.getScheduleOwnerElement().getNetid());
		Assert.assertEquals("wisccal team", element.getDescription());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetScheduleOwnerByIdRequest() throws Exception {
		GetScheduleOwnerByIdRequest request = new GetScheduleOwnerByIdRequest();
		request.setId(42);
		
		// marshal to System.out
		context.createMarshaller().marshal(request, System.out);
		System.out.println();
		
		// marshal to a ByteArrayOutputStream so we can unmarshal it back
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		context.createMarshaller().marshal(request, output);
		
		GetScheduleOwnerByIdRequest unmarshalled = (GetScheduleOwnerByIdRequest) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(output.toByteArray()));
		Assert.assertNotNull(unmarshalled);
		Assert.assertEquals(request.getId(), unmarshalled.getId());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetScheduleOwnerByIdResponse() throws Exception {
		PreferencesSet preferencesSet = new PreferencesSet();
		PreferencesElement e1 = new PreferencesElement();
		e1.setKey("key1");
		e1.setValue("value1");
		PreferencesElement e2 = new PreferencesElement();
		e2.setKey("key2");
		e2.setValue("value2");
		preferencesSet.getPreferencesElement().add(e1);
		preferencesSet.getPreferencesElement().add(e2);
		
		ScheduleOwnerElement owner = new ScheduleOwnerElement();
		owner.setFullName("FULL NAME");
		owner.setId(42);
		owner.setNetid("netid");
		owner.setPreferencesSet(preferencesSet);
		GetScheduleOwnerByIdResponse response = new GetScheduleOwnerByIdResponse();
		response.setScheduleOwnerElement(owner);
		
		
		// marshal to System.out
		context.createMarshaller().marshal(response, System.out);
		System.out.println();
		
		// marshal to a ByteArrayOutputStream so we can unmarshal it back
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		context.createMarshaller().marshal(response, output);
		
		GetScheduleOwnerByIdResponse unmarshalled = (GetScheduleOwnerByIdResponse) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(output.toByteArray()));
		Assert.assertNotNull(unmarshalled);
		ScheduleOwnerElement uOwner = unmarshalled.getScheduleOwnerElement();
		Assert.assertNotNull(uOwner);
		Assert.assertEquals(owner.getFullName(), uOwner.getFullName());
		Assert.assertEquals(owner.getId(), uOwner.getId());
		Assert.assertEquals(owner.getNetid(), uOwner.getNetid());
		Assert.assertEquals(owner.getPreferencesSet().getPreferencesElement(), uOwner.getPreferencesSet().getPreferencesElement());
	}
	
	/**
	 * Marshal a {@link VisibleScheduleRequest} to a byte array stream, unmarshal the {@link VisibleScheduleRequest}
	 * back out from the byte array stream, assert it comes back intact.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVisibleScheduleRequest() throws Exception {
		VisibleScheduleRequest request = new VisibleScheduleRequest();
		request.setOwnerId(127L);
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		request.setVisitorNetid("visitornetid");
		
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		context.createMarshaller().marshal(request, output);
		context.createMarshaller().marshal(request, System.out);
		System.out.println();
		
		VisibleScheduleRequest result = (VisibleScheduleRequest) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(output.toByteArray()));
		Assert.assertNotNull(result);
		Assert.assertEquals(127L, result.getOwnerId());
		Assert.assertEquals("visitornetid", result.getVisitorNetid());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVisibleScheduleRequestFromSample() throws Exception {
		String requestString = "<ava:VisibleScheduleRequest xmlns:ava=\"https://source.jasig.org/schemas/sched-assist\">" +
         				"<ava:ownerId>135</ava:ownerId>" +
         				"<ava:visitorNetid>visitorid</ava:visitorNetid>" +
         				"</ava:VisibleScheduleRequest>";
		
		
		VisibleScheduleRequest request = (VisibleScheduleRequest) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(requestString.getBytes()));
		Assert.assertNotNull(request);
		Assert.assertEquals(135L, request.getOwnerId());
		Assert.assertEquals("visitorid", request.getVisitorNetid());
		// verify default value of 1 is set
		Assert.assertEquals(1, request.getWeekStart());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVisibleScheduleRequestWithWeekStart() throws Exception {
		String requestString = "<ava:VisibleScheduleRequest xmlns:ava=\"https://source.jasig.org/schemas/sched-assist\">" +
			"<ava:ownerId>135</ava:ownerId>" +
			"<ava:visitorNetid>visitorid</ava:visitorNetid>" +
			"<ava:weekStart>6</ava:weekStart>" +
			"</ava:VisibleScheduleRequest>";

		
		VisibleScheduleRequest request = (VisibleScheduleRequest) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(requestString.getBytes()));
		Assert.assertNotNull(request);
		Assert.assertEquals(135L, request.getOwnerId());
		Assert.assertEquals("visitorid", request.getVisitorNetid());
		Assert.assertEquals(6, request.getWeekStart());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetTargetAvailableBlockRequest() throws Exception {
		GetTargetAvailableBlockRequest request = new GetTargetAvailableBlockRequest();
		request.setDoubleLength(true);
		request.setOwnerId(42);
		Date now = new Date();
		request.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(now));
		
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		context.createMarshaller().marshal(request, output);
		context.createMarshaller().marshal(request, System.out);
		System.out.println();
		
		GetTargetAvailableBlockRequest result = (GetTargetAvailableBlockRequest) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(output.toByteArray()));
		Assert.assertNotNull(result);
		Assert.assertEquals(42, result.getOwnerId());
		Assert.assertEquals(true, result.isDoubleLength());
		Assert.assertEquals(now, XMLDataUtils.convertXMLGregorianCalendarToDate(result.getStartTime()));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetTargetAvailableBlockResponse() throws Exception {
		AvailableBlockElement element = new AvailableBlockElement();
		Date start = toDateTime("20091120-1200");
		Date end = toDateTime("20091120-1230");
		element.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(start));
		element.setEndTime(XMLDataUtils.convertDateToXMLGregorianCalendar(end));
		element.setStatus(AvailableStatusType.FREE);
		element.setVisitorLimit(1);
		element.setVisitorsAttending(0);
		
		GetTargetAvailableBlockResponse response = new GetTargetAvailableBlockResponse();
		response.setAvailableBlockElement(element);
		
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		context.createMarshaller().marshal(response, output);
		context.createMarshaller().marshal(response, System.out);
		System.out.println();
		
		GetTargetAvailableBlockResponse result = (GetTargetAvailableBlockResponse) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(output.toByteArray()));
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.getAvailableBlockElement().getVisitorsAttending());
		Assert.assertEquals(1, result.getAvailableBlockElement().getVisitorLimit());
		Assert.assertEquals(AvailableStatusType.FREE, result.getAvailableBlockElement().getStatus());
		Assert.assertEquals(start, XMLDataUtils.convertXMLGregorianCalendarToDate(result.getAvailableBlockElement().getStartTime()));
		Assert.assertEquals(end, XMLDataUtils.convertXMLGregorianCalendarToDate(result.getAvailableBlockElement().getEndTime()));
	}
	
	@Test
	public void testUnmarshalGetRelationshipsResponseControl() throws JAXBException, IOException {
		Unmarshaller u = context.createUnmarshaller();
		Resource getRelationshipsResponseControl = new ClassPathResource("messaging-examples/GetRelationshipsResponse-control.xml");
		
		GetRelationshipsResponse response = (GetRelationshipsResponse) u.unmarshal(getRelationshipsResponseControl.getInputStream());
		Assert.assertNotNull(response);
		RelationshipList list = response.getRelationshipList();
		Assert.assertEquals(1, list.getRelationshipElement().size());
		RelationshipElement r = list.getRelationshipElement().get(0);
		Assert.assertEquals("Bedework Development Team", r.getDescription());
		ScheduleOwnerElement owner = r.getScheduleOwnerElement();
		Assert.assertEquals(1L, owner.getId());
		Assert.assertEquals("schwag", owner.getNetid());
		Assert.assertEquals("Schwartz, Gary", owner.getFullName());
		PreferencesSet preferences = owner.getPreferencesSet();
		List<PreferencesElement> elements = preferences.getPreferencesElement();
		Assert.assertEquals(10, elements.size());
	}
	
	
	/**
	 * Helper function to convert a {@link String} in the format
	 * "yyyyMMdd-HHmm" to the corresponding {@link Date}.
	 * 
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	private Date toDateTime(final String value) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmm");
		return df.parse(value);
	}
}
