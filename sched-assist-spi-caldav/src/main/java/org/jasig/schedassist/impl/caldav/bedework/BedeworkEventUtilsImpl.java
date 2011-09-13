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

package org.jasig.schedassist.impl.caldav.bedework;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.XProperty;

import org.jasig.schedassist.IAffiliationSource;
import org.jasig.schedassist.impl.caldav.CaldavEventUtilsImpl;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;

/**
 * Bedework specific override for {@link CaldavEventUtilsImpl}.
 * 
 * @author Nicholas Blair
 * @version $Id: BedeworkEventUtilsImpl.java $
 */
public class BedeworkEventUtilsImpl extends CaldavEventUtilsImpl {

	public static final String BEDEWORK_SUBMITTEDBY = "X-BEDEWORK-SUBMITTEDBY";
	/**
	 * 
	 * @param affiliationSource
	 */
	public BedeworkEventUtilsImpl(IAffiliationSource affiliationSource) {
		super(affiliationSource);
	}

	/**
	 * Adds an {@link XProperty} with name {@link #BEDEWORK_SUBMITTEDBY} and value as the
	 * {@link IScheduleOwner}'s username.
	 * 
	 *  (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CaldavEventUtilsImpl#constructAvailableAppointment(org.jasig.schedassist.model.AvailableBlock, org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.IScheduleVisitor, java.lang.String)
	 */
	@Override
	public VEvent constructAvailableAppointment(AvailableBlock block,
			IScheduleOwner owner, IScheduleVisitor visitor,
			String eventDescription) {
		VEvent event = super.constructAvailableAppointment(block, owner, visitor,
				eventDescription);
		event.getProperties().add(new XProperty(BEDEWORK_SUBMITTEDBY, owner.getCalendarAccount().getUsername()));
		return event;
	}

}
