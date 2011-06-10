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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * {@link Validator} for {@link ClearAvailableScheduleFormBackingObject}s.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ClearAvailableScheduleFormBackingObjectValidator.java 2304 2010-07-28 17:18:20Z npblair $
 */
public class ClearAvailableScheduleFormBackingObjectValidator implements
		Validator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class<?> clazz) {
		return ClearAvailableScheduleFormBackingObject.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object command, Errors errors) {
		ClearAvailableScheduleFormBackingObject fbo = (ClearAvailableScheduleFormBackingObject) command;
		if(fbo.isConfirmedCancelWeek()) {
			try {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				df.parse(fbo.getWeekOfPhrase());
			} catch (ParseException e) {
				errors.rejectValue("weekOfPhrase", "field.weekofphraseformat", "weekOf must be formatted as \"yyyyMMdd-HHmm\"");
			}
		}
	}

}
