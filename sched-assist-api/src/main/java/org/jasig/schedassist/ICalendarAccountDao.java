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

package org.jasig.schedassist;

import java.util.List;

import org.jasig.schedassist.model.ICalendarAccount;


/**
 * Interface for retrieving {@link ICalendarAccount} objects.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ICalendarAccountDao.java 2006 2010-04-26 15:15:26Z npblair $
 */
public interface ICalendarAccountDao {

	/**
	 * Locate a {@link ICalendarAccount} by username.
	 * 
	 * @param username
	 * @return the corresponding account, or null if not found.
	 */
	ICalendarAccount getCalendarAccount(String username);
	
	/**
	 * Locate a {@link ICalendarAccount} by unique id in the calendar system.
	 * 
	 * @param calendarUniqueId
	 * @return the corresponding account, or null if not found.
	 */
	ICalendarAccount getCalendarAccountFromUniqueId(String calendarUniqueId);
	
	/**
	 * Locate a {@link ICalendarAccount} by listed attributeName and attributeValue.
	 * 
	 * @param attributeName
	 * @param attributeValue
	 * @return the corresponding account, or null if not found.
	 */
	ICalendarAccount getCalendarAccount(String attributeName, String attributeValue);
	
	/**
	 * Return 0 or more {@link ICalendarAccount}s that correspond with the 
	 * searchText argument.
	 * Never returns null; will return an empty {@link List} if no users match.
	 * 
	 * @param searchText
	 * @return a never null, but possibly empty {@link List} of matching {@link ICalendarAccount}s.
	 */
	List<ICalendarAccount> searchForCalendarAccounts(String searchText);
}
