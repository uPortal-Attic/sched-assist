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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Test bench for {@link AdvisorListDataSourceImpl}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AdvisorListDataSourceImplTest.java 2905 2010-11-18 21:05:33Z npblair $
 */
public class AdvisorListRelationshipDataSourceImplTest {

	/**
	 * Test parseLine function with a number of different inputs.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParseLine() throws Exception {
		AdvisorListRelationshipDataSourceImpl dataSource = new AdvisorListRelationshipDataSourceImpl();
		String example1 = "notokens";
		StudentAdvisorAssignment record = dataSource.parseLine(example1);
		Assert.assertNull(record);
		
		String example2 = "with;semi;colons";
		record = dataSource.parseLine(example2);
		Assert.assertNull(record);
		
		String example3 = "student1@wisc.edu;One,Test Student;0000000001;9010000001;Graduate;L&S;College of Letters and Science;4.000;1092;Fall 2008-2009;87.000;PHD 922L&S;;NWD;G922L;Sociology - LS;PHD 922L&S;Sociology PHD-L&S;Sociology;advisor1@wisc.edu;One,Advisor;1000000001;9020000001;ADVR;Academic";
		record = dataSource.parseLine(example3);
		Assert.assertNotNull(record);
		Assert.assertEquals("1000000001", record.getAdvisorEmplid());
		Assert.assertEquals("Sociology - LS", record.getAdvisorRelationshipDescription());
		Assert.assertEquals("0000000001", record.getStudentEmplid());
		Assert.assertEquals("1092", record.getTermNumber());
		Assert.assertEquals("Fall 2008-2009", record.getTermDescription());
		Assert.assertEquals("Academic", record.getAdvisorType());
	}
	
	/**
	 * Test parseLine function using an alternate field number.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParseLineAlternateFieldNumber() throws Exception {
		AdvisorListRelationshipDataSourceImpl dataSource = new AdvisorListRelationshipDataSourceImpl();
		dataSource.setAdvisorEmplidFieldNumber(19);
		String example1 = "notokens";
		StudentAdvisorAssignment record = dataSource.parseLine(example1);
		Assert.assertNull(record);
		
		String example2 = "with;semi;colons";
		record = dataSource.parseLine(example2);
		Assert.assertNull(record);
		
		String example3 = "student1@wisc.edu;One,Test Student;0000000001;9010000001;Graduate;L&S;College of Letters and Science;4.000;1092;Fall 2008-2009;87.000;PHD 922L&S;;NWD;G922L;Sociology - LS;PHD 922L&S;Sociology PHD-L&S;Sociology;advisor1@wisc.edu;One,Advisor;1000000001;9020000001;ADVR;Academic";
		record = dataSource.parseLine(example3);
		Assert.assertNotNull(record);
		// field number 19 is email instead of emplid
		Assert.assertEquals("advisor1@wisc.edu", record.getAdvisorEmplid());
		Assert.assertEquals("Sociology - LS", record.getAdvisorRelationshipDescription());
		Assert.assertEquals("0000000001", record.getStudentEmplid());
		Assert.assertEquals("1092", record.getTermNumber());
		Assert.assertEquals("Fall 2008-2009", record.getTermDescription());
		Assert.assertEquals("Academic", record.getAdvisorType());
	}
	
	/**
	 * Test parseLine function with a number of different inputs.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testParseLineAddCommitteeRole() throws Exception {
		AdvisorListRelationshipDataSourceImpl dataSource = new AdvisorListRelationshipDataSourceImpl();
		String example1 = "notokens";
		StudentAdvisorAssignment record = dataSource.parseLine(example1);
		Assert.assertNull(record);
		
		String example2 = "with;semi;colons";
		record = dataSource.parseLine(example2);
		Assert.assertNull(record);
		
		String example3 = "student1@wisc.edu;One,Test Student;0000000001;9010000001;Graduate;L&S;College of Letters and Science;4.000;1092;Fall 2008-2009;87.000;PHD 922L&S;;NWD;G922L;Sociology - LS;PHD 922L&S;Sociology PHD-L&S;Sociology;advisor1@wisc.edu;One,Advisor;1000000001;9020000001;ADVR;Academic;Career";
		record = dataSource.parseLine(example3);
		Assert.assertNotNull(record);
		Assert.assertEquals("1000000001", record.getAdvisorEmplid());
		Assert.assertEquals("Sociology - LS", record.getAdvisorRelationshipDescription());
		Assert.assertEquals("0000000001", record.getStudentEmplid());
		Assert.assertEquals("1092", record.getTermNumber());
		Assert.assertEquals("Fall 2008-2009", record.getTermDescription());
		Assert.assertEquals("Academic", record.getAdvisorType());
		Assert.assertEquals("Career", record.getCommitteeRole());
	}
	
	/**
	 * Test readResource function with {@link ByteArrayResource}/
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadResource() throws Exception {
		String example1 = "student1@wisc.edu;One,Test Student;0000000001;9010000001;Graduate;L&S;College of Letters and Science;4.000;1092;Fall 2008-2009;87.000;PHD 922L&S;;NWD;G922L;Sociology - LS;PHD 922L&S;Sociology PHD-L&S;Sociology;advisor1@wisc.edu;One,Advisor;1000000001;9020000001;ADVR;Academic";
		String example2 = "student2@wisc.edu;Two,Test Student;0000000002;9010000002;Senior;NUR;School of Nursing;3.556;1094;Spring 2008-2009;128.000;NUR 712;;NWD;NUR;Nursing Undergraduate;NUR 712;Nursing NUR;Nursing;advisor2@wisc.edu;Two,Advisor;1000000002;9020000002;ADVR;Academic";
		StringBuilder builder = new StringBuilder();
		builder.append(example1);
		builder.append("\n");
		builder.append(example2);
		
		ByteArrayResource sampleResource = new ByteArrayResource(builder.toString().getBytes());
		
		AdvisorListRelationshipDataSourceImpl dataSource = new AdvisorListRelationshipDataSourceImpl();
		List<StudentAdvisorAssignment> records = dataSource.readResource(sampleResource, "1094");
		Assert.assertEquals(1, records.size());
		StudentAdvisorAssignment record = records.get(0);
		Assert.assertEquals("1000000002", record.getAdvisorEmplid());
		Assert.assertEquals("Nursing Undergraduate", record.getAdvisorRelationshipDescription());
		Assert.assertEquals("0000000002", record.getStudentEmplid());
		Assert.assertEquals("1094", record.getTermNumber());
		Assert.assertEquals("Spring 2008-2009", record.getTermDescription());
		Assert.assertEquals("Academic", record.getAdvisorType());
		
		records = dataSource.readResource(sampleResource, "1092");
		Assert.assertEquals(2, records.size());
	}
	
	@Test
	public void testSampleDataSource() throws Exception {
		ClassPathResource sample = new ClassPathResource("sample-advisorlist-data.txt");
		AdvisorListRelationshipDataSourceImpl dataSource = new AdvisorListRelationshipDataSourceImpl();
		List<StudentAdvisorAssignment> records = dataSource.readResource(sample, "1094");
		
		Assert.assertEquals(3, records.size());
		for(StudentAdvisorAssignment rec: records) {
			Assert.assertEquals("1094", rec.getTermNumber());
			Assert.assertEquals("Spring 2008-2009", rec.getTermDescription());
			Assert.assertNull(rec.getCommitteeRole());
		}
	}
	
	@Test
	public void testSampleDataSourceWithCommitteeRole() throws Exception {
		ClassPathResource sample = new ClassPathResource("sample-advisorlist-data-add-committee-role.txt");
		AdvisorListRelationshipDataSourceImpl dataSource = new AdvisorListRelationshipDataSourceImpl();
		List<StudentAdvisorAssignment> records = dataSource.readResource(sample, "1094");
		
		Assert.assertEquals(5, records.size());
		
		StudentAdvisorAssignment example1 = new StudentAdvisorAssignment();
		example1.setAdvisorEmplid("1000000005");
		example1.setAdvisorRelationshipDescription("Business Undergraduate");
		example1.setAdvisorType("Academic");
		example1.setCommitteeRole("Career");
		example1.setStudentEmplid("0000000004");
		example1.setTermDescription("Fall 2010-2011");
		example1.setTermNumber("1112");
		Assert.assertTrue(records.contains(example1));
		
		StudentAdvisorAssignment example2 = new StudentAdvisorAssignment();
		example2.setAdvisorEmplid("1000000006");
		example2.setAdvisorRelationshipDescription("Business Undergraduate");
		example2.setAdvisorType("Academic");
		example2.setCommitteeRole("Academic");
		example2.setStudentEmplid("0000000004");
		example2.setTermDescription("Fall 2010-2011");
		example2.setTermNumber("1112");
	}
	
	@Test
	public void testIsResourceUpdated() throws Exception {
		Resource testData = new ClassPathResource("sample-advisorlist-data.txt");
		AdvisorListRelationshipDataSourceImpl dataSource = new AdvisorListRelationshipDataSourceImpl();
		
		Assert.assertTrue(dataSource.isResourceUpdated(testData));
	}
}
