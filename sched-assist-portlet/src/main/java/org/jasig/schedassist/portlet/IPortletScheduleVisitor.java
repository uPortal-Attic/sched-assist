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

package org.jasig.schedassist.portlet;

import org.jasig.schedassist.model.IScheduleVisitor;

/**
 * Simple interface for representing a schedule visitor in the portlet,
 * where the full {@link IScheduleVisitor} is not needed/constructable.
 * 
 * @author Nicholas Blair
 * @version $Id: IPortletScheduleVisitor.java $
 */
public interface IPortletScheduleVisitor {

	/**
	 * Return a String that uniquely idenfies this visitor in the
	 * remote calendar service.
	 * This could be a username or an email address.
	 * 
	 * @return a string that uniquely identifies this visitor.
	 */
	String getAccountId();
	
	/**
	 * 
	 * @return true if this account is eligible for the remote calendar service
	 */
	boolean isEligible();
}
