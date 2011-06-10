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

package org.jasig.schedassist.impl.relationship.advising;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

/**
 * A term at UW Madison is designated by a 4 digit integer with the following
 * rules:
 <ul>
 <li>in the left most digit, a 0 corresponds with the 20th century, a 1 corresponds with the 21st</li>
 <li>the second and third digits represet the last 2 digits of the year (e.g. 09 for 2009)</li>
 <li>the last digit is a 2 for Fall, 4 for Spring, and 6 for Summer (3 for oft-unused Winter)</li>
 </ul>
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: TermCalculator.java 1711 2010-02-15 16:20:17Z npblair $
 */
public class TermCalculator {

	/**
	 * 
	 * @return
	 */
	public static String getCurrentTerm() {
		return calculateTerm(Calendar.getInstance());
	}
	
	/**
	 * Calculate the term number for the specified date time (as a {@link Calendar}).
	 * This uses a rough formula to determine Fall/Spring/Summer semesters:
	 <pre>
	 if the month is between January and May (inclusive), the term is Spring.
	 if the month is between June and August (inclusive), the term is Summer.
	 if the month is between September and December (inclusive), the term is Fall.
	 </pre>
	 *
	 * This is only approximate.
	 * 
	 * @param calendar
	 * @return
	 */
	public static String calculateTerm(final Calendar calendar) {
		StringBuilder term = new StringBuilder();
		
		
		int month = calendar.get(Calendar.MONTH);
		String monthDigit;
		if(month >= Calendar.JANUARY && month <= Calendar.MAY) {
			monthDigit = "4";
		} else if (month >= Calendar.JUNE && month <= Calendar.AUGUST) {
			monthDigit = "6";
		} else {
			monthDigit = "2";
		}
		
		int year = calendar.get(Calendar.YEAR);
		if("2".equals(monthDigit)) {
			// increment year by one for fall semester
			year++;
		}
		
		String centuryDigit = "0";
		if (year >= 2000) {
			centuryDigit = "1";
		}
		
		int twoDigitYear = year % 100;
		String yearDigit = StringUtils.leftPad(Integer.toString(twoDigitYear), 2, "0");
		
		term.append(centuryDigit);
		term.append(yearDigit);
		term.append(monthDigit);
		return term.toString();
	}
	
	/**
	 * 
	 * @param termLeft
	 * @param termRight
	 * @returntrue if the termLeft is less than or equals to termRight
	 */
	public static boolean termLessThanOrEquals(String termLeft, String termRight) {
		return termLeft.compareTo(termRight) <= 0;
	}
	
	/**
	 * 
	 * @param termLeft
	 * @param termRight
	 * @return true if the termLeft is greater than or equals to termRight
	 */
	public static boolean termGreaterThanOrEquals(String termLeft, String termRight) {
		return termLeft.compareTo(termRight) >= 0;
	}
	
}
