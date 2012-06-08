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


package org.jasig.schedassist.web.owner.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.impl.owner.AvailableScheduleDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * {@link Controller} implementation that returns the {@link IScheduleOwner}'s
 * {@link AvailableSchedule} for the 7 days starting at the "startDate" parameter.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableScheduleDataController.java 2424 2010-08-30 20:57:23Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/schedule-data.json","/delegate/schedule-data.json" })
public class AvailableScheduleDataController {

	protected final Log log = LogFactory.getLog(this.getClass());
	
	private AvailableScheduleDao availableScheduleDao;

	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(AvailableScheduleDao availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
	}

	/**
	 * @return the availableScheduleDao
	 */
	public AvailableScheduleDao getAvailableScheduleDao() {
		return availableScheduleDao;
	}

	/**
	 * 
	 * @param startParam
	 * @param response
	 * @return
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String getAvailableSchedule(@RequestParam(value="startDate",required=false) String startParam,
			final ModelMap model, HttpServletResponse response) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		
		Date startDate = new Date();
		if(StringUtils.isNotBlank(startParam)) {
			SimpleDateFormat df = CommonDateOperations.getDateFormat();
			try {
				startDate = df.parse(startParam);
			} catch (ParseException e) {
				log.debug("ignoring unparseable startDate: " + startDate);
			}
		}

		Date weekStart = CommonDateOperations.calculateSundayPrior(startDate);
		Date weekEnd = DateUtils.addDays(weekStart, 6);
		model.addAttribute("weekStart", formatDate(weekStart));
		model.addAttribute("weekEnd", formatDate(weekEnd));
		
		AvailableSchedule schedule = availableScheduleDao.retrieveWeeklySchedule(owner, weekStart);
		model.addAttribute("scheduleBlocks", formatJson(schedule));
		model.addAttribute("defaultMeetingLocation", owner.getPreferredLocation());
		response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
		response.setHeader("Expires", "Fri, 31 Jan 1997 05:00:00 GMT");
		return "jsonView";
	}
	
	/**
	 * 
	 * @param schedule
	 * @return
	 */
	public static List<AvailableBlockJsonRepresentation> formatJson(final AvailableSchedule schedule) {
		List<AvailableBlockJsonRepresentation> results = new ArrayList<AvailableScheduleDataController.AvailableBlockJsonRepresentation>();
		// insure that the blocks are combined
		Set<AvailableBlock> combined = AvailableBlockBuilder.combine(schedule.getAvailableBlocks());
		for(AvailableBlock block : combined) {
			results.add(new AvailableBlockJsonRepresentation(block));
		}
		
		return results;
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static Date roundDownToNearest15(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		int minutesField = cal.get(Calendar.MINUTE);
		int toRemove = minutesField % 15;
		
		Date result = date;
		if(toRemove != 0) {
			result = DateUtils.addMinutes(result, -toRemove);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	private String formatDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy");
		return df.format(date);
	}
	
	/**
	 * Simpler representation of an {@link AvailableBlock} tailored for JSON.
	 * 
	 * @author Nicholas Blair, npblair@wisc.edu
	 *
	 */
	public static class AvailableBlockJsonRepresentation {
		
		public static final String DATETIME_FORMAT = "EEEHHmm";
		static final FastDateFormat dateFormat = FastDateFormat.getInstance(DATETIME_FORMAT);
		private final AvailableBlock block;
		private final Date startTimeRounded;

		/**
		 * @param block
		 */
		public AvailableBlockJsonRepresentation(AvailableBlock block) {
			this.block = block;
			this.startTimeRounded = roundDownToNearest15(block.getStartTime());
		}
		
		/**
		 * 
		 * @return the formatted start time of the block
		 */
		public String getStartTime() {
			return dateFormat.format(startTimeRounded);
		}
		
		/**
		 * 
		 * @return the duration of the block in 15 minute segments (e.g. ~block duration / 15)
		 */
		public int getDurationIn15Mins() {
			Set<AvailableBlock> expanded = AvailableBlockBuilder.expand(block, 15);
			int blockSize = expanded.size();
			
			if(!block.getStartTime().equals(startTimeRounded)) {
				// start time was rounded
				// since we're showing an earlier start time, we have to add a block to get the right end date
				blockSize++;
			}
			
			return blockSize;
		}
		
		/**
		 * 
		 * @return the block's visitorLimit
		 */
		public int getVisitorLimit() {
			return block.getVisitorLimit();
		}
		
		/**
		 * 
		 * @return the block's meetingLocation
		 */
		public String getMeetingLocation() {
			return block.getMeetingLocation();
		}
	}
}
