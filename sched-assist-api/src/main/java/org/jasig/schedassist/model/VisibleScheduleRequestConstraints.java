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
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Java bean to encapsulate the derived fields from a request to view
 * an {@link IScheduleOwner}'s visible schedule.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleScheduleDisplayRequestDetails.java $
 */
public class VisibleScheduleRequestConstraints implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2213895500433357816L;
	
	public static final int WEEKS_PER_PAGE = 4;
	private static Log LOG = LogFactory.getLog(VisibleScheduleRequestConstraints.class);
	
	private final Date targetStartDate;
	private final Date targetEndDate;
	private final Integer nextWeekIndex;
	private final Integer prevWeekIndex;
	private final int constrainedWeekStart;
	
	/**
	 * 
	 * @param targetStartDate
	 * @param targetEndDate
	 * @param nextWeekIndex
	 * @param prevWeekIndex
	 * @param constrainedWeekStart
	 */
	private VisibleScheduleRequestConstraints(Date targetStartDate,
			Date targetEndDate, Integer nextWeekIndex, Integer prevWeekIndex,
			int constrainedWeekStart) {
		this.targetStartDate = targetStartDate;
		this.targetEndDate = targetEndDate;
		this.nextWeekIndex = nextWeekIndex;
		this.prevWeekIndex = prevWeekIndex;
		this.constrainedWeekStart = constrainedWeekStart;
	}
	
	/**
	 * @return the targetStartDate
	 */
	public Date getTargetStartDate() {
		return targetStartDate;
	}

	/**
	 * @return the targetEndDate
	 */
	public Date getTargetEndDate() {
		return targetEndDate;
	}

	/**
	 * @return the nextWeekIndex
	 */
	public Integer getNextWeekIndex() {
		return nextWeekIndex;
	}

	/**
	 * @return the prevWeekIndex
	 */
	public Integer getPrevWeekIndex() {
		return prevWeekIndex;
	}

	/**
	 * @return the constrainedWeekStart
	 */
	public int getConstrainedWeekStart() {
		return constrainedWeekStart;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VisibleScheduleDisplayRequestDetails [targetStartDate=");
		builder.append(targetStartDate);
		builder.append(", targetEndDate=");
		builder.append(targetEndDate);
		builder.append(", nextWeekIndex=");
		builder.append(nextWeekIndex);
		builder.append(", prevWeekIndex=");
		builder.append(prevWeekIndex);
		builder.append(", constrainedWeekStart=");
		builder.append(constrainedWeekStart);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Use this method to construct a {@link VisibleScheduleRequestConstraints}, which 
	 * enforces all the appropriate constraints.
	 * 
	 * @param owner the target {@link IScheduleOwner} for the request
	 * @param weekStart an integer representing the number of weeks forward to start the request, 0 meaning "now"
	 * @return an appropriate VisibleScheduleRequestConstraints for the owner.
	 */
	public static VisibleScheduleRequestConstraints newInstance(IScheduleOwner owner, int weekStart) {
		VisibleWindow window = owner.getPreferredVisibleWindow();
		
		int constrainedWeekStart = constrainWeekStartToWindow(window, weekStart);
		
		Integer nextWeekIndex = calculateNextWeekIndex(window, constrainedWeekStart);
		Integer prevWeekIndex = calculatePrevWeekIndex(window, constrainedWeekStart);
		
		Date targetStartDate = resolveStartDate(new Date(), constrainedWeekStart);
		Date targetEndDate = resolveEndDate(window, targetStartDate);
		
		VisibleScheduleRequestConstraints result = new VisibleScheduleRequestConstraints(
				targetStartDate,
				targetEndDate, 
				nextWeekIndex, 
				prevWeekIndex,
				constrainedWeekStart);
		return result;
	}
	
	/**
	 * 
	 * @param window
	 * @param weekStart
	 * @return return a weekStart value that falls within the VisibleWindow argument
	 */
	protected static int constrainWeekStartToWindow(VisibleWindow window, int weekStart) {
		if(weekStart < 1) {
			//forcibly set weekStart to 1 for 0 and negative values
			return 0;
		} else if(weekStart > window.getWindowWeeksEnd()) {
			return window.getWindowWeeksEnd();
		} else {
			return weekStart;
		}
	}
	/**
	 * 
	 * @param window
	 * @param weekStart
	 * @return increment weekStart if possible; return null if incrementing would put weekStart outside visible window
	 */
	protected static Integer calculateNextWeekIndex(VisibleWindow window, int weekStart) {
		if(weekStart < 1) {
			//forcibly set weekStart to 1 for 0 and negative values
			weekStart = 0;
		}
		
		final int maxPreferredWeekStart = window.getWindowWeeksEnd();
		Integer result = null;
		if(window.getWindowWeeksEnd() > WEEKS_PER_PAGE && weekStart < maxPreferredWeekStart) {
			result = weekStart + WEEKS_PER_PAGE;
			if(result > maxPreferredWeekStart) {
				result = null;
			}
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("calculateNextWeekStart for " + window + ", weekStart=" + weekStart + " returns " + result);
		}
		return result;
	}
	
	/**
	 * 
	 * @param window
	 * @param weekStart
	 * @return decrement weekStart if possible; return null if decrementing would put weekStart outside visible window
	 */
	protected static Integer calculatePrevWeekIndex(VisibleWindow window, int weekStart) {
		Integer result = null;
		// ignore weekStart less than 1 and greater than to owner preferred visble window end
		if(weekStart > 0 && window.getWindowWeeksEnd() > WEEKS_PER_PAGE && weekStart < window.getWindowWeeksEnd()) {
			result = weekStart - WEEKS_PER_PAGE < 1 ? 0 : weekStart - WEEKS_PER_PAGE;
		} else if (weekStart == window.getWindowWeeksEnd()) {
			if(window.getWindowWeeksEnd() > WEEKS_PER_PAGE) {
				result = weekStart - WEEKS_PER_PAGE;
			}
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("calculatePrevWeekStart for " + window + ", weekStart=" + weekStart + " returns " + result);
		}
		return result;
	}
	
	/**
	 * 
	 * @param referencePoint
	 * @param weekStart
	 * @return add an appropriate number of days and weeks to the referencepoint based on the weekStart argument
	 */
	protected static Date resolveStartDate(Date referencePoint, int weekStart) {
		Date start = referencePoint;
		// weekStart <= 0 means "now"
		if(weekStart > 0) {
			// first roll start to sunday
			start = DateUtils.addDays(start, CommonDateOperations.numberOfDaysUntilSunday(start));
			// subtract 1 from weekStart
			start = DateUtils.addWeeks(start, weekStart - 1);
		}
		return start;
	}
	/**
	 * 
	 * @param window
	 * @param start
	 * @return an appropriate end date that falls within the visible window
	 */
	protected static Date resolveEndDate(VisibleWindow window, Date start) {
		Date preferredEnd = DateUtils.addWeeks(start, window.getWindowWeeksEnd());
		
		Date end = DateUtils.addWeeks(start, WEEKS_PER_PAGE);
		if(end.after(preferredEnd)) {
			return preferredEnd;
		} else {
			return end;
		}
	}
}
