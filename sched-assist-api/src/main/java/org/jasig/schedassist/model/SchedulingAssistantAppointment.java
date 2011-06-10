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
 * {@link XProperty} to be set on all appointments created via
 * the Scheduling Assistant system.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableAppointment.java 2373 2010-08-19 15:13:09Z npblair $
 */
public class SchedulingAssistantAppointment extends XProperty {

	private static final long serialVersionUID = 53706L;
	
	/**
	 * Property name: X-UW-AVAILABLE-APPOINTMENT
	 */
	public static final String AVAILABLE_APPOINTMENT = "X-UW-AVAILABLE-APPOINTMENT";
	
	/**
	 * Property that represents this event IS an appointment created via the Scheduling Assistant system.
	 */
	public static final SchedulingAssistantAppointment TRUE = new SchedulingAssistantAppointment("TRUE");

	/**
	 * @param value
	 */
	private SchedulingAssistantAppointment(String value) {
		super(AVAILABLE_APPOINTMENT);
		this.setValue(value);
	}
	
}
