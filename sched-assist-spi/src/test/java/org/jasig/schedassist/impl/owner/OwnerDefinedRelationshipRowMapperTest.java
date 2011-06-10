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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;

import org.junit.Test;

/**
 * Test harness for {@link OwnerDefinedRelationshipRowMapper}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: OwnerDefinedRelationshipRowMapperTest.java 1136 2009-10-16 17:01:25Z npblair $
 */
public class OwnerDefinedRelationshipRowMapperTest {

	/**
	 * Control example of row in adhoc relationship table.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testControl() throws Exception {
		ResultSet mock = createMock(ResultSet.class);
		expect(mock.getString("owner_username")).andReturn("owner1");
		expect(mock.getString("relationship")).andReturn("test case");
		expect(mock.getString("visitor_username")).andReturn("visitor2");
		replay(mock);
		
		OwnerDefinedRelationshipRowMapper mapper = new OwnerDefinedRelationshipRowMapper();
		OwnerDefinedRelationship relationship = mapper.mapRow(mock, 1);
		assertEquals("owner1", relationship.getOwnerUsername());
		assertEquals("test case", relationship.getRelationship());
		assertEquals("visitor2", relationship.getVisitorUsername());
		
		verify(mock);
	}
}
