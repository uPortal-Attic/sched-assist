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

package org.jasig.schedassist.model;

/**
 * @author Nicholas Blair
 * @version $Id: PublicProfileTag.java $
 */
public class PublicProfileTag {

	private String profileKey;
	private String tagDisplay;
	private String tag;
	/**
	 * @return the profileKey
	 */
	public String getProfileKey() {
		return profileKey;
	}
	/**
	 * @param profileKey the profileKey to set
	 */
	public void setProfileKey(String profileKey) {
		this.profileKey = profileKey;
	}
	/**
	 * @return the tagDisplay
	 */
	public String getTagDisplay() {
		return tagDisplay;
	}
	/**
	 * @param tagDisplay the tagDisplay to set
	 */
	public void setTagDisplay(String tagDisplay) {
		this.tagDisplay = tagDisplay;
	}
	
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tagDisplay.toUpperCase();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PublicProfileTag [profileKey=" + profileKey + ", tagDisplay="
				+ tagDisplay + ", tag=" + tag + "]";
	}
	
	
}
