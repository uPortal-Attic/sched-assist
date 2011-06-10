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
package org.jasig.schedassist.web.security;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.ppolicy.PasswordPolicyException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * This subclass of {@link DaoAuthenticationProvider} is intended to provide
 * better integration for LDAP-sourced calendar accounts with Spring Security and
 * the {@link CalendarUserDetailsServiceImpl}.
 * 
 * Spring Security's LDAP support will only return their custom LDAP {@link UserDetails}
 * implementation. That is not satisfactory for use within this application, as we have
 * an extension ({@link CalendarAccountUserDetails} that must be returned.
 * 
 * Instead of sub-classing Spring Security's LDAP {@link AuthenticationProvider} implementation,
 * this class sub-classes {@link DaoAuthenticationProvider} to borrow it's tight integration
 * with the {@link UserDetailsService}.
 * 
 * @author Nicholas Blair
 * @version $ Id: CustomLDAPAuthenticationProvider.java $
 */
public class CustomLDAPAuthenticationProvider extends DaoAuthenticationProvider {

	private LdapAuthenticator authenticator;
	
	/**
	 * @return the ldapAuthenticator
	 */
	public LdapAuthenticator getAuthenticator() {
		return authenticator;
	}
	/**
	 * @param authenticator the ldap authenticator to set
	 */
	public void setAuthenticator(LdapAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

	/**
	 * Incorporates some of the 
	 *  (non-Javadoc)
	 * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider#additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails, org.springframework.security.authentication.UsernamePasswordAuthenticationToken)
	 */
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        if (logger.isDebugEnabled()) {
            logger.debug("Processing authentication request for user: " + username);
        }

        if (!StringUtils.hasLength(username)) {
            throw new BadCredentialsException(messages.getMessage("LdapAuthenticationProvider.emptyUsername",
                    "Empty Username"));
        }

        Assert.notNull(password, "Null password was supplied in authentication token");

        try {
            DirContextOperations userData = getAuthenticator().authenticate(authentication);
            if(userData == null) {
            	throw new BadCredentialsException(messages.getMessage(
                        "LdapAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        } catch (PasswordPolicyException ppe) {
            // The only reason a ppolicy exception can occur during a bind is that the account is locked.
            throw new LockedException(messages.getMessage(ppe.getStatus().getErrorCode(),
                    ppe.getStatus().getDefaultMessage()));
        } catch (UsernameNotFoundException notFound) {
            if (hideUserNotFoundExceptions) {
                throw new BadCredentialsException(messages.getMessage(
                        "LdapAuthenticationProvider.badCredentials", "Bad credentials"));
            } else {
                throw notFound;
            }
        } 
	}

	
}
