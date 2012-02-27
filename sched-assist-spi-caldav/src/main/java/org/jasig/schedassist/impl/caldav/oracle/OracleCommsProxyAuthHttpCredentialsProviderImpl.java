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

package org.jasig.schedassist.impl.caldav.oracle;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.jasig.schedassist.impl.caldav.DefaultHttpCredentialsProviderImpl;
import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Subclass of {@link DefaultHttpCredentialsProviderImpl} that supports Oracle Communications Suite's
 * "proxy authentication" mechanism.
 * 
 * If the super class is used against an Oracle Communications Suite CalDAV server, the server will 
 * automatically add a SENT-BY parameter with a value of {@link #getCaldavAdminUsername()} on the ORGANIZER property
 * for events created by the Scheduling Assistant.
 * 
 * This class provides Oracle Communications Suite "proxy authentication" credentials, the difference being in the username,
 * which is constructed from the {@link #getCaldavAdminUsername()}, a semi-colon, and finally the Schedule Owner's email address.
 * When used in place of the super class with Oracle Communications Suite, no SENT-BY parameter is added
 * to the ORGANIZER.
 * 
 * @author Nicholas Blair
 * @version $Id: OracleCommsProxyAuthHttpCredentialsProviderImpl.java $
 */
public class OracleCommsProxyAuthHttpCredentialsProviderImpl extends DefaultHttpCredentialsProviderImpl {

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.HttpCredentialsProvider#getCredentials(org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public Credentials getCredentials(ICalendarAccount account) {
		StringBuilder username = new StringBuilder(getCaldavAdminUsername());
		username.append(";");
		username.append(account.getEmailAddress());
		UsernamePasswordCredentials proxyCredentials = new UsernamePasswordCredentials(username.toString(), getCaldavAdminPassword());
		return proxyCredentials;
	}

}
