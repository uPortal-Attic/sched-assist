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

import java.io.Serializable;

/**
 * Interface to represent a "Schedule Visitor."
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: IScheduleVisitor.java 1898 2010-04-14 21:07:32Z npblair $
 */
public interface IScheduleVisitor extends Serializable {

	/**
	 * 
	 * @return the {@link ICalendarAccount} for this visitor
	 */
	ICalendarAccount getCalendarAccount();
}
