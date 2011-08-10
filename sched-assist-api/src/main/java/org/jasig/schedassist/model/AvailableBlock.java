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
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateUtils;

/**
 * Representation of a block of availability.
 * Constructor is package private, and should not be called in normal usage.
 * Instead, use {@link AvailableBlockBuilder}.
 * {@link AvailableBlock}s are immutable.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableBlock.java 2335 2010-08-06 19:16:06Z npblair $
 */
public final class AvailableBlock implements Comparable<AvailableBlock>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7574365284572265106L;
	private static int MILLISECONDS_PER_MINUTE = 60 * 1000;
	private final Date startTime;
	private final Date endTime;
	private final int visitorLimit;
	private final String meetingLocation;
	
	private int visitorsAttending = 0;
	
	/**
	 * @param startTime
	 * @param endTime
	 * @throws 
	 */
	AvailableBlock(final Date startTime, final Date endTime)  {
		this(startTime, endTime, 1);
	}
	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @param visitorLimit
	 * @throws IllegalArgumentException if startTime/endTime are null, or if endTime is before or equal to startTime, or if visitorLimit is less than 1 
	 */
	AvailableBlock(final Date startTime, final Date endTime, final int visitorLimit) {
		this(startTime, endTime, visitorLimit, null);
	}
	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @param visitorLimit
	 * @param meetingLocation
	 * @throws IllegalArgumentException if startTime/endTime are null, or if endTime is before or equal to startTime, or if visitorLimit is less than 1 
	 */
	AvailableBlock(final Date startTime, final Date endTime, final int visitorLimit, String meetingLocation) {
		Validate.notNull(startTime, "startTime cannot be null");
		Validate.notNull(endTime, "endTime cannot be null");
		if(endTime.before(startTime) || endTime.equals(startTime)) {
			throw new IllegalArgumentException("startTime (" + startTime + ") must precede endTime (" + endTime + ")");
		}
		if(visitorLimit < 1) {
			throw new IllegalArgumentException("visitorLimit must be greater than or equal to 1: " + visitorLimit);
		}
		this.startTime = DateUtils.truncate(startTime, Calendar.MINUTE);
		this.endTime = DateUtils.truncate(endTime, Calendar.MINUTE);
		this.visitorLimit = visitorLimit;
		this.meetingLocation = meetingLocation;
	}
	/**
	 * 
	 * @param sourceBlock
	 */
	AvailableBlock(final AvailableBlock sourceBlock) {
		this.startTime = sourceBlock.startTime;
		this.endTime = sourceBlock.endTime;
		this.visitorLimit = sourceBlock.visitorLimit;
		this.meetingLocation = sourceBlock.meetingLocation;
	}
	
	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return new Date(endTime.getTime());
	}	
	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return new Date(startTime.getTime());
	}
	/**
	 * @return the visitorLimit
	 */
	public int getVisitorLimit() {
		return visitorLimit;
	}
	
	/**
	 * Get the meetingLocation specified for this block. This may return null; in that case
	 * consumers should use the schedule owner's default meetingLocation (via preferences).
	 * @return the meetingLocation
	 */
	public String getMeetingLocation() {
		return meetingLocation;
	}
	/**
	 * 
	 * @return the duration of this block in minutes
	 */
	public int getDurationInMinutes() {
		long start = startTime.getTime();
		long end = endTime.getTime();
		int minutes = (int)(end - start) / MILLISECONDS_PER_MINUTE;
		return minutes;
	}

	/**
	 * 
	 * @param visitorsAttending
	 */
	public void setVisitorsAttending(final int visitorsAttending) {
		this.visitorsAttending = visitorsAttending;
	}
	/**
	 * 
	 * @return the number of visitorsAttending (not always set)
	 */
	public int getVisitorsAttending() {
		return visitorsAttending;
	}
	
	/**
	 * Order of comparison:
	 * <ol>
	 * <li>startTime</li>
	 * <li>endTime</li>
	 * </ol>
	 * 
	 * The visitorLimit and meetingLocation fields are immaterial to comparison.
	 * 
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(AvailableBlock o) {
		return new CompareToBuilder()
			.append(this.startTime, o.startTime)
			.append(this.endTime, o.endTime)
			.toComparison();
	}
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof AvailableBlock)) {
			return false;
		}
		AvailableBlock rhs = (AvailableBlock) object;
		return new EqualsBuilder()
			.append(this.startTime, rhs.startTime)
			.append(this.endTime, rhs.endTime)
			.isEquals();
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("endTime", this.endTime)
			.append("startTime", this.startTime)
			.append("visitorLimit", this.visitorLimit)
			.append("visitorsAttending", this.visitorsAttending)
			.append("meetingLocation", this.meetingLocation)
			.toString();
	}
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(-1720909897, 187194383)
			.append(this.startTime)
			.append(this.endTime)
			.toHashCode();
	}
}
