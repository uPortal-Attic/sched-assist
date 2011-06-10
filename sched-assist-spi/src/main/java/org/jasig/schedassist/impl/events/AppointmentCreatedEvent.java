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

package org.jasig.schedassist.impl.events;

import net.fortuna.ical4j.model.component.VEvent;

import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.context.ApplicationEvent;

/**
 * {@link ApplicationEvent} that is raised when an 
 * appointment is successfully scheduled.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AppointmentCreatedEvent.java 1911 2010-04-14 21:15:46Z npblair $
 */
public class AppointmentCreatedEvent extends AbstractAppointmentEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	private final String eventDescription;
	
	/**
	 * 
	 * @param event
	 * @param owner
	 * @param visitor
	 * @param block
	 */
	public AppointmentCreatedEvent(VEvent event, IScheduleOwner owner, IScheduleVisitor visitor, AvailableBlock block, String eventDescription) {
		super(event, owner, visitor, block);
		this.eventDescription = eventDescription;
	}

	/**
	 * @return the eventDescription
	 */
	public String getEventDescription() {
		return eventDescription;
	}
	
	
}
