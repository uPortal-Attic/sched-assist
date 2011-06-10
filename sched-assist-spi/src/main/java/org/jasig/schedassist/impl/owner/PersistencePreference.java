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

package org.jasig.schedassist.impl.owner;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Java bean to represent a {@link IScheduleOwner}'s stored
 * "preference."
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PersistencePreference.java 1920 2010-04-14 21:20:15Z npblair $
 */
class PersistencePreference {

	private String preferenceKey;
	private String preferenceValue;
	private int ownerId;
	/**
	 * @return the preferenceKey
	 */
	public String getPreferenceKey() {
		return preferenceKey;
	}
	/**
	 * @param preferenceKey the preferenceKey to set
	 */
	public void setPreferenceKey(String preferenceKey) {
		this.preferenceKey = preferenceKey;
	}
	/**
	 * @return the preferenceValue
	 */
	public String getPreferenceValue() {
		return preferenceValue;
	}
	/**
	 * @param preferenceValue the preferenceValue to set
	 */
	public void setPreferenceValue(String preferenceValue) {
		this.preferenceValue = preferenceValue;
	}
	/**
	 * @return the ownerId
	 */
	public int getOwnerId() {
		return ownerId;
	}
	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("ownerId", ownerId);
		builder.append("preferenceKey", preferenceKey);
		builder.append("preferenceValue", preferenceValue);
		return builder.toString();
	}
	
}
