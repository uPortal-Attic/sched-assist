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

package org.jasig.schedassist.impl.owner;

import java.util.Map;

import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.Preferences;

/**
 * Interface that defines the mechanism for converting a {@link ICalendarAccount}
 * to an {@link IScheduleOwner}.
 * Implementations can decide what logic is used to determine
 * which {@link ICalendarAccount}s are eligible for {@link IScheduleOwner} status.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: OwnerDao.java 2124 2010-05-19 16:36:43Z npblair $
 */
public interface OwnerDao {

	/**
	 * Register a {@link ICalendarAccount} with the available application.
	 * Successful invocation results in a record of the user stored in the 
	 * available data source.
	 * 
	 * Returns the completed {@link ScheduleOwner} that corresponds.
	 * 
	 * @param calendarUser
	 * @return a {@link ScheduleOwner} 
	 * @throws IneligibleException if the {@link ICalendarAccount} is not eligible
	 */
	IScheduleOwner register(ICalendarAccount calendarUser) throws IneligibleException;
	
	/**
	 * Unregister the specified {@link IScheduleOwner}.
	 * 
	 * Successful invocation only removes {@link IScheduleOwner} account data and 
	 * and preferences; no appointments created will be touched.
	 * 
	 * @param owner
	 */
	void removeAccount(IScheduleOwner owner);
	
	/**
	 * Attempt to locate an existing {@link IScheduleOwner} for the specified
	 * {@link ICalendarAccount}, returning null if not registered.
	 * 
	 * @param calendarUser
	 * @return
	 */
	IScheduleOwner locateOwner(ICalendarAccount calendarUser);
	
	/**
	 * Attempt to locate an existing {@link IScheduleOwner} by  
	 * internal id, returning null if not found.
	 * 
	 * @see ScheduleOwner#getId()
	 * @param internalId
	 * @return
	 */
	IScheduleOwner locateOwnerByAvailableId(long internalId);
	
	/**
	 * Set the value for the specified {@link Preferences} for the {@link IScheduleOwner}.
	 * 
	 * The returned {@link IScheduleOwner} will have the updated preferences.
	 * 
	 * @param owner
	 * @param preference
	 * @param value
	 * @return
	 */
	IScheduleOwner updatePreference(IScheduleOwner owner, Preferences preference, String value);
	
	/**
	 * Retrieve a single preference value for the given {@link IScheduleOwner}.
	 * 
	 * @param owner
	 * @param preference
	 * @return
	 */
	String retreivePreference(IScheduleOwner owner, Preferences preference);
	
	/**
	 * Remove the {@link Preferences} value from this {@link IScheduleOwner}'s account
	 * completely.
	 * The net effect of this call should make future calls to {@link #retreivePreference(IScheduleOwner, Preferences)}
	 * for the same {@link Preferences} return the system's default value.
	 * 
	 * @param owner
	 * @param preference
	 */
	IScheduleOwner removePreference(IScheduleOwner owner, Preferences preference);
	
	/**
	 * Retrieve all stored preferences->values for the given {@link IScheduleOwner}.
	 * 
	 * @param owner
	 * @return
	 */
	Map<Preferences, String> retrievePreferences(IScheduleOwner owner);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	String lookupUsername(long internalId);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	String lookupUniqueId(long internalId);
	
}
