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

import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.PublicProfile;
import org.jasig.schedassist.model.PublicProfileId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * {@link PublicProfileDao} backed by Spring JDBC.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: SpringJDBCPublicProfileDaoImpl.java 2239 2010-06-25 15:37:23Z npblair $
 */
@Service("publicProfileDao")
public class SpringJDBCPublicProfileDaoImpl 
		implements PublicProfileDao {

	protected static final int PROFILE_KEY_LENGTH = 8;
	private Log LOG = LogFactory.getLog(this.getClass());
	private SimpleJdbcTemplate simpleJdbcTemplate;
	
	/**
	 * @param dataSource the dataSource to set
	 */
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#createPublicProfile(org.jasig.schedassist.model.IScheduleOwner, java.lang.String)
	 */
	@Transactional
	@Override
	public PublicProfile createPublicProfile(IScheduleOwner owner,
			String profileDescription) throws PublicProfileAlreadyExistsException {
		if(null != locatePublicProfileByOwner(owner)) {
			throw new PublicProfileAlreadyExistsException("owner already has a public profile: " + owner);
		}
		String uniqueKey = generateNewProfileKey();
		
		this.simpleJdbcTemplate.update("insert into public_profiles (owner_id,owner_display_name,profile_key,profile_description) values (?,?,?,?)", 
				owner.getId(),
				owner.getCalendarAccount().getDisplayName(),
				uniqueKey,
				profileDescription);
		
		PublicProfile result = locatePublicProfileByKey(uniqueKey);
		LOG.info("created new public profile " + result);
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String generateNewProfileKey() {
		boolean uniqueFound = false;
		String uniqueKey = null;
		while(!uniqueFound) {
			uniqueKey = RandomStringUtils.randomAlphabetic(PROFILE_KEY_LENGTH);	
			List<String> existing = this.simpleJdbcTemplate.query("select profile_key from public_profiles where profile_key = ?", 
					new SingleColumnRowMapper<String>(),
					uniqueKey);
			uniqueFound = (existing.size() == 0);
		}
		return uniqueKey;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#getPublicProfileIds()
	 */
	@Cacheable(cacheName="publicProfileCache")
	@Override
	public List<PublicProfileId> getPublicProfileIds() {
		List<PublicProfileId> profileIds = this.simpleJdbcTemplate.query(
				"select profile_key, owner_display_name from public_profiles", 
				new PublicProfileIdRowMapper());
		return profileIds;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#getPublicProfileIds(int, int)
	 */
	@Cacheable(cacheName="publicProfileCache")
	@Override
	public List<PublicProfileId> getPublicProfileIds(int indexStart,
			int indexEnd) {
		List<PublicProfileId> all = getPublicProfileIds();
		Collections.sort(all);
		
		if(indexStart < 0) {
			indexStart = 0;
		}
		
		if(indexEnd > all.size()) {
			indexEnd = all.size();
		}

		List<PublicProfileId> sublist = all.subList(indexStart, indexEnd);
		return sublist;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#getAdvisorPublicProfileIds()
	 */
	@Override
	public List<PublicProfileId> getAdvisorPublicProfileIds() {
		List<PublicProfileId> profileIds = this.simpleJdbcTemplate.query(
				"select prof.profile_key, prof.owner_display_name from public_profiles prof, preferences pref where pref.preference_key = '" 
				+ Preferences.ADVISOR_SHARE_WITH_STUDENTS.getKey() + 
				"' and pref.preference_value='true' and prof.owner_id = pref.owner_id", 
				new PublicProfileIdRowMapper());
		return profileIds;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#locatePublicProfileByKey(java.lang.String)
	 */
	@Cacheable(cacheName="publicProfileCache")
	@Override
	public PublicProfile locatePublicProfileByKey(final String profileKey) {
		List<PublicProfile> profiles = this.simpleJdbcTemplate.query(
				"select prof.owner_id, prof.owner_display_name, prof.profile_key, prof.profile_description, pref.preference_value as noteboard from public_profiles prof, preferences pref where prof.profile_key = ? and prof.owner_id = pref.owner_id and pref.preference_key = '" + Preferences.NOTEBOARD.getKey() + "'",
				new PublicProfileRowMapper(),
				profileKey);
		PublicProfile result = DataAccessUtils.singleResult(profiles);
		return result;
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#locatePublicProfileByOwner(org.jasig.schedassist.model.IScheduleOwner)
	 */
	@Override
	public PublicProfile locatePublicProfileByOwner(final IScheduleOwner owner) {
		List<PublicProfile> profiles = this.simpleJdbcTemplate.query(
				"select prof.owner_id, prof.owner_display_name, prof.profile_key, prof.profile_description, pref.preference_value as noteboard from public_profiles prof, preferences pref where prof.owner_id = ? and prof.owner_id = pref.owner_id and pref.preference_key = '" + Preferences.NOTEBOARD.getKey() + "'",
				new PublicProfileRowMapper(),
				owner.getId());
		PublicProfile result = DataAccessUtils.singleResult(profiles);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#removePublicProfile(org.jasig.schedassist.model.PublicProfileId)
	 */
	@Transactional
	@Override
	//@TriggersRemove(cacheName="publicProfileCache")
	public void removePublicProfile(PublicProfileId profileId) {
		this.simpleJdbcTemplate.update("delete from public_profiles where profile_key = ?", profileId.getProfileKey());
		LOG.info("removed public profile " + profileId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#updatePublicProfileDescription(org.jasig.schedassist.model.PublicProfileId, java.lang.String)
	 */
	@Transactional
	@Override
	//@TriggersRemove(cacheName="publicProfileCache")
	public PublicProfile updatePublicProfileDescription(
			PublicProfileId profileId, String profileDescription) {
		this.simpleJdbcTemplate.update("update public_profiles set profile_description = ? where profile_key = ?", 
				profileDescription,
				profileId.getProfileKey());
		PublicProfile updated = locatePublicProfileByKey(profileId.getProfileKey());
		LOG.info("created new public profile " + updated);
		return updated;
	}

}
