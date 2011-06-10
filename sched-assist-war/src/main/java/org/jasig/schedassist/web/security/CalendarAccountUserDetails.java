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


package org.jasig.schedassist.web.security;

import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Extension of {@link UserDetails} to carry available-specific fields.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarAccountUserDetails.java 2306 2010-07-28 17:20:12Z npblair $
 */
public interface CalendarAccountUserDetails extends UserDetails {

	/**
	 * 
	 * @return a friendly display name for this account
	 */
	String getActiveDisplayName();
	
	/**
	 * 
	 * @return the {@link ICalendarAccount} for this account
	 */
	ICalendarAccount getCalendarAccount();
	
	/**
	 * 
	 * @return the {@link IScheduleOwner} for this account (if registered)
	 * @throws NotRegisteredException if the is account is not yet registered
	 */
	IScheduleOwner getScheduleOwner() throws NotRegisteredException;
	
	/**
	 * 
	 * @return true if this account is a delegate
	 */
	boolean isDelegate();
	
	/**
	 * Update the {@link IScheduleOwner} instance stored internally.
	 * Future calls to {@link #getScheduleOwner()} should retrieve this new instance.
	 * 
	 * @param owner
	 */
	void updateScheduleOwner(IScheduleOwner owner);
}
