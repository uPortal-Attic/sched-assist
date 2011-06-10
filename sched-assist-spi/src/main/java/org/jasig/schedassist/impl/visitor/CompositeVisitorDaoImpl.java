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

package org.jasig.schedassist.impl.visitor;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleVisitor;

/**
 * Composite implementation of {@link VisitorDao}; depends on an
 * arbitrary number of {@link VisitorDao}s. 
 * Returns the first result; if all configured {@link VisitorDao}s throw
 * {@link NotAVisitorException} a new {@link NotAVisitorException} will be thrown.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CompositeVisitorDaoImpl.java 1917 2010-04-14 21:18:41Z npblair $
 */
public final class CompositeVisitorDaoImpl implements VisitorDao {

	private Log LOG = LogFactory.getLog(this.getClass());
	private List<VisitorDao> visitorDaos = Collections.emptyList();
	
	/**
	 * @param visitorDaos the visitorDaos to set
	 */
	public void setVisitorDaos(List<VisitorDao> visitorDaos) {
		this.visitorDaos = visitorDaos;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.visitor.VisitorDao#toVisitor(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public IScheduleVisitor toVisitor(final ICalendarAccount calendarUser)
			throws NotAVisitorException {
		for(VisitorDao component : visitorDaos) {
			try {
				IScheduleVisitor visitor = component.toVisitor(calendarUser);
				return visitor;
			} catch (NotAVisitorException e) {
				LOG.debug("caught NotAVisitorException for " + calendarUser + " from component " + component);
			}
		}
		throw new NotAVisitorException("no configured visitorDaos are able to return a ScheduleVisitor for " + calendarUser);
	}

}
