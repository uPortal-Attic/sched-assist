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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.IDelegateCalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Controller for looking up {@link ICalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AccountLookupFormController.java 2978 2011-01-25 19:20:51Z npblair $
 */
@Controller
@RequestMapping("/admin/account-lookup.html")
@SessionAttributes("command")
public class AccountLookupFormController {

	private ICalendarAccountDao calendarAccountDao;
	private IDelegateCalendarAccountDao delegateCalendarAccountDao;
	
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(@Qualifier("people") ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param delegateCalendarAccountDao the delegateCalendarAccountDao to set
	 */
	@Autowired
	public void setDelegateCalendarAccountDao(
			@Qualifier("delegates") IDelegateCalendarAccountDao delegateCalendarAccountDao) {
		this.delegateCalendarAccountDao = delegateCalendarAccountDao;
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
	@RequestMapping(method=RequestMethod.GET)
	protected String showForm(final ModelMap model) {
		AccountLookupFormBackingObject fbo = new AccountLookupFormBackingObject();
		model.addAttribute("command", fbo);
		return "admin/account-lookup-form";
	}
	/**
	 * 
	 * @param qValue
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET, params="type=people")
	protected String peopleAjaxQuery(@RequestParam(value="q",required=true) final String qValue, final ModelMap model) {
		// q parameter is set, execute non-interactive search
		model.addAttribute("searchText", qValue);
		List<ICalendarAccount> results = new ArrayList<ICalendarAccount>();
		if(qValue.length() > 2) {
			// alter search text before submitting to calendarUserDao
			final String searchText = StringUtils.replace(qValue, " ", "*");
			results = calendarAccountDao.searchForCalendarAccounts(searchText);
		}
		model.addAttribute("results", results);
		return "admin/account-results-ac-people";
	}
	
	/**
	 * 
	 * @param qValue
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET, params="type=resources")
	protected String resourcesAjaxQuery(@RequestParam(value="q",required=true) final String qValue, final ModelMap model) {
		// q parameter is set, execute non-interactive search
		model.addAttribute("searchText", qValue);
		List<IDelegateCalendarAccount> results = new ArrayList<IDelegateCalendarAccount>();
		if(qValue.length() > 2) {
			// alter search text before submitting to calendarUserDao
			final String searchText = StringUtils.replace(qValue, " ", "*");
			results = delegateCalendarAccountDao.searchForDelegates(searchText);
		}
		model.addAttribute("results", results);
		return "admin/account-results-ac-resources";
	}

	/**
	 * 
	 * @param fbo
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String interactiveSearch(@ModelAttribute("command") AccountLookupFormBackingObject fbo, final ModelMap model) {
		List<ICalendarAccount> results = new ArrayList<ICalendarAccount>();

		if(StringUtils.isNotBlank(fbo.getCtcalxitemid())) {
			ICalendarAccount account = this.calendarAccountDao.getCalendarAccountFromUniqueId(fbo.getCtcalxitemid());
			if(null != account) {
				results.add(account);
			}
		} 
		if(StringUtils.isNotBlank(fbo.getUsername())) {
			ICalendarAccount account = this.calendarAccountDao.getCalendarAccount(fbo.getUsername());
			if(null != account) {
				results.add(account);
			}
		} 
		
		if(StringUtils.isNotBlank(fbo.getResourceName())) {
			IDelegateCalendarAccount delegate = this.delegateCalendarAccountDao.getDelegate(fbo.getResourceName());
			if(null != delegate) {
				results.add(delegate);
			}
		} 
			
		if(StringUtils.isNotBlank(fbo.getSearchText()) && fbo.getSearchText().length() > 2) {
			// alter search text before submitting to calendarUserDao
			final String searchText = StringUtils.replace(fbo.getSearchText(), " ", "*");
			results.addAll(calendarAccountDao.searchForCalendarAccounts(searchText));
			results.addAll(delegateCalendarAccountDao.searchForDelegates(searchText));
		}
		
		if(results.size() == 1) {
			// redirect to that 1 result
			ICalendarAccount account = results.get(0);
			return "redirect:/admin/account-details.html?id=" + account.getCalendarUniqueId();
		} else {
			model.addAttribute("results", results);
			return "admin/account-results";
		}
	}
	

}
