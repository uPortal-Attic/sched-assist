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


package org.jasig.schedassist.web.visitor;

/**
 * Thrown by the controllers in this package if the request is for
 * a {@link ScheduleOwner} not related to the current authenticated
 * {@link ScheduleVisitor}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: OwnerNotFoundException.java 1739 2010-02-16 18:19:23Z npblair $
 */
public class OwnerNotFoundException extends Exception {

	private static final long serialVersionUID = 53706L;
	
	/**
	 * 
	 */
	public OwnerNotFoundException() {
	}

	/**
	 * @param message
	 */
	public OwnerNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public OwnerNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OwnerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
