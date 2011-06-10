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

import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;

/**
 * Basic interface defining a mechanism for identifying which 
 * {@link ICalendarAccount}s are eligible for the {@link IScheduleOwner} role.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: OwnerAuthorization.java 1900 2010-04-14 21:09:03Z npblair $
 */
public interface OwnerAuthorization {

	/**
	 * 
	 * @param user
	 * @return true if the user is eligible, false if not
	 */
	boolean isEligible(ICalendarAccount user);
}
