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

import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This {@link Controller} implementation simply verifies that 
 * the authenticated {@link IScheduleOwner} has been registered;
 * if not a {@link NotRegisteredException} is thrown.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableScheduleViewController.java 2051 2010-04-30 16:03:17Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/schedule.html", "/delegate/schedule.html" })
public class AvailableScheduleViewController {
	/**
	 * 
	 * @return
	 * @throws NotRegisteredException 
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String displaySchedule() throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		currentUser.getScheduleOwner();
		return "owner-schedule/schedule-view";
	}	
}
