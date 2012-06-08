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

import java.util.List;

import org.jasig.schedassist.impl.statistics.DailyEventSummary;
import org.jasig.schedassist.impl.statistics.StatisticsDao;
import org.jasig.schedassist.model.CommonDateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * {@link Controller} to show {@link DailyEventSummary} objects.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: EventsSummaryFormController.java 2978 2011-01-25 19:20:51Z npblair $
 */
@Controller
@RequestMapping("/admin/events-summary.html")
@SessionAttributes("command")
public class EventsSummaryFormController {

	private StatisticsDao statisticsDao;
	
	/**
	 * @param statisticsDao the statisticsDao to set
	 */
	@Autowired
	public void setStatisticsDao(StatisticsDao statisticsDao) {
		this.statisticsDao = statisticsDao;
	}

	/**
	 * @return the statisticsDao
	 */
	public StatisticsDao getStatisticsDao() {
		return statisticsDao;
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String setupForm(final ModelMap model) {
		DateRangeFormBackingObject fbo = new DateRangeFormBackingObject();
		model.put("command", fbo);
		return "admin/events-summary-form";
	}
	
	/**
	 * Display {@link DailyEventSummary} objects that correspond to the dates in the {@link DateRangeFormBackingObject}.
	 * 
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST) 
	protected String getEventCounts(@ModelAttribute("command") DateRangeFormBackingObject fbo, final ModelMap model) {
		List<DailyEventSummary> eventCounts = this.statisticsDao.getEventCounts(
				CommonDateOperations.beginningOfDay(fbo.getStart()), 
				CommonDateOperations.endOfDay(fbo.getEnd()));
		int rangeTotal = 0;
		for(DailyEventSummary s : eventCounts) {
			rangeTotal += s.getEventCount();
		}
		model.addAttribute("rangeTotal", rangeTotal);
		model.addAttribute("eventCounts", eventCounts);
		return "admin/events-summary-results";
	}
}
