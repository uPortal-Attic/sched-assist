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

package org.jasig.schedassist.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarDataDao;

/**
 * Object representation of the merged result of an {@link IScheduleOwner}'s
 * {@link AvailableSchedule} and their Calendar data (from a {@link ICalendarDataDao}).
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleSchedule.java 2530 2010-09-10 20:21:16Z npblair $
 */
public class VisibleSchedule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8774450894322731603L;
	private Log LOG = LogFactory.getLog(this.getClass());
	private SortedMap<AvailableBlock, AvailableStatus> blockMap = new TreeMap<AvailableBlock, AvailableStatus>();
	private final MeetingDurations meetingDurations;

	/**
	 * Default constructor.
	 */
	public VisibleSchedule(final MeetingDurations meetingDurations) {
		this.meetingDurations = meetingDurations;
	}

	/**
	 * If the internal map already contains the target block as a key,
	 * the existing key is removed and the new block is stored as the key.
	 * Stores the value for this key as {@link AvailableStatus#FREE}.
	 * 
	 * @param block
	 */
	public void addFreeBlock(final AvailableBlock block) {
		SortedSet<AvailableBlock> expanded = AvailableBlockBuilder.expand(block, meetingDurations.getMinLength());
		for(AvailableBlock small : expanded) {
			if(blockMap.containsKey(small)) {
				// remove any existing keys
				blockMap.remove(small);
			}
			this.blockMap.put(small, AvailableStatus.FREE);
		}
	}
	/**
	 * If the internal map already contains the target block as a key,
	 * remove the existing and store the argument in its place.
	 * Otherwise does not store this block.
	 * @param block
	 */
	public void overwriteFreeBlockOnlyIfPresent(final AvailableBlock block) {
		if(this.blockMap.containsKey(block)) {
			// this only works because AvailableBlock's hashCode/equals doesn't take visitorLimit into account
			this.blockMap.remove(block);
			this.blockMap.put(block, AvailableStatus.FREE);
		}
	}
	/**
	 * Invokes {@link #addFreeBlock(AvailableBlock)} on each {@link AvailableBlock}
	 * in the {@link Collection}.
	 * 
	 * @param blocks
	 */
	public void addFreeBlocks(final Collection<AvailableBlock> blocks) {
		for(AvailableBlock block: blocks) {
			addFreeBlock(block);
		}
	}
	/**
	 * ONLY stores the block in the map if conflicting FREE blocks
	 * are already stored in the block.
	 * 
	 * @param block
	 */
	public void setBusyBlock(final AvailableBlock block) {
		if(this.blockMap.containsKey(block)) {
			this.blockMap.put(block, AvailableStatus.BUSY);
		} else {
			LOG.debug("setBusyBlock on non-matching block: " + block);
			Set<AvailableBlock> conflicting = locateConflicting(block);
			for(AvailableBlock conflict: conflicting) {
				this.blockMap.put(conflict, AvailableStatus.BUSY);
			}
		}
		
	}
	/**
	 * 
	 * @param blocks
	 */
	public void setBusyBlocks(final Collection<AvailableBlock> blocks) {
		for(AvailableBlock block: blocks) {
			setBusyBlock(block);
		}
	}
	/**
	 * ONLY stores the block in the map if conflicting FREE blocks
	 * are already stored in the block.
	 * 
	 * @param block
	 */
	public void setAttendingBlock(final AvailableBlock block) {
		if(this.blockMap.containsKey(block)) {
			this.blockMap.put(block, AvailableStatus.ATTENDING);
		} else {
			Set<AvailableBlock> conflicting = locateConflicting(block);
			if(conflicting.size() > 0) {
				// remove the conflicts
				for(AvailableBlock conflict: conflicting) {
					this.blockMap.remove(conflict);
				}
				// store only the original
				this.blockMap.put(block, AvailableStatus.ATTENDING);
			}
		}
	}
	/**
	 * 
	 * @param blocks
	 */
	public void setAttendingBlocks(final Collection<AvailableBlock> blocks) {
		for(AvailableBlock block: blocks) {
			setAttendingBlock(block);
		}
	}

	/**
	 * @return a defensive copy of the whole map
	 */
	public SortedMap<AvailableBlock, AvailableStatus> getBlockMap() {
		return new TreeMap<AvailableBlock, AvailableStatus>(blockMap);
	}
	/**
	 * 
	 * @return the number of events in this VisibleSchedule
	 */
	public int getSize() {
		return this.blockMap.size();
	}
	/**
	 * 
	 * @return the number of free blocks in this instance
	 */
	public int getFreeCount() {
		return getCountForStatus(AvailableStatus.FREE);
	}
	/**
	 * 
	 * @return the number of busy blocks in this instance
	 */
	public int getBusyCount() {
		return getCountForStatus(AvailableStatus.BUSY);
	}
	/**
	 * 
	 * @return the number of attending blocks in this instance
	 */
	public int getAttendingCount() {
		return getCountForStatus(AvailableStatus.ATTENDING);
	}
	/**
	 * 
	 * @return a {@link List} of {@link AvailableBlock} in this {@link VisibleSchedule} that are {@link AvailableStatus#FREE}
	 */
	public List<AvailableBlock> getFreeList() {
		return getBlockListForStatus(AvailableStatus.FREE);
	}
	/**
	 * 
	 * @return a {@link List} of {@link AvailableBlock} in this {@link VisibleSchedule} that are {@link AvailableStatus#BUSY}
	 */
	public List<AvailableBlock> getBusyList() {
		return getBlockListForStatus(AvailableStatus.BUSY);
	}
	/**
	 * 
	 * @return a {@link List} of {@link AvailableBlock} in this {@link VisibleSchedule} that are {@link AvailableStatus#ATTENDING}
	 */
	public List<AvailableBlock> getAttendingList() {
		return getBlockListForStatus(AvailableStatus.ATTENDING);
	}

	/**
	 * Return the startTime of the first {@link AvailableBlock} 
	 * stored within this object, or null if this object
	 * is empty.
	 * 
	 * @return the first start time of the first block in this instance
	 */
	public Date getScheduleStart() {
		if(this.blockMap.isEmpty()) {
			return null;
		} else {
			AvailableBlock firstKey =  this.blockMap.firstKey();
			return firstKey == null ? null : firstKey.getStartTime();
		}
	}
	
	/**
	 * Return the endTime of the last {@link AvailableBlock} stored
	 * within this object, or null if this object
	 * is empty.
	 * 
	 * @return the very last end time of the last block in this instance
	 */
	public Date getScheduleEnd() {
		if(this.blockMap.isEmpty()) {
			return null;
		} else {
			AvailableBlock lastKey = this.blockMap.lastKey();
			return lastKey == null ? null : lastKey.getEndTime();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Calendar getCalendar() {
		return getCalendar(TimeZone.getDefault());
	}
	/**
	 * Convenience method to generate an ical4j {@link Calendar} from
	 * the {@link AvailableBlock}s stored in this instance.
	 * 
	 * The {@link VEvent}s are very rudimentary, simply defining
	 * the start date, end date, and have an event title that matches
	 * the status (and number of attendees if visitorLimit is > 1).
	 * 
	 * @param timeZone
	 * @return a {@link Calendar} of free/busy/attending events from this instance
	 */
	public Calendar getCalendar(TimeZone timeZone) {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		net.fortuna.ical4j.model.TimeZone ical4jTimeZone = registry.getTimeZone(timeZone.getID());
		ComponentList components = new ComponentList();
		for(Entry<AvailableBlock, AvailableStatus> mapEntry: blockMap.entrySet()) {
			AvailableBlock block = mapEntry.getKey();
			AvailableStatus status = mapEntry.getValue();
			StringBuilder eventTitle = new StringBuilder();
			if(block.getVisitorLimit() > 1 && AvailableStatus.FREE.equals(status)) {
				eventTitle.append("(");
				eventTitle.append(block.getVisitorLimit() - block.getVisitorsAttending());
				eventTitle.append("/");
				eventTitle.append(block.getVisitorLimit());
				eventTitle.append(") ");
			}
			eventTitle.append(status.getValue());
			
			DateTime eventStart = new net.fortuna.ical4j.model.DateTime(block.getStartTime());
			eventStart.setTimeZone(ical4jTimeZone);
			DateTime eventEnd = new net.fortuna.ical4j.model.DateTime(block.getEndTime());
			eventEnd.setTimeZone(ical4jTimeZone);
			VEvent event = new VEvent(eventStart,
					eventEnd,
					eventTitle.toString());
			components.add(event);
		}
		Calendar result = new Calendar(components);
		return result;
	}
	
	
	/**
	 * This method returns a subset of this {@link VisibleSchedule} including 
	 * only blocks between start and end, inclusive.
	 * 
	 * @param start
	 * @param end
	 * @return a subset of this instance between the dates, inclusive
	 */
	public VisibleSchedule subset(final Date start, final Date end) {
		VisibleSchedule result = new VisibleSchedule(this.meetingDurations);
		// add the free blocks only first
		for(Entry<AvailableBlock, AvailableStatus> e : this.blockMap.entrySet()) {
			AvailableBlock block = e.getKey();
			AvailableStatus status = e.getValue();
			if(CommonDateOperations.equalsOrAfter(block.getStartTime(), start) &&
					CommonDateOperations.equalsOrBefore(block.getEndTime(), end)) {
				// have to register the block as free first as BUSY/ATTENDING setters only overwrite free blocks
				result.addFreeBlock(block);
				switch(status) {
				case FREE:
					// do nothing, already added as free
					break;
				case BUSY:
					result.setBusyBlock(block);
					break;
				case ATTENDING:
					result.setAttendingBlock(block);
					break;
				case UNAVAILABLE:
					throw new IllegalStateException("unexpected status (" + status + ") for block " + block);
				}	
			}
		}

		return result;
	}

	/**
	 * Returns the set of {@link AvailableBlock} objects within this instance
	 * that conflict with the argument.
	 * 
	 * A conflict is defined as any overlap of 1 minute or more.
	 * 
	 * @param conflict
	 * @return a set of conflicting blocks within this instance that conflict with the block argument
	 */
	protected Set<AvailableBlock> locateConflicting(final AvailableBlock conflict) {
		Set<AvailableBlock> conflictingKeys = new HashSet<AvailableBlock>();
		
		Date conflictDayStart = DateUtils.truncate(conflict.getStartTime(), java.util.Calendar.DATE);
		Date conflictDayEnd = DateUtils.addDays(conflictDayStart, 1);
		conflictDayEnd = DateUtils.addMinutes(conflictDayEnd, -1);
		
		AvailableBlock rangeStart = AvailableBlockBuilder.createPreferredMinimumDurationBlock(
				conflictDayStart,
				meetingDurations);
		LOG.debug("rangeStart: " + rangeStart);
		AvailableBlock rangeEnd = AvailableBlockBuilder.createBlockEndsAt(conflictDayEnd, meetingDurations.getMinLength());
		LOG.debug("rangeEnd: " + rangeStart);
		
		SortedMap<AvailableBlock, AvailableStatus> subMap = blockMap.subMap(rangeStart, rangeEnd);
		LOG.debug("subset of blockMap size: " + subMap.size());
		
		for(AvailableBlock mapKey: subMap.keySet()) {
			// all the AvailableBlock keys in the map have start/endtimes truncated to the minute
			// shift the key slightly forward (10 seconds) so that conflicts that start or end on the
			// same minute as a key does don't result in false positives
			Date minuteWithinBlock = DateUtils.addSeconds(mapKey.getStartTime(), 10);
			boolean shortCircuit = true;
			while(shortCircuit && CommonDateOperations.equalsOrBefore(minuteWithinBlock, mapKey.getEndTime())) {
				if(minuteWithinBlock.before(conflict.getEndTime()) 
						&& minuteWithinBlock.after(conflict.getStartTime())) {
					conflictingKeys.add(mapKey);
					shortCircuit = false;
				}
				minuteWithinBlock = DateUtils.addMinutes(minuteWithinBlock, 1);
			}
		}
		
		return conflictingKeys;
	}
	/**
	 * Iterate through the blockMap and return a count of
	 * {@link AvailableBlock}s that match the target {@link AvailableStatus}.
	 * @param targetStatus
	 * @return a count of the number of blocks in this instance that match the status argument
	 */
	protected int getCountForStatus(final AvailableStatus targetStatus) {
		int count = 0;
		for(AvailableStatus status : blockMap.values()) {
			if(targetStatus.equals(status)) {
				count++;
			}
		}
		return count;
	}
	/**
	 * Iterate through the blockMap and return a {@link List} of
	 * {@link AvailableBlock}s that match the target {@link AvailableStatus}.
	 * @param targetStatus
	 * @return the list of blocks within this instance that match the status
	 */
	protected List<AvailableBlock> getBlockListForStatus(final AvailableStatus targetStatus) {
		List<AvailableBlock> results = new ArrayList<AvailableBlock>();
		for(Entry<AvailableBlock, AvailableStatus> mapEntry: blockMap.entrySet()) {
			AvailableStatus status = mapEntry.getValue();
			if(targetStatus.equals(status)) {
				AvailableBlock block = mapEntry.getKey();
				results.add(block);
			}
		}
		return results;
	}
	
}
