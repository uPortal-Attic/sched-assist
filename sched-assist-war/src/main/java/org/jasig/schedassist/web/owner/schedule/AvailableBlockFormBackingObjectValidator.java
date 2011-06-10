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


package org.jasig.schedassist.web.owner.schedule;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.InputFormatException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * {@link Validator} for {@link AvailableBlockFormBackingObject}s.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableBlockFormBackingObjectValidator.java 2304 2010-07-28 17:18:20Z npblair $
 */
public class AvailableBlockFormBackingObjectValidator implements Validator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class<?> clazz) {
		return AvailableBlockFormBackingObject.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startTimePhrase", "field.required", "Start time field is required.");
		AvailableBlockFormBackingObject fbo = (AvailableBlockFormBackingObject) target;

		Date startTime = null;
		Date endTime = null;
		// validate startTimePhrase (if present)
		if(StringUtils.isNotBlank(fbo.getStartTimePhrase())) {
			try {
				startTime = CommonDateOperations.parseDateTimePhrase(fbo.getStartTimePhrase());
			} catch (InputFormatException e) {
				errors.rejectValue("startTimePhrase", "field.parseexception", "Start time does not match expected format (YYYYmmDD-hhmm)");
			}
		}
		// validate endTimePhrase if present
		if(StringUtils.isNotBlank(fbo.getEndTimePhrase())) {
			try {
				endTime = CommonDateOperations.parseDateTimePhrase(fbo.getEndTimePhrase());
			} catch (InputFormatException e) {
				errors.rejectValue("endTimePhrase", "field.parseexception", "End time does not match expected format (YYYYmmDD-hhmm)");
			}
		}
		if(fbo.getVisitorLimit() < 1) {
			errors.rejectValue("visitorLimit", "visitorLimit.toosmall", "Visitor limit must be greater than or equal to 1.");
		}
		if(fbo.getVisitorLimit() > 99) {
			errors.rejectValue("visitorLimit", "visitorLimit.toolarge", "Maximum value for visitor limit is 99.");
		}

		if(null != startTime && null != endTime) {
			// validate AvailabilityBlock construction
			try {
				AvailableBlockBuilder.createBlock(fbo.getStartTimePhrase(), fbo.getEndTimePhrase());
			} catch (InputFormatException e) {
				errors.reject("field.inputformatexception", e.getMessage());
			} 
		}
	}

}
