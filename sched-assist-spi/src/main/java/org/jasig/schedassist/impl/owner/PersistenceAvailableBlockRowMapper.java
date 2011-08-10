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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * {@link RowMapper} for {@link PersistenceAvailableBlock}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PersistenceAvailableBlockRowMapper.java 1919 2010-04-14 21:19:48Z npblair $
 */
class PersistenceAvailableBlockRowMapper implements
		RowMapper<PersistenceAvailableBlock> {

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java.sql.ResultSet, int)
	 */
	public PersistenceAvailableBlock mapRow(ResultSet rs, int rowNum) throws SQLException {
		PersistenceAvailableBlock schedule = new PersistenceAvailableBlock();
		schedule.setOwnerId(rs.getLong("owner_id"));
		schedule.setStartTime(rs.getTimestamp("start_time"));
		schedule.setEndTime(rs.getTimestamp("end_time"));
		int visitorLimit = rs.getInt("visitor_limit");
		if(visitorLimit == 0) {
			// visitor_limit column empty (undefined), set default (1)
			visitorLimit = 1;
		}
		schedule.setVisitorLimit(visitorLimit);
		schedule.setMeetingLocation(rs.getString("meeting_location"));
		return schedule;
	}

}
