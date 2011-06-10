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

package org.jasig.schedassist.impl.events;

import org.jasig.schedassist.impl.AvailableScheduleReflectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * {@link ApplicationListener} for {@link AvailableScheduleChangedEvent}s.
 * 
 * @see AvailableScheduleReflectionService#reflectAvailableSchedule(org.jasig.schedassist.model.IScheduleOwner)
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AvailableScheduleChangedApplicationListener.java 2832 2010-11-02 17:07:37Z npblair $
 */
@Component
public class AvailableScheduleChangedApplicationListener implements
ApplicationListener<AvailableScheduleChangedEvent> {

	private AvailableScheduleReflectionService availableScheduleReflectionService;
	/**
	 * @param availableScheduleReflectionService the availableScheduleReflectionService to set
	 */
	@Autowired
	public void setAvailableScheduleReflectionService(
			AvailableScheduleReflectionService availableScheduleReflectionService) {
		this.availableScheduleReflectionService = availableScheduleReflectionService;
	}


	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(AvailableScheduleChangedEvent event) {
		this.availableScheduleReflectionService.reflectAvailableSchedule(event.getOwner());
	}

}
