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


package org.jasig.schedassist.web.owner.relationships;

/**
 * Form backing object for {@link CalendarUserSearchFormController}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CalendarUserSearchFormBackingObject.java 1739 2010-02-16 18:19:23Z npblair $
 */
public class CalendarUserSearchFormBackingObject {

	private String searchText = "";
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
