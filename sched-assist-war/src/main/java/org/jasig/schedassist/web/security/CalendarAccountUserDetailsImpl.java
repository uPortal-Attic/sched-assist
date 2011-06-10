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
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.security.core.GrantedAuthority;

/**
 * {@link CalendarAccountUserDetails} implementation for standard people {@link ICalendarAccount}s.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarAccountUserDetailsImpl.java 2979 2011-01-25 19:24:44Z npblair $
 */
public class CalendarAccountUserDetailsImpl implements CalendarAccountUserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	private final ICalendarAccount calendarAccount;
	private IScheduleVisitor scheduleVisitor;
	private IScheduleOwner scheduleOwner;
	private boolean administrator = false;
	private static final String EMPTY = "";

	/**
	 * Construct a new {@link CalendarAccountUserDetailsImpl} instance
	 * and set the {@link ICalendarAccount} field.
	 * 
	 * If the {@link ICalendarAccount} field is not null, the only {@link GrantedAuthority}
	 * this instance will have is {@link SecurityConstants#REGISTER}.
	 * Use the various setters to alter available {@link GrantedAuthority}.
	 *
	 * @param calendarAccount
	 */
	CalendarAccountUserDetailsImpl(final ICalendarAccount calendarAccount) {
		this.calendarAccount = calendarAccount;
	}
	
	CalendarAccountUserDetailsImpl(final ICalendarAccount calendarAccount, final IScheduleOwner scheduleOwner) {
		this(calendarAccount);
		this.scheduleOwner = scheduleOwner;
	}

	/**
	 * Returns an array of {@link GrantedAuthority}s based on which fields are set:
	 * <ol>
	 * <li>if the "unregistered" {@link ICalendarAccount} is set and {@link ICalendarAccount#isEligible()}, adds {@link SecurityConstants#REGISTER}.</li>
	 * <li>if the {@link IScheduleVisitor} field is set and is eligible, adds {@link SecurityConstants#VISITOR}.</li>
	 * <li>if the {@link IScheduleOwner} field is set and is eligible, adds {@link SecurityConstants#OWNER}.</li>
	 * </ol>
	 * 
	 * @see org.springframework.security.userdetails.UserDetails#getAuthorities()
	 */
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if(null != this.calendarAccount) {
			if(this.calendarAccount.isEligible()) {
				authorities.add(SecurityConstants.REGISTER);
				authorities.add(SecurityConstants.DELEGATE_LOGIN);
			}
		}

		if(null != this.scheduleVisitor) {
			if(this.scheduleVisitor.getCalendarAccount().isEligible()) {
				authorities.add(SecurityConstants.VISITOR);
			}
		}

		if(null != this.scheduleOwner) {
			if(this.scheduleOwner.getCalendarAccount().isEligible()) {
				authorities.add(SecurityConstants.OWNER);
				authorities.remove(SecurityConstants.REGISTER);
			}
		}
		
		if(this.administrator) {
			authorities.add(SecurityConstants.AVAILABLE_ADMINISTRATOR);
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
		return this.calendarAccount.getUsername();
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
		return null != this.calendarAccount ? this.calendarAccount.isEligible() : false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#getCalendarAccount()
	 */
	@Override
	public ICalendarAccount getCalendarAccount() {
		return calendarAccount;
	}
	/**
	 * @return the scheduleVisitor
	 */
	public IScheduleVisitor getScheduleVisitor() {
		return scheduleVisitor;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#getScheduleOwner()
	 */
	@Override
	public IScheduleOwner getScheduleOwner() throws NotRegisteredException {
		if(null == this.scheduleOwner) {
			throw new NotRegisteredException(this.calendarAccount + " is not registered");
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
		return false;
	}

	/**
	 * @param scheduleVisitor the scheduleVisitor to set
	 */
	void setScheduleVisitor(IScheduleVisitor scheduleVisitor) {
		this.scheduleVisitor = scheduleVisitor;
	}
	/**
	 * @param scheduleOwner the scheduleOwner to set
	 */
	void setScheduleOwner(IScheduleOwner scheduleOwner) {
		this.scheduleOwner = scheduleOwner;
	}	

	/**
	 * @return the administrator
	 */
	boolean isAdministrator() {
		return this.administrator;
	}
	/**
	 * @param administrator the administrator to set
	 */
	void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#updateScheduleOwner(org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public void updateScheduleOwner(IScheduleOwner scheduleOwner) {
		this.scheduleOwner = scheduleOwner;
	}
	public boolean isScheduleOwnerSet() {
		return null != this.scheduleOwner;
	}
	public boolean isScheduleVisitorSet() {
		return null != this.scheduleVisitor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.web.security.CalendarAccountUserDetails#getActiveDisplayName()
	 */
	public String getActiveDisplayName() {
		StringBuilder display = new StringBuilder();
		display.append(this.calendarAccount.getDisplayName());
		display.append(" (");
		display.append(this.calendarAccount.getUsername());
		display.append(")");
		return display.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CalendarAccountUserDetailsImpl [calendarAccount=");
		builder.append(calendarAccount);
		builder.append(", scheduleVisitor=");
		builder.append(scheduleVisitor);
		builder.append(", scheduleOwner=");
		builder.append(scheduleOwner);
		builder.append(", administrator=");
		builder.append(administrator);
		builder.append("]");
		return builder.toString();
	}
	
}
