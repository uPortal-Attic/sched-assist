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

package org.jasig.schedassist.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock {@link AbstractCalendarAccount} implementation.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: MockCalendarAccount.java 1899 2010-04-14 21:08:06Z npblair $
 */
class MockCalendarAccount extends AbstractCalendarAccount {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	private Map<String,List<String>> attributes = new HashMap<String, List<String>>();
	
	/**
	 * 
	 * @param attributeName
	 * @param attributeValue
	 */
	public void setAttributeValue(String attributeName, String attributeValue) {
		List<String> values = getAttributeListSafely(attributeName);
		values.add(attributeValue);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.AbstractCalendarAccount#getAttributes()
	 */
	@Override
	public Map<String, List<String>> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Map<String, List<String>> attributes) {
		this.attributes = attributes;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.AbstractCalendarAccount#getCalendarLoginId()
	 */
	@Override
	public String getCalendarLoginId() {
		return getUsername();
	}

}
