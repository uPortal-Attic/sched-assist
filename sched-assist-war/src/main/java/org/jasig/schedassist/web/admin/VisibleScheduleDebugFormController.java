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

import javax.validation.Valid;

import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.VisibleSchedule;
import org.jasig.schedassist.web.visitor.OwnerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Controller that displays the {@link VisibleSchedule} for a particular
 * {@link IScheduleOwner}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleScheduleDebugFormController.java 2978 2011-01-25 19:20:51Z npblair $
 */
@Controller
@RequestMapping(value="/admin/schedule-debug.html")
@SessionAttributes("command")
public class VisibleScheduleDebugFormController {

	private VisibleScheduleDebugFormBackingObjectValidator validator;

	/**
	 * @param validator the validator to set
	 */
	@Autowired
	public void setValidator(VisibleScheduleDebugFormBackingObjectValidator validator) {
		this.validator = validator;
	}
	
	/**
	 * 
	 * @param binder
	 */
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String setupForm(ModelMap model) {
		VisibleScheduleDebugFormBackingObject command = new VisibleScheduleDebugFormBackingObject();
		model.addAttribute("command", command);
		return "admin/debug-lookup-form";
	}
	
	/**
	 * 
	 * @param ownerId
	 * @param highContrast
	 * @return
	 * @throws NotAVisitorException
	 * @throws OwnerNotFoundException
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String displaySchedule(@Valid @ModelAttribute("command") VisibleScheduleDebugFormBackingObject command, 
			BindingResult bindingResult,
			@RequestParam(value="highContrast", required=false, defaultValue="false") boolean highContrast,
			@RequestParam(value="weekStart", required=false, defaultValue="0") int weekStart,
			final ModelMap model)  {
		
		if(bindingResult.hasErrors()) {
			return "admin/debug-lookup-form";
		}
		
		IScheduleOwner selectedOwner = command.getScheduleOwner();
		IScheduleVisitor visitor = command.getScheduleVisitor();
		
		// redirect to the right location
		StringBuilder redirectUrl = new StringBuilder();
		redirectUrl.append("redirect:/admin/schedule-debug/");
		redirectUrl.append(selectedOwner.getId());
		redirectUrl.append("/view.html?visitorUsername=");
		redirectUrl.append(visitor.getCalendarAccount().getUsername());
		return redirectUrl.toString();
	}

}
