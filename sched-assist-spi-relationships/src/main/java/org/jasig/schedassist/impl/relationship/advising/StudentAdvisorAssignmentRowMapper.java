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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * {@link ParameterizedRowMapper} implementation for {@link StudentAdvisorAssignment}.
 * 
 * Expects the following column names:
 * 
 <ol>
 <li>advisor_emplid</li>
 <li>advisor_relationship</li>
 <li>student_emplid</li>
 <li>term_description</li>
 <li>term_number</li>
 <li>advisor_type</li>
 <li>committee_role</li>
 </ol>
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: StudentAdvisorAssignmentRowMapper.java 2905 2010-11-18 21:05:33Z npblair $
 */
public class StudentAdvisorAssignmentRowMapper implements RowMapper<StudentAdvisorAssignment> {

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java.sql.ResultSet, int)
	 */
	public StudentAdvisorAssignment mapRow(ResultSet rs, int rowNum) throws SQLException {
		StudentAdvisorAssignment record = new StudentAdvisorAssignment();
		record.setAdvisorEmplid(rs.getString("advisor_emplid"));
		record.setAdvisorRelationshipDescription(rs.getString("advisor_relationship"));
		record.setStudentEmplid(rs.getString("student_emplid"));
		record.setTermDescription(rs.getString("term_description"));
		record.setTermNumber(rs.getString("term_number"));
		record.setAdvisorType(rs.getString("advisor_type"));
		record.setCommitteeRole(rs.getString("committee_role"));
		return record;
	}

}
