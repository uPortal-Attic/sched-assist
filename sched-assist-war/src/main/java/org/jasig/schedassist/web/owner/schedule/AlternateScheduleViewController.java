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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * This {@link Controller} returns the schedule data for 
 * the current authenticated {@link IScheduleOwner}. It's intended to
 * be used with the non-Javascript enabled view (schedule-noscript.jsp).
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AlternateScheduleViewController.java 2070 2010-04-30 16:52:11Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/schedule-noscript.html","/delegate/schedule-noscript.html" })
public class AlternateScheduleViewController {

	private Log LOG = LogFactory.getLog(this.getClass());
	
	private AvailableScheduleDao availableScheduleDao;	
	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(AvailableScheduleDao availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
	}

	/**
	 * 
	 * @param startParam
	 * @param response
	 * @return
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView displaySchedule(@RequestParam(value="startDate", required=false) String startParam, HttpServletResponse response) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		
		// grab the intended date period from the request
		Date startDate = new Date();
		if(null != startParam) {
			SimpleDateFormat df = CommonDateOperations.getDateFormat();
			try {
				startDate = df.parse(startParam);
			} catch (ParseException e) {
				LOG.debug("ignoring unparseable startDate: " + startDate);
			}
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		Date weekStart = CommonDateOperations.calculateSundayPrior(startDate);
		Date weekEnd = DateUtils.addDays(weekStart, 6);
		model.put("weekStart", weekStart);
		model.put("weekEnd", weekEnd);
		
		Date prevWeekStart = DateUtils.addDays(weekStart, -7);
		model.put("prevWeekStart", prevWeekStart);
		Date nextWeekStart = DateUtils.addDays(weekEnd, 1);
		model.put("nextWeekStart", nextWeekStart);
		model.put("defaultMeetingLocation", owner.getPreferredLocation());
		AvailableSchedule schedule = availableScheduleDao.retrieveWeeklySchedule(owner, weekStart);
		Set<AvailableBlock> blocks = AvailableBlockBuilder.combine(schedule.getAvailableBlocks());
		model.put("scheduleBlocks", blocks);
		
		response.setHeader("Cache-Control", "no-cache");
		return new ModelAndView("owner-schedule/schedule-noscript", "model", model);
	}
	

}
