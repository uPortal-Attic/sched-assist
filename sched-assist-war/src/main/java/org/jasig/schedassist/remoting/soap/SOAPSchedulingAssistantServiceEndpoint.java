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


package org.jasig.schedassist.remoting.soap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.CalendarAccountNotFoundException;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.impl.visitor.VisitorDao;
import org.jasig.schedassist.messaging.AvailableBlockElement;
import org.jasig.schedassist.messaging.AvailableBlockList;
import org.jasig.schedassist.messaging.AvailableStatusType;
import org.jasig.schedassist.messaging.CancelAppointmentRequest;
import org.jasig.schedassist.messaging.CancelAppointmentResponse;
import org.jasig.schedassist.messaging.CreateAppointmentRequest;
import org.jasig.schedassist.messaging.CreateAppointmentResponse;
import org.jasig.schedassist.messaging.GetRelationshipsRequest;
import org.jasig.schedassist.messaging.GetRelationshipsResponse;
import org.jasig.schedassist.messaging.GetScheduleOwnerByIdRequest;
import org.jasig.schedassist.messaging.GetScheduleOwnerByIdResponse;
import org.jasig.schedassist.messaging.GetTargetAvailableBlockRequest;
import org.jasig.schedassist.messaging.GetTargetAvailableBlockResponse;
import org.jasig.schedassist.messaging.IsEligibleRequest;
import org.jasig.schedassist.messaging.IsEligibleResponse;
import org.jasig.schedassist.messaging.PreferencesElement;
import org.jasig.schedassist.messaging.PreferencesSet;
import org.jasig.schedassist.messaging.RelationshipElement;
import org.jasig.schedassist.messaging.RelationshipList;
import org.jasig.schedassist.messaging.ScheduleOwnerElement;
import org.jasig.schedassist.messaging.VisibleScheduleRequest;
import org.jasig.schedassist.messaging.VisibleScheduleResponse;
import org.jasig.schedassist.messaging.VisitorConflictsRequest;
import org.jasig.schedassist.messaging.VisitorConflictsResponse;
import org.jasig.schedassist.messaging.XMLDataUtils;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableStatus;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.MeetingDurations;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.model.VisibleSchedule;
import org.jasig.schedassist.model.VisibleScheduleRequestConstraints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
/**
 * {@link Endpoint} implementation for Available, exposes some of the functionality
 * provided by {@link SchedulingAssistantService} and {@link RelationshipDao}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: RemoteAvailableServiceEndpoint.java 2976 2011-01-25 14:04:08Z npblair $
 */
@Endpoint("schedulingAssistantEndpoint")
public class SOAPSchedulingAssistantServiceEndpoint implements SOAPSchedulingAssistantOperations {

	private Log LOG = LogFactory.getLog(this.getClass());
	
	private SchedulingAssistantService schedulingAssistantService;
	private ICalendarAccountDao calendarAccountDao;
	private OwnerDao ownerDao;
	private VisitorDao visitorDao;
	private RelationshipDao relationshipDao;
	private AvailableScheduleDao availableScheduleDao;

