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


package org.jasig.schedassist.web.visitor;

import java.util.ArrayList;
import java.util.List;

import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.MeetingDurations;

/**
 * Form backing object for {@link CreateAppointmentFormController}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CreateAppointmentFormBackingObject.java 1713 2010-02-15 16:23:42Z npblair $
 */
public class CreateAppointmentFormBackingObject {

	private final AvailableBlock targetBlock;
	private final MeetingDurations meetingDurations;
	
	// these fields available when visitorLimit of block == 1
	private String reason;
	private int selectedDuration;
	
	// this field available when visitorLimit of block > 1
	private boolean confirmJoin = false;
	
	/**
	 * 
	 * @param targetBlock
	 */
	public CreateAppointmentFormBackingObject(AvailableBlock targetBlock, MeetingDurations meetingDurations) {
		this.targetBlock = targetBlock;
		this.meetingDurations = meetingDurations;
		this.selectedDuration = meetingDurations.getMinLength();
	}
	/**
	 * @return the targetBlock
	 */
	public AvailableBlock getTargetBlock() {
		return targetBlock;
	}
	/**
	 * @return the meetingDurations
	 */
	public MeetingDurations getMeetingDurations() {
		return meetingDurations;
	}
	
	/**
	 * @return the multipleVisitors
	 */
	public boolean isMultipleVisitors() {
		return this.targetBlock.getVisitorLimit() > 1;
	}
	
	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	/**
	 * @param reason the reason to set
	 */
	public void setReason(final String reason) {
		this.reason = reason;
	}
	
	/**
	 * @return whether or not "double length" meeting duration is available
	 */
	public boolean isDoubleLengthAvailable() {
		return !isMultipleVisitors() && meetingDurations.isDoubleLength();
	}
	
	/**
	 * A {@link List} of {@link Integer}s containing the acceptable
	 * values for selectedDuration.
	 * If the internal {@link MeetingDurations} "isDoubleLengthAvailable,"
	 * the returned {@link List} will contain 1 entry for the minValue and 1 entry
	 * for the maxValue.
	 * 
	 * Otherwise, the returned list will only contain 1 (the minValue).
	 * 
	 * @return the meetingDurations
	 */
	public List<Integer> getMeetingDurationsAsList() {
		List<Integer> result = new ArrayList<Integer>();
		result.add(meetingDurations.getMinLength());
		if(meetingDurations.isDoubleLength()) {
			result.add(meetingDurations.getMaxLength());
		}
		return result;
	}
	
	/**
	 * @return the selectedDuration
	 */
	public int getSelectedDuration() {
		return selectedDuration;
	}
	/**
	 * @param selectedDuration the selectedDuration to set
	 */
	public void setSelectedDuration(int selectedDuration) {
		this.selectedDuration = selectedDuration;
	}
	/**
	 * @return the confirmJoin
	 */
	public boolean isConfirmJoin() {
		return confirmJoin;
	}
	/**
	 * @param confirmJoin the confirmJoin to set
	 */
	public void setConfirmJoin(boolean confirmJoin) {
		this.confirmJoin = confirmJoin;
	}
	
}
