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

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHeader;
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
	protected static final Header CLIENT_ID_HEADER = new BasicHeader("Client-Id", "Jasig Scheduling Assistant");
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.caldav.HttpMethodInterceptor#doWithMethod(org.apache.http.HttpRequest, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public HttpRequest doWithMethod(HttpRequest request,
			ICalendarAccount onBehalfOf) {
		request.addHeader(CLIENT_ID_HEADER);
		request.addHeader(new BasicHeader(RUN_AS_HEADER, onBehalfOf.getCalendarLoginId()));
		return request;
	}

}
