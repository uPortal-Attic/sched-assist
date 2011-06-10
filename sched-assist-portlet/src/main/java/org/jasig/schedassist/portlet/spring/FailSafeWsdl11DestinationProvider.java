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

package org.jasig.schedassist.portlet.spring;

import org.springframework.core.io.Resource;
import org.springframework.ws.client.support.destination.Wsdl11DestinationProvider;

/**
 * Subclass of {@link Wsdl11DestinationProvider} that overrides
 * the {@link #setWsdl(Resource)} to nullify one of it's tests that
 * may cause the portlet to fail to load if the WSDL is temporarily unreachable.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: FailSafeWsdl11DestinationProvider.java $
 */
public class FailSafeWsdl11DestinationProvider extends
		Wsdl11DestinationProvider {

	/* (non-Javadoc)
	 * @see org.springframework.ws.client.support.destination.Wsdl11DestinationProvider#setWsdl(org.springframework.core.io.Resource)
	 */
	@Override
	public void setWsdl(Resource wsdlResource) {
		super.setWsdl(new DelegatingResource(wsdlResource) {
			@Override
			public boolean exists() {
				return true;
			}
		});
	}

}
