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

import java.util.List;
import java.util.Map;

/**
 * @author Nicholas Blair
 * @version $Id: MockDelegateCalendarAccount.java $
 */
public class MockDelegateCalendarAccount implements IDelegateCalendarAccount {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8813143938160632214L;
	private String displayName;
	private String username;
	private String emailAddress;
	private String calendarLoginId;
	private String calendarUniqueId;
	private String location;
	private String contactInformation;
	private Map<String, List<String>> attributes;
	private boolean eligible;
	private ICalendarAccount accountOwner;
	private String accountOwnerAttribute;
	
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	/**
	 * @return the calendarLoginId
	 */
	public String getCalendarLoginId() {
		return calendarLoginId;
	}
	/**
	 * @param calendarLoginId the calendarLoginId to set
	 */
	public void setCalendarLoginId(String calendarLoginId) {
		this.calendarLoginId = calendarLoginId;
	}
	/**
	 * @return the calendarUniqueId
	 */
	public String getCalendarUniqueId() {
		return calendarUniqueId;
	}
	/**
	 * @param calendarUniqueId the calendarUniqueId to set
	 */
	public void setCalendarUniqueId(String calendarUniqueId) {
		this.calendarUniqueId = calendarUniqueId;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the contactInformation
	 */
	public String getContactInformation() {
		return contactInformation;
	}
	/**
	 * @param contactInformation the contactInformation to set
	 */
	public void setContactInformation(String contactInformation) {
		this.contactInformation = contactInformation;
	}
	@Override
	public String getAttributeValue(String attributeName) {
		List<String> attributeValues = getAttributeValues(attributeName);
		if(attributeValues == null) {
			return null;
		}
		
		if(attributeValues.size() == 1) {
			return attributeValues.get(0);
		}
		
		return null;
	}
	@Override
	public List<String> getAttributeValues(String attributeName) {
		return attributes.get(attributeName);
	}
	@Override
	public Map<String, List<String>> getAttributes() {
		return attributes;
	}
	@Override
	public boolean isEligible() {
		return eligible;
	}
	@Override
	public boolean isDelegate() {
		return true;
	}
	@Override
	public ICalendarAccount getAccountOwner() {
		return accountOwner;
	}
	/**
	 * @return the accountOwnerAttribute
	 */
	public String getAccountOwnerAttribute() {
		return accountOwnerAttribute;
	}
	/**
	 * @param accountOwnerAttribute the accountOwnerAttribute to set
	 */
	public void setAccountOwnerAttribute(String accountOwnerAttribute) {
		this.accountOwnerAttribute = accountOwnerAttribute;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MockDelegateCalendarAccount [displayName=" + displayName
				+ ", username=" + username + ", emailAddress=" + emailAddress
				+ ", calendarLoginId=" + calendarLoginId
				+ ", calendarUniqueId=" + calendarUniqueId + ", location="
				+ location + ", contactInformation=" + contactInformation
				+ ", attributes=" + attributes + ", eligible=" + eligible
				+ ", accountOwner=" + accountOwner + ", accountOwnerAttribute="
				+ accountOwnerAttribute + "]";
	}
	

}
