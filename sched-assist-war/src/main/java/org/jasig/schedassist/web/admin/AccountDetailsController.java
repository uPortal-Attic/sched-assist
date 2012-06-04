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

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.IAffiliationSource;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.IDelegateCalendarAccountDao;
import org.jasig.schedassist.impl.ldap.HasDistinguishedName;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.owner.PublicProfileDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.impl.visitor.VisitorDao;
import org.jasig.schedassist.model.AffiliationImpl;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.PublicProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * {@link Controller} implementation that provides {@link ICalendarAccount} details.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AccountDetailsController.java 2978 2011-01-25 19:20:51Z npblair $
 */
@Controller
@RequestMapping("/admin/account-details.html")
public class AccountDetailsController {

	private ICalendarAccountDao calendarAccountDao;
	private IDelegateCalendarAccountDao delegateCalendarAccountDao;
	private VisitorDao visitorDao;
	private OwnerDao ownerDao;
	private PublicProfileDao publicProfileDao;
	private IAffiliationSource affiliationSource;
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(@Qualifier("composite") ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param delegateCalendarAccountDao the delegateCalendarAccountDao to set
	 */
	@Autowired
	public void setDelegateCalendarAccountDao(
			IDelegateCalendarAccountDao delegateCalendarAccountDao) {
		this.delegateCalendarAccountDao = delegateCalendarAccountDao;
	}
	/**
	 * @param visitorDao the visitorDao to set
	 */
	@Autowired
	public void setVisitorDao(VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(OwnerDao ownerDao) {
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
	 * @param affiliationSource the affiliationSource to set
	 */
	@Autowired
	public void setAffiliationSource(IAffiliationSource affiliationSource) {
		this.affiliationSource = affiliationSource;
	}
	/**
	 * @return the calendarAccountDao
	 */
	public ICalendarAccountDao getCalendarAccountDao() {
		return calendarAccountDao;
	}
	/**
	 * @return the delegateCalendarAccountDao
	 */
	public IDelegateCalendarAccountDao getDelegateCalendarAccountDao() {
		return delegateCalendarAccountDao;
	}
	/**
	 * @return the visitorDao
	 */
	public VisitorDao getVisitorDao() {
		return visitorDao;
	}
	/**
	 * @return the ownerDao
	 */
	public OwnerDao getOwnerDao() {
		return ownerDao;
	}
	/**
	 * @return the publicProfileDao
	 */
	public PublicProfileDao getPublicProfileDao() {
		return publicProfileDao;
	}
	/**
	 * @return the affiliationSource
	 */
	public IAffiliationSource getAffiliationSource() {
		return affiliationSource;
	}
	/**
	 * 
	 * @param uniqueId
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String showDetails(@RequestParam(value="id", required=false, defaultValue="") String uniqueId, final ModelMap model) {
		model.addAttribute("id", uniqueId);
		if(StringUtils.isNotBlank(uniqueId)) {
			ICalendarAccount account = this.calendarAccountDao.getCalendarAccountFromUniqueId(uniqueId);
			if(null != account) {
				model.addAttribute("isDelegate", account instanceof IDelegateCalendarAccount);
				model.addAttribute("calendarAccount", account);
				model.addAttribute("isAdvisor", affiliationSource.doesAccountHaveAffiliation(account, AffiliationImpl.ADVISOR));
				model.addAttribute("isInstructor", affiliationSource.doesAccountHaveAffiliation(account, AffiliationImpl.INSTRUCTOR));
				model.addAttribute("calendarAccountAttributes", account.getAttributes().entrySet());
				if(account instanceof HasDistinguishedName) {
					model.addAttribute("hasDistinguishedName", "true");
				}
				// try to look up visitor
				try {
					this.visitorDao.toVisitor(account);
					model.addAttribute("isVisitor", true);
				} catch (NotAVisitorException e) {
					// ignore
				}
				// try to look up scheduleowner
				IScheduleOwner owner = this.ownerDao.locateOwner(account);
				if(null != owner) {
					model.addAttribute("owner", owner);
					model.addAttribute("ownerPreferences", owner.getPreferences().entrySet());
					// if a scheduleowner, try to look up public profile
					PublicProfile profile = this.publicProfileDao.locatePublicProfileByOwner(owner);
					if(null != profile) {
						model.addAttribute("publicProfile", profile);
					}
				}
			}
		}
		return "admin/account-details";
	}
	
	/**
	 * 
	 * @param uniqueId
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET, params="resource")
	protected String showDetailsResource(@RequestParam(value="id", required=false, defaultValue="") String uniqueId, final ModelMap model) {
		model.addAttribute("id", uniqueId);
		if(StringUtils.isNotBlank(uniqueId)) {
			IDelegateCalendarAccount account = this.delegateCalendarAccountDao.getDelegateByUniqueId(uniqueId);
			if(null != account) {
				model.addAttribute("isDelegate", true);
				model.addAttribute("calendarAccount", account);
				model.addAttribute("isAdvisor", affiliationSource.doesAccountHaveAffiliation(account, AffiliationImpl.ADVISOR));
				model.addAttribute("isInstructor", affiliationSource.doesAccountHaveAffiliation(account, AffiliationImpl.INSTRUCTOR));
				model.addAttribute("calendarAccountAttributes", account.getAttributes().entrySet());
				
				// try to look up visitor
				try {
					this.visitorDao.toVisitor(account);
					model.addAttribute("isVisitor", true);
				} catch (NotAVisitorException e) {
					// ignore
				}
				// try to look up scheduleowner
				IScheduleOwner owner = this.ownerDao.locateOwner(account);
				if(null != owner) {
					model.addAttribute("owner", owner);
					model.addAttribute("ownerPreferences", owner.getPreferences().entrySet());
					// if a scheduleowner, try to look up public profile
					PublicProfile profile = this.publicProfileDao.locatePublicProfileByOwner(owner);
					if(null != profile) {
						model.addAttribute("publicProfile", profile);
					}
				}
			}
		}
		return "admin/account-details";
	}
}
