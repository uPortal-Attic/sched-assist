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

package org.jasig.schedassist.impl.owner;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.model.AvailableBlock;

/**
 * Internal representation of {@link AvailableBlock} objects as they are persisted.
 * Unlike a {@link AvailableBlock}, there are fewer restrictions on construction.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PersistenceAvailableBlock.java 1711 2010-02-15 16:20:17Z npblair $
 */
class PersistenceAvailableBlock {

	private long ownerId;
	private Date startTime;
	private Date endTime;
	private int visitorLimit;
	private String meetingLocation;

	/**
	 * Default constructor.
	 */
	PersistenceAvailableBlock() { }
	
	/**
	 * 
	 * @param block
	 * @param ownerId
	 */
	PersistenceAvailableBlock(final AvailableBlock block, final long ownerId) {
		this.ownerId = ownerId;
		this.startTime = block.getStartTime();
		this.endTime = block.getEndTime();
		this.visitorLimit = block.getVisitorLimit();
		this.meetingLocation = block.getMeetingLocation();
	}
	
	
	/**
	 * @return the ownerId
	 */
	public long getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = DateUtils.truncate(endTime, Calendar.MINUTE);
	}
	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = DateUtils.truncate(startTime, Calendar.MINUTE);
	}
	/**
	 * @return the visitorLimit
	 */
	public int getVisitorLimit() {
		return visitorLimit;
	}
	/**
	 * @param visitorLimit the visitorLimit to set
	 */
	public void setVisitorLimit(int visitorLimit) {
		this.visitorLimit = visitorLimit;
	}

	/**
	 * @return the meetingLocation
	 */
	public String getMeetingLocation() {
		return meetingLocation;
	}

	/**
	 * @param meetingLocation the meetingLocation to set
	 */
	public void setMeetingLocation(String meetingLocation) {
		this.meetingLocation = meetingLocation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PersistenceAvailableBlock [ownerId=" + ownerId + ", startTime="
				+ startTime + ", endTime=" + endTime + ", visitorLimit="
				+ visitorLimit + ", meetingLocation=" + meetingLocation + "]";
	}
	
}
