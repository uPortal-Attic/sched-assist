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
 * Form backing object to represent an {@link AvailableBlock}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableBlockFormBackingObject.java 1937 2010-04-16 17:08:38Z npblair $
 */
public class AvailableBlockFormBackingObject {

	private String startTimePhrase;
	private String endTimePhrase;
	private int visitorLimit = 1;
	private boolean interactive = false;
	
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
	 * @return the visitorLimit
	 */
	public int getVisitorLimit() {
		return visitorLimit;
	}
	/**
	 * @param visitorLimit the visitorLimit to set
	 */
	public void setVisitorLimit(int visitorLimit) {
		this.visitorLimit = visitorLimit;
	}
	/**
	 * @return the interactive
	 */
	public boolean isInteractive() {
		return interactive;
	}
	/**
	 * @param interactive the interactive to set
	 */
	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}
	
}
