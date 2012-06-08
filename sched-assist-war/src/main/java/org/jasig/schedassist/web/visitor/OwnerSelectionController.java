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


package org.jasig.schedassist.web.visitor;

import java.util.List;

import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.web.security.CalendarAccountUserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * {@link Controller} implementation tied closely to
 * the {@link RelationshipDao}. 
 * When requested, it calls {@link RelationshipDao#forVisitor(IScheduleVisitor)}
 * for the authenticated visitor. If more than 1 result is returned, a
 * view containing links for each {@link IScheduleOwner} is displayed.
 * If only 1 result is returned, the visitor is sent a redirect to the
 * display of that owners visible schedule.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: OwnerSelectionController.java 2046 2010-04-30 15:57:09Z npblair $
 */
@Controller
public class OwnerSelectionController {

	private RelationshipDao relationshipDao;
	/**
	 * @param relationshipDao the relationshipDao to set
	 */
	@Autowired
	public void setRelationshipDao(@Qualifier("composite") RelationshipDao relationshipDao) {
		this.relationshipDao = relationshipDao;
	}
	/**
	 * @return the relationshipDao
	 */
	public RelationshipDao getRelationshipDao() {
		return relationshipDao;
	}
	/**
	 * Locate relationships for the authenticated visitor and add them to the model.
	 * 
	 * @return the view name for the owner select form
	 * @throws NotAVisitorException
	 */
	@RequestMapping(value="/visitor.html", method=RequestMethod.GET)
	protected String getRelationships(final ModelMap model) throws NotAVisitorException {
		CalendarAccountUserDetailsImpl currentUser = (CalendarAccountUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleVisitor visitor = currentUser.getScheduleVisitor();
		
		List<Relationship> relationships = relationshipDao.forVisitor(visitor);
		model.addAttribute("relationships", relationships);
		return "visitor/owner-select";
	}

}
