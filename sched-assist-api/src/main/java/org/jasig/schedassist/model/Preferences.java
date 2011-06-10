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
import java.util.Map;

/**
 * Enum to store the displayName and key for the different 
 * Schedule Owner preferences stored in the Scheduling Assistant system.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: Preferences.java 2983 2011-01-26 21:52:38Z npblair $
 */
public enum Preferences {

	NOTEBOARD("Noteboard", "owner.NOTEBOARD", ""),
	LOCATION("Meeting Location", "owner.LOCATION", "TBD"),
	MEETING_PREFIX("Meeting Title Prefix", "owner.PREFIX", "Appointment"),
	DURATIONS("Appointment Durations", "owner.DURATION", "30"),
	VISIBLE_WINDOW("Visible Window", "owner.VISIBLE_WINDOW", "24,3"),
	ADVISOR_SHARE_WITH_STUDENTS("Advisor: Share with Assigned Advisees", "advisor.SHARE_WITH_STUDENTS", "false"),
	DEFAULT_VISITOR_LIMIT("Default number of visitors per available block", "owner.DEFAULT_VISITOR_LIMIT", "1"),
	MEETING_LIMIT("Maximum number of meetings a visitor may have within window", "owner.MEETING_LIMIT", "-1"),
	REFLECT_SCHEDULE("Reflect Availability Schedule back to WiscCal as Daily Notes", "owner.REFLECT_SCHEDULE", "false"),
	REMINDERS("Send Reminders to visitors in advance of their appointments", "owner.EMAIL_REMINDERS", "false,false,24");
	
	private String displayName;
	private String key;
	private String defaultValue = "";
	
	/**
	 * @param displayName
	 * @param key
	 */
	private Preferences(String displayName, String key) {
		this.displayName = displayName;
		this.key = key;
	}

	/**
	 * 
	 * @param displayName
	 * @param key
	 * @param defaultValue
	 */
	private Preferences(String displayName, String key, String defaultValue) {
		this.displayName = displayName;
		this.key = key;
		this.defaultValue = defaultValue;
	}
	
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * 
	 * @param key
	 * @return null if no {@link Preferences} has a matching key
	 */
	public static Preferences fromKey(final String key) {
		for(Preferences single : Preferences.values()) {
			if(single.getKey().equals(key)) {
				return single;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param displayName
	 * @return null if no {@link Preferences} has a matching displayName
	 */
	public static Preferences fromDisplayName(final String displayName) {
		for(Preferences single : Preferences.values()) {
			if(single.getDisplayName().equals(displayName)) {
				return single;
			}
		}
		return null;
	}
	
	/**
	 * Returns a {@link Map} containing all {@link Preferences} as keys mapped
	 * to their default values (as {@link String}s).
	 * 
	 * @return a {@link Map} of {@link Preferences} as keys and their default values
	 */
	public static Map<Preferences, String> getDefaultPreferences() {
		Map<Preferences, String> prefs = new HashMap<Preferences, String>();
		for(Preferences single : Preferences.values()) {
			prefs.put(single, single.getDefaultValue());
		}
		return prefs;
	}
	
}
