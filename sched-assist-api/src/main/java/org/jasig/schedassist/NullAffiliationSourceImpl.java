/**
 * Copyright 2011 The Board of Regents of the University of Wisconsin System.
 */
package org.jasig.schedassist;

import org.jasig.schedassist.model.IAffiliation;
import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Empty {@link IAffiliationSource} implementation; knows of no affiliations.
 * 
 * @author Nicholas Blair
 * @version $Id: NullAffiliationSourceImpl.java $
 */
public class NullAffiliationSourceImpl implements IAffiliationSource {

	/**
	 * Always returns false.
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.IAffiliationSource#doesAccountHaveAffiliation(org.jasig.schedassist.model.ICalendarAccount, org.jasig.schedassist.model.IAffiliation)
	 */
	@Override
	public boolean doesAccountHaveAffiliation(ICalendarAccount calendarAccount,
			IAffiliation affiliation) {
		return false;
	}

}
