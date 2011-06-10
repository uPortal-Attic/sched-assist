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

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * {@link Validator} for {@link CancelAppointmentFormBackingObject}s.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CancelAppointmentFormBackingObjectValidator.java 2285 2010-07-23 15:04:09Z npblair $
 */
public class CancelAppointmentFormBackingObjectValidator implements Validator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return CancelAppointmentFormBackingObject.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object command, Errors errors) {
		CancelAppointmentFormBackingObject fbo = (CancelAppointmentFormBackingObject) command;

		if(!fbo.isConfirmCancel()) {
			errors.rejectValue("confirmCancel", "confirmCancel.false", "Please mark the checkbox to confirm you wish to cancel/leave the appointment.");
		}
		if(!fbo.isMultipleVisitors()) {
			//TODO check reason field
		} 
	}

}
