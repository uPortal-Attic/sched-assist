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
import java.util.Map;

import org.jasig.schedassist.impl.owner.PublicProfileDao;
import org.jasig.schedassist.model.PublicProfile;
import org.jasig.schedassist.model.PublicProfileId;
import org.jasig.schedassist.model.PublicProfileTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * {@link Controller} for browsing lists of {@Link PublicProfile}s.
 * 
 * @author Nicholas Blair
 * @version $Id: PublicProfilesBrowseController.java $
 */
@Controller
public class PublicProfilesBrowseController {

	private PublicProfileDao publicProfileDao;
	/**
	 * @param publicProfileDao the publicProfileDao to set
	 */
	@Autowired
	public void setPublicProfileDao(PublicProfileDao publicProfileDao) {
		this.publicProfileDao = publicProfileDao;
	}
	
	/**
	 * Retrieve profiles between startIndex and (startIndex + resultsPerPage).
	 * @param startIndex
	 * @param resultsPerPage
	 * @return the view name to display links to each {@link PublicProfile}
	 */
	@RequestMapping(value="/public/browse.html", method=RequestMethod.GET)
	public String retrieveAllProfiles(final ModelMap model, @RequestParam(value="startIndex",required=false,defaultValue="0") int startIndex) {
		List<PublicProfileId> profileIds = publicProfileDao.getPublicProfileIds();
		if(profileIds.isEmpty()) {
			// short circuit
			model.addAttribute("titleSuffix", "");
			return "profiles/public-listing";
		}
		
		populateModel(model, profileIds, startIndex);
		return "profiles/public-listing";
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
			model.addAttribute("titleSuffix", "");
			return "profiles/public-advisor-listing";
		}
		
		populateModel(model, profileIds, startIndex);
		return "profiles/public-advisor-listing";
	}
	
	/**
	 * 
	 * @param model
	 * @return the view name for the listing of instructors with {@link PublicProfile}s
	 */
	@RequestMapping(value="/public/instructors.html", method=RequestMethod.GET)
	public String retrieveInstructorProfiles(final ModelMap model, @RequestParam(value="startIndex",required=false,defaultValue="0") int startIndex) {
		List<PublicProfileId> profileIds = publicProfileDao.getInstructorPublicProfileIds();
		if(profileIds.isEmpty()) {
			// short circuit for empty list
			model.addAttribute("titleSuffix", "");
			return "profiles/public-instructor-listing";
		}
		
		populateModel(model, profileIds, startIndex);
		return "profiles/public-instructor-listing";
	}
	
	/**
	 * Populate the {@link ModelMap} with the appropriate information.
	 * 
	 * @param model
	 * @param profileIds
	 * @param startIndex
	 */
	protected void populateModel(ModelMap model, List<PublicProfileId> profileIds, int startIndex) {
		Collections.sort(profileIds);
		ProfilePageInformation pageInfo = new ProfilePageInformation(profileIds, startIndex);
		List<PublicProfileId> sublist = pageInfo.getSublist();
		Map<PublicProfileId, List<PublicProfileTag>> profileMap = publicProfileDao.getProfileTagsBatch(sublist);
		model.addAttribute("titleSuffix", buildPageTitleSuffix(pageInfo.getStartIndex(), pageInfo.getEndIndex()));
		model.addAttribute("profileIds", sublist);
		model.addAttribute("profileMap", profileMap);
		model.addAttribute("showPrev", pageInfo.isShowPrev());
		model.addAttribute("showPrevIndex", pageInfo.getShowPrevIndex());
		model.addAttribute("showNext", pageInfo.isShowNext());
		model.addAttribute("showNextIndex", pageInfo.getShowNextIndex());
	}
	
	/**
	 * Create a string appended to the document title, e.g.:
	 * 
	 * "Public Profiles 1 - 10".
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	protected String buildPageTitleSuffix(int startIndex, int endIndex) {
		StringBuilder title = new StringBuilder();
		title.append(" ");
		title.append(startIndex == 0 ? 1 : startIndex + 1);
		title.append(" - ");
		title.append(endIndex);
		return title.toString();
	}
}
