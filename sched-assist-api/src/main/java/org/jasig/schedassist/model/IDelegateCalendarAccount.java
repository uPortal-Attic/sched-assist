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




/**
 * Extension of {@link ICalendarAccount} that represents a (potentially non-human)
 * calendar account that can be administered by another {@link ICalendarAccount} (which
 * is the return value of {@link #getAccountOwner()}).
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: IDelegateCalendarAccount.java 2294 2010-07-27 16:54:52Z npblair $
 */
public interface IDelegateCalendarAccount extends ICalendarAccount {

	/**
	 * 
	 * @return the {@link ICalendarAccount} responsible for administering this delegate account.
	 */
	ICalendarAccount getAccountOwner();
	
	/**
	 * 
	 * @return the username of this delegate's account owner.
	 */
	String getAccountOwnerAttribute();
	
	/**
	 * 
	 * @return the location for this delegate, if defined (may return null)
	 */
	String getLocation();

	/**
	 * 
	 * @return contact information for this delegate, if defined (may return null)
	 */
	String getContactInformation();
}
