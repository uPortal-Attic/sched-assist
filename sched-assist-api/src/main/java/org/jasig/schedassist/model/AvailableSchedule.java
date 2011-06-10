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

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Container for a {@link SortedSet} of {@link AvailableBlock}s.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableSchedule.java 2515 2010-09-09 18:23:35Z npblair $
 */
public class AvailableSchedule {
	
	private SortedSet<AvailableBlock> availableBlocks = new TreeSet<AvailableBlock>();
	
	/**
	 * 
	 * @param availableBlocks
	 */
	public AvailableSchedule(Set<AvailableBlock> availableBlocks) {
		this.availableBlocks.addAll(availableBlocks);
	}
	
	/**
	 * @return the availableBlocks
	 */
	public SortedSet<AvailableBlock> getAvailableBlocks() {
		return availableBlocks;
	}
	
	/**
	 * Add all of the supplied availableBlocks to this schedule.
	 * 
	 * @param availableBlocks
	 */
	public void addAvailableBlocks(final Set<AvailableBlock> availableBlocks) {
		this.availableBlocks.addAll(availableBlocks);
	}
	
	/**
	 * Remove all of the supplied availableBlocks from this schedule.
	 * 
	 * @param blocksToRemove
	 */
	public void removeAvailableBlocks(Set<AvailableBlock> blocksToRemove) {
		this.availableBlocks.removeAll(blocksToRemove);
	}
	
	/**
	 * 
	 * @return true if the internal set of blocks is empty
	 */
	public boolean isEmpty() {
		return this.availableBlocks.isEmpty();
	}
	
	/**
	 * 
	 * @return the start time of the first {@link AvailableBlock} in this schedule, or null if {@link #isEmpty()}.
	 */
	public Date getScheduleStartTime() {
		if(isEmpty()) {
			return null;
		} else {
			return this.availableBlocks.first().getStartTime();
		}
	}
	
	/**
	 * 
	 * @return the end time of the last {@link AvailableBlock} in this schedule, or null if {@link #isEmpty()}.
	 */
	public Date getScheduleEndTime() {
		if(isEmpty()) {
			return null;
		} else {
			return this.availableBlocks.last().getEndTime();
		}
	}
	
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof AvailableSchedule)) {
			return false;
		}
		AvailableSchedule rhs = (AvailableSchedule) object;
		return new EqualsBuilder()
			.append(this.availableBlocks, rhs.availableBlocks)
			.isEquals();
	}
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(875004871, 1021420287)
			.append(this.availableBlocks)
			.toHashCode();
	}
	
	
}
