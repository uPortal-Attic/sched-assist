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
import org.jasig.schedassist.model.IDelegateCalendarAccount;

/**
 * Interface that defines operations for
 * locating and searching for {@link IDelegateCalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: IDelegateCalendarAccountDao.java 2599 2010-09-16 17:42:21Z npblair $
 */
public interface IDelegateCalendarAccountDao  {

	/**
	 * Return a {@link List} of {@link IDelegateCalendarAccount} that correspond
	 * to the searchText argument and are assigned to the {@link ICalendarAccount} owner argument.
	 * 
	 * Implementations of this method must never return null; return an empty list if
	 * no matches can be found.
	 * 
	 * @see #searchForDelegates(String)
	 * @param searchText
	 * @param owner
	 * @return a never null, but possibly empty {@link List} of matching {@link IDelegateCalendarAccount}s.
	 */
	List<IDelegateCalendarAccount> searchForDelegates(String searchText, ICalendarAccount owner);
	
	/**
	 * Return a {@link List} of {@link IDelegateCalendarAccount} that correspond
	 * to the searchText argument.
	 * Implementations may decide internally which account attributes are used
	 * to map searchText. searchText will contain the asterisk ('*') character
	 * to serve as a wildcard.
	 * 
	 * Implementations of this method must never return null; return an empty list if
	 * no matches can be found.
	 * 
	 * @param searchText
	 * @return a never null, but possibly empty {@link List} of matching {@link IDelegateCalendarAccount}s.
	 */
	List<IDelegateCalendarAccount> searchForDelegates(String searchText);
	/**
	 * Return the specified {@link IDelegateCalendarAccount} by name, only skip resolution of the 
	 * owner {@link ICalendarAccount}.
	 * 
	 * @param accountName
	 * @return the named {@link IDelegateCalendarAccount}, only without its {@link ICalendarAccount} owner
	 */
	IDelegateCalendarAccount getDelegate(String accountName);
	
	/**
	 * Return the specified {@link IDelegateCalendarAccount} by name if the specified
	 * {@link ICalendarAccount} argument is the designated account owner.
	 * 
	 * @param accountName
	 * @param owner
	 * @return the {@link IDelegateCalendarAccount}, or null if not found
	 */
	IDelegateCalendarAccount getDelegate(String accountName, ICalendarAccount owner);
	
	
	/**
	 * Return the specified {@link IDelegateCalendarAccount} by calendar system unique id, only skip resolution of the 
	 * owner {@link ICalendarAccount}.
	 * 
	 * @param accountUniqueId
	 * @return the corresponding {@link IDelegateCalendarAccount}, only without its {@link ICalendarAccount} owner
	 */
	IDelegateCalendarAccount getDelegateByUniqueId(String accountUniqueId);
	
	/**
	 * Return the specified {@link IDelegateCalendarAccount} by unique id if the specified
	 * {@link ICalendarAccount} argument is the designated account owner.
	 * 
	 * @param accountUniqueId
	 * @param owner
	 * @return the {@link IDelegateCalendarAccount}, or null if not found
	 */
	IDelegateCalendarAccount getDelegateByUniqueId(String accountUniqueId, ICalendarAccount owner);
	
}
