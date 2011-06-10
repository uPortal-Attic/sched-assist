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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link StudentAdvisorRelationshipDaoImpl}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: StudentAdvisorRelationshipDaoImplTest.java $
 */
public class StudentAdvisorRelationshipDaoImplTest {

	@Test
	public void testBuildRelationshipDescriptionControl() {
		StudentAdvisorAssignment assignment = new StudentAdvisorAssignment();
		assignment.setAdvisorRelationshipDescription("Business Undergraduate");
		assignment.setAdvisorType("Academic");
		assignment.setTermDescription("Fall 2010-2011");
		
		StudentAdvisorRelationshipDaoImpl impl = new StudentAdvisorRelationshipDaoImpl();
		String relationshipDescription = impl.buildDescription(assignment);
		Assert.assertEquals("Academic Advisor, Business Undergraduate, Fall 2010-2011", relationshipDescription);
	}
	
	@Test
	public void testBuildRelationshipDescriptionCommitteeRoleCareer() {
		StudentAdvisorAssignment assignment = new StudentAdvisorAssignment();
		assignment.setAdvisorRelationshipDescription("Business Undergraduate");
		assignment.setAdvisorType("Academic");
		assignment.setTermDescription("Fall 2010-2011");
		assignment.setCommitteeRole("Career");
		
		StudentAdvisorRelationshipDaoImpl impl = new StudentAdvisorRelationshipDaoImpl();
		String relationshipDescription = impl.buildDescription(assignment);
		Assert.assertEquals("Career Academic Advisor, Business Undergraduate, Fall 2010-2011", relationshipDescription);
	}
	
	@Test
	public void testBuildRelationshipDescriptionCommitteeRoleAcademic() {
		StudentAdvisorAssignment assignment = new StudentAdvisorAssignment();
		assignment.setAdvisorRelationshipDescription("Business Undergraduate");
		assignment.setAdvisorType("Academic");
		assignment.setTermDescription("Fall 2010-2011");
		assignment.setCommitteeRole("Academic");
		
		StudentAdvisorRelationshipDaoImpl impl = new StudentAdvisorRelationshipDaoImpl();
		String relationshipDescription = impl.buildDescription(assignment);
		Assert.assertEquals("Academic Advisor, Business Undergraduate, Fall 2010-2011", relationshipDescription);
	}
	
	@Test
	public void testBuildRelationshipDescriptionCommitteeRoleAcademicDifferentAdvisorType() {
		StudentAdvisorAssignment assignment = new StudentAdvisorAssignment();
		assignment.setAdvisorRelationshipDescription("General Course BS Degree");
		assignment.setAdvisorType("Honors - Letters & Science");
		assignment.setTermDescription("Fall 2010-2011");
		assignment.setCommitteeRole("Academic");
		
		StudentAdvisorRelationshipDaoImpl impl = new StudentAdvisorRelationshipDaoImpl();
		String relationshipDescription = impl.buildDescription(assignment);
		Assert.assertEquals("Academic Honors - Letters & Science Advisor, General Course BS Degree, Fall 2010-2011", relationshipDescription);
	}
}
