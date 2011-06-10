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

package org.jasig.schedassist.portlet.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.portlet.PortletSchedulingAssistantService;
import org.jasig.schedassist.portlet.webflow.FlowHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * {@link Controller} that provides visitor conflicts data as JSON.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisitorConflictsController.java $
 */
@Controller
public class VisitorConflictsController {

	private Log LOG = LogFactory.getLog(this.getClass());
	private PortletSchedulingAssistantService schedulingAssistantService;

	/**
	 * @param schedulingAssistantService the portletAvailableService to set
	 */
	@Autowired
	public void setPortletAvailableService(
			@Qualifier("ajax") PortletSchedulingAssistantService schedulingAssistantService) {
		this.schedulingAssistantService = schedulingAssistantService;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/ajax/visitor-conflicts.json")
	public String getVisitorConflicts(@RequestParam("ownerId") long ownerId, @RequestParam("weekStart") int weekStart, 
			final ModelMap model, final WebRequest request) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("enter getVisitorConflicts, ownerId: "  + ownerId + ", weekStart: " + weekStart);
		}
		final String visitorUsername = (String) request.getAttribute(FlowHelper.CURRENT_USER_ATTR, PortletSession.APPLICATION_SCOPE);
		if(LOG.isDebugEnabled()) {
			LOG.debug("visitorUsername: " + visitorUsername);
		}
		if(StringUtils.isBlank(visitorUsername)) {
			model.addAttribute("soup for you", "none");
			// short-circuit for unauthenticated
			return "jsonView";
		}
		List<AvailableBlock> conflicts = this.schedulingAssistantService.calculateVisitorConflicts(visitorUsername, ownerId, weekStart);
		Collections.sort(conflicts);
		
		List<String> conflictBlocks = new ArrayList<String>();
		SimpleDateFormat df = CommonDateOperations.getDateTimeFormat();
		for(AvailableBlock b: conflicts) {
			conflictBlocks.add(df.format(b.getStartTime()));
		}
		
		model.addAttribute("conflicts", conflictBlocks);
		if(LOG.isDebugEnabled()) {
			LOG.debug("exit getVisitorConflicts");
		}
		return "jsonView";
	}
}
