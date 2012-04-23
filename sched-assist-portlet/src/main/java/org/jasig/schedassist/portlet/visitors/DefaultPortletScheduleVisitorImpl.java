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

package org.jasig.schedassist.portlet.visitors;

import org.jasig.schedassist.portlet.IPortletScheduleVisitor;

/**
 * Default implementation of {@link IScheduleVisitor}.
 * 
 * @author Nicholas Blair
 */
public class DefaultPortletScheduleVisitorImpl implements IPortletScheduleVisitor {

	private final String username;
	private boolean eligible = false;
	/**
	 * @param username
	 */
	public DefaultPortletScheduleVisitorImpl(String username) {
		this.username = username;
	}
	
	/**
	 * 
	 * @param username
	 * @param eligible
	 */
	public DefaultPortletScheduleVisitorImpl(String username, boolean eligible) {
		this(username);
		this.eligible = eligible;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.IPortletScheduleVisitor#getAccountId()
	 */
	@Override
	public String getAccountId() {
		return username;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.IPortletScheduleVisitor#isEligible()
	 */
	@Override
	public boolean isEligible() {
		return eligible;
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
		result = prime * result + (eligible ? 1231 : 1237);
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
		DefaultPortletScheduleVisitorImpl other = (DefaultPortletScheduleVisitorImpl) obj;
		if (eligible != other.eligible)
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
		return "DefaultPortletScheduleVisitorImpl [username=" + username
				+ ", eligible=" + eligible + "]";
	}

}
