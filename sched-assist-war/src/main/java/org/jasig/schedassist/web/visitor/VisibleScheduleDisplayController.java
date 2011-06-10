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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.owner.PublicProfileDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.PublicProfile;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.model.VisibleSchedule;
import org.jasig.schedassist.model.VisibleScheduleRequestConstraints;
import org.jasig.schedassist.web.security.CalendarAccountUserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that displays the {@link VisibleSchedule} for a particular
 * {@link IScheduleOwner}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleScheduleDisplayController.java 3006 2011-01-28 19:58:30Z npblair $
 */
@Controller
public class VisibleScheduleDisplayController {

	private Log LOG = LogFactory.getLog(this.getClass());
	private SchedulingAssistantService schedulingAssistantService;
	private RelationshipDao relationshipDao;
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
	 * 
	 * @param ownerId
	 * @param highContrast
	 * @return
	 * @throws NotAVisitorException
	 * @throws OwnerNotFoundException
	 */
	@RequestMapping(value="/schedule/{ownerIdentifier}/view.html", method=RequestMethod.GET)
	public ModelAndView displaySchedule(@PathVariable("ownerIdentifier") String ownerIdentifier, 
			@RequestParam(value="highContrast", required=false, defaultValue="false") boolean highContrast,
			@RequestParam(value="weekStart", required=false, defaultValue="0") int weekStart) throws NotAVisitorException, OwnerNotFoundException {
		CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleVisitor visitor = currentUser.getScheduleVisitor();

		IScheduleOwner selectedOwner = locateOwnerFromIdentifier(ownerIdentifier, visitor);
		VisibleScheduleRequestConstraints requestConstraints = VisibleScheduleRequestConstraints.newInstance(selectedOwner, weekStart);
		if(LOG.isDebugEnabled()) {
			LOG.debug("displaySchedule request, currentUser: " + currentUser + "; weekStart: " + weekStart + "requestConstraints " + requestConstraints);
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		final String noteboard = selectedOwner.getPreference(Preferences.NOTEBOARD);
		String [] noteboardSentences = noteboard.split("\n");
		model.put("noteboardSentences", noteboardSentences);
		model.put("owner", selectedOwner);
		model.put("highContrast", highContrast);
		
		VisibleSchedule schedule;

		if(selectedOwner.hasMeetingLimit()) {
			// we have to look at the whole visible schedule for attendings
			schedule = schedulingAssistantService.getVisibleSchedule(
					visitor, selectedOwner);
			if(selectedOwner.isExceedingMeetingLimit(schedule.getAttendingCount())) {	
				// return attending only view
				List<AvailableBlock> attendingList = schedule.getAttendingList();
				model.put("attendingList", attendingList);
				return new ModelAndView("visitor/visitor-already-attending", model);
			} else {
				// extract start->end from visibleSchedule
				schedule = schedule.subset(requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
			}
			
		} else {	
			// only pull start->end of schedule
			schedule = schedulingAssistantService.getVisibleSchedule(
					visitor, selectedOwner, requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
		}
		
		model.put("visibleSchedule", schedule);
		model.put("scheduleStart", schedule.getScheduleStart());
		model.put("prevWeekStart", requestConstraints.getPrevWeekIndex());
		model.put("nextWeekStart", requestConstraints.getNextWeekIndex());
		model.put("weekStart", requestConstraints.getConstrainedWeekStart());
		model.put("ownerVisitorSamePerson", selectedOwner.isSamePerson(visitor));
		return new ModelAndView("visitor/visitor-visible-schedule", model);
	}

	/**
	 * 
	 * @param ownerIdentifier
	 * @param visitor
	 * @return
	 * @throws OwnerNotFoundException
	 */
	private IScheduleOwner locateOwnerFromIdentifier(final String ownerIdentifier, final IScheduleVisitor visitor) throws OwnerNotFoundException {
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

		return selectedOwner;
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
