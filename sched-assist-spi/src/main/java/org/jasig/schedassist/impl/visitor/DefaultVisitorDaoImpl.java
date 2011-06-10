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
import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.stereotype.Service;

/**
 * Simple {@link VisitorDao} = everyone's a visitor as long as:
 *<ol>
 * <li>the argument is not an instance of {@link IDelegateCalendarAccount}</li>
 * <li>{@link ICalendarAccount#isEligible()} returns true</li>
 *</ol>
 *   
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SimpleVisitorDaoImpl.java 2589 2010-09-16 15:47:17Z npblair $
 */
@Service("visitorDao")
public class DefaultVisitorDaoImpl implements VisitorDao {

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.visitor.VisitorDao#toVisitor(org.jasig.schedassist.model.ICalendarAccount)
	 */
	public IScheduleVisitor toVisitor(final ICalendarAccount calendarUser) throws NotAVisitorException {
		if(null != calendarUser && !(calendarUser instanceof IDelegateCalendarAccount) && calendarUser.isEligible()) {
			return new DefaultScheduleVisitorImpl(calendarUser);
		} else {
			throw new NotAVisitorException("not eligible for Calendar Service");
		}
	}

}
