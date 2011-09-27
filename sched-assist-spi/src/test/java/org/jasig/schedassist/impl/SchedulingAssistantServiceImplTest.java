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

import java.sql.SQLException;

import junit.framework.Assert;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;

import org.easymock.EasyMock;
import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.NullAffiliationSourceImpl;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.DefaultScheduleOwnerImpl;
import org.jasig.schedassist.impl.visitor.DefaultScheduleVisitorImpl;
import org.jasig.schedassist.model.AppointmentRole;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.DefaultEventUtilsImpl;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.junit.Test;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

/**
 * Test bench for {@link SchedulingAssistantServiceImpl}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableServiceImplTest.java 1914 2010-04-14 21:17:42Z npblair $
 */
public class SchedulingAssistantServiceImplTest {
	
	/**
	 * Expect a OracleCalendarDataAccessException to bubble up.
	 * @throws Exception
	 */
	@Test
	public void testScheduleAppointmentCalendarDaoUnavailable() throws Exception {
		// construct a schedule owner
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);
		
		// construct a schedule visitor
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setUsername("user2");
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(visitorAccount);
		
		// construct target availableblock for appointment
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock("20091111-1330", "20091111-1400", 1);
		
		// create mock CalendarDao and AvailableScheduleDao
		ICalendarDataDao mockCalendarDao = EasyMock.createMock(ICalendarDataDao.class);
		mockCalendarDao.checkForConflicts(owner, targetBlock);
		EasyMock.expectLastCall().andThrow(new RuntimeException());
		AvailableScheduleDao mockScheduleDao = EasyMock.createMock(AvailableScheduleDao.class);
		EasyMock.expect(mockScheduleDao.retrieveTargetBlock(owner, CommonDateOperations.parseDateTimePhrase("20091111-1330"))).andReturn(targetBlock);
		EasyMock.replay(mockCalendarDao, mockScheduleDao);
		
		SchedulingAssistantServiceImpl serviceImpl = new SchedulingAssistantServiceImpl();
		serviceImpl.setAvailableScheduleDao(mockScheduleDao);
		serviceImpl.setCalendarDataDao(mockCalendarDao);
		
		try {
			serviceImpl.scheduleAppointment(visitor, owner, targetBlock, "description");
			Assert.fail("expected RuntimeException not thrown");
		} catch(RuntimeException e) {
			// success
		}
		
