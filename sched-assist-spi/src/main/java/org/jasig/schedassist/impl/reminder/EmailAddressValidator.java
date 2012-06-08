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

package org.jasig.schedassist.impl.reminder;

import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Validator for the reminder service to determine
 * whether or not an account's email address attribute
 * is a valid recipient for email.
 * 
 * Some deployers can use this interface to explicitly prevent
 * mail going to some domains or addresses.
 * 
 * @author Nicholas Blair
 */
public interface EmailAddressValidator {

	/**
	 * 
	 * @param calendarAccount
	 * @return true if we can send a message to this address
	 */
	boolean canSendToEmailAddress(ICalendarAccount calendarAccount);
}
