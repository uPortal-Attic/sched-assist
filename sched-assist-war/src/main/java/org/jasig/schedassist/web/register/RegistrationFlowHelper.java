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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;

import org.jasig.schedassist.IAffiliationSource;
import org.jasig.schedassist.impl.AvailableScheduleReflectionService;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.IneligibleException;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.model.AffiliationImpl;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.web.owner.preferences.PreferencesFormBackingObjectValidator;
import org.jasig.schedassist.web.security.CalendarAccountUserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Bridge between web flows and some of the data access operations.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: RegistrationFlowHelper.java 2552 2010-09-13 18:31:11Z npblair $
 */
@Service
public class RegistrationFlowHelper {
	
	private OwnerDao ownerDao;
	private AvailableScheduleDao availableScheduleDao;
	private AvailableScheduleReflectionService reflectionService;
	private IAffiliationSource affiliationSource;
	private PreferencesFormBackingObjectValidator preferencesValidator;
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
	 * @param reflectionService the reflectionService to set
	 */
	@Autowired
	public void setReflectionService(
			AvailableScheduleReflectionService reflectionService) {
		this.reflectionService = reflectionService;
	}
	/**
	 * @param affiliationSource the affiliationSource to set
	 */
	@Autowired
	public void setAffiliationSource(IAffiliationSource affiliationSource) {
		this.affiliationSource = affiliationSource;
	}
	/**
	 * @param preferencesValidator the preferencesValidator to set
	 */
	@Autowired
	public void setPreferencesValidator(
			PreferencesFormBackingObjectValidator preferencesValidator) {
		this.preferencesValidator = preferencesValidator;
	}
	
	/**
	 * 
	 * @return a new {@link Registration} instance
	 */
	public Registration newRegistrationInstance() {
		return new Registration(preferencesValidator);
	}
	/**
	 * Invoke methods on the {@link OwnerDao} and {@link AvailableScheduleDao} to complete
	 * the registration process.
	 * 
	 * @param registration
	 * @throws IneligibleException
	 * @throws ParseException 
	 * @throws InputFormatException 
	 */
	public void executeRegistration(final Registration registration) throws IneligibleException, InputFormatException, ParseException {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) authentication.getPrincipal();
		IScheduleOwner owner = ownerDao.register(currentUser.getCalendarAccount());
		owner = ownerDao.updatePreference(owner, Preferences.DURATIONS, registration.durationPreferenceValue());
		owner = ownerDao.updatePreference(owner, Preferences.LOCATION, registration.getLocation());
		owner = ownerDao.updatePreference(owner, Preferences.MEETING_PREFIX, registration.getTitlePrefix());
		owner = ownerDao.updatePreference(owner, Preferences.NOTEBOARD, registration.getNoteboard());
		owner = ownerDao.updatePreference(owner, Preferences.VISIBLE_WINDOW, registration.visibleWindowPreferenceKey());
		owner = ownerDao.updatePreference(owner, Preferences.DEFAULT_VISITOR_LIMIT, Integer.toString(registration.getDefaultVisitorsPerAppointment()));
		owner = ownerDao.updatePreference(owner, Preferences.MEETING_LIMIT, Integer.toString(registration.getMeetingLimitValue()));
		owner = ownerDao.updatePreference(owner, Preferences.REFLECT_SCHEDULE, Boolean.toString(registration.isReflectSchedule()));
		owner = ownerDao.updatePreference(owner, Preferences.REMINDERS, registration.emailReminderPreferenceKey());
		
		if(affiliationSource.doesAccountHaveAffiliation(owner.getCalendarAccount(), AffiliationImpl.ADVISOR)) {
			// set ADVISOR_SHARE_WITH_STUDENTS by default for all academic advisors
			owner = ownerDao.updatePreference(owner, Preferences.ADVISOR_SHARE_WITH_STUDENTS, "true");
		}
		if(affiliationSource.doesAccountHaveAffiliation(owner.getCalendarAccount(), AffiliationImpl.INSTRUCTOR)) {
			// set INSTRUCTOR_SHARE_WITH_STUDENTS by default for all instructors
			owner = ownerDao.updatePreference(owner, Preferences.INSTRUCTOR_SHARE_WITH_STUDENTS, "true");
		}
		if(registration.isScheduleSet()) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Set<AvailableBlock> blocks = AvailableBlockBuilder.createBlocks(registration.getStartTimePhrase(), 
					registration.getEndTimePhrase(),
					registration.getDaysOfWeekPhrase(),
					dateFormat.parse(registration.getStartDatePhrase()),
					dateFormat.parse(registration.getEndDatePhrase()),
					registration.getDefaultVisitorsPerAppointment());
			availableScheduleDao.addToSchedule(owner, blocks);
		}
		
		if(registration.isReflectSchedule()) {
			reflectionService.reflectAvailableSchedule(owner);
		}
		
		// since Spring Security won't let you update someone's Authorities, have to force re-auth
		SecurityContextHolder.clearContext();
	}
	
	/**
	 * Return true if the current authenticated {@link ICalendarAccount} is an advisor.
	 * 
	 * @return
	 */
	public boolean isCurrentAuthenticatedCalendarUserAdvisor() {
		if ((SecurityContextHolder.getContext() == null)
                || !(SecurityContextHolder.getContext() instanceof SecurityContext)
                || (SecurityContextHolder.getContext().getAuthentication() == null)) {
			return false;
		} else {
			CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			ICalendarAccount calendarAccount = currentUser.getCalendarAccount();
			return affiliationSource.doesAccountHaveAffiliation(calendarAccount, AffiliationImpl.ADVISOR);
		}
	}
	
	/**
	 * Return true if the current authenticated {@link ICalendarAccount} is an instructor.
	 * 
	 * @return
	 */
	public boolean isCurrentAuthenticatedCalendarUserInstructor() {
		if ((SecurityContextHolder.getContext() == null)
                || !(SecurityContextHolder.getContext() instanceof SecurityContext)
                || (SecurityContextHolder.getContext().getAuthentication() == null)) {
			return false;
		} else {
			CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			ICalendarAccount calendarAccount = currentUser.getCalendarAccount();
			return affiliationSource.doesAccountHaveAffiliation(calendarAccount, AffiliationImpl.INSTRUCTOR);
		}
	}
}
