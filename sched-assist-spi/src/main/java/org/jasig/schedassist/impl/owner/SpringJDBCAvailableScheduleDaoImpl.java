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

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.impl.events.AvailableScheduleChangedEvent;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.IScheduleOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring JDBC backed implementation of {@link AvailableScheduleDao}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SpringJDBCAvailableScheduleDaoImpl.java 2517 2010-09-09 18:40:54Z npblair $
 */
@Service("availableScheduleDao")
public class SpringJDBCAvailableScheduleDaoImpl 
implements AvailableScheduleDao {

	private Log LOG = LogFactory.getLog(this.getClass());

	private SimpleJdbcTemplate simpleJdbcTemplate;
	private ApplicationEventPublisher applicationEventPublisher;
	
	/**
	 * @param dataSource the dataSource to set
	 */
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	/**
	 * @param applicationEventPublisher the applicationEventPublisher to set
	 */
	@Autowired
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.AvailableScheduleDao#addToSchedule(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.AvailableBlock)
	 */
	@Transactional
	@Override
	public AvailableSchedule addToSchedule(final IScheduleOwner owner, final AvailableBlock block) {
		// expand input block and call overloaded version
		Set<AvailableBlock> blockExpanded = AvailableBlockBuilder.expand(block, 1);
		return addToSchedule(owner, blockExpanded);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.AvailableScheduleDao#addToSchedule(org.jasig.schedassist.model.IScheduleOwner, java.util.Set)
	 */
	@Transactional
	@Override
	public AvailableSchedule addToSchedule(final IScheduleOwner owner,
			final Set<AvailableBlock> blocks) {
		// retrieve existing schedule
		AvailableSchedule stored = retrieve(owner);

		// expand it to minimum possible size
		SortedSet<AvailableBlock> storedExpanded = AvailableBlockBuilder.expand(stored.getAvailableBlocks(), 1);
		// expand the argument to minimum possible size blocks
		SortedSet<AvailableBlock> blocksExpanded = AvailableBlockBuilder.expand(blocks, 1);

		// add the new blocks to the expanded set
		boolean modified = storedExpanded.addAll(blocksExpanded);
		if(modified) {
			replaceSchedule(owner, storedExpanded);
		}

		// retrieve the new complete schedule and return
		return retrieve(owner);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.AvailableScheduleDao#clearAllBlocks(org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Transactional
	@Override
	public void clearAllBlocks(final IScheduleOwner owner) {
		// delete all old blocks
		LOG.warn("issuing clearAllBlocks for owner " + owner);
		int rowsUpdated = this.simpleJdbcTemplate.update(
				"delete from schedules where owner_id = ?",
				owner.getId());
		LOG.warn("deleted " + rowsUpdated + " for owner " + owner);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.AvailableScheduleDao#removeFromSchedule(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.AvailableBlock)
	 */
	@Transactional
	@Override
	public AvailableSchedule removeFromSchedule(final IScheduleOwner owner,
			final AvailableBlock block) {
		// expand input block and call overloaded version
		Set<AvailableBlock> blockExpanded = AvailableBlockBuilder.expand(block, 1);
		return removeFromSchedule(owner, blockExpanded);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.AvailableScheduleDao#removeFromSchedule(org.jasig.schedassist.model.IScheduleOwner, java.util.Set)
	 */
	@Transactional
	@Override
	public AvailableSchedule removeFromSchedule(final IScheduleOwner owner,
			final Set<AvailableBlock> blocksToRemove) {
		// retrieve existing schedule
		AvailableSchedule stored = retrieve(owner);

		// expand it to minimum possible size
		SortedSet<AvailableBlock> expanded = AvailableBlockBuilder.expand(stored.getAvailableBlocks(), 1);
		// expand the argument to minimum possible size blocks
		SortedSet<AvailableBlock> blocksToRemoveExpanded = AvailableBlockBuilder.expand(blocksToRemove, 1);

		boolean modified = false;
		for(AvailableBlock toRemove : blocksToRemoveExpanded) {
			if(expanded.contains(toRemove)) {
				// remove the specified block
				boolean result =  expanded.remove(toRemove);
				if(result && !modified) {
					modified = true;
				}
			}
		}

		if(modified) {
			replaceSchedule(owner, expanded);
		}
		// retrieve the new complete schedule and return
		return retrieve(owner);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.AvailableScheduleDao#retrieve(org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public AvailableSchedule retrieve(final IScheduleOwner owner) {
		Set<AvailableBlock> availableBlocks = internalRetrieveSchedule(owner);
		AvailableSchedule schedule = new AvailableSchedule(availableBlocks);
		return schedule;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.AvailableScheduleDao#retrieve(org.jasig.schedassist.model.IScheduleOwner, java.util.Date, java.util.Date)
	 */
	@Override
	public AvailableSchedule retrieve(IScheduleOwner owner, Date startTime,
			Date endTime) {
		Set<AvailableBlock> storedBlocks = internalRetrieveSchedule(owner, startTime, endTime);
		AvailableSchedule schedule = new AvailableSchedule(storedBlocks);
		return schedule;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.AvailableScheduleDao#retrieveWeeklySchedule(org.jasig.schedassist.model.IScheduleOwner, java.util.Date)
	 */
	@Override
	public AvailableSchedule retrieveWeeklySchedule(final IScheduleOwner owner, final Date weekOf) {
		Date weekStart = DateUtils.truncate(weekOf, Calendar.DATE);
		Date weekEnd = DateUtils.addDays(weekStart, 7);
		return retrieve(owner, weekStart, weekEnd);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.AvailableScheduleDao#retrieveTargetBlock(org.jasig.schedassist.model.IScheduleOwner, java.util.Date)
	 */
	@Override
	public AvailableBlock retrieveTargetBlock(final IScheduleOwner owner,
			final Date startDate) {
		// truncate startDate to the second
		final Date truncatedStart = DateUtils.truncate(startDate, Calendar.MINUTE);

		// retrieve all blocks for the day.
		Date startOfDay = DateUtils.truncate(startDate, Calendar.DATE);
		Date endOfDay = DateUtils.addDays(startOfDay, 1);
		List<PersistenceAvailableBlock> scheduleRows = this.simpleJdbcTemplate
		.query("select * from schedules where owner_id = ? and start_time >= ? and end_time < ?", 
				new PersistenceAvailableBlockRowMapper(), 
				owner.getId(),
				startOfDay,
				endOfDay);
		SortedSet<AvailableBlock> availableBlocks = new TreeSet<AvailableBlock>();
		for(PersistenceAvailableBlock row : scheduleRows) {
			availableBlocks.add(AvailableBlockBuilder.createBlock(row.getStartTime(), row.getEndTime(), row.getVisitorLimit(), row.getMeetingLocation()));
		}

		int ownerPreferredMinDuration = owner.getPreferredMeetingDurations().getMinLength();
		SortedSet<AvailableBlock> expanded = AvailableBlockBuilder.expand(availableBlocks, ownerPreferredMinDuration);
		if(expanded.size() > 0) {
			for(Iterator<AvailableBlock> expandedIterator = expanded.iterator(); expandedIterator.hasNext();) {
				AvailableBlock block = expandedIterator.next();
				if(block.getStartTime().equals(truncatedStart)) {
					// always return preferred minimum length block
					return block;
				}
			}
		}
		// block not found, return null
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.AvailableScheduleDao#retrieveTargetDoubleLengthBlock(org.jasig.schedassist.model.IScheduleOwner, java.util.Date)
	 */
	@Override
	public AvailableBlock retrieveTargetDoubleLengthBlock(IScheduleOwner owner,
			Date startDate) {
		// truncate startDate to the second
		final Date truncatedStart = DateUtils.truncate(startDate, Calendar.MINUTE);

		// retrieve all blocks for the day.
		Date startOfDay = DateUtils.truncate(startDate, Calendar.DATE);
		Date endOfDay = DateUtils.addDays(startOfDay, 1);
		List<PersistenceAvailableBlock> scheduleRows = this.simpleJdbcTemplate
		.query("select * from schedules where owner_id = ? and start_time >= ? and end_time < ?", 
				new PersistenceAvailableBlockRowMapper(), 
				owner.getId(),
				startOfDay,
				endOfDay);
		SortedSet<AvailableBlock> availableBlocks = new TreeSet<AvailableBlock>();
		for(PersistenceAvailableBlock row : scheduleRows) {
			availableBlocks.add(AvailableBlockBuilder.createBlock(row.getStartTime(), row.getEndTime(), row.getVisitorLimit(), row.getMeetingLocation()));
		}

		int ownerPreferredMinDuration = owner.getPreferredMeetingDurations().getMinLength();
		SortedSet<AvailableBlock> expanded = AvailableBlockBuilder.expand(availableBlocks, ownerPreferredMinDuration);
		if(expanded.size() > 0) {
			for(Iterator<AvailableBlock> expandedIterator = expanded.iterator(); expandedIterator.hasNext();) {
				AvailableBlock block = expandedIterator.next();
				if(block.getStartTime().equals(truncatedStart)) {
					if(owner.getPreferredMeetingDurations().isDoubleLength() && expandedIterator.hasNext()) {
						// combine the block with the next
						AvailableBlock nextBlock = expandedIterator.next();
						AvailableBlock combined = AvailableBlockBuilder.createBlock(block.getStartTime(), nextBlock.getEndTime(), block.getVisitorLimit(), block.getMeetingLocation());
						return combined;
					} 
				} 
			}
		}
		// block not found, return null
		return null;
	}

	/**
	 * Remove blocks from the schedules table from all owners that have endTimes prior
	 * to "<daysPrior argument> before today".
	 * 
	 * @return the number of blocks removed by this operation
	 */
	@Transactional
	@Override
	public int purgeExpiredBlocks(final Integer daysPrior) {
        final String propertyValue = System.getProperty("org.jasig.schedassist.runScheduledTasks", "true");
        if(Boolean.parseBoolean(propertyValue)) {
        	Date priorTo = DateUtils.truncate(
				DateUtils.addDays(new Date(), -daysPrior),
				Calendar.DATE);
        	int rowCount = this.simpleJdbcTemplate.update("delete from schedules where end_time < ?", priorTo);
        	LOG.warn("purged " + rowCount + " rows from schedules table with end_time values prior to: " + priorTo);
        	return rowCount;
        } else {
        	LOG.debug("ignoring purgeExpiredBlocks as 'org.jasig.schedassist.runScheduledTasks' set to false");
        	return 0;
        }
	}

	/**
	 * Executes "insert into schedules (schedule_id, start_time, end_time, visitor_limit) values (?, ?, ?, ?)".
	 * 
	 * @param scheduleBlock
	 * @return the number of rows affected (should be 1 on success)
	 */
	protected int internalStoreBlock(final PersistenceAvailableBlock scheduleBlock) {
		try {
			return this.simpleJdbcTemplate.update(
					"insert into schedules (owner_id, start_time, end_time, visitor_limit, meeting_location) values (?, ?, ?, ?, ?)", 
					scheduleBlock.getOwnerId(), 
					scheduleBlock.getStartTime(), 
					scheduleBlock.getEndTime(),
					scheduleBlock.getVisitorLimit(),
					scheduleBlock.getMeetingLocation());
		} catch (DataIntegrityViolationException e) {
			LOG.warn("ignoring attempt to insert duplicate row", e);
			return 0;
		}
	}

	/**
	 * Inserts all of the arguments into the schedules table using
	 * {@link SimpleJdbcTemplate#batchUpdate(String, SqlParameterSource[])}.
	 * 
	 * @param blocks
	 */
	protected void internalStoreBlocks(final Set<PersistenceAvailableBlock> blocks) {
		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(blocks.toArray());
		this.simpleJdbcTemplate.batchUpdate(
				"insert into schedules (owner_id, start_time, end_time, visitor_limit, meeting_location) values (:ownerId, :startTime, :endTime, :visitorLimit, :meetingLocation)",
				batch);
	}
	/**
	 * Retrieve ALL {@link AvailableBlock}s for an owner in a {@link SortedSet}.
	 * 
	 * The blocks are returned as-is (no expansion).
	 * 
	 * @param owner
	 * @return 
	 */
	protected SortedSet<AvailableBlock> internalRetrieveSchedule(final IScheduleOwner owner) {
		List<PersistenceAvailableBlock> scheduleRows = this.simpleJdbcTemplate
		.query("select * from schedules where owner_id = ?", 
				new PersistenceAvailableBlockRowMapper(), 
				owner.getId());

		SortedSet<AvailableBlock> availableBlocks = new TreeSet<AvailableBlock>();
		for(PersistenceAvailableBlock row : scheduleRows) {
			availableBlocks.add(AvailableBlockBuilder.createBlock(row.getStartTime(), row.getEndTime(), row.getVisitorLimit(), row.getMeetingLocation()));
		}

		return availableBlocks;
	}

	/**
	 * Retrieve the {@link AvailableBlock}s between the specified dates for an owner in a {@link SortedSet}.
	 * 
	 * Starts by retrieving all rows for the owner, then calculating the subSet between the start and end dates.
	 * 
	 * @param owner
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	protected SortedSet<AvailableBlock> internalRetrieveSchedule(final IScheduleOwner owner, final Date startDate, final Date endDate) {		
		TreeSet<AvailableBlock> allStoredBlocks = new TreeSet<AvailableBlock>();
		allStoredBlocks.addAll(internalRetrieveSchedule(owner));
		TreeSet<AvailableBlock> expanded = new TreeSet<AvailableBlock>();
		expanded.addAll(AvailableBlockBuilder.expand(allStoredBlocks, 1));

		// we need the subset of blocks
		AvailableBlock startBlock = AvailableBlockBuilder.createSmallestAllowedBlock(startDate);
		AvailableBlock endBlock = AvailableBlockBuilder.createBlockEndsAt(endDate, 1);
		NavigableSet<AvailableBlock> innerSet = expanded.subSet(startBlock, true,
				endBlock, true);
		// combine the inner set before returning
		SortedSet<AvailableBlock> combinedInnerSet = AvailableBlockBuilder.combine(innerSet);
		return combinedInnerSet;
	}

	/**
	 * Deletes all existing stored blocks and inserts all specified blocks.
	 * 
	 * @param owner
	 * @param blocks
	 */
	private void replaceSchedule(final IScheduleOwner owner, final SortedSet<AvailableBlock> blocks) {
		LOG.debug("replacing schedule for owner " + owner + "; argument contains " + blocks.size() + " blocks");
		// delete all old blocks
		int rowsUpdated = this.simpleJdbcTemplate.update(
				"delete from schedules where owner_id = ?",
				owner.getId());
		LOG.debug("deleted " + rowsUpdated + " for owner " + owner.getId());

		// persist the recombined set
		SortedSet<AvailableBlock> combined = AvailableBlockBuilder.combine(blocks);
		LOG.debug("combined set for owner contains " + combined.size() + " blocks");
		Set<PersistenceAvailableBlock> persistenceBlocks = new HashSet<PersistenceAvailableBlock>();
		for(AvailableBlock newBlock: combined) {
			PersistenceAvailableBlock p = new PersistenceAvailableBlock(newBlock, owner.getId());
			persistenceBlocks.add(p);
		}
		internalStoreBlocks(persistenceBlocks);

		LOG.warn("schedule replaced for owner " + owner);
		if(null != applicationEventPublisher) {
			AvailableScheduleChangedEvent e = new AvailableScheduleChangedEvent(new AvailableSchedule(blocks), owner);
			applicationEventPublisher.publishEvent(e);
		}
	}
}
