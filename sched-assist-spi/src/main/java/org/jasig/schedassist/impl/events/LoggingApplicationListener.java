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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * {@link ApplicationListener} that logs appointment creation, cancellation,
 * join, and leave events asynchronously.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: LoggingApplicationListener.java 2832 2010-11-02 17:07:37Z npblair $
 */
@Component
public class LoggingApplicationListener implements ApplicationListener<AbstractAppointmentEvent> {

	private Log LOG = LogFactory.getLog(this.getClass());
	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Async
	@Override
	public void onApplicationEvent(AbstractAppointmentEvent event) {
		if(event instanceof AppointmentCreatedEvent) {
			AppointmentCreatedEvent a = (AppointmentCreatedEvent) event;
			LOG.info("create appointment: " + a.getOwner() + ", " + a.getVisitor() + ", " + a.getBlock());
		} else if (event instanceof AppointmentCancelledEvent) {
			AppointmentCancelledEvent a = (AppointmentCancelledEvent) event;
			LOG.info("cancel appointment: " + a.getOwner() + ", " + a.getVisitor() + ", " + a.getBlock());
		} else if (event instanceof AppointmentJoinedEvent) {
			AppointmentJoinedEvent a = (AppointmentJoinedEvent) event;
			LOG.info("join appointment: " + a.getOwner() + ", " + a.getVisitor() + ", " + a.getBlock());
		} else if (event instanceof AppointmentLeftEvent) {
			AppointmentLeftEvent a = (AppointmentLeftEvent) event;
			LOG.info("left appointment: " + a.getOwner() + ", " + a.getVisitor() + ", " + a.getBlock());
		}
	}

}
