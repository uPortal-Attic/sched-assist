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
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * {@link Validator} for {@link CreateAppointmentFormBackingObject}s.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CreateAppointmentFormBackingObjectValidator.java 2285 2010-07-23 15:04:09Z npblair $
 */
public class CreateAppointmentFormBackingObjectValidator implements Validator {


	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return CreateAppointmentFormBackingObject.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object command, Errors errors) {
		CreateAppointmentFormBackingObject fbo = (CreateAppointmentFormBackingObject) command;

		if(fbo.isMultipleVisitors()) {
			if(!fbo.isConfirmJoin()) {
				errors.rejectValue("confirmJoin", "confirmJoin.false", "Please mark the checkbox to confirm you wish to join the appointment.");
			}
		} else {
			// reason cannot be null if only 1 visitor
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reason", "field.required", "Reason for the meeting is required.");
			
			// selectedDuration only present if 1 visitor
			if(!fbo.getMeetingDurationsAsList().contains(fbo.getSelectedDuration())) {
				errors.rejectValue("selectedDuration", "selectedDuration.outofbounds", "Unacceptable value for meeting duration");
			}
		}
		
	}

}
