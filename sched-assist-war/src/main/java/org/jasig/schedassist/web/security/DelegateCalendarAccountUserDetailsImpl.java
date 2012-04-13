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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.springframework.security.core.GrantedAuthority;

/**
 * {@link CalendarAccountUserDetails} implementation for {@link IDelegateCalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateCalendarAccountUserDetailsImpl.java 2306 2010-07-28 17:20:12Z npblair $
 */
public class DelegateCalendarAccountUserDetailsImpl implements CalendarAccountUserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	private static final String EMPTY = "";
	
	private final IDelegateCalendarAccount delegateCalendarAccount;
	private IScheduleOwner scheduleOwner;
	/**
	 * 
	 * @param delegateCalendarAccount
	 */
	public DelegateCalendarAccountUserDetailsImpl(IDelegateCalendarAccount delegateCalendarAccount) {
		this(delegateCalendarAccount, null);
	}
	
	/**
	 * @param delegateCalendarAccount
	 * @param delegateScheduleOwner
	 */
	public DelegateCalendarAccountUserDetailsImpl(
			IDelegateCalendarAccount delegateCalendarAccount,
			IScheduleOwner delegateScheduleOwner) {
		this.delegateCalendarAccount = delegateCalendarAccount;
		this.scheduleOwner = delegateScheduleOwner;
	}


	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#getAuthorities()
	 */
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if(null != this.delegateCalendarAccount && this.delegateCalendarAccount.isEligible()) {
			authorities.add(SecurityConstants.DELEGATE_REGISTER);
		}
		
		if(null != this.scheduleOwner) {
			authorities.add(SecurityConstants.DELEGATE_OWNER);
			authorities.remove(SecurityConstants.DELEGATE_REGISTER);
		}
		
		return Collections.unmodifiableList(authorities);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#getPassword()
	 */
	public String getPassword() {
		return EMPTY;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#getUsername()
	 */
	public String getUsername() {
		return this.delegateCalendarAccount.getUsername();
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isAccountNonExpired()
	 */
	public boolean isAccountNonExpired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
	 */
	public boolean isAccountNonLocked() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.userdetails.UserDetails#isEnabled()
	 */
	public boolean isEnabled() {
		return null != this.delegateCalendarAccount ? this.delegateCalendarAccount.isEligible() : false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#getActiveDisplayName()
	 */
	public String getActiveDisplayName() {
		StringBuilder display = new StringBuilder();
		display.append(this.delegateCalendarAccount.getDisplayName());
		display.append(" (managed by ");
		display.append(this.delegateCalendarAccount.getAccountOwner().getUsername());
		display.append(")");
		return display.toString();
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#getCalendarAccount()
	 */
	@Override
	public ICalendarAccount getCalendarAccount() {
		return getDelegateCalendarAccount();
	}
	
	/**
	 * 
	 * @return the {@link IDelegateCalendarAccount}
	 */
	public IDelegateCalendarAccount getDelegateCalendarAccount() {
		return this.delegateCalendarAccount;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#getScheduleOwner()
	 */
	@Override
	public IScheduleOwner getScheduleOwner() throws NotRegisteredException {
		if(null == this.scheduleOwner) {
			throw new NotRegisteredException(this.delegateCalendarAccount + " is not registered");
		} else {
			return this.scheduleOwner;
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#isDelegate()
	 */
	@Override
	public final boolean isDelegate() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#updateScheduleOwner(org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public void updateScheduleOwner(IScheduleOwner owner) {
		this.scheduleOwner = owner;
	}
}
