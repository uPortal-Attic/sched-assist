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
 * {@link XProperty} that describes the version of
 * the Scheduling Assistant software used to create this event.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableVersion.java 1989 2010-04-23 17:14:11Z npblair $
 */
public class AvailableVersion extends XProperty {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;
	
	public static final String AVAILABLE_VERSION = "X-UW-AVAILABLE-VERSION";
	public static final AvailableVersion AVAILABLE_VERSION_1_0 = new AvailableVersion("1.0");
	public static final AvailableVersion AVAILABLE_VERSION_1_1 = new AvailableVersion("1.1");
	public static final AvailableVersion AVAILABLE_VERSION_1_2 = new AvailableVersion("1.2");
	
	/**
	 * 
	 * @param value
	 */
	public AvailableVersion(final String value) {
		super(AVAILABLE_VERSION);
		this.setValue(value);
	}
}
