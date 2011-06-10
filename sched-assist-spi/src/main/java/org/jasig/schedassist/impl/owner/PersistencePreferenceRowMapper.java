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
 * {@link RowMapper} implementation for {@link PersistencePreference}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PersistencePreferenceRowMapper.java 1919 2010-04-14 21:19:48Z npblair $
 */
class PersistencePreferenceRowMapper implements
		RowMapper<PersistencePreference> {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java.sql.ResultSet, int)
	 */
	public PersistencePreference mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		PersistencePreference preference = new PersistencePreference();
		preference.setOwnerId(rs.getInt("owner_id"));
		preference.setPreferenceKey(rs.getString("preference_key"));
		preference.setPreferenceValue(rs.getString("preference_value"));
		return preference;
	}

}
