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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Alternate {@link Controller} implementation used with the {@link AlternateScheduleViewController}.
 * 
 * This controller responds to GET requests, interpreting the desired
 * {@link AvailableBlock} to remove via the "startTime" and "endTime" request parameters.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AlternateRemoveAvailableBlockController.java 2070 2010-04-30 16:52:11Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/remove-block-alternate.html", "/delegate/remove-block-alternate.html" })
public class AlternateRemoveAvailableBlockController {

	protected final Log log = LogFactory.getLog(this.getClass());
	
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
	 * @param startTimePhrase
	 * @param endTimePhrase
	 * @return
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView removeBlock(@RequestParam(value="startTime", required=true) String startTimePhrase, @RequestParam(value="endTime", required=true) String endTimePhrase) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		
		try {
			AvailableBlock block = AvailableBlockBuilder.createBlock(startTimePhrase, endTimePhrase);
			log.debug("parsed request parameters: " + block);
			availableScheduleDao.removeFromSchedule(owner, block);
		} catch (InputFormatException e) {
			log.info("cannot create block", e);
			// TODO should redirect to error form
		} 
		
		return new ModelAndView(new RedirectView("schedule-noscript.html", true));
	}

}
