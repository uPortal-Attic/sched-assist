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

import java.util.Date;

import net.fortuna.ical4j.model.component.VEvent;

import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;

/**
 * Data model for reminder objects.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 * @version $Id: IReminder.java 3051 2011-02-04 16:34:14Z npblair $
 */
public interface IReminder {

	/**
	 * 
	 * @return unique identifier for this {@link IReminder} instance
	 */
	long getReminderId();
	/**
	 * 
	 * @return
	 */
	IScheduleOwner getScheduleOwner();
	
	/**
	 * 
	 * @return
	 */
	ICalendarAccount getRecipient();
	
	/**
	 * 
	 * @return
	 */
	Date getSendTime();
	
	/**
	 * 
	 * @return
	 */
	VEvent getEvent();
}
