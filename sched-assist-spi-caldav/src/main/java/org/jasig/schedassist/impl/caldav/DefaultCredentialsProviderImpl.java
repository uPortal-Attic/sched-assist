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

package org.jasig.schedassist.impl.caldav;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;

/**
 * This {@link CredentialsProvider} implementation will return the 
 * supplied-at-construction-time {@link Credentials} if and only if
 * the request matches the configured {@link AuthScope}.
 * 
 * @author Nicholas Blair
 * @version $Id: DefaultCredentialsProviderImpl.java $
 */
public class DefaultCredentialsProviderImpl implements CredentialsProvider {

	private final Credentials adminCredentials;
	private final AuthScope targetAuthScope;
	/**
	 * 
	 * @param adminCredentials
	 * @param targetAuthScope
	 */
	public DefaultCredentialsProviderImpl(Credentials adminCredentials,
			AuthScope targetAuthScope) {
		this.adminCredentials = adminCredentials;
		this.targetAuthScope = targetAuthScope;
	}
	/**
	 * @return the adminCredentials
	 */
	public Credentials getAdminCredentials() {
		return adminCredentials;
	}
	/**
	 * @return the targetAuthScope
	 */
	public AuthScope getTargetAuthScope() {
		return targetAuthScope;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.http.client.CredentialsProvider#clear()
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException("clear not supported");	
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.http.client.CredentialsProvider#getCredentials(org.apache.http.auth.AuthScope)
	 */
	@Override
	public Credentials getCredentials(AuthScope authscope) {
		if(!targetAuthScope.equals(authscope)) {
			return null;
		}
		return adminCredentials;
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.http.client.CredentialsProvider#setCredentials(org.apache.http.auth.AuthScope, org.apache.http.auth.Credentials)
	 */
	@Override
	public void setCredentials(AuthScope arg0, Credentials arg1) {
		throw new UnsupportedOperationException("setCredentials not supported");	
	}
}
