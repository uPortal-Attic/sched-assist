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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Provides JSON data for {@link AdhocRelationshipsController}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AdhocRelationshipsDataController.java 2050 2010-04-30 16:01:31Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/authorized-visitors.json", "/delegate/authorized-visitors.json"})
public class AdhocRelationshipsDataController {
	private RelationshipDao relationshipDao;

	/**
	 * @param relationshipDao the relationshipDao to set
	 */
	@Autowired
	public void setRelationshipDao(@Qualifier("adhoc") RelationshipDao relationshipDao) {
		this.relationshipDao = relationshipDao;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws NotRegisteredException 
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String getRelationshipsData(final ModelMap model) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();

		List<Relationship> relationships = relationshipDao.forOwner(owner);
		List<AdhocRelationshipVisitorDataBean> beans = new ArrayList<AdhocRelationshipsDataController.AdhocRelationshipVisitorDataBean>();
		if(relationships.size() > 0) {	
			for(Relationship r: relationships) {
				beans.add(convert(r));
			}
			Collections.sort(beans, new Comparator<AdhocRelationshipVisitorDataBean>() {
				public int compare(AdhocRelationshipVisitorDataBean o1, AdhocRelationshipVisitorDataBean o2) {
					return o1.getFullName().compareTo(o2.getFullName());
				}
			});
		}
		model.put("relationships", beans);
		return "jsonView";
	}

	/**
	 * 
	 * @param relationship
	 * @return
	 */
	protected AdhocRelationshipVisitorDataBean convert(Relationship relationship) {
		AdhocRelationshipVisitorDataBean result = new AdhocRelationshipVisitorDataBean();
		result.setDescription(relationship.getDescription());
		result.setFullName(relationship.getVisitor().getCalendarAccount().getDisplayName());
		result.setUsername(relationship.getVisitor().getCalendarAccount().getUsername());
		return result;
	}
	/**
	 * Java bean to simplify the data in a {@link Relationship} to just
	 * the fields useful for the enclosing class (JSON controller for adhoc relationships).
	 *
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: AdhocRelationshipsDataController.java $
	 */
	public static class AdhocRelationshipVisitorDataBean {
		private String username;
		private String fullName;
		private String description;
		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}
		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}
		/**
		 * @return the fullName
		 */
		public String getFullName() {
			return fullName;
		}
		/**
		 * @param fullName the fullName to set
		 */
		public void setFullName(String fullName) {
			this.fullName = fullName;
		}
		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}
		/**
		 * @param description the description to set
		 */
		public void setDescription(String description) {
			this.description = description;
		}
	}
}
