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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Default {@link EmailAddressValidator} implementation.
 * Performs basic syntax validation, and stores a configurable set of restricted domains.
 * If the address' domain part 
 * 
 * @author Nicholas Blair
 */
public class DefaultEmailAddressValidatorImpl implements EmailAddressValidator {

	private Set<String> restrictedDomains = Collections.synchronizedSet(new HashSet<String>());
	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * 
	 * @param propertyValue
	 */
	public void setRestrictedDomains(String propertyValue) {
		String [] domains = org.springframework.util.StringUtils.commaDelimitedListToStringArray(propertyValue);
		restrictedDomains.addAll(Arrays.asList(domains));
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.EmailAddressValidator#canSendToEmailAddress(java.lang.String)
	 */
	@Override
	public boolean canSendToEmailAddress(ICalendarAccount account) {
		if(account == null) {
			return false;
		}

		final String address = account.getEmailAddress();
		if(StringUtils.isBlank(address)) {
			return false;
		}

		try {
			InternetAddress emailAddr = new InternetAddress(address);
			emailAddr.validate();
			String [] parts = address.split("@");
			if(parts.length == 2) {
				if(!restrictedDomains.contains(parts[1])) {
					return true;
				}
			}
		} catch (AddressException ex) {
			log.info("email address for " + account + " failed to valdidate", ex);
			return false;
		}

		return false;
	}

}
