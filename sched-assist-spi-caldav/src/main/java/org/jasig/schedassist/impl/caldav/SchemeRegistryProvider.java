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

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.SchemeRegistryFactory;

/**
 * Commons HttpClient's connection manager depends on a {@link SchemeRegistry}.
 * {@link SchemeRegistryFactory#createDefault()} will return one that is ready for the default ports,
 * but if your CalDAV server is on an alternate port it won't work.
 * 
 * This class provides a static method that can be used to setup a {@link SchemeRegistry} that is
 * configured properly for CalDAV servers on alternate ports.
 * 
 * @author Nicholas Blair
 * @version $Id: SchemeRegistryProvider.java $
 */
public final class SchemeRegistryProvider {

	/**
	 * 
	 * @see SchemeRegistryFactory#createDefault()
	 * @param schemeName
	 * @param port
	 * @param useSsl
	 * @return
	 */
	public static SchemeRegistry createSchemeRegistry(String schemeName, int port, boolean useSsl) {
		SchemeRegistry registry = SchemeRegistryFactory.createDefault();
		if(useSsl) {
			registry.register(
	                new Scheme(schemeName, port, SSLSocketFactory.getSocketFactory()));
		} else {
			registry.register(
	                new Scheme(schemeName, port, PlainSocketFactory.getSocketFactory()));
		}
		
		return registry;
	}
}
