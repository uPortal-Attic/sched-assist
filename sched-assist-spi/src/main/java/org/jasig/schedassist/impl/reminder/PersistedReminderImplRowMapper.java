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