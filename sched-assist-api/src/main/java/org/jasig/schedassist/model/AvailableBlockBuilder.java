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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory for {@link AvailableBlock} objects.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableBlockBuilder.java 2525 2010-09-10 19:13:01Z npblair $
 */
public final class AvailableBlockBuilder {

	protected static final String TIME_REGEX = "(\\d{1,2})\\:([0-5]\\d{1}) ([AP]M)";
	protected static final Pattern TIME_PATTERN = Pattern.compile(TIME_REGEX, Pattern.CASE_INSENSITIVE);
	protected static final int MINIMUM_MINUTES = 5;
	
	private static Log LOG = LogFactory.getLog(AvailableBlockBuilder.class);
	
	/**
	 * Construct a list of {@link AvailableBlock} from the following criteria:
	 * 
	 * <ul>
	 * <li>startTimePhrase and endTimePhrase should look like HH:MM AM/PM.</li>
	 * <li>daysOfWeekPhrase looks like "MWF" and uses the following characters:
	 * <ul>
	 * <li>N is Sunday</li>
	 * <li>M is Monday</li>
	 * <li>T is Tuesday</li>
	 * <li>W is Wednesday</li>
	 * <li>R is Thursday</li>
	 * <li>F is Friday</li>
	 * <li>S is Saturday</li>
	 * </ul></li>
	 * <li>startDate must exist before endDate on the calendar. Any non-midnight time value on either
	 * of these dates is rolled back to 00:00:00 (midnight).</li>
	 * </ul>
	 * 
	 * @param startTimePhrase
	 * @param endTimePhrase
	 * @param daysOfWeekPhrase
	 * @param startDate
	 * @param endDate
	 * @return the {@link List} of {@link AvailableBlock}s that fall within the specified date/time criteria.
	 * @throws InputFormatException if the values for startTimePhrase or endTimePhrase do not match the expected format
	 */
	public static SortedSet<AvailableBlock> createBlocks(final String startTimePhrase, final String endTimePhrase, 
			final String daysOfWeekPhrase, final Date startDate, final Date endDate) throws InputFormatException {
		return createBlocks(startTimePhrase, endTimePhrase, daysOfWeekPhrase, startDate, endDate, 1);
	}
	
	/**
	 * Construct a list of {@link AvailableBlock} from the following criteria:
	 * 
	 * <ul>
	 * <li>startTimePhrase and endTimePhrase should look like HH:MM AM/PM.</li>
	 * <li>daysOfWeekPhrase looks like "MWF" and uses the following characters:
	 * <ul>
	 * <li>N is Sunday</li>
	 * <li>M is Monday</li>
	 * <li>T is Tuesday</li>
	 * <li>W is Wednesday</li>
	 * <li>R is Thursday</li>
	 * <li>F is Friday</li>
	 * <li>S is Saturday</li>
	 * </ul></li>
	 * <li>startDate must exist before endDate on the calendar. Any non-midnight time value on either
	 * of these dates is rolled back to 00:00:00 (midnight).</li>
	 * </ul>
	 * 
	 * @param startTimePhrase
	 * @param endTimePhrase
	 * @param daysOfWeekPhrase
	 * @param startDate
	 * @param endDate
	 * @param visitorLimit
	 * @return the {@link List} of {@link AvailableBlock}s that fall within the specified date/time criteria.
	 * @throws InputFormatException if the values for startTimePhrase or endTimePhrase do not match the expected format
	 */
	public static SortedSet<AvailableBlock> createBlocks(final String startTimePhrase, final String endTimePhrase, 
			final String daysOfWeekPhrase, final Date startDate, final Date endDate, final int visitorLimit) throws InputFormatException {
		return createBlocks(startTimePhrase, endTimePhrase, daysOfWeekPhrase, startDate, endDate, visitorLimit, null);
	}
	
	/**
	 * 
	 * @param startTimePhrase
	 * @param endTimePhrase
	 * @param daysOfWeekPhrase
	 * @param startDate
	 * @param endDate
	 * @param visitorLimit
	 * @param meetingLocation
	 * @return
	 * @throws InputFormatException
	 */
	public static SortedSet<AvailableBlock> createBlocks(final String startTimePhrase, final String endTimePhrase, 
			final String daysOfWeekPhrase, final Date startDate, final Date endDate, final int visitorLimit, final String meetingLocation) throws InputFormatException {
		SortedSet<AvailableBlock> blocks = new TreeSet<AvailableBlock>();
		// set time of startDate to 00:00:00
		Date realStartDate = DateUtils.truncate(startDate, Calendar.DATE);
		// set time of endDate to 23:59:59
		Date dayAfterEndDate = DateUtils.truncate(DateUtils.addDays(endDate, 1), Calendar.DATE);
		Date realEndDate = DateUtils.addSeconds(dayAfterEndDate, -1);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("createBlocks calculated realStartDate: " + realStartDate + ", realEndDate: " + realEndDate);
		}
		
