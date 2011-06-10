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


package org.jasig.schedassist.web.owner.statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.impl.statistics.AppointmentEvent;
import org.jasig.schedassist.impl.statistics.StatisticsDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * {@link Controller} for displaying {@link IScheduleVisitor}
 * activity history with the authenticated {@link IScheduleOwner}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisitorHistoryFormController.java 2608 2010-09-16 19:03:42Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/visitor-history.html","/delegate/visitor-history.html"})
@SessionAttributes("command")
public class VisitorHistoryFormController {

	private StatisticsDao statisticsDao;
	private ICalendarAccountDao calendarAccountDao;
	/**
	 * @param statisticsDao the statisticsDao to set
	 */
	@Autowired
	public void setStatisticsDao(StatisticsDao statisticsDao) {
		this.statisticsDao = statisticsDao;
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	
	/**
	 * 
	 * @param binder
	 */
	@InitBinder(value="command")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new VisitorHistoryFormBackingObjectValidator());
        SimpleDateFormat dateFormat = new SimpleDateFormat(VisitorHistoryFormBackingObject.DATE_FORMAT);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String setupForm(@RequestParam(value="noscript",required=false,defaultValue="false") boolean noscript, final ModelMap model) {
		VisitorHistoryFormBackingObject fbo = new VisitorHistoryFormBackingObject();
		Date today = new Date();		
		Date sevenDaysAgo = DateUtils.addDays(today, -7);
		// truncate sevenDaysAgo to midnight
		sevenDaysAgo = DateUtils.truncate(sevenDaysAgo, Calendar.DATE);
		fbo.setStartTime(sevenDaysAgo);
		fbo.setEndTime(today);
		model.addAttribute("command", fbo);
		if(noscript) {
			return "statistics/visitor-history-form-noscript";
		} else {
			return "statistics/visitor-history-form";
		}
	}

	/**
	 * 
	 * @param fbo
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws NotRegisteredException 
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String submit(@Valid @ModelAttribute("command") VisitorHistoryFormBackingObject fbo, BindingResult bindingResult, ModelMap model) throws NotRegisteredException {
		if(bindingResult.hasErrors()) {
			return "statistics/visitor-history-form";
		}
		
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		
		ICalendarAccount visitorAccount = this.calendarAccountDao.getCalendarAccount(fbo.getVisitorUsername());
		model.addAttribute("visitorAccount", visitorAccount);
		if(null != visitorAccount) {
			List<AppointmentEvent> events = this.statisticsDao.getEvents(owner, fbo.getVisitorUsername(), fbo.getAdjustedStartTime(), fbo.getAdjustedEndTime());
			Collections.sort(events);
			model.addAttribute("events", events);
		}
		return "statistics/visitor-history-success";
	}
}
