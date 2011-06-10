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

import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.Attendee;

/**
 * {@link XParameter} that is added to an {@link Attendee} to earmark
 * the Attendee's role in the event (owner, visitor, or both).
 * 
 * BOTH is a special case where the owner and visitor are the same person.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AppointmentRole.java 1245 2009-11-10 22:08:04Z npblair $
 */
public class AppointmentRole extends XParameter {

	private static final long serialVersionUID = 53706L;
	
	public static final AppointmentRole OWNER = new AppointmentRole(Value.OWNER);
	public static final AppointmentRole VISITOR= new AppointmentRole(Value.VISITOR);
	public static final AppointmentRole BOTH = new AppointmentRole(Value.BOTH);
	
	/**
	 * iCalendar property name
	 */
	public static final String APPOINTMENT_ROLE = "X-UW-AVAILABLE-APPOINTMENT-ROLE";
	
	/**
	 * 
	 * @param value
	 */
	public AppointmentRole(String value) {
		this(Value.valueOf(value));
	}
	/**
	 * 
	 * @param value
	 */
	public AppointmentRole(Value value) {
		super(APPOINTMENT_ROLE, value.name());
	}
	
	/**
	 * 
	 * @return true if the value of this parameter matches OWNER
	 */
	public boolean isOwner() {
		Value value = Value.valueOf(this.getValue());
		return Value.OWNER.equals(value);
	}
	/**
	 * 
	 * @return if the value of this parameter matches VISITOR
	 */
	public boolean isVisitor() {
		Value value = Value.valueOf(this.getValue());
		return Value.VISITOR.equals(value);
	}
	/**
	 * 
	 * @return if the value of this parameter matches BOTH
	 */
	public boolean isBoth() {
		Value value = Value.valueOf(this.getValue());
		return Value.BOTH.equals(value);
	}
	/**
	 * Enum to represent possible values for the AppointmentRole X-Parameter.
	 *  
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: AppointmentRole.java 1245 2009-11-10 22:08:04Z npblair $
	 */
	public static enum Value {
		OWNER,
		VISITOR,
		BOTH;
	}
}
