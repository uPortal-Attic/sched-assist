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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Nicholas Blair
 * @version $Id: LDAPPersonCalendarAccountImplTest.java $
 */
public class LDAPPersonCalendarAccountImplTest {

	/**
	 * Empty person is ineligible and has no fields set.
	 */
	@Test
	public void testEmptyPerson() {
		LDAPAttributesKey ldapAttributesKey = new LDAPAttributesKeyImpl();
		LDAPPersonCalendarAccountImpl person = new LDAPPersonCalendarAccountImpl(new HashMap<String, String>(), ldapAttributesKey);
		Assert.assertFalse(person.isEligible());
		Assert.assertNull(person.getCalendarLoginId());
		Assert.assertNull(person.getCalendarUniqueId());
		Assert.assertNull(person.getDisplayName());
		Assert.assertNull(person.getEmailAddress());
		Assert.assertNull(person.getUsername());
	}
	
	/**
	 * Control person, using default eligibility evaluator.
	 */
	@Test
	public void testControlPerson() {
		LDAPAttributesKey ldapAttributesKey = new LDAPAttributesKeyImpl();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(ldapAttributesKey.getDisplayNameAttributeName(), "Buckingham Badger");
		attributes.put(ldapAttributesKey.getEmailAddressAttributeName(), "bbadger@wisc.edu");
		attributes.put(ldapAttributesKey.getUniqueIdentifierAttributeName(), "bbadger");
		attributes.put(ldapAttributesKey.getUsernameAttributeName(), "bbadger");
		LDAPPersonCalendarAccountImpl person = new LDAPPersonCalendarAccountImpl(attributes, ldapAttributesKey);
		Assert.assertTrue(person.isEligible());
		Assert.assertEquals("bbadger", person.getCalendarLoginId());
		Assert.assertEquals("bbadger", person.getCalendarUniqueId());
		Assert.assertEquals("Buckingham Badger", person.getDisplayName());
		Assert.assertEquals("bbadger@wisc.edu", person.getEmailAddress());
		Assert.assertEquals("bbadger", person.getUsername());
		
	}
	
	/**
	 * Override {@link LDAPAttributesKey} implementation to use different attribute
	 * evaluator.
	 */
	@Test
	public void testAlternateAttributesKey() {
		// subclass LDAPAttributesKey and override evaluator to look for "Y" value in another attribute
		LDAPAttributesKey ldapAttributesKey = new LDAPAttributesKeyImpl() {
			/* (non-Javadoc)
			 * @see org.jasig.schedassist.impl.ldap.LDAPAttributesKeyImpl#evaluateEligibilityAttributeValue(java.util.Map)
			 */
			@Override
			public boolean evaluateEligibilityAttributeValue(
					Map<String, String> attributes) {
				final String eligibilityValue = attributes.get(getEligibilityAttributeName());
				return "Y".equalsIgnoreCase(eligibilityValue);
			}
		};
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(ldapAttributesKey.getDisplayNameAttributeName(), "Buckingham Badger");
		attributes.put(ldapAttributesKey.getEligibilityAttributeName(), "Y");
		attributes.put(ldapAttributesKey.getEmailAddressAttributeName(), "bbadger@wisc.edu");
		attributes.put(ldapAttributesKey.getUniqueIdentifierAttributeName(), "bbadger");
		attributes.put(ldapAttributesKey.getUsernameAttributeName(), "bbadger");
		LDAPPersonCalendarAccountImpl person = new LDAPPersonCalendarAccountImpl(attributes, ldapAttributesKey);
		Assert.assertTrue(person.isEligible());
		Assert.assertEquals("bbadger", person.getCalendarLoginId());
		Assert.assertEquals("bbadger", person.getCalendarUniqueId());
		Assert.assertEquals("Buckingham Badger", person.getDisplayName());
		Assert.assertEquals("bbadger@wisc.edu", person.getEmailAddress());
		Assert.assertEquals("bbadger", person.getUsername());
		
		attributes.put(ldapAttributesKey.getEligibilityAttributeName(), "N");
		person = new LDAPPersonCalendarAccountImpl(attributes, ldapAttributesKey);
		Assert.assertFalse(person.isEligible());
	}
}
