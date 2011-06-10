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


package org.jasig.schedassist.web.owner.preferences;

/**
 * Form backing object for {@link AdvancedPreferencesFormController}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AdvancedPreferencesFormBackingObject.java 1930 2010-04-16 17:06:16Z npblair $
 */
public class AdvancedPreferencesFormBackingObject {

	private boolean eligibleForAdvisor = false;
	private boolean advisorShareWithStudents = false;
	private boolean createPublicProfile = false;
	private String publicProfileDescription = "";
	private String publicProfileKey;
	
	/**
	 * @return the eligibleForAdvisor
	 */
	public boolean isEligibleForAdvisor() {
		return eligibleForAdvisor;
	}
	/**
	 * @param eligibleForAdvisor the eligibleForAdvisor to set
	 */
	public void setEligibleForAdvisor(boolean eligibleForAdvisor) {
		this.eligibleForAdvisor = eligibleForAdvisor;
	}
	/**
	 * @return the advisorShareWithStudents
	 */
	public boolean isAdvisorShareWithStudents() {
		return advisorShareWithStudents;
	}
	/**
	 * @param advisorShareWithStudents the advisorShareWithStudents to set
	 */
	public void setAdvisorShareWithStudents(boolean advisorShareWithStudents) {
		this.advisorShareWithStudents = advisorShareWithStudents;
	}
	/**
	 * @return the createPublicProfile
	 */
	public boolean isCreatePublicProfile() {
		return createPublicProfile;
	}
	/**
	 * @param createPublicProfile the createPublicProfile to set
	 */
	public void setCreatePublicProfile(boolean createPublicProfile) {
		this.createPublicProfile = createPublicProfile;
	}
	/**
	 * @return the publicProfileDescription
	 */
	public String getPublicProfileDescription() {
		return publicProfileDescription;
	}
	/**
	 * @param publicProfileDescription the publicProfileDescription to set
	 */
	public void setPublicProfileDescription(String publicProfileDescription) {
		this.publicProfileDescription = publicProfileDescription;
	}
	/**
	 * @return the publicProfileKey
	 */
	public String getPublicProfileKey() {
		return publicProfileKey;
	}
	/**
	 * @param publicProfileKey the publicProfileKey to set
	 */
	public void setPublicProfileKey(String publicProfileKey) {
		this.publicProfileKey = publicProfileKey;
	}
	
}
