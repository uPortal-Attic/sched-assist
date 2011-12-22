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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Value;

/**
 * Default {@link HttpCredentialsProvider} uses the exact same credentials for 
 * all accounts.
 * 
 * The intent with this implementation is that the administrative credentials you configure
 * have full read/write/admin privileges for all {@link ICalendarAccount}s. in your calendar
 * system.
 * 
 * @author Nicholas Blair
 * @version $Id: DefaultHttpCredentialsProviderImpl.java $
 */
public class DefaultHttpCredentialsProviderImpl implements
		HttpCredentialsProvider {

	private String caldavAdminUsername;
	private String caldavAdminPassword;
	
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
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.HttpCredentialsProvider#getCredentials(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public Credentials getCredentials(ICalendarAccount account) {
		UsernamePasswordCredentials adminCredentials = new UsernamePasswordCredentials(caldavAdminUsername, caldavAdminPassword);
		return adminCredentials;
	}

}

