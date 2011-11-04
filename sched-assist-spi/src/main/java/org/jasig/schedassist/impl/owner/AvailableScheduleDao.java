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

import java.util.Date;
import java.util.Set;

import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.MeetingDurations;

/**
 * Interface for storing an {@link IScheduleOwner}'s {@link AvailableSchedule}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableScheduleDao.java 2261 2010-07-20 15:57:51Z npblair $
 */
public interface AvailableScheduleDao {

	/**
	 * Add a single {@link AvailableBlock} to the {@link IScheduleOwner}'s {@link AvailableSchedule}.
	 * 
	 * @param owner
	 * @param block
	 * @return
	 */
	AvailableSchedule addToSchedule(IScheduleOwner owner, AvailableBlock block);
	
	/**
	 * Add a {@link Set} of {@link AvailableBlock}s to the {@link IScheduleOwner}'s {@link AvailableSchedule}.
	 * @param owner
	 * @param block
	 * @return
	 */
	AvailableSchedule addToSchedule(IScheduleOwner owner, Set<AvailableBlock> blocks);
	
	/**
	 * Remove a single {@link AvailableBlock} from the {@link IScheduleOwner}'s {@link AvailableSchedule}.
	 * 
	 * Implementations should ignore the visitorLimit field of the block argument and simply
	 * delete any blocks that match the start and end times.
	 * 
	 * @param owner
	 * @param block
	 * @return
	 */
	AvailableSchedule removeFromSchedule(IScheduleOwner owner, AvailableBlock block);
	
	/**
	 * Remove a {@link Set} of {@link AvailableBlock}s from the {@link IScheduleOwner}'s {@link AvailableSchedule}.
	 * 
	 * Implementations should ignore the visitorLimit field of the blocksToRemove argument and simply
	 * delete any blocks that match the start and end times.
	 * 
	 * @param owner
	 * @param blocksToRemove
	 * @return
	 */
	AvailableSchedule removeFromSchedule(IScheduleOwner owner, Set<AvailableBlock> blocksToRemove);
	
	/**
	 * Remove ALL stored {@link AvailableBlock}s from an {@link IScheduleOwner}'s {@link AvailableSchedule}.
	 * 
	 * @param ScheduleOwner
	 * @return
	 */
	void clearAllBlocks(IScheduleOwner ScheduleOwner);
	
	/**
	 * Retrieve the {@link IScheduleOwner}'s entire {@link AvailableSchedule}.
	 * 
	 * @param owner
	 * @return
	 */
	AvailableSchedule retrieve(IScheduleOwner owner);
	
	/**
	 * Retrieve the {@link IScheduleOwner}'s schedule between
	 * the 2 specified {@link Date}s.
	 * @param owner
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	AvailableSchedule retrieve(IScheduleOwner owner, Date startTime, Date endTime);
	
	/**
	 * Retrieve the {@link IScheduleOwner}'s {@link AvailableSchedule} for the
	 * 7 days starting at weekOf.
	 * 
	 * @param owner
	 * @param weekOf
	 * @return
	 */
	AvailableSchedule retrieveWeeklySchedule(IScheduleOwner owner, Date weekOf);
	
	/**
	 * Retrieve a single {@link AvailableBlock} from the {@link IScheduleOwner}'s
	 * schedule that starts at the startDate parameter, if it exists.
	 * 
	 * This block should have a duration equal to the minimum duration
	 * in the {@link IScheduleOwner}'s preferred {@link MeetingDurations}.
	 *  
	 * @see MeetingDurations#getMinLength()
	 * @param owner
	 * @param startDate
	 * @return the corresponding {@link AvailableBlock} in the {@link IScheduleOwner}'s schedule, or null if doesn't exist
	 */
	AvailableBlock retrieveTargetBlock(IScheduleOwner owner, Date startDate);
	
	/**
	 * Retrieve a single {@link AvailableBlock} from the {@link IScheduleOwner}'s
	 * schedule that starts at the startDate parameter AND ENDs at the endDate parameter, if it exists.
	 *  
	 * @param owner
	 * @param startDate
	 * @param endDate
	 * @return the corresponding {@link AvailableBlock} in the {@link IScheduleOwner}'s schedule, or null if doesn't exist
	 */
	AvailableBlock retrieveTargetBlock(IScheduleOwner owner, Date startDate, Date endDate);
	
	/**
	 * Retrieve a single "double length" {@link AvailableBlock} from the {@link IScheduleOwner}'s
	 * schedule that starts at the startDate parameter.
	 * 
	 * May return null if the {@link IScheduleOwner} doesn't support double length blocks.
	 * @param owner
	 * @param startDate
	 * @return the corresponding {@link AvailableBlock} in the {@link IScheduleOwner}'s schedule, or null if doesn't exist
	 */
	AvailableBlock retrieveTargetDoubleLengthBlock(IScheduleOwner owner, Date startDate);
	
	/**
	 * Remove blocks from the schedules table from all owners that have endTimes prior
	 * to "<daysPrior argument> before today".
	 * 
	 * @param daysPrior number of days before "today" to be used as a reference point
	 * @return the number of blocks removed by this operation
	 */
	int purgeExpiredBlocks(final Integer daysPrior);
	
}
