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

package org.jasig.schedassist.impl.owner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link OwnerAuthorization} implementation that checks for a
 * non-empty value of a specific attribute on the {@link ICalendarAccount}.
 * 
 * If attributeValuePattern is set, the {@link #isEligible(ICalendarAccount)}
 * implementation also checks that the user's attribute value matches
 * the pattern.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Header: RequiredAttributeOwnerAuthorizationImpl.java Exp $
 */
public class RequiredAttributeOwnerAuthorizationImpl implements
		OwnerAuthorization {

	private Log LOG = LogFactory.getLog(this.getClass());
	private String attributeName = "wisceduisisadvisoremplid";
	private Pattern attributeValuePattern;
	
	/**
	 * @param attributeName the attributeName to set
	 */
	@Required
	public void setAttributeName(final String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @param attributeValuePattern the attributeValuePattern to set
	 */
	public void setAttributeValuePattern(final String attributeValuePattern) {
		this.attributeValuePattern = Pattern.compile(attributeValuePattern);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.OwnerAuthorization#isEligible(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public boolean isEligible(final ICalendarAccount user) {
		String userAttributeValue = user.getAttributeValue(this.attributeName);
		if(null != attributeValuePattern) {
			// test for pattern equality
			Matcher m = attributeValuePattern.matcher(userAttributeValue);
			if(m.matches()) {
				LOG.debug("user is eligible " + user);
				return true;
			} 
		} else {
			// test just for existence (non-blank)
			if(!StringUtils.isBlank("userAttributeValue")) {
				LOG.debug("user is eligible " + user);
				return true;
			}
		}
		LOG.debug("user is not eligible " + user);
		return false;
	}

}
