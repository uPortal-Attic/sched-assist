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

package org.jasig.schedassist;



/**
 * Exception that signals a conflict exists within the schedule.\
 * Most likely thrown when attempting to schedule an appointment 
 * and the time is no longer available.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Header: ConflictExistsException.java $
 */
public class ConflictExistsException extends SchedulingException {

	private static final long serialVersionUID = 53706L;
	
	/**
	 * 
	 */
	public ConflictExistsException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConflictExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ConflictExistsException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ConflictExistsException(Throwable cause) {
		super(cause);
	}

}
