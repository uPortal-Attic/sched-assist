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

/**
 * 
 */
package org.jasig.schedassist.impl.caldav;

import net.fortuna.ical4j.model.Calendar;

/**
 * Java bean used to track a {@link Calendar} along with it's source URI 
 * (and etag, if available) in the CalDAV server.
 * 
 * @author Nicholas Blair
 * @version $ Id: CalendarWithURI.java $
 */
public class CalendarWithURI {

	private final Calendar calendar;
	private final String uri;
	private final String etag;
	
	/**
	 * 
	 * @param calendar
	 * @param uri
	 */
	public CalendarWithURI(Calendar calendar, String uri) {
		this(calendar, uri, null);
	}
	/**
	 * @param calendar
	 * @param uri
	 * @param etag
	 */
	public CalendarWithURI(Calendar calendar, String uri, String etag) {
		this.calendar = calendar;
		this.uri = uri;
		this.etag = etag;
	}
	/**
	 * @return the calendar
	 */
	public Calendar getCalendar() {
		return calendar;
	}
	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	/**
	 * @return the etag
	 */
	public String getEtag() {
		return etag;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CalendarWithURI [calendar=" + calendar + ", uri=" + uri
				+ ", etag=" + etag + "]";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((calendar == null) ? 0 : calendar.hashCode());
		result = prime * result + ((etag == null) ? 0 : etag.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CalendarWithURI other = (CalendarWithURI) obj;
		if (calendar == null) {
			if (other.calendar != null) {
				return false;
			}
		} else if (!calendar.equals(other.calendar)) {
			return false;
		}
		if (etag == null) {
			if (other.etag != null) {
				return false;
			}
		} else if (!etag.equals(other.etag)) {
			return false;
		}
		if (uri == null) {
			if (other.uri != null) {
				return false;
			}
		} else if (!uri.equals(other.uri)) {
			return false;
		}
		return true;
	}
	
	
}
