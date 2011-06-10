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

package org.jasig.schedassist.portlet;

/**
 * Exception raised when an action requiring an {@link IScheduleOwner} doesn't have
 * enough information for the owner or the information points to an owner that doesn't exist.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ScheduleOwnerNotFoundException.java 770 2009-07-07 18:36:51Z npblair $
 */
public class ScheduleOwnerNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	/**
	 * 
	 */
	public ScheduleOwnerNotFoundException() {
	}

	/**
	 * @param message
	 */
	public ScheduleOwnerNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ScheduleOwnerNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ScheduleOwnerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
