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

import java.util.HashMap;
import java.util.Map;

import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.VisibleSchedule;
import org.jasig.schedassist.model.VisibleScheduleRequestConstraints;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * This {@link Controller} implementation generates a preview
 * for the {@link IScheduleOwner} of the display of their current {@link VisibleSchedule}
 * shown to the {@link IScheduleVisitor}.
 * 
 * Requires a {@link OwnerDao} and {@link AvailableService} be set.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PreviewVisibleScheduleController.java 3006 2011-01-28 19:58:30Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/preview.html", "/delegate/preview.html"})
public class PreviewVisibleScheduleController {

	private SchedulingAssistantService schedulingAssistantService;
	
	/**
	 * @param schedulingAssistantService the schedulingAssistantService to set
	 */
	@Autowired
	public void setSchedulingAssistantService(SchedulingAssistantService schedulingAssistantService) {
		this.schedulingAssistantService = schedulingAssistantService;
	}

	/**
	 * @return the schedulingAssistantService
	 */
	public SchedulingAssistantService getSchedulingAssistantService() {
		return schedulingAssistantService;
	}

	/**
	 * 
	 * @param highContrast
	 * @return
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected ModelAndView previewVisibleSchedule(@RequestParam(value="highContrast",required=false,defaultValue="false") boolean highContrast,
			@RequestParam(value="weekStart", required=false, defaultValue="0") int weekStart) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		VisibleScheduleRequestConstraints requestConstraints = VisibleScheduleRequestConstraints.newInstance(owner, weekStart);

		// only pull start->end of schedule
		VisibleSchedule schedule = schedulingAssistantService.getVisibleSchedule(
				null, owner, requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("prevWeekStart", requestConstraints.getPrevWeekIndex());
		model.put("nextWeekStart", requestConstraints.getNextWeekIndex());
		model.put("scheduleStart", schedule.getScheduleStart());
		model.put("visibleSchedule", schedule);
		final String noteboard = owner.getPreference(Preferences.NOTEBOARD);
		String [] noteboardSentences = noteboard.split("\n");
		model.put("noteboardSentences", noteboardSentences);
		model.put("owner", owner);
		model.put("highContrast", highContrast);
		model.put("weekStart", weekStart);
		return new ModelAndView("owner-schedule/preview-visible-schedule", model);
	}
	
}
