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

package org.jasig.schedassist.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.impl.owner.DefaultScheduleOwnerImpl;
import org.jasig.schedassist.impl.visitor.DefaultScheduleVisitorImpl;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test bench for {@link CompositeRelationshipDaoImpl}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CompositeRelationshipDaoImplTest.java 1914 2010-04-14 21:17:42Z npblair $
 */
public class CompositeRelationshipDaoImplTest {

	/**
	 * 1 owner, 1 visitor, 1 relationship.
	 * 1 Mock RelationshipDao implementation.
	 * 
	 * Verify that the CompositeDao returns the same data the mock does.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSingleComponent() throws Exception {

		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);

		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setUsername("user2");
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(visitorAccount);
			
		MockCalendarAccount owner2Account = new MockCalendarAccount();
		owner2Account.setUsername("user2");
		DefaultScheduleOwnerImpl someoneElse = new DefaultScheduleOwnerImpl(owner2Account, 2);
		
		Relationship relationship = new Relationship();
		relationship.setDescription("testSingleComponent");
		relationship.setOwner(owner);
		relationship.setVisitor(visitor);
		
		List<Relationship> relationships = new ArrayList<Relationship>();
		relationships.add(relationship);
		
		RelationshipDao mock = createMock(RelationshipDao.class);
		expect(mock.forOwner(owner)).andReturn(relationships);
		expect(mock.forVisitor(visitor)).andReturn(relationships);
		expect(mock.forOwner(someoneElse)).andReturn(new ArrayList<Relationship>());
		replay(mock);
		
		List<RelationshipDao> components = new ArrayList<RelationshipDao>();
		components.add(mock);
		
		CompositeRelationshipDaoImpl impl = new CompositeRelationshipDaoImpl();
		impl.setComponents(components);
		
		Assert.assertEquals(relationships, impl.forOwner(owner));
		Assert.assertEquals(relationships, impl.forVisitor(visitor));
		Assert.assertEquals(new ArrayList<Relationship>(), impl.forOwner(someoneElse));
		
		verify(mock);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMultipleComponents() throws Exception {
		
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("owner1");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);

		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setUsername("visitor1");
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(visitorAccount);
		
		MockCalendarAccount owner2Account = new MockCalendarAccount();
		owner2Account.setUsername("owner2");
		DefaultScheduleOwnerImpl owner2 = new DefaultScheduleOwnerImpl(owner2Account, 2);

		MockCalendarAccount visitor2Account = new MockCalendarAccount();
		visitor2Account.setUsername("visitor2");
		DefaultScheduleVisitorImpl visitor2 = new DefaultScheduleVisitorImpl(visitor2Account);
		
		Relationship relationship1 = new Relationship();
		relationship1.setDescription("from component 1");
		relationship1.setOwner(owner);
		relationship1.setVisitor(visitor);
		
		Relationship relationship2 = new Relationship();
		relationship2.setDescription("from component 2");
		relationship2.setOwner(owner);
		relationship2.setVisitor(visitor2);
		
		Relationship relationship3 = new Relationship();
		relationship3.setDescription("from component 2");
		relationship3.setOwner(owner2);
		relationship3.setVisitor(visitor2);
		
		List<Relationship> list1 = new ArrayList<Relationship>();
		list1.add(relationship1);
		
		List<Relationship> list2 = new ArrayList<Relationship>();
		list2.add(relationship2);
		list2.add(relationship3);
		
		RelationshipDao mock1 = createMock(RelationshipDao.class);
		expect(mock1.forOwner(owner)).andReturn(list1);
		replay(mock1);
		
		RelationshipDao mock2 = createMock(RelationshipDao.class);
		expect(mock2.forOwner(owner)).andReturn(list2);
		replay(mock2);
		
		List<RelationshipDao> components = new ArrayList<RelationshipDao>();
		components.add(mock1);
		components.add(mock2);
		
		CompositeRelationshipDaoImpl impl = new CompositeRelationshipDaoImpl();
		impl.setComponents(components);
		
		List<Relationship> expectedForOwner1 = new ArrayList<Relationship>();
		expectedForOwner1.add(relationship1);
		expectedForOwner1.add(relationship2);
		
		List<Relationship> returnedForOwner1 = impl.forOwner(owner);
		Assert.assertTrue(returnedForOwner1.contains(relationship1));
		Assert.assertTrue(returnedForOwner1.contains(relationship2));
		
		verify(mock1);
		verify(mock2);
	}
}
