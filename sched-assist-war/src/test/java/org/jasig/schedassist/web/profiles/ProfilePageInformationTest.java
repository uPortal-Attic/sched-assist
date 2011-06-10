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


package org.jasig.schedassist.web.profiles;

import java.util.ArrayList;
import java.util.List;

import org.jasig.schedassist.model.PublicProfileId;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link ProfilePageInformation}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ProfilePageInformationTest.java $
 */
public class ProfilePageInformationTest {

	@Test
	public void testEmptyList() {
		List<PublicProfileId> list = new ArrayList<PublicProfileId>();
		ProfilePageInformation pageInfo = new ProfilePageInformation(list, 0);
		Assert.assertNotNull(pageInfo);
		Assert.assertEquals(0, pageInfo.getStartIndex());
		Assert.assertEquals(0, pageInfo.getEndIndex());
		Assert.assertFalse(pageInfo.isShowNext());
		Assert.assertFalse(pageInfo.isShowPrev());
		Assert.assertTrue(pageInfo.getSublist().isEmpty());
	}
	
	@Test
	public void testListOfOne() {
		List<PublicProfileId> list = new ArrayList<PublicProfileId>();
		PublicProfileId profileId = new PublicProfileId();
		profileId.setOwnerDisplayName("OWNER 1");
		profileId.setProfileKey("ABCDE");
		list.add(profileId);
		ProfilePageInformation pageInfo = new ProfilePageInformation(list, 0);
		Assert.assertNotNull(pageInfo);
		Assert.assertEquals(0, pageInfo.getStartIndex());
		Assert.assertEquals(1, pageInfo.getEndIndex());
		Assert.assertFalse(pageInfo.isShowNext());
		Assert.assertFalse(pageInfo.isShowPrev());
		Assert.assertTrue(pageInfo.getSublist().contains(profileId));
	}
	
	@Test
	public void testListOfFive() {
		List<PublicProfileId> list = new ArrayList<PublicProfileId>();
		for(int i = 1; i <=5 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			list.add(profileId);
		}
		ProfilePageInformation pageInfo = new ProfilePageInformation(list, 0);
		Assert.assertNotNull(pageInfo);
		Assert.assertEquals(0, pageInfo.getStartIndex());
		Assert.assertEquals(5, pageInfo.getEndIndex());
		Assert.assertFalse(pageInfo.isShowNext());
		Assert.assertFalse(pageInfo.isShowPrev());
		for(int i = 1; i <=5 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			Assert.assertTrue(pageInfo.getSublist().contains(profileId));
		}
	}
	
	@Test
	public void testListOfTen() {
		List<PublicProfileId> list = new ArrayList<PublicProfileId>();
		for(int i = 1; i <=10 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			list.add(profileId);
		}
		ProfilePageInformation pageInfo = new ProfilePageInformation(list, 0);
		Assert.assertNotNull(pageInfo);
		Assert.assertEquals(0, pageInfo.getStartIndex());
		Assert.assertEquals(10, pageInfo.getEndIndex());
		Assert.assertFalse(pageInfo.isShowNext());
		Assert.assertFalse(pageInfo.isShowPrev());
		for(int i = 1; i <=10 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			Assert.assertTrue(pageInfo.getSublist().contains(profileId));
		}
	}
	
	@Test
	public void testListOfTenStartAt5() {
		List<PublicProfileId> list = new ArrayList<PublicProfileId>();
		for(int i = 1; i <=10 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			list.add(profileId);
		}
		ProfilePageInformation pageInfo = new ProfilePageInformation(list, 5);
		Assert.assertNotNull(pageInfo);
		Assert.assertEquals(5, pageInfo.getStartIndex());
		Assert.assertEquals(10, pageInfo.getEndIndex());
		Assert.assertFalse(pageInfo.isShowNext());
		Assert.assertTrue(pageInfo.isShowPrev());
		Assert.assertEquals(0, pageInfo.getShowPrevIndex());
		for(int i = 6; i <=10 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			Assert.assertTrue(pageInfo.getSublist().contains(profileId));
		}
	}
	
