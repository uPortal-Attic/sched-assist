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


package org.jasig.schedassist.web.profiles;

import java.util.Collections;
import java.util.List;

import org.jasig.schedassist.impl.owner.PublicProfileDao;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.PublicProfile;
import org.jasig.schedassist.model.PublicProfileId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * {@link Controller} that looks up all of the {@link PublicProfile}s and
 * returns solely the objects that refer to UW Academic Advisors (@see {@link Preferences#ADVISOR_SHARE_WITH_STUDENTS}).
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PublicProfilesAcademicAdvisorsController.java 2752 2010-10-05 15:36:26Z npblair $
 */
@Controller
public class PublicProfilesAcademicAdvisorsController {

	private PublicProfileDao publicProfileDao;
	
	/**
	 * @param publicProfileDao the publicProfileDao to set
	 */
	@Autowired
	public void setPublicProfileDao(PublicProfileDao publicProfileDao) {
		this.publicProfileDao = publicProfileDao;
	}

	/**
	 * 
	 * @param model
	 * @return the view name for the listing of advisors with {@link PublicProfile}s
	 */
	@RequestMapping(value="/public/advisors.html", method=RequestMethod.GET)
	public String retrieveAdvisorProfiles(final ModelMap model, @RequestParam(value="startIndex",required=false,defaultValue="0") int startIndex) {
		List<PublicProfileId> profileIds = publicProfileDao.getAdvisorPublicProfileIds();
		if(profileIds.isEmpty()) {
			// short circuit for empty list
			return "profiles/public-advisor-listing";
		}
		
		Collections.sort(profileIds);
		ProfilePageInformation pageInfo = new ProfilePageInformation(profileIds, startIndex);
		
		model.addAttribute("titleSuffix", buildPageTitleSuffix(pageInfo.getStartIndex(), pageInfo.getEndIndex()));
		model.addAttribute("profileIds", pageInfo.getSublist());
		model.addAttribute("showPrev", pageInfo.isShowPrev());
		model.addAttribute("showPrevIndex", pageInfo.getShowPrevIndex());
		model.addAttribute("showNext", pageInfo.isShowNext());
		model.addAttribute("showNextIndex", pageInfo.getShowNextIndex());
		return "profiles/public-advisor-listing";
	}

	/**
	 * Create a string appended to the document title, e.g.:
	 * 
	 * "Academic Advisors 1 - 10".
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	protected String buildPageTitleSuffix(int startIndex, int endIndex) {
		StringBuilder title = new StringBuilder();
		title.append("Academic Advisors ");
		title.append(startIndex == 0 ? 1 : startIndex);
		title.append(" - ");
		title.append(endIndex);
		return title.toString();
	}
}
