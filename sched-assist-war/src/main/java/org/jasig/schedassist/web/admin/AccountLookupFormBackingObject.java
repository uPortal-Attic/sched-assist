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
 * Form backing object for {@link AccountLookupFormController}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AccountLookupFormBackingObject.java 2978 2011-01-25 19:20:51Z npblair $
 */
public class AccountLookupFormBackingObject {

	private String searchText = "";
	private String ctcalxitemid;
	private String username;
	private String resourceName;
	
	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}
	/**
	 * @param searchText the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	/**
	 * @return the ctcalxitemid
	 */
	public String getCtcalxitemid() {
		return ctcalxitemid;
	}
	/**
	 * @param ctcalxitemid the ctcalxitemid to set
	 */
	public void setCtcalxitemid(String ctcalxitemid) {
		this.ctcalxitemid = ctcalxitemid;
	}
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
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}
	/**
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountLookupFormBackingObject [searchText=");
		builder.append(searchText);
		builder.append(", ctcalxitemid=");
		builder.append(ctcalxitemid);
		builder.append(", username=");
		builder.append(username);
		builder.append(", resourceName=");
		builder.append(resourceName);
		builder.append("]");
		return builder.toString();
	}

}
