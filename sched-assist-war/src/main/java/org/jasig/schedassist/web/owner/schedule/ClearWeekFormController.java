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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * {@link Controller} implementation that allows an {@link IScheduleOwner}
 * to remove a week of {@link AvailableBlock}s from their {@link AvailableSchedule}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Idr: ClearWeekFormController.java $
 */
@Controller
@RequestMapping(value={"/owner/clear-week.html", "/delegate/clear-week.html" })
public class ClearWeekFormController {

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
	 * @param binder
	 */
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new ClearAvailableScheduleFormBackingObjectValidator());
    }
	/**
	 * 
	 * @param weekOfPhrase
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String setupForm(@RequestParam(value="weekOf",required=false) String weekOfPhrase, final ModelMap model) {
		ClearAvailableScheduleFormBackingObject fbo = new ClearAvailableScheduleFormBackingObject();
		if(StringUtils.isBlank(weekOfPhrase)) {
			SimpleDateFormat df = CommonDateOperations.getDateFormat();
			weekOfPhrase = df.format(CommonDateOperations.calculateSundayPrior(new Date()));
		}
		fbo.setWeekOfPhrase(weekOfPhrase);
		model.addAttribute("command", fbo);
		return "owner-schedule/clear-week-form";
	}
	/**
	 * 
	 * @param fbo
	 * @return
	 * @throws NotRegisteredException
	 * @throws InputFormatException
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected ModelAndView clearWeek(@Valid ClearAvailableScheduleFormBackingObject fbo) throws NotRegisteredException, InputFormatException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		
		if(fbo.isConfirmedCancelWeek()) {
			Date weekOf = CommonDateOperations.parseDatePhrase(fbo.getWeekOfPhrase());
			AvailableSchedule scheduleWeekOf = availableScheduleDao.retrieveWeeklySchedule(owner, weekOf);
			availableScheduleDao.removeFromSchedule(owner, scheduleWeekOf.getAvailableBlocks());
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("weekOf", weekOf);
			return new ModelAndView("owner-schedule/clear-week-success", model);
		} else {
			LOG.info("owner (" + owner + ") did not confirm request to clear schedule for weekOf " + fbo.getWeekOfPhrase() + ", cancelling");
			return new ModelAndView(new RedirectView("schedule.html", true));
		}
	}

}
