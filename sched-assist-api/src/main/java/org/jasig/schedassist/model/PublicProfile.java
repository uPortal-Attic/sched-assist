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


/**
 * The bean represents a public profile for an {@link IScheduleOwner}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PublicProfile.java 3049 2011-02-04 15:55:05Z npblair $
 */
public class PublicProfile {

	private long ownerId;
	private PublicProfileId publicProfileId;
	private String description;
	private String ownerNoteboard;
	
	/**
	 * @return the ownerId
	 */
	public long getOwnerId() {
		return ownerId;
	}
	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	/**
	 * @return the publicProfileId
	 */
	public PublicProfileId getPublicProfileId() {
		return publicProfileId;
	}
	/**
	 * @param publicProfileId the publicProfileId to set
	 */
	public void setPublicProfileId(PublicProfileId publicProfileId) {
		this.publicProfileId = publicProfileId;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the ownerNoteboard
	 */
	public String getOwnerNoteboard() {
		return ownerNoteboard;
	}
	/**
	 * Utility method to return the owner's noteboard as
	 * an array of "sentences" by splitting the noteboard value
	 * on newline characters.
	 * @return the ownerNoteboard preference as an array of {@link String}s
	 */
	public String[] getOwnerNoteboardSentences() {
		String [] noteboardSentences = ownerNoteboard.split("\n");
		return noteboardSentences;
	}
	/**
	 * @param ownerNoteboard the ownerNoteboard to set
	 */
	public void setOwnerNoteboard(String ownerNoteboard) {
		this.ownerNoteboard = ownerNoteboard;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((ownerNoteboard == null) ? 0 : ownerNoteboard.hashCode());
		result = prime * result
				+ ((publicProfileId == null) ? 0 : publicProfileId.hashCode());
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
		if (!(obj instanceof PublicProfile)) {
			return false;
		}
		PublicProfile other = (PublicProfile) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (ownerNoteboard == null) {
			if (other.ownerNoteboard != null) {
				return false;
			}
		} else if (!ownerNoteboard.equals(other.ownerNoteboard)) {
			return false;
		}
		if (publicProfileId == null) {
			if (other.publicProfileId != null) {
				return false;
			}
		} else if (!publicProfileId.equals(other.publicProfileId)) {
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
		builder.append("PublicProfile [description=");
		builder.append(description);
		builder.append(", ownerNoteboard=");
		builder.append(ownerNoteboard);
		builder.append(", publicProfileId=");
		builder.append(publicProfileId);
		builder.append("]");
		return builder.toString();
	}
	
}
