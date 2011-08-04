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

import java.util.List;

import org.jasig.schedassist.impl.owner.PublicProfileDao;
import org.jasig.schedassist.model.PublicProfile;
import org.jasig.schedassist.model.PublicProfileTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * {@link Controller} implementation that displays a single
 * {@link PublicProfile} identified by the value for it's key 
 * in the URL.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PublicProfileController.java 2210 2010-06-22 16:08:08Z npblair $
 */
@Controller
public class PublicProfileController {

	private PublicProfileDao publicProfileDao;
	/**
	 * @param publicProfileDao the publicProfileDao to set
	 */
	@Autowired
	public void setPublicProfileDao(PublicProfileDao publicProfileDao) {
		this.publicProfileDao = publicProfileDao;
	}

	/**
	 * Retrieve the {@link PublicProfile} for the profileKey argument, returning
	 * the appropriate view if found.
	 * @param model
	 * @param profileKey 
	 * @return the view name to render the profile, or the owner not found view name
	 */
	@RequestMapping(value="/public/profiles/{profileKey}.html", method = RequestMethod.GET)
	public String retrieveProfile(final ModelMap model, @PathVariable("profileKey") String profileKey) {
		PublicProfile profile = publicProfileDao.locatePublicProfileByKey(profileKey);
		if(null != profile) {
			model.addAttribute("profile", profile);
			List<PublicProfileTag> tags = publicProfileDao.getProfileTags(profile.getPublicProfileId());
			model.addAttribute("profileTags", tags);
			return "profiles/profile";
		} else {
			return "profiles/owner-notfound";
		}
	}
}
