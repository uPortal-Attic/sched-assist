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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
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
import org.springframework.web.servlet.ModelAndView;

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

	private Log LOG = LogFactory.getLog(this.getClass());
	
	private AvailableScheduleDao availableScheduleDao;
	
	private static final String BLOCK_DELIM = " x ";

	/**
	 * @param availableScheduleDao the availableScheduleDao to set
	 */
	@Autowired
	public void setAvailableScheduleDao(AvailableScheduleDao availableScheduleDao) {
		this.availableScheduleDao = availableScheduleDao;
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
				LOG.debug("ignoring unparseable startDate: " + startDate);
			}
		}

		Date weekStart = CommonDateOperations.calculateSundayPrior(startDate);
		Date weekEnd = DateUtils.addDays(weekStart, 6);
		model.addAttribute("weekStart", formatDate(weekStart));
		model.addAttribute("weekEnd", formatDate(weekEnd));
		
		AvailableSchedule schedule = availableScheduleDao.retrieveWeeklySchedule(owner, weekStart);
		List<String> formattedScheduleBlocks = formatSchedule(schedule);
		model.addAttribute("scheduleBlocks", formattedScheduleBlocks);
		
		response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
		response.setHeader("Expires", "Fri, 31 Jan 1997 05:00:00 GMT");
		return "jsonView";
	}
	
	/**
	 * Convert the {@link AvailableBlock}s from an {@link AvailableSchedule} 
	 * into a {@link List} of {@link String}s that are formatted as the 
	 * Javascript for the schedule view expects.
	 * 
	 * An example of the format for each string is:
	 <pre>
	 Mon0900 x 24 x Mon1500
	 </pre>
	 * This string starts at 9:00 AM Monday and ends at 3:00 PM Monday, and is made
	 * up of 24 15 minute blocks.
	 * The "x" characters are used as delimiters.
	 * 
	 * @param schedule
	 * @return
	 */
	public static List<String> formatSchedule(final AvailableSchedule schedule) {
		List<String> results = new ArrayList<String>();
		
		// insure that the blocks are combined
		Set<AvailableBlock> combined = AvailableBlockBuilder.combine(schedule.getAvailableBlocks());
		for(AvailableBlock block : combined) {
			String blockString = convertBlock(block);
			results.add(blockString.toString());
		}
		return results;
	}

	/**
	 * Convert an {@link AvailableBlock} to a formatted String, like:
	 <pre>
	 Mon0900 x 24 x 1
	 </pre>
	 *
	 * The view currently only displays blocks from 7:00 AM until 5:00 PM (last id is 1645).
	 * The last number represents the visitor limit (only 1 guest in this example).
	 * 
	 * The max number of displayed 15 minute increments is 40.
	 * 
	 * @param block
	 * @return
	 */
	public static String convertBlock(final AvailableBlock block) {
		SimpleDateFormat blockPrintFormat = new SimpleDateFormat("EEEHHmm");
		// expand the block to 15 minute chunks and get the size
		Set<AvailableBlock> expanded = AvailableBlockBuilder.expand(block, 15);
		int blockSize = expanded.size();
		
		StringBuilder blockString = new StringBuilder();
		Date rounded = roundDownToNearest15(block.getStartTime());
		if(!block.getStartTime().equals(rounded)) {
			// start time was rounded
			// since we're showing an earlier start time, we have to add a block to get the right end date
			blockSize++;
		}
		
		// put it all together
		// "Mon0900"
		blockString.append(blockPrintFormat.format(rounded));
		// " x "
		blockString.append(BLOCK_DELIM);
		// "24"
		blockString.append(blockSize);
		// " x "
		blockString.append(BLOCK_DELIM);
		// "1"
		blockString.append(block.getVisitorLimit());
		
		return blockString.toString();
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
}
