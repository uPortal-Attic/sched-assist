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

import java.io.Serializable;

/**
 * Bean to represent the reminders preference.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 *
 */
public class Reminders implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7394013183165410327L;

	private final String key;
	private final boolean enabled;
	private final boolean includeOwner;
	private final int hours;
	
	/**
	 * Default email reminders (disabled).
	 */
	public static final Reminders DEFAULT = Reminders.fromKey(Preferences.REMINDERS.getDefaultValue());
	/**
	 * 
	 * @param key
	 * @param enabled
	 * @param includeOwner
	 * @param hours
	 */
	Reminders(String key, boolean enabled, boolean includeOwner, int hours) {
		this.key = key;
		this.enabled = enabled;
		this.includeOwner = includeOwner;
		this.hours = hours;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @return the includeOwner
	 */
	public boolean isIncludeOwner() {
		return includeOwner;
	}
	/**
	 * @return the hours
	 */
	public int getHours() {
		return hours;
	}

	/**
	 * 
	 * @param keyValue
	 * @return a {@link Reminders} instance from the key value
	 * @throws IllegalArgumentException for keys that couldn't be converted
	 */
	public static Reminders fromKey(String keyValue) {
		try {
			String [] tokens = keyValue.split(",");
			if(tokens.length != 3) {
				throw new IllegalArgumentException("could not convert key to an EmailReminders " + keyValue); 
			}
			boolean enabled = Boolean.parseBoolean(tokens[0]);
			boolean includeOwner = Boolean.parseBoolean(tokens[1]);
			int minutes = Integer.parseInt(tokens[2]);
			
			Reminders result = new Reminders(keyValue, enabled, includeOwner, minutes);
			return result;
			
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("could not convert key to an EmailReminders " + keyValue, e);
		}
	}
}
