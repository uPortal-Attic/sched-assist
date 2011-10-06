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

import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Interface definining the LDAP attributes that are used to bind
 * to {@link ICalendarAccount} fields.
 * 
 * @author Nicholas Blair
 * @version $Id: LdapAttributesKey.java $
 */
public interface LDAPAttributesKey {

	/**
	 * @return the usernameAttributeName
	 */
	public String getUsernameAttributeName();

	/**
	 * @return the displayNameAttributeName
	 */
	public String getDisplayNameAttributeName();

	/**
	 * @return the eligibilityAttributeName
	 */
	public String getEligibilityAttributeName();

	/**
	 * @return the emailAddressAttributeName
	 */
	public String getEmailAddressAttributeName();

	/**
	 * @return the uniqueIdentifierAttributeName
	 */
	public String getUniqueIdentifierAttributeName();

	/**
	 * @return the delegateOwnerAttributeName
	 */
	public String getDelegateOwnerAttributeName();
	
	/**
	 * @return the delegateLocationAttributeName
	 */
	public String getDelegateLocationAttributeName();
	/**
	 * @return the delegateContactInformationAttributeName
	 */
	public String getDelegateContactInformationAttributeName();
	/**
	 * @return the passwordAttributeName
	 */
	public String getPasswordAttributeName();
	/**
	 * Return true if the value of the eligibilityAttribute equates to the
	 * account being eligible for service.
	 * 
	 * The reason for this method is due to the LDAP attribute being a String 
	 * that may have different interpretations. It could just be "true"; "Y" or "not empty" 
	 * could also evaluate to true eligibility.
	 * 
	 * @return true if the value of the attribute means the account is eligible for service
	 */
	public boolean evaluateEligibilityAttributeValue(Map<String, String> attributes);

}