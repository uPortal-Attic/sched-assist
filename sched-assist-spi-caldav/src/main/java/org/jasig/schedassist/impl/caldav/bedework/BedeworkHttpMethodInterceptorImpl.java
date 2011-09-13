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

/**
 * 
 */
package org.jasig.schedassist.impl.caldav.bedework;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.jasig.schedassist.impl.caldav.HttpMethodInterceptor;
import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Bedework expects each request include a Request Header with the name {@link #RUN_AS_HEADER}
 * and the value set to the username of the {@link ICalendarAccount} the request is being sent on behalf of.
 * 
 * @author Nicholas Blair
 * @version $ Id: BedeworkHttpMethodInterceptorImpl.java $
 */
public class BedeworkHttpMethodInterceptorImpl implements HttpMethodInterceptor {

	protected static final String RUN_AS_HEADER = "Run-As";
	protected static final Header CLIENT_ID_HEADER = new Header("Client-Id", "Jasig Scheduling Assistant");
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.HttpMethodInterceptor#doWithMethod(HttpMethod, ICalendarAccount)
	 */
	@Override
	public HttpMethod doWithMethod(HttpMethod method, ICalendarAccount calendarAccount) {
		method.addRequestHeader(CLIENT_ID_HEADER);
		method.addRequestHeader(RUN_AS_HEADER, calendarAccount.getCalendarLoginId());
		return method;
	}

}
