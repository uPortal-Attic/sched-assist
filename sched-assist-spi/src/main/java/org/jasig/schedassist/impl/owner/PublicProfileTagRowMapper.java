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

import org.jasig.schedassist.model.PublicProfileTag;
import org.springframework.jdbc.core.RowMapper;

/**
 * {@link RowMapper} for {@link PublicProfileTag}.
 * 
 * @author Nicholas Blair
 * @version $Id: PublicProfileTagRowMapper.java $
 */
public class PublicProfileTagRowMapper implements RowMapper<PublicProfileTag> {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public PublicProfileTag mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		PublicProfileTag result = new PublicProfileTag();
		result.setProfileKey(rs.getString("profile_key"));
		result.setTagDisplay(rs.getString("tag_display"));
		return result;
	}

}
