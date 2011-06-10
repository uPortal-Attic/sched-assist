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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides functions to provide hints
 * about roles and other identity information.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: IdentityUtils.java 1906 2010-04-14 21:12:01Z npblair $
 */
public final class IdentityUtils {

	public static final Log LOG = LogFactory.getLog(IdentityUtils.class);
	public static final String ADVISOR_FLAG_ATTRIBUTE = "wisceduadvisorflag";
	
	/**
	 * Detects if the {@link ICalendarAccount} is identifiable as
	 * an Academic Advisor.
	 *  
	 * @param calendarAccount
	 * @return true if the account is an academic advisor.
	 */
	public static boolean isAdvisor(final ICalendarAccount calendarAccount) {
		if(null == calendarAccount) {
			return false;
		}
		String advisorFlagValue = calendarAccount.getAttributeValue(ADVISOR_FLAG_ATTRIBUTE);
		if(LOG.isDebugEnabled()) {
			LOG.debug("calendarUser " + calendarAccount + " found advisor flag: " + advisorFlagValue);
		}
		boolean isAdvisor = "Y".equalsIgnoreCase(advisorFlagValue);
		return isAdvisor;
	}
	
	
}
