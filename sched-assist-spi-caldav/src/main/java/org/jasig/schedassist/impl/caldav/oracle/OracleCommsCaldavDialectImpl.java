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

package org.jasig.schedassist.impl.caldav.oracle;

import org.apache.commons.lang.Validate;
import org.jasig.schedassist.impl.caldav.DefaultCaldavDialectImpl;
import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Sub class of {@link DefaultCaldavDialectImpl} useful for Oracle Communications Suite.
 * 
 * <strong>Note:</strong> Oracle Communications Suite can use the {@link DefaultCaldavDialectImpl}, only use 
 * this instance if you need to provide Scheduling Assistant features to accounts across multiple domains 
 * (e.g. department1.university.edu, department2.university.edu, etc).
 * 
 * @author Nicholas Blair
 * @version $Id: OracleCommsCaldavDialectImpl.java $
 */
public class OracleCommsCaldavDialectImpl extends DefaultCaldavDialectImpl {

	/**
	 * Overrides the default behavior to use the {@link ICalendarAccount}'s email address in the 
	 * path (rather than username).
	 * 
	 * Useful only if you provide the Scheduling Assistant to accounts across multiple domains; if
	 * you do not the {@link DefaultCaldavDialectImpl} will suffice.
	 * 
	 *  (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.DefaultCaldavDialectImpl#getCalendarAccountHome(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public String getCalendarAccountHome(ICalendarAccount calendarAccount) {
		Validate.notNull(calendarAccount, "calendarAccount argument must not be null");
		final String emailAddress = calendarAccount.getEmailAddress();
		Validate.notNull(emailAddress, "emailAddress in calendarAccount argument must not be null");
		
		StringBuilder uri = new StringBuilder();
		uri.append(getCaldavHost().toString());
		uri.append(getAccountHomePrefix());
		uri.append(emailAddress);
		uri.append(getAccountHomeSuffix());
		return uri.toString();
	}

}