	/**
	 * @param schedulingAssistantService the schedulingAssistantService to set
	 */
	@Autowired
	public void setAvailableService(SchedulingAssistantService schedulingAssistantService) {
		this.schedulingAssistantService = schedulingAssistantService;
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(final OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param visitorDao the visitorDao to set
	 */
	@Autowired
	public void setVisitorDao(final VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}
	/**
	 * @param relationshipDao the relationshipDao to set
	 */
	@Autowired
	public void setRelationshipDao(@Qualifier("composite") RelationshipDao relationshipDao) {
		this.relationshipDao = relationshipDao;
	}
	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(AvailableScheduleDao availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.remoting.soap.SOAPSchedulingAssistantOperations#isEligible(org.jasig.schedassist.messaging.IsEligibleRequest)
	 */
	@Override
	@PayloadRoot(localPart="IsEligibleRequest", namespace = "https://source.jasig.org/schemas/sched-assist")
	public IsEligibleResponse isEligible(IsEligibleRequest request) {
		boolean eligible = false;
		IsEligibleResponse response = new IsEligibleResponse();
		ICalendarAccount account = this.calendarAccountDao.getCalendarAccount(request.getVisitorNetid());
		if(null != account) {
			try {
				this.visitorDao.toVisitor(account);
				eligible = true;
			} catch (NotAVisitorException e) {
				LOG.debug(request.getVisitorNetid() + " not a visitor");
			}
		}
		
		response.setEligible(eligible);
		return response;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.remoting.soap.SOAPSchedulingAssistantOperations#getTargetAvailableBlock(org.jasig.schedassist.messaging.GetTargetAvailableBlockRequest)
	 */
	@Override
	@PayloadRoot(localPart = "GetTargetAvailableBlockRequest", namespace = "https://source.jasig.org/schemas/sched-assist")
	public GetTargetAvailableBlockResponse getTargetAvailableBlock(
			GetTargetAvailableBlockRequest request) throws NotRegisteredException, SchedulingException {
		if(LOG.isDebugEnabled()) {
			LOG.debug(request);
		}
		IScheduleOwner owner = ownerDao.locateOwnerByAvailableId(request.getOwnerId());
		if(null == owner) {
			throw new NotRegisteredException("no schedule owner found with id " + request.getOwnerId());
		}
		Date startTime = XMLDataUtils.convertXMLGregorianCalendarToDate(request.getStartTime());
		
		AvailableBlock targetBlock;
		if(request.isDoubleLength()) {
			targetBlock = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, startTime);
		} else {
			targetBlock = availableScheduleDao.retrieveTargetBlock(owner, startTime);
		}
		
		GetTargetAvailableBlockResponse response = new GetTargetAvailableBlockResponse();
		AvailableBlockElement element = createAvailableBlockElement(targetBlock);
		response.setAvailableBlockElement(element);
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.remoting.soap.SOAPSchedulingAssistantOperations#getVisibleSchedule(org.jasig.schedassist.messaging.VisibleScheduleRequest)
	 */
	@Override
	@PayloadRoot(localPart = "VisibleScheduleRequest", namespace = "https://source.jasig.org/schemas/sched-assist")
	public VisibleScheduleResponse getVisibleSchedule(final VisibleScheduleRequest request) throws NotAVisitorException, CalendarAccountNotFoundException, NotRegisteredException {	
		if(LOG.isDebugEnabled()) {
			LOG.debug(request);
		}
		ICalendarAccount visitorCalendarUser = calendarAccountDao.getCalendarAccount(
				request.getVisitorNetid());
		IScheduleVisitor visitor = visitorDao.toVisitor(visitorCalendarUser);
		
		IScheduleOwner owner = ownerDao.locateOwnerByAvailableId(request.getOwnerId());
		if(null == owner) {
			throw new NotRegisteredException(request.getOwnerId()  + " not currently registered as a schedule owner");
		}
	
		VisibleScheduleRequestConstraints requestConstraints = VisibleScheduleRequestConstraints.newInstance(owner, request.getWeekStart());
		
		VisibleScheduleResponse response = new VisibleScheduleResponse();
		MeetingDurations ownerDurations = owner.getPreferredMeetingDurations();
		PreferencesElement durationsElement = new PreferencesElement();
		durationsElement.setKey(Preferences.DURATIONS.getKey());
		durationsElement.setValue(ownerDurations.getKey());
		response.setOwnerMeetingDurationsPreference(durationsElement);
		
		VisibleSchedule schedule;
		if(owner.hasMeetingLimit()) {
			// we have to look at the whole visible schedule for attendings
			schedule = schedulingAssistantService.getVisibleSchedule(
					visitor, owner);
			if(owner.isExceedingMeetingLimit(schedule.getAttendingCount())) {	
				List<AvailableBlockElement> blockElementList = new ArrayList<AvailableBlockElement>();
				// return ONLY the attendings
				List<AvailableBlock> attendingList = schedule.getAttendingList();
				for(AvailableBlock b: attendingList) {
					AvailableBlockElement element = createAvailableBlockElement(b);
					element.setStatus(AvailableStatusType.ATTENDING);
					blockElementList.add(element);
				}
				
				AvailableBlockList listWrapper = new AvailableBlockList();
				listWrapper.getAvailableBlockElement().addAll(blockElementList);
				
				response.setAvailableBlockList(listWrapper);
				// set meetingLimitExceeded to true
				response.setMeetingLimitExceeded(true);
				
				// short-circuit
				return response;
			} else {
				// extract subset between start->end
				schedule = schedule.subset(requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
			}
		} else {
			// only pull start->end of schedule
			schedule = schedulingAssistantService.getVisibleSchedule(visitor, owner, requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
		}
		
		
		List<AvailableBlockElement> blockElementList = new ArrayList<AvailableBlockElement>();
		Map<AvailableBlock, AvailableStatus> blockMap = schedule.getBlockMap();
		for(Entry<AvailableBlock, AvailableStatus> entry: blockMap.entrySet()) {
			AvailableBlock block = entry.getKey();
			AvailableStatus status = entry.getValue();
			
			AvailableBlockElement element = createAvailableBlockElement(block);
			
			switch(status) {
			case FREE:
				element.setStatus(AvailableStatusType.FREE);
				break;
			case ATTENDING:
				element.setStatus(AvailableStatusType.ATTENDING);
				break;
			case BUSY: 
				element.setStatus(AvailableStatusType.BUSY);
				break;
			}
			
			blockElementList.add(element);
		}
		
		AvailableBlockList listWrapper = new AvailableBlockList();
		listWrapper.getAvailableBlockElement().addAll(blockElementList);
		
		response.setAvailableBlockList(listWrapper);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("visitor " + visitor + " requested visible schedule for owner " + owner);
		}
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.remoting.soap.SOAPSchedulingAssistantOperations#scheduleAppointment(org.jasig.schedassist.messaging.CreateAppointmentRequest)
	 */
	@Override
	@PayloadRoot(localPart = "CreateAppointmentRequest", namespace = "https://source.jasig.org/schemas/sched-assist")
	public CreateAppointmentResponse scheduleAppointment(final CreateAppointmentRequest request) throws NotAVisitorException, InputFormatException, SchedulingException, CalendarAccountNotFoundException, NotRegisteredException {
		ICalendarAccount visitorCalendarUser = calendarAccountDao.getCalendarAccount(
				request.getVisitorNetid());
		IScheduleVisitor visitor = visitorDao.toVisitor(visitorCalendarUser);
		
		IScheduleOwner owner = ownerDao.locateOwnerByAvailableId(request.getOwnerId());
		if(null == owner) {
			throw new NotRegisteredException(request.getOwnerId()  + " not currently registered as a schedule owner");
		}
		
		AvailableBlock block = availableScheduleDao.retrieveTargetBlock(owner, XMLDataUtils.convertXMLGregorianCalendarToDate(request.getStartTime()));
		if(null != block && block.getVisitorLimit() == 1 && owner.getPreferredMeetingDurations().isDoubleLength()) {
			if(request.getSelectedDuration() == owner.getPreferredMeetingDurations().getMaxLength()) {
				block = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, XMLDataUtils.convertXMLGregorianCalendarToDate(request.getStartTime()));
			}
		}
		if(null == block) {
			throw new SchedulingException("requested time is not available");
		}
		
		VEvent event = schedulingAssistantService.scheduleAppointment(visitor, 
				owner, 
				block, 
				request.getEventDescription());
		
		CreateAppointmentResponse response = new CreateAppointmentResponse();
		
		response.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(event.getStartDate().getDate()));
		response.setEndTime(XMLDataUtils.convertDateToXMLGregorianCalendar(event.getEndDate().getDate()));
		response.setEventLocation(event.getLocation().getValue());
		response.setEventTitle(event.getSummary().getValue());
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.remoting.soap.SOAPSchedulingAssistantOperations#cancelAppointment(org.jasig.schedassist.messaging.CancelAppointmentRequest)
	 */
	@Override
	@PayloadRoot(localPart = "CancelAppointmentRequest", namespace = "https://source.jasig.org/schemas/sched-assist")
	public CancelAppointmentResponse cancelAppointment(final CancelAppointmentRequest request) throws NotAVisitorException, InputFormatException, SchedulingException, CalendarAccountNotFoundException, NotRegisteredException {
		ICalendarAccount visitorCalendarUser = calendarAccountDao.getCalendarAccount(
				request.getVisitorNetid());
		IScheduleVisitor visitor = visitorDao.toVisitor(visitorCalendarUser);
		
		IScheduleOwner owner = ownerDao.locateOwnerByAvailableId(request.getOwnerId());
		if(null == owner) {
			throw new NotRegisteredException(request.getOwnerId()  + " not currently registered as a schedule owner");
		}
		
		if(null == request.getStartTime() || null == request.getEndTime()) {
			throw new InputFormatException("start and/or end time not properly set");
		}
		Date startTime = XMLDataUtils.convertXMLGregorianCalendarToDate(request.getStartTime());
		if(LOG.isDebugEnabled()) {
			LOG.debug("start time: " + startTime);
		}
		Date endTime = XMLDataUtils.convertXMLGregorianCalendarToDate(request.getEndTime());
		if(LOG.isDebugEnabled()) {
			LOG.debug("end time: " + endTime);
		}
		AvailableBlock targetBlock = availableScheduleDao.retrieveTargetBlock(owner, startTime);
		if(null == targetBlock) {
			throw new SchedulingException("requested time is not available in schedule");
		}
		if(!targetBlock.getEndTime().equals(endTime)) {
			// the returned block doesn't match the specified end time - try grabbing doublelength
			targetBlock = availableScheduleDao.retrieveTargetDoubleLengthBlock(owner, startTime);
			if(null == targetBlock || !targetBlock.getEndTime().equals(endTime)) {
				throw new SchedulingException("requested time is not available in schedule");
			} 
		}
		
		VEvent existingEvent = schedulingAssistantService.getExistingAppointment(targetBlock, owner);
		schedulingAssistantService.cancelAppointment(visitor, owner, existingEvent, targetBlock, request.getReason());
		
		CancelAppointmentResponse response = new CancelAppointmentResponse();
		response.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(targetBlock.getStartTime()));
		response.setEndTime(XMLDataUtils.convertDateToXMLGregorianCalendar(targetBlock.getEndTime()));

		return response;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.remoting.soap.SOAPSchedulingAssistantOperations#getRelationships(org.jasig.schedassist.messaging.GetRelationshipsRequest)
	 */
	@Override
	@PayloadRoot(localPart = "GetRelationshipsRequest", namespace = "https://source.jasig.org/schemas/sched-assist")
	public GetRelationshipsResponse getRelationships(
			final GetRelationshipsRequest request) throws 
			NotAVisitorException, CalendarAccountNotFoundException {
		ICalendarAccount visitorCalendarUser = calendarAccountDao.getCalendarAccount(
				request.getVisitorNetid());
		IScheduleVisitor visitor = visitorDao.toVisitor(visitorCalendarUser);
		
		List<Relationship> relationships = relationshipDao.forVisitor(visitor);
		
		List<RelationshipElement> elements = new ArrayList<RelationshipElement>();
		for(Relationship relationship : relationships) {
			IScheduleOwner owner = relationship.getOwner();
			ScheduleOwnerElement ownerElement = createScheduleOwnerElement(owner);

			RelationshipElement element = new RelationshipElement();
			element.setDescription(relationship.getDescription());
			element.setScheduleOwnerElement(ownerElement);
			elements.add(element);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("visitor: " + visitor + ", relationships: " + relationships);
		}
		RelationshipList relationshipList = new RelationshipList();
		relationshipList.getRelationshipElement().addAll(elements);
		
		GetRelationshipsResponse response = new GetRelationshipsResponse();
		response.setRelationshipList(relationshipList);
		return response;
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.remoting.soap.SOAPSchedulingAssistantOperations#getScheduleOwnerById(org.jasig.schedassist.messaging.GetScheduleOwnerByIdRequest)
	 */
	@Override
	@PayloadRoot(localPart = "GetScheduleOwnerByIdRequest", namespace = "https://source.jasig.org/schemas/sched-assist")
	public GetScheduleOwnerByIdResponse getScheduleOwnerById(
			GetScheduleOwnerByIdRequest request) throws CalendarAccountNotFoundException, NotRegisteredException {
		IScheduleOwner owner = ownerDao.locateOwnerByAvailableId(request.getId());
		GetScheduleOwnerByIdResponse response = new GetScheduleOwnerByIdResponse();
		if(null != owner) {
			ScheduleOwnerElement element = createScheduleOwnerElement(owner);
			response.setScheduleOwnerElement(element);
			return response;
		} else {
			throw new NotRegisteredException("no registered ScheduleOwner found for available id: " + request.getId());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.remoting.soap.SOAPSchedulingAssistantOperations#getVisitorConflicts(org.jasig.schedassist.messaging.VisitorConflictsRequest)
	 */
	@Override
	@PayloadRoot(localPart = "VisitorConflictsRequest", namespace = "https://source.jasig.org/schemas/sched-assist")
	public VisitorConflictsResponse getVisitorConflicts(
			VisitorConflictsRequest request) throws NotAVisitorException, NotRegisteredException {
		if(LOG.isDebugEnabled()) {
			LOG.debug(request);
		}
		ICalendarAccount visitorCalendarUser = calendarAccountDao.getCalendarAccount(
				request.getVisitorNetid());
		IScheduleVisitor visitor = visitorDao.toVisitor(visitorCalendarUser);
		IScheduleOwner owner = ownerDao.locateOwnerByAvailableId(request.getOwnerId());
		if(null == owner) {
			throw new NotRegisteredException(request.getOwnerId()  + " not currently registered as a schedule owner");
		}
		
		VisibleScheduleRequestConstraints requestConstraints = VisibleScheduleRequestConstraints.newInstance(owner, request.getWeekStart());
		
		List<AvailableBlock> conflicts = this.schedulingAssistantService.calculateVisitorConflicts(visitor, owner,
				requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
		List<AvailableBlockElement> blockElementList = new ArrayList<AvailableBlockElement>();
		for(AvailableBlock conflict: conflicts) {
			AvailableBlockElement element = createAvailableBlockElement(conflict);
			element.setStatus(AvailableStatusType.BUSY);
			blockElementList.add(element);
		}
		
		AvailableBlockList listWrapper = new AvailableBlockList();
		listWrapper.getAvailableBlockElement().addAll(blockElementList);
		
		VisitorConflictsResponse response = new VisitorConflictsResponse();
		response.setAvailableBlockList(listWrapper);
		
		return response;
	}
	
	/**
	 * Convert an {@link AvailableBlock} into an {@link AvailableBlockElement}.
	 * 
	 * @param block
	 * @return
	 */
	protected AvailableBlockElement createAvailableBlockElement(final AvailableBlock block) {
		if(null == block) {
			return null;
		}
		AvailableBlockElement element = new AvailableBlockElement();
		element.setEndTime(XMLDataUtils.convertDateToXMLGregorianCalendar(block.getEndTime()));
		element.setStartTime(XMLDataUtils.convertDateToXMLGregorianCalendar(block.getStartTime()));
		element.setVisitorLimit(block.getVisitorLimit());
		element.setVisitorsAttending(block.getVisitorsAttending());
		return element;
	}
	/**
	 * Convert a {@link IScheduleOwner} into a {@link ScheduleOwnerElement}.
	 * 
	 * @param owner
	 * @return
	 */
	protected static ScheduleOwnerElement createScheduleOwnerElement(final IScheduleOwner owner) {
		ScheduleOwnerElement element = new ScheduleOwnerElement();
		element.setFullName(owner.getCalendarAccount().getDisplayName());
		element.setId(owner.getId());
		element.setNetid(owner.getCalendarAccount().getUsername());
		PreferencesSet prefSet = new PreferencesSet();
		element.setPreferencesSet(prefSet);
		
		Map<Preferences, String> ownerPreferences = owner.getPreferences();
		for(Entry<Preferences, String> entry : ownerPreferences.entrySet() ) {
			PreferencesElement prefElement = new PreferencesElement();
			prefElement.setKey(entry.getKey().getKey());
			prefElement.setValue(entry.getValue());
			element.getPreferencesSet().getPreferencesElement().add(prefElement);
		}
		
		return element;
	}

}
