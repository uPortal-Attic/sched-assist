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
package org.jasig.schedassist.impl.ldap;

import java.util.List;

import org.jasig.schedassist.IDelegateCalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @author Nicholas Blair
 * @version $ Id: LDAPDelegateCalendarAccountDaoImpl.java $
 */
public class LDAPDelegateCalendarAccountDaoImpl implements
		IDelegateCalendarAccountDao {

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#searchForDelegates(java.lang.String, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public List<IDelegateCalendarAccount> searchForDelegates(String searchText,
			ICalendarAccount owner) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#searchForDelegates(java.lang.String)
	 */
	@Override
	public List<IDelegateCalendarAccount> searchForDelegates(String searchText) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#getDelegate(java.lang.String)
	 */
	@Override
	public IDelegateCalendarAccount getDelegate(String accountName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#getDelegate(java.lang.String, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public IDelegateCalendarAccount getDelegate(String accountName,
			ICalendarAccount owner) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#getDelegateByUniqueId(java.lang.String)
	 */
	@Override
	public IDelegateCalendarAccount getDelegateByUniqueId(String accountUniqueId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#getDelegateByUniqueId(java.lang.String, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public IDelegateCalendarAccount getDelegateByUniqueId(
			String accountUniqueId, ICalendarAccount owner) {
		// TODO Auto-generated method stub
		return null;
	}

}
