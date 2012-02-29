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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interface to describe common properties of 
 * accounts with a remote calendar system.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ICalendarAccount.java 1898 2010-04-14 21:07:32Z npblair $
 */
public interface ICalendarAccount extends Serializable {

	/**
	 * The "display name" for the account.
	 * 
	 * @return The "display name" for the account.
	 */
	String getDisplayName();
	
	/**
	 * The common "username" for the account.
	 * May or may not be identical to {@link #getCalendarLoginId()}, depending
	 * on the calendar system (or account type).
	 * @return The common "username" for the account.
	 */
	String getUsername();
	
	/**
	 * 
	 * @return the email address for the account
	 */
	String getEmailAddress();
	
	/**
	 * Get the ID (as a {@link String}) used to authenticate with the calendar system.
	 * 
	 * @return the ID (as a {@link String}) used to authenticate with the calendar system.
	 */
	String getCalendarLoginId();
	
	/**
	 * Get the unique identifier (as a {@link String}) for this account in
	 * the calendar system.
	 * May or may not be identical to {@link #getCalendarLoginId()}, depending
	 * on the calendar system (or account type).
	 * 
	 * @return the unique identifier (as a {@link String}) for this account in  the calendar system.
	 */
	String getCalendarUniqueId();
	
	/**
	 * Get the value of the specified single-valued attribute on the account.
	 * 
	 * @param attributeName
	 * @return the value of the attribute
	 */
	String getAttributeValue(final String attributeName);
	
	/**
	 * Get the list of values of the specified multi-valued attribute on the account.
	 * 
	 * @param attributeName
	 * @return
	 */
	List<String> getAttributeValues(String attributeName);
	/**
	 * 
	 * @return a map of the account's attributes
	 */
	Map<String, List<String>> getAttributes();
	
	/**
	 * 
	 * @return true if this account is eligible for calendar service
	 */
	boolean isEligible();
}
