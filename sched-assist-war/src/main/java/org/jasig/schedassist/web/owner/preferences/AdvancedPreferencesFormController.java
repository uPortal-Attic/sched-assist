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


package org.jasig.schedassist.web.owner.preferences;

import javax.validation.Valid;

import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.owner.PublicProfileAlreadyExistsException;
import org.jasig.schedassist.impl.owner.PublicProfileDao;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IdentityUtils;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.PublicProfile;
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
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * {@link Controller} to execute the changes listed
 * in the "advanced sharing form".
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AdvancedPreferencesFormController.java 2827 2010-11-01 14:18:42Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/advanced.html","/delegate/advanced.html"})
@SessionAttributes("command")
public class AdvancedPreferencesFormController {

	private OwnerDao ownerDao;
	private PublicProfileDao publicProfileDao;
	
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(final OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param publicProfileDao the publicProfileDao to set
	 */
	@Autowired
	public void setPublicProfileDao(PublicProfileDao publicProfileDao) {
		this.publicProfileDao = publicProfileDao;
	}
	/**
	 * 
	 * @param binder
	 */
	@InitBinder("command")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new AdvancedPreferencesFormBackingObjectValidator());
    }
	/**
	 * 
	 * @param model
	 * @return the form view name
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String setupForm(final ModelMap model) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();

		AdvancedPreferencesFormBackingObject fbo = new AdvancedPreferencesFormBackingObject();

		PublicProfile existingProfile = publicProfileDao.locatePublicProfileByOwner(owner);
		fbo.setCreatePublicProfile(null != existingProfile);
		
		if(fbo.isCreatePublicProfile()) {
			fbo.setPublicProfileDescription(existingProfile.getDescription());
			fbo.setPublicProfileKey(existingProfile.getPublicProfileId().getProfileKey());
		}

		//fbo.setEligibleForAdvisor(IdentityUtils.isAdvisor(owner.getCalendarAccount()));
		//if(IdentityUtils.isAdvisor(owner.getCalendarAccount())) {
			String prefValue = owner.getPreference(Preferences.ADVISOR_SHARE_WITH_STUDENTS);
			fbo.setAdvisorShareWithStudents(Boolean.parseBoolean(prefValue));
		//}
		model.addAttribute("command", fbo);
		return "owner-preferences/advanced-preferences-form";
	}
	
	/**
	 * Update preferences that reflect changes to existing
	 * values for the current authenticated {@link IScheduleOwner}.
	 * 
	 * @param fbo
	 * @param bindingResult
	 * @param model
	 * @return the form view name if binding errors exist, otherwise the success view name
	 * @throws NotRegisteredException
	 * @throws PublicProfileAlreadyExistsException 
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String updateAdvancedPreferences(@Valid @ModelAttribute("command") AdvancedPreferencesFormBackingObject fbo, BindingResult bindingResult,
			final ModelMap model) throws NotRegisteredException, PublicProfileAlreadyExistsException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		
		if(bindingResult.hasErrors()) {
			return "owner-preferences/advanced-preferences-form";
		}
	
		//if(IdentityUtils.isAdvisor(owner.getCalendarAccount())) {
			owner = ownerDao.updatePreference(owner, Preferences.ADVISOR_SHARE_WITH_STUDENTS, String.valueOf(fbo.isAdvisorShareWithStudents()));
			if(fbo.isAdvisorShareWithStudents()) {
				model.addAttribute("advisorShareWithStudentsOn", true);
			} else {
				model.addAttribute("advisorShareWithStudentsOff", true);
			}

		//}
		//String storedSharePublicValue = owner.getPreference(Preferences.SHARE_PUBLIC);
		PublicProfile existingProfile = this.publicProfileDao.locatePublicProfileByOwner(owner);
		
		// set public profile preference (only if owner previously was not sharing)
		if(fbo.isCreatePublicProfile() && null == existingProfile) {
			PublicProfile newProfile = this.publicProfileDao.createPublicProfile(owner, fbo.getPublicProfileDescription());
			model.addAttribute("createdPublicProfile", true);
			model.addAttribute("publicProfileKey", newProfile.getPublicProfileId().getProfileKey());

		} else if(!fbo.isCreatePublicProfile() && null != existingProfile) {
			this.publicProfileDao.removePublicProfile(existingProfile.getPublicProfileId());
			model.put("removedPublicProfile", true);
		} else if (fbo.isCreatePublicProfile() && null != existingProfile) {
			// check to see if we need to update the description
			if(!existingProfile.getDescription().equals(fbo.getPublicProfileDescription())) {
				// fbo is different from stored, update
				this.publicProfileDao.updatePublicProfileDescription(existingProfile.getPublicProfileId(), fbo.getPublicProfileDescription());
				model.addAttribute("updatedPublicProfile", true);
			}
		}
		currentUser.updateScheduleOwner(owner);
		return "owner-preferences/advanced-preferences-success";
	}
	
}
