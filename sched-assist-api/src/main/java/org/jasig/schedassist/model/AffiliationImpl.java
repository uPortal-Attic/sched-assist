/**
 * Copyright 2011 The Board of Regents of the University of Wisconsin System.
 */
package org.jasig.schedassist.model;

/**
 * Default {@link IAffiliation} implementation.
 * 
 * @author Nicholas Blair
 * @version $Id: AffiliationImpl.java $
 */
public class AffiliationImpl implements IAffiliation {

	public static final AffiliationImpl ADVISOR = new AffiliationImpl("advisor");
	public static final AffiliationImpl INSTRUCTOR = new AffiliationImpl("instructor");
	
	private final String name;
	/**
	 * 
	 * @param name
	 */
	public AffiliationImpl(String name) {
		this.name = name;
	}
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.model.IAffiliation#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AffiliationImpl other = (AffiliationImpl) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AffiliationImpl [name=" + name + "]";
	}

}
