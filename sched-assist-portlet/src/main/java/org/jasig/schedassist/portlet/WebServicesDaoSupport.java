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

package org.jasig.schedassist.portlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

/**
 * Base class for Available implementations that depend
 * on the Available Web Services endpoint.
 * 
 * Depends on a fully configured {@link WebServiceTemplate}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: WebServicesDaoSupport.java 3024 2011-02-01 19:25:08Z npblair $
 */
public abstract class WebServicesDaoSupport {

	public static final String INELIGIBLE_MESSAGE = "User not eligible for Schedule Visitor role";
	public static final String SERVICE_UNAVAILABLE_MESSAGE = "Service Unavailable";
	private Log LOG = LogFactory.getLog(this.getClass());
	
	private WebServiceTemplate webServiceTemplate;

	/**
	 * @param webServiceTemplate the webServiceTemplate to set
	 */
	@Required
	public void setWebServiceTemplate(WebServiceTemplate webServiceTemplate) {
		this.webServiceTemplate = webServiceTemplate;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	protected Object doSendAndReceive(Object request) {
		try {
			LOG.debug("enter doSendAndReceive");
			Object result = this.webServiceTemplate.marshalSendAndReceive(request);
			LOG.debug("exit doSendAndReceive");
			return result;
		}  catch (SoapFaultClientException e) {
			LOG.debug("caught exception in doSendAndReceive on " + request, e);
			final String faultString = e.getFaultStringOrReason();
			if(faultString != null) {
				if(faultString.contains(INELIGIBLE_MESSAGE)) {
					throw new IneligibleForServiceException(e);
				} else if (faultString.contains(SERVICE_UNAVAILABLE_MESSAGE)){
					LOG.warn("exception faultString translates to ServiceUnavailableExcpetion for " + request, e);
					throw new ServiceUnavailableException(e);
				}
			} 
			// fall through, through as is
			throw e;
		}
	}
}
