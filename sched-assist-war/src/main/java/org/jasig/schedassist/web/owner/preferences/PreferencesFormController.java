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

import org.jasig.schedassist.impl.AvailableScheduleReflectionService;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.MeetingDurations;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.Reminders;
import org.jasig.schedassist.model.VisibleWindow;
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
 * Form controller that invokes the method {@link OwnerDao#updatePreference(IScheduleOwner, Preferences, String)}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PreferencesFormController.java 2985 2011-01-26 21:58:45Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/preferences.html","/delegate/preferences.html"})
@SessionAttributes("command")
public class PreferencesFormController {

	private OwnerDao ownerDao;
	private AvailableScheduleDao availableScheduleDao;
	private AvailableScheduleReflectionService reflectionService;
	private PreferencesFormBackingObjectValidator validator;
	
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
	/**
	 * @param validator the validator to set
	 */
	@Autowired
	public void setValidator(PreferencesFormBackingObjectValidator validator) {
		this.validator = validator;
	}
	/**
	 * 
	 * @param binder
	 */
	@InitBinder("command")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(this.validator);
    }
	/**
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method = RequestMethod.GET)
	protected String setupForm(final ModelMap model) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		
		PreferencesFormBackingObject fbo = new PreferencesFormBackingObject();
		MeetingDurations meetingDurations = owner.getPreferredMeetingDurations();
		VisibleWindow visibleWindow = owner.getPreferredVisibleWindow();
		Reminders emailReminders = owner.getRemindersPreference();
		
		fbo.setMeetingLength(String.valueOf(meetingDurations.getMinLength()));
		fbo.setAllowDoubleLength(meetingDurations.isDoubleLength());
		
		fbo.setLocation(owner.getPreference(Preferences.LOCATION));
		fbo.setTitlePrefix(owner.getPreference(Preferences.MEETING_PREFIX));
		fbo.setNoteboard(owner.getPreference(Preferences.NOTEBOARD));
		fbo.setWindowHoursStart(visibleWindow.getWindowHoursStart());
		fbo.setWindowWeeksEnd(visibleWindow.getWindowWeeksEnd());
		fbo.setDefaultVisitorsPerAppointment(Integer.parseInt(owner.getPreference(Preferences.DEFAULT_VISITOR_LIMIT)));
		int currentMeetingLimitValue = Integer.parseInt(owner.getPreference(Preferences.MEETING_LIMIT));
		fbo.setMeetingLimitValue(currentMeetingLimitValue);
		fbo.setReflectSchedule(owner.isReflectSchedule());
		fbo.setEnableEmailReminders(emailReminders.isEnabled());
		fbo.setEmailReminderIncludeOwner(emailReminders.isIncludeOwner());
		fbo.setEmailReminderHours(emailReminders.getHours());

		model.addAttribute("command", fbo);
		return "owner-preferences/preferences-form";
	}
	
	/**
	 * 
	 * @param fbo
	 * @param bindingResult
	 * @param request
	 * @return
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method = RequestMethod.POST)
	protected String updatePreferences(@Valid @ModelAttribute("command") PreferencesFormBackingObject fbo, BindingResult bindingResult) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		if(bindingResult.hasErrors()) {
			return "owner-preferences/preferences-form";
		}
		// only update preferences that change
		if(!owner.getPreference(Preferences.DURATIONS).equals(fbo.durationPreferenceValue())) {
			owner = ownerDao.updatePreference(owner, Preferences.DURATIONS, fbo.durationPreferenceValue());
		}
		if(!owner.getPreference(Preferences.LOCATION).equals(fbo.getLocation())) {
			owner = ownerDao.updatePreference(owner, Preferences.LOCATION, fbo.getLocation());
		}
		if(!owner.getPreference(Preferences.MEETING_PREFIX).equals(fbo.getTitlePrefix())) {
			owner = ownerDao.updatePreference(owner, Preferences.MEETING_PREFIX, fbo.getTitlePrefix());
		}
		if(!owner.getPreference(Preferences.NOTEBOARD).equals(fbo.getNoteboard())) {
			owner = ownerDao.updatePreference(owner, Preferences.NOTEBOARD, fbo.getNoteboard());
		}
		if(!owner.getPreference(Preferences.VISIBLE_WINDOW).equals(fbo.visibleWindowPreferenceKey())) {
			owner = ownerDao.updatePreference(owner, Preferences.VISIBLE_WINDOW, fbo.visibleWindowPreferenceKey());
		}
		if(!owner.getPreference(Preferences.DEFAULT_VISITOR_LIMIT).equals(Integer.toString(fbo.getDefaultVisitorsPerAppointment()))) {
			owner = ownerDao.updatePreference(owner, Preferences.DEFAULT_VISITOR_LIMIT, Integer.toString(fbo.getDefaultVisitorsPerAppointment()));
		}
		if(!owner.getPreference(Preferences.MEETING_LIMIT).equals(Integer.toString(fbo.getMeetingLimitValue()))) {
			owner = ownerDao.updatePreference(owner, Preferences.MEETING_LIMIT, Integer.toString(fbo.getMeetingLimitValue()));
		}	
		if(!owner.getPreference(Preferences.REMINDERS).equals(fbo.emailReminderPreferenceKey())) {
			owner = ownerDao.updatePreference(owner, Preferences.REMINDERS, fbo.emailReminderPreferenceKey());
		}
		if(owner.isReflectSchedule() != fbo.isReflectSchedule()) {
			owner = ownerDao.updatePreference(owner, Preferences.REFLECT_SCHEDULE, Boolean.toString(fbo.isReflectSchedule()));
			if(fbo.isReflectSchedule()) {
				this.reflectionService.reflectAvailableSchedule(owner);
			} else {
				AvailableSchedule schedule = this.availableScheduleDao.retrieve(owner);
				if(!schedule.isEmpty()) {
					this.reflectionService.purgeReflections(owner, schedule.getScheduleStartTime(), schedule.getScheduleEndTime());
				}
			}
		}
		currentUser.updateScheduleOwner(owner);
		return "owner-preferences/preferences-success";
	}
}
