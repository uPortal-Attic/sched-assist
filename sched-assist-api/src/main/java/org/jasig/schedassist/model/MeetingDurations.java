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
 * Class to represent meeting durations.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: MeetingDurations.java 2335 2010-08-06 19:16:06Z npblair $
 */
public final class MeetingDurations implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8057215291650880899L;
	public static final MeetingDurations FIFTEEN = new MeetingDurations("15", 15, 15);
	public static final MeetingDurations THIRTY = new MeetingDurations("30", 30, 30);
	public static final MeetingDurations THIRTY_SIXTY = new MeetingDurations("30,60", 30, 60);
	public static final MeetingDurations FORTYFIVE = new MeetingDurations("45", 45, 45);

	private String key;
	private int minLength;
	private int maxLength;

	/**
	 * @param key
	 * @param minLength
	 */
	MeetingDurations(String key, int minLength, int maxLength) {
		this.key = key;
		this.minLength = minLength;
		this.maxLength = maxLength;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the minLength
	 */
	public int getMinLength() {
		return minLength;
	}

	/**
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return maxLength;
	}
	/**
	 * 
	 * @return true if maxLength is 2*minLength
	 */
	public boolean isDoubleLength() {
		return maxLength == (2*minLength);
	}

	/**
	 * Creates a {@link MeetingDurations} object from the argument String.
	 * 
	 * If the argument is a single number, a {@link MeetingDurations} will be created
	 * with minValue and maxValue set to the same integer value.
	 * This method also accepts a comma separated string containing two integers; the first
	 * will set minValue, the latter maxValue. 
	 * 
	 * @see Integer#parseInt(String)
	 * @param key
	 * @return an appropriate MeetingDurations instance from the key argument
	 * @throws IllegalArgumentException if the key contains characters that do not represent integers
	 */
	public static MeetingDurations fromKey(final String key) {	
		try {
			String [] tokens = key.split(",");
			int minValue = Integer.parseInt(tokens[0]);
			int maxValue = minValue;
			if(tokens.length == 2) {
				maxValue = Integer.parseInt(tokens[1]);
			}

			MeetingDurations durations = new MeetingDurations(key, minValue, maxValue);
			return durations;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("could not convert key to an integer " + key, e);
		}
	}
}
