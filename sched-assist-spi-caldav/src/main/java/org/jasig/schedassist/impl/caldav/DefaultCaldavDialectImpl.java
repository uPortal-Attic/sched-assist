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

package org.jasig.schedassist.impl.caldav;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Resource;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IEventUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class to encapsulate generation of the dialect between the scheduling assistant
 * and the CalDAV server.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DefaultCaldavDialectImpl.java 51 2011-05-06 14:35:33Z nblair $
 */
public class DefaultCaldavDialectImpl implements CaldavDialect{

	/**
	 * Date format expected by CalDAV servers.
	 */
	protected static final String DATE_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
	/**
	 * {@link ProdId} attached to {@link Calendar}s sent to the CalDAV server by the Scheduling Assistant.
	 */
	protected static final ProdId PROD_ID = new ProdId("-//jasig.org//Jasig Scheduling Assistant 1.0//EN");
	
	private URI caldavHost;
	private String accountHomePrefix = "/ucaldav/user/";
	private String accountHomeSuffix = "/calendar/";
	private IEventUtils eventUtils;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @return the accountHomePrefix
	 */
	public String getAccountHomePrefix() {
		return accountHomePrefix;
	}
	/**
	 * @param accountHomePrefix the accountHomePrefix to set
	 */
	public void setAccountHomePrefix(String accountHomePrefix) {
		this.accountHomePrefix = accountHomePrefix;
	}
	/**
	 * @return the accountHomeSuffix
	 */
	public String getAccountHomeSuffix() {
		return accountHomeSuffix;
	}
	/**
	 * @param accountHomeSuffix the accountHomeSuffix to set
	 */
	public void setAccountHomeSuffix(String accountHomeSuffix) {
		this.accountHomeSuffix = accountHomeSuffix;
	}
	
	
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavDialect#getCaldavHost()
	 */
	@Override
	public URI getCaldavHost() {
		return this.caldavHost;
	}
	/**
	 * Annotated with {@link Resource} to allow injection by name.
	 * 
	 * @param caldavHost the caldavHost to set
	 */
	@Resource
	public void setCaldavHost(URI caldavHost) {
		this.caldavHost = caldavHost;
	}
	/**
	 * @param eventUtils the eventUtils to set
	 */
	@Autowired
	public void setEventUtils(IEventUtils eventUtils) {
		this.eventUtils = eventUtils;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavDialect#generateGetCalendarRequestEntity(java.util.Date, java.util.Date)
	 */
	public RequestEntity generateGetCalendarRequestEntity(Date startDate, Date endDate) {
		final String content = generateGetCalendarRequestXML(startDate, endDate);
		
		StringRequestEntity requestEntity;
		try {
			requestEntity = new StringRequestEntity(content, "text/xml", "UTF-8");
			return requestEntity;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	/**
	 * Generate the XML that makes up a valid CalDAV calendar-query request body that would
	 * be issued with a REPORT request for Calendar events between the 2 {@link Date} arguments.
	 * 
	 * @param startDate
	 * @param endDate
	 * @return calendar-query XML content for REPORT request
	 */
	protected String generateGetCalendarRequestXML(Date startDate, Date endDate) {
		StringBuilder content = new StringBuilder();
		content.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		content.append("<C:calendar-query xmlns:D=\"DAV:\" xmlns:C=\"urn:ietf:params:xml:ns:caldav\">");
		content.append("  <D:prop>");
		content.append("    <D:getetag/>");
		content.append("    <C:calendar-data/>");
		content.append("  </D:prop>");
		content.append("  <C:filter>");
		content.append("    <C:comp-filter name=\"VCALENDAR\">");
		content.append("      <C:comp-filter name=\"VEVENT\">");
		content.append("        <C:time-range start=\"" + formatDateTime(startDate) + "\" end=\"" + formatDateTime(endDate) + "\"/>");
		content.append("      </C:comp-filter>");
		content.append("    </C:comp-filter>");
		content.append("  </C:filter>");
		content.append("</C:calendar-query>");
		String result = content.toString();
		log.debug(result);
		return result;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavDialect#generateCreateAppointmentRequestEntity(net.fortuna.ical4j.model.component.VEvent)
	 */
	public RequestEntity generatePutAppointmentRequestEntity(VEvent event) {
		final String requestEntityBody = this.eventUtils.wrapEventInCalendar(event).toString();
		try {
			StringRequestEntity requestEntity = new StringRequestEntity(requestEntityBody, "text/calendar", "UTF-8");
			return requestEntity;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavDialect#calculateCalendarAccountHome(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public String getCalendarAccountHome(ICalendarAccount calendarAccount) {
		Validate.notNull(calendarAccount, "calendarAccount argument must not be null");
		final String accountUsername = calendarAccount.getUsername();
		Validate.notNull(accountUsername, "username in calendarAccount argument must not be null");
		
		StringBuilder uri = new StringBuilder();
		uri.append(getCaldavHost().toString());
		uri.append(getAccountHomePrefix());
		uri.append(accountUsername);
		uri.append(getAccountHomeSuffix());
		return uri.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavDialect#resolveCalendarURI(org.jasig.schedassist.impl.caldav.CalendarWithURI)
	 */
	@Override
	public URI resolveCalendarURI(CalendarWithURI calendar) {
		Validate.notNull(calendar, "CalendarWithURI argument cannot be null");
		StringBuilder result = new StringBuilder();
		result.append(this.caldavHost.toString());
		result.append(calendar.getUri());
		URI uri;
		try {
			uri = new URI(result.toString());
			return uri;
		} catch (URISyntaxException e) {
			log.error("cannot construct uri from " + result.toString(), e);
			throw new IllegalStateException("cannot construct uri from " + result.toString(), e);
		}
	}
	/**
	 * Convert the date to a String using the format:
	 * <pre>
	 yyyyMMdd'T'HHmmssZ
	 </pre>
	 *
	 * @param date
	 * @return
	 */
	protected String formatDateTime(Date date) {
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df.format(date);
	}

}
