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
import org.jasig.schedassist.impl.EventType;
import org.jasig.schedassist.model.IScheduleOwner;

/**
 * Bean to store the details surrounding an appointment event.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AppointmentEvent.java 2758 2010-10-05 16:49:20Z npblair $
 */
public class AppointmentEvent implements Comparable<AppointmentEvent> {

	private long eventId;
	private long ownerId;
	private IScheduleOwner scheduleOwner;
	private String visitorId;
	
	private Date eventTimestamp;
	private EventType eventType;
	private Date appointmentStartTime;
	/**
	 * @return the eventId
	 */
	public long getEventId() {
		return eventId;
	}
	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(long eventId) {
		this.eventId = eventId;
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
	 * @return the scheduleOwner
	 */
	public IScheduleOwner getScheduleOwner() {
		return scheduleOwner;
	}
	/**
	 * @param scheduleOwner the scheduleOwner to set
	 */
	public void setScheduleOwner(IScheduleOwner scheduleOwner) {
		this.scheduleOwner = scheduleOwner;
	}
	/**
	 * @return the visitorId
	 */
	public String getVisitorId() {
		return visitorId;
	}
	/**
	 * @param visitorId the visitorId to set
	 */
	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}
	
	/**
	 * @return the eventTimestamp
	 */
	public Date getEventTimestamp() {
		return eventTimestamp;
	}
	/**
	 * @param eventTimestamp the eventTimestamp to set
	 */
	public void setEventTimestamp(Date eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}
	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}
	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	/**
	 * @return the appointmentStartTime
	 */
	public Date getAppointmentStartTime() {
		return appointmentStartTime;
	}
	/**
	 * @param appointmentStartTime the appointmentStartTime to set
	 */
	public void setAppointmentStartTime(Date appointmentStartTime) {
		this.appointmentStartTime = appointmentStartTime;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((appointmentStartTime == null) ? 0 : appointmentStartTime
						.hashCode());
		result = prime * result + (int) (eventId ^ (eventId >>> 32));
		result = prime * result
				+ ((eventTimestamp == null) ? 0 : eventTimestamp.hashCode());
		result = prime * result
				+ ((eventType == null) ? 0 : eventType.hashCode());
		result = prime * result + (int) (ownerId ^ (ownerId >>> 32));
		result = prime * result
				+ ((scheduleOwner == null) ? 0 : scheduleOwner.hashCode());
		result = prime * result
				+ ((visitorId == null) ? 0 : visitorId.hashCode());
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
		if (!(obj instanceof AppointmentEvent)) {
			return false;
		}
		AppointmentEvent other = (AppointmentEvent) obj;
		if (appointmentStartTime == null) {
			if (other.appointmentStartTime != null) {
				return false;
			}
		} else if (!appointmentStartTime.equals(other.appointmentStartTime)) {
			return false;
		}
		if (eventId != other.eventId) {
			return false;
		}
		if (eventTimestamp == null) {
			if (other.eventTimestamp != null) {
				return false;
			}
		} else if (!eventTimestamp.equals(other.eventTimestamp)) {
			return false;
		}
		if (eventType == null) {
			if (other.eventType != null) {
				return false;
			}
		} else if (!eventType.equals(other.eventType)) {
			return false;
		}
		if (ownerId != other.ownerId) {
			return false;
		}
		if (scheduleOwner == null) {
			if (other.scheduleOwner != null) {
				return false;
			}
		} else if (!scheduleOwner.equals(other.scheduleOwner)) {
			return false;
		}
		if (visitorId == null) {
			if (other.visitorId != null) {
				return false;
			}
		} else if (!visitorId.equals(other.visitorId)) {
			return false;
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AppointmentEvent [appointmentStartTime=");
		builder.append(appointmentStartTime);
		builder.append(", eventId=");
		builder.append(eventId);
		builder.append(", eventTimestamp=");
		builder.append(eventTimestamp);
		builder.append(", eventType=");
		builder.append(eventType);
		builder.append(", ownerId=");
		builder.append(ownerId);
		builder.append(", scheduleOwner=");
		builder.append(scheduleOwner);
		builder.append(", visitorId=");
		builder.append(visitorId);
		builder.append("]");
		return builder.toString();
	}
	/**
	 * Uses only eventTimestamp and eventId fields, in that order
	 * of precedence.
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AppointmentEvent o) {
		return new CompareToBuilder()
			.append(this.eventTimestamp, o.eventTimestamp)
			.append(this.eventId, o.eventId)
			.toComparison();
	}
	
}
