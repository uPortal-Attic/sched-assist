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


package org.jasig.schedassist.web.owner.preferences;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * {@link Validator} for {@link AdvancedPreferencesFormBackingObject}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AdvancedPreferencesFormBackingObjectValidator.java 2304 2010-07-28 17:18:20Z npblair $
 */
public class AdvancedPreferencesFormBackingObjectValidator implements Validator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return AdvancedPreferencesFormBackingObject.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object target, Errors errors) {
		AdvancedPreferencesFormBackingObject fbo = (AdvancedPreferencesFormBackingObject) target;
		if(fbo.isCreatePublicProfile()) {
			if(StringUtils.isBlank(fbo.getPublicProfileDescription())) {
				errors.rejectValue("publicProfileDescription", "publicProfileDescription.empty", "Description field is required if creating a public profile.");
			} else if(fbo.getPublicProfileDescription().length() > 200) {
				errors.rejectValue("publicProfileDescription", "publicProfileDescription.length", "Description field must be 200 characters or less.");
			}
		}
	}

}
