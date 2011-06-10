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


package org.jasig.schedassist.web.register.delegate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.mvc.servlet.FlowController;

/**
 * Simple extension of {@link FlowController}
 * with needed spring 3 mvc annotations.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateRegistrationFlowController.java 1973 2010-04-20 21:19:35Z npblair $
 */
@Controller
@RequestMapping("/delegate-register.html")
public class DelegateRegistrationFlowController extends FlowController {
	/* (non-Javadoc)
	 * @see org.springframework.webflow.mvc.servlet.FlowController#setFlowExecutor(org.springframework.webflow.executor.FlowExecutor)
	 */
	@Autowired
	@Override
	public void setFlowExecutor(FlowExecutor flowExecutor) {
		super.setFlowExecutor(flowExecutor);
	}
}
