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

package org.jasig.schedassist.impl.statistics;

import java.util.Date;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * Bean used to tie a {@link Date} with a count of events.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DailyEventSummary.java $
 */
public class DailyEventSummary implements Comparable<DailyEventSummary> {

	private Date date;
	private long eventCount;
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the eventCount
	 */
	public long getEventCount() {
		return eventCount;
	}
	/**
	 * @param eventCount the eventCount to set
	 */
	public void setEventCount(long eventCount) {
		this.eventCount = eventCount;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DailyEventSummary [date=");
		builder.append(date);
		builder.append(", eventCount=");
		builder.append(eventCount);
		builder.append("]");
		return builder.toString();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (int) (eventCount ^ (eventCount >>> 32));
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
		if (!(obj instanceof DailyEventSummary)) {
			return false;
		}
		DailyEventSummary other = (DailyEventSummary) obj;
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		if (eventCount != other.eventCount) {
			return false;
		}
		return true;
	}
	/**
	 * 
	 * @param rhs
	 * @return
	 */
	@Override
	public int compareTo(DailyEventSummary rhs) {
		return new CompareToBuilder().append(this.date, rhs.date).toComparison();
	}
}
