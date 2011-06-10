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

import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.impl.AvailableScheduleReflectionService;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * {@link Controller} for triggering available schedule
 * reflection manually.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableScheduleReflectionServiceController.java 2987 2011-01-27 16:56:38Z npblair $
 */
@Controller
@RequestMapping("/admin/reflection-service.html")
public class AvailableScheduleReflectionServiceController {

	private ICalendarAccountDao calendarAccountDao;
	private OwnerDao ownerDao;
	private AvailableScheduleReflectionService reflectionService;
	private AvailableScheduleDao availableScheduleDao;

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
	 * @param reflectionService the reflectionService to set
	 */
	@Autowired
	public void setReflectionService(
			AvailableScheduleReflectionService reflectionService) {
		this.reflectionService = reflectionService;
	}
	
	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(AvailableScheduleDao availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
	}
	
	@RequestMapping
	protected String setupForm(final ModelMap model) {
		ScheduleOwnerFormBackingObject command = new ScheduleOwnerFormBackingObject();
		model.addAttribute("command", command);
		return "admin/reflect-form";
	}
	
	@RequestMapping(method=RequestMethod.POST, params="action=reflect")
	protected String reflectAvailableSchedule(@ModelAttribute("command") ScheduleOwnerFormBackingObject command) {
		IScheduleOwner owner = null;
		if(command.getOwnerId() != null) {
			owner = this.ownerDao.locateOwnerByAvailableId(command.getOwnerId());
		} else {
			ICalendarAccount account = this.calendarAccountDao.getCalendarAccount(command.getUsername());
			if(account != null) {
				owner = this.ownerDao.locateOwner(account);
			}
		}
		
		if(owner == null) {
			return "owner-not-found";
		}
		
		this.reflectionService.reflectAvailableSchedule(owner);
		
		return "admin/reflect-success";
	}
	
	@RequestMapping(method=RequestMethod.POST, params="action=purge")
	protected String purgeReflections(ScheduleOwnerFormBackingObject command) {
		IScheduleOwner owner = null;
		if(command.getOwnerId() != null) {
			owner = this.ownerDao.locateOwnerByAvailableId(command.getOwnerId());
		} else {
			ICalendarAccount account = this.calendarAccountDao.getCalendarAccount(command.getUsername());
			if(account != null) {
				owner = this.ownerDao.locateOwner(account);
			}
		}
		
		if(owner == null) {
			return "admin/owner-not-found";
		}
		
		AvailableSchedule schedule = availableScheduleDao.retrieve(owner);
		if(!schedule.isEmpty()) {
			this.reflectionService.purgeReflections(owner, schedule.getScheduleStartTime(), schedule.getScheduleEndTime());
		}
		
		return "admin/reflect-purge-success";
	}
}
