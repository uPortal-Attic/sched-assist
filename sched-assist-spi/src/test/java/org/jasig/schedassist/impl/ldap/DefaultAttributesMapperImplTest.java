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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Nicholas Blair
 * @version $Id: DefaultAttributesMapperImplTest.java $
 */
public class DefaultAttributesMapperImplTest {

	@Test
	public void testControl() throws NamingException {
		LDAPAttributesKey ldapAttributesKey = new LDAPAttributesKeyImpl();
		DefaultAttributesMapperImpl mapper = new DefaultAttributesMapperImpl(ldapAttributesKey);
		Attributes attributes = mock(Attributes.class);
		@SuppressWarnings("unchecked")
		NamingEnumeration<String> attributeIds = mock(NamingEnumeration.class);
		when(attributeIds.hasMore()).thenReturn(true, true, true, true, false);
		when(attributeIds.next()).thenReturn(ldapAttributesKey.getDisplayNameAttributeName(), 
				ldapAttributesKey.getEmailAddressAttributeName(), 
				ldapAttributesKey.getUniqueIdentifierAttributeName(), 
				ldapAttributesKey.getUsernameAttributeName());
		when(attributes.getIDs()).thenReturn(attributeIds);
		Attribute cn = mock(Attribute.class);
		when(cn.get()).thenReturn("Buckingham Badger");
		when(attributes.get(ldapAttributesKey.getDisplayNameAttributeName())).thenReturn(cn);
		Attribute email = mock(Attribute.class);
		when(email.get()).thenReturn("bbadger@wisc.edu");
		when(attributes.get(ldapAttributesKey.getEmailAddressAttributeName())).thenReturn(email);
		Attribute uniqueId = mock(Attribute.class);
		when(uniqueId.get()).thenReturn("bbadger");
		when(attributes.get(ldapAttributesKey.getUniqueIdentifierAttributeName())).thenReturn(uniqueId);
		
		Attribute username= mock(Attribute.class);
		when(username.get()).thenReturn("bbadger");
		when(attributes.get(ldapAttributesKey.getUsernameAttributeName())).thenReturn(username);
		
		Object o = mapper.mapFromAttributes(attributes);
		Assert.assertNotNull(o);
		Assert.assertTrue(o instanceof LDAPPersonCalendarAccountImpl);
		LDAPPersonCalendarAccountImpl person = (LDAPPersonCalendarAccountImpl) o;
		Assert.assertTrue(person.isEligible());
		Assert.assertEquals("bbadger", person.getCalendarUniqueId());
		Assert.assertEquals("Buckingham Badger", person.getDisplayName());
		Assert.assertEquals("bbadger@wisc.edu", person.getEmailAddress());
		Assert.assertEquals("bbadger", person.getUsername());
	}
	
	@Test
	public void testSkipPassword() throws NamingException {
		LDAPAttributesKey ldapAttributesKey = new LDAPAttributesKeyImpl();
		DefaultAttributesMapperImpl mapper = new DefaultAttributesMapperImpl(ldapAttributesKey);
		Attributes attributes = mock(Attributes.class);
		@SuppressWarnings("unchecked")
		NamingEnumeration<String> attributeIds = mock(NamingEnumeration.class);
		when(attributeIds.hasMore()).thenReturn(true, true, true, true, true, false);
		when(attributeIds.next()).thenReturn(ldapAttributesKey.getDisplayNameAttributeName(),
				"userPassword",
				ldapAttributesKey.getEmailAddressAttributeName(), 
				ldapAttributesKey.getUniqueIdentifierAttributeName(), 
				ldapAttributesKey.getUsernameAttributeName());
		when(attributes.getIDs()).thenReturn(attributeIds);
		Attribute cn = mock(Attribute.class);
		when(cn.get()).thenReturn("Buckingham Badger");
		when(attributes.get(ldapAttributesKey.getDisplayNameAttributeName())).thenReturn(cn);
		Attribute password = mock(Attribute.class);
		when(password.get()).thenReturn("badgers!");
		when(attributes.get("userPassword")).thenReturn(password);
		Attribute email = mock(Attribute.class);
		when(email.get()).thenReturn("bbadger@wisc.edu");
		when(attributes.get(ldapAttributesKey.getEmailAddressAttributeName())).thenReturn(email);
		Attribute uniqueId = mock(Attribute.class);
		when(uniqueId.get()).thenReturn("bbadger");
		when(attributes.get(ldapAttributesKey.getUniqueIdentifierAttributeName())).thenReturn(uniqueId);
		
		Attribute username= mock(Attribute.class);
		when(username.get()).thenReturn("bbadger");
		when(attributes.get(ldapAttributesKey.getUsernameAttributeName())).thenReturn(username);
		
		Map<String, String> values = mapper.convertToStringAttributesMap(attributes);
		Assert.assertNull(values.get("userPassword"));
		Assert.assertEquals("bbadger", values.get(ldapAttributesKey.getUniqueIdentifierAttributeName()));
		Assert.assertEquals("Buckingham Badger", values.get(ldapAttributesKey.getDisplayNameAttributeName()));
		Assert.assertEquals("bbadger@wisc.edu", values.get(ldapAttributesKey.getEmailAddressAttributeName()));
		Assert.assertEquals("bbadger", values.get(ldapAttributesKey.getUsernameAttributeName()));
	}
}
