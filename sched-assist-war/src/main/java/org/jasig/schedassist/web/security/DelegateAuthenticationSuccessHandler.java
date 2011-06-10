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

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * {@link AuthenticationSuccessHandler} for the delegation login form.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateAuthenticationSuccessHandler.java 2089 2010-05-03 19:04:26Z npblair $
 */
public final class DelegateAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private String delegateOwnerTarget;
	private String delegateRegisterTarget;
	private String logoutTarget;
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	/**
	 * @param delegateOwnerTarget the delegateOwnerTarget to set
	 */
	public void setDelegateOwnerTarget(String delegateOwnerTarget) {
		this.delegateOwnerTarget = delegateOwnerTarget;
	}
	/**
	 * @param delegateRegisterTarget the delegateRegisterTarget to set
	 */
	public void setDelegateRegisterTarget(String delegateRegisterTarget) {
		this.delegateRegisterTarget = delegateRegisterTarget;
	}
	/**
	 * @param logoutTarget the logoutTarget to set
	 */
	public void setLogoutTarget(String logoutTarget) {
		this.logoutTarget = logoutTarget;
	}
	/**
	 * @param redirectStrategy the redirectStrategy to set
	 */
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}


	/**
	 * Redirects to the correct url based on the {@link GrantedAuthority}s contained by the {@link Authentication} argument.
	 * 
	 * @see org.springframework.security.web.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
	 */
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
	throws IOException, ServletException {

		Collection<GrantedAuthority> authorities = authentication.getAuthorities();
		if(authorities.contains(SecurityConstants.DELEGATE_OWNER)) {
			// redirect to delegateOwnerTarget
			this.redirectStrategy.sendRedirect(request, response, this.delegateOwnerTarget);
		} else if(authorities.contains(SecurityConstants.DELEGATE_REGISTER)) {
			// redirect to delegateRegisterTarget
			this.redirectStrategy.sendRedirect(request, response, this.delegateRegisterTarget);
		} else {
			// this must be a logout success
			// redirect to logoutTarget
			this.redirectStrategy.sendRedirect(request, response, this.logoutTarget);
		}
	}

}
