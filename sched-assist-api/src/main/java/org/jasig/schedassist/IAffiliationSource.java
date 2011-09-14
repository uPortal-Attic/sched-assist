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

import org.jasig.schedassist.model.IAffiliation;
import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Interface definition operations regarding account "affiliations" (e.g. instructor, advisor, et al).
 * 
 * @author Nicholas Blair
 * @version $Id: IAffiliationSource.java $
 */
public interface IAffiliationSource {

	/**
	 * 
	 * @param calendarAccount
	 * @param affiliation
	 * @return true if the account has the specified affiliation
	 */
	boolean doesAccountHaveAffiliation(ICalendarAccount calendarAccount, IAffiliation affiliation);
}
