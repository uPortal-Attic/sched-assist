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

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import org.jasig.schedassist.ConflictExistsException;
import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;

/**
 * {@link ICalendarDataDao} implementation useful for {@link DefaultAvailableScheduleReflectionServiceImplTest}.
 * Depends on a {@link CountDownLatch}; {@link #reflectAvailableSchedule(IScheduleOwner, AvailableSchedule)} 
 * calls await on the latch.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: BlockingCalendarDao.java $
 */
public class BlockingCalendarDataDaoImpl implements ICalendarDataDao {

	private final CountDownLatch latch;
	
	
	public BlockingCalendarDataDaoImpl(CountDownLatch latch) {
		this.latch = latch;
	}
	/**
	 * @return the latch
	 */
	public CountDownLatch getLatch() {
		return latch;
	}

	@Override
	public Calendar getCalendar(ICalendarAccount calendarAccount,
			Date startDate, Date endDate) {
		return null;
	}

	@Override
	public VEvent getExistingAppointment(IScheduleOwner owner,
			AvailableBlock block) {
		return null;
	}

	@Override
	public VEvent createAppointment(IScheduleVisitor visitor,
			IScheduleOwner owner, AvailableBlock block, String eventDescription) {
		return null;
	}

	@Override
	public void cancelAppointment(IScheduleVisitor visitor, IScheduleOwner owner, VEvent event) {

	}

	@Override
	public VEvent joinAppointment(IScheduleVisitor visitor,
			IScheduleOwner owner, VEvent appointment)
			throws SchedulingException {

		return null;
	}

	@Override
	public VEvent leaveAppointment(IScheduleVisitor visitor,
			IScheduleOwner owner, VEvent appointment)
			throws SchedulingException {
		return null;
	}

	@Override
	public void checkForConflicts(IScheduleOwner owner, AvailableBlock block)
			throws ConflictExistsException {

	}

	/**
	 * Calls {@link CountDownLatch#await()}.
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarDataDao#reflectAvailableSchedule(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.AvailableSchedule)
	 */
	@Override
	public void reflectAvailableSchedule(IScheduleOwner owner,
			AvailableSchedule schedule) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void purgeAvailableScheduleReflections(IScheduleOwner owner,
			Date startDate, Date endDate) {

	}

}
