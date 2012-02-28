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
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Default {@link CredentialsProviderFactory} implementation, returns
 * {@link DefaultCredentialsProviderImpl} instances.
 * 
 * 
 * @author Nicholas Blair
 * @version $Id: DefaultCredentialsProviderProviderImpl.java $
 */
public class DefaultCredentialsProviderFactoryImpl implements
		CredentialsProviderFactory {

	private String caldavAdminUsername;
	private String caldavAdminPassword;
	private AuthScope authScope;
	/**
	 * @param caldavAdminUsername the caldavAdminUsername to set
	 */
	@Value("${caldav.admin.username}")
	public void setCaldavAdminUsername(String caldavAdminUsername) {
		this.caldavAdminUsername = caldavAdminUsername;
	}
	/**
	 * @param caldavAdminPassword the caldavAdminPassword to set
	 */
	@Value("${caldav.admin.password}")
	public void setCaldavAdminPassword(String caldavAdminPassword) {
		this.caldavAdminPassword = caldavAdminPassword;
	}
	/**
	 * @return the caldavAdminUsername
	 */
	protected String getCaldavAdminUsername() {
		return caldavAdminUsername;
	}
	/**
	 * @return the caldavAdminPassword
	 */
	protected String getCaldavAdminPassword() {
		return caldavAdminPassword;
	}
	/**
	 * @return the authScope
	 */
	public AuthScope getAuthScope() {
		return authScope;
	}
	/**
	 * @param authScope the authScope to set
	 */
	@Autowired
	public void setAuthScope(AuthScope authScope) {
		this.authScope = authScope;
	}
	/**
	 * 
	 * @return a {@link Credentials} made up of {@link #getCaldavAdminUsername()} and {@link #getCaldavAdminPassword()}.
	 */
	protected Credentials getAdminCredentials() {
		return new UsernamePasswordCredentials(getCaldavAdminUsername(), getCaldavAdminPassword());
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.CredentialsProviderProvider#getCredentialsProvider(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public CredentialsProvider getCredentialsProvider(ICalendarAccount account) {
		DefaultCredentialsProviderImpl provider = new DefaultCredentialsProviderImpl(getAdminCredentials(), authScope);
		return provider;
	}
}
