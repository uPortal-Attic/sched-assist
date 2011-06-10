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
 *
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PersistencePreferenceRowMapperTest.java 1123 2009-10-07 21:40:35Z npblair $
 */
public class PersistencePreferenceRowMapperTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testControl() throws Exception {
		ResultSet mock = createMock(ResultSet.class);
		expect(mock.getInt("owner_id")).andReturn(45);
		expect(mock.getString("preference_key")).andReturn("key");
		expect(mock.getString("preference_value")).andReturn("value");
		replay(mock);
		
		PersistencePreferenceRowMapper mapper = new PersistencePreferenceRowMapper();
		PersistencePreference pref = mapper.mapRow(mock, 1);
		assertEquals("key", pref.getPreferenceKey());
		assertEquals("value", pref.getPreferenceValue());
		assertEquals(45, pref.getOwnerId());
		
		verify(mock);
	}
	
	public void testPreference() throws Exception {
		
	}
}
