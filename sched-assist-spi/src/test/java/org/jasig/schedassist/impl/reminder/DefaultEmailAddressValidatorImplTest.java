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

package org.jasig.schedassist.impl.reminder;

import org.jasig.schedassist.model.ICalendarAccount;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * @author Nicholas Blair
 */
public class DefaultEmailAddressValidatorImplTest {

	@Test
	public void testNull() {
		ICalendarAccount account = mock(ICalendarAccount.class);
		DefaultEmailAddressValidatorImpl validator = new DefaultEmailAddressValidatorImpl();
		Assert.assertFalse(validator.canSendToEmailAddress(account));	
	}
	
	@Test
	public void testInvalid() {
		ICalendarAccount account = mock(ICalendarAccount.class);
		when(account.getEmailAddress()).thenReturn("notanemailaddress");
		DefaultEmailAddressValidatorImpl validator = new DefaultEmailAddressValidatorImpl();
		Assert.assertFalse(validator.canSendToEmailAddress(account));	
	}
	
	@Test
	public void testControl() {
		ICalendarAccount account = mock(ICalendarAccount.class);
		when(account.getEmailAddress()).thenReturn("email@address.com");
		DefaultEmailAddressValidatorImpl validator = new DefaultEmailAddressValidatorImpl();
		Assert.assertTrue(validator.canSendToEmailAddress(account));	
	}
	
	@Test
	public void testRestrictedDomain() {
		ICalendarAccount account = mock(ICalendarAccount.class);
		when(account.getEmailAddress()).thenReturn("email@address.com");
		DefaultEmailAddressValidatorImpl validator = new DefaultEmailAddressValidatorImpl();
		validator.setRestrictedDomains("address.com");
		Assert.assertFalse(validator.canSendToEmailAddress(account));	
		
		ICalendarAccount account2 = mock(ICalendarAccount.class);
		when(account2.getEmailAddress()).thenReturn("email@notaddress.com");
		Assert.assertTrue(validator.canSendToEmailAddress(account2));	
	}
}
