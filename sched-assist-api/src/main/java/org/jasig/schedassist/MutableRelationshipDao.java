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

package org.jasig.schedassist;

import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.Relationship;

/**
 * Extension of {@link RelationshipDao} interface that adds
 * functions for the Available system to create or destroy
 * {@link Relationship}s
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: MutableRelationshipDao.java 1900 2010-04-14 21:09:03Z npblair $
 */
public interface MutableRelationshipDao extends RelationshipDao {

	/**
	 * Create a relationship between owner and visitor with the 3rd argument
	 * being the relationship description.
	 * 
	 * @param owner
	 * @param visitor
	 * @param relationshipDescription
	 */
	Relationship createRelationship(IScheduleOwner owner, IScheduleVisitor visitor, String relationshipDescription);
	
	/**
	 * Destroy the existing relationship between the owner and visitor.
	 * 
	 * @param owner
	 * @param visitor
	 */
	void destroyRelationship(IScheduleOwner owner, IScheduleVisitor visitor);
}
