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

package org.jasig.schedassist.messaging;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This class provides helper methods for converting {@link XMLGregorianCalendar}
 * objects to {@link Date}s and vice-versa.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: XMLDataUtils.java 2974 2011-01-25 13:44:23Z npblair $
 */
public class XMLDataUtils {

	/**
	 * Convert the {@link XMLGregorianCalendar} into a {@link Date}.
	 * 
	 * @param calendar
	 * @return the converted date
	 * @throws IllegalArgumentException if input is null
	 */
	public static Date convertXMLGregorianCalendarToDate(final XMLGregorianCalendar calendar) {
		if(null == calendar) {
			throw new IllegalArgumentException("cannot convert null date/time");
		}
		return calendar.toGregorianCalendar().getTime();
	}
	
	/**
	 * Convert the {@link Date} into a {@link XMLGregorianCalendar}.
	 * 
	 * Calls {@link DatatypeFactory#newInstance()} - if a {@link DatatypeConfigurationException}
	 * is thrown it get's wrapped in the unchecked {@link IllegalStateException}.
	 * 
	 * @param date
	 * @return the converted calendar
	 * @throws IllegalStateException wrapping a {@link DatatypeConfigurationException}.
	 */
	public static XMLGregorianCalendar convertDateToXMLGregorianCalendar(final Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException("unable to invoke DatatypeFactory.newInstance", e);
		}
	}
}
