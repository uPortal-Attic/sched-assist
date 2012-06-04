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

import org.jasig.schedassist.CalendarAccountNotFoundException;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.MutableRelationshipDao;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.impl.visitor.VisitorDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
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
 * {@link IScheduleOwner}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: RelationshipsForOwnerController.java 2978 2011-01-25 19:20:51Z npblair $
 */
@Controller
@RequestMapping("/admin/relationships-for-owner.html")
public class RelationshipsForOwnerController {

	private ICalendarAccountDao calendarAccountDao;
	private OwnerDao ownerDao;
	private VisitorDao visitorDao;
	private MutableRelationshipDao mutableRelationshipDao;
	
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(@Qualifier("people") ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param visitorDao the visitorDao to set
	 */
	@Autowired
	public void setVisitorDao(VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}
	/**
	 * @param mutableRelationshipDao the mutableRelationshipDao to set
	 */
	@Autowired
	public void setMutableRelationshipDao(
			MutableRelationshipDao mutableRelationshipDao) {
		this.mutableRelationshipDao = mutableRelationshipDao;
	}
	/**
	 * @return the calendarAccountDao
	 */
	public ICalendarAccountDao getCalendarAccountDao() {
		return calendarAccountDao;
	}
	/**
	 * @return the ownerDao
	 */
	public OwnerDao getOwnerDao() {
		return ownerDao;
	}
	/**
	 * @return the visitorDao
	 */
	public VisitorDao getVisitorDao() {
		return visitorDao;
	}
	/**
	 * @return the mutableRelationshipDao
	 */
	public MutableRelationshipDao getMutableRelationshipDao() {
		return mutableRelationshipDao;
	}
	/**
	 * 
	 * @param ctcalxitemid
	 * @param model
	 * @return
	 * @throws NotAVisitorException
	 */
	@RequestMapping(method=RequestMethod.GET)
	protected String showRelationships(@RequestParam(value="id", required=false, defaultValue="0") long ownerId, final ModelMap model) throws NotAVisitorException {
		model.addAttribute("id", ownerId);
		if(ownerId != 0) {
			IScheduleOwner owner = this.ownerDao.locateOwnerByAvailableId(ownerId);
			if(null != owner) {
				model.addAttribute("owner", owner);
				List<Relationship> relationships = this.mutableRelationshipDao.forOwner(owner);
				model.addAttribute("relationships", relationships);
			}
		}
		return "admin/relationships-for-owner";
	}
	
	/**
	 * 
	 * @param fbo
	 * @param model
	 * @return
	 * @throws CalendarAccountNotFoundException
	 * @throws NotAVisitorException
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String destroyAdhocRelationship(ModifyAdhocRelationshipFormBackingObject fbo, final ModelMap model) throws CalendarAccountNotFoundException, NotAVisitorException {
		IScheduleOwner owner = this.ownerDao.locateOwnerByAvailableId(fbo.getOwnerId());
		if(null != owner) {
			model.addAttribute("owner", owner);
			ICalendarAccount visitorUser = calendarAccountDao.getCalendarAccount(fbo.getVisitorUsername());
			if(null == visitorUser) {
				throw new CalendarAccountNotFoundException(fbo.getVisitorUsername() + " does not exist or is not eligible for WiscCal");
			}
			IScheduleVisitor visitor = visitorDao.toVisitor(visitorUser);
			model.addAttribute("visitor", visitor);
			mutableRelationshipDao.destroyRelationship(owner, visitor);
			return "admin/destroy-adhoc-relationship-success";
		}
		return "admin/not-modified";
	}
	
}
