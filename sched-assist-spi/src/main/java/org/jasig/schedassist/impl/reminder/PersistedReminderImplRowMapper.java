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

package org.jasig.schedassist.impl.reminder;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * {@link RowMapper} for persisted reminders.
 *
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DefaultReminderServiceImpl.java 3070 2011-02-09 13:53:34Z npblair $
 */
class PersistedReminderImplRowMapper implements RowMapper<PersistedReminderImpl> {

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public PersistedReminderImpl mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		PersistedReminderImpl result = new PersistedReminderImpl();
		result.setBlockEndTime(rs.getTimestamp("event_end"));
		result.setBlockStartTime(rs.getTimestamp("event_start"));
		result.setOwnerId(rs.getLong("owner_id"));
		result.setRecipientId(rs.getString("recipient"));
		result.setReminderId(rs.getLong("reminder_id"));
		result.setSendTime(rs.getTimestamp("send_time"));
		return result;
	}
}