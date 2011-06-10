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

package org.jasig.schedassist.portlet.webflow;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.mvc.builder.FlowResourceFlowViewResolver;
import org.springframework.webflow.mvc.view.FlowViewResolver;

/**
 * Spring MVC {@link ViewResolver} that delegates to a {@link FlowViewResolver}.
 * Uses a {@link FlowResourceFlowViewResolver} by default.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: FlowResourceDelegatingViewResolver.java $
 */
public class FlowViewResolverDelegatingViewResolver implements ViewResolver {

	private Log LOG = LogFactory.getLog(this.getClass());
	private FlowViewResolver flowViewResolver = new FlowResourceFlowViewResolver();

	/**
	 * @param flowViewResolver the flowViewResolver to set
	 */
	public void setFlowViewResolver(FlowViewResolver flowViewResolver) {
		this.flowViewResolver = flowViewResolver;
	}

	/**
	 * Delegates to the internal {@link FlowViewResolver}.
	 * If the {@link FlowViewResolver} throws an exception, it is simply logged and the method
	 * will return null.
	 * 
	 * @see org.springframework.web.servlet.ViewResolver#resolveViewName(java.lang.String, java.util.Locale)
	 */
	@Override
	public View resolveViewName(String viewId, Locale arg1) throws Exception {
		try {
			View result = flowViewResolver.resolveView(viewId, RequestContextHolder.getRequestContext());
			if(LOG.isDebugEnabled()) {
				LOG.debug("viewId " + viewId + " resolved to " + result);
			}
			return result;
		} catch (IllegalArgumentException e) {
			LOG.warn("ignoring IllegalArgumentException to allow next resolver to try " + viewId);
			return null;
		}
	}

}
