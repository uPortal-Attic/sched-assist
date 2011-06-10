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
 * Default implementation of {@link IScheduleVisitor}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DefaultScheduleVisitorImpl.java 2996 2011-01-27 17:38:54Z npblair $
 */
public class DefaultScheduleVisitorImpl implements IScheduleVisitor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;
	private final ICalendarAccount calendarAccount;
	
	
	/**
	 * @param calendarAccount
	 */
	public DefaultScheduleVisitorImpl(ICalendarAccount calendarAccount) {
		this.calendarAccount = calendarAccount;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.IScheduleVisitor#getCalendarAccount()
	 */
	@Override
	public ICalendarAccount getCalendarAccount() {
		return this.calendarAccount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DefaultScheduleVisitorImpl [calendarAccount=");
		builder.append(calendarAccount);
		builder.append("]");
		return builder.toString();
	}

}
