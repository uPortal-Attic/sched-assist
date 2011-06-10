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

import org.jasig.schedassist.impl.events.AbstractAppointmentEvent;
import org.jasig.schedassist.impl.events.AppointmentCancelledEvent;
import org.jasig.schedassist.impl.events.AppointmentCreatedEvent;
import org.jasig.schedassist.impl.events.AppointmentJoinedEvent;
import org.jasig.schedassist.impl.events.AppointmentLeftEvent;

/**
 * Enum to list the various available appointment event types.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: EventType.java 2208 2010-06-22 16:06:22Z npblair $
 */
public enum EventType {

	CREATED,
	CANCELLED,
	JOINED,
	LEFT;
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	public static EventType fromEvent(AbstractAppointmentEvent e) {
		if(e instanceof AppointmentCreatedEvent) {
			return CREATED;
		} else if (e instanceof AppointmentCancelledEvent) {
			return CANCELLED;
		} else if (e instanceof AppointmentJoinedEvent) {
			return JOINED;
		} else if (e instanceof AppointmentLeftEvent) {
			return LEFT;
		} else {
			throw new IllegalArgumentException("unknown event type " + e.getClass() );
		}
	}
}
