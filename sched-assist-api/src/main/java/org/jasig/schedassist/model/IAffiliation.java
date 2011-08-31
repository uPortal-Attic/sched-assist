/**
 * Copyright 2011 The Board of Regents of the University of Wisconsin System.
 */
package org.jasig.schedassist.model;

/**
 * Simple interface to describe a calendar account's affiliation with the
 * university or organization.
 * 
 * @author Nicholas Blair
 * @version $Id: IAffiliation.java $
 */
public interface IAffiliation {

	/**
	 * 
	 * @return a descriptive name for the affiliation
	 */
	String getName();
}
