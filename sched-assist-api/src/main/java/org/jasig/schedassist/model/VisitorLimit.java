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

import net.fortuna.ical4j.model.property.XProperty;


/**
 * {@link XProperty} that represents
 * the {@link AvailableBlock}'s visitor limit.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisitorLimit.java 1234 2009-11-09 15:03:46Z npblair $
 */
public class VisitorLimit extends XProperty {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;
	public static final String VISITOR_LIMIT = "X-UW-AVAILABLE-VISITOR-LIMIT";

	/**
	 * 
	 * @param value
	 */
	public VisitorLimit(int value) {
		super(VISITOR_LIMIT, Integer.toString(value));
	}

	/**
	 * 
	 * @return the visitor limit value as an integer
	 */
	public int getIntegerValue() {
		return Integer.parseInt(getValue());
	}
}
