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
package org.jasig.schedassist.impl.ldap;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Default {@link LDAPAttributesKey} implementation.
 * 
 * "uid" is used for username and uniqueIdentifier by default.
 * 
 * @author Nicholas Blair
 * @version $Id: LDAPAttributesKey.java $
 */
public class LDAPAttributesKeyImpl implements LDAPAttributesKey {

	private String usernameAttributeName = "uid";
	private String displayNameAttributeName = "cn";
	private String eligibilityAttributeName = "eligibility";
	private String emailAddressAttributeName = "mail";
	private String uniqueIdentifierAttributeName = "uid";
	private String delegateOwnerAttributeName = "owneruid";
	private String delegateLocationAttributeName = "postaladdress";
	private String delegateContactInformationAttributeName = "telephonenumber";
	
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.ldap.LDAPAttributesKey#getUsernameAttributeName()
	 */
	@Override
	public String getUsernameAttributeName() {
		return usernameAttributeName;
	}
	/**
	 * @param usernameAttributeName the usernameAttributeName to set
	 */
	public void setUsernameAttributeName(String usernameAttributeName) {
		this.usernameAttributeName = usernameAttributeName;
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.ldap.LDAPAttributesKey#getDisplayNameAttributeName()
	 */
	@Override
	public String getDisplayNameAttributeName() {
		return displayNameAttributeName;
	}
	/**
	 * @param displayNameAttributeName the displayNameAttributeName to set
	 */
	public void setDisplayNameAttributeName(String displayNameAttributeName) {
		this.displayNameAttributeName = displayNameAttributeName;
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.ldap.LDAPAttributesKey#getEligibilityAttributeName()
	 */
	@Override
	public String getEligibilityAttributeName() {
		return eligibilityAttributeName;
	}
	/**
	 * @param eligibilityAttributeName the eligibilityAttributeName to set
	 */
	public void setEligibilityAttributeName(String eligibilityAttributeName) {
		this.eligibilityAttributeName = eligibilityAttributeName;
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.ldap.LDAPAttributesKey#getEmailAddressAttributeName()
	 */
	@Override
	public String getEmailAddressAttributeName() {
		return emailAddressAttributeName;
	}
	/**
	 * @param emailAddressAttributeName the emailAddressAttributeName to set
	 */
	public void setEmailAddressAttributeName(String emailAddressAttributeName) {
		this.emailAddressAttributeName = emailAddressAttributeName;
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.ldap.LDAPAttributesKey#getUniqueIdentifierAttributeName()
	 */
	@Override
	public String getUniqueIdentifierAttributeName() {
		return uniqueIdentifierAttributeName;
	}
	/**
	 * @param uniqueIdentifierAttributeName the uniqueIdentifierAttributeName to set
	 */
	public void setUniqueIdentifierAttributeName(
			String uniqueIdentifierAttributeName) {
		this.uniqueIdentifierAttributeName = uniqueIdentifierAttributeName;
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.ldap.LDAPAttributesKey#getDelegateOwnerAttributeName()
	 */
	@Override
	public String getDelegateOwnerAttributeName() {
		return delegateOwnerAttributeName;
	}
	/**
	 * @param delegateOwnerAttributeName the delegateOwnerAttributeName to set
	 */
	public void setDelegateOwnerAttributeName(String delegateOwnerAttributeName) {
		this.delegateOwnerAttributeName = delegateOwnerAttributeName;
	}
	
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.ldap.LDAPAttributesKey#getDelegateLocationAttributeName()
	 */
	@Override
	public String getDelegateLocationAttributeName() {
		return delegateLocationAttributeName;
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.ldap.LDAPAttributesKey#getDelegateContactInformationAttributeName()
	 */
	@Override
	public String getDelegateContactInformationAttributeName() {
		return delegateContactInformationAttributeName;
	}
	/**
	 * @param delegateLocationAttributeName the delegateLocationAttributeName to set
	 */
	public void setDelegateLocationAttributeName(
			String delegateLocationAttributeName) {
		this.delegateLocationAttributeName = delegateLocationAttributeName;
	}
	/**
	 * @param delegateContactInformationAttributeName the delegateContactInformationAttributeName to set
	 */
	public void setDelegateContactInformationAttributeName(
			String delegateContactInformationAttributeName) {
		this.delegateContactInformationAttributeName = delegateContactInformationAttributeName;
	}
	/**
	 * Default implementation returns true if the uniqueIdentifier attribute is not empty.
	 * 
	 * @see org.jasig.schedassist.impl.ldap.LDAPAttributesKey#evaluateEligibilityAttributeValue(java.lang.String)
	 */
	@Override
	public boolean evaluateEligibilityAttributeValue(Map<String, String> attributes) {
		if(attributes == null || attributes.isEmpty()) {
			return false;
		}
		final String uniqueId = attributes.get(getUniqueIdentifierAttributeName());
		return StringUtils.isNotBlank(uniqueId);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((delegateContactInformationAttributeName == null) ? 0
						: delegateContactInformationAttributeName.hashCode());
		result = prime
				* result
				+ ((delegateLocationAttributeName == null) ? 0
						: delegateLocationAttributeName.hashCode());
		result = prime
				* result
				+ ((delegateOwnerAttributeName == null) ? 0
						: delegateOwnerAttributeName.hashCode());
		result = prime
				* result
				+ ((displayNameAttributeName == null) ? 0
						: displayNameAttributeName.hashCode());
		result = prime
				* result
				+ ((eligibilityAttributeName == null) ? 0
						: eligibilityAttributeName.hashCode());
		result = prime
				* result
				+ ((emailAddressAttributeName == null) ? 0
						: emailAddressAttributeName.hashCode());
		result = prime
				* result
				+ ((uniqueIdentifierAttributeName == null) ? 0
						: uniqueIdentifierAttributeName.hashCode());
		result = prime
				* result
				+ ((usernameAttributeName == null) ? 0 : usernameAttributeName
						.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LDAPAttributesKeyImpl other = (LDAPAttributesKeyImpl) obj;
		if (delegateContactInformationAttributeName == null) {
			if (other.delegateContactInformationAttributeName != null)
				return false;
		} else if (!delegateContactInformationAttributeName
				.equals(other.delegateContactInformationAttributeName))
			return false;
		if (delegateLocationAttributeName == null) {
			if (other.delegateLocationAttributeName != null)
				return false;
		} else if (!delegateLocationAttributeName
				.equals(other.delegateLocationAttributeName))
			return false;
		if (delegateOwnerAttributeName == null) {
			if (other.delegateOwnerAttributeName != null)
				return false;
		} else if (!delegateOwnerAttributeName
				.equals(other.delegateOwnerAttributeName))
			return false;
		if (displayNameAttributeName == null) {
			if (other.displayNameAttributeName != null)
				return false;
		} else if (!displayNameAttributeName
				.equals(other.displayNameAttributeName))
			return false;
		if (eligibilityAttributeName == null) {
			if (other.eligibilityAttributeName != null)
				return false;
		} else if (!eligibilityAttributeName
				.equals(other.eligibilityAttributeName))
			return false;
		if (emailAddressAttributeName == null) {
			if (other.emailAddressAttributeName != null)
				return false;
		} else if (!emailAddressAttributeName
				.equals(other.emailAddressAttributeName))
			return false;
		if (uniqueIdentifierAttributeName == null) {
			if (other.uniqueIdentifierAttributeName != null)
				return false;
		} else if (!uniqueIdentifierAttributeName
				.equals(other.uniqueIdentifierAttributeName))
			return false;
		if (usernameAttributeName == null) {
			if (other.usernameAttributeName != null)
				return false;
		} else if (!usernameAttributeName.equals(other.usernameAttributeName))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LDAPAttributesKeyImpl [usernameAttributeName="
				+ usernameAttributeName + ", displayNameAttributeName="
				+ displayNameAttributeName + ", eligibilityAttributeName="
				+ eligibilityAttributeName + ", emailAddressAttributeName="
				+ emailAddressAttributeName
				+ ", uniqueIdentifierAttributeName="
				+ uniqueIdentifierAttributeName
				+ ", delegateOwnerAttributeName=" + delegateOwnerAttributeName
				+ ", delegateLocationAttributeName="
				+ delegateLocationAttributeName
				+ ", delegateContactInformationAttributeName="
				+ delegateContactInformationAttributeName + "]";
	}
	
}
