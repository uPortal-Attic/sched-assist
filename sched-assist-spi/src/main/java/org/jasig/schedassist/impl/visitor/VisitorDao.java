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

package org.jasig.schedassist.impl.visitor;

import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleVisitor;

/**
 * Interface that defines the mechanism for converting a {@link ICalendarAccount}
 * to a {@link IScheduleVisitor}.
 * Implementations can decide what logic is used to determine
 * which {@link ICalendarAccount}s are eligible for {@link IScheduleVisitor} status.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisitorDao.java 1900 2010-04-14 21:09:03Z npblair $
 */
public interface VisitorDao {

	/**
	 * Returns a {@link IScheduleVisitor} object for the specified {@link CalendarUser}, or
	 * throws a {@link NotAVisitorException} if not possible.
	 * Implementations should never return null.
	 * 
	 * @param calendarUser
	 * @return the corresponding {@link IScheduleVisitor} (never null)
	 * @throws NotAVisitorException if the {@link CalendarUser} is not eligible for the visitor role
	 */
	IScheduleVisitor toVisitor(ICalendarAccount calendarUser) throws NotAVisitorException;
}
