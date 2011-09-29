package org.jasig.schedassist.impl.ldap;

import java.util.Map;

import org.jasig.schedassist.model.ICalendarAccount;

/**
 * Interface definining the LDAP attributes that are used to bind
 * to {@link ICalendarAccount} fields.
 * 
 * @author Nicholas Blair
 * @version $Id: LdapAttributesKey.java $
 */
public interface LDAPAttributesKey {

	/**
	 * @return the usernameAttributeName
	 */
	public String getUsernameAttributeName();

	/**
	 * @return the displayNameAttributeName
	 */
	public String getDisplayNameAttributeName();

	/**
	 * @return the eligibilityAttributeName
	 */
	public String getEligibilityAttributeName();

	/**
	 * @return the emailAddressAttributeName
	 */
	public String getEmailAddressAttributeName();

	/**
	 * @return the uniqueIdentifierAttributeName
	 */
	public String getUniqueIdentifierAttributeName();

	/**
	 * @return the delegateOwnerAttributeName
	 */
	public String getDelegateOwnerAttributeName();
	
	/**
	 * @return the delegateLocationAttributeName
	 */
	public String getDelegateLocationAttributeName();
	/**
	 * @return the delegateContactInformationAttributeName
	 */
	public String getDelegateContactInformationAttributeName();
	
	/**
	 * Return true if the value of the eligibilityAttribute equates to the
	 * account being eligible for service.
	 * 
	 * The reason for this method is due to the LDAP attribute being a String 
	 * that may have different interpretations. It could just be "true"; "Y" or "not empty" 
	 * could also evaluate to true eligibility.
	 * 
	 * @return true if the value of the attribute means the account is eligible for service
	 */
	public boolean evaluateEligibilityAttributeValue(Map<String, String> attributes);

}