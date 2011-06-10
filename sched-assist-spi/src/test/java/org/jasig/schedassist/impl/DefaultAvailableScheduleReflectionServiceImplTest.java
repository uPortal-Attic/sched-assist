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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.impl.DefaultAvailableScheduleReflectionServiceImpl;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.jasig.schedassist.model.mock.MockScheduleOwner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test for {@link AvailableScheduleReflectionServiceImpl}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableScheduleReflectionServiceImplTest.java $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:oracle-database.xml"})
public class DefaultAvailableScheduleReflectionServiceImplTest {

	private DefaultAvailableScheduleReflectionServiceImpl availableScheduleReflectionService;
	private BlockingCalendarDataDaoImpl blockingCalendarDao;
	private Log LOG = LogFactory.getLog(this.getClass());
	
	/**
	 * @param availableScheduleReflectionService the availableScheduleReflectionService to set
	 */
	@Autowired
	public void setAvailableScheduleReflectionService(
			DefaultAvailableScheduleReflectionServiceImpl availableScheduleReflectionService) {
		this.availableScheduleReflectionService = availableScheduleReflectionService;
	}
	
	/**
	 * @param blockingCalendarDao the blockingCalendarDao to set
	 */
	@Autowired
	public void setBlockingCalendarDao(BlockingCalendarDataDaoImpl blockingCalendarDao) {
		this.blockingCalendarDao = blockingCalendarDao;
	}

	/**
	 * @throws InterruptedException 
	 * 
	 */
	@Test
	public void testLockCollision() throws InterruptedException {
		MockCalendarAccount calendarAccount = new MockCalendarAccount();
		// owner Id must match a valid id in test database
		final long ownerId = 45L;
		final MockScheduleOwner owner = new MockScheduleOwner(calendarAccount, ownerId);
		availableScheduleReflectionService.addOwnerToLockTableIfNotPresent(owner);

		ThreadGroupRunner threadGroupRunner = new ThreadGroupRunner("availableScheduleReflectionService-integrationTest", true);
		LOG.info("threadGroup initialized");
		threadGroupRunner.addTask(2, new Runnable() {
			@Override
			public void run() {
				availableScheduleReflectionService.processScheduleOwner(owner);
				blockingCalendarDao.getLatch().countDown();
			}
		});
		LOG.info("threadGroup tasks added");
		threadGroupRunner.start();
		LOG.info("threadGroup started");
		threadGroupRunner.join();
	}
}
