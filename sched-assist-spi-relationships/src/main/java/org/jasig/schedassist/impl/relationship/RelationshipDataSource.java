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

import java.util.Date;

/**
 * Interface describing methods for an external source of relationship data that
 * needs to be stored locally in the Scheduling Assistant database.
 * 
 * Not all sources of relationship data need to be stored locally. Before implementing
 * this interface, consider implementing an on-demand {@link RelationshipDao} instead.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: RelationshipDataSource.java 2425 2010-09-01 14:41:32Z npblair $
 */
public interface RelationshipDataSource {

	/**
	 * Reload the data source into the scheduling assistant database.
	 * Use with caution: implementations may be synchronized and/or resource intensive.
	 */
	public void reloadData();
	
	/**
	 * 
	 * @return the {@link Date} the relationship data was last reloaded, or null if not yet loaded
	 */
	public Date getLastReloadTimestamp();
}