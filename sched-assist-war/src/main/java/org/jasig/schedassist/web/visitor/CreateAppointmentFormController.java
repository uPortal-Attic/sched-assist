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


package org.jasig.schedassist.web.visitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.owner.PublicProfileDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.IEventUtils;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.PublicProfile;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.model.VisibleSchedule;
import org.jasig.schedassist.model.VisibleWindow;
import org.jasig.schedassist.web.security.CalendarAccountUserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * {@link Controller} for {@link IScheduleVisitor}s to create appointments 
 * via an {@link AvailableService}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CreateAppointmentFormController.java 2332 2010-08-02 17:56:49Z npblair $
 */
@Controller
@RequestMapping("/schedule/{ownerIdentifier}/create.html")
@SessionAttributes("command")
public class CreateAppointmentFormController {

	private static final String COMMAND_ATTR_NAME = "command";
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SchedulingAssistantService schedulingAssistantService;
	private RelationshipDao relationshipDao;
	private AvailableScheduleDao availableScheduleDao;
	private OwnerDao ownerDao;
	private PublicProfileDao publicProfileDao;
	private IEventUtils eventUtils;
	
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
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param publicProfileDao the publicProfileDao to set
	 */
	@Autowired
	public void setPublicProfileDao(PublicProfileDao publicProfileDao) {
		this.publicProfileDao = publicProfileDao;
	}
	/**
	 * @param eventUtils the eventUtils to set
	 */
	@Autowired
	public void setEventUtils(IEventUtils eventUtils) {
		this.eventUtils = eventUtils;
	}
	/**
	 * @return the schedulingAssistantService
	 */
	public SchedulingAssistantService getSchedulingAssistantService() {
		return schedulingAssistantService;
	}
	/**
	 * @param schedulingAssistantService the schedulingAssistantService to set
	 */
	@Autowired
	public void setSchedulingAssistantService(
			SchedulingAssistantService schedulingAssistantService) {
		this.schedulingAssistantService = schedulingAssistantService;
	}
	/**
	 * @return the relationshipDao
	 */
	public RelationshipDao getRelationshipDao() {
		return relationshipDao;
	}
	/**
	 * @return the availableScheduleDao
	 */
	public AvailableScheduleDao getAvailableScheduleDao() {
		return availableScheduleDao;
	}
	/**
	 * @return the ownerDao
	 */
	public OwnerDao getOwnerDao() {
		return ownerDao;
	}
	/**
	 * @return the publicProfileDao
	 */
	public PublicProfileDao getPublicProfileDao() {
		return publicProfileDao;
	}
	/**
	 * @return the eventUtils
	 */
	public IEventUtils getEventUtils() {
		return eventUtils;
	}
	/**
	 * 
	 * @param binder
	 */
	@InitBinder(COMMAND_ATTR_NAME)
	protected void initBinder(final WebDataBinder binder) {
		binder.setValidator(new CreateAppointmentFormBackingObjectValidator());
	}
	/**
	 * 
	 * @param model
	 * @param startTimePhrase
	 * @param ownerId
	 * @return
	 * @throws InputFormatException
	 * @throws SchedulingException
	 * @throws OwnerNotFoundException
	 * @throws NotAVisitorException
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String setupForm(final ModelMap model, @RequestParam(value="startTime",required=true) final String startTimePhrase, @PathVariable("ownerIdentifier") final String ownerIdentifier) throws InputFormatException, SchedulingException, OwnerNotFoundException, NotAVisitorException {
		CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleVisitor visitor = currentUser.getScheduleVisitor();
	
		IScheduleOwner selectedOwner = null;
		if(StringUtils.isNumeric(ownerIdentifier)) {
			Long ownerId = Long.parseLong(ownerIdentifier);
			selectedOwner = findOwnerForVisitor(visitor, ownerId);
		} else {
			PublicProfile profile = publicProfileDao.locatePublicProfileByKey(ownerIdentifier);
			if(null != profile) {
				selectedOwner = ownerDao.locateOwnerByAvailableId(profile.getOwnerId());
			}
		}
		
		if(null == selectedOwner) {
			throw new OwnerNotFoundException("no owner found for " + ownerIdentifier);
		}
		model.put("owner", selectedOwner);
		Date startTime = CommonDateOperations.parseDateTimePhrase(startTimePhrase);
		validateChosenStartTime(selectedOwner.getPreferredVisibleWindow(), startTime);
		
		AvailableBlock targetBlock = availableScheduleDao.retrieveTargetBlock(selectedOwner, startTime);
		if(null == targetBlock) {
			throw new SchedulingException("requested time is not available");
		} 
		
		if(selectedOwner.hasMeetingLimit()) {
			VisibleSchedule sched = schedulingAssistantService.getVisibleSchedule(visitor, selectedOwner);
			int attendingCount = sched.getAttendingCount();
			if(selectedOwner.isExceedingMeetingLimit(attendingCount)) {
				// visitor has already matched owner's appointment limit
				log.warn("blocked attempt to use create form by visitor: " + visitor + ", target owner: " + selectedOwner);
				return "redirect:view.html";
			}
		}
		
		VEvent event = schedulingAssistantService.getExistingAppointment(targetBlock, selectedOwner);
		if(event != null) {
			model.put("event", event);
			if(this.eventUtils.isAttendingAsVisitor(event, visitor.getCalendarAccount())) {
				// redirect the visitor to the cancel form 
				StringBuilder redirect = new StringBuilder("redirect:cancel.html?r=true&startTime=");
				SimpleDateFormat dateFormat = CommonDateOperations.getDateTimeFormat();
				redirect.append(startTimePhrase);
				redirect.append("&endTime=");
				redirect.append(dateFormat.format(targetBlock.getEndTime()));
				return redirect.toString();
			}
			
			Integer visitorLimit = this.eventUtils.getEventVisitorLimit(event);
			model.put("visitorLimit", visitorLimit);
			if(this.eventUtils.getScheduleVisitorCount(event) >= visitorLimit) {
				return "visitor/appointment-full";
			}
		}
		CreateAppointmentFormBackingObject fbo = new CreateAppointmentFormBackingObject(targetBlock, selectedOwner.getPreferredMeetingDurations());
		model.addAttribute(COMMAND_ATTR_NAME, fbo);
		return "visitor/create-appointment-form";
	}
	
	/**
	 * 
	 * @param ownerIdentifier
	 * @param model
	 * @param fbo
	 * @param bindingResult
	 * @return
	 * @throws NotAVisitorException
	 * @throws OwnerNotFoundException
	 * @throws SchedulingException
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String createAppointment(final ModelMap model, @PathVariable("ownerIdentifier") final String ownerIdentifier,
			@Valid @ModelAttribute(COMMAND_ATTR_NAME) final CreateAppointmentFormBackingObject fbo, BindingResult bindingResult) throws NotAVisitorException, OwnerNotFoundException, SchedulingException {
		CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleVisitor visitor = currentUser.getScheduleVisitor();
		
		if(bindingResult.hasErrors()) {
			return "visitor/create-appointment-form";
		}
		
		IScheduleOwner selectedOwner = null;
		if(StringUtils.isNumeric(ownerIdentifier)) {
			Long ownerId = Long.parseLong(ownerIdentifier);
			selectedOwner = findOwnerForVisitor(visitor, ownerId);
		} else {
			PublicProfile profile = publicProfileDao.locatePublicProfileByKey(ownerIdentifier);
			if(null != profile) {
				selectedOwner = ownerDao.locateOwnerByAvailableId(profile.getOwnerId());
			}
		}
		
		if(null == selectedOwner) {
			throw new OwnerNotFoundException("no owner found for " + ownerIdentifier);
		}
		
		validateChosenStartTime(selectedOwner.getPreferredVisibleWindow(), fbo.getTargetBlock().getStartTime());
		
		AvailableBlock finalAppointmentBlock = fbo.getTargetBlock();
		if(fbo.isDoubleLengthAvailable()) {
			// check if selected meeting duration matches meeting durations maxLength
			// if it's greater, then we need to look up the next block in the schedule and attempt to combine
			if(fbo.getSelectedDuration() == fbo.getMeetingDurations().getMaxLength()) {
				finalAppointmentBlock = availableScheduleDao.retrieveTargetDoubleLengthBlock(selectedOwner, finalAppointmentBlock.getStartTime());
			}
		}
		if(null == finalAppointmentBlock) {
			throw new SchedulingException("requested time is not available");
		}
		
		VEvent event = schedulingAssistantService.scheduleAppointment(visitor, selectedOwner, finalAppointmentBlock, fbo.getReason());
		model.put("event", event);
		model.put("owner", selectedOwner);
		model.put("ownerRemindersPreference", selectedOwner.getRemindersPreference());
		return "visitor/create-appointment-success";
	}

	/**
	 * Verify the startTime argument is within the window; throws a {@link ScheduleException} if not.
	 * 
	 * @param window
	 * @param startTime
	 * @throws SchedulingException
	 */
	protected void validateChosenStartTime(VisibleWindow window, Date startTime) throws SchedulingException {
		final Date currentWindowStart = window.calculateCurrentWindowStart();
		final Date currentWindowEnd = window.calculateCurrentWindowEnd();
		if(startTime.before(currentWindowStart) || startTime.equals(currentWindowEnd) || startTime.after(currentWindowEnd)) {
			throw new SchedulingException("requested time is no longer within visible window");
		}
	}
	/**
	 * 
	 * @param visitor
	 * @param ownerId
	 * @return
	 * @throws OwnerNotFoundException
	 */
	private IScheduleOwner findOwnerForVisitor(final IScheduleVisitor visitor, final long ownerId) throws OwnerNotFoundException {
		List<Relationship> relationships = relationshipDao.forVisitor(visitor);
		for(Relationship potential : relationships) {
			if(potential.getOwner().getId() == ownerId) {
				return potential.getOwner();
			}
		}
		
		throw new OwnerNotFoundException("owner id " + ownerId + " not found");
	}

}
