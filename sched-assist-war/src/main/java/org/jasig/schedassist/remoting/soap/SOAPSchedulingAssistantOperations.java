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

package org.jasig.schedassist.remoting.soap;

import org.jasig.schedassist.CalendarAccountNotFoundException;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.messaging.CancelAppointmentRequest;
import org.jasig.schedassist.messaging.CancelAppointmentResponse;
import org.jasig.schedassist.messaging.CreateAppointmentRequest;
import org.jasig.schedassist.messaging.CreateAppointmentResponse;
import org.jasig.schedassist.messaging.GetRelationshipsRequest;
import org.jasig.schedassist.messaging.GetRelationshipsResponse;
import org.jasig.schedassist.messaging.GetScheduleOwnerByIdRequest;
import org.jasig.schedassist.messaging.GetScheduleOwnerByIdResponse;
import org.jasig.schedassist.messaging.GetTargetAvailableBlockRequest;
import org.jasig.schedassist.messaging.GetTargetAvailableBlockResponse;
import org.jasig.schedassist.messaging.IsEligibleRequest;
import org.jasig.schedassist.messaging.IsEligibleResponse;
import org.jasig.schedassist.messaging.VisibleScheduleRequest;
import org.jasig.schedassist.messaging.VisibleScheduleResponse;
import org.jasig.schedassist.messaging.VisitorConflictsRequest;
import org.jasig.schedassist.messaging.VisitorConflictsResponse;
import org.jasig.schedassist.model.InputFormatException;

/**
 * Interface defining remote version of {@link SchedulingAssistantService}.
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: RemoteAvailableService.java 2976 2011-01-25 14:04:08Z npblair $
 */
public interface SOAPSchedulingAssistantOperations {

	/**
	 * Simple method to determine eligibility for a visitor.
	 * 
	 * @param request
	 * @return
	 */
	IsEligibleResponse isEligible(IsEligibleRequest request);
	/**
	 * 
	 * @param request
	 * @return
	 * @throws NotRegisteredException 
	 * @throws SchedulingException 
	 */
	GetTargetAvailableBlockResponse getTargetAvailableBlock(GetTargetAvailableBlockRequest request) throws NotRegisteredException, SchedulingException;
	
	/**
	 * 
	 * @param query
	 * @return
	 * @throws NotAVisitorException
	 * @throws CalendarAccountNotFoundException 
	 * @throws NotRegisteredException 
	 */
	VisibleScheduleResponse getVisibleSchedule(final VisibleScheduleRequest query)
			throws NotAVisitorException, 
			CalendarAccountNotFoundException, NotRegisteredException;

	/**
	 * 
	 * @param request
	 * @return
	 * @throws NotAVisitorException
	 * @throws InputFormatException
	 * @throws SchedulingException
	 * @throws CalendarAccountNotFoundException 
	 * @throws NotRegisteredException 
	 */
	CreateAppointmentResponse scheduleAppointment(
			final CreateAppointmentRequest request)
			throws NotAVisitorException,
			InputFormatException, SchedulingException,
			CalendarAccountNotFoundException, NotRegisteredException;

	/**
	 * 
	 * @param request
	 * @return
	 * @throws NotAVisitorException
	 * @throws InputFormatException
	 * @throws SchedulingException
	 * @throws CalendarAccountNotFoundException 
	 * @throws NotRegisteredException 
	 */
	CancelAppointmentResponse cancelAppointment(CancelAppointmentRequest request)
			throws NotAVisitorException, 
			InputFormatException, SchedulingException,
			CalendarAccountNotFoundException, NotRegisteredException;
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws NotAVisitorException 
	 * @throws CalendarAccountNotFoundException 
	 */
	GetRelationshipsResponse getRelationships(GetRelationshipsRequest request) 
			throws NotAVisitorException, CalendarAccountNotFoundException;
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws NotAnOwnerException
	 * @throws CalendarAccountNotFoundException
	 * @throws NotRegisteredException 
	 */
	GetScheduleOwnerByIdResponse getScheduleOwnerById(GetScheduleOwnerByIdRequest request) throws CalendarAccountNotFoundException, NotRegisteredException;

	/**
	 * 
	 * @param request
	 * @return
	 * @throws NotAVisitorException 
	 * @throws NotRegisteredException 
	 */
	VisitorConflictsResponse getVisitorConflicts(VisitorConflictsRequest request) throws NotAVisitorException, NotRegisteredException;
}