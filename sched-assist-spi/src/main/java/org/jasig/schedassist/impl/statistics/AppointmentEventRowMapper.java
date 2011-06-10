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

import org.jasig.schedassist.impl.EventType;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.model.IScheduleOwner;
import org.springframework.jdbc.core.RowMapper;

/**
 * {@link RowMapper} for {@link AppointmentEvent}s.
 * 
 * Depends on an {@link OwnerDao}.
 * The {@link #mapRow(ResultSet, int)} implementation will try to attach the matching
 * {@link IScheduleOwner} (if still exists).
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AppointmentEventRowMapper.java 2208 2010-06-22 16:06:22Z npblair $
 */
public class AppointmentEventRowMapper implements RowMapper<AppointmentEvent> {

	private OwnerDao ownerDao;
	
	/**
	 * @param ownerDao
	 */
	public AppointmentEventRowMapper(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}

	/**
	 * Expects the following columns in the {@link ResultSet}:
	 * 
	 <pre>
	  event_id
	  owner_id
	  visitor_id,
	  event_type,
	  event_timestamp,
	  event_start 
	 </pre>
	 * 
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public AppointmentEvent mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		AppointmentEvent event = new AppointmentEvent();
		
		event.setEventId(rs.getLong("EVENT_ID"));
		final long ownerId = rs.getLong("OWNER_ID");
		event.setOwnerId(ownerId);
		IScheduleOwner owner = ownerDao.locateOwnerByAvailableId(ownerId);
		event.setScheduleOwner(owner);
		
		final String visitorId = rs.getString("VISITOR_ID");
		event.setVisitorId(visitorId);
		
		event.setEventType(EventType.valueOf(rs.getString("EVENT_TYPE")));
		event.setEventTimestamp(rs.getTimestamp("EVENT_TIMESTAMP"));
		event.setAppointmentStartTime(rs.getTimestamp("EVENT_START"));
		
		return event;
	}

}
