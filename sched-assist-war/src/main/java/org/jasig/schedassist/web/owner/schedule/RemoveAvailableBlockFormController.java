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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.InputFormatException;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 * {@link Controller} implementation that allows {@link IScheduleOwner}s
 * to remove a single {@link AvailableBlock} from their {@link AvailableSchedule}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: RemoveAvailableBlockFormController.java 2051 2010-04-30 16:03:17Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/remove-block.html","/delegate/remove-block.html"})
public class RemoveAvailableBlockFormController {

	private AvailableScheduleDao availableScheduleDao;
	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(AvailableScheduleDao availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
	}
	/**
	 * @return the availableScheduleDao
	 */
	public AvailableScheduleDao getAvailableScheduleDao() {
		return availableScheduleDao;
	}
	/**
	 * 
	 * @param binder
	 */
	@InitBinder(value="command")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new AvailableBlockFormBackingObjectValidator());
    }
	/**
	 * 
	 * @param model
	 * @return
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String setupForm(final ModelMap model, @RequestParam(value="interactive", required=false, defaultValue="false") boolean interactive) throws NotRegisteredException {
		AvailableBlockFormBackingObject fbo = new AvailableBlockFormBackingObject();
		fbo.setInteractive(interactive);
		model.addAttribute("command", fbo);
		return "owner-schedule/remove-form";
	}
	/**
	 * 
	 * @param fbo
	 * @return
	 * @throws NotRegisteredException 
	 * @throws InputFormatException 
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String removeAvailableBlock(@Valid @ModelAttribute("command") AvailableBlockFormBackingObject fbo, BindingResult bindingResult, 
			final ModelMap model) throws NotRegisteredException, InputFormatException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		
		if(bindingResult.hasErrors()) {
			if(fbo.isInteractive()) {
				return "owner-schedule/remove-form";
			} else {
				model.addAttribute("reason", "An unexpected error occurred; refresh the page and try again.");
				return "jsonView";
			}
		}
		
		AvailableBlock block = AvailableBlockBuilder.createSmallestAllowedBlock(fbo.getStartTimePhrase());
		if(null != fbo.getEndTimePhrase() && !"".equals(fbo.getEndTimePhrase())) {
			block = AvailableBlockBuilder.createBlock(fbo.getStartTimePhrase(), fbo.getEndTimePhrase());
		}
		availableScheduleDao.removeFromSchedule(owner, block);
		//model.put("block", block);
		model.addAttribute("blockStart", formatDate(block.getStartTime()));
		model.addAttribute("blockEnd", formatDate(block.getEndTime()));
		model.addAttribute("blockId", formatBlockId(block.getStartTime()));
		model.addAttribute("visitorLimit", block.getVisitorLimit());
		
		if(fbo.isInteractive()) {
			return "owner-schedule/remove-block-success";
		} else {
			return "jsonView";
		}
	}

	private String formatBlockId(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("EEEHHmm");
		return df.format(date);
	}
	private String formatDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy HH:mm");
		return df.format(date);
	}
}
