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

import org.apache.commons.httpclient.methods.EntityEnclosingMethod;

/**
 * Provides "REPORT" HTTP Method.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ReportMethod.java $
 */
public final class ReportMethod extends EntityEnclosingMethod {

	protected static final String REPORT = "REPORT";

	/**
	 * 
	 */
	public ReportMethod() {
		super();
	}
	/**
	 * @param uri
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	public ReportMethod(String uri) throws IllegalArgumentException,
			IllegalStateException {
		super(uri);
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.httpclient.HttpMethodBase#getName()
	 */
	@Override
	public String getName() {
		return REPORT;
	}
}
