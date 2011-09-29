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

import java.util.HashMap;
import java.util.Map;

import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;

/**
 * {@link IDelegateCalendarAccount} sourced by LDAP.
 * 
 * @author Nicholas Blair
 * @version $Id: LDAPDelegateCalendarAccountImpl.java $
 */
class LDAPDelegateCalendarAccountImpl implements IDelegateCalendarAccount {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	private final String calendarUniqueId;
	private final String emailAddress;
	private final String displayName;
	private final String username;
	private final boolean eligible;
	private final String location;
	private final String contactInformation;
	private Map<String, String> attributesMap = new HashMap<String, String>();
	
	private ICalendarAccount accountOwner;
	
	/**
	 * Initializes a delegate account without setting the accountOwner.
	 * 
	 * @param attributes
	 * @param ldapAttributesKey
	 */
	public LDAPDelegateCalendarAccountImpl(Map<String, String> attributes, LDAPAttributesKey ldapAttributesKey) {
		this(attributes, ldapAttributesKey, null);
	}
	
	/**
	 * Default implementation.
	 * 
	 * @param attributes
	 * @param ldapAttributesKey
	 * @param accountOwner
	 */
	public LDAPDelegateCalendarAccountImpl(Map<String, String> attributes, LDAPAttributesKey ldapAttributesKey,
			ICalendarAccount accountOwner) {
		this.attributesMap = attributes;
		this.accountOwner = accountOwner;
		// populate fields first
		calendarUniqueId = attributes.get(ldapAttributesKey.getUniqueIdentifierAttributeName());
		displayName = attributes.get(ldapAttributesKey.getDisplayNameAttributeName());
		emailAddress = attributes.get(ldapAttributesKey.getEmailAddressAttributeName());
		username = attributes.get(ldapAttributesKey.getUsernameAttributeName());
		location = attributes.get(ldapAttributesKey.getDelegateLocationAttributeName());
		contactInformation = attributes.get(ldapAttributesKey.getDelegateContactInformationAttributeName());
		// set eligibility
		eligible = ldapAttributesKey.evaluateEligibilityAttributeValue(attributes);
	}
	
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getEmailAddress()
	 */
	@Override
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Default implementation returns username.
	 *  (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getCalendarLoginId()
	 */
	@Override
	public String getCalendarLoginId() {
		return getUsername();
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getCalendarUniqueId()
	 */
	@Override
	public String getCalendarUniqueId() {
		return calendarUniqueId;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getAttributeValue(java.lang.String)
	 */
	@Override
	public String getAttributeValue(String attributeName) {
		return this.attributesMap.get(attributeName);
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getAttributes()
	 */
	@Override
	public Map<String, String> getAttributes() {
		return this.attributesMap;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#isEligible()
	 */
	@Override
	public boolean isEligible() {
		return eligible;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.IDelegateCalendarAccount#getAccountOwner()
	 */
	@Override
	public ICalendarAccount getAccountOwner() {
		return accountOwner;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.IDelegateCalendarAccount#getAccountOwnerUsername()
	 */
	@Override
	public String getAccountOwnerUsername() {
		if(accountOwner != null) {
			return accountOwner.getUsername();
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.IDelegateCalendarAccount#getLocation()
	 */
	@Override
	public String getLocation() {
		return location;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.IDelegateCalendarAccount#getContactInformation()
	 */
	@Override
	public String getContactInformation() {
		return contactInformation;
	}

}
