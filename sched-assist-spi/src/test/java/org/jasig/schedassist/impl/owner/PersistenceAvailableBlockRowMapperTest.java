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
import static org.junit.Assert.assertNull;

import java.sql.ResultSet;
import java.sql.Timestamp;

import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.junit.Test;

/**
 *
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PersistenceAvailableBlockRowMapperTest.java 1123 2009-10-07 21:40:35Z npblair $
 */
public class PersistenceAvailableBlockRowMapperTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testControl() throws Exception {
		AvailableBlock testBlock = AvailableBlockBuilder.createBlock("20091007-1600", "20091007-1700", 1);
		
		ResultSet mock = createMock(ResultSet.class);
		expect(mock.getLong("owner_id")).andReturn(45L);
		expect(mock.getTimestamp("start_time")).andReturn(new Timestamp(testBlock.getStartTime().getTime()));
		expect(mock.getTimestamp("end_time")).andReturn(new Timestamp(testBlock.getEndTime().getTime()));
		expect(mock.getInt("visitor_limit")).andReturn(1);
		expect(mock.getString("meeting_location")).andReturn(null);
		replay(mock);
		
		PersistenceAvailableBlockRowMapper mapper = new PersistenceAvailableBlockRowMapper();
		PersistenceAvailableBlock block = mapper.mapRow(mock, 1);
		
		assertEquals(testBlock.getStartTime(), block.getStartTime());
		assertEquals(testBlock.getEndTime(), block.getEndTime());
		assertEquals(testBlock.getVisitorLimit(), block.getVisitorLimit());
		assertEquals(45L, block.getOwnerId());
		assertNull(block.getMeetingLocation());
		verify(mock);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVisitorLimitLargerThan1() throws Exception {
		AvailableBlock testBlock = AvailableBlockBuilder.createBlock("20091007-1600", "20091007-1700", 10);
		
		ResultSet mock = createMock(ResultSet.class);
		expect(mock.getLong("owner_id")).andReturn(45L);
		expect(mock.getTimestamp("start_time")).andReturn(new Timestamp(testBlock.getStartTime().getTime()));
		expect(mock.getTimestamp("end_time")).andReturn(new Timestamp(testBlock.getEndTime().getTime()));
		expect(mock.getInt("visitor_limit")).andReturn(10);
		expect(mock.getString("meeting_location")).andReturn(null);
		replay(mock);
		
		PersistenceAvailableBlockRowMapper mapper = new PersistenceAvailableBlockRowMapper();
		PersistenceAvailableBlock block = mapper.mapRow(mock, 1);
		
		assertEquals(testBlock.getStartTime(), block.getStartTime());
		assertEquals(testBlock.getEndTime(), block.getEndTime());
		assertEquals(testBlock.getVisitorLimit(), block.getVisitorLimit());
		assertEquals(45L, block.getOwnerId());
		assertNull(block.getMeetingLocation());
		
		verify(mock);
	}
	

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNullVisitorLimit() throws Exception {
		AvailableBlock testBlock = AvailableBlockBuilder.createBlock("20091007-1600", "20091007-1700", 1);
		
		ResultSet mock = createMock(ResultSet.class);
		expect(mock.getLong("owner_id")).andReturn(45L);
		expect(mock.getTimestamp("start_time")).andReturn(new Timestamp(testBlock.getStartTime().getTime()));
		expect(mock.getTimestamp("end_time")).andReturn(new Timestamp(testBlock.getEndTime().getTime()));
		expect(mock.getInt("visitor_limit")).andReturn(0);
		expect(mock.getString("meeting_location")).andReturn(null);
		replay(mock);
		
		PersistenceAvailableBlockRowMapper mapper = new PersistenceAvailableBlockRowMapper();
		PersistenceAvailableBlock block = mapper.mapRow(mock, 1);
		
		assertEquals(testBlock.getStartTime(), block.getStartTime());
		assertEquals(testBlock.getEndTime(), block.getEndTime());
		assertEquals(testBlock.getVisitorLimit(), block.getVisitorLimit());
		assertEquals(45L, block.getOwnerId());
		assertNull(block.getMeetingLocation());
		verify(mock);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMeetingLocation() throws Exception {
		AvailableBlock testBlock = AvailableBlockBuilder.createBlock("20091007-1600", "20091007-1700", 1);
		
		ResultSet mock = createMock(ResultSet.class);
		expect(mock.getLong("owner_id")).andReturn(45L);
		expect(mock.getTimestamp("start_time")).andReturn(new Timestamp(testBlock.getStartTime().getTime()));
		expect(mock.getTimestamp("end_time")).andReturn(new Timestamp(testBlock.getEndTime().getTime()));
		expect(mock.getInt("visitor_limit")).andReturn(1);
		expect(mock.getString("meeting_location")).andReturn("some office");
		replay(mock);
		
		PersistenceAvailableBlockRowMapper mapper = new PersistenceAvailableBlockRowMapper();
		PersistenceAvailableBlock block = mapper.mapRow(mock, 1);
		
		assertEquals(testBlock.getStartTime(), block.getStartTime());
		assertEquals(testBlock.getEndTime(), block.getEndTime());
		assertEquals(testBlock.getVisitorLimit(), block.getVisitorLimit());
		assertEquals(45L, block.getOwnerId());
		assertEquals("some office", block.getMeetingLocation());
		verify(mock);
	}
}
