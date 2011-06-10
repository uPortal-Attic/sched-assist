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

import org.jasig.schedassist.impl.relationship.RelationshipDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Simple {@link Controller} for investigating the status
 * and interacting with a {@link RelationshipDataSource}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AdvisorListAdminController.java 2978 2011-01-25 19:20:51Z npblair $
 */
@Controller
@RequestMapping("/admin/relationshipSource.html")
public class RelationshipDataSourceAdminController {

	private RelationshipDataSource relationshipDataSource;
	
	
	/**
	 * @param relationshipDataSource the relationshipDataSource to set
	 */
	@Autowired
	public void setAdvisorListDataSource(RelationshipDataSource relationshipDataSource) {
		this.relationshipDataSource = relationshipDataSource;
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping()
	protected String showForm(final ModelMap model) {
		model.addAttribute("lastReloadTimestamp", this.relationshipDataSource.getLastReloadTimestamp());
		return "admin/advisorlist-form";
	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST, params="action=reload")
	protected String triggerReload() {
		this.relationshipDataSource.reloadData();
		return "admin/advisorlist-reload-complete";
	}
}	
