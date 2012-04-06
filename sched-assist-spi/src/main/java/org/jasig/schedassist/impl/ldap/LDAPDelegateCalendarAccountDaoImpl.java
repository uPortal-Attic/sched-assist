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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.IDelegateCalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IDelegateCalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.ldap.SizeLimitExceededException;
import org.springframework.ldap.TimeLimitExceededException;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.OrFilter;

/**
 * @author Nicholas Blair
 * @version $ Id: LDAPDelegateCalendarAccountDaoImpl.java $
 */
public class LDAPDelegateCalendarAccountDaoImpl implements
		IDelegateCalendarAccountDao {

	private static final String WILDCARD = "*";

	private final Log log = LogFactory.getLog(this.getClass());
	
	private LdapOperations ldapTemplate;
	private LDAPAttributesKey ldapAttributesKey = new LDAPAttributesKeyImpl();
	private String baseDn = "o=isp";
	private long searchResultsLimit = 25L;
	private int searchTimeLimit = 10000;
	private boolean treatOwnerAttributeAsDistinguishedName = false;
	
	/**
	 * @param ldapTemplate the ldapTemplate to set
	 */
	@Autowired
	public void setLdapTemplate(LdapOperations ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}
	/**
	 * @param ldapAttributesKey the ldapAttributesKey to set
	 */
	@Autowired
	public void setLdapAttributesKey(LDAPAttributesKey ldapAttributesKey) {
		this.ldapAttributesKey = ldapAttributesKey;
	}
	/**
	 * @param baseDn the baseDn to set
	 */
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
	/**
	 * @param searchResultsLimit the searchResultsLimit to set
	 */
	public void setSearchResultsLimit(long searchResultsLimit) {
		this.searchResultsLimit = searchResultsLimit;
	}
	/**
	 * @param searchTimeLimit the searchTimeLimit to set (in milliseconds)
	 */
	public void setSearchTimeLimit(int searchTimeLimit) {
		this.searchTimeLimit = searchTimeLimit;
	}
	/**
	 * @return the treatOwnerAttributeAsDistinguishedName
	 */
	public boolean isTreatOwnerAttributeAsDistinguishedName() {
		return treatOwnerAttributeAsDistinguishedName;
	}
	/**
	 * @param treatOwnerAttributeAsDistinguishedName the treatOwnerAttributeAsDistinguishedName to set
	 */
	public void setTreatOwnerAttributeAsDistinguishedName(
			boolean treatOwnerAttributeAsDistinguishedName) {
		this.treatOwnerAttributeAsDistinguishedName = treatOwnerAttributeAsDistinguishedName;
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#searchForDelegates(java.lang.String, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public List<IDelegateCalendarAccount> searchForDelegates(String searchText,
			ICalendarAccount owner) {
		String searchTextInternal = searchText.replace(" ", WILDCARD);
		if(!searchTextInternal.endsWith(WILDCARD)) {
			searchTextInternal += WILDCARD;
		}

		AndFilter searchFilter = new AndFilter();
		
		// inner orFilter searches on displayName and username
		OrFilter orFilter = new OrFilter();
		orFilter.or(new LikeFilter(ldapAttributesKey.getDisplayNameAttributeName(), searchTextInternal));
		orFilter.or(new LikeFilter(ldapAttributesKey.getUsernameAttributeName(), searchTextInternal));
		
		// if the owner isn't null and owner attribute is NOT a DN, include the owner id in the search
		if (owner != null && !isTreatOwnerAttributeAsDistinguishedName()) {
			// TODO assumes delegateOwnerAttributeName has values of ICalendarAccount#getUsername
			searchFilter.and(new EqualsFilter(ldapAttributesKey.getDelegateOwnerAttributeName(), owner.getUsername()));
		}
		// and the orFilter with filter to assert our results have calendar unique ids 
		searchFilter.and(orFilter);
		searchFilter.and(new LikeFilter(ldapAttributesKey.getUniqueIdentifierAttributeName(), WILDCARD));

		List<IDelegateCalendarAccount> results = new ArrayList<IDelegateCalendarAccount>(executeSearchReturnList(searchFilter, owner));
		return results;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#searchForDelegates(java.lang.String)
	 */
	@Override
	public List<IDelegateCalendarAccount> searchForDelegates(String searchText) {
		return searchForDelegates(searchText, null);
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#getDelegate(java.lang.String)
	 */
	@Override
	public IDelegateCalendarAccount getDelegate(String accountName) {
		return getDelegate(accountName, null);
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#getDelegate(java.lang.String, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public IDelegateCalendarAccount getDelegate(String accountName,
			ICalendarAccount owner) {
		AndFilter searchFilter = new AndFilter();
		searchFilter.and(new EqualsFilter(ldapAttributesKey.getDisplayNameAttributeName(), accountName));
		if(owner != null && !isTreatOwnerAttributeAsDistinguishedName()) {
			// TODO assumes delegateOwnerAttributeName has values of ICalendarAccount#getUsername
			searchFilter.and(new EqualsFilter(ldapAttributesKey.getDelegateOwnerAttributeName(), owner.getUsername()));
		}
		searchFilter.and(new LikeFilter(ldapAttributesKey.getUniqueIdentifierAttributeName(), WILDCARD));

		List<LDAPDelegateCalendarAccountImpl> results = executeSearchReturnList(searchFilter, owner);
		LDAPDelegateCalendarAccountImpl delegate = (LDAPDelegateCalendarAccountImpl) DataAccessUtils.singleResult(results);
		return delegate;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#getDelegateByUniqueId(java.lang.String)
	 */
	@Override
	public IDelegateCalendarAccount getDelegateByUniqueId(String accountUniqueId) {
		return getDelegateByUniqueId(accountUniqueId, null);
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.IDelegateCalendarAccountDao#getDelegateByUniqueId(java.lang.String, org.jasig.schedassist.model.ICalendarAccount)
	 */
	@Override
	public IDelegateCalendarAccount getDelegateByUniqueId(
			String accountUniqueId, ICalendarAccount owner) {
		AndFilter searchFilter = new AndFilter();
		searchFilter.and(new EqualsFilter(ldapAttributesKey.getUniqueIdentifierAttributeName(), accountUniqueId));
		if(owner != null && !isTreatOwnerAttributeAsDistinguishedName()) {
			// TODO assumes delegateOwnerAttributeName has values of ICalendarAccount#getUsername
			searchFilter.and(new EqualsFilter(ldapAttributesKey.getDelegateOwnerAttributeName(), owner.getUsername()));
		}
		List<LDAPDelegateCalendarAccountImpl> results = executeSearchReturnList(searchFilter, owner);
		LDAPDelegateCalendarAccountImpl delegate = (LDAPDelegateCalendarAccountImpl) DataAccessUtils.singleResult(results);
		return delegate;
	}

	/**
	 * 
	 * @param searchFilter
	 * @param owner
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<LDAPDelegateCalendarAccountImpl> executeSearchReturnList(final Filter searchFilter, final ICalendarAccount owner) {
		SearchControls searchControls = new SearchControls();
		searchControls.setCountLimit(searchResultsLimit);
		searchControls.setTimeLimit(searchTimeLimit);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		
		List<LDAPDelegateCalendarAccountImpl> results = Collections.emptyList();
		try {
			results = ldapTemplate.search(
				baseDn, 
				searchFilter.toString(), 
				searchControls, 
				new DefaultDelegateAccountAttributesMapperImpl(ldapAttributesKey, owner));
			if(log.isDebugEnabled()) {
				log.debug("search " + searchFilter + " returned " + results.size() + " results");
			}
			
			if(isTreatOwnerAttributeAsDistinguishedName() && owner != null && owner instanceof HasDistinguishedName) {
				HasDistinguishedName ldapOwnerAccount = (HasDistinguishedName) owner;
				enforceDistinguishedNameMatch(results, ldapOwnerAccount);
			}
			Collections.sort(results, new DelegateDisplayNameComparator());
		} catch (SizeLimitExceededException e) {
			log.debug("search filter exceeded size limit (" + searchResultsLimit + "): " + searchFilter);
		} catch (TimeLimitExceededException e) {
			log.debug("search filter exceeded time limit(" + searchTimeLimit + " milliseconds): " + searchFilter);
		}
		return results;
	}

	/**
	 * Iterate over delegates, removing elements that have mismatched DN to owner
	 * @param delegates
	 * @param desiredOwnerAccount
	 */
	protected void enforceDistinguishedNameMatch(List<LDAPDelegateCalendarAccountImpl> delegates, HasDistinguishedName desiredOwnerAccount) {
		for(Iterator<LDAPDelegateCalendarAccountImpl> i = delegates.iterator(); i.hasNext(); ) {
			LDAPDelegateCalendarAccountImpl delegate = i.next();
			String ownerAttributeValue = delegate.getAccountOwnerUsername();
			if(!desiredOwnerAccount.getDistinguishedName().equals(new DistinguishedName(ownerAttributeValue))) {
				if(log.isDebugEnabled()) {
					log.debug(ownerAttributeValue + " does not match desired owner ICalendarAccount dn: " + desiredOwnerAccount.getDistinguishedName());
				}
				i.remove();
			}
		}
	}
	/**
	 * Simple {@link Comparator} for {@link IDelegateCalendarAccount} that compares
	 * on the displayName field.
	 *
	 * @author Nicholas Blair
	 * @version $Id: LDAPDelegateCalendarAccountDaoImpl $
	 */
	private static class DelegateDisplayNameComparator implements Comparator<IDelegateCalendarAccount>{
		@Override
		public int compare(IDelegateCalendarAccount o1,
				IDelegateCalendarAccount o2) {
			return new CompareToBuilder().append(o1.getDisplayName(), o2.getDisplayName()).toComparison();
		}
	}
}
