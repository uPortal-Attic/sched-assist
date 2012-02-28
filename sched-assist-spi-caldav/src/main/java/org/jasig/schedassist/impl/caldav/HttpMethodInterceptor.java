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
package org.jasig.schedassist.impl.caldav;

import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Different CalDAV servers may expect different information in the HTTP requests.
 * 
 * This interface provides a mechanism for altering the {@link HttpRequest}s
 * created by the {@link CaldavCalendarDataDaoImpl} before they are sent to the CalDAV
 * server.
 * 
 * @author Nicholas Blair
 * @version $ Id: HttpMethodInterceptor.java $
 */
public interface HttpMethodInterceptor {

	/**
	 * Implementations will receive the {@link HttpRequest} just before the
	 * {@link HttpClient#executeMethod(HttpRequest)} is called on it.
	 * 
	 * Implementations are allowed to mutate the method as needed, but MUST never return null.
	 * 
	 * @param method
	 * @param onBehalfOf the {@link ICalendarAccount} that matches the account the request will be on behalf of
	 * @return the CalDAV implementation-specific altered {@link HttpRequest}
	 */
	HttpRequest doWithMethod(HttpRequest method, ICalendarAccount onBehalfOf);
}
