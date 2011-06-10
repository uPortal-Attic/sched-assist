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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.CalendarAccountNotFoundException;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.impl.visitor.VisitorDao;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Helper class to provide a means to create an Available
 * appointment via the main method (command line).
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableAppointmentTool.java 2400 2010-08-19 18:12:29Z npblair $
 */
public final class AppointmentTool {

	public static final String CONFIG = System.getProperty(
			"org.jasig.schedassist.impl.AppointmentTool.CONFIG", 
			"cli-tools-SAMPLE.xml");
	
	public static final String CREATE = "create";
	public static final String OWNER_ARG = "-owner";
	public static final String VISITOR_ARG = "-visitor";
	public static final String START_ARG = "-start";
	public static final String DURATION_ARG = "-duration";
	
	public static final String DATE_FORMAT = "yyyyMMdd-HHmm";
	
	private ICalendarAccountDao calendarAccountDao;
	private OwnerDao ownerDao;
	private VisitorDao visitorDao;
	private AvailableScheduleDao availableScheduleDao;
	
	private SchedulingAssistantService schedulingAssistantService;
	
	/**
	 * @param schedulingAssistantService
	 * @param calendarDao
	 * @param calendarUserDao
	 * @param ownerDao
	 * @param visitorDao
	 */
	public AppointmentTool(final SchedulingAssistantService schedulingAssistantService,
			final ICalendarAccountDao calendarAccountDao, final OwnerDao ownerDao,
			final VisitorDao visitorDao, final AvailableScheduleDao availableScheduleDao) {
		this.schedulingAssistantService = schedulingAssistantService;
		this.calendarAccountDao = calendarAccountDao;
		this.ownerDao = ownerDao;
		this.visitorDao = visitorDao;
		this.availableScheduleDao = availableScheduleDao;
	}

	/**
	 * 
	 * @param visitorUsername
	 * @param ownerUsername
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws CalendarAccountNotFoundException
	 * @throws NotAVisitorException
	 * @throws SchedulingException
	 */
	public VEvent createAvailableAppointment(String visitorUsername, String ownerUsername,
			Date startDate, Date endDate) throws CalendarAccountNotFoundException, NotAVisitorException, SchedulingException {
		ICalendarAccount visitorUser = calendarAccountDao.getCalendarAccount(visitorUsername);
		ICalendarAccount ownerUser = calendarAccountDao.getCalendarAccount(ownerUsername);
		
		IScheduleVisitor visitor = visitorDao.toVisitor(visitorUser);
		IScheduleOwner owner = ownerDao.locateOwner(ownerUser);
		if(null == owner) {
			throw new SchedulingException("owner not registered with Available");
		}
		
		AvailableBlock block = availableScheduleDao.retrieveTargetBlock(owner, startDate);
		if(null == block) {
			throw new SchedulingException("owner does not have availability at " + startDate);
		}
		VEvent result = this.schedulingAssistantService.scheduleAppointment(visitor, owner, block, "test appointment created by WiscCal administrator");
		return result;
	}
	
	
	/**
	 * main method to interact with {@link AvailableApplicationTool}.
	 * 
	 * @param args
	 * @throws SchedulingException 
	 * @throws NotAVisitorException 
	 * @throws CalendarAccountNotFoundException 
	 */
	public static void main(String[] args) throws CalendarAccountNotFoundException, NotAVisitorException, SchedulingException {
		// scan the arguments
		if(args.length == 0) {
			System.err.println("Usage: AppointmentTool create [-owner username] [-visitor username] [-start YYYYmmdd-hhmm] [-duration minutes]");
			System.exit(1);
			
		}
		
		if(CREATE.equals(args[0])) {
			String visitorUsername = null;
			String ownerUsername = null;
			Date startTime = null;
			int duration = 30;
			
			for(int i = 1; i < args.length; i++) {
				if(OWNER_ARG.equalsIgnoreCase(args[i])) {
					ownerUsername = args[++i]; 
				} else if (VISITOR_ARG.equalsIgnoreCase(args[i])) {
					visitorUsername = args[++i]; 
				} else if (START_ARG.equalsIgnoreCase(args[i])) {
					String start = args[++i];
					SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
					try {
						startTime = df.parse(start);
					} catch (ParseException e) {
						System.err.println("Invalid format for start parameter, must match: " + DATE_FORMAT);
						System.exit(1);
					}
				} else if (DURATION_ARG.equalsIgnoreCase(args[i])) {
					String dur = args[++i];
					duration = Integer.parseInt(dur);
				}
			}
			
			Validate.notEmpty(ownerUsername, "owner argument cannot be empty");
			Validate.notEmpty(visitorUsername, "visitor argument cannot be empty");
			Validate.notNull(startTime, "start argument cannot be empty");
			
			ApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONFIG);

			AppointmentTool tool = new AppointmentTool(
					(SchedulingAssistantService) applicationContext.getBean("schedulingAssistantService"),
					(ICalendarAccountDao) applicationContext.getBean("calendarAccountDao"),
					(OwnerDao) applicationContext.getBean("ownerDao"), 
					(VisitorDao) applicationContext.getBean("visitorDao"),
					(AvailableScheduleDao) applicationContext.getBean("availableScheduleDao"));
			
			Date endDate = DateUtils.addMinutes(startTime, duration);
			VEvent event = tool.createAvailableAppointment(visitorUsername, ownerUsername, startTime, endDate);
			System.out.println("Event successfully created: ");
			System.out.println(event.toString());
		} else {
			System.err.println("Unrecognized command: " + args[0]);
			System.exit(1);
		}
	}

}
