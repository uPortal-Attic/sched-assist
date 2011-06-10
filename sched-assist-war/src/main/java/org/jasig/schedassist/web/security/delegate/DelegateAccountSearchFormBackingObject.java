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


package org.jasig.schedassist.web.security.delegate;

import java.io.Serializable;

import org.jasig.schedassist.model.IDelegateCalendarAccount;

/**
 * Form backing object used to search for {@link IDelegateCalendarAccount}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateAccountSearchFormBackingObject.java 1966 2010-04-20 17:44:20Z npblair $
 */
public class DelegateAccountSearchFormBackingObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;
	
	private String searchText;
	private String delegateName;
	private boolean returnForAutocomplete = false;
	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}
	/**
	 * @param searchText the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	/**
	 * @return the delegateName
	 */
	public String getDelegateName() {
		return delegateName;
	}
	/**
	 * @param delegateName the delegateName to set
	 */
	public void setDelegateName(String delegateName) {
		this.delegateName = delegateName;
	}
	/**
	 * @return the returnForAutocomplete
	 */
	public boolean isReturnForAutocomplete() {
		return returnForAutocomplete;
	}
	/**
	 * @param returnForAutocomplete the returnForAutocomplete to set
	 */
	public void setReturnForAutocomplete(boolean returnForAutocomplete) {
		this.returnForAutocomplete = returnForAutocomplete;
	}

}
