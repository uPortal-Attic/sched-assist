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

import net.fortuna.ical4j.model.property.XProperty;

/**
 * {@link XProperty} added to Daily notes to signal that the event is a 
 * reflection of a period of Availability in an owner's schedule.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailabilityReflection.java $
 */
public class AvailabilityReflection extends XProperty {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5289589738727306035L;
	
	/**
	 * Property name
	 */
	public static final String AVAILABILITY_REFLECTION = "X-UW-AVAILABILITY-REFLECTION";
	/**
	 * 
	 */
	public static final AvailabilityReflection TRUE = new AvailabilityReflection("TRUE");
	
	private AvailabilityReflection(String value) {
		super(AVAILABILITY_REFLECTION);
		this.setValue(value);
	}
}
