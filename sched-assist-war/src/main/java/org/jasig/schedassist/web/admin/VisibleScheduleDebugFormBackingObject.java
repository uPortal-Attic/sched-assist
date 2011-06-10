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


package org.jasig.schedassist.web.admin;

import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;

/**
 * Form backing object for {@link VisibleScheduleDebugFormController}; made up
 * of 2 {@link AccountLookupFormBackingObject}s, 1 for the owner, and 1 for the visitor.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleScheduleDebugFormBackingObject.java 2978 2011-01-25 19:20:51Z npblair $
 */
public class VisibleScheduleDebugFormBackingObject {

	private AccountLookupFormBackingObject visitorLookup = new AccountLookupFormBackingObject();
	private AccountLookupFormBackingObject ownerLookup = new AccountLookupFormBackingObject();
	private IScheduleOwner scheduleOwner;
	private IScheduleVisitor scheduleVisitor;
	/**
	 * @return the visitorLookup
	 */
	public AccountLookupFormBackingObject getVisitorLookup() {
		return visitorLookup;
	}
	/**
	 * @param visitorLookup the visitorLookup to set
	 */
	public void setVisitorLookup(AccountLookupFormBackingObject visitorLookup) {
		this.visitorLookup = visitorLookup;
	}
	/**
	 * @return the ownerLookup
	 */
	public AccountLookupFormBackingObject getOwnerLookup() {
		return ownerLookup;
	}
	/**
	 * @param ownerLookup the ownerLookup to set
	 */
	public void setOwnerLookup(AccountLookupFormBackingObject ownerLookup) {
		this.ownerLookup = ownerLookup;
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
	 * @return the scheduleVisitor
	 */
	public IScheduleVisitor getScheduleVisitor() {
		return scheduleVisitor;
	}
	/**
	 * @param scheduleVisitor the scheduleVisitor to set
	 */
	public void setScheduleVisitor(IScheduleVisitor scheduleVisitor) {
		this.scheduleVisitor = scheduleVisitor;
	}
	
	
}
