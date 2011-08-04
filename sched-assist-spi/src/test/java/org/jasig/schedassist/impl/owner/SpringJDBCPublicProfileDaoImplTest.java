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

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.PublicProfile;
import org.jasig.schedassist.model.PublicProfileId;
import org.jasig.schedassist.model.PublicProfileTag;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link SpringJDBCPublicProfileDaoImpl}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SpringJDBCPublicProfileDaoImplTest.java 2243 2010-06-25 21:08:38Z npblair $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:database-test.xml"})
public class SpringJDBCPublicProfileDaoImplTest extends AbstractJUnit4SpringContextTests {

	private SpringJDBCOwnerDaoImpl ownerDao;
	private SpringJDBCPublicProfileDaoImpl publicProfileDao;
	private ICalendarAccountDao calendarAccountDao;

	/**
	 * @param publicProfileDao the publicProfileDao to set
	 */
	@Autowired
	public void setPublicProfileDao(SpringJDBCPublicProfileDaoImpl publicProfileDao) {
		this.publicProfileDao = publicProfileDao;
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(SpringJDBCOwnerDaoImpl ownerDao) {
		this.ownerDao = ownerDao;
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreatePublicProfile()  {
		ICalendarAccount user1 = this.calendarAccountDao.getCalendarAccount("username1");
		Assert.assertNotNull(user1);
		IScheduleOwner owner1 = null;
		try {
			owner1 = ownerDao.register(user1);
		} catch (IneligibleException e) {
			Assert.fail("unexpected IneligibleException");
		}
		Assert.assertNotNull(owner1);
		Assert.assertEquals(1, owner1.getId());
		owner1 = ownerDao.updatePreference(owner1, Preferences.NOTEBOARD, "test noteboard");
		
		PublicProfile profile = null;
		try {
			profile = this.publicProfileDao.createPublicProfile(owner1, "test create public profile");
		} catch (PublicProfileAlreadyExistsException e) {
			Assert.fail("unexpected PublicProfileAlreadyExistsException");
		}
		Assert.assertNotNull(profile);
		Assert.assertEquals(1, profile.getOwnerId());
		Assert.assertEquals("test create public profile", profile.getDescription());
		Assert.assertEquals(8, profile.getPublicProfileId().getProfileKey().length());
		Assert.assertEquals(user1.getDisplayName(), profile.getPublicProfileId().getOwnerDisplayName());
		Assert.assertEquals("test noteboard", profile.getOwnerNoteboard());
		
		PublicProfile retrieveByKey = this.publicProfileDao.locatePublicProfileByKey(profile.getPublicProfileId().getProfileKey());
		Assert.assertEquals(profile, retrieveByKey);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreatePublicProfileWithTags()  {
		ICalendarAccount user1 = this.calendarAccountDao.getCalendarAccount("username1");
		Assert.assertNotNull(user1);
		IScheduleOwner owner1 = null;
		try {
			owner1 = ownerDao.register(user1);
		} catch (IneligibleException e) {
			Assert.fail("unexpected IneligibleException");
		}
		Assert.assertNotNull(owner1);
		Assert.assertEquals(1, owner1.getId());
		owner1 = ownerDao.updatePreference(owner1, Preferences.NOTEBOARD, "test noteboard");
		
		PublicProfile profile = null;
		try {
			profile = this.publicProfileDao.createPublicProfile(owner1, "test create public profile");
		} catch (PublicProfileAlreadyExistsException e) {
			Assert.fail("unexpected PublicProfileAlreadyExistsException");
		}
		Assert.assertNotNull(profile);
		Assert.assertEquals(1, profile.getOwnerId());
		Assert.assertEquals("test create public profile", profile.getDescription());
		Assert.assertEquals(8, profile.getPublicProfileId().getProfileKey().length());
		Assert.assertEquals(user1.getDisplayName(), profile.getPublicProfileId().getOwnerDisplayName());
		Assert.assertEquals("test noteboard", profile.getOwnerNoteboard());
		
		PublicProfile retrieveByKey = this.publicProfileDao.locatePublicProfileByKey(profile.getPublicProfileId().getProfileKey());
		Assert.assertEquals(profile, retrieveByKey);
		
		List<String> tags = Arrays.asList(new String[] { "math" });
		PublicProfileId profileId = profile.getPublicProfileId();
		List<PublicProfileTag> storedTags = this.publicProfileDao.setProfileTags(tags, profileId);
		Assert.assertNotNull(storedTags);
		Assert.assertEquals(1, storedTags.size());
		Assert.assertEquals("math", storedTags.get(0).getTagDisplay());
		Assert.assertEquals("MATH", storedTags.get(0).getTag());
		
		List<PublicProfileId> idSearchByTag = this.publicProfileDao.getPublicProfileIdsWithTag("math");
		Assert.assertEquals(1, idSearchByTag.size());
		Assert.assertEquals(profileId, idSearchByTag.get(0));
		// check case insensitive
		idSearchByTag = this.publicProfileDao.getPublicProfileIdsWithTag("mATh");
		Assert.assertEquals(1, idSearchByTag.size());
		Assert.assertEquals(profileId, idSearchByTag.get(0));
		
		tags = Arrays.asList(new String[] { "Computer Science", "DoIT" });
		storedTags = this.publicProfileDao.setProfileTags(tags, profileId);
		Assert.assertNotNull(storedTags);
		Assert.assertEquals(2, storedTags.size());
		Assert.assertEquals("Computer Science", storedTags.get(0).getTagDisplay());
		Assert.assertEquals("COMPUTER SCIENCE", storedTags.get(0).getTag());
		Assert.assertEquals("DoIT", storedTags.get(1).getTagDisplay());
		Assert.assertEquals("DOIT", storedTags.get(1).getTag());
		
		tags = Arrays.asList(new String[] {  });
		storedTags = this.publicProfileDao.setProfileTags(tags, profileId);
		Assert.assertNotNull(storedTags);
		Assert.assertEquals(0, storedTags.size());
	}
	
	@Test
	public void testCreatePublicProfileTwice()  {
		ICalendarAccount user1 = this.calendarAccountDao.getCalendarAccount("username1");
		Assert.assertNotNull(user1);
		IScheduleOwner owner1 = null;
		try {
			owner1 = ownerDao.register(user1);
		} catch (IneligibleException e) {
			Assert.fail("unexpected IneligibleException");
		}
		Assert.assertNotNull(owner1);
		Assert.assertEquals(1, owner1.getId());
		owner1 = ownerDao.updatePreference(owner1, Preferences.NOTEBOARD, "test noteboard");
		
		PublicProfile profile = null;
		try {
			profile = this.publicProfileDao.createPublicProfile(owner1, "test create public profile twice");
		} catch (PublicProfileAlreadyExistsException e) {
			Assert.fail("unexpected PublicProfileAlreadyExistsException");
		}
		Assert.assertNotNull(profile);
		Assert.assertEquals(1, profile.getOwnerId());
		Assert.assertEquals("test create public profile twice", profile.getDescription());
		Assert.assertEquals(8, profile.getPublicProfileId().getProfileKey().length());
		Assert.assertEquals(user1.getDisplayName(), profile.getPublicProfileId().getOwnerDisplayName());
		Assert.assertEquals("test noteboard", profile.getOwnerNoteboard());
		
		try {
			this.publicProfileDao.createPublicProfile(owner1, "test should fail");
			Assert.fail("expected PublicProfileAlreadyExistsException not thrown");
		} catch (PublicProfileAlreadyExistsException e) {
			// success
		}
		
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdatePublicProfileDescription()  {
		ICalendarAccount user1 = this.calendarAccountDao.getCalendarAccount("username1");
		Assert.assertNotNull(user1);
		IScheduleOwner owner1 = null;
		try {
			owner1 = ownerDao.register(user1);
		} catch (IneligibleException e) {
			Assert.fail("unexpected IneligibleException");
		}
		Assert.assertNotNull(owner1);
		Assert.assertEquals(1, owner1.getId());
		owner1 = ownerDao.updatePreference(owner1, Preferences.NOTEBOARD, "test noteboard");
		
		PublicProfile profile = null;
		try {
			profile = this.publicProfileDao.createPublicProfile(owner1, "test create public profile");
		} catch (PublicProfileAlreadyExistsException e) {
			Assert.fail("unexpected PublicProfileAlreadyExistsException");
		}
		final String key = profile.getPublicProfileId().getProfileKey();
		Assert.assertNotNull(profile);
		Assert.assertEquals(1, profile.getOwnerId());
		Assert.assertEquals("test create public profile", profile.getDescription());
		Assert.assertEquals(8, key.length());
		Assert.assertEquals(user1.getDisplayName(), profile.getPublicProfileId().getOwnerDisplayName());
		Assert.assertEquals("test noteboard", profile.getOwnerNoteboard());
		
		PublicProfile updated = this.publicProfileDao.updatePublicProfileDescription(profile.getPublicProfileId(), "test update public profile");
		Assert.assertNotNull(updated);
		Assert.assertEquals(1, updated.getOwnerId());
		Assert.assertEquals(key, updated.getPublicProfileId().getProfileKey());
		Assert.assertEquals(user1.getDisplayName(), updated.getPublicProfileId().getOwnerDisplayName());
		Assert.assertEquals("test noteboard", updated.getOwnerNoteboard());
		Assert.assertEquals("test update public profile", updated.getDescription());
	}
	
	/**
	 * 
	 */
	@Test
	public void testRemovePublicProfile() {
		ICalendarAccount user1 = this.calendarAccountDao.getCalendarAccount("username1");
		Assert.assertNotNull(user1);
		IScheduleOwner owner1 = null;
		try {
			owner1 = ownerDao.register(user1);
		} catch (IneligibleException e) {
			Assert.fail("unexpected IneligibleException");
		}
		Assert.assertNotNull(owner1);
		Assert.assertEquals(1, owner1.getId());
		owner1 = ownerDao.updatePreference(owner1, Preferences.NOTEBOARD, "test noteboard");
		
		PublicProfile profile = null;
		try {
			profile = this.publicProfileDao.createPublicProfile(owner1, "test create public profile");
		} catch (PublicProfileAlreadyExistsException e) {
			Assert.fail("unexpected PublicProfileAlreadyExistsException");
		}
		Assert.assertNotNull(profile);
		final String key = profile.getPublicProfileId().getProfileKey();
		Assert.assertEquals(1, profile.getOwnerId());
		Assert.assertEquals("test create public profile", profile.getDescription());
		Assert.assertEquals(8, key.length());
		Assert.assertEquals(user1.getDisplayName(), profile.getPublicProfileId().getOwnerDisplayName());
		Assert.assertEquals("test noteboard", profile.getOwnerNoteboard());
		
		PublicProfile retrieveByKey = this.publicProfileDao.locatePublicProfileByKey(key);
		Assert.assertEquals(profile, retrieveByKey);
		
		this.publicProfileDao.removePublicProfile(profile.getPublicProfileId());
		
		PublicProfile afterRemove = this.publicProfileDao.locatePublicProfileByKey(key);
		Assert.assertNull(afterRemove);
	}
	
	/**
	 * 
	 */
	@Test
	public void testRemovePublicProfileWithTags() {
		ICalendarAccount user1 = this.calendarAccountDao.getCalendarAccount("username1");
		Assert.assertNotNull(user1);
		IScheduleOwner owner1 = null;
		try {
			owner1 = ownerDao.register(user1);
		} catch (IneligibleException e) {
			Assert.fail("unexpected IneligibleException");
		}
		Assert.assertNotNull(owner1);
		Assert.assertEquals(1, owner1.getId());
		owner1 = ownerDao.updatePreference(owner1, Preferences.NOTEBOARD, "test noteboard");
		
		PublicProfile profile = null;
		try {
			profile = this.publicProfileDao.createPublicProfile(owner1, "test create public profile");
		} catch (PublicProfileAlreadyExistsException e) {
			Assert.fail("unexpected PublicProfileAlreadyExistsException");
		}
		Assert.assertNotNull(profile);
		final String key = profile.getPublicProfileId().getProfileKey();
		Assert.assertEquals(1, profile.getOwnerId());
		Assert.assertEquals("test create public profile", profile.getDescription());
		Assert.assertEquals(8, key.length());
		Assert.assertEquals(user1.getDisplayName(), profile.getPublicProfileId().getOwnerDisplayName());
		Assert.assertEquals("test noteboard", profile.getOwnerNoteboard());
		
		List<String> tags = Arrays.asList(new String[] { "math" });
		this.publicProfileDao.setProfileTags(tags, profile.getPublicProfileId());
		
		List<PublicProfileId> idSearchByTag = this.publicProfileDao.getPublicProfileIdsWithTag("math");
		Assert.assertEquals(1, idSearchByTag.size());
		Assert.assertEquals(profile.getPublicProfileId(), idSearchByTag.get(0));
		
		PublicProfile retrieveByKey = this.publicProfileDao.locatePublicProfileByKey(key);
		Assert.assertEquals(profile, retrieveByKey);
		
		this.publicProfileDao.removePublicProfile(profile.getPublicProfileId());
		
		PublicProfile afterRemove = this.publicProfileDao.locatePublicProfileByKey(key);
		Assert.assertNull(afterRemove);
		
		List<PublicProfileTag> emptyTags = this.publicProfileDao.getProfileTags(profile.getPublicProfileId());
		Assert.assertNotNull(emptyTags);
		Assert.assertEquals(0, emptyTags.size());
	}
	
	@Test
	public void testGetPublicProfileIds() {
		ICalendarAccount user1 = this.calendarAccountDao.getCalendarAccount("username1");
		Assert.assertNotNull(user1);
		IScheduleOwner owner1 = null;
		try {
			owner1 = ownerDao.register(user1);
		} catch (IneligibleException e) {
			Assert.fail("unexpected IneligibleException");
		}
		Assert.assertNotNull(owner1);
		Assert.assertEquals(1, owner1.getId());
		owner1 = ownerDao.updatePreference(owner1, Preferences.NOTEBOARD, "test1 noteboard");
		
		ICalendarAccount user2 = this.calendarAccountDao.getCalendarAccount("username2");
		Assert.assertNotNull(user2);
		IScheduleOwner owner2 = null;
		try {
			owner2 = ownerDao.register(user2);
		} catch (IneligibleException e) {
			Assert.fail("unexpected IneligibleException");
		}
		Assert.assertNotNull(owner2);
		Assert.assertEquals(2, owner2.getId());
		owner2 = ownerDao.updatePreference(owner2, Preferences.NOTEBOARD, "test2 noteboard");
		
		ICalendarAccount user3 = this.calendarAccountDao.getCalendarAccount("username3");
		Assert.assertNotNull(user3);
		IScheduleOwner owner3 = null;
		try {
			owner3 = ownerDao.register(user3);
		} catch (IneligibleException e) {
			Assert.fail("unexpected IneligibleException");
		}
		Assert.assertNotNull(owner3);
		Assert.assertEquals(3, owner3.getId());
		owner3 = ownerDao.updatePreference(owner3, Preferences.NOTEBOARD, "test3 noteboard");
		
		PublicProfile profile1 = null;
		try {
			profile1 = this.publicProfileDao.createPublicProfile(owner1, "test1 create public profile");
		} catch (PublicProfileAlreadyExistsException e) {
			Assert.fail("unexpected PublicProfileAlreadyExistsException");
		}
		PublicProfile profile2 = null;
		try {
			profile2 = this.publicProfileDao.createPublicProfile(owner2, "test2 create public profile");
		} catch (PublicProfileAlreadyExistsException e) {
			Assert.fail("unexpected PublicProfileAlreadyExistsException");
		}
		PublicProfile profile3 = null;
		try {
			profile3 = this.publicProfileDao.createPublicProfile(owner3, "test3 create public profile");
		} catch (PublicProfileAlreadyExistsException e) {
			Assert.fail("unexpected PublicProfileAlreadyExistsException");
		}
		
		List<PublicProfileId> profileIds = this.publicProfileDao.getPublicProfileIds();
		Assert.assertEquals(3, profileIds.size());
		Assert.assertTrue(profileIds.contains(profile1.getPublicProfileId()));
		Assert.assertTrue(profileIds.contains(profile2.getPublicProfileId()));
		Assert.assertTrue(profileIds.contains(profile3.getPublicProfileId()));
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Before
	public void createDatabase() throws Exception {
		Resource createDdl = (Resource) this.applicationContext.getBean("createDdl");
		
		String sql = IOUtils.toString(createDdl.getInputStream());
		JdbcTemplate template = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		template.execute(sql);
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
