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
 * Form backing object for {@link ClearWeekFormController} and {@link ClearEntireAvailableScheduleFormController}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ClearAvailableScheduleFormBackingObject.java 1713 2010-02-15 16:23:42Z npblair $
 */
public class ClearAvailableScheduleFormBackingObject {

	private boolean confirmedCancelAll = false;
	private boolean confirmedCancelWeek = false;
	private String weekOfPhrase;
	/**
	 * @return the confirmedCancelAll
	 */
	public boolean isConfirmedCancelAll() {
		return confirmedCancelAll;
	}
	/**
	 * @param confirmedCancelAll the confirmedCancelAll to set
	 */
	public void setConfirmedCancelAll(boolean confirmedCancelAll) {
		this.confirmedCancelAll = confirmedCancelAll;
	}
	/**
	 * @return the confirmedCancelWeek
	 */
	public boolean isConfirmedCancelWeek() {
		return confirmedCancelWeek;
	}
	/**
	 * @param confirmedCancelWeek the confirmedCancelWeek to set
	 */
	public void setConfirmedCancelWeek(boolean confirmedCancelWeek) {
		this.confirmedCancelWeek = confirmedCancelWeek;
	}
	/**
	 * @return the weekOfPhrase
	 */
	public String getWeekOfPhrase() {
		return weekOfPhrase;
	}
	/**
	 * @param weekOfPhrase the weekOfPhrase to set
	 */
	public void setWeekOfPhrase(String weekOfPhrase) {
		this.weekOfPhrase = weekOfPhrase;
	}

}
