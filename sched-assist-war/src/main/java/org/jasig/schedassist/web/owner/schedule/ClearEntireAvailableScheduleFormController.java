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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.impl.AvailableScheduleReflectionService;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * {@link Controller} implementation that allows an {@link IScheduleOwner} to
 * remove ALL {@link AvailableBlock}s from their {@link AvailableSchedule}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ClearEntireAvailableScheduleFormController.java 2542 2010-09-13 16:07:29Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/clear-entire-schedule.html", "/delegate/clear-entire-schedule.html" })
public class ClearEntireAvailableScheduleFormController {

	protected final Log log = LogFactory.getLog(this.getClass());
	
	private AvailableScheduleDao availableScheduleDao;
	private AvailableScheduleReflectionService reflectionService;
	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(AvailableScheduleDao availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
	}
	/**
	 * @param reflectionService the reflectionService to set
	 */
	@Autowired
	public void setReflectionService(
			AvailableScheduleReflectionService reflectionService) {
		this.reflectionService = reflectionService;
	}

	/**
	 * @return the availableScheduleDao
	 */
	public AvailableScheduleDao getAvailableScheduleDao() {
		return availableScheduleDao;
	}
	/**
	 * @return the reflectionService
	 */
	public AvailableScheduleReflectionService getReflectionService() {
		return reflectionService;
	}
	/**
	 * 
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String setupForm(final ModelMap model) {
		ClearAvailableScheduleFormBackingObject fbo = new ClearAvailableScheduleFormBackingObject();
		model.addAttribute("command", fbo);
		return "owner-schedule/clear-schedule-form";
	}
	/**
	 * 
	 * @param fbo
	 * @return
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected ModelAndView clearSchedule(@ModelAttribute ClearAvailableScheduleFormBackingObject fbo) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		
		if(fbo.isConfirmedCancelAll()) {
			AvailableSchedule schedule = availableScheduleDao.retrieve(owner);
			if(!schedule.isEmpty()) {
				availableScheduleDao.clearAllBlocks(owner);
				reflectionService.purgeReflections(owner, 
					schedule.getScheduleStartTime(), schedule.getScheduleEndTime());
			}
			return new ModelAndView("owner-schedule/clear-schedule-success");
		} else {
			log.info("owner (" + owner + ") did not confirm request to clear schedule, cancelling");
			return new ModelAndView(new RedirectView("schedule.html", true));
		}
	}
	
	
}
