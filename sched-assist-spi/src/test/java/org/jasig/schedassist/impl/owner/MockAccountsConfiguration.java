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

package org.jasig.schedassist.impl.owner;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Nicholas Blair
 * @version $Id: MockAccountsConfiguration.java $
 */
@Configuration
public class MockAccountsConfiguration {

	@Bean
	public MockCalendarAccountDao calendarAccountDao() {
		List<MockCalendarAccount> accounts = constructAccounts(0, 10);
		return new MockCalendarAccountDao(accounts);
	}
	/**
	 * 
	 * @param startId
	 * @param endId
	 * @return
	 */
	protected List<MockCalendarAccount> constructAccounts(int startId, int endId) {
		Validate.isTrue(startId < endId, "invalid arguments, startId (" + startId + ") must be less than endId (" + endId + ")");
		List<MockCalendarAccount> accounts = new ArrayList<MockCalendarAccount>();
		for(int i = startId; i < endId; i++) {
			MockCalendarAccount calendarAccount = new MockCalendarAccount();
			calendarAccount.setUsername("user"+i);
			calendarAccount.setCalendarUniqueId("10000:0000" + i);
			calendarAccount.setEmailAddress("email"+ i + "@domain.com");
			calendarAccount.setDisplayName("User Name" + i);
			calendarAccount.setAttributeValue("uid", calendarAccount.getUsername());
			calendarAccount.setAttributeValue("mail", calendarAccount.getEmailAddress());
			accounts.add(calendarAccount);
		}
		return accounts;
	}
}
