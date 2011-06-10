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

/**
 * Class to represent the {@link IScheduleOwner}'s preferences
 * for schedule visibility.
 * 
 * windowHoursStart defines the minimum amount of time from "now" (in hours)
 * that a {@link IScheduleVisitor} is allowed to schedule an appointment.
 * windowWeeksEnd defines the maximum amount of time from "now" (in weeks)
 * that a {@link IScheduleVisitor} is allowed to schedule an appointment.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleWindow.java 690 2009-05-14 16:57:58Z npblair $
 */
public final class VisibleWindow {

	private String key;
	private int windowHoursStart;
	private int windowWeeksEnd;
	
	/**
	 * Default value for {@link VisibleWindow} is 24 hours (start) and 3 weeks (end).
	 */
	public static VisibleWindow DEFAULT = new VisibleWindow("24,3", 24, 3);
	
	/**
	 * @param key
	 * @param windowHoursStart
	 * @param windowWeeksEnd
	 */
	VisibleWindow(String key, int windowHoursStart, int windowWeeksEnd) {
		this.key = key;
		this.windowHoursStart = windowHoursStart;
		this.windowWeeksEnd = windowWeeksEnd;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @return the windowHoursStart
	 */
	public int getWindowHoursStart() {
		return windowHoursStart;
	}
	/**
	 * @return the windowWeeksEnd
	 */
	public int getWindowWeeksEnd() {
		return windowWeeksEnd;
	}
	
	/**
	 * Static method to generate a {@link VisibleWindow} from
	 * it's {@link String} storage format.
	 * 
	 * The lone argument must be formatted as follows:
	 <pre>
	 windowHoursStart,windowWeeksEnd
	 </pre>
	 * Both values must be integers, separated by a single comma.
	 * 
	 * @param key
	 * @return an appropriate visible window from the key
	 * @throws IllegalArgumentException
	 */
	public static VisibleWindow fromKey(final String key) {
		String [] tokens = key.split(",");
		if(tokens.length != 2) {
			throw new IllegalArgumentException("key must be formatted as 'start,end': " + key);
		}
		try {
			int minValue = Integer.parseInt(tokens[0]);
			int maxValue = Integer.parseInt(tokens[1]);
		
			VisibleWindow window = new VisibleWindow(key, minValue, maxValue);
			return window;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("values must be integers: " + key, e);
		}
	}
}
