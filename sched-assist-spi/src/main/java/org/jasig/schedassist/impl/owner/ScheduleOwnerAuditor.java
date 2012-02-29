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

package org.jasig.schedassist.impl.owner;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * Tool for auditing {@link IScheduleOwner} records.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ScheduleOwnerAuditor.java 2400 2010-08-19 18:12:29Z npblair $
 */
public final class ScheduleOwnerAuditor extends SimpleJdbcDaoSupport {

	/**
	 * Set a {@link System} property with this name to specify an alternate location for the Spring {@link ApplicationContext} used by {@link #main(String[])}.
	 */
	public static final String CONFIG_SYSTEM_PROPERTY = "org.jasig.schedassist.impl.owner.ScheduleOwnerAuditor.CONFIG";
	public static final String CONFIG = System.getProperty(
			CONFIG_SYSTEM_PROPERTY, 
			"cli-tools.xml");
	private final Log LOG = LogFactory.getLog(this.getClass());
	private OwnerDao ownerDao;
	private ICalendarAccountDao calendarAccountDao;
	
	private boolean purge = false;
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(@Qualifier("composite") ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}

	/**
	 * @param purge the purge to set
	 */
	public void setPurge(boolean purge) {
		this.purge = purge;
	}
	/**
	 * 
	 * @return a {@link List} of all {@link PersistenceScheduleOwner}s in the database
	 */
	protected List<PersistenceScheduleOwner> gatherAllScheduleOwnerRecords() {
		List<PersistenceScheduleOwner> ownerRecords = this.getSimpleJdbcTemplate().query(
				"select * from owners", 
				new PersistenceScheduleOwnerRowMapper());
		return ownerRecords;
	}

	/**
	 * Verify (via the {@link CalendarUserDao}) that a {@link CalendarUser} still
	 * exists and is eligible for the specified {@link PersistenceScheduleOwner}.
	 * 
	 * @param owner
	 * @param purge
	 */
	protected void auditRecord(final PersistenceScheduleOwner owner) {
		// look for owner by username
		ICalendarAccount byUsername = this.calendarAccountDao.getCalendarAccount(owner.getUsername());
		if(null == byUsername || !byUsername.isEligible()) {
			LOG.debug("no calendar account found for username " + owner.getUsername());
			// check by uniqueId
			ICalendarAccount byUniqueId = this.calendarAccountDao.getCalendarAccountFromUniqueId(owner.getCalendarUniqueId());
			if(null != byUniqueId && byUniqueId.isEligible()) {
				// this means that the customer must have changed their NetID, and our records need an update
				LOG.warn("found calendarAccount by unique id that has different username from records, owner record: " + owner + ", account: " +byUniqueId);
				int rows = this.getSimpleJdbcTemplate().update("update owners set username = ? where external_unique_id = ?",
						byUniqueId.getUsername(),
						owner.getCalendarUniqueId());
				if(rows == 1) {
					LOG.warn("successfully updated record for " + byUniqueId);
				} else {
					LOG.error("failed to update records for " + byUniqueId);
				}
				return;
			}
			LOG.warn("no ICalendarAccount found for record: " + owner);
		} else {
			// user still valid, do nothing
			LOG.info(owner + " still eligible, corresponds to ICalendarAccount: " + byUsername);
			return;
		}


		if(this.purge) {
			// we can't call OwnerDao#locateOwnerByAvailableId because that includes a lookup to the CalendarUserDao, which we know fails
			// make a CalendarUser sufficient enough for use
			ICalendarAccount ineligible = constructCalendarUserForRemoval(owner);

			IScheduleOwner ownerAccount = ownerDao.locateOwner(ineligible);
			ownerDao.removeAccount(ownerAccount);
			LOG.warn("removed record: " + owner);
		}
	}

	/**
	 * Construct a {@link ICalendarAccount} sufficient to locate a {@link IScheduleOwner} for removal
	 * 
	 * @see #auditRecord(PersistenceScheduleOwner)
	 * @param owner
	 * @return
	 */
	private ICalendarAccount constructCalendarUserForRemoval(final PersistenceScheduleOwner owner) {
		DeactivatingCalendarAccount ineligible = new DeactivatingCalendarAccount();
		ineligible.setCalendarUniqueId(owner.getCalendarUniqueId());
		ineligible.setUsername(owner.getUsername());
		return ineligible;
	}

	/**
	 * Depends on the presence of a Spring {@link ApplicationContext} at the location
	 * on the classpath defined by the {@link #CONFIG_SYSTEM_PROPERTY} System property.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
		ScheduleOwnerAuditor auditor = (ScheduleOwnerAuditor) context.getBean("scheduleOwnerAuditor");
		List<PersistenceScheduleOwner> allOwnerRecords = auditor.gatherAllScheduleOwnerRecords();
		for(PersistenceScheduleOwner owner : allOwnerRecords) {
			auditor.auditRecord(owner);
		}
	}

	/**
	 * Internal representation of {@link ICalendarAccount} to be used
	 * for deactivated accounts.
	 * It only contains the 2 fields we know from the owners table in the database:
	 * calendarUniqueId (external_unique_id column) and username (username column).
	 *
	 * All other methods/fields I return null.
	 * 
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: ScheduleOwnerAuditor.java 2400 2010-08-19 18:12:29Z npblair $
	 */
	static final class DeactivatingCalendarAccount implements ICalendarAccount {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String calendarUniqueId;
		private String username;

		@Override
		public String getAttributeValue(String attributeName) {
			return null;
		}

		@Override
		public Map<String, List<String>> getAttributes() {
			return null;
		}

		@Override
		public String getCalendarLoginId() {
			return null;
		}

		@Override
		public String getCalendarUniqueId() {
			return this.calendarUniqueId;
		}

		@Override
		public String getDisplayName() {
			return null;
		}

		@Override
		public String getEmailAddress() {
			return null;
		}

		@Override
		public String getUsername() {
			return this.username;
		}

		@Override
		public boolean isEligible() {
			return false;
		}

		/**
		 * @param calendarUniqueId the calendarUniqueId to set
		 */
		public void setCalendarUniqueId(String calendarUniqueId) {
			this.calendarUniqueId = calendarUniqueId;
		}
		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}

		@Override
		public List<String> getAttributeValues(String attributeName) {
			return null;
		}
	}
}
