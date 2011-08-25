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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.Preferences;
import org.jasig.schedassist.model.PublicProfile;
import org.jasig.schedassist.model.PublicProfileId;
import org.jasig.schedassist.model.PublicProfileTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;


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
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * @param dataSource the dataSource to set
	 */
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#createPublicProfile(org.jasig.schedassist.model.IScheduleOwner, java.lang.String)
	 */
	@Transactional
	@Override
	@TriggersRemove(cacheName="publicProfileCache", removeAll=true)
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
	@TriggersRemove(cacheName="publicProfileCache", removeAll=true)
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
	@TriggersRemove(cacheName="publicProfileCache", removeAll=true)
	public PublicProfile updatePublicProfileDescription(
			PublicProfileId profileId, String profileDescription) {
		this.simpleJdbcTemplate.update("update public_profiles set profile_description = ? where profile_key = ?", 
				profileDescription,
				profileId.getProfileKey());
		PublicProfile updated = locatePublicProfileByKey(profileId.getProfileKey());
		LOG.info("created new public profile " + updated);
		return updated;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#getProfileTags(org.jasig.schedassist.model.PublicProfileId)
	 */
	@Override
	public List<PublicProfileTag> getProfileTags(PublicProfileId profileId) {
		List<PublicProfileTag> tags = this.simpleJdbcTemplate.query("select profile_key, tag, tag_display from profile_tags where profile_key=?", 
				new PublicProfileTagRowMapper(),
				profileId.getProfileKey());
		
		return tags;
	}
	
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#getProfileTagsBatch(java.util.List)
	 */
	@Override
	public Map<PublicProfileId, List<PublicProfileTag>> getProfileTagsBatch(
			List<PublicProfileId> profileIds) {
		// keep a map to quickly lookup profileId by key
		final Map<String, PublicProfileId> idMap = new HashMap<String, PublicProfileId>();
		
		final String sql = "select profile_key,tag,tag_display from profile_tags where profile_key in (:key)";
		
		for(PublicProfileId profileId : profileIds) {
			String key = profileId.getProfileKey();
			// populate idMap with key->profileId
			idMap.put(key, profileId);
		}
		// map to name the parameters
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<String> keys = new ArrayList<String>(idMap.keySet());
		paramMap.put("key", keys);
		
		Map<PublicProfileId, List<PublicProfileTag>> results = this.namedParameterJdbcTemplate.query(
				sql.toString(),
				paramMap,
				new ResultSetExtractor<Map<PublicProfileId, List<PublicProfileTag>>>() {
					private final PublicProfileTagRowMapper TAG_ROW_MAPPER = new PublicProfileTagRowMapper();
					@Override
					public Map<PublicProfileId, List<PublicProfileTag>> extractData(
							ResultSet rs) throws SQLException,
							DataAccessException {
						Map<PublicProfileId, List<PublicProfileTag>> results = new HashMap<PublicProfileId, List<PublicProfileTag>>();
						while(rs.next()) {
							PublicProfileTag tag = TAG_ROW_MAPPER.mapRow(rs, rs.getRow());
						
							PublicProfileId profileId = idMap.get(tag.getProfileKey());
							List<PublicProfileTag> listForKey = results.get(profileId);
							if(listForKey == null) {
								listForKey = new ArrayList<PublicProfileTag>();
								results.put(profileId, listForKey);
							}
							listForKey.add(tag);
						}
						return results;
					}
				});
		return results;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#setProfileTags(java.util.List, org.jasig.schedassist.model.PublicProfileId)
	 */
	@Transactional
	@Override
	public List<PublicProfileTag> setProfileTags(List<String> tags, PublicProfileId profileId) {
		int rows = this.simpleJdbcTemplate.update("delete from profile_tags where profile_key=?", profileId.getProfileKey());
		LOG.debug("deleted " + rows + " from profile_tags for profile " + profileId);
		
		List<PublicProfileTag> toStore = stringAsTags(tags, profileId);
		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(toStore.toArray());
		this.simpleJdbcTemplate.batchUpdate("insert into profile_tags (profile_key,tag,tag_display) values (:profileKey,:tag,:tagDisplay)",
				batch);
		LOG.debug("inserted " + tags.size() + " tags for " + profileId);
		return toStore;	
	}
	
	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.owner.PublicProfileDao#getPublicProfileIdsWithTag(java.lang.String)
	 */
	@Override
	public List<PublicProfileId> getPublicProfileIdsWithTag(String tag) {
		if(StringUtils.isBlank(tag)) {
			return Collections.emptyList();
		}
		
		List<PublicProfileId> profileIds = this.simpleJdbcTemplate.query(
				"select tags.profile_key,prof.owner_display_name from profile_tags tags left join public_profiles prof on prof.profile_key=tags.profile_key where tag=?",
				new PublicProfileIdRowMapper(),
				tag.toUpperCase());
		return profileIds;
	}
	/**
	 * 
	 * @param tags
	 * @return
	 */
	protected List<PublicProfileTag> stringAsTags(List<String> tags, PublicProfileId profileId) {
		List<PublicProfileTag> result = new ArrayList<PublicProfileTag>();
		
		for(String token : tags) {
			PublicProfileTag p = new PublicProfileTag();
			p.setProfileKey(profileId.getProfileKey());
			p.setTagDisplay(token);
			result.add(p);
		}
		
		return result;
	}
}
