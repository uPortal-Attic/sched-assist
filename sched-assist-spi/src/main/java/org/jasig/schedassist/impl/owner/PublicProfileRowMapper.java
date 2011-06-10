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

import org.jasig.schedassist.model.PublicProfile;
import org.springframework.jdbc.core.RowMapper;

/**
 * {@link RowMapper} for {@link PublicProfile}s.
 *
 * @see PublicProfileIdRowMapper
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: PublicProfileRowMapper.java 2126 2010-05-19 16:38:32Z npblair $
 */
public class PublicProfileRowMapper implements RowMapper<PublicProfile> {

	private final PublicProfileIdRowMapper profileIdMapper = new PublicProfileIdRowMapper();
	/**
	 * Expects following columns (in addition to all columns expected by {@link PublicProfileIdRowMapper#mapRow(ResultSet, int)}:
	 <pre>
	 profile_description
	 noteboard
	 </pre>
	 * 
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public PublicProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
		PublicProfile profile = new PublicProfile();
		profile.setPublicProfileId(profileIdMapper.mapRow(rs, rowNum));	
		profile.setOwnerId(rs.getLong("owner_id"));
		profile.setDescription(rs.getString("profile_description"));
		profile.setOwnerNoteboard(rs.getString("noteboard"));
		return profile;
	}

}
