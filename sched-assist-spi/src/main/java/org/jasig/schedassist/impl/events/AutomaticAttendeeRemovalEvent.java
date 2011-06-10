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

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;

import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.context.ApplicationEvent;

/**
 * {@link ApplicationEvent} raised when an {@link Attendee} is automatically
 * pruned from an event.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AutomaticAttendeeRemovalEvent.java $
 */
public class AutomaticAttendeeRemovalEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5567515737237527390L;
	private final ICalendarAccount owner;
	private final Property removed;
	/**
	 * @param source
	 */
	public AutomaticAttendeeRemovalEvent(VEvent event, ICalendarAccount owner, Property attendee) {
		super(event);
		this.owner = owner;
		this.removed = attendee;
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
	 * @return the removed
	 */
	public Property getRemoved() {
		return removed;
	}
}
