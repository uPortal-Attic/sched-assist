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

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.jasig.schedassist.impl.caldav.DefaultCredentialsProviderImpl;
import org.jasig.schedassist.model.ICalendarAccount;


/**
 * If the default {@link CredentialsProvider} implementation is used against an Oracle Communications Suite CalDAV server, the server will 
 * automatically add a SENT-BY parameter with a value of {@link #getCaldavAdminUsername()} on the ORGANIZER property
 * for events created by the Scheduling Assistant.
 * 
 * This class provides Oracle Communications Suite "proxy authentication" credentials, the difference being in the username,
 * which is constructed from the username in {@link #getAdminCredentials()}, a semi-colon, and finally the Schedule Owner's email address.
 * When used in place of the super class with Oracle Communications Suite, no SENT-BY parameter is added
 * to the ORGANIZER.
 * 
 * @author Nicholas Blair
 * @version $Id: OracleCommsCredentialsProviderImpl.java $
 */
public class OracleCommsCredentialsProviderImpl extends
		DefaultCredentialsProviderImpl {

	private static final String SEMICOLON = ";";
	private final ICalendarAccount accountToProxy;
	
	/**
	 * 
	 * @param accountToProxy
	 * @param adminCredentials
	 * @param targetAuthScope
	 */
	public OracleCommsCredentialsProviderImpl(ICalendarAccount accountToProxy, Credentials adminCredentials,
			AuthScope targetAuthScope) {
		super(adminCredentials, targetAuthScope);
		this.accountToProxy = accountToProxy;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.DefaultCredentialsProviderImpl#getCredentials(org.apache.http.auth.AuthScope)
	 */
	@Override
	public Credentials getCredentials(AuthScope authscope) {
		if(!getTargetAuthScope().equals(authscope)) {
			return null;
		}
		
		StringBuilder username = new StringBuilder(getAdminCredentials().getUserPrincipal().getName());
		username.append(SEMICOLON);
		username.append(accountToProxy.getEmailAddress());
		UsernamePasswordCredentials proxyCredentials = new UsernamePasswordCredentials(username.toString(), getAdminCredentials().getPassword());
		return proxyCredentials; 
	}

}
