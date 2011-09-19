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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.util.Calendars;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ConflictExistsException;
import org.jasig.schedassist.ICalendarDataDao;
import org.jasig.schedassist.NullAffiliationSourceImpl;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.impl.caldav.xml.ReportResponseHandlerImpl;
import org.jasig.schedassist.model.AppointmentRole;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableSchedule;
import org.jasig.schedassist.model.AvailableVersion;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IEventUtils;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.SchedulingAssistantAppointment;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ICalendarDataDao} for CalDAV-capable calendar servers.
 * 
 * Requires the following be provided (via setter injection):
 * <ul>
 * <li>{@link HttpClient} instance.</li>
 * <li>{@link Credentials} and {@link AuthScope} for authentication; will need authorization to alter any account's calendar.</li>
 * <li>{@link CaldavDialect} instance.</li>
 * </ul>
 * 
 * This instance constructs a {@link CaldavEventUtilsImpl} instance; if you need to override the {@link IEventUtils}
 * instance, a setter is provided ({@link #setEventUtils(IEventUtils)}).
 * 
 * Lastly this instance constructs a {@link NoopHttpMethodInterceptorImpl} instance; if you need to
 * override the {@link HttpMethodInterceptor} a setter is provided ({@link #setMethodInterceptor(HttpMethodInterceptor)}).
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CaldavCalendarDataDaoImpl.java 50 2011-05-05 21:07:25Z nblair $
 */
@Service
public class CaldavCalendarDataDaoImpl implements ICalendarDataDao, InitializingBean {

	private static final String CONTENT_LENGTH_HEADER = "Content-Length";
	private static final Header IF_NONE_MATCH_HEADER = new Header("If-None-Match", "*");
	private static final Header ICALENDAR_CONTENT_TYPE_HEADER = new Header("Content-Type", "text/calendar");
	private static final String IF_MATCH_HEADER = "If-Match";
	
	private static final Header DEPTH_HEADER = new Header("Depth", "1");
	protected final Log log = LogFactory.getLog(this.getClass());
	private HttpClient httpClient;
	private Credentials caldavAdminCredentials;
	private AuthScope caldavAdminAuthScope;
	private IEventUtils eventUtils = new CaldavEventUtilsImpl(new NullAffiliationSourceImpl());
	private CaldavDialect caldavDialect;
	private HttpMethodInterceptor methodInterceptor = new NoopHttpMethodInterceptorImpl();
	

	/**
	 * @param httpClient the httpClient to set
	 */
	@Autowired
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	/**
	 * @param caldavAdminCredentials the caldavAdminCredentials to set
	 */
	@Autowired
	public void setCaldavAdminCredentials(Credentials caldavAdminCredentials) {
		this.caldavAdminCredentials = caldavAdminCredentials;
	}
	/**
	 * @param caldavAdminAuthScope the caldavAdminAuthScope to set
	 */
	@Autowired
	public void setCaldavAdminAuthScope(AuthScope caldavAdminAuthScope) {
		this.caldavAdminAuthScope = caldavAdminAuthScope;
	}
	/**
	 * @param eventUtils the eventUtils to set
	 */
	@Autowired(required=false)
	public void setEventUtils(IEventUtils eventUtils) {
		this.eventUtils = eventUtils;
	}
	/**
	 * @param caldavDialect the caldavDialect to set
	 */
	@Autowired
	public void setCaldavDialect(CaldavDialect caldavDialect) {
		this.caldavDialect = caldavDialect;
	}
	/**
	 * @param methodInterceptor the methodInterceptor to set
	 */
	@Autowired(required=false)
	public void setMethodInterceptor(HttpMethodInterceptor methodInterceptor) {
		this.methodInterceptor = methodInterceptor;
	}
	/**
	 * Injects the {@link Credentials} and {@link AuthScope} into the
	 * {@link HttpClient}'s {@link HttpState}.
	 * This task is performed in afterPropertiesSet because {@link HttpState#setCredentials(AuthScope, Credentials)}'s
	 * method signature is not supported by Spring's DI.
	 * 
	 * @see HttpState#setCredentials(AuthScope, Credentials)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		httpClient.getState().setCredentials(
				caldavAdminAuthScope,
				caldavAdminCredentials); 
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarDataDao#getCalendar(org.jasig.schedassist.model.ICalendarAccount, java.util.Date, java.util.Date)
	 */
	@Override
	public Calendar getCalendar(ICalendarAccount calendarAccount,
			Date startDate, Date endDate) {
		List<CalendarWithURI> calendars = getCalendarsInternal(calendarAccount, startDate, endDate);
		Calendar result = consolidate(calendars);
		return result;
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarDataDao#getExistingAppointment(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.AvailableBlock)
	 */
	@Override
	public VEvent getExistingAppointment(IScheduleOwner owner,
			AvailableBlock block) {
		CalendarWithURI calendarWithUri = getExistingAppointmentInternal(owner, block.getStartTime(), block.getEndTime());
		if(null != calendarWithUri) {
			ComponentList componentList = calendarWithUri.getCalendar().getComponents(VEvent.VEVENT);
			return (VEvent) componentList.get(0);
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarDataDao#createAppointment(org.jasig.schedassist.model.IScheduleVisitor, org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.AvailableBlock, java.lang.String)
	 */
	@Override
	public VEvent createAppointment(IScheduleVisitor visitor,
			IScheduleOwner owner, AvailableBlock block, String eventDescription) {
		VEvent event = this.eventUtils.constructAvailableAppointment(
				block, 
				owner,
				visitor, 
				eventDescription);

		String uri = generateEventUri(owner, event);
		PutMethod method = new PutMethod(uri);
		method.addRequestHeader(IF_NONE_MATCH_HEADER);
		method.addRequestHeader(ICALENDAR_CONTENT_TYPE_HEADER);
		RequestEntity requestEntity = caldavDialect.generatePutAppointmentRequestEntity(event);
		method.setRequestEntity(requestEntity);
		
		if(log.isDebugEnabled()) {
			log.debug("createAppointment executing " + methodToString(method) + " for " + owner + ", " + visitor + ", " + block);
		}
		HttpMethod toExecute = this.methodInterceptor.doWithMethod(method, owner.getCalendarAccount());
		
		try {
			int statusCode = this.httpClient.executeMethod(toExecute);
			if(log.isDebugEnabled()) {
				log.debug("createAppointment status code: " + statusCode);
				InputStream content = method.getResponseBodyAsStream();
				log.debug("createAppointment response body: " + IOUtils.toString(content));
			}
			if(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
				return event;
			} else {
				throw new CaldavDataAccessException("createAppointment for " + visitor + ", " + owner + ", " + block + " failed with unexpected status code: " + statusCode);
			}
		} catch (HttpException e) {
			log.error("an HttpException occurred in createAppointment for " + owner + ", " + visitor + ", " + block);
			throw new CaldavDataAccessException(e);
		} catch (IOException e) {
			log.error("an IOException occurred in createAppointment for " + owner + ", " + visitor + ", " + block);
			throw new CaldavDataAccessException(e);
		} 
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarDataDao#cancelAppointment(org.jasig.schedassist.model.IScheduleOwner, net.fortuna.ical4j.model.component.VEvent)
	 */
	@Override
	public void cancelAppointment(IScheduleOwner owner, VEvent appointment) {
		Date startTime = appointment.getStartDate().getDate();
		Date endTime = appointment.getEndDate(true).getDate();

		CalendarWithURI calendar = getExistingAppointmentInternal(owner, startTime, endTime);
		if(null != calendar) {
			URI uri = this.caldavDialect.resolveCalendarURI(calendar);
			DeleteMethod method = new DeleteMethod(uri.toString());
			if(log.isDebugEnabled()) {
				log.debug("cancelAppointment executing " + methodToString(method) + " for " + owner + ", " + startTime);
			}
			HttpMethod toExecute = methodInterceptor.doWithMethod(method,owner.getCalendarAccount());
			try {
				int statusCode = this.httpClient.executeMethod(toExecute);
				log.debug("cancelAppointment status code: " + statusCode);
				if(statusCode == HttpStatus.SC_NO_CONTENT) {
					return;
				} else {
					throw new CaldavDataAccessException("cancelAppointment for " + owner + ", " + startTime +  ", " + endTime +" failed with unexpected status code: " + statusCode);
				}
			} catch (HttpException e) {
				log.error("an HttpException occurred in cancelAppointment for " + owner + ", " + startTime);
				throw new CaldavDataAccessException(e);
			} catch (IOException e) {
				log.error("an IOException occurred in leaveAppointment for " + owner + ", " + startTime);
				throw new CaldavDataAccessException(e);
			} 
		} else {
			log.warn("cannot cancelAppointment for " + owner + ", no matching appointment found (" + appointment + ")");
		}
	}

	
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarDataDao#joinAppointment(org.jasig.schedassist.model.IScheduleVisitor, org.jasig.schedassist.model.IScheduleOwner, net.fortuna.ical4j.model.component.VEvent)
	 */
	@Override
	public VEvent joinAppointment(IScheduleVisitor visitor,
			IScheduleOwner owner, VEvent appointment)
	throws SchedulingException {
		Date startTime = appointment.getStartDate().getDate();
		Date endTime = appointment.getEndDate(true).getDate();

		CalendarWithURI calendar = getExistingAppointmentInternal(owner, startTime, endTime);
		if(null != calendar) {
			VEvent event = extractSchedulingAssistantAppointment(calendar);
			
			Attendee attendee = this.eventUtils.constructAvailableAttendee(visitor.getCalendarAccount(), AppointmentRole.VISITOR);
			event.getProperties().add(attendee);
			
			URI uri = this.caldavDialect.resolveCalendarURI(calendar);
			PutMethod method = new PutMethod(uri.toString());
			method.addRequestHeader(IF_MATCH_HEADER, calendar.getEtag());
			method.addRequestHeader(ICALENDAR_CONTENT_TYPE_HEADER);
			RequestEntity requestEntity = caldavDialect.generatePutAppointmentRequestEntity(event);
			method.setRequestEntity(requestEntity);
			if(log.isDebugEnabled()) {
				log.debug("joinAppointment executing " + methodToString(method) + " for " + owner + ", " + visitor + ", " + startTime);
			}
			HttpMethod toExecute = methodInterceptor.doWithMethod(method, owner.getCalendarAccount());

			try {
				int statusCode = this.httpClient.executeMethod(toExecute);
				log.debug("joinAppointment status code: " + statusCode);
				if(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_NO_CONTENT) {
					return event;
				} else if (statusCode == HttpStatus.SC_PRECONDITION_FAILED) {
					// event changed in the interim, fail fast
					throw new SchedulingException("joinAppointment failed for " + visitor + " and " + owner + ", appointment was altered");
				} else {
					throw new CaldavDataAccessException("joinAppointment for " + visitor + ", " + owner + ", " + startTime + " failed with unexpected status code: " + statusCode);
				}
			} catch (HttpException e) {
				log.error("an HttpException occurred in joinAppointment for " + owner + ", " + visitor + ", " + startTime);
				throw new CaldavDataAccessException(e);
			} catch (IOException e) {
				log.error("an IOException occurred in joinAppointment for " + owner + ", " + visitor + ", " + startTime);
				throw new CaldavDataAccessException(e);
			} 
		} else {
			log.warn("cannot joinAppointment for " + owner + ", no matching appointment found (" + appointment + ")");
			throw new SchedulingException("joinAppointment failed for " + visitor + " and " + owner + ", no matching appointment found");
		}
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarDataDao#leaveAppointment(org.jasig.schedassist.model.IScheduleVisitor, org.jasig.schedassist.model.IScheduleOwner, net.fortuna.ical4j.model.component.VEvent)
	 */
	@Override
	public VEvent leaveAppointment(IScheduleVisitor visitor,
			IScheduleOwner owner, VEvent appointment)
	throws SchedulingException {
		Date startTime = appointment.getStartDate().getDate();
		Date endTime = appointment.getEndDate(true).getDate();

		CalendarWithURI calendar = getExistingAppointmentInternal(owner, startTime, endTime);
		if(null != calendar) {
			VEvent event = extractSchedulingAssistantAppointment(calendar);
			Property attendee = this.eventUtils.getAttendeeForUserFromEvent(event, visitor.getCalendarAccount());
			event.getProperties().remove(attendee);
			
			URI uri = this.caldavDialect.resolveCalendarURI(calendar);
			PutMethod method = new PutMethod(uri.toString());
			method.addRequestHeader(IF_MATCH_HEADER, calendar.getEtag());
			method.addRequestHeader(ICALENDAR_CONTENT_TYPE_HEADER);
			RequestEntity requestEntity = caldavDialect.generatePutAppointmentRequestEntity(event);
			method.setRequestEntity(requestEntity);
			if(log.isDebugEnabled()) {
				log.debug("leaveAppointment executing " + methodToString(method) + " for " + owner + ", " + visitor + ", " + startTime);
			}
			HttpMethod toExecute = methodInterceptor.doWithMethod(method, owner.getCalendarAccount());

			try {
				int statusCode = this.httpClient.executeMethod(toExecute);
				log.debug("leaveAppointment status code: " + statusCode);
				if(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_NO_CONTENT) {
					return event;
				} else if (statusCode == HttpStatus.SC_PRECONDITION_FAILED) {
					// event changed in the interim, fail fast
					throw new SchedulingException("leaveAppointment failed for " + visitor + " and " + owner + ", appointment was altered");
				} else {
					throw new CaldavDataAccessException("leaveAppointment for " + visitor + ", " + owner + ", " + startTime + " failed with unexpected status code: " + statusCode);
				}
			} catch (HttpException e) {
				log.error("an HttpException occurred in leaveAppointment for " + owner + ", " + visitor + ", " + startTime);
				throw new CaldavDataAccessException(e);
			} catch (IOException e) {
				log.error("an IOException occurred in leaveAppointment for " + owner + ", " + visitor + ", " + startTime);
				throw new CaldavDataAccessException(e);
			} 
		} else {
			log.warn("cannot leaveAppointment for " + owner + ", no matching appointment found (" + appointment + ")");
			throw new SchedulingException("leaveAppointment failed for " + visitor + " and " + owner + ", no matching appointment found");
		}
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarDataDao#checkForConflicts(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.AvailableBlock)
	 */
	@Override
	public void checkForConflicts(IScheduleOwner owner, AvailableBlock block)
	throws ConflictExistsException {
		List<CalendarWithURI> calendars = getCalendarsInternal(owner.getCalendarAccount(), block.getStartTime(), block.getEndTime());
		for(CalendarWithURI calendar: calendars) {
			ComponentList events = calendar.getCalendar().getComponents(VEvent.VEVENT);
			for(Object component : events) {
				VEvent event = (VEvent) component;
				if(this.eventUtils.willEventCauseConflict(owner.getCalendarAccount(), event)) {
					throw new ConflictExistsException("an appointment already exists for " + block);
				} 
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarDataDao#reflectAvailableSchedule(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.AvailableSchedule)
	 */
	@Override
	public void reflectAvailableSchedule(IScheduleOwner owner,
			AvailableSchedule schedule) {
		log.info("reflectAvailableSchedule unimplemented");
		if(schedule.isEmpty()) {
			return;
		}
		Date startDate = CommonDateOperations.beginningOfDay(schedule.getScheduleStartTime());
		Date endDate = CommonDateOperations.endOfDay(schedule.getScheduleEndTime());
		//purgeAvailableScheduleReflections(owner, startDate, endDate);
		
		//Calendar newReflections = this.eventUtils.convertScheduleForReflection(schedule);
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.ICalendarDataDao#purgeAvailableScheduleReflections(org.jasig.schedassist.model.IScheduleOwner, java.util.Date, java.util.Date)
	 */
	@Override
	public void purgeAvailableScheduleReflections(IScheduleOwner owner,
			Date startDate, Date endDate) {
		log.info("purgeAvailableScheduleReflections unimplemented");
	}

	/**
	 * This method is intended to generate a unique URI to use with the PUT method
	 * in {@link #createAppointment(IScheduleVisitor, IScheduleOwner, AvailableBlock, String)}.
	 * 
	 * It is implemented by the following:
	 * <pre>
	 caldavDialect.calculateCalendarAccountHome(owner.getCalendarAccount) + "/sched-assist-" + randomAlphanumeric + ".ics"
	 * </pre>
	 * @param owner
	 * @return
	 */
	protected String generateEventUri(IScheduleOwner owner, VEvent event) {
		Validate.notNull(event, "event argument cannot be null");
		Validate.notNull(event.getUid(), "cannot generateEventUri for event with null UID");
		String accountHome = this.caldavDialect.getCalendarAccountHome(owner.getCalendarAccount());

		StringBuilder eventUri = new StringBuilder(accountHome);
		eventUri.append(event.getUid().getValue());
		eventUri.append(".ics");
		return eventUri.toString();
	}

	/**
	 * 
	 * @param calendarAccount
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	protected List<CalendarWithURI> getCalendarsInternal(ICalendarAccount calendarAccount,
			Date startDate, Date endDate) {

		String accountUri = this.caldavDialect.getCalendarAccountHome(calendarAccount);
		RequestEntity requestEntity = caldavDialect.generateGetCalendarRequestEntity(startDate, endDate);
		ReportMethod method = new ReportMethod(accountUri);
		method.setRequestEntity(requestEntity);
		method.addRequestHeader(CONTENT_LENGTH_HEADER, Long.toString(requestEntity.getContentLength()));
		method.addRequestHeader(DEPTH_HEADER);
		if(log.isDebugEnabled()) {
			log.debug("getCalendarsInternal executing " + methodToString(method) + " for " + calendarAccount + ", start " + startDate + ", end " + endDate);
		}
		HttpMethod toExecute = methodInterceptor.doWithMethod(method,calendarAccount);
		try {
			int statusCode = this.httpClient.executeMethod(toExecute);
			log.debug("getCalendarsInternal status code: " + statusCode);
			if(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_MULTI_STATUS) {
				InputStream content = method.getResponseBodyAsStream();
				ReportResponseHandlerImpl reportResponseHandler = new ReportResponseHandlerImpl();
				List<CalendarWithURI> calendars = reportResponseHandler.extractCalendars(content);
				return calendars;
			} else {
				throw new CaldavDataAccessException("unexpected status code: " + statusCode);
			}
		} catch (HttpException e) {
			log.error("an HttpException occurred in getCalendarsInternal for " + calendarAccount + ", " + startDate + ", " + endDate);
			throw new CaldavDataAccessException(e);
		} catch (IOException e) {
			log.error("an IOException occurred in getCalendarsInternal for " + calendarAccount + ", " + startDate + ", " + endDate);
			throw new CaldavDataAccessException(e);
		} 
	}
	/**
	 * Consolidate the {@link Calendar}s within the argument, returning 1.
	 * 
	 * @see Calendars#merge(Calendar, Calendar)
	 * @param calendars
	 * @return never null
	 * @throws ParserException
	 */
	protected Calendar consolidate(List<CalendarWithURI> calendars) {
		final int size = calendars.size();
		if(size == 0) {
			return new Calendar();
		} else if(size == 1) {
			return calendars.get(0).getCalendar();
		} else if (size == 2) {
			return Calendars.merge(calendars.get(0).getCalendar(), calendars.get(1).getCalendar());
		} else {
			Calendar main = Calendars.merge(calendars.get(0).getCalendar(), calendars.get(1).getCalendar());
			for(int i = 2; i < size; i++) {
				main = Calendars.merge(main, calendars.get(i).getCalendar());
			}
			return main;
		}
	}

	/**
	 * This method returns the {@link CalendarWithURI} containing a single {@link VEvent} that
	 * was created with the Scheduling Assistant with the specified {@link IScheduleOwner} as the owner
	 * and the specified start and end times.
	 * 
	 * @param owner
	 * @param startTime
	 * @param endTime
	 * @return the matching Scheduling Assistant {@link VEvent}, or null if none for this {@link IScheduleOwner} at the specified times
	 */
	protected CalendarWithURI getExistingAppointmentInternal(IScheduleOwner owner,
			Date startTime, Date endTime) {
		final DateTime targetStartTime = new DateTime(startTime);
		final DateTime targetEndTime = new DateTime(endTime);

		List<CalendarWithURI> calendars = getCalendarsInternal(owner.getCalendarAccount(), startTime, endTime);
		for(CalendarWithURI calendarWithUri : calendars) {
			ComponentList componentList = calendarWithUri.getCalendar().getComponents(VEvent.VEVENT);
			if(componentList.size() != 1) {
				// scheduling assistant creates calendars with only a single event, short-circuit on calendars with > 1 events
				continue;
			}
			for(Object o: componentList) {
				VEvent event = (VEvent) o;
				Date eventStart = event.getStartDate().getDate();
				Date eventEnd = event.getEndDate(true).getDate();
				Property schedAssistProperty = event.getProperty(SchedulingAssistantAppointment.AVAILABLE_APPOINTMENT);
				if(!SchedulingAssistantAppointment.TRUE.equals(schedAssistProperty)) {
					// immediately skip over non-scheduling assistant appointments
					continue;
				}

				// TODO version check still needed in Jasig release?
				// check for version first
				Property versionProperty = event.getProperty(AvailableVersion.AVAILABLE_VERSION);
				if (AvailableVersion.AVAILABLE_VERSION_1_2.equals(versionProperty)) {
					Parameter ownerAttendeeRole = null;
					Property ownerAttendee = this.eventUtils.getAttendeeForUserFromEvent(event, owner.getCalendarAccount());
					if(ownerAttendee != null) {
						ownerAttendeeRole = ownerAttendee.getParameter(AppointmentRole.APPOINTMENT_ROLE);
					}

					if(null == ownerAttendeeRole) {
						// owner role not on organizer or on attendee
						continue;
					}
					// event has to be (1) an available appointment
					// with (2) owner as AppointmentRole#OWNER
					// (3) start and (4) end date have to match
					if(AppointmentRole.OWNER.equals(ownerAttendeeRole) &&
							eventStart.equals(targetStartTime) &&
							eventEnd.equals(targetEndTime)) {
						if(log.isDebugEnabled()) {
							log.debug("getExistingAppointmentInternal found " + event);
						}

						return calendarWithUri;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Method intended to pull the single {@link VEvent} from a 
	 * {@link CalendarWithURI} containing a scheduling assistant appointment.
	 * 
	 * @param calendar
	 * @return
	 */
	private VEvent extractSchedulingAssistantAppointment(CalendarWithURI calendar) {
		ComponentList events = calendar.getCalendar().getComponents(VEvent.VEVENT);
		Validate.isTrue(events.size() == 1, "expecting calendar with single event");
		return (VEvent) events.get(0);
	}
	
	private String methodToString(HttpMethod method) {
		StringBuilder result = new StringBuilder();
		result.append(method.getName());
		result.append(" ");
		result.append(method.getPath());
		return result.toString();
	}
}
