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
package org.jasig.schedassist.portlet;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean to store relevant details of a canceled event.
 * 
 * @author Nicholas Blair
 * @version $ Id: EventCancellation.java $
 */
public class EventCancellation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5273561662086754782L;
	private Date startDate;
	private Date endDate;
	
	/**
	 * @param startDate
	 * @param endDate
	 */
	public EventCancellation(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EventCancellation [startDate=" + startDate + ", endDate="
				+ endDate + "]";
	}
	
	
}
