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


package org.jasig.schedassist.web.owner.statistics;

import java.util.Date;

import org.jasig.schedassist.model.CommonDateOperations;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Form backing object for {@link VisitorHistoryFormController}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisitorHistoryFormBackingObject.java 2522 2010-09-10 16:06:13Z npblair $
 */
public class VisitorHistoryFormBackingObject {

	protected static final String DATE_FORMAT="MM/dd/yyyy";
	
	@DateTimeFormat(pattern=DATE_FORMAT)
	private Date startTime;
	@DateTimeFormat(pattern=DATE_FORMAT)
	private Date endTime;
	private String userSearchText;
	private String visitorUsername;
	
	/**
	 * @return the userSearchText
	 */
	public String getUserSearchText() {
		return userSearchText;
	}
	/**
	 * @param userSearchText the userSearchText to set
	 */
	public void setUserSearchText(String userSearchText) {
		this.userSearchText = userSearchText;
	}
	/**
	 * @return the visitorUsername
	 */
	public String getVisitorUsername() {
		return visitorUsername;
	}
	/**
	 * @param visitorUsername the visitorUsername to set
	 */
	public void setVisitorUsername(String visitorUsername) {
		this.visitorUsername = visitorUsername;
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
		this.startTime = startTime;
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
		this.endTime = endTime;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VisitorHistoryFormBackingObject [endTime=");
		builder.append(endTime);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", userSearchText=");
		builder.append(userSearchText);
		builder.append(", visitorUsername=");
		builder.append(visitorUsername);
		builder.append("]");
		return builder.toString();
	}
	/**
	 * This method will return the start time value truncated to the beginning of the day.
	 * 
	 * @return
	 */
	public Date getAdjustedStartTime() {
		return CommonDateOperations.beginningOfDay(this.startTime);
	}
	/**
	 * This method will return the end time value advanced to the end of the day.
	 * 
	 * @return
	 */
	public Date getAdjustedEndTime() {
		return CommonDateOperations.endOfDay(this.endTime);
	}
}
