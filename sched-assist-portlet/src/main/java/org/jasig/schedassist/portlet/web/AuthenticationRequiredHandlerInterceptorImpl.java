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

package org.jasig.schedassist.portlet.web;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.portlet.AuthenticationRequiredException;
import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.portlet.handler.HandlerInterceptorAdapter;

/**
 * Simple {@link HandlerInterceptor} implementation that will
 * throw {@link AuthenticationRequiredException}s in the {@link #preHandleAction(ActionRequest, ActionResponse, Object)}
 * and {@link #preHandleRender(RenderRequest, RenderResponse, Object)} methods if 
 * {@link PortletRequest#getRemoteUser()} returns null or an empty string.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AuthenticationRequiredHandlerInterceptorImpl.java 1712 2010-02-15 16:22:23Z npblair $
 */
public class AuthenticationRequiredHandlerInterceptorImpl extends
		HandlerInterceptorAdapter {

	private Log LOG = LogFactory.getLog(this.getClass());
	
	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.HandlerInterceptor#preHandleAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandleAction(ActionRequest request,
			ActionResponse response, Object handler) throws Exception {
		if(LOG.isDebugEnabled()) {
			LOG.debug("in preHandleAction, remoteUser: " + request.getRemoteUser());
		}
		if(StringUtils.isBlank(request.getRemoteUser())) {
			throw new AuthenticationRequiredException();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.HandlerInterceptor#preHandleRender(javax.portlet.RenderRequest, javax.portlet.RenderResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandleRender(RenderRequest request,
			RenderResponse response, Object handler) throws Exception {
		if(LOG.isDebugEnabled()) {
			LOG.debug("in preHandleRender, remoteUser: " + request.getRemoteUser());
		}
		if(StringUtils.isBlank(request.getRemoteUser())) {
			throw new AuthenticationRequiredException();
		}
		return true;
	}

}
