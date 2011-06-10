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

import java.util.Map;

/**
 * Abstract super type for {@link ICalendarAccount}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AbstractCalendarAccount.java 1898 2010-04-14 21:07:32Z npblair $
 */
public abstract class AbstractCalendarAccount implements ICalendarAccount {

	protected String calendarUniqueId;
	protected String emailAddress;
	protected String displayName;
	protected String username;
	protected boolean eligible;
	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getCalendarUniqueId()
	 */
	@Override
	public String getCalendarUniqueId() {
		return this.calendarUniqueId;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getEmailAddress()
	 */
	@Override
	public String getEmailAddress() {
		return this.emailAddress;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return this.displayName;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getUsername()
	 */
	@Override
	public String getUsername() {
		return this.username;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#isEligible()
	 */
	@Override
	public boolean isEligible() {
		return this.eligible;
	}
	/**
	 * @param calendarUniqueId the calendarUniqueId to set
	 */
	public void setCalendarUniqueId(String calendarUniqueId) {
		this.calendarUniqueId = calendarUniqueId;
	}
	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	/**
	 * @param displayName the name to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @param eligible the eligible to set
	 */
	public void setEligible(boolean eligible) {
		this.eligible = eligible;
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
				+ ((calendarUniqueId == null) ? 0 : calendarUniqueId.hashCode());
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + (eligible ? 1231 : 1237);
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractCalendarAccount other = (AbstractCalendarAccount) obj;
		if (calendarUniqueId == null) {
			if (other.calendarUniqueId != null)
				return false;
		} else if (!calendarUniqueId.equals(other.calendarUniqueId))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (eligible != other.eligible)
			return false;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractCalendarAccount [calendarUniqueId=");
		builder.append(calendarUniqueId);
		builder.append(", displayName=");
		builder.append(displayName);
		builder.append(", eligible=");
		builder.append(eligible);
		builder.append(", emailAddress=");
		builder.append(emailAddress);
		builder.append(", username=");
		builder.append(username);
		builder.append("]");
		return builder.toString();
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getAttributeValue(java.lang.String)
	 */
	@Override
	public abstract String getAttributeValue(String attributeName);

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getAttributes()
	 */
	@Override
	public abstract Map<String, String> getAttributes();

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.model.ICalendarAccount#getCalendarLoginId()
	 */
	@Override
	public abstract String getCalendarLoginId();
	
}
