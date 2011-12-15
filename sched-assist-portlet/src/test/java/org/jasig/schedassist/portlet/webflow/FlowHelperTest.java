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
package org.jasig.schedassist.portlet.webflow;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.VisibleWindow;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link FlowHelper}.
 * 
 * @author nblair
 *
 */
public class FlowHelperTest {

	@Test
	public void testValidateChosenStartTime() throws InputFormatException {
		FlowHelper flowHelper = new FlowHelper();
		
		VisibleWindow window = VisibleWindow.fromKey("1,1");
		Date now = new Date();
		Assert.assertEquals(FlowHelper.NO, flowHelper.validateChosenStartTime(window, now));
		// make sure it doesn't work in the past either
		Assert.assertEquals(FlowHelper.NO,flowHelper.validateChosenStartTime(window, DateUtils.addHours(new Date(), -1)));
		
		Assert.assertEquals(FlowHelper.YES,flowHelper.validateChosenStartTime(window, DateUtils.addHours(new Date(), 2)));
		Assert.assertEquals(FlowHelper.YES,flowHelper.validateChosenStartTime(window, DateUtils.addHours(new Date(), 167)));
		
		// still good 1 minute before window end
		Assert.assertEquals(FlowHelper.YES,flowHelper.validateChosenStartTime(window, DateUtils.addMinutes(DateUtils.addHours(new Date(), 168), -1)));
		
		Assert.assertEquals(FlowHelper.NO,flowHelper.validateChosenStartTime(window, DateUtils.addHours(new Date(), 168)));
		Assert.assertEquals(FlowHelper.NO,flowHelper.validateChosenStartTime(window, DateUtils.addHours(new Date(), 169)));


	}
}