		List<Date> matchingDays = matchingDays(daysOfWeekPhrase, realStartDate, realEndDate);
		for(Date matchingDate : matchingDays) {
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.setTime(matchingDate);
			Calendar endCalendar = (Calendar) startCalendar.clone();
			
			interpretAndUpdateTime(startTimePhrase, startCalendar);
			interpretAndUpdateTime(endTimePhrase, endCalendar);
			
			Date blockStartTime = startCalendar.getTime();
			Date blockEndTime = endCalendar.getTime();
			if(!blockEndTime.after(blockStartTime)) {
				throw new InputFormatException("Start time must occur before end time");
			}
			if(CommonDateOperations.equalsOrAfter(blockStartTime, realStartDate) && CommonDateOperations.equalsOrBefore(blockEndTime, realEndDate)) {
				AvailableBlock block = new AvailableBlock(blockStartTime, blockEndTime, visitorLimit, meetingLocation);
				blocks.add(block);
			}
			
		}
		return blocks;
	}
	
	/**
	 * Create a single {@link AvailableBlock} with a visitorLimit of 1.
	 * 
	 * @param startDate
	 * @param endDate
	 * @return the new block
	 */
	public static AvailableBlock createBlock(final Date startDate, final Date endDate) {
		return createBlock(startDate, endDate, 1);
	}
	
	/**
	 * Create a single {@link AvailableBlock}.
	 * 
	 * @param startDate
	 * @param endDate
	 * @param visitorLimit
	 * @return the new block
	 */
	public static AvailableBlock createBlock(final Date startDate, final Date endDate, final int visitorLimit) {
		return createBlock(startDate, endDate, visitorLimit, null);
	}
	
	/**
	 * Create a single {@link AvailableBlock}.
	 * 
	 * @param startDate
	 * @param endDate
	 * @param visitorLimit
	 * @param meetingLocation
	 * @return the new block
	 */
	public static AvailableBlock createBlock(final Date startDate, final Date endDate, final int visitorLimit, final String meetingLocation) {
		return new AvailableBlock(startDate, endDate, visitorLimit, meetingLocation);
	}
	
	/**
	 * Create a single {@link AvailableBlock} with a visitorLimit of 1 and using this application's common time format ("yyyyMMdd-HHmm")
	 * for the start and end datetimes.
	 * 
	 * @see CommonDateOperations#parseDateTimePhrase(String)
	 * @param startTimePhrase
	 * @param endTimePhrase
	 * @return the new block
	 * @throws InputFormatException
	 */
	public static AvailableBlock createBlock(final String startTimePhrase, final String endTimePhrase) throws InputFormatException {
		return createBlock(startTimePhrase, endTimePhrase, 1);
	}
	
	/**
	 * Create a single {@link AvailableBlock} using this applications common time format ("yyyyMMdd-HHmm").
	 * 
	 * @see CommonDateOperations#parseDateTimePhrase(String)
	 * @param startTimePhrase
	 * @param endTimePhrase
	 * @param visitorLimit
	 * @return the new block
	 * @throws InputFormatException
	 */
	public static AvailableBlock createBlock(final String startTimePhrase, final String endTimePhrase, final int visitorLimit) throws InputFormatException {
		return createBlock(startTimePhrase, endTimePhrase, visitorLimit, null);
	}
	
	/**
	 * Create a single {@link AvailableBlock} using this applications common time format ("yyyyMMdd-HHmm").
	 * 
	 * @see CommonDateOperations#parseDateTimePhrase(String)
	 * @param startTimePhrase
	 * @param endTimePhrase
	 * @param visitorLimit
	 * @return the new block
	 * @throws InputFormatException
	 */
	public static AvailableBlock createBlock(final String startTimePhrase, final String endTimePhrase, final int visitorLimit, final String meetingLocation) throws InputFormatException {
		Date startTime = CommonDateOperations.parseDateTimePhrase(startTimePhrase);
		Date endTime = CommonDateOperations.parseDateTimePhrase(endTimePhrase);
		return createBlock(startTime, endTime, visitorLimit, meetingLocation);
	}
	
	/**
	 * Create a single {@link AvailableBlock} that starts at the startTime Phrase (uses 
	 * {@link CommonDateOperations#parseDateTimePhrase(String)} format) and ends duration minutes later.
	 * 
	 * @param startTimePhrase
	 * @param duration
	 * @return the new block
	 * @throws InputFormatException
	 */
	public static AvailableBlock createBlock(final String startTimePhrase, final int duration) throws InputFormatException {
		Date startTime = CommonDateOperations.parseDateTimePhrase(startTimePhrase);
		Date endTime = DateUtils.addMinutes(startTime, duration);
		return createBlock(startTime, endTime);
	}
	
	/**
	 * Create a single {@link AvailableBlock} that ENDS at the endDate argument and starts duration minutes prior.
	 * 
	 * @param endDate end time
	 * @param duration how many minutes prior for the start date
	 * @return the new block
	 */
	public static AvailableBlock createBlockEndsAt(final Date endDate, final int duration) {
		Date startDate = DateUtils.addMinutes(endDate, -duration);
		return createBlock(startDate, endDate);
	}
	
	/**
	 * Create an {@link AvailableBlock} from the specified startTime to an endTime interpreted from {@link MeetingDurations#getMinLength()}.
	 * visitorLimit for the returned {@link AvailableBlock} will be set to 1.
	 * 
	 * @param startTime
	 * @param preferredMeetingDurations
	 * @return the new block
	 */
	public static AvailableBlock createPreferredMinimumDurationBlock(final Date startTime, final MeetingDurations preferredMeetingDurations) {
		return createPreferredMinimumDurationBlock(startTime, preferredMeetingDurations, 1);
	}
	/**
	 * Create an {@link AvailableBlock} from the specified startTime to an endTime interpreted from {@link MeetingDurations#getMinLength()}.
	 * 
	 * @param startTime
	 * @param preferredMeetingDurations
	 * @param visitorLimit
	 * @return the new block
	 */
	public static AvailableBlock createPreferredMinimumDurationBlock(final Date startTime, final MeetingDurations preferredMeetingDurations, final int visitorLimit) {
		Date endTime = DateUtils.addMinutes(startTime, preferredMeetingDurations.getMinLength());
		return createBlock(startTime, endTime, visitorLimit);
	}
	
	/**
	 * Creates a minimum size {@link AvailableBlock} by adding MINIMUM_MINUTES minutes to startTime as the endTime
	 * and visitorLimit of 1.
	 * 
	 * @param startTimePhrase
	 * @return the new block
	 * @throws InputFormatException
	 */
	public static AvailableBlock createSmallestAllowedBlock(final String startTimePhrase) throws InputFormatException {
		return createSmallestAllowedBlock(startTimePhrase, 1);
	}
	/**
	 * Creates a minimum size {@link AvailableBlock} by adding MINIMUM_MINUTES minutes to startTime as the endTime.
	 * 
	 * @see #createSmallestAllowedBlock(Date, int)
	 * @param startTimePhrase
	 * @param visitorLimit
	 * @return the new block
	 * @throws InputFormatException
	 */
	public static AvailableBlock createSmallestAllowedBlock(final String startTimePhrase, final int visitorLimit) throws InputFormatException {
		Date startTime = CommonDateOperations.parseDateTimePhrase(startTimePhrase);
		return createSmallestAllowedBlock(startTime, visitorLimit);
	}
	
	/**
	 * Creates a minimum size {@link AvailableBlock} by adding MINIMUM_MINUTES minutes to startTime as the endTime
	 * and visitorLimit of 1.
	 * 
	 * @see #createSmallestAllowedBlock(Date, int)
	 * @param startTime
	 * @return the new block
	 */
	public static AvailableBlock createSmallestAllowedBlock(final Date startTime) {
		return createSmallestAllowedBlock(startTime, 1);
	}
	/**
	 * Creates a minimum size {@link AvailableBlock} by adding MINIMUM_MINUTES minutes to startTime as the endTime.
	 * 
	 * @param startTime
	 * @param visitorLimit
	 * @return the new block
	 */
	public static AvailableBlock createSmallestAllowedBlock(final Date startTime, final int visitorLimit) {
		Date endTime = DateUtils.addMinutes(startTime, MINIMUM_MINUTES);
		return createBlock(startTime, endTime, visitorLimit);
	}
	
	/**
	 * Creates a minimum size {@link AvailableBlock} using the argument endTime as the end and MINIMUM_MINUTES minutes
	 * prior to endTime as the start.
	 * 
	 * @param endTime
	 * @return the new block
	 */
	public static AvailableBlock createMinimumEndBlock(final Date endTime)  {
		Date startTime = DateUtils.addMinutes(endTime, -MINIMUM_MINUTES);
		return createBlock(startTime, endTime);
	}
	
	/**
	 * Expand one {@link AvailableBlock} into a {@link SortedSet} of {@link AvailableBlock}s with a duration equal
	 * to the meetingLengthMinutes argument in minutes.
	 * 
	 * @param largeBlock
	 * @return the new set
	 */
	public static SortedSet<AvailableBlock> expand(final AvailableBlock largeBlock, final int meetingLengthMinutes) {
		SortedSet<AvailableBlock> smallBlocks = new TreeSet<AvailableBlock>();
		
		long meetingLengthInMsec = convertMinutesToMsec(meetingLengthMinutes);
		Date currentStart = largeBlock.getStartTime();
		while(largeBlock.getEndTime().getTime() - currentStart.getTime() >= meetingLengthInMsec) {
			Date newEndTime = new Date(currentStart.getTime() + meetingLengthInMsec);
			AvailableBlock smallBlock = createBlock(currentStart, newEndTime, largeBlock.getVisitorLimit(), largeBlock.getMeetingLocation());
			smallBlock.setVisitorsAttending(largeBlock.getVisitorsAttending());
			smallBlocks.add(smallBlock);
			currentStart = newEndTime;
		}
		return smallBlocks;
	}
	
	/**
	 * Expand a {@link Set} of {@link AvailableBlock} into a {@link SortedSet} of {@link AvailableBlock}s with a duration equal
	 * to the meetingLengthMinutes argument in minutes.
	 * 
	 * @param largeBlocks
	 * @return the new set
	 */
	public static SortedSet<AvailableBlock> expand(Set<AvailableBlock> largeBlocks, final int meetingLengthMinutes) {
		SortedSet<AvailableBlock> smallBlocks = new TreeSet<AvailableBlock>();
		for(AvailableBlock sourceBlock : largeBlocks) {
			smallBlocks.addAll(expand(sourceBlock, meetingLengthMinutes));
		}
		return smallBlocks;
	}
	
	/**
	 * Combine adjacent {@link AvailableBlock}s in the argument {@link Set}.
	 * 
	 * @param smallBlocks
	 * @return the new set
	 */
	public static SortedSet<AvailableBlock> combine(SortedSet<AvailableBlock> smallBlocks) {
		SortedSet<AvailableBlock> largeBlocks = new TreeSet<AvailableBlock>();
		
		Iterator<AvailableBlock> smallBlockIterator = smallBlocks.iterator();
		
		if(smallBlockIterator.hasNext()) {
			AvailableBlock current = smallBlockIterator.next();
			while(smallBlockIterator.hasNext()) {
				AvailableBlock next = smallBlockIterator.next();
				if(combinable(current, next)) {
					// current and next are adjacent AND have the same visitorLimit AND have same meetinglocation
					// update current to have current.startTime and next.endTime
					try {
						current = new AvailableBlock(current.getStartTime(), next.getEndTime(), current.getVisitorLimit(), current.getMeetingLocation());
					} catch (IllegalArgumentException e) {
						// could not create a block, what to do?
						LOG.error("failed to create an AvailableBlock from " + current.getStartTime() + " and " + next.getEndTime(), e);
					}
				} else {
					// current and next are either not adjacent or have a different visitorLimit
					// current should be pushed into largeBlocks
					largeBlocks.add(current);
					// current should now point to next
					current = next;
				}
			}
			// guarantee that current gets pushed into largeBlocks
			largeBlocks.add(current);
		}
		
		return largeBlocks;
	}
	
	/**
	 * 2 blocks are combinable if and onlyl if:
	 * <ol>
	 * <li>the end time of the left equals the start time of the right</li>
	 * <li>the visitor limits are equivalent</li>
	 * <li>the meeting locations are equivalent</li>
	 * </ol>
	 * 
	 * @see #safeMeetingLocationEquals(AvailableBlock, AvailableBlock)
	 * @param left
	 * @param right
	 * @return true if the 2 blocks can be combined
	 */
	static boolean combinable(AvailableBlock left, AvailableBlock right) {
		if(left == null || right == null) {
			return false;
		}
		return left.getEndTime().equals(right.getStartTime()) && 
			left.getVisitorLimit() == right.getVisitorLimit() &&
			safeMeetingLocationEquals(left, right);
	}
	/**
	 * Null safe equality test for {@link AvailableBlock#getMeetingLocation()
	 * @param left
	 * @param right
	 * @return true if the meetingLocation fields are equivalent
	 */
	static boolean safeMeetingLocationEquals(AvailableBlock left, AvailableBlock right) {
		final String leftLocation = left.getMeetingLocation();
		final String rightLocation = right.getMeetingLocation();
		if(leftLocation == null && rightLocation == null) {
			return true;
		}
		if(leftLocation != null) {
			return leftLocation.equals(rightLocation);
		}
		if(rightLocation != null) {
			return rightLocation.equals(leftLocation);
		}
		// not reachable?
		return false;
	}
	
	/**
	 * Returns a {@link List} of {@link Date} objects that fall between startDate and endDate and
	 * exist on the days specified by daysOfWeekPhrase.
	 * 
	 * For instance, passing "MWF", a start Date of June 30 2008, and an end Date of July 04 2008, this 
	 * method will return a list of 3 Date objects (one for Monday June 30, one for Wednesday July 2, and
	 * one for Friday July 4).
	 * 
	 * The time values for returned {@link Date}s will always be 00:00:00 (in the JVM's default timezone).
	 * 
	 * @param daysOfWeekPhrase
	 * @param startDate
	 * @param endDate
	 * @return a {@link List} of {@link Date} objects that fall between startDate and endDate and
	 * exist on the days specified by daysOfWeekPhrase.
	 */
	protected static List<Date> matchingDays(final String daysOfWeekPhrase, final Date startDate, final Date endDate) {
		List<Date> matchingDays = new ArrayList<Date>();
		
		Set<Integer> daysOfWeek = new HashSet<Integer>();
		for(char character : daysOfWeekPhrase.toUpperCase().toCharArray()) {
			switch(character) {
				case 'N':
					daysOfWeek.add(Calendar.SUNDAY);
					break;
				case 'M':
					daysOfWeek.add(Calendar.MONDAY);
					break;
				case 'T':
					daysOfWeek.add(Calendar.TUESDAY);
					break;
				case 'W':
					daysOfWeek.add(Calendar.WEDNESDAY);
					break;	
				case 'R':
					daysOfWeek.add(Calendar.THURSDAY);
					break;
				case 'F':
					daysOfWeek.add(Calendar.FRIDAY);
					break;
				case 'S':
					daysOfWeek.add(Calendar.SATURDAY);
					break;
			}	
		}
		
		Calendar current = Calendar.getInstance();
		current.setTime(startDate);
		// set the time to 00:00:00 to insure the time doesn't affect our comparison)
		// (because there may be a valid time window on endDate)
		current = CommonDateOperations.zeroOutTimeFields(current);
		
		while(current.getTime().compareTo(endDate) < 0) {
			if(daysOfWeek.contains(current.get(Calendar.DAY_OF_WEEK))) {
				matchingDays.add(current.getTime());
			}	
			// increment currentDate +1 day 
			current.add(Calendar.DATE, 1);
		}
		return matchingDays;
	}
	
	/**
	 * Parses the timePhrase (e.g. 9:30 AM) and updates the HOUR_OF_DAY and MINUTE fields on the toModify Calendar.
	 * 
	 * Mutates the toModify Calendar argument.
	 * 
	 * @param timePhrase
	 * @param toModify
	 * @throws InputFormatException
	 */
	protected static void interpretAndUpdateTime(final String timePhrase, final Calendar toModify) throws InputFormatException {
		Matcher matcher = TIME_PATTERN.matcher(timePhrase);
		if(matcher.matches()) {
			int endHour = Integer.parseInt(matcher.group(1));
			int endMinutes = Integer.parseInt(matcher.group(2));
			if(endHour == 12 && matcher.group(3).equalsIgnoreCase("am") ) {
				endHour = 0;
			}
			if(matcher.group(3).equalsIgnoreCase("pm") && endHour != 12) {
				endHour += 12;
			}
			toModify.set(Calendar.HOUR_OF_DAY, endHour);
			toModify.set(Calendar.MINUTE, endMinutes);
		} else {
			throw new InputFormatException(timePhrase + " does not match expected format of HH:MM AM/PM");
		}
	}
	
	/**
	 * Convert integer minutes to milliseconds (as long).
	 * 
	 * @param minutes
	 * @return the number of milliseconds in the minutes argument
	 */
	protected static long convertMinutesToMsec(final int minutes) {
		final long msecPerSecond = 1000;
		final long secondsPerMinute = 60;
		long result = ((long) minutes) * secondsPerMinute * msecPerSecond;
		return result;
	}
}
