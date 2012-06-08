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


package org.jasig.schedassist.web.register;

import java.util.Date;

import javax.validation.Valid;

import org.jasig.schedassist.impl.AvailableScheduleReflectionService;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.IScheduleOwner;
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
 * Form tied to {@link OwnerDao#removeAccount(IScheduleOwner)}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: RemoveAccountFormController.java 2696 2010-09-24 14:05:05Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/removeAccount.html","/delegate/removeAccount.html"})
public class RemoveAccountFormController  {

	private OwnerDao ownerDao;
	private AvailableScheduleDao availableScheduleDao;
	private AvailableScheduleReflectionService availableScheduleReflectionService;
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(AvailableScheduleDao availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
	}
	/**
	 * @param availableScheduleReflectionService the availableScheduleReflectionService to set
	 */
	@Autowired
	public void setAvailableScheduleReflectionService(
			AvailableScheduleReflectionService availableScheduleReflectionService) {
		this.availableScheduleReflectionService = availableScheduleReflectionService;
	}
	/**
	 * @return the ownerDao
	 */
	public OwnerDao getOwnerDao() {
		return ownerDao;
	}
	/**
	 * @return the availableScheduleDao
	 */
	public AvailableScheduleDao getAvailableScheduleDao() {
		return availableScheduleDao;
	}
	/**
	 * @return the availableScheduleReflectionService
	 */
	public AvailableScheduleReflectionService getAvailableScheduleReflectionService() {
		return availableScheduleReflectionService;
	}
	/**
	 * 
	 * @param binder
	 */
	@InitBinder("command")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new RemoveAccountFormBackingObjectValidator());
    }
	/**
	 * 
	 * @return the view name to use to display the remove account form
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String displayForm(final ModelMap model) {
		model.addAttribute("command", new RemoveAccountFormBackingObject());
		return "remove-account-form";
	}
	/**
	 * For a {@link Valid} {@link RemoveAccountFormBackingObject}, remove
	 * the current authenticated {@link IScheduleOwner} account.
	 * Destroy the current authenticated credentials, and redirect to an appropriate view.
	 * If the {@link RemoveAccountFormBackingObject} is not valid, redirect to the schedule view.
	 * @see OwnerDao#removeAccount(IScheduleOwner)
	 * @param fbo
	 * @param result
	 * @return 
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String removeAccount(@Valid @ModelAttribute RemoveAccountFormBackingObject fbo, BindingResult result) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		if(null == owner) {
			throw new NotRegisteredException("action requires registration");
		}
		if(result.hasErrors()) {
			return "remove-account-form";
		}
		
		if(fbo.isConfirmed()) {
			if(owner.isReflectSchedule()) {
				AvailableSchedule schedule = this.availableScheduleDao.retrieve(owner);
				this.availableScheduleReflectionService.purgeReflections(owner, new Date(), schedule.getScheduleEndTime());
			}
			ownerDao.removeAccount(owner);
			SecurityContextHolder.getContext().setAuthentication(null);
			return "redirect:/accountRemoved.html";
		} else {
			return "redirect:schedule.html";
		}
	}	
	
}
