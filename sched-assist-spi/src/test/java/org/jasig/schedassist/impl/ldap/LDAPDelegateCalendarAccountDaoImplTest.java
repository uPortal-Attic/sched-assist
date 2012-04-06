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
package org.jasig.schedassist.impl.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Name;

import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.jasig.schedassist.model.mock.MockCalendarAccount;
import org.jasig.schedassist.model.mock.MockDelegateCalendarAccount;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.ldap.core.DistinguishedName;

/**
 * Tests for {@link LDAPDelegateCalendarAccountDaoImpl}.
 * 
 * @author Nicholas Blair
 */
public class LDAPDelegateCalendarAccountDaoImplTest {

	@Test
	public void testEnforceDistinguishNameControl() {
		DistinguishedName name = new DistinguishedName("wwid=ABCDE12345,ou=people,o=domain,o=isp");
		HasDistinguishedName owner = Mockito.mock(HasDistinguishedName.class);
		Mockito.when(owner.getDistinguishedName()).thenReturn(name);
		
		MockCalendarAccountWithDistinguishedName accountOwner = new MockCalendarAccountWithDistinguishedName();
		accountOwner.setDistinguishedName(name);
		
		LDAPDelegateCalendarAccountDaoImpl accountDao = new LDAPDelegateCalendarAccountDaoImpl();
		accountDao.setTreatOwnerAttributeAsDistinguishedName(true);
		
		List<IDelegateCalendarAccount> delegates = new ArrayList<IDelegateCalendarAccount>();
		MockDelegateCalendarAccount mockDelegate = new MockDelegateCalendarAccount();
		mockDelegate.setAccountOwnerAttribute(name.toString());
		delegates.add(mockDelegate);
		accountDao.enforceDistinguishedNameMatch(delegates, owner);
		Assert.assertEquals(1, delegates.size());
	}
	
	@Test
	public void testEnforceDistinguishNameNoMatch() {
		DistinguishedName name = new DistinguishedName("wwid=ABCDE12345,ou=people,o=domain,o=isp");
		HasDistinguishedName owner = Mockito.mock(HasDistinguishedName.class);
		Mockito.when(owner.getDistinguishedName()).thenReturn(name);
		
		MockCalendarAccountWithDistinguishedName accountOwner = new MockCalendarAccountWithDistinguishedName();
		accountOwner.setDistinguishedName(name);
		
		LDAPDelegateCalendarAccountDaoImpl accountDao = new LDAPDelegateCalendarAccountDaoImpl();
		accountDao.setTreatOwnerAttributeAsDistinguishedName(true);
		
		List<IDelegateCalendarAccount> delegates = new ArrayList<IDelegateCalendarAccount>();
		MockDelegateCalendarAccount mockDelegate = new MockDelegateCalendarAccount();
		// off by one
		mockDelegate.setAccountOwnerAttribute("wwid=ABCDE12346,ou=people,o=domain,o=isp");
		delegates.add(mockDelegate);
		accountDao.enforceDistinguishedNameMatch(delegates, owner);
		// verify removed from results
		Assert.assertEquals(0, delegates.size());
	}
	
	class MockCalendarAccountWithDistinguishedName extends MockCalendarAccount implements HasDistinguishedName {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4368560472490091943L;
		private Name distinguishedName;
		
		/**
		 * @param distinguishedName the distinguishedName to set
		 */
		public void setDistinguishedName(Name distinguishedName) {
			this.distinguishedName = distinguishedName;
		}

		@Override
		public Name getDistinguishedName() {
			return distinguishedName;
		}	
	}
}
