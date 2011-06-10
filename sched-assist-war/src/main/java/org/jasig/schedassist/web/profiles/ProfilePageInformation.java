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

import org.jasig.schedassist.model.PublicProfileId;

/**
 * Java bean to help validate startIndex request parameter for the
 * public profile controllers.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ProfilePageInformation.java $
 */
public class ProfilePageInformation {

	public static int DEFAULT_RESULTS_PER_PAGE = 10;
	private int startIndex = 0;
	private int endIndex = DEFAULT_RESULTS_PER_PAGE;
	private boolean showPrev = false;
	private boolean showNext = false;
	private int showPrevIndex = 0;
	private int showNextIndex = DEFAULT_RESULTS_PER_PAGE;
	private final List<PublicProfileId> sublist;
	
	/**
	 * 
	 * @param profileIds
	 * @param requestedStartIndex
	 */
	public ProfilePageInformation(List<PublicProfileId> profileIds, int requestedStartIndex) {
		if(requestedStartIndex >= profileIds.size()) {
			requestedStartIndex = profileIds.size() - DEFAULT_RESULTS_PER_PAGE;
			this.endIndex = profileIds.size();
		} else {
			int tempEndIndex = requestedStartIndex + DEFAULT_RESULTS_PER_PAGE;
			if(tempEndIndex > profileIds.size()) {
				this.endIndex = profileIds.size();
			} else {
				this.endIndex = tempEndIndex;
			}
		}
		if(requestedStartIndex < 0) {
			this.startIndex = 0;
		} else {
			this.startIndex = requestedStartIndex;
		}
		
		
		if(this.endIndex < profileIds.size()) {
			this.showNext = true;
			this.showNextIndex = this.endIndex;
		}
		if(this.startIndex > 0) {
			this.showPrev = true;
			this.showPrevIndex = this.startIndex - DEFAULT_RESULTS_PER_PAGE;
			if(this.showPrevIndex < 0) {
				this.showPrevIndex = 0;
			}
		}
		
		
		sublist = Collections.unmodifiableList(profileIds.subList(startIndex, endIndex));
	}
	/**
	 * @return the startIndex
	 */
	public int getStartIndex() {
		return startIndex;
	}
	/**
	 * @return the endIndex
	 */
	public int getEndIndex() {
		return endIndex;
	}
	/**
	 * @return the showPrev
	 */
	public boolean isShowPrev() {
		return showPrev;
	}
	/**
	 * @return the showNext
	 */
	public boolean isShowNext() {
		return showNext;
	}
	/**
	 * @return the sublist
	 */
	public List<PublicProfileId> getSublist() {
		return sublist;
	}
	/**
	 * @return the showPrevIndex
	 */
	public int getShowPrevIndex() {
		return showPrevIndex;
	}
	/**
	 * @return the showNextIndex
	 */
	public int getShowNextIndex() {
		return showNextIndex;
	}
	
}
