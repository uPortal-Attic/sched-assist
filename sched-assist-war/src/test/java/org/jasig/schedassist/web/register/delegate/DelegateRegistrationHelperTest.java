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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.jasig.schedassist.model.mock.MockScheduleOwner;
import org.jasig.schedassist.web.security.DelegateCalendarAccountUserDetailsImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 *
 * @author Nicholas Blair
 * @version $Id: DelegateRegistrationHelperTest.java $
 */
public class DelegateRegistrationHelperTest {

	@Test
	public void testCurrentDelegateIsIneligibleDefault() {
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		MockScheduleOwner owner = new MockScheduleOwner(ownerAccount, 1L);
		IDelegateCalendarAccount delegate = mock(IDelegateCalendarAccount.class);
		when(delegate.isEligible()).thenReturn(true);
		DelegateCalendarAccountUserDetailsImpl details = new DelegateCalendarAccountUserDetailsImpl(delegate, owner);

        SecurityContext context = new SecurityContextImpl();

        context.setAuthentication(new UsernamePasswordAuthenticationToken(details, ""));

        SecurityContextHolder.setContext(context);
		DelegateRegistrationHelper helper = new DelegateRegistrationHelper();
		Assert.assertFalse(helper.currentDelegateIsIneligible());
		
	}
	
	@Test
	public void testCurrentDelegateIsIneligibleTrue() {
		MockCalendarAccount ownerAccount = new MockCalendarAccount();
		MockScheduleOwner owner = new MockScheduleOwner(ownerAccount, 1L);
		IDelegateCalendarAccount delegate = mock(IDelegateCalendarAccount.class);
		when(delegate.isEligible()).thenReturn(false);
		DelegateCalendarAccountUserDetailsImpl details = new DelegateCalendarAccountUserDetailsImpl(delegate, owner);

        SecurityContext context = new SecurityContextImpl();

        context.setAuthentication(new UsernamePasswordAuthenticationToken(details, ""));

        SecurityContextHolder.setContext(context);
		DelegateRegistrationHelper helper = new DelegateRegistrationHelper();
		Assert.assertTrue(helper.currentDelegateIsIneligible());
		
	}
	
}
