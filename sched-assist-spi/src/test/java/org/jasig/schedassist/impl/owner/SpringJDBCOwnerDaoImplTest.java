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

import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.MeetingDurations;
import org.jasig.schedassist.model.Preferences;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

/**
 * Test harness for {@link SpringJDBCOwnerDaoImpl}.
 * 
 * Depends on {@link NeedsTestDatabase}.
 *  
 * @author Nicholas Blair
 */
public class SpringJDBCOwnerDaoImplTest extends NeedsTestDatabase {

	private SpringJDBCOwnerDaoImpl ownerDao;
	private MockCalendarAccountDao calendarAccountDao;
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(SpringJDBCOwnerDaoImpl ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(MockCalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}

	@Override
	public void afterCreate() {
	}
	@Override
	public void afterDestroy() {
		// always clear the CalendarUserDao in case a mock was temporarily set
		ownerDao.setCalendarAccountDao(null);
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRegister() throws Exception {
		ICalendarAccount user1 = this.calendarAccountDao.getCalendarAccount("user1");
		Assert.assertNotNull(user1);
		IScheduleOwner owner1 = ownerDao.register(user1);
		Assert.assertNotNull(owner1);
		Assert.assertEquals(1, owner1.getId());
		Map<Preferences, String> ownerPrefs = owner1.getPreferences();
		Assert.assertEquals(Preferences.getDefaultPreferences(), ownerPrefs);
		
		// test lookup methods
		Assert.assertEquals("user1", ownerDao.lookupUsername(1));
		Assert.assertEquals("10000:00001", ownerDao.lookupUniqueId(1));
		
		ICalendarAccount user2 = this.calendarAccountDao.getCalendarAccount("user2");
		Assert.assertNotNull(user2);
		IScheduleOwner owner2 = ownerDao.register(user2);
		Assert.assertNotNull(owner2);
		Assert.assertEquals(2, owner2.getId());
		Map<Preferences, String> owner2Prefs = owner2.getPreferences();
		Assert.assertEquals(Preferences.getDefaultPreferences(), owner2Prefs);
		
		Assert.assertEquals("user2", ownerDao.lookupUsername(2));
		Assert.assertEquals("10000:00002", ownerDao.lookupUniqueId(2));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRegisterAndRemove() throws Exception {
		ICalendarAccount user1 = this.calendarAccountDao.getCalendarAccount("user1");
		Assert.assertNotNull(user1);
		IScheduleOwner owner1 = ownerDao.register(user1);
		Assert.assertNotNull(owner1);
		Assert.assertEquals(1, owner1.getId());
		Map<Preferences, String> ownerPrefs = owner1.getPreferences();
		Assert.assertEquals(Preferences.getDefaultPreferences(), ownerPrefs);
		
		// test lookup methods
		Assert.assertEquals("user1", ownerDao.lookupUsername(1));
		Assert.assertEquals("10000:00001", ownerDao.lookupUniqueId(1));
		
		ownerDao.removeAccount(owner1);
		Assert.assertNull(ownerDao.lookupUsername(1));
		Assert.assertNull(ownerDao.lookupUniqueId(1));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRegisterTwice() throws Exception {
		ICalendarAccount user1 = this.calendarAccountDao.getCalendarAccount("user1");
		Assert.assertNotNull(user1);
		IScheduleOwner owner1 = ownerDao.register(user1);
		Assert.assertNotNull(owner1);
		Assert.assertEquals(1, owner1.getId());
		Map<Preferences, String> ownerPrefs = owner1.getPreferences();
		Assert.assertEquals(Preferences.getDefaultPreferences(), ownerPrefs);
		
		// test lookup methods
		Assert.assertEquals("user1", ownerDao.lookupUsername(1));
		Assert.assertEquals("10000:00001", ownerDao.lookupUniqueId(1));
		
		// try to register again
		IScheduleOwner attempt2 = ownerDao.register(user1);
		Assert.assertEquals(owner1, attempt2);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdatePreference() throws Exception {
		ICalendarAccount user1 = this.calendarAccountDao.getCalendarAccount("user1");
		Assert.assertNotNull(user1);
		IScheduleOwner owner1 = ownerDao.register(user1);
		Assert.assertNotNull(owner1);
		Assert.assertEquals(1, owner1.getId());
		Map<Preferences, String> ownerPrefs = owner1.getPreferences();
		Assert.assertEquals(Preferences.getDefaultPreferences(), ownerPrefs);
		
		owner1 = ownerDao.updatePreference(owner1, Preferences.LOCATION, "My new office!");
		Assert.assertEquals("My new office!", owner1.getPreference(Preferences.LOCATION));
		
		String storedValue = ownerDao.retreivePreference(owner1, Preferences.LOCATION);
		Assert.assertEquals("My new office!", storedValue);
		
		MeetingDurations maxDurations = MeetingDurations.fromKey("240,480");
		owner1 = ownerDao.updatePreference(owner1, Preferences.DURATIONS, maxDurations.getKey());
		
		Assert.assertEquals(480, owner1.getPreferredMeetingDurations().getMaxLength());
		Assert.assertEquals(240, owner1.getPreferredMeetingDurations().getMinLength());
		
		owner1 = ownerDao.updatePreference(owner1, Preferences.DURATIONS, Preferences.DURATIONS.getDefaultValue());
		Assert.assertEquals(30, owner1.getPreferredMeetingDurations().getMaxLength());
		Assert.assertEquals(30, owner1.getPreferredMeetingDurations().getMinLength());
	}
	
	/**
	 * <ol>
	 * <li>Register a calendar account, set some preferences.</li>
	 * <li>Change the calendar account's unique id.</li>
	 * <li>Verify the ownerDao returns the same schedule owner</li>
	 * </ol>
	 * 
	 * @throws IneligibleException 
	 */
	@Test
	public void testExternalUniqueIdChanges() throws IneligibleException {
		ICalendarAccount user = this.calendarAccountDao.getCalendarAccount("user4");
		Assert.assertNotNull(user);
		IScheduleOwner owner = ownerDao.register(user);
		Assert.assertNotNull(owner);
		Assert.assertEquals(1, owner.getId());
		Map<Preferences, String> ownerPrefs = owner.getPreferences();
		Assert.assertEquals(Preferences.getDefaultPreferences(), ownerPrefs);
		
		IScheduleOwner updated = ownerDao.updatePreference(owner, Preferences.LOCATION, "My new office!");
		Assert.assertEquals("My new office!", updated.getPreference(Preferences.LOCATION));
		
		String storedValue = ownerDao.retreivePreference(owner, Preferences.LOCATION);
		Assert.assertEquals("My new office!", storedValue);
		
		ICalendarAccount updatedUser = this.calendarAccountDao.changeAccountUniqueId(user, "23000:10001");
		
		IScheduleOwner target = this.ownerDao.locateOwner(updatedUser);
		Assert.assertNotNull(target);
		Assert.assertEquals(updated.getId(), target.getId());
		Assert.assertEquals(updated.getPreferredLocation(), target.getPreferredLocation());
	}
	
	/**
	 * <ol>
	 * <li>Register a calendar account, set some preferences.</li>
	 * <li>Change the calendar account's username</li>
	 * <li>Verify the ownerDao returns the same schedule owner</li>
	 * </ol>
	 * 
	 * @throws IneligibleException 
	 */
	@Test
	public void testUsernameChanges() throws IneligibleException {
		ICalendarAccount user = this.calendarAccountDao.getCalendarAccount("user5");
		Assert.assertNotNull(user);
		IScheduleOwner owner = ownerDao.register(user);
		Assert.assertNotNull(owner);
		Assert.assertEquals(1, owner.getId());
		Map<Preferences, String> ownerPrefs = owner.getPreferences();
		Assert.assertEquals(Preferences.getDefaultPreferences(), ownerPrefs);
		
		IScheduleOwner updated = ownerDao.updatePreference(owner, Preferences.LOCATION, "My new office!");
		Assert.assertEquals("My new office!", updated.getPreference(Preferences.LOCATION));
		
		String storedValue = ownerDao.retreivePreference(owner, Preferences.LOCATION);
		Assert.assertEquals("My new office!", storedValue);
		
		ICalendarAccount updatedUser = this.calendarAccountDao.changeAccountUsername(user, "altered-username5");
		
		IScheduleOwner target = this.ownerDao.locateOwner(updatedUser);
		Assert.assertNotNull(target);
		Assert.assertEquals(updated.getId(), target.getId());
		Assert.assertEquals(updated.getPreferredLocation(), target.getPreferredLocation());
	}
	
	/**
	 * <ol>
	 * <li>Register a calendar account, set some preferences.</li>
	 * <li>Change the calendar account's username</li>
	 * <li>Verify the ownerDao returns the same schedule owner</li>
	 * </ol>
	 * 
	 * Takes a slightly different path than {@link #testUsernameChanges()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLookupByIdUsernameChanged() throws Exception {
		ICalendarAccount user = this.calendarAccountDao.getCalendarAccount("user6");
		Assert.assertNotNull(user);
		IScheduleOwner owner = ownerDao.register(user);
		Assert.assertNotNull(owner);
		Assert.assertEquals(1, owner.getId());
		Map<Preferences, String> ownerPrefs = owner.getPreferences();
		Assert.assertEquals(Preferences.getDefaultPreferences(), ownerPrefs);
		
		IScheduleOwner updated = ownerDao.updatePreference(owner, Preferences.LOCATION, "My new office!");
		Assert.assertEquals("My new office!", updated.getPreference(Preferences.LOCATION));
		
		String storedValue = ownerDao.retreivePreference(owner, Preferences.LOCATION);
		Assert.assertEquals("My new office!", storedValue);
		
		ICalendarAccount updatedUser = this.calendarAccountDao.changeAccountUsername(user, "altered-username6");
		Assert.assertEquals("altered-username6", updatedUser.getUsername());
		
		// ownerDao needs a reference to the calendardao
		this.ownerDao.setCalendarAccountDao(this.calendarAccountDao);
		IScheduleOwner target = this.ownerDao.locateOwnerByAvailableId(1L);
		Assert.assertNotNull(target);
		Assert.assertEquals(updated.getId(), target.getId());
		Assert.assertEquals(updated.getPreferredLocation(), target.getPreferredLocation());
		Assert.assertEquals(updatedUser.getUsername(), target.getCalendarAccount().getUsername());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Before
	public void createDatabase() throws Exception {
		Resource createDdl = (Resource) this.applicationContext.getBean("createDdl");
		
		SimpleJdbcTemplate template = new SimpleJdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		SimpleJdbcTestUtils.executeSqlScript(template, createDdl, false);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@After
	public void destroyDatabase() throws Exception {
		Resource destroyDdl = (Resource) this.applicationContext.getBean("destroyDdl");
		
		String sql = IOUtils.toString(destroyDdl.getInputStream());
		JdbcTemplate template = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		template.execute(sql);
		
		// always clear the CalendarUserDao in case a mock was temporarily set
		ownerDao.setCalendarAccountDao(null);
	}
}
