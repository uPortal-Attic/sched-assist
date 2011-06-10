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

package org.jasig.schedassist.impl.relationship.advising;


/**
 * Java bean to represent the record for a student and their assigned advisor.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: StudentAdvisorAssignment.java 2905 2010-11-18 21:05:33Z npblair $
 */
public class StudentAdvisorAssignment {

	private String studentEmplid;
	private String advisorEmplid;
	private String termNumber;
	private String termDescription;
	private String advisorRelationshipDescription;
	private String advisorType;
	private String committeeRole;
	
	/**
	 * @return the studentEmplid
	 */
	public String getStudentEmplid() {
		return studentEmplid;
	}
	/**
	 * @param studentEmplid the studentEmplid to set
	 */
	public void setStudentEmplid(String studentEmplid) {
		this.studentEmplid = studentEmplid;
	}
	/**
	 * @return the advisorEmplid
	 */
	public String getAdvisorEmplid() {
		return advisorEmplid;
	}
	/**
	 * @param advisorEmplid the advisorEmplid to set
	 */
	public void setAdvisorEmplid(String advisorEmplid) {
		this.advisorEmplid = advisorEmplid;
	}
	/**
	 * @return the termNumber
	 */
	public String getTermNumber() {
		return termNumber;
	}
	/**
	 * @param termNumber the termNumber to set
	 */
	public void setTermNumber(String termNumber) {
		this.termNumber = termNumber;
	}
	/**
	 * @return the termDescription
	 */
	public String getTermDescription() {
		return termDescription;
	}
	/**
	 * @param termDescription the termDescription to set
	 */
	public void setTermDescription(String termDescription) {
		this.termDescription = termDescription;
	}
	/**
	 * @return the advisorRelationshipDescription
	 */
	public String getAdvisorRelationshipDescription() {
		return advisorRelationshipDescription;
	}
	/**
	 * @param advisorRelationshipDescription the advisorRelationshipDescription to set
	 */
	public void setAdvisorRelationshipDescription(
			String advisorRelationshipDescription) {
		this.advisorRelationshipDescription = advisorRelationshipDescription;
	}
	/**
	 * @return the advisorType
	 */
	public String getAdvisorType() {
		return advisorType;
	}
	/**
	 * @param advisorType the advisorType to set
	 */
	public void setAdvisorType(String advisorType) {
		this.advisorType = advisorType;
	}
	/**
	 * @return the committeeRole
	 */
	public String getCommitteeRole() {
		return committeeRole;
	}
	/**
	 * @param committeeRole the committeeRole to set
	 */
	public void setCommitteeRole(String committeeRole) {
		this.committeeRole = committeeRole;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StudentAdvisorAssignment [studentEmplid=");
		builder.append(studentEmplid);
		builder.append(", advisorEmplid=");
		builder.append(advisorEmplid);
		builder.append(", termNumber=");
		builder.append(termNumber);
		builder.append(", termDescription=");
		builder.append(termDescription);
		builder.append(", advisorRelationshipDescription=");
		builder.append(advisorRelationshipDescription);
		builder.append(", advisorType=");
		builder.append(advisorType);
		builder.append(", committeeRole=");
		builder.append(committeeRole);
		builder.append("]");
		return builder.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((advisorEmplid == null) ? 0 : advisorEmplid.hashCode());
		result = prime
				* result
				+ ((advisorRelationshipDescription == null) ? 0
						: advisorRelationshipDescription.hashCode());
		result = prime * result
				+ ((advisorType == null) ? 0 : advisorType.hashCode());
		result = prime * result
				+ ((committeeRole == null) ? 0 : committeeRole.hashCode());
		result = prime * result
				+ ((studentEmplid == null) ? 0 : studentEmplid.hashCode());
		result = prime * result
				+ ((termDescription == null) ? 0 : termDescription.hashCode());
		result = prime * result
				+ ((termNumber == null) ? 0 : termNumber.hashCode());
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
		if (!(obj instanceof StudentAdvisorAssignment)) {
			return false;
		}
		StudentAdvisorAssignment other = (StudentAdvisorAssignment) obj;
		if (advisorEmplid == null) {
			if (other.advisorEmplid != null) {
				return false;
			}
		} else if (!advisorEmplid.equals(other.advisorEmplid)) {
			return false;
		}
		if (advisorRelationshipDescription == null) {
			if (other.advisorRelationshipDescription != null) {
				return false;
			}
		} else if (!advisorRelationshipDescription
				.equals(other.advisorRelationshipDescription)) {
			return false;
		}
		if (advisorType == null) {
			if (other.advisorType != null) {
				return false;
			}
		} else if (!advisorType.equals(other.advisorType)) {
			return false;
		}
		if (committeeRole == null) {
			if (other.committeeRole != null) {
				return false;
			}
		} else if (!committeeRole.equals(other.committeeRole)) {
			return false;
		}
		if (studentEmplid == null) {
			if (other.studentEmplid != null) {
				return false;
			}
		} else if (!studentEmplid.equals(other.studentEmplid)) {
			return false;
		}
		if (termDescription == null) {
			if (other.termDescription != null) {
				return false;
			}
		} else if (!termDescription.equals(other.termDescription)) {
			return false;
		}
		if (termNumber == null) {
			if (other.termNumber != null) {
				return false;
			}
		} else if (!termNumber.equals(other.termNumber)) {
			return false;
		}
		return true;
	}
	
}
