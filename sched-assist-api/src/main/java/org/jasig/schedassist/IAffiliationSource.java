/**
 * Copyright 2011 The Board of Regents of the University of Wisconsin System.
 */
package org.jasig.schedassist;

import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Interface definition operations regarding account "affiliations" (e.g. instructor, advisor, et al).
 * 
 * @author Nicholas Blair
 * @version $Id: IAffiliationSource.java $
 */
public interface IAffiliationSource {

	/**
	 * 
	 * @param calendarAccount
	 * @param affiliation
	 * @return true if the account has the specified affiliation
	 */
	boolean doesAccountHaveAffiliation(ICalendarAccount calendarAccount, String affiliation);
}
