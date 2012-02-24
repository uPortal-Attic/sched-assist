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

/**
 * Form backing object for {@link CreateAdhocRelationshipFormController} and
 * {@link DeauthorizeVisitorFormController}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ModifyAdhocRelationshipFormBackingObject.java 1932 2010-04-16 17:07:26Z npblair $
 */
public class ModifyAdhocRelationshipFormBackingObject {

	private String userSearchText;
	private String visitorUsername;
	private String relationship;
	
	/**
	 * @return the visitorUsername
	 */
	public String getVisitorUsername() {
		return visitorUsername;
	}

	/**
	 * @param visitorUsername the visitorUsername to set
	 */
	public void setVisitorUsername(String visitorUsername) {
		this.visitorUsername = visitorUsername;
	}

	/**
	 * @return the relationship
	 */
	public String getRelationship() {
		return relationship;
	}

	/**
	 * @param relationship the relationship to set
	 */
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	/**
	 * @return the userSearchText
	 */
	public String getUserSearchText() {
		return userSearchText;
	}

	/**
	 * @param userSearchText the userSearchText to set
	 */
	public void setUserSearchText(String userSearchText) {
		this.userSearchText = userSearchText;
	}

	@Override
	public String toString() {
		return "ModifyAdhocRelationshipFormBackingObject [userSearchText="
				+ userSearchText + ", visitorUsername=" + visitorUsername
				+ ", relationship=" + relationship + "]";
	}

}
