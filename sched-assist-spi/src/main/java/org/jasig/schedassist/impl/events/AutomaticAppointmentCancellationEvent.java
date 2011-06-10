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

import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.context.ApplicationEvent;

/**
 * {@link ApplicationEvent} raised when an appointment is cancelled automatically.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AutomaticAppointmentCancellationEvent.java $
 */
public class AutomaticAppointmentCancellationEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2666099764667899136L;
	private final ICalendarAccount owner;
	private final Reason reason;
	/**
	 * @param source
	 */
	public AutomaticAppointmentCancellationEvent(VEvent event, ICalendarAccount owner, Reason reason) {
		super(event);
		this.owner = owner;
		this.reason = reason;
	}
	/**
	 * 
	 * @return
	 */
	public final VEvent getEvent() {
		return (VEvent) getSource();
	}
	/**
	 * @return the owner
	 */
	public ICalendarAccount getOwner() {
		return owner;
	}
	
	/**
	 * @return the reason
	 */
	public Reason getReason() {
		return reason;
	}

	public enum Reason {
		OWNER_DECLINED,
		NO_REMAINING_VISITORS;
	}
}
