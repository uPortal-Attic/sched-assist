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

import java.util.List;

import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.Relationship;

/**
 * Interface defining operations for retrieving {@link Relationship}s
 * for {@link IScheduleOwner}s and {@link IScheduleVisitor}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: RelationshipDao.java 1900 2010-04-14 21:09:03Z npblair $
 */
public interface RelationshipDao {

	/**
	 * Return a {@link List} of {@link Relationship}s with
	 * the specified {@link IScheduleOwner}.
	 * 
	 * Do not return null; if there are no results for specified owner 
	 * return an empty {@link List}.
	 *  
	 * @param owner
	 * @return a never null, but possibly empty, {@link List} of {@link Relationship}s including the {@link IScheduleOwner}.
	 */
	List<Relationship> forOwner(IScheduleOwner owner);
	
	/**
	 * Return a {@link List} of {@link Relationship}s with
	 * the specified {@link IScheduleVisitor}.
	 * 
	 * Do not return null; if there are no results for specified visitor 
	 * return an empty {@link List}.
	 * 
	 * @param visitor
	 * @return a never null, but possibly empty, {@link List} of {@link Relationship}s including the {@link IScheduleVisitor}.
	 */
	List<Relationship> forVisitor(IScheduleVisitor visitor);
}
