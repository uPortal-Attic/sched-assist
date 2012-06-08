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


package org.jasig.schedassist.web.owner.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;

import javax.validation.Valid;

import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * {@link Controller} for building a series of {@link AvailableBlock}s.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: BlockBuilderFormController.java 2070 2010-04-30 16:52:11Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/builder.html","/delegate/builder.html"})
public class BlockBuilderFormController {
	
	private AvailableScheduleDao availableScheduleDao;
	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(AvailableScheduleDao availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
	}
	/**
	 * @return the availableScheduleDao
	 */
	public AvailableScheduleDao getAvailableScheduleDao() {
		return availableScheduleDao;
	}
	/**
	 * 
	 * @param binder
	 */
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new BlockBuilderFormBackingObjectValidator());
    }
	/**
	 * 
	 * @param model
	 * @return
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String setupForm(final ModelMap model) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		int defaultVisitorLimit = Integer.parseInt(owner.getPreference(Preferences.DEFAULT_VISITOR_LIMIT));
		BlockBuilderFormBackingObject fbo = new BlockBuilderFormBackingObject();
		fbo.setVisitorsPerAppointment(defaultVisitorLimit);
		fbo.setMeetingLocation(owner.getPreferredLocation());
		model.addAttribute("command", fbo);
		return "owner-schedule/builder-form";
	}
	
	/**
	 * 
	 * @param fbo
	 * @return
	 * @throws InputFormatException
	 * @throws ParseException
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String updateSchedule(@Valid @ModelAttribute("command") BlockBuilderFormBackingObject fbo, BindingResult bindingResult) throws InputFormatException, ParseException, NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		
		if(bindingResult.hasErrors()) {
			return "owner-schedule/builder-form";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		String meetingLocation = owner.getPreferredLocation();
		if(meetingLocation.equals(fbo.getMeetingLocation())) {
			meetingLocation = null;
		}
		Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks(fbo.getStartTimePhrase(), 
				fbo.getEndTimePhrase(),
				fbo.getDaysOfWeekPhrase(),
				dateFormat.parse(fbo.getStartDatePhrase()),
				dateFormat.parse(fbo.getEndDatePhrase()),
				fbo.getVisitorsPerAppointment(),
				fbo.getMeetingLocation());
		
		availableScheduleDao.addToSchedule(owner, blocks);
		
		return "redirect:schedule.html";
	}
}
