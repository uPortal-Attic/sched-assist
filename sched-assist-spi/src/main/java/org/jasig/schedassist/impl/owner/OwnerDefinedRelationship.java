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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Bean that represents an owner defined ("adhoc") relationship to a visitor.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: OwnerDefinedRelationship.java 1131 2009-10-16 16:59:47Z npblair $
 */
public class OwnerDefinedRelationship {

	private String ownerUsername;
	private String visitorUsername;
	private String relationship;
	/**
	 * @return the ownerUsername
	 */
	public String getOwnerUsername() {
		return ownerUsername;
	}
	/**
	 * @param ownerUsername the ownerUsername to set
	 */
	public void setOwnerUsername(String ownerUsername) {
		this.ownerUsername = ownerUsername;
	}
	/**
	 * @return the visitorUsername
	 */
	public String getVisitorUsername() {
		return visitorUsername;
	}
	/**
	 * @param visitorUsername the visitorUsername to set
	 */
	public void setVisitorUsername(String visitorUsername) {
		this.visitorUsername = visitorUsername;
	}
	/**
	 * @return the relationship
	 */
	public String getRelationship() {
		return relationship;
	}
	/**
	 * @param relationship the relationship to set
	 */
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("relationship", this.relationship)
			.append("ownerUsername", this.ownerUsername)
			.append("visitorUsername", this.visitorUsername)
			.toString();
	}
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof OwnerDefinedRelationship)) {
			return false;
		}
		OwnerDefinedRelationship rhs = (OwnerDefinedRelationship) object;
		return new EqualsBuilder()
			.append(this.relationship, rhs.relationship)
			.append(this.visitorUsername, rhs.visitorUsername)
			.append(this.ownerUsername, rhs.ownerUsername)
			.isEquals();
	}
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(-380539001, -1577919779)
			.append(this.relationship)
			.append(this.visitorUsername)
			.append(this.ownerUsername)
			.toHashCode();
	}

}