		EasyMock.verify(mockCalendarDao, mockScheduleDao);
	}
	
	/**
	 * Expect a DataAccessException to bubble up.
	 * @throws Exception
	 */
	@Test
	public void testScheduleAppointmentAvailableScheduleDaoUnavailable() throws Exception {
		// construct a schedule owner
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);
		
		// construct a schedule visitor
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setUsername("user2");
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(visitorAccount);
		
		
		// construct target availableblock for appointment
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock("20091111-1330", "20091111-1400", 1);

		AvailableScheduleDao mockScheduleDao = EasyMock.createMock(AvailableScheduleDao.class);
		EasyMock.expect(mockScheduleDao.retrieveTargetBlock(owner, targetBlock.getStartTime())).andThrow(new CannotGetJdbcConnectionException("database unavailable", new SQLException()));
		EasyMock.replay(mockScheduleDao);
		
		SchedulingAssistantServiceImpl serviceImpl = new SchedulingAssistantServiceImpl();
		serviceImpl.setAvailableScheduleDao(mockScheduleDao);
			
		try {
			serviceImpl.scheduleAppointment(visitor, owner, targetBlock, "description");
			Assert.fail("expected CannotGetJdbcConnectionException not thrown");
		} catch(CannotGetJdbcConnectionException e) {
			// success
		}
		
		EasyMock.verify(mockScheduleDao);
	}
	
	/**
	 * Expect {@link AvailableServiceImpl#scheduleAppointment(ScheduleVisitor, ScheduleOwner, AvailableBlock, String)}
	 * to return null for {@link ScheduleOwner#isSamePerson(ScheduleVisitor)} returning true.
	 * @throws Exception
	 */
	@Test
	public void testScheduleAppointmentOwnerVisitorSamePerson() throws Exception {
		// construct a schedule owner
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);
		
		// construct a schedule visitor from same person
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(ownerAccount);
		
		// construct target availableblock for appointment
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock("20091111-1330", "20091111-1400", 1);
		
		SchedulingAssistantServiceImpl serviceImpl = new SchedulingAssistantServiceImpl();
		Assert.assertNull(serviceImpl.scheduleAppointment(visitor, owner, targetBlock, "description"));
	}
	/**
	 * Expect a SchedulingException
	 * @throws Exception
	 */
	@Test
	public void testScheduleAppointmentNotInSchedule() throws Exception {
		// construct a schedule owner
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);
		
		// construct a schedule visitor
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setUsername("user2");
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(visitorAccount);
		
		
		// construct target availableblock for appointment
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock("20091111-1330", "20091111-1400", 1);

		AvailableScheduleDao mockScheduleDao = EasyMock.createMock(AvailableScheduleDao.class);
		EasyMock.expect(mockScheduleDao.retrieveTargetBlock(owner, targetBlock.getStartTime())).andReturn(null);
		EasyMock.replay(mockScheduleDao);
		
		SchedulingAssistantServiceImpl serviceImpl = new SchedulingAssistantServiceImpl();
		serviceImpl.setAvailableScheduleDao(mockScheduleDao);
			
		try {
			serviceImpl.scheduleAppointment(visitor, owner, targetBlock, "description");
			Assert.fail("expected SchedulingException not thrown");
		} catch(SchedulingException e) {
			// success
		}
		
		EasyMock.verify(mockScheduleDao);
	}
	
	/**
	 * Expect a SchedulingException
	 * @throws Exception
	 */
	@Test
	public void testScheduleAppointmentVisitorLimitExceeded() throws Exception {
		// construct a schedule owner
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		ownerAccount.setEmailAddress("owner@domain.com");
		ownerAccount.setDisplayName("OWNER OWNER");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);
		
		// construct a schedule visitor
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setUsername("v1");
		visitorAccount.setEmailAddress("v1@doit.wisc.edu");
		visitorAccount.setDisplayName("VISITOR ONE");
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(visitorAccount);
		// construct 2nd visitor
		MockCalendarAccount visitor2Account = new MockCalendarAccount();
		visitor2Account.setUsername("v2");
		visitor2Account.setEmailAddress("v2@doit.wisc.edu");
		visitor2Account.setDisplayName("VISITOR TWO");
		DefaultScheduleVisitorImpl visitor2 = new DefaultScheduleVisitorImpl(visitor2Account);

		
		// construct target availableblock for appointment
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock("20091111-1330", "20091111-1400", 2);
		
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
		// construct expected VEvent
		VEvent expectedEvent = eventUtils.constructAvailableAppointment(targetBlock, owner, visitor, "description");
		expectedEvent.getProperties().add(eventUtils.constructAvailableAttendee(visitor2.getCalendarAccount(), AppointmentRole.VISITOR));
		
		// create mock CalendarDao and AvailableScheduleDao
		ICalendarDataDao mockCalendarDao = EasyMock.createMock(ICalendarDataDao.class);
		EasyMock.expect(mockCalendarDao.getExistingAppointment(owner, targetBlock)).andReturn(expectedEvent);
		
		AvailableScheduleDao mockScheduleDao = EasyMock.createMock(AvailableScheduleDao.class);
		EasyMock.expect(mockScheduleDao.retrieveTargetBlock(owner, CommonDateOperations.parseDateTimePhrase("20091111-1330"))).andReturn(targetBlock);
		EasyMock.replay(mockCalendarDao, mockScheduleDao);
		
		SchedulingAssistantServiceImpl serviceImpl = new SchedulingAssistantServiceImpl();
		serviceImpl.setAvailableScheduleDao(mockScheduleDao);
		serviceImpl.setCalendarDataDao(mockCalendarDao);
		serviceImpl.setEventUtils(new DefaultEventUtilsImpl(new NullAffiliationSourceImpl()));
		
		// construct 3rd visitor
		MockCalendarAccount visitor3Account = new MockCalendarAccount();
		visitor3Account.setUsername("v3");
		visitor3Account.setEmailAddress("v3@doit.wisc.edu");
		visitor3Account.setDisplayName("VISITOR THREE");
		DefaultScheduleVisitorImpl visitor3 = new DefaultScheduleVisitorImpl(visitor3Account);

		try {
			serviceImpl.scheduleAppointment(visitor3, owner, targetBlock, "description");
			Assert.fail("expected SchedulingException not thrown");
		} catch (SchedulingException e) {
			// success
		}
		
		
		EasyMock.verify(mockCalendarDao, mockScheduleDao);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testScheduleAppointmentControl() throws Exception {
		// construct a schedule owner
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		ownerAccount.setEmailAddress("owner@domain.com");
		ownerAccount.setDisplayName("OWNER OWNER");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);
		
		// construct a schedule visitor
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setUsername("v1");
		visitorAccount.setEmailAddress("v1@doit.wisc.edu");
		visitorAccount.setDisplayName("VISITOR ONE");
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(visitorAccount);
		
		
		// construct target availableblock for appointment
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock("20091111-1330", "20091111-1400", 1);
		
		// construct successfull VEvent
		VEvent expectedEvent = new VEvent();
		
		// create mock CalendarDao and AvailableScheduleDao
		ICalendarDataDao mockCalendarDao = EasyMock.createMock(ICalendarDataDao.class);
		mockCalendarDao.checkForConflicts(owner, targetBlock);
		EasyMock.expectLastCall();
		EasyMock.expect(mockCalendarDao.createAppointment(visitor, owner, targetBlock, "description")).andReturn(expectedEvent);
		AvailableScheduleDao mockScheduleDao = EasyMock.createMock(AvailableScheduleDao.class);
		EasyMock.expect(mockScheduleDao.retrieveTargetBlock(owner, CommonDateOperations.parseDateTimePhrase("20091111-1330"))).andReturn(targetBlock);
		EasyMock.replay(mockCalendarDao, mockScheduleDao);
		
		SchedulingAssistantServiceImpl serviceImpl = new SchedulingAssistantServiceImpl();
		serviceImpl.setAvailableScheduleDao(mockScheduleDao);
		serviceImpl.setCalendarDataDao(mockCalendarDao);
		
		
		VEvent event = serviceImpl.scheduleAppointment(visitor, owner, targetBlock, "description");
		Assert.assertEquals(expectedEvent, event);
		
		EasyMock.verify(mockCalendarDao, mockScheduleDao);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testScheduleAppointmentMultipleVisitorsCreate() throws Exception {
		// construct a schedule owner
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		ownerAccount.setEmailAddress("owner@domain.com");
		ownerAccount.setDisplayName("OWNER OWNER");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);
		
		// construct a schedule visitor
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setUsername("v1");
		visitorAccount.setEmailAddress("v1@doit.wisc.edu");
		visitorAccount.setDisplayName("VISITOR ONE");
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(visitorAccount);
		
		// construct target availableblock for appointment
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock("20091111-1330", "20091111-1400", 2);
		
		// construct expected VEvent
		VEvent expectedEvent = new VEvent();
		
		// create mock CalendarDao and AvailableScheduleDao
		ICalendarDataDao mockCalendarDao = EasyMock.createMock(ICalendarDataDao.class);
		EasyMock.expect(mockCalendarDao.getExistingAppointment(owner, targetBlock)).andReturn(null);
		mockCalendarDao.checkForConflicts(owner, targetBlock);
		EasyMock.expectLastCall();
		EasyMock.expect(mockCalendarDao.createAppointment(visitor, owner, targetBlock, "description")).andReturn(expectedEvent);
		AvailableScheduleDao mockScheduleDao = EasyMock.createMock(AvailableScheduleDao.class);
		EasyMock.expect(mockScheduleDao.retrieveTargetBlock(owner, CommonDateOperations.parseDateTimePhrase("20091111-1330"))).andReturn(targetBlock);
		EasyMock.replay(mockCalendarDao, mockScheduleDao);
		
		SchedulingAssistantServiceImpl serviceImpl = new SchedulingAssistantServiceImpl();
		serviceImpl.setAvailableScheduleDao(mockScheduleDao);
		serviceImpl.setCalendarDataDao(mockCalendarDao);
		
		VEvent event = serviceImpl.scheduleAppointment(visitor, owner, targetBlock, "description");
		Assert.assertEquals(expectedEvent, event);
		
		EasyMock.verify(mockCalendarDao, mockScheduleDao);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testScheduleAppointmentMultipleVisitorsJoin() throws Exception {
		// construct a schedule owner
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		ownerAccount.setEmailAddress("owner@domain.com");
		ownerAccount.setDisplayName("OWNER OWNER");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);
		
		// construct a schedule visitor
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setUsername("v1");
		visitorAccount.setEmailAddress("v1@doit.wisc.edu");
		visitorAccount.setDisplayName("VISITOR ONE");
		DefaultScheduleVisitorImpl alreadyAcceptedVisitor = new DefaultScheduleVisitorImpl(visitorAccount);
		
		// construct target availableblock for appointment
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock("20091111-1330", "20091111-1400", 2);
		
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
		// construct existing VEvent
		VEvent existingEvent = eventUtils.constructAvailableAppointment(targetBlock, owner, alreadyAcceptedVisitor, null);
		
		
		// construct 2nd visitor
		MockCalendarAccount visitor2Account = new MockCalendarAccount();
		visitor2Account.setUsername("v2");
		visitor2Account.setEmailAddress("v2@doit.wisc.edu");
		visitor2Account.setDisplayName("VISITOR TWO");
		DefaultScheduleVisitorImpl newVisitor = new DefaultScheduleVisitorImpl(visitor2Account);

		
		// construct expected result event
		VEvent expectedEvent = new VEvent(new PropertyList(existingEvent.getProperties()));
		Attendee newAttendee = eventUtils.constructAvailableAttendee(newVisitor.getCalendarAccount(), AppointmentRole.VISITOR);
		expectedEvent.getProperties().add(newAttendee);
		
		// create mock CalendarDao and AvailableScheduleDao
		ICalendarDataDao mockCalendarDao = EasyMock.createMock(ICalendarDataDao.class);
		EasyMock.expect(mockCalendarDao.getExistingAppointment(owner, targetBlock)).andReturn(existingEvent);
		EasyMock.expect(mockCalendarDao.joinAppointment(newVisitor, owner, existingEvent)).andReturn(expectedEvent);
		AvailableScheduleDao mockScheduleDao = EasyMock.createMock(AvailableScheduleDao.class);
		EasyMock.expect(mockScheduleDao.retrieveTargetBlock(owner, CommonDateOperations.parseDateTimePhrase("20091111-1330"))).andReturn(targetBlock);
		EasyMock.replay(mockCalendarDao, mockScheduleDao);
		
		SchedulingAssistantServiceImpl serviceImpl = new SchedulingAssistantServiceImpl();
		serviceImpl.setAvailableScheduleDao(mockScheduleDao);
		serviceImpl.setCalendarDataDao(mockCalendarDao);
		serviceImpl.setEventUtils(new DefaultEventUtilsImpl(new NullAffiliationSourceImpl()));
		
		VEvent event = serviceImpl.scheduleAppointment(newVisitor, owner, targetBlock, null);
		Assert.assertEquals(expectedEvent, event);
		
		EasyMock.verify(mockCalendarDao, mockScheduleDao);
	}
	
	/**
	 * Expect {@link AvailableServiceImpl#cancelAppointment(ScheduleVisitor, ScheduleOwner, VEvent)}
	 * to return immediately for {@link ScheduleOwner#isSamePerson(ScheduleVisitor)} returning true.
	 * @throws Exception
	 */
	@Test
	public void testCancelAppointmentOwnerVisitorSamePerson() throws Exception {
		// construct a schedule owner
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);
		
		// construct a schedule visitor from same person
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(ownerAccount);
		
		SchedulingAssistantServiceImpl serviceImpl = new SchedulingAssistantServiceImpl();
		// this call will throw exception due to missing daos if owner.isSamePerson(visitor) equals false
		serviceImpl.cancelAppointment(visitor, owner, null, null, "cancel reason");
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCancelAppointmentMultipleVisitorsLeave() throws Exception {
		// construct a schedule owner
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		ownerAccount.setEmailAddress("owner@domain.com");
		ownerAccount.setDisplayName("OWNER OWNER");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);
		
		// construct a schedule visitor
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setUsername("v1");
		visitorAccount.setEmailAddress("v1@doit.wisc.edu");
		visitorAccount.setDisplayName("VISITOR ONE");
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(visitorAccount);
		
		// construct target availableblock for appointment
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock("20091111-1330", "20091111-1400", 2);
		
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
		// construct existing VEvent
		VEvent existingEvent = eventUtils.constructAvailableAppointment(targetBlock, owner, visitor, "event description");		
		Attendee newAttendee = eventUtils.constructAvailableAttendee(visitor.getCalendarAccount(), AppointmentRole.VISITOR);
		existingEvent.getProperties().add(newAttendee);

		// construct expected result
		VEvent expectedEvent = new VEvent(new PropertyList(existingEvent.getProperties()));
		expectedEvent.getProperties().remove(newAttendee);
		
		// create mock CalendarDao and AvailableScheduleDao
		ICalendarDataDao mockCalendarDao = EasyMock.createMock(ICalendarDataDao.class);
		EasyMock.expect(mockCalendarDao.getExistingAppointment(owner, targetBlock)).andReturn(existingEvent);
		EasyMock.expect(mockCalendarDao.leaveAppointment(visitor, owner, existingEvent)).andReturn(expectedEvent);
		//AvailableScheduleDao mockScheduleDao = EasyMock.createMock(AvailableScheduleDao.class);
		//EasyMock.expect(mockScheduleDao.retrieveTargetBlock(owner, CommonDateOperations.parseDateTimePhrase("20091111-1330"))).andReturn(targetBlock);
		EasyMock.replay(mockCalendarDao);
		
		SchedulingAssistantServiceImpl serviceImpl = new SchedulingAssistantServiceImpl();
		//serviceImpl.setAvailableScheduleDao(mockScheduleDao);
		serviceImpl.setCalendarDataDao(mockCalendarDao);
		serviceImpl.setEventUtils(new DefaultEventUtilsImpl(new NullAffiliationSourceImpl()));
		
		serviceImpl.cancelAppointment(visitor, owner, existingEvent, targetBlock, "cancel reason");
		
		EasyMock.verify(mockCalendarDao);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCancelAppointmentControl() throws Exception {
		// construct a schedule owner
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		ownerAccount.setUsername("user1");
		ownerAccount.setEmailAddress("owner@domain.com");
		ownerAccount.setDisplayName("OWNER OWNER");
		DefaultScheduleOwnerImpl owner = new DefaultScheduleOwnerImpl(ownerAccount, 1);
		
		// construct a schedule visitor
		MockCalendarAccount visitorAccount = new MockCalendarAccount();
		visitorAccount.setUsername("v1");
		visitorAccount.setEmailAddress("v1@doit.wisc.edu");
		visitorAccount.setDisplayName("VISITOR ONE");
		DefaultScheduleVisitorImpl visitor = new DefaultScheduleVisitorImpl(visitorAccount);
		
		// construct target availableblock for appointment
		AvailableBlock targetBlock = AvailableBlockBuilder.createBlock("20091111-1330", "20091111-1400", 1);
		
		DefaultEventUtilsImpl eventUtils = new DefaultEventUtilsImpl(new NullAffiliationSourceImpl());
		// construct successfull VEvent
		VEvent expectedEvent = eventUtils.constructAvailableAppointment(targetBlock, owner, visitor, "description");
		
		// create mock CalendarDao and AvailableScheduleDao
		ICalendarDataDao mockCalendarDao = EasyMock.createMock(ICalendarDataDao.class);
		EasyMock.expect(mockCalendarDao.getExistingAppointment(owner, targetBlock)).andReturn(expectedEvent);
		mockCalendarDao.cancelAppointment(visitor, owner, expectedEvent);
		EasyMock.expectLastCall();
		EasyMock.replay(mockCalendarDao);
		
		SchedulingAssistantServiceImpl serviceImpl = new SchedulingAssistantServiceImpl();
		serviceImpl.setCalendarDataDao(mockCalendarDao);
		serviceImpl.setEventUtils(new DefaultEventUtilsImpl(new NullAffiliationSourceImpl()));
		
		serviceImpl.cancelAppointment(visitor, owner, expectedEvent, targetBlock, "cancel reason");
		
		
		EasyMock.verify(mockCalendarDao);
	}
}
