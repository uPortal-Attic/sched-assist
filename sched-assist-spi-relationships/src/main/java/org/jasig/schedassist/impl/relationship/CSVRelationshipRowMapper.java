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

package org.jasig.schedassist.impl.relationship;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * {@link RowMapper} for {@link CSVRelationship}s.
 * 
 * @author Nicholas Blair
 * @version $Id: CSVRelationshipRowMapper.java 147 2011-06-10 15:03:02Z npblair $
 */
public class CSVRelationshipRowMapper implements
		RowMapper<CSVRelationship> {

	/**
	 * Expects columns:
	 * 
	 * <pre>
	 owner_id
	 rel_description
	 visitor_id
	 </pre>
	 *
	 *  (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public CSVRelationship mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		CSVRelationship relationship = new CSVRelationship();
		relationship.setOwnerIdentifier(rs.getString("owner_id"));
		relationship.setRelationshipDescription(rs.getString("rel_description"));
		relationship.setVisitorIdentifier(rs.getString("visitor_id"));
		return relationship;
	}

}
