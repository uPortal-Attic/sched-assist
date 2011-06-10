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

/*******************************************************************************
*  Copyright 2009 The Board of Regents of the University of Wisconsin System.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*******************************************************************************/
package org.jasig.schedassist.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Bean to represent an association between
 * an {@link IScheduleOwner} and an {@link IScheduleVisitor}
 * along with a description.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: Relationship.java 2335 2010-08-06 19:16:06Z npblair $
 */
public class Relationship implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6249594180199804748L;
	private IScheduleOwner owner;
	private IScheduleVisitor visitor;
	private String description;
	
	/**
	 * @return the owner
	 */
	public IScheduleOwner getOwner() {
		return owner;
	}
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(IScheduleOwner owner) {
		this.owner = owner;
	}
	/**
	 * @return the visitor
	 */
	public IScheduleVisitor getVisitor() {
		return visitor;
	}
	/**
	 * @param visitor the visitor to set
	 */
	public void setVisitor(IScheduleVisitor visitor) {
		this.visitor = visitor;
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
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("owner", this.owner)
			.append("visitor", this.visitor)
			.append("description", this.description)
			.toString();	
	}
	/**
	 * This implementation ignores the "description" field.
	 * 
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof Relationship)) {
			return false;
		}
		Relationship rhs = (Relationship) object;
		return new EqualsBuilder()
				.append(this.owner, rhs.owner)
				.append(this.visitor, rhs.visitor)
				.isEquals();
	}
	
	/**
	 * Non-standard equals method includes 
	 * the description field.
	 * 
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equalsIncludeDescription(Object object) {
		if (!(object instanceof Relationship)) {
			return false;
		}
		Relationship rhs = (Relationship) object;
		return new EqualsBuilder()
				.append(this.owner, rhs.owner)
				.append(this.visitor, rhs.visitor)
				.append(this.description, rhs.description)
				.isEquals();
	}
	/**
	 * Implementation ignores the "description" field.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(199295793, 2105435121)
			.append(this.owner)
			.append(this.visitor)
			.toHashCode();
	}
	
	
}