	@Test
	public void testListOfEleven() {
		List<PublicProfileId> list = new ArrayList<PublicProfileId>();
		for(int i = 1; i <=11 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			list.add(profileId);
		}
		ProfilePageInformation pageInfo = new ProfilePageInformation(list, 0);
		Assert.assertNotNull(pageInfo);
		Assert.assertEquals(0, pageInfo.getStartIndex());
		Assert.assertEquals(10, pageInfo.getEndIndex());
		Assert.assertTrue(pageInfo.isShowNext());
		Assert.assertEquals(10, pageInfo.getShowNextIndex());
		Assert.assertFalse(pageInfo.isShowPrev());
		for(int i = 1; i <=10 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			Assert.assertTrue(pageInfo.getSublist().contains(profileId));
		}
		
		ProfilePageInformation nextPageInfo = new ProfilePageInformation(list, 10);
		Assert.assertNotNull(nextPageInfo);
		Assert.assertEquals(10, nextPageInfo.getStartIndex());
		Assert.assertEquals(11, nextPageInfo.getEndIndex());
		
		PublicProfileId profileId11 = new PublicProfileId();
		profileId11.setOwnerDisplayName("OWNER 11");
		profileId11.setProfileKey("ABCDE11");
		Assert.assertTrue(nextPageInfo.getSublist().contains(profileId11));
		
		Assert.assertTrue(nextPageInfo.isShowPrev());
		Assert.assertEquals(0, nextPageInfo.getShowPrevIndex());
		Assert.assertFalse(nextPageInfo.isShowNext());
	}
	
	@Test
	public void testListOfFortyFive() {
		List<PublicProfileId> list = new ArrayList<PublicProfileId>();
		for(int i = 1; i <=45 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			list.add(profileId);
		}
		ProfilePageInformation pageInfo = new ProfilePageInformation(list, 0);
		Assert.assertNotNull(pageInfo);
		Assert.assertEquals(0, pageInfo.getStartIndex());
		Assert.assertEquals(10, pageInfo.getEndIndex());
		Assert.assertTrue(pageInfo.isShowNext());
		Assert.assertEquals(10, pageInfo.getShowNextIndex());
		Assert.assertFalse(pageInfo.isShowPrev());
		for(int i = 1; i <=10 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			Assert.assertTrue(pageInfo.getSublist().contains(profileId));
		}
		
		ProfilePageInformation nextPageInfo = new ProfilePageInformation(list, 10);
		Assert.assertNotNull(nextPageInfo);
		Assert.assertEquals(10, nextPageInfo.getStartIndex());
		Assert.assertEquals(20, nextPageInfo.getEndIndex());
		for(int i = 11; i <=20 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			Assert.assertTrue(nextPageInfo.getSublist().contains(profileId));
		}
		Assert.assertTrue(nextPageInfo.isShowPrev());
		Assert.assertEquals(0, nextPageInfo.getShowPrevIndex());
		Assert.assertTrue(nextPageInfo.isShowNext());
		Assert.assertEquals(20, nextPageInfo.getShowNextIndex());
		
		nextPageInfo = new ProfilePageInformation(list, 20);
		Assert.assertNotNull(nextPageInfo);
		Assert.assertEquals(20, nextPageInfo.getStartIndex());
		Assert.assertEquals(30, nextPageInfo.getEndIndex());
		for(int i = 21; i <=30 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			Assert.assertTrue(nextPageInfo.getSublist().contains(profileId));
		}
		Assert.assertTrue(nextPageInfo.isShowPrev());
		Assert.assertEquals(10, nextPageInfo.getShowPrevIndex());
		Assert.assertTrue(nextPageInfo.isShowNext());
		Assert.assertEquals(30, nextPageInfo.getShowNextIndex());
		
		nextPageInfo = new ProfilePageInformation(list, 30);
		Assert.assertNotNull(nextPageInfo);
		Assert.assertEquals(30, nextPageInfo.getStartIndex());
		Assert.assertEquals(40, nextPageInfo.getEndIndex());
		for(int i = 31; i <=40 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			Assert.assertTrue(nextPageInfo.getSublist().contains(profileId));
		}
		Assert.assertTrue(nextPageInfo.isShowPrev());
		Assert.assertEquals(20, nextPageInfo.getShowPrevIndex());
		Assert.assertTrue(nextPageInfo.isShowNext());
		Assert.assertEquals(40, nextPageInfo.getShowNextIndex());
		
		nextPageInfo = new ProfilePageInformation(list, 40);
		Assert.assertNotNull(nextPageInfo);
		Assert.assertEquals(40, nextPageInfo.getStartIndex());
		Assert.assertEquals(45, nextPageInfo.getEndIndex());
		for(int i = 41; i <=45 ; i++) {
			PublicProfileId profileId = new PublicProfileId();
			profileId.setOwnerDisplayName("OWNER " + i);
			profileId.setProfileKey("ABCDE"+i);
			Assert.assertTrue(nextPageInfo.getSublist().contains(profileId));
		}
		Assert.assertTrue(nextPageInfo.isShowPrev());
		Assert.assertEquals(30, nextPageInfo.getShowPrevIndex());
		Assert.assertFalse(nextPageInfo.isShowNext());
	}
}
