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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.NoAppointmentExistsException;
import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.owner.PublicProfileDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.AvailableVersion;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.PublicProfile;
import org.jasig.schedassist.model.Relationship;
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
 * {@link Controller} for {@link IScheduleVisitor}s to invoke
 * {@link AvailableService#cancelAppointment(IScheduleVisitor, IScheduleOwner, VEvent, AvailableBlock)}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Header: CancelAppointmentFormController.java $
 */
@Controller
@RequestMapping("/schedule/{ownerIdentifier}/cancel.html")
@SessionAttributes("command")
public class CancelAppointmentFormController {

	private SchedulingAssistantService schedulingAssistantService;
	private RelationshipDao relationshipDao;
	private AvailableScheduleDao availableScheduleDao;
	private OwnerDao ownerDao;
	private PublicProfileDao publicProfileDao;
	/**
	 * @param schedulingAssistantService the schedulingAssistantService to set
	 */
	@Autowired
	public void setAvailableService(SchedulingAssistantService schedulingAssistantService) {
		this.schedulingAssistantService = schedulingAssistantService;
	}
	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(AvailableScheduleDao availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
	}
	/**
	 * @param relationshipDao the relationshipDao to set
	 */
	@Autowired
	public void setRelationshipDao(@Qualifier("composite") RelationshipDao relationshipDao) {
		this.relationshipDao = relationshipDao;
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
	 * @return the schedulingAssistantService
	 */
	public SchedulingAssistantService getSchedulingAssistantService() {
		return schedulingAssistantService;
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
	 * 
	 * @param binder
	 */
	@InitBinder("command")
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new CancelAppointmentFormBackingObjectValidator());
	}
	/**
	 * 
	 * @param model
	 * @param startTimePhrase
	 * @param endTimePhrase
	 * @param ownerId
	 * @return
	 * @throws ParseException
	 * @throws NotAVisitorException
	 * @throws OwnerNotFoundException
	 * @throws InputFormatException
	 * @throws SchedulingException
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String setupForm(final ModelMap model, @RequestParam(value="startTime",required=true) String startTimePhrase, 
			@RequestParam(value="endTime",required=true) String endTimePhrase, @PathVariable("ownerIdentifier") String ownerIdentifier,
			@RequestParam(value="r", required=false, defaultValue="false") boolean redirectedFromCreate) throws ParseException, NotAVisitorException, OwnerNotFoundException, InputFormatException, SchedulingException {
		SimpleDateFormat dateFormat = CommonDateOperations.getDateTimeFormat();
		Date time = dateFormat.parse(startTimePhrase);
		model.put("redirected", redirectedFromCreate);
		model.put("startTimePhrase", startTimePhrase);
		model.put("startTime", time);
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
		Date endTime = CommonDateOperations.parseDateTimePhrase(endTimePhrase);
		
		// try to get the minimum size first
		AvailableBlock targetBlock = availableScheduleDao.retrieveTargetBlock(selectedOwner, startTime);
		if(null == targetBlock) {
			throw new SchedulingException("requested time is not available in schedule");
		} 
		
		if(!targetBlock.getEndTime().equals(endTime)) {
			// the returned block doesn't match the specified end time - try grabbing doublelength
			targetBlock = availableScheduleDao.retrieveTargetDoubleLengthBlock(selectedOwner, startTime);
		} 
		
		if(null != targetBlock && targetBlock.getEndTime().equals(endTime)) {
			VEvent existingEvent = schedulingAssistantService.getExistingAppointment(targetBlock, selectedOwner);
			if(null == existingEvent) {
				throw new NoAppointmentExistsException("no available appointment found in " + targetBlock);
			}
			if(null == existingEvent.getProperty(AvailableVersion.AVAILABLE_VERSION)) {
				// this is a pre-1.1 appointment, override block's visitor limit and set to 1
				targetBlock = AvailableBlockBuilder.createBlock(targetBlock.getStartTime(), targetBlock.getEndTime(), 1);
			}
			CancelAppointmentFormBackingObject fbo = new CancelAppointmentFormBackingObject(targetBlock);
			model.put("command", fbo);
			
			return "visitor/cancel-appointment-form";
		} else {
			throw new SchedulingException("requested time is not available in schedule");
		}
	}
	
	/**
	 * 
	 * @param fbo
	 * @param ownerId
	 * @return
	 * @throws NotAVisitorException
	 * @throws OwnerNotFoundException
	 * @throws SchedulingException
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String cancelAppointment(final ModelMap model, @PathVariable("ownerIdentifier") String ownerIdentifier,
			@Valid @ModelAttribute("command") CancelAppointmentFormBackingObject fbo, BindingResult bindingResult) throws NotAVisitorException, OwnerNotFoundException, SchedulingException {
		CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleVisitor visitor = currentUser.getScheduleVisitor();
		
		if(bindingResult.hasErrors()) {
			return "visitor/cancel-appointment-form";
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
		
		AvailableBlock block = fbo.getTargetBlock();
		
		VEvent existingEvent = schedulingAssistantService.getExistingAppointment(block, selectedOwner);
		if(null == existingEvent) {
			throw new NoAppointmentExistsException("no available appointment found in " + block);
		}
		
		schedulingAssistantService.cancelAppointment(visitor, selectedOwner, existingEvent, block, fbo.getReason());
		model.addAttribute("owner", selectedOwner);
		model.addAttribute("event", existingEvent);
		return "visitor/cancel-appointment-success";
	}
	
	/**
	 * 
	 * @param visitor
	 * @param ownerId
	 * @return
	 * @throws OwnerNotFoundException
	 */
	private IScheduleOwner findOwnerForVisitor(IScheduleVisitor visitor, long ownerId) throws OwnerNotFoundException {
		List<Relationship> relationships = relationshipDao.forVisitor(visitor);
		for(Relationship potential : relationships) {
			if(potential.getOwner().getId() == ownerId) {
				return potential.getOwner();
			}
		}
		
		throw new OwnerNotFoundException("owner id " + ownerId + " not found");
	}
	

}
