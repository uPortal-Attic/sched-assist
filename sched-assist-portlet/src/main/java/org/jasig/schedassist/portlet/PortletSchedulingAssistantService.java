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

package org.jasig.schedassist.portlet;

import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.component.VEvent;

import org.jasig.schedassist.RelationshipDao;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.model.VisibleSchedule;

/**
 * Service object mimicking methods within {@link SchedulingAssistantService}, {@link RelationshipDao},
 * and {@link AvailableScheduleDao} tailored for the portlet.
 * The main differences correspond to the limited amount of data available
 * for customer accounts (both visitors and owners).
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PortletAvailableService.java 2359 2010-08-12 14:10:38Z npblair $
 */
public interface PortletSchedulingAssistantService {

	/**
	 * 
	 * @param visitorUsername
	 * @return true if the customer account specified by visitorUsername is eligible for service
	 */
	boolean isEligible(String visitorUsername);
	/**
	 * 
	 * @see SchedulingAssistantService#cancelAppointment(org.jasig.schedassist.model.IScheduleVisitor, IScheduleOwner, VEvent, AvailableBlock, String)
	 * @param visitorUsername
	 * @param ownerId
	 * @param block
	 * @return the event that was canceled
	 * @throws SchedulingException
	 */
	EventCancellation cancelAppointment(String visitorUsername,
			long ownerId, AvailableBlock block, String cancelReason)
			throws SchedulingException;

	/**
	 * 
	 * @see SchedulingAssistantService#getVisibleSchedule(org.jasig.schedassist.model.IScheduleVisitor, IScheduleOwner)
	 * @param visitorUsername
	 * @param ownerId
	 * @return
	 */
	VisibleSchedule getVisibleSchedule(String visitorUsername,
			long ownerId);

	/**
	 * 
	 * @see SchedulingAssistantService#getVisibleSchedule(org.jasig.schedassist.model.IScheduleVisitor, IScheduleOwner, Date, Date)
	 * @param visitorUsername
	 * @param ownerId
	 * @return
	 */
	VisibleSchedule getVisibleSchedule(String visitorUsername,
			long ownerId, int weekStart);
	
	/**
	 * 
	 * @see SchedulingAssistantService#calculateVisitorConflicts(org.jasig.schedassist.model.IScheduleVisitor, IScheduleOwner, Date, Date)
	 * @param visitorUsername
	 * @param ownerId
	 * @param weekStart
	 * @return
	 */
	List<AvailableBlock> calculateVisitorConflicts(String visitorUsername, long ownerId, int weekStart);
	/**
	 * 
	 * @see AvailableScheduleDao#retrieveTargetBlock(IScheduleOwner, Date)
	 * @param owner
	 * @param startTime
	 * @return
	 */
	AvailableBlock getTargetBlock(IScheduleOwner owner, Date startTime);
	
	/**
	 * 
	 * @see AvailableScheduleDao#retrieveTargetDoubleLengthBlock(IScheduleOwner, Date)
	 * @param owner
	 * @param startTime
	 * @return
	 */
	AvailableBlock getTargetDoubleLengthBlock(IScheduleOwner owner, Date startTime);
	
	/**
	 * 
	 * @see SchedulingAssistantService#scheduleAppointment(org.jasig.schedassist.model.IScheduleVisitor, IScheduleOwner, AvailableBlock, String)
	 * @param visitorUsername
	 * @param ownerId
	 * @param block
	 * @param eventDescription
	 * @return
	 * @throws SchedulingException
	 */
	VEvent scheduleAppointment(String visitorUsername,
			long ownerId, AvailableBlock block,
			String eventDescription) throws SchedulingException;

	/**
	 * 
	 * @see RelationshipDao#forVisitor(org.jasig.schedassist.model.IScheduleVisitor)
	 * @param visitor
	 * @return
	 */
	List<Relationship> relationshipsForVisitor(String visitorUsername);

}