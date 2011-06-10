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

package org.jasig.schedassist.impl.reminder;

import java.util.Date;

import net.fortuna.ical4j.model.component.VEvent;

import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;

/**
 * {@link IReminder} implementation.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ReminderImpl.java $
 */
class ReminderImpl implements IReminder {

	private final long reminderId;
	private final IScheduleOwner scheduleOwner;
	private final ICalendarAccount recipient;
	private final Date sendTime;
	private final VEvent event;
	
	/**
	 * @param reminderId
	 * @param scheduleOwner
	 * @param recipient
	 * @param sendTime
	 * @param event
	 */
	ReminderImpl(long reminderId, IScheduleOwner scheduleOwner,
			ICalendarAccount recipient, Date sendTime, VEvent event) {
		this.reminderId = reminderId;
		this.scheduleOwner = scheduleOwner;
		this.recipient = recipient;
		this.sendTime = sendTime;
		this.event = event;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.IReminder#getReminderId()
	 */
	@Override
	public long getReminderId() {
		return this.reminderId;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.IReminder#getScheduleOwner()
	 */
	@Override
	public IScheduleOwner getScheduleOwner() {
		return this.scheduleOwner;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.IReminder#getRecipient()
	 */
	@Override
	public ICalendarAccount getRecipient() {
		return this.recipient;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.IReminder#getSendTime()
	 */
	@Override
	public Date getSendTime() {
		return this.sendTime;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.IReminder#getEvent()
	 */
	@Override
	public VEvent getEvent() {
		return this.event;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result
				+ ((recipient == null) ? 0 : recipient.hashCode());
		result = prime * result + (int) (reminderId ^ (reminderId >>> 32));
		result = prime * result
				+ ((scheduleOwner == null) ? 0 : scheduleOwner.hashCode());
		result = prime * result
				+ ((sendTime == null) ? 0 : sendTime.hashCode());
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
		if (!(obj instanceof ReminderImpl)) {
			return false;
		}
		ReminderImpl other = (ReminderImpl) obj;
		if (event == null) {
			if (other.event != null) {
				return false;
			}
		} else if (!event.equals(other.event)) {
			return false;
		}
		if (recipient == null) {
			if (other.recipient != null) {
				return false;
			}
		} else if (!recipient.equals(other.recipient)) {
			return false;
		}
		if (reminderId != other.reminderId) {
			return false;
		}
		if (scheduleOwner == null) {
			if (other.scheduleOwner != null) {
				return false;
			}
		} else if (!scheduleOwner.equals(other.scheduleOwner)) {
			return false;
		}
		if (sendTime == null) {
			if (other.sendTime != null) {
				return false;
			}
		} else if (!sendTime.equals(other.sendTime)) {
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
		builder.append("ReminderImpl [reminderId=");
		builder.append(reminderId);
		builder.append(", scheduleOwner=");
		builder.append(scheduleOwner);
		builder.append(", recipient=");
		builder.append(recipient);
		builder.append(", sendTime=");
		builder.append(sendTime);
		builder.append(", event=");
		builder.append(event);
		builder.append("]");
		return builder.toString();
	}

}
