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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.owner.PublicProfileDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.PublicProfile;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.model.VisibleScheduleRequestConstraints;
import org.jasig.schedassist.web.security.CalendarAccountUserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

/**
 * {@link Controller} that provides data about the current authenticated
 * {@link IScheduleVisitor}'s schedule.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisitorScheduleController.java $
 */
@Controller
public class VisitorScheduleController {

	private OwnerDao ownerDao;
	private RelationshipDao relationshipDao;
	private PublicProfileDao publicProfileDao;
	private SchedulingAssistantService schedulingAssistantService;
	
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param relationshipDao the relationshipDao to set
	 */
	@Autowired
	public void setRelationshipDao(@Qualifier("composite") RelationshipDao relationshipDao) {
		this.relationshipDao = relationshipDao;
	}
	/**
	 * @param publicProfileDao the publicProfileDao to set
	 */
	@Autowired
	public void setPublicProfileDao(PublicProfileDao publicProfileDao) {
		this.publicProfileDao = publicProfileDao;
	}
	/**
	 * @param schedulingAssistantService the availableService to set
	 */
	@Autowired
	public void setAvailableService(SchedulingAssistantService schedulingAssistantService) {
		this.schedulingAssistantService = schedulingAssistantService;
	}
	/**
	 * 
	 * @param ownerIdentifier
	 * @param weekStart
	 * @return
	 * @throws NotAVisitorException
	 * @throws OwnerNotFoundException
	 */
	@RequestMapping(value="/schedule/{ownerIdentifier}/visitor-conflicts.json", method=RequestMethod.GET)
	public View retrieveVisitorConflicts(@PathVariable("ownerIdentifier") String ownerIdentifier, 
			@RequestParam(value="weekStart", required=false, defaultValue="0") int weekStart,
			final ModelMap model) throws NotAVisitorException, OwnerNotFoundException {
		CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleVisitor visitor = currentUser.getScheduleVisitor();
		
		IScheduleOwner owner = locateOwnerFromIdentifier(ownerIdentifier, visitor);
		VisibleScheduleRequestConstraints requestConstraints = VisibleScheduleRequestConstraints.newInstance(owner, weekStart);
		
		List<AvailableBlock> visitorConflicts = this.schedulingAssistantService.calculateVisitorConflicts(visitor, 
				owner, requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
		List<String> conflictBlocks = new ArrayList<String>();
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		for(AvailableBlock b: visitorConflicts) {
			conflictBlocks.add(df.format(b.getStartTime()));
		}
		model.addAttribute("conflicts", conflictBlocks);
		return new MappingJacksonJsonView();
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
