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

package org.jasig.schedassist.portlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ConflictExistsException;
import org.jasig.schedassist.NoAppointmentExistsException;
import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.messaging.AvailableBlockElement;
import org.jasig.schedassist.messaging.AvailableStatusType;
import org.jasig.schedassist.messaging.CancelAppointmentRequest;
import org.jasig.schedassist.messaging.CancelAppointmentResponse;
import org.jasig.schedassist.messaging.CreateAppointmentRequest;
import org.jasig.schedassist.messaging.CreateAppointmentResponse;
import org.jasig.schedassist.messaging.GetRelationshipsRequest;
import org.jasig.schedassist.messaging.GetRelationshipsResponse;
import org.jasig.schedassist.messaging.GetTargetAvailableBlockRequest;
import org.jasig.schedassist.messaging.GetTargetAvailableBlockResponse;
import org.jasig.schedassist.messaging.IsEligibleRequest;
import org.jasig.schedassist.messaging.IsEligibleResponse;
import org.jasig.schedassist.messaging.RelationshipElement;
import org.jasig.schedassist.messaging.ScheduleOwnerElement;
import org.jasig.schedassist.messaging.VisibleScheduleRequest;
import org.jasig.schedassist.messaging.VisibleScheduleResponse;
import org.jasig.schedassist.messaging.VisitorConflictsRequest;
import org.jasig.schedassist.messaging.VisitorConflictsResponse;
import org.jasig.schedassist.messaging.XMLDataUtils;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.MeetingDurations;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.model.VisibleSchedule;
import org.springframework.ws.soap.client.SoapFaultClientException;

/**
 * Mimics {@link SchedulingAssistantService} and {@link RelationshipDao}, however {@link IScheduleOwner} 
 * and {@link IScheduleVisitor} arguments are replaced with {@link String}s containing solely the username.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PortletAvailableServiceImpl.java 3024 2011-02-01 19:25:08Z npblair $
 */
public final class PortletSchedulingAssistantServiceImpl extends WebServicesDaoSupport implements PortletSchedulingAssistantService {

	private Log LOG = LogFactory.getLog(this.getClass());
	
	public static final String CONFLICT_MESSAGE = "conflict exists";
	public static final String TIME_NOT_AVAILABLE_MESSAGE = "time not available";
	public static final String CANCEL_FAILED_MESSAGE = "Appointment no longer exists";
	
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.PortletSchedulingAssistantService#isEligible(java.lang.String)
	 */
	@Override
	public boolean isEligible(String visitorUsername) {
		IsEligibleRequest request = new IsEligibleRequest();
		request.setVisitorNetid(visitorUsername);
		if(LOG.isDebugEnabled()) {
			LOG.debug("sending isEligible request for " + visitorUsername);
		}
		IsEligibleResponse response = (IsEligibleResponse) this.doSendAndReceive(request);
		if(LOG.isDebugEnabled()) {
			LOG.debug("isEligible for " + visitorUsername + " returns " + response.isEligible());
		}
		return response.isEligible();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.PortletSchedulingAssistantService#cancelAppointment(java.lang.String, long, org.jasig.schedassist.model.AvailableBlock, java.lang.String)
	 */
	public EventCancellation cancelAppointment(final String visitorUsername, final long ownerId,
			final AvailableBlock block, final String cancelReason) throws SchedulingException {
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("sending cancelAppointment request, visitor: " + visitorUsername + ", owner: " + ownerId + ", block: " + block);
		}
		CancelAppointmentRequest request = new CancelAppointmentRequest();
		request.setEndTime(XMLDataUtils.convertDateToXMLGregorianCalendar(block.getEndTime()));
		request.setOwnerId(ownerId);
		request.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(block.getStartTime()));
		request.setVisitorNetid(visitorUsername);
		request.setReason(cancelReason);

