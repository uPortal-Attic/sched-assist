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


package org.jasig.schedassist.web.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.CalendarAccountNotFoundException;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.impl.visitor.VisitorDao;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.VisibleSchedule;
import org.jasig.schedassist.model.VisibleScheduleRequestConstraints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

/**
 * Controller that displays the {@link VisibleSchedule} for a particular
 * {@link IScheduleOwner}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleScheduleDebugController.java 2978 2011-01-25 19:20:51Z npblair $
 */
@Controller
public class VisibleScheduleDebugController {

	private Log LOG = LogFactory.getLog(this.getClass());
	private SchedulingAssistantService schedulingAssistantService;
	private ICalendarAccountDao calendarAccountDao;
	private OwnerDao ownerDao;
	private VisitorDao visitorDao;
	private ICalendarDataDao calendarDataDao;
	/**
	 * @param schedulingAssistantService the availableService to set
	 */
	@Autowired
	public void setSchedulingAssistantService(SchedulingAssistantService schedulingAssistantService) {
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
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param visitorDao the visitorDao to set
	 */
	@Autowired
	public void setVisitorDao(VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}
	/**
	 * @param calendarDataDao the calendarDao to set
	 */
	@Autowired
	public void setCalendarDataDao(ICalendarDataDao calendarDataDao) {
		this.calendarDataDao = calendarDataDao;
	}
	
	/**
	 * 
	 * @param ownerId
	 * @param highContrast
	 * @param visitorUsername
	 * @return
	 * @throws NotAVisitorException
	 * @throws CalendarUserNotFoundException 
	 */
	@RequestMapping(value="/admin/schedule-debug/{ownerIdentifier}/view.html", method=RequestMethod.GET)
	public String displaySchedule(@PathVariable("ownerIdentifier") long ownerIdentifier, 
			@RequestParam(value="highContrast", required=false, defaultValue="false") boolean highContrast,
			@RequestParam(value="weekStart", required=false, defaultValue="0") int weekStart,
			@RequestParam(value="visitorUsername", required=true) String visitorUsername,
			ModelMap model) throws NotAVisitorException, CalendarAccountNotFoundException {
		
		ICalendarAccount visitorAccount = this.calendarAccountDao.getCalendarAccount(visitorUsername);
		if(visitorAccount == null) {
			throw new NotAVisitorException(visitorUsername + " not found");
		}
		IScheduleVisitor visitor = this.visitorDao.toVisitor(visitorAccount);
		model.addAttribute("visitor", visitor);
		IScheduleOwner selectedOwner = ownerDao.locateOwnerByAvailableId(ownerIdentifier);
		if(selectedOwner == null) {
			throw new CalendarAccountNotFoundException("no owner found for id " + ownerIdentifier);
		}
		VisibleScheduleRequestConstraints requestConstraints = VisibleScheduleRequestConstraints.newInstance(selectedOwner, weekStart);
		if(LOG.isDebugEnabled()) {
			LOG.debug("displaySchedule request, visitor: " + visitor + "; weekStart: " + weekStart + "requestConstraints " + requestConstraints);
		}
		
		Calendar ownerCalendar = this.calendarDataDao.getCalendar(selectedOwner.getCalendarAccount(), requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
		model.addAttribute("ownerCalendarData", ownerCalendar.toString());
		model.addAttribute("noteboard", selectedOwner.getPreference(Preferences.NOTEBOARD));
		model.addAttribute("owner", selectedOwner);
		model.addAttribute("highContrast", highContrast);
		
		VisibleSchedule schedule;

		if(selectedOwner.hasMeetingLimit()) {
			// we have to look at the whole visible schedule for attendings
			schedule = schedulingAssistantService.getVisibleSchedule(
					visitor, selectedOwner);
			if(selectedOwner.isExceedingMeetingLimit(schedule.getAttendingCount())) {	
				// return attending only view
				List<AvailableBlock> attendingList = schedule.getAttendingList();
				model.addAttribute("attendingList", attendingList);
				return "admin/debug-already-attending";
			} else {
				// extract start->end from visibleSchedule
				schedule = schedule.subset(requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
			}
			
		} else {	
			// only pull start->end of schedule
			schedule = schedulingAssistantService.getVisibleSchedule(
					visitor, selectedOwner, requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
		}
		
		model.addAttribute("visibleSchedule", schedule);
		model.addAttribute("scheduleStart", schedule.getScheduleStart());
		model.addAttribute("prevWeekStart", requestConstraints.getPrevWeekIndex());
		model.addAttribute("nextWeekStart", requestConstraints.getNextWeekIndex());
		model.addAttribute("weekStart", requestConstraints.getConstrainedWeekStart());
		model.addAttribute("ownerVisitorSamePerson", selectedOwner.isSamePerson(visitor));
		return "admin/debug-visible-schedule";
	}
	
	/**
	 * 
	 * @param ownerIdentifier
	 * @param weekStart
	 * @param visitorUsername
	 * @param model
	 * @return
	 * @throws NotAVisitorException 
	 * @throws CalendarUserNotFoundException 
	 */
	@RequestMapping(value="/admin/schedule-debug/{ownerIdentifier}/visitor-conflicts.json", method=RequestMethod.GET)
	public View visitorConflicts(@PathVariable("ownerIdentifier") long ownerIdentifier, 
			@RequestParam(value="weekStart", required=false, defaultValue="1") int weekStart,
			@RequestParam(value="visitorUsername", required=true) String visitorUsername,
			final ModelMap model) throws NotAVisitorException, CalendarAccountNotFoundException {
		
		ICalendarAccount visitorAccount = this.calendarAccountDao.getCalendarAccount(visitorUsername);
		if(visitorAccount == null) {
			throw new NotAVisitorException(visitorUsername + " not found");
		}
		IScheduleVisitor visitor = this.visitorDao.toVisitor(visitorAccount);
	
		IScheduleOwner owner = ownerDao.locateOwnerByAvailableId(ownerIdentifier);
		if(owner == null) {
			throw new CalendarAccountNotFoundException("no owner found for id " + ownerIdentifier);
		}
		VisibleScheduleRequestConstraints requestConstraints = VisibleScheduleRequestConstraints.newInstance(owner, weekStart);
		
		List<AvailableBlock> visitorConflicts = this.schedulingAssistantService.calculateVisitorConflicts(visitor, owner, 
				requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
		List<String> conflictBlocks = new ArrayList<String>();
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		for(AvailableBlock b: visitorConflicts) {
			conflictBlocks.add(df.format(b.getStartTime()));
		}
		model.addAttribute("conflicts", conflictBlocks);
		
		Calendar visitorCalendar = this.calendarDataDao.getCalendar(visitorAccount, 
				requestConstraints.getTargetStartDate(), requestConstraints.getTargetEndDate());
		model.addAttribute("visitorCalendarData", visitorCalendar.toString());
		return new MappingJacksonJsonView();
	}
	
}
