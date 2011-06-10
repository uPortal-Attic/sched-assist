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

/**
 * Form backing object for administrative forms to alter adhoc relationships.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ModifyAdhocRelationshipFormBackingObject.java 2978 2011-01-25 19:20:51Z npblair $
 */
public class ModifyAdhocRelationshipFormBackingObject {

	private long ownerId;
	private String visitorUsername;
	/**
	 * @return the ownerId
	 */
	public long getOwnerId() {
		return ownerId;
	}
	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
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
}
