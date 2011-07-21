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


package org.jasig.schedassist.web.owner.relationships;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Form controller implementation that invokes {@link ICalendarAccountDao#searchForCalendarAccounts(String)}.
 * Will only invoke the method if {@link CalendarUserSearchFormBackingObject#getSearchText()}
 * has 3 or more characters.
 * 
 * {@link #formBackingObject(HttpServletRequest)} and {@link #isFormSubmission(HttpServletRequest)} have
 * been overriden to support invocation of {@link #onSubmit(Object)} if the "q" request parameter
 * is set (and the request is a GET). The intent of these changes is to support a JQuery autocomplete
 * plugin (which makes GET requests to "url?q=searchText").
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarUserSearchFormController.java 2050 2010-04-30 16:01:31Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/visitor-search.html","/delegate/visitor-search.html"})
@SessionAttributes("command")
public class CalendarUserSearchFormController {

	private ICalendarAccountDao calendarAccountDao;
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}

	/**
	 * If the qValue parameter is not blank, execute a search, and return
	 * the autocomplete results view name.
	 * Otherwise, return the form view name.
	 * @param qValue
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String onGet(@RequestParam(value="q",required=false) final String qValue, final ModelMap model) {
		if(StringUtils.isBlank(qValue)) {
			CalendarUserSearchFormBackingObject fbo = new CalendarUserSearchFormBackingObject();
			model.addAttribute("command", fbo);
			return "owner-relationships/calendaruser-search-form";
		}
		// if qValue isn't blank, this should be considered a search request
		model.addAttribute("searchText", qValue);
		List<ICalendarAccount> matches = new ArrayList<ICalendarAccount>();
		if(null != qValue && qValue.length() > 2) {
			// alter search text before submitting to calendarUserDao
			final String searchText = StringUtils.replace(qValue, " ", "*");
			matches = calendarAccountDao.searchForCalendarAccounts(searchText);
		}
		List<ICalendarAccount> results = filterForEligible(matches);
		model.addAttribute("results", results);
		return "owner-relationships/calendaruser-results-ac";
	}
	/**
	 * 
	 * @param fbo
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String search(@ModelAttribute("command") CalendarUserSearchFormBackingObject fbo, final ModelMap model) {
		model.addAttribute("searchText", fbo.getSearchText());
		List<ICalendarAccount> matches = new ArrayList<ICalendarAccount>();
		if(fbo.getSearchText() != null && fbo.getSearchText().length() > 2) {
			// alter search text before submitting to calendarUserDao
			final String searchText = StringUtils.replace(fbo.getSearchText(), " ", "*");
			matches = calendarAccountDao.searchForCalendarAccounts(searchText);
		}
		List<ICalendarAccount> results = filterForEligible(matches);
		model.addAttribute("results", results);
		return "owner-relationships/calendaruser-results";
	}	

	/**
	 * Filter out {@link ICalendarAccount} that return false for {@link ICalendarAccount#isEligible()}.
	 *
	 * @param matches
	 * @return
	 */
	protected List<ICalendarAccount> filterForEligible(List<ICalendarAccount> matches) {
		List<ICalendarAccount> results = new ArrayList<ICalendarAccount>();
		for(ICalendarAccount a: matches) {
			if(a.isEligible()) {
				results.add(a);
			}
		}
		return results;
	}
}
