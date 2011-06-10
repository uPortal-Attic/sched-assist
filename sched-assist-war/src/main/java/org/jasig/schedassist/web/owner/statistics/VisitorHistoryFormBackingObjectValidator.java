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


package org.jasig.schedassist.web.owner.statistics;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * {@link Validator} implementation for {@link VisitorHistoryFormBackingObject}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisitorHistoryFormBackingObjectValidator.java 2522 2010-09-10 16:06:13Z npblair $
 */
public class VisitorHistoryFormBackingObjectValidator implements Validator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class<?> clazz) {
		return VisitorHistoryFormBackingObject.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "visitorUsername", "visitorUsername.blank", "No person matching your input was found.");
		
		VisitorHistoryFormBackingObject fbo = (VisitorHistoryFormBackingObject) target;
		if(null == fbo.getStartTime()) {
			errors.rejectValue("startTime", "startTime.notset", "Start time must be set (format MM/dd/YYYY).");
		}
		
		if(null == fbo.getEndTime()) {
			errors.rejectValue("endTime", "endTime.notset", "End time must be set (format MM/dd/YYYY).");
		}
	}

}
