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
 * Abstract {@link IScheduleVisitor} implementation.
 * All {@link ICalendarAccount} methods are implemented by delegating to the 
 * internal {@link ICalendarAccount} required by the sole constructor.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AbstractScheduleVisitor.java 1898 2010-04-14 21:07:32Z npblair $
 */
public abstract class AbstractScheduleVisitor implements IScheduleVisitor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final ICalendarAccount calendarAccount;
	
	/**
	 * 
	 * @param calendarAccount
	 */
	public AbstractScheduleVisitor(final ICalendarAccount calendarAccount) {
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
}
