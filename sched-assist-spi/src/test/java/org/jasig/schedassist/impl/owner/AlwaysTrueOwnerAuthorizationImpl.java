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

import org.jasig.schedassist.impl.owner.OwnerAuthorization;
import org.jasig.schedassist.model.ICalendarAccount;

/**
 * {@link #isEligible(ICalendarAccount)} always returns true, useful for testing.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AlwaysTrueOwnerAuthorizationImpl.java $
 */
public class AlwaysTrueOwnerAuthorizationImpl implements OwnerAuthorization {

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.OwnerAuthorization#isEligible(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public boolean isEligible(ICalendarAccount user) {
		return true;
	}

}
