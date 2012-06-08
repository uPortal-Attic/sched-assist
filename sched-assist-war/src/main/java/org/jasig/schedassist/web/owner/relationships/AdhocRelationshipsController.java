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

import java.util.List;

import org.jasig.schedassist.MutableRelationshipDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller that displays an {@link IScheduleOwner}'s
 * adhoc relationships.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AdhocRelationshipsController.java 2050 2010-04-30 16:01:31Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/sharing.html", "/delegate/sharing.html"})
public class AdhocRelationshipsController {

	private MutableRelationshipDao mutableRelationshipDao;
	/**
	 * @param mutableRelationshipDao the mutableRelationshipDao to set
	 */
	@Autowired
	public void setMutableRelationshipDao(
			MutableRelationshipDao mutableRelationshipDao) {
		this.mutableRelationshipDao = mutableRelationshipDao;
	}
	
	/**
	 * @return the mutableRelationshipDao
	 */
	public MutableRelationshipDao getMutableRelationshipDao() {
		return mutableRelationshipDao;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String getAdhocRelationships(final ModelMap model) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		
		List<Relationship> relationships = mutableRelationshipDao.forOwner(owner);
		model.addAttribute("relationships", relationships);
		return "owner-relationships/adhoc-authorizations";
	}
	
}
