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


package org.jasig.schedassist.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * {@link AbstractAuthenticationProcessingFilter} implemented to inspect the REMOTE_USER environment
 * variable as the source for authentication.
 * 
 * This was developed against Pubcookie and will work for Shibboleth or cas
 * You need to add protection around the url:
 * 
 * web application context + "/security_check"
 * 
 * Where web application context is the location of your app ("/scheduling-assistant" by default).
 * 
 * @author Nicholas Blair
 * @version $Id: RemoteUserAuthenticationProcessingFilterImpl.java  $
 */
public class RemoteUserAuthenticationProcessingFilterImpl extends AbstractAuthenticationProcessingFilter {

	protected static final String DEFAULT_FILTER_PROCESSES_URL = "/security_check";

	protected RemoteUserAuthenticationProcessingFilterImpl() {
		super(DEFAULT_FILTER_PROCESSES_URL);
	}
	protected RemoteUserAuthenticationProcessingFilterImpl(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
		String username = request.getRemoteUser();
		String password = "";
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        authRequest.setDetails(authenticationDetailsSource.buildDetails((HttpServletRequest) request));

        return this.getAuthenticationManager().authenticate(authRequest);
	}

	
	/* (non-Javadoc)
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
	
}
