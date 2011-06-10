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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test bench for {@link StudentAdvisorAssignmentRowMapper}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: StudentAdvisorAssignmentRowMapperTest.java 2905 2010-11-18 21:05:33Z npblair $
 */
public class StudentAdvisorAssignmentRowMapperTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSample1() throws Exception {
		ResultSet mock = createMock(ResultSet.class);
		expect(mock.getString("advisor_emplid")).andReturn("10000000001");
		expect(mock.getString("advisor_relationship")).andReturn("L&S");
		expect(mock.getString("student_emplid")).andReturn("00000000001");
		expect(mock.getString("term_description")).andReturn("Spring 2008-2009");
		expect(mock.getString("term_number")).andReturn("1094");
		expect(mock.getString("advisor_type")).andReturn("Academic");
		expect(mock.getString("committee_role")).andReturn(null);
		
		replay(mock);
		
		StudentAdvisorAssignmentRowMapper mapper = new StudentAdvisorAssignmentRowMapper();
		StudentAdvisorAssignment record = mapper.mapRow(mock, 1);
		Assert.assertEquals("10000000001", record.getAdvisorEmplid());
		Assert.assertEquals("L&S", record.getAdvisorRelationshipDescription());
		Assert.assertEquals("00000000001", record.getStudentEmplid());
		Assert.assertEquals("Spring 2008-2009", record.getTermDescription());
		Assert.assertEquals("1094", record.getTermNumber());
		Assert.assertEquals("Academic", record.getAdvisorType());
		Assert.assertNull(record.getCommitteeRole());
	}
	
	@Test
	public void testCareerCommitteeRole() throws Exception {
		ResultSet mock = createMock(ResultSet.class);
		expect(mock.getString("advisor_emplid")).andReturn("10000000001");
		expect(mock.getString("advisor_relationship")).andReturn("L&S");
		expect(mock.getString("student_emplid")).andReturn("00000000001");
		expect(mock.getString("term_description")).andReturn("Spring 2008-2009");
		expect(mock.getString("term_number")).andReturn("1094");
		expect(mock.getString("advisor_type")).andReturn("Academic");
		expect(mock.getString("committee_role")).andReturn("Career");
		
		replay(mock);
		
		StudentAdvisorAssignmentRowMapper mapper = new StudentAdvisorAssignmentRowMapper();
		StudentAdvisorAssignment record = mapper.mapRow(mock, 1);
		Assert.assertEquals("10000000001", record.getAdvisorEmplid());
		Assert.assertEquals("L&S", record.getAdvisorRelationshipDescription());
		Assert.assertEquals("00000000001", record.getStudentEmplid());
		Assert.assertEquals("Spring 2008-2009", record.getTermDescription());
		Assert.assertEquals("1094", record.getTermNumber());
		Assert.assertEquals("Academic", record.getAdvisorType());
		Assert.assertEquals("Career", record.getCommitteeRole());
	}
}
