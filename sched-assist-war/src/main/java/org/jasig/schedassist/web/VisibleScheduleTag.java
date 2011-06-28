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


package org.jasig.schedassist.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableStatus;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.VisibleSchedule;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * JSP tag for rendering a {@link VisibleSchedule}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: VisibleScheduleTag.java 2527 2010-09-10 19:25:38Z npblair $
 */
public class VisibleScheduleTag extends RequestContextAwareTag {

	private static final long serialVersionUID = 53706L;
	
	private Log LOG = LogFactory.getLog(this.getClass());

	private VisibleSchedule visibleSchedule;
	private boolean previewMode = false;
	/**
	 * @param visibleSchedule the visibleSchedule to set
	 */
	public void setVisibleSchedule(final VisibleSchedule visibleSchedule) {
		this.visibleSchedule = visibleSchedule;
	}
	/**
	 * @param previewMode the previewMode to set
	 */
	public void setPreviewMode(boolean previewMode) {
		this.previewMode = previewMode;
	}
	/**
	 * 
	 * @return the {@link MessageSource}
	 */
	public MessageSource getMessageSource() {
		return getRequestContext().getMessageSource();
	}
	/**
	 * 
	 * @return the path to the silk icons directory (dependency on {@link ServletContext} of the request)
	 */
	protected String getSilkIconPrefix(ServletContext servletContext) {
		final String silkPrefix = servletContext.getContextPath() + "/rs/famfamfam/silk/1.3/";
		return silkPrefix;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
	 */
	@Override
	public int doStartTagInternal()  {
		final ServletContext servletContext = pageContext.getServletContext();
		
		final Date scheduleStart = visibleSchedule.getScheduleStart();
		if(null == scheduleStart) {
			// the visibleSchedule is empty, short circuit
			try {
				StringBuilder noappointments = new StringBuilder();
				noappointments.append("<span class=\"none-available\">");
				noappointments.append(getMessageSource().getMessage("no.available.appointments", null, null));
				noappointments.append("</span>");
				pageContext.getOut().write(noappointments.toString());
			} catch (IOException e) {
				LOG.error("IOException occurred in doStartTag", e);
			} 
			// SKIP_BODY means don't print any content from body of tag
			return SKIP_BODY;
		}
		
		LOG.debug("scheduleStart: " + scheduleStart);
		SortedMap<Date, List<AvailableBlock>> dailySchedules = new TreeMap<Date, List<AvailableBlock>>();
		Date index = DateUtils.truncate(scheduleStart, java.util.Calendar.DATE);
		Date scheduleEnd = visibleSchedule.getScheduleEnd();
		while(index.before(scheduleEnd)) {
			dailySchedules.put(index, new ArrayList<AvailableBlock>());
			index = DateUtils.addDays(index, 1);
		}
		final Date lastMapKey = dailySchedules.lastKey();
		LOG.debug("visibleSchedule spans " + dailySchedules.keySet().size() + " days");
		
		try {
			SortedMap<AvailableBlock, AvailableStatus> scheduleBlockMap = visibleSchedule.getBlockMap();
			int numberOfEventsToDisplay = 0;
			for(AvailableBlock block : scheduleBlockMap.keySet()) {
				Date eventStartDate = block.getStartTime();
				LOG.debug("event start date: " + eventStartDate);
				Date mapKey = DateUtils.truncate(eventStartDate, java.util.Calendar.DATE);
				if(CommonDateOperations.equalsOrAfter(eventStartDate, scheduleStart) && dailySchedules.containsKey(mapKey)) {
					dailySchedules.get(mapKey).add(block);
					numberOfEventsToDisplay++;
				}
			}
			LOG.debug("number of events to display: " + numberOfEventsToDisplay);
			if(numberOfEventsToDisplay == 0) {
				// no available times in this range!
				StringBuilder noappointments = new StringBuilder();
				noappointments.append("<span class=\"none-available\">");
				noappointments.append(getMessageSource().getMessage("no.available.appointments", null, null));
				noappointments.append("</span>");
				pageContext.getOut().write(noappointments.toString());
			} else {
				int weekNumber = 1;
				Date currentWeekStart = DateUtils.truncate(scheduleStart, java.util.Calendar.DATE);
				Date currentWeekFinish = DateUtils.addDays(currentWeekStart, CommonDateOperations.numberOfDaysUntilSunday(currentWeekStart));
				currentWeekFinish = DateUtils.addMinutes(currentWeekFinish, -1);
				
				boolean renderAnotherWeek = true;
				
				while(renderAnotherWeek) {
					if(LOG.isDebugEnabled()) {
						LOG.debug("will render another week using currentWeekStart " + currentWeekStart + " and currentWeekFinish " + currentWeekFinish);
					}
					SortedMap<Date, List<AvailableBlock>> subMap = dailySchedules.subMap(currentWeekStart, currentWeekFinish);
					renderWeek(servletContext, pageContext.getOut(), weekNumber++, subMap, scheduleBlockMap);
					
					currentWeekStart = DateUtils.addMinutes(currentWeekFinish, 1);
					currentWeekFinish = DateUtils.addDays(currentWeekStart, 7);
					currentWeekFinish = DateUtils.addMinutes(currentWeekFinish, -1);

					if(LOG.isDebugEnabled()) {
						LOG.debug("recalculated currentWeekStart " + currentWeekStart + ", currentWeekFinish " + currentWeekFinish);
					}
					
					if(currentWeekStart.after(lastMapKey)) {
						renderAnotherWeek = false;
						LOG.debug("will not render another week");
					}
				}
			}

		} catch (IOException e) {
			LOG.error("IOException occurred in doStartTag", e);
		}
		// SKIP_BODY means don't print any content from body of tag
		return SKIP_BODY;
	}
	
	/**
	 * Inspects the {@link List} of {@link AvailableBlock} for each map key between the start and end
	 * arguments. If any {@link List} for those keys is not empty, return true.
	 * 
	 * @param dailySchedules
	 * @param start
	 * @param end
	 * @return true if any blocks exist between start and end; false if none.
	 */
	protected boolean doesWeekHaveBlocks(Map<Date, List<AvailableBlock>> dailySchedules) {

		for(Map.Entry<Date, List<AvailableBlock>> entry: dailySchedules.entrySet()) {
			final List<AvailableBlock> daySchedule = entry.getValue();
			if(daySchedule.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Render a single week.
	 * 
	 * @param servletContext
	 * @param writer
	 * @param weekNumber
	 * @param dailySchedules
	 * @param scheduleBlockMap
	 * @throws IOException
	 */
	protected void renderWeek(final ServletContext servletContext, final JspWriter writer, final int weekNumber, 
			final SortedMap<Date, List<AvailableBlock>> dailySchedules,
			final SortedMap<AvailableBlock, AvailableStatus> scheduleBlockMap) throws IOException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("begin renderWeek for " + weekNumber);
		}
		final boolean hasBlocks = doesWeekHaveBlocks(dailySchedules);
		if(hasBlocks) {
			final SimpleDateFormat headFormat = new SimpleDateFormat("EEE M/d");
			writer.write("<div class=\"weekcontainer\" id=\"week" + weekNumber + "\">");
			for(Map.Entry<Date, List<AvailableBlock>> entry: dailySchedules.entrySet()) {
				final Date day = entry.getKey();
				final List<AvailableBlock> daySchedule = entry.getValue();
				if(LOG.isDebugEnabled()) {
					LOG.debug("in renderWeek weeknumber: " + weekNumber + ", day: " + day);
				}
				if(daySchedule.size() > 0) {
					writer.write("<div class=\"weekday\">");
					writer.write("<ul class=\"scheduleblocks\">");

					writer.write("<li class=\"dayhead\">");
					writer.write(headFormat.format(day));
					writer.write("</li>");
					for(AvailableBlock event : daySchedule) {
						AvailableStatus eventStatus = scheduleBlockMap.get(event);
						if(AvailableStatus.BUSY.equals(eventStatus)) {
							renderBusyBlock(servletContext, writer, event);
						} else if(AvailableStatus.FREE.equals(eventStatus)) {
							renderFreeBlock(servletContext, writer, event);
						} else if(AvailableStatus.ATTENDING.equals(eventStatus)) {
							renderAttendingBlock(servletContext, writer, event);
						}
					}

					writer.write("</ul>");
					writer.write("</div> <!-- end weekday -->");
				}
			}

			writer.write("</div> <!-- end weekcontainer -->");
		} else {
			if(LOG.isDebugEnabled()) {
				LOG.debug("renderWeek has no blocks for weekNumber: " + weekNumber);
			}
		}
	}
	/**
	 * Render a single {@link AvailableBlock} with {@link AvailableStatus#BUSY}.
	 * 
	 * @param servletContext
	 * @param writer
	 * @param event
	 * @throws IOException
	 */
	protected void renderBusyBlock(final ServletContext servletContext, final JspWriter writer, final AvailableBlock event) throws IOException {
		SimpleDateFormat idFormat = CommonDateOperations.getDateTimeFormat();
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		String busyTitle = getMessageSource().getMessage("busy.shortdescription", null, null);
		writer.write("<li id=\"" + idFormat.format(event.getStartTime()) + "\" class=\"busy\" title=\"" + busyTitle + "\">");
		writer.write("<img src=\"" + getSilkIconPrefix(servletContext) + "delete.png\" alt=\"\"/>&nbsp;");
		writer.write("<span id=\"" + idFormat.format(event.getStartTime()) + "-text\">");
		writer.write(timeFormat.format(event.getStartTime()));
		writer.write(" - ");
		writer.write(timeFormat.format(event.getEndTime()));
		writer.write("</span>");
		writer.write("</li>");
	}
	
	/**
	 * Render a single {@link AvailableBlock} with {@link AvailableStatus#FREE}.
	 * 
	 * @param servletContext
	 * @param writer
	 * @param event
	 * @throws IOException
	 */
	protected void renderFreeBlock(final ServletContext servletContext, final JspWriter writer, final AvailableBlock event) throws IOException {
		SimpleDateFormat stpFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
		SimpleDateFormat idFormat = CommonDateOperations.getDateTimeFormat();
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		SimpleDateFormat readableFormat = new SimpleDateFormat("EEE MMM d");
		String appointmentTitle;
		
		if(event.getVisitorLimit() > 1) {
			appointmentTitle = getMessageSource().getMessage("join.appointment.for", new Object[] { 
					readableFormat.format(event.getStartTime()),
					Integer.toString(event.getVisitorLimit() - event.getVisitorsAttending()),
					event.getVisitorLimit()
				}, null);
		} else {
			appointmentTitle = getMessageSource().getMessage("create.appointment.for", new Object[] {
					readableFormat.format(event.getStartTime())
				}, null);
		}
		writer.write("<li id=\"" + idFormat.format(event.getStartTime()) + "\" class=\"free\" title=\"" + appointmentTitle.toString() + "\">");
		if(!previewMode) {
			writer.write("<a href=\"create.html?startTime=" + stpFormat.format(event.getStartTime()) + "\">");
		}
		if(event.getVisitorLimit() > 1) {
			writer.write("<img src=\"" + getSilkIconPrefix(servletContext) + "group.png\" alt=\"\"/>&nbsp;");
		} else {
			writer.write("<img src=\"" + getSilkIconPrefix(servletContext) + "calendar_add.png\" alt=\"\"/>&nbsp;");
		}
		writer.write("<span id=\"" + idFormat.format(event.getStartTime()) + "-text\">");
		writer.write(timeFormat.format(event.getStartTime()));
		writer.write(" - ");
		writer.write(timeFormat.format(event.getEndTime()));
		writer.write("</span>");
		
		if(!previewMode) {
			writer.write("</a>");
		}
		writer.write("</li>");
	}
	/**
	 * Render a single {@link AvailableBlock} with {@link AvailableStatus#ATTENDING}.
	 * 
	 * @param writer
	 * @param event
	 * @throws IOException
	 */
	protected void renderAttendingBlock(final ServletContext servletContext, final JspWriter writer, final AvailableBlock event) throws IOException {
		SimpleDateFormat stpFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
		SimpleDateFormat idFormat = CommonDateOperations.getDateTimeFormat();
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		String startTimeFormatted = stpFormat.format(event.getStartTime());
		String endTimeFormatted = stpFormat.format(event.getEndTime());
		String cancelTitle = getMessageSource().getMessage("cancel.my.appointment", null, null);
		writer.write("<li id=\"" + idFormat.format(event.getStartTime()) + "\" class=\"attending\" title=\"" + cancelTitle + "\">");
		if(!previewMode) {
			writer.write("<a href=\"cancel.html?startTime=" + 
				startTimeFormatted + 
				"&endTime=" +
				endTimeFormatted + 
				"\">");
		}
		writer.write("<img src=\"" + getSilkIconPrefix(servletContext) + "calendar_delete.png\" alt=\"\"/>&nbsp;");
		writer.write("<span id=\"" + idFormat.format(event.getStartTime()) + "-text\">");
		writer.write(timeFormat.format(event.getStartTime()));
		writer.write(" - ");
		writer.write(timeFormat.format(event.getEndTime()));
		writer.write("</span>");
		if(!previewMode) {
			writer.write("</a>");
		}
		writer.write("</li>");
	}
	
}
