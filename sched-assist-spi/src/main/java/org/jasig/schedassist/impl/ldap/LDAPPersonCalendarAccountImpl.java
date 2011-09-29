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

/**
 * 
 */
package org.jasig.schedassist.impl.ldap;

import java.util.HashMap;
import java.util.Map;

import org.jasig.schedassist.model.AbstractCalendarAccount;

/**
 * {@link AbstractCalendarAccount} implementation to represent a 
 * person sourced from LDAP.
 * 
 * @author Nicholas BlairNicholas Blair
 * @version $Id: LDAPPersonCalendarAccountImpl.java $
 */
class LDAPPersonCalendarAccountImpl extends AbstractCalendarAccount {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1794331642591042311L;

	private Map<String, String> attributesMap = new HashMap<String, String>();
	/**
	 * Default implementation.
	 * 
	 * @param attributes
	 * @param ldapAttributesKey
	 */
	public LDAPPersonCalendarAccountImpl(Map<String, String> attributes, LDAPAttributesKey ldapAttributesKey) {
		this.attributesMap = attributes;
		// populate fields first
		setCalendarUniqueId(attributes.get(ldapAttributesKey.getUniqueIdentifierAttributeName()));
		setDisplayName(attributes.get(ldapAttributesKey.getDisplayNameAttributeName()));
		setEmailAddress(attributes.get(ldapAttributesKey.getEmailAddressAttributeName()));
		setUsername(attributes.get(ldapAttributesKey.getUsernameAttributeName()));
		// set eligibility
		setEligible(ldapAttributesKey.evaluateEligibilityAttributeValue(attributes));
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.AbstractCalendarAccount#getAttributeValue(java.lang.String)
	 */
	@Override
	public String getAttributeValue(String attributeName) {
		return this.attributesMap.get(attributeName);
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.AbstractCalendarAccount#getAttributes()
	 */
	@Override
	public Map<String, String> getAttributes() {
		return this.attributesMap;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.AbstractCalendarAccount#getCalendarLoginId()
	 */
	@Override
	public String getCalendarLoginId() {
		return getUsername();
	}

}
