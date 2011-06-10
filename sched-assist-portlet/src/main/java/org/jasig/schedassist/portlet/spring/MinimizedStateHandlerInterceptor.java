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

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.springframework.web.portlet.handler.HandlerInterceptorAdapter;

/**
 *  {@link HandlerInterceptorAdaptor} for supporting {@link WindowState#MINIMIZED}.
 * See <a href="https://wiki.jasig.org/display/PLT/Minimized+WindowState+Handling">https://wiki.jasig.org/display/PLT/Minimized+WindowState+Handling</a>
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: MinimizedStateHandlerInterceptor.java $
 */
public class MinimizedStateHandlerInterceptor extends HandlerInterceptorAdapter {
	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.portlet.handler.HandlerInterceptorAdapter#preHandleRender(javax.portlet.RenderRequest, javax.portlet.RenderResponse, java.lang.Object)
	 */
	@Override
    public boolean preHandleRender(RenderRequest request, RenderResponse response, Object handler) throws Exception {
        if (WindowState.MINIMIZED.equals(request.getWindowState())) {
            return false;
        }
        return true;
    }
}
