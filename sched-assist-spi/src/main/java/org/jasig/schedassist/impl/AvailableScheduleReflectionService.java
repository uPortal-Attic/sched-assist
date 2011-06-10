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

package org.jasig.schedassist.impl;

import java.util.Date;

import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.IScheduleOwner;

/**
 * Service interface to manage the flow of {@link AvailableSchedule}s
 * into back into the {@link CalendarDao}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableScheduleReflectionService.java $
 */
public interface AvailableScheduleReflectionService {

	/**
	 * Reflect the specified {@link IScheduleOwner}'s {@link AvailableSchedule}
	 * into the {@link CalendarDao}.
	 * 
	 * Implementations are not required to be synchronous, e.g. this method may
	 * return before the synchronization action is taken.
	 * 
	 * @param owner
	 */
	void reflectAvailableSchedule(IScheduleOwner owner);
	
	/**
	 * Reflect the specified {@link IScheduleOwner}'s (identified by their internal
	 * available owner id number) {@link AvailableSchedule} into the {@link CalendarDao}.
	 * 
	 * Implementations are not required to be synchronous, e.g. this method may
	 * return before the synchronization action is taken.
	 * 
	 * @param ownerId
	 */
	void reflectAvailableSchedule(long ownerId);
	
	/**
	 * Remove any reflections from the specified {@link IScheduleOwner}'s
	 * account between the {@link Date}s specified.
	 * 
	 * Implementations are not required to be synchronous, e.g. this method may
	 * return before the synchronization action is taken.
	 * 
	 * @param owner
	 * @param start
	 * @param end
	 */
	void purgeReflections(IScheduleOwner owner, Date start, Date end);
	
	/**
	 * Remove any reflections from the specified {@link IScheduleOwner}'s (identified by their 
	 * internal available owner id number) account between the {@link Date}s specified.
	 * 
	 * Implementations are not required to be synchronous, e.g. this method may
	 * return before the synchronization action is taken.
	 * 
	 * @param ownerId
	 * @param start
	 * @param end
	 */
	void purgeReflections(long ownerId, Date start, Date end);
}
