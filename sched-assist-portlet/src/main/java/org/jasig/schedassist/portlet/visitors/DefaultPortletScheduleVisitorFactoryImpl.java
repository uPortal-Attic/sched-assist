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

package org.jasig.schedassist.portlet.visitors;

import javax.portlet.PortletRequest;

import org.jasig.schedassist.portlet.IPortletScheduleVisitor;
import org.jasig.schedassist.portlet.IPortletScheduleVisitorFactory;
import org.jasig.schedassist.portlet.PortletSchedulingAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Default {@link IPortletScheduleVisitorFactory} implementation.
 * The AccountId returned on the {@link IPortletScheduleVisitor} is sourced
 * from {@link PortletRequest#getRemoteUser()}.
 * 
 * @author Nicholas Blair
 */
public class DefaultPortletScheduleVisitorFactoryImpl implements
		IPortletScheduleVisitorFactory {

	private PortletSchedulingAssistantService schedulingAssistantService;
	
	/**
	 * @param schedulingAssistantService the schedulingAssistantService to set
	 */
	@Autowired
	public void setSchedulingAssistantService(
			@Qualifier("portlet") PortletSchedulingAssistantService schedulingAssistantService) {
		this.schedulingAssistantService = schedulingAssistantService;
	}
	/**
	 * @return the schedulingAssistantService
	 */
	public PortletSchedulingAssistantService getSchedulingAssistantService() {
		return schedulingAssistantService;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.portlet.IPortletScheduleVisitorFactory#getPortletScheduleVisitor(javax.portlet.PortletRequest)
	 */
	@Override
	public IPortletScheduleVisitor getPortletScheduleVisitor(
			PortletRequest portletRequest) {
		final String remoteUser = portletRequest.getRemoteUser();
		return doConstructVisitor(remoteUser);
	}

	/**
	 * Invoke {@link PortletSchedulingAssistantService#isEligible(String)} and construct
	 * a {@link DefaultPortletScheduleVisitorImpl} with the accountId and eligible result.
	 * 
	 * @param accountId
	 * @return the {@link IPortletScheduleVisitor}.
	 */
	protected IPortletScheduleVisitor doConstructVisitor(String accountId) {
		boolean eligible = schedulingAssistantService.isEligible(accountId);
		DefaultPortletScheduleVisitorImpl visitor = new DefaultPortletScheduleVisitorImpl(accountId, eligible);
		return visitor;
	}
}
