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


package org.jasig.schedassist.web.owner.schedule;

/**
 * Form backing object for the {@link BlockBuilderFormController}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: BlockBuilderFormBackingObject.java 1713 2010-02-15 16:23:42Z npblair $
 */
public class BlockBuilderFormBackingObject {

	private String startTimePhrase;
	private String endTimePhrase;
	private String daysOfWeekPhrase;
	private String startDatePhrase;
	private String endDatePhrase;
	private int visitorsPerAppointment;
	private String meetingLocation;
	
	/**
	 * @return the daysOfWeekPhrase
	 */
	public String getDaysOfWeekPhrase() {
		return daysOfWeekPhrase;
	}
	/**
	 * @param daysOfWeekPhrase the daysOfWeekPhrase to set
	 */
	public void setDaysOfWeekPhrase(String daysOfWeekPhrase) {
		this.daysOfWeekPhrase = daysOfWeekPhrase;
	}
	/**
	 * @return the endDatePhrase
	 */
	public String getEndDatePhrase() {
		return endDatePhrase;
	}
	/**
	 * @param endDatePhrase the endDatePhrase to set
	 */
	public void setEndDatePhrase(String endDatePhrase) {
		this.endDatePhrase = endDatePhrase;
	}
	/**
	 * @return the endTimePhrase
	 */
	public String getEndTimePhrase() {
		return endTimePhrase;
	}
	/**
	 * @param endTimePhrase the endTimePhrase to set
	 */
	public void setEndTimePhrase(String endTimePhrase) {
		this.endTimePhrase = endTimePhrase;
	}
	/**
	 * @return the startDatePhrase
	 */
	public String getStartDatePhrase() {
		return startDatePhrase;
	}
	/**
	 * @param startDatePhrase the startDatePhrase to set
	 */
	public void setStartDatePhrase(String startDatePhrase) {
		this.startDatePhrase = startDatePhrase;
	}
	/**
	 * @return the startTimePhrase
	 */
	public String getStartTimePhrase() {
		return startTimePhrase;
	}
	/**
	 * @param startTimePhrase the startTimePhrase to set
	 */
	public void setStartTimePhrase(String startTimePhrase) {
		this.startTimePhrase = startTimePhrase;
	}
	/**
	 * @return the visitorsPerAppointment
	 */
	public int getVisitorsPerAppointment() {
		return visitorsPerAppointment;
	}
	/**
	 * @param visitorsPerAppointment the visitorsPerAppointment to set
	 */
	public void setVisitorsPerAppointment(int visitorsPerAppointment) {
		this.visitorsPerAppointment = visitorsPerAppointment;
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
		StringBuilder builder = new StringBuilder();
		builder.append("BlockBuilderFormBackingObject [startTimePhrase=");
		builder.append(startTimePhrase);
		builder.append(", endTimePhrase=");
		builder.append(endTimePhrase);
		builder.append(", daysOfWeekPhrase=");
		builder.append(daysOfWeekPhrase);
		builder.append(", startDatePhrase=");
		builder.append(startDatePhrase);
		builder.append(", endDatePhrase=");
		builder.append(endDatePhrase);
		builder.append(", visitorsPerAppointment=");
		builder.append(visitorsPerAppointment);
		builder.append(", meetingLocation=");
		builder.append(meetingLocation);
		builder.append("]");
		return builder.toString();
	}
	
}
