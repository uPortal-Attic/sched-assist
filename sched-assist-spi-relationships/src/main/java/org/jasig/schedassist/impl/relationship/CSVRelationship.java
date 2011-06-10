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

package org.jasig.schedassist.impl.relationship;

/**
 * Simple java bean to represent a row in the CSV relationship data source.
 * 
 * @author Nicholas Blair
 * @version $Id: CSVRelationship.java 147 2011-06-10 15:03:02Z npblair $
 */
public class CSVRelationship {

	private String visitorIdentifier;
	private String ownerIdentifier;
	private String relationshipDescription;
	/**
	 * @return the visitorIdentifier
	 */
	public String getVisitorIdentifier() {
		return visitorIdentifier;
	}
	/**
	 * @param visitorIdentifier the visitorIdentifier to set
	 */
	public void setVisitorIdentifier(String visitorIdentifier) {
		this.visitorIdentifier = visitorIdentifier;
	}
	/**
	 * @return the ownerIdentifier
	 */
	public String getOwnerIdentifier() {
		return ownerIdentifier;
	}
	/**
	 * @param ownerIdentifier the ownerIdentifier to set
	 */
	public void setOwnerIdentifier(String ownerIdentifier) {
		this.ownerIdentifier = ownerIdentifier;
	}
	/**
	 * @return the relationshipDescription
	 */
	public String getRelationshipDescription() {
		return relationshipDescription;
	}
	/**
	 * @param relationshipDescription the relationshipDescription to set
	 */
	public void setRelationshipDescription(String relationshipDescription) {
		this.relationshipDescription = relationshipDescription;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CSVRelationship [visitorIdentifier="
				+ visitorIdentifier + ", ownerIdentifier=" + ownerIdentifier
				+ ", relationshipDescription=" + relationshipDescription + "]";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ownerIdentifier == null) ? 0 : ownerIdentifier.hashCode());
		result = prime
				* result
				+ ((relationshipDescription == null) ? 0
						: relationshipDescription.hashCode());
		result = prime
				* result
				+ ((visitorIdentifier == null) ? 0 : visitorIdentifier
						.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CSVRelationship other = (CSVRelationship) obj;
		if (ownerIdentifier == null) {
			if (other.ownerIdentifier != null)
				return false;
		} else if (!ownerIdentifier.equals(other.ownerIdentifier))
			return false;
		if (relationshipDescription == null) {
			if (other.relationshipDescription != null)
				return false;
		} else if (!relationshipDescription
				.equals(other.relationshipDescription))
			return false;
		if (visitorIdentifier == null) {
			if (other.visitorIdentifier != null)
				return false;
		} else if (!visitorIdentifier.equals(other.visitorIdentifier))
			return false;
		return true;
	}
	
	
}
