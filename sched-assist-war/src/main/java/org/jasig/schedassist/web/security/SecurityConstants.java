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


package org.jasig.schedassist.web.security;

import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

/**
 * This class defines the {@link GrantedAuthority} instances that constitute
 * the roles within this application.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SecurityConstants.java 2979 2011-01-25 19:24:44Z npblair $
 */
public final class SecurityConstants {

	/**
	 * Role that grants access to the registration form for delegate calendar accounts.
	 */
	public static final GrantedAuthority DELEGATE_REGISTER = new GrantedAuthorityImpl("ROLE_DELEGATE_REGISTER");
	/**
	 * Role that grants access to {@link IScheduleOwner} features for delegate calendar accoutnts.
	 */
	public static final GrantedAuthority DELEGATE_OWNER = new GrantedAuthorityImpl("ROLE_DELEGATE_OWNER");
	/**
	 * Role that grants access to {@link IScheduleOwner} features for customer accounts.
	 */
	public static final GrantedAuthority OWNER = new GrantedAuthorityImpl("ROLE_OWNER");
	/**
	 * Role that grants access to {@link IScheduleVisitor} features (view schedule, create/cancel appointments)
	 */
	public static final GrantedAuthority VISITOR = new GrantedAuthorityImpl("ROLE_VISITOR");
	/**
	 * Role that grants access to the registration form for customer accounts.
	 */
	public static final GrantedAuthority REGISTER = new GrantedAuthorityImpl("ROLE_REGISTER");
	/**
	 * Role that grants access to log in as a {@link IDelegateCalendarAccount}.
	 */
	public static final GrantedAuthority DELEGATE_LOGIN = new GrantedAuthorityImpl("ROLE_DELEGATE_LOGIN");
	
	/**
	 * Role that grants access to the application administrative functions.
	 */
	public static final GrantedAuthority AVAILABLE_ADMINISTRATOR = new GrantedAuthorityImpl("ROLE_AVAILABLE_ADMINISTRATOR");

}
