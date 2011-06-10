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

package org.jasig.schedassist.model;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * A {@link PublicProfileId} is the combination of fields
 * that identify a {@link PublicProfile}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PublicProfileId.java 2124 2010-05-19 16:36:43Z npblair $
 */
public class PublicProfileId implements Comparable<PublicProfileId> {

	private String profileKey;
	private String ownerDisplayName;
	/**
	 * @return the profileKey
	 */
	public String getProfileKey() {
		return profileKey;
	}
	/**
	 * @param profileKey the profileKey to set
	 */
	public void setProfileKey(String profileKey) {
		this.profileKey = profileKey;
	}
	/**
	 * @return the ownerDisplayName
	 */
	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}
	/**
	 * @param ownerDisplayName the ownerDisplayName to set
	 */
	public void setOwnerDisplayName(String ownerDisplayName) {
		this.ownerDisplayName = ownerDisplayName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PublicProfileId o) {
		return new CompareToBuilder().append(this.ownerDisplayName, o.ownerDisplayName).toComparison();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((ownerDisplayName == null) ? 0 : ownerDisplayName.hashCode());
		result = prime * result
				+ ((profileKey == null) ? 0 : profileKey.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PublicProfileId)) {
			return false;
		}
		PublicProfileId other = (PublicProfileId) obj;
		if (ownerDisplayName == null) {
			if (other.ownerDisplayName != null) {
				return false;
			}
		} else if (!ownerDisplayName.equals(other.ownerDisplayName)) {
			return false;
		}
		if (profileKey == null) {
			if (other.profileKey != null) {
				return false;
			}
		} else if (!profileKey.equals(other.profileKey)) {
			return false;
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PublicProfileId [ownerDisplayName=");
		builder.append(ownerDisplayName);
		builder.append(", profileKey=");
		builder.append(profileKey);
		builder.append("]");
		return builder.toString();
	}
	
}