		try {
			CancelAppointmentResponse response = (CancelAppointmentResponse) this.doSendAndReceive(request);
			LOG.debug("cancelAppointment success");
			EventCancellation result = new EventCancellation(
					XMLDataUtils.convertXMLGregorianCalendarToDate(response.getStartTime()),
					XMLDataUtils.convertXMLGregorianCalendarToDate(response.getEndTime()));
			return result;
		} catch (SoapFaultClientException e) {
			if(CANCEL_FAILED_MESSAGE.equals(e.getFaultStringOrReason())) {
				throw new NoAppointmentExistsException(CANCEL_FAILED_MESSAGE, e);
			} else {
				throw new SchedulingException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.PortletSchedulingAssistantService#getVisibleSchedule(java.lang.String, long)
	 */
	@Override
	public VisibleSchedule getVisibleSchedule(final String visitorUsername,
			final long ownerId) {
		return getVisibleSchedule(visitorUsername, ownerId, 1);
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.PortletSchedulingAssistantService#getVisibleSchedule(java.lang.String, long, int)
	 */
	@Override
	public VisibleSchedule getVisibleSchedule(String visitorUsername,
			long ownerId, int weekStart) {
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("sending getVisibleSchedule request, visitor: " + visitorUsername + ", owner: " + ownerId);
		}
		VisibleScheduleRequest request = new VisibleScheduleRequest();
		request.setOwnerId(ownerId);
		request.setVisitorNetid(visitorUsername);
		request.setWeekStart(weekStart);
		if(LOG.isDebugEnabled()) {
			LOG.debug(request);
		}
		VisibleScheduleResponse response = (VisibleScheduleResponse) this.doSendAndReceive(request);
		
		MeetingDurations durations = MeetingDurations.fromKey(response.getOwnerMeetingDurationsPreference().getValue());
		VisibleSchedule result = new VisibleSchedule(durations);

		List<AvailableBlockElement> blockElements = response.getAvailableBlockList().getAvailableBlockElement();
		for(AvailableBlockElement blockElement : blockElements) {
			AvailableStatusType status = blockElement.getStatus();
			AvailableBlock block = AvailableBlockBuilder.createBlock(
					blockElement.getStartTime().toGregorianCalendar().getTime(), 
					blockElement.getEndTime().toGregorianCalendar().getTime(),
					blockElement.getVisitorLimit());
			block.setVisitorsAttending(blockElement.getVisitorsAttending());
			// first add the block as a freeblock
			result.addFreeBlock(block);
			// then add with it's true status
			switch (status) {
			case ATTENDING:
				result.setAttendingBlock(block);
				break;
			case BUSY:
				result.setBusyBlock(block);
				break;
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.PortletSchedulingAssistantService#calculateVisitorConflicts(java.lang.String, long, int)
	 */
	@Override
	public List<AvailableBlock> calculateVisitorConflicts(
			String visitorUsername, long ownerId, int weekStart) {
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("calculateVisitorConflicts, visitor: " + visitorUsername + ", owner: " + ownerId);
		}
		VisitorConflictsRequest request = new VisitorConflictsRequest();
		request.setOwnerId(ownerId);
		request.setVisitorNetid(visitorUsername);
		request.setWeekStart(weekStart);
		if(LOG.isDebugEnabled()) {
			LOG.debug(request);
		}
		VisitorConflictsResponse response = (VisitorConflictsResponse) this.doSendAndReceive(request);
		List<AvailableBlockElement> blockElements = response.getAvailableBlockList().getAvailableBlockElement();
		
		List<AvailableBlock> results = new ArrayList<AvailableBlock>();
		for(AvailableBlockElement blockElement : blockElements) {
			AvailableBlock block = AvailableBlockBuilder.createBlock(
					blockElement.getStartTime().toGregorianCalendar().getTime(), 
					blockElement.getEndTime().toGregorianCalendar().getTime());
			results.add(block);
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("calculateVisitorConflicts result has " + results.size() + " elements");
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.PortletSchedulingAssistantService#getTargetBlock(org.jasig.schedassist.model.IScheduleOwner, java.util.Date)
	 */
	@Override
	public AvailableBlock getTargetBlock(IScheduleOwner owner, Date startTime) {
		return getTargetBlockInternal(owner, startTime, false);
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.PortletSchedulingAssistantService#getTargetDoubleLengthBlock(org.jasig.schedassist.model.IScheduleOwner, java.util.Date)
	 */
	@Override
	public AvailableBlock getTargetDoubleLengthBlock(IScheduleOwner owner,
			Date startTime) {
		return getTargetBlockInternal(owner, startTime, true);
	}
	
	/**
	 * 
	 * @param owner
	 * @param startTime
	 * @param doubleLength
	 * @return
	 */
	protected AvailableBlock getTargetBlockInternal(IScheduleOwner owner,
			Date startTime, boolean doubleLength) {
		
		GetTargetAvailableBlockRequest request = new GetTargetAvailableBlockRequest();
		request.setOwnerId(owner.getId());
		request.setDoubleLength(doubleLength);
		request.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(startTime));
		
		//GetTargetAvailableBlockResponse response = (GetTargetAvailableBlockResponse) this.webServiceTemplate.marshalSendAndReceive(request);
		GetTargetAvailableBlockResponse response = (GetTargetAvailableBlockResponse) this.doSendAndReceive(request);
		
		if(null == response.getAvailableBlockElement()) {
			return null;
		} else {
			AvailableBlock result = convertAvailableBlockElement(response.getAvailableBlockElement());
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.PortletSchedulingAssistantService#scheduleAppointment(java.lang.String, long, org.jasig.schedassist.model.AvailableBlock, java.lang.String)
	 */
	@Override
	public VEvent scheduleAppointment(final String visitorUsername,
			final long ownerId, final AvailableBlock block, final String eventDescription)
			throws SchedulingException {
		
		LOG.debug("scheduleAppointment called; visitor: " + visitorUsername + ", owner: " + ownerId + ", block: " + block);

		CreateAppointmentRequest request = new CreateAppointmentRequest();
		request.setSelectedDuration(block.getDurationInMinutes());
		request.setEventDescription(eventDescription);
		request.setOwnerId(ownerId);
		request.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(block.getStartTime()));
		request.setVisitorNetid(visitorUsername);
		try {
			//CreateAppointmentResponse response = (CreateAppointmentResponse) webServiceTemplate.marshalSendAndReceive(request);
			CreateAppointmentResponse response = (CreateAppointmentResponse) this.doSendAndReceive(request);
			if(null == response) {
				LOG.error("response was null!");
			}
			LOG.debug("received response; start: " + response.getStartTime() + ", end: " + response.getEndTime() + ", location: " + response.getEventLocation() + ", title: " + response.getEventTitle());
			VEvent vevent = new VEvent(
					new DateTime(response.getStartTime().toGregorianCalendar().getTime()), 
					new DateTime(response.getEndTime().toGregorianCalendar().getTime()), 
					response.getEventTitle());

			Location location = new Location(response.getEventLocation());
			vevent.getProperties().add(location);
			LOG.debug("scheduleAppointment success");
			return vevent;
		} catch (SoapFaultClientException e) {
			LOG.error("caught SOAP Fault in scheduleAppointment: ", e);
			if(e.getFaultStringOrReason().contains(CONFLICT_MESSAGE) || e.getFaultStringOrReason().contains(TIME_NOT_AVAILABLE_MESSAGE)) {
				throw new ConflictExistsException("a conflict exists for " + block, e);
			} else {
				throw e;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.PortletSchedulingAssistantService#relationshipsForVisitor(java.lang.String)
	 */
	@Override
	public List<Relationship> relationshipsForVisitor(final String visitorUsername) {
		LOG.debug("relationshipsForVisitor, visitor: " + visitorUsername);
		List<Relationship> result = new ArrayList<Relationship>();
		GetRelationshipsRequest request = new GetRelationshipsRequest();
		request.setVisitorNetid(visitorUsername);

		GetRelationshipsResponse response = (GetRelationshipsResponse) this.doSendAndReceive(request);

		LOG.debug("getRelationships response received");
		List<RelationshipElement> relationshipElements = response.getRelationshipList().getRelationshipElement();
		LOG.debug("getRelationships response received with size " + relationshipElements.size());
		for(RelationshipElement relationshipElement : relationshipElements) {
			IScheduleOwner owner = convertOwnerElement(relationshipElement.getScheduleOwnerElement());
			LOG.debug("-> " + visitorUsername + ", " + owner.getCalendarAccount().getUsername());

			Relationship relationship = new Relationship();
			relationship.setDescription(relationshipElement.getDescription());
			relationship.setOwner(owner);

			result.add(relationship);
		}
		return result;
	}
	

	/**
	 * Convert a {@link ScheduleOwnerElement} into an {@link IScheduleOwner}.
	 * 
	 * @param element
	 * @return
	 */
	protected static IScheduleOwner convertOwnerElement(ScheduleOwnerElement element) {
		PortletScheduleOwnerImpl owner = new PortletScheduleOwnerImpl(element);
		return owner;
	}
	
	/**
	 * Convert a {@link AvailableBlockElement} into a {@link AvailableBlock}.
	 * 
	 * @param element
	 * @return
	 */
	protected static AvailableBlock convertAvailableBlockElement(AvailableBlockElement element) {
		Date startTime = XMLDataUtils.convertXMLGregorianCalendarToDate(element.getStartTime());
		Date endTime = XMLDataUtils.convertXMLGregorianCalendarToDate(element.getEndTime());
		
		AvailableBlock result = AvailableBlockBuilder.createBlock(startTime, endTime, element.getVisitorLimit());
		result.setVisitorsAttending(element.getVisitorsAttending());
		return result;
	}
}
