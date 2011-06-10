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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

/**
 * {@link Date} and {@link Calendar} operations common across this application.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CommonDateOperations.java 2511 2010-09-08 15:45:37Z npblair $
 */
public final class CommonDateOperations {

    private static final long MILLISECS_PER_DAY = 24*60*60*1000;
    
	protected static final String DATE_TIME_FORMAT = "yyyyMMdd-HHmm";
	protected static final String DATE_FORMAT = "yyyyMMdd";

	/**
	 * @return a new instance of {@link SimpleDateFormat} that uses this application's common Date/Time format ("yyyyMMdd-HHmm").
	 */
	public static SimpleDateFormat getDateTimeFormat() {
		return new SimpleDateFormat(DATE_TIME_FORMAT);
	}

	/**
	 * @return a new instance of {@link SimpleDateFormat} that uses this application's common Date format ("yyyyMMdd").
	 */
	public static SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat(DATE_FORMAT);
	}

	/**
	 * Returns the Sunday prior to the date argument
	 * elements of the returned array, respectively.
	 * @param date
	 * @return the sunday prior to the date argument
	 */
	public static Date calculateSundayPrior(final Date date) {
		Calendar weekOfCal = Calendar.getInstance();
		weekOfCal.setTime(date);
		weekOfCal = DateUtils.truncate(weekOfCal, Calendar.DATE);

		int dayOfWeek = weekOfCal.get(Calendar.DAY_OF_WEEK);

		// dayOfWeek ranges 1-7
		int differenceToSunday = (dayOfWeek % 7) - 1;
		// subtract difference to sunday
		weekOfCal.add(Calendar.DATE, -differenceToSunday);
		return weekOfCal.getTime();
	}

	/**
	 * Convert a {@link String} in the common date/time format for this application into a {@link Date}.
	 * 
	 * @param timePhrase format: "yyyyMMdd-HHmm"
	 * @return the corresponding date
	 * @throws InputFormatException
	 */
	public static Date parseDateTimePhrase(final String timePhrase) throws InputFormatException {
		try {
			Date time = getDateTimeFormat().parse(timePhrase);
			time = DateUtils.truncate(time, Calendar.MINUTE);
			return time;
		} catch (ParseException e) {
			throw new InputFormatException("cannot parse date/time phrase " + timePhrase, e);
		}

	}

	/**
	 * Convert a {@link String} in the common date format for this application into a {@link Date}.
	 * 
	 * @param datePhrase format: "yyyyMMdd"
	 * @return the corresponding date
	 * @throws InputFormatException
	 */
	public static Date parseDatePhrase(final String datePhrase) throws InputFormatException {
		try {
			Date date = getDateFormat().parse(datePhrase);
			date = DateUtils.truncate(date, Calendar.DATE);
			return date;
		} catch (ParseException e) {
			throw new InputFormatException("cannot parse date phrase " + datePhrase, e);
		}
	}

	/**
	 * Returns true if the specified dates are equal OR if date1 is after date2.
	 * 
	 * @see DateUtils#isSameInstant(Date, Date)
	 * @see Date#after(Date)
	 * @param date1
	 * @param date2
	 * @return true if date1 is equal to or after date2
	 */
	public static boolean equalsOrAfter(Date date1, Date date2) {
		return DateUtils.isSameInstant(date1, date2) || date1.after(date2);
	}

	/**
	 * Returns true if the specified dates are equal OR if date1 is before date2.
	 * 
	 * @see DateUtils#isSameInstant(Date, Date)
	 * @see Date#before(Date)
	 * @param date1
	 * @param date2
	 * @return true if date1 is equal to or before date2
	 */
	public static boolean equalsOrBefore(Date date1, Date date2) {
		return DateUtils.isSameInstant(date1, date2) || date1.before(date2);
	}

	/**
	 * Returns the approximate difference in DAYS between start and end.
	 * 
	 * @param start
	 * @param end
	 * @return the approximate number of days between the 2 dates
	 */
	public static long approximateDifference(Date start, Date end) {
		Calendar s = Calendar.getInstance();
		s.setTime(start);
		Calendar e = Calendar.getInstance();
		e.setTime(end);

		long endL   =  e.getTimeInMillis() +  e.getTimeZone().getOffset(e.getTimeInMillis());
		long startL = s.getTimeInMillis() + s.getTimeZone().getOffset(s.getTimeInMillis());
		
		return (endL - startL) / MILLISECS_PER_DAY;
	}
	
	/**
	 * Helper method to calculate the first week's end date.
	 * 
	 * @param date
	 * @return the number of days from date until the following Sunday
	 */
	public static int numberOfDaysUntilSunday(final Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int result = 7;
		switch(dayOfWeek) {
		case Calendar.SUNDAY:
			result = 7;
			break;
		case Calendar.MONDAY:
			result = 6;
			break;
		case Calendar.TUESDAY:
			result = 5;
			break;
		case Calendar.WEDNESDAY:
			result = 4;
			break;
		case Calendar.THURSDAY:
			result = 3;
			break;
		case Calendar.FRIDAY:
			result = 2;
			break;
		case Calendar.SATURDAY:
			result = 1;
			break;
		}
		return result;
	}

	/**
	 * Simple method that sets Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, and Calendar.MILLISECOND to 0.
	 * @param calendar
	 * @return a calendar truncated to the beginning of the same day
	 */
	protected static Calendar zeroOutTimeFields(Calendar calendar) {
		return DateUtils.truncate(calendar, Calendar.DATE);
	}
	
	/**
	 * Return a new {@link Date} object that represents the beginning of the same day as the argument,
	 * e.g. 00:00:00.000
	 * 
	 * @param date
	 * @return a new {@link Date} object that represents the beginning of the same day as the argument
	 */
	public static Date beginningOfDay(Date date) {
		return DateUtils.truncate(date, Calendar.DATE);
	}
	
	/**
	 * Return a new {@link Date} object that represents the end of the same day as the argument,
	 * e.g. 23:59:59.999
	 * 
	 * @param date
	 * @return a new {@link Date} object that represents the end of the same day as the argument
	 */
	public static Date endOfDay(Date date) {
		Date local = beginningOfDay(date);
		local = DateUtils.addDays(local, 1);
		local = DateUtils.addMilliseconds(local, -1);
		return local;
	}
}
