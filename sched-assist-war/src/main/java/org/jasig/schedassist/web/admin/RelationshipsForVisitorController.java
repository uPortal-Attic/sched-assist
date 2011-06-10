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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.impl.visitor.VisitorDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller to display relationships for the selected
 * {@link IScheduleVisitor}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: RelationshipsForVisitorController.java 2978 2011-01-25 19:20:51Z npblair $
 */
@Controller
@RequestMapping("/admin/relationships-for-visitor.html")
public class RelationshipsForVisitorController {

	private ICalendarAccountDao calendarAccountDao;
	private VisitorDao visitorDao;
	private RelationshipDao relationshipDao;
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(@Qualifier("people") ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param visitorDao the visitorDao to set
	 */
	@Autowired
	public void setVisitorDao(VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}
	/**
	 * @param relationshipDao the relationshipDao to set
	 */
	@Autowired
	public void setRelationshipDao(@Qualifier("composite") RelationshipDao relationshipDao) {
		this.relationshipDao = relationshipDao;
	}
	
	/**
	 * 
	 * @param ctcalxitemid
	 * @param model
	 * @return
	 * @throws NotAVisitorException
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String showRelationships(@RequestParam(value="id", required=false, defaultValue="") String ctcalxitemid, final ModelMap model) throws NotAVisitorException {
		model.addAttribute("id", ctcalxitemid);
		if(StringUtils.isNotBlank(ctcalxitemid)) {
			ICalendarAccount account = this.calendarAccountDao.getCalendarAccountFromUniqueId(ctcalxitemid);
			if(null != account) {
				IScheduleVisitor visitor = this.visitorDao.toVisitor(account);
				model.addAttribute("visitor", visitor);
				List<Relationship> relationships = this.relationshipDao.forVisitor(visitor);
				model.addAttribute("relationships", relationships);
			}
		}
		return "admin/relationships-for-visitor";
	}
}
