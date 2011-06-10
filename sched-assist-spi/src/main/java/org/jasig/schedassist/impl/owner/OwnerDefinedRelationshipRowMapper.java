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
 * RowMapper for {@link OwnerDefinedRelationship} objects.
 * Expects the following column names (all strings) :
 <ul>
 <li>owner_username</li>
 <li>relationship</li>
 <li>visitor_username</li>
 </ul>
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: OwnerDefinedRelationshipRowMapper.java 1919 2010-04-14 21:19:48Z npblair $
 */
public class OwnerDefinedRelationshipRowMapper implements
		RowMapper<OwnerDefinedRelationship> {

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java.sql.ResultSet, int)
	 */
	public OwnerDefinedRelationship mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		OwnerDefinedRelationship relationship = new OwnerDefinedRelationship();
		relationship.setOwnerUsername(rs.getString("owner_username"));
		relationship.setRelationship(rs.getString("relationship"));
		relationship.setVisitorUsername(rs.getString("visitor_username"));
		return relationship;
	}

}
