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

import org.jasig.schedassist.model.AvailableBlock;

/**
 * Form backing object for {@link CancelAppointmentFormController}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CancelAppointmentFormBackingObject.java 1713 2010-02-15 16:23:42Z npblair $
 */
public class CancelAppointmentFormBackingObject {

	private final AvailableBlock targetBlock;
	
	private String reason;
	private boolean confirmCancel = false;
	
	/**
	 * 
	 * @param targetBlock
	 */
	public CancelAppointmentFormBackingObject(final AvailableBlock targetBlock) {
		this.targetBlock = targetBlock;
	}
	/**
	 * 
	 * @return true if the {@link AvailableBlock} that this instance targets has visitorLimit greater than 1
	 */
	public boolean isMultipleVisitors() {
		return this.targetBlock.getVisitorLimit() > 1;
	}
	/**
	 * @return the confirmCancel
	 */
	public boolean isConfirmCancel() {
		return confirmCancel;
	}
	/**
	 * @param confirmCancel the confirmCancel to set
	 */
	public void setConfirmCancel(boolean confirmCancel) {
		this.confirmCancel = confirmCancel;
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
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the targetBlock
	 */
	public AvailableBlock getTargetBlock() {
		return targetBlock;
	}
	
}
