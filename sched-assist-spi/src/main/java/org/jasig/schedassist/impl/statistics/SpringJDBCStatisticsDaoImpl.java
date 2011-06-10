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

package org.jasig.schedassist.impl.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.impl.EventType;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.model.CommonDateOperations;
import org.jasig.schedassist.model.IScheduleOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 *  {@link SimpleJdbcTemplate} backed {@link StatisticsDao}.
 *  Requires a {@link DataSource} and an {@link OwnerDao} be set.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SpringJDBCStatisticsDaoImpl.java 2321 2010-07-30 17:32:27Z npblair $
 */
@Service("statisticsDao")
public class SpringJDBCStatisticsDaoImpl implements StatisticsDao {

	private static final Log LOG = LogFactory.getLog(SpringJDBCStatisticsDaoImpl.class);
	private OwnerDao ownerDao;
	private SimpleJdbcTemplate simpleJdbcTemplate;
	/**
	 * 
	 * @param dataSource
	 */
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.statistics.StatisticsDao#getEventCounts(java.util.Date, java.util.Date)
	 */
	@Override
	public List<DailyEventSummary> getEventCounts(Date startTime, Date endTime) {
		List<DailyEventSummary> dailyCounts = this.simpleJdbcTemplate.query(
				"select month,day,year,count(*) as num_events from (select to_char(event_timestamp,'MM') as month, to_char(event_timestamp,'DD') as day, to_char(event_timestamp,'YYYY') as year from EVENT_STATISTICS where EVENT_TIMESTAMP >= ? and EVENT_TIMESTAMP <= ?) group by year,month,day order by year,month,day",
			new DailyEventSummaryRowMapper(),
			startTime,
			endTime);
		
		return dailyCounts;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.statistics.StatisticsDao#getEventCounts(java.util.Date, java.util.Date, org.jasig.schedassist.impl.EventType)
	 */
	@Override
	public List<DailyEventSummary> getEventCounts(Date startTime, Date endTime,
			EventType eventType) {
		List<DailyEventSummary> dailyCounts = this.simpleJdbcTemplate.query(
				"select month,day,year,count(*) as num_events from (select to_char(event_timestamp,'MM') as month, to_char(event_timestamp,'DD') as day, to_char(event_timestamp,'YYYY') as year from EVENT_STATISTICS where EVENT_TIMESTAMP >= ? and EVENT_TIMESTAMP <= ? and EVENT_TYPE = ?) group by year,month,day order by year,month,day",
			new DailyEventSummaryRowMapper(),
			startTime,
			endTime,
			eventType.toString());
		
		return dailyCounts;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.statistics.StatisticsDao#getEvents(java.util.Date, java.util.Date)
	 */
	@Override
	public List<AppointmentEvent> getEvents(Date startTime, Date endTime) {
		List<AppointmentEvent> results = this.simpleJdbcTemplate.query(
				"select event_id,owner_id,visitor_id,event_type,event_timestamp,event_start from event_statistics where event_timestamp >= ? and event_timestamp <= ?",
				new AppointmentEventRowMapper(ownerDao), 
				startTime,
				endTime);
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.statistics.StatisticsDao#getEvents(org.jasig.schedassist.model.IScheduleOwner, java.util.Date, java.util.Date)
	 */
	@Override
	public List<AppointmentEvent> getEvents(final IScheduleOwner owner, final Date startTime, final Date endTime) {
		List<AppointmentEvent> results = this.simpleJdbcTemplate.query(
				"select event_id,owner_id,visitor_id,event_type,event_timestamp,event_start from event_statistics where owner_id = ? and event_timestamp >= ? and event_timestamp <= ?",
				new AppointmentEventRowMapper(ownerDao), 
				owner.getId(),
				startTime,
				endTime);	
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.statistics.StatisticsDao#getEvents(org.jasig.schedassist.model.IScheduleOwner, java.util.Date, java.util.Date, org.jasig.schedassist.impl.EventType)
	 */
	@Override
	public List<AppointmentEvent> getEvents(final IScheduleOwner owner, final Date startTime, final Date endTime,
			final EventType eventType) {
		List<AppointmentEvent> results = this.simpleJdbcTemplate.query(
				"select event_id,owner_id,visitor_id,event_type,event_timestamp,event_start from event_statistics where owner_id = ? and event_timestamp >= ? and event_timestamp <= ? and event_type = ?",
				new AppointmentEventRowMapper(ownerDao), 
				owner.getId(),
				startTime,
				endTime,
				eventType.toString());	
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.statistics.StatisticsDao#getEvents(org.jasig.schedassist.model.IScheduleOwner, java.util.Date, java.util.Date, java.util.List)
	 */
	@Override
	public List<AppointmentEvent> getEvents(IScheduleOwner owner,
			Date startTime, Date endTime, List<EventType> eventTypes) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("ownerId", owner.getId());
		parameterSource.addValue("startTime", startTime);
		parameterSource.addValue("endTime", endTime);
		parameterSource.addValue("eventTypes", eventTypes);
		List<AppointmentEvent> results = this.simpleJdbcTemplate.query(
				"select event_id,owner_id,visitor_id,event_type,event_timestamp,event_start from event_statistics where owner_id = :ownerId and event_timestamp >= :startTime and event_timestamp <= :endTime and event_type in (:eventTypes)",
				new AppointmentEventRowMapper(ownerDao), 
				parameterSource);
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.statistics.StatisticsDao#getEvents(org.jasig.schedassist.model.IScheduleOwner, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<AppointmentEvent> getEvents(final IScheduleOwner owner, final String visitorUsername, 
			final Date startTime, final Date endTime) {
		List<AppointmentEvent> results = this.simpleJdbcTemplate.query(
				"select event_id,owner_id,visitor_id,event_type,event_timestamp,event_start from event_statistics where owner_id = ? and visitor_id = ? and event_timestamp >= ? and event_timestamp <= ?",
				new AppointmentEventRowMapper(ownerDao), 
				owner.getId(),
				visitorUsername,
				startTime,
				endTime);	
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.statistics.StatisticsDao#getEvents(org.jasig.schedassist.model.IScheduleOwner, java.lang.String, java.util.Date, java.util.Date, org.jasig.schedassist.impl.EventType)
	 */
	@Override
	public List<AppointmentEvent> getEvents(final IScheduleOwner owner, final String visitorUsername, 
			final Date startTime,final Date endTime, final EventType eventType) {
		List<AppointmentEvent> results = this.simpleJdbcTemplate.query(
				"select event_id,owner_id,visitor_id,event_type,event_timestamp,event_start from event_statistics where owner_id = ? and visitor_id = ? and event_timestamp >= ? and event_timestamp <= ? and event_type = ?",
				new AppointmentEventRowMapper(ownerDao), 
				owner.getId(),
				visitorUsername,
				startTime,
				endTime,
				eventType.toString());	
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.statistics.StatisticsDao#getEvents(org.jasig.schedassist.model.IScheduleOwner, java.lang.String, java.util.Date, java.util.Date, java.util.List)
	 */
	@Override
	public List<AppointmentEvent> getEvents(final IScheduleOwner owner, final String visitorUsername, 
			final Date startTime, final Date endTime, final List<EventType> eventTypes) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("ownerId", owner.getId());
		parameterSource.addValue("visitorId", visitorUsername);
		parameterSource.addValue("startTime", startTime);
		parameterSource.addValue("endTime", endTime);
		parameterSource.addValue("eventTypes", eventTypes);
		List<AppointmentEvent> results = this.simpleJdbcTemplate.query(
				"select event_id,owner_id,visitor_id,event_type,event_timestamp,event_start from event_statistics where owner_id = :ownerId and visitor_id = :visitorId and event_timestamp >= :startTime and event_timestamp <= :endTime and event_type in (:eventTypes)",
				new AppointmentEventRowMapper(ownerDao), 
				parameterSource);
		return results;
	}
	
	/**
	 * {@link RowMapper} for {@link DailyEventSummary} objects.
	 *
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: SpringJDBCStatisticsDaoImpl.java 2321 2010-07-30 17:32:27Z npblair $
	 */
	protected static class DailyEventSummaryRowMapper implements RowMapper<DailyEventSummary> {

		/*
		 * (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		@Override
		public DailyEventSummary mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			DailyCount result = new DailyCount();
			result.setYear(rs.getString("year"));
			result.setMonth(rs.getString("month"));
			result.setDay(rs.getString("day"));
			result.setCount(rs.getLong("num_events"));
			return result.toDailyEventSummary();
		}
		
	}
	
	/**
	 * Inner bean to pair a count of events with a date.
	 *
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: SpringJDBCStatisticsDaoImpl.java 2321 2010-07-30 17:32:27Z npblair $
	 */
	private static class DailyCount {
		private String year;
		private String month;
		private String day;
		private long count;
		/**
		 * @return the count
		 */
		public long getCount() {
			return count;
		}
		/**
		 * @param year the year to set
		 */
		public void setYear(String year) {
			this.year = year;
		}
		/**
		 * @param month the month to set
		 */
		public void setMonth(String month) {
			this.month = month;
		}
		/**
		 * @param day the day to set
		 */
		public void setDay(String day) {
			this.day = day;
		}
		/**
		 * @param count the count to set
		 */
		public void setCount(long count) {
			this.count = count;
		}
		
		/**
		 * 
		 * @return
		 */
		public Date getDate() {
			if(year == null || month == null || day == null) {
				return null;
			}
			SimpleDateFormat df = CommonDateOperations.getDateFormat();
			try {
				Date date =  df.parse(year + month + day);
				return DateUtils.truncate(date, Calendar.DATE);
			} catch (ParseException e) {
				LOG.debug("failed to parse " + year + month + day, e);
				return null;
			}
		}
		
		/**
		 * 
		 * @return
		 */
		public DailyEventSummary toDailyEventSummary() {
			DailyEventSummary result = new DailyEventSummary();
			result.setDate(getDate());
			result.setEventCount(getCount());
			return result;
		}
	}
}
