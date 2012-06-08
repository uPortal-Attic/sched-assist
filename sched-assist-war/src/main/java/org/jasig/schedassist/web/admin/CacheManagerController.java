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


package org.jasig.schedassist.web.admin;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * {@link Controller} for interacting with the {@link CacheManager}.
 * 
 * @author Nicholas Blair, npblair@wisc.edu
 * @version $Id: CacheManagerController.java 3062 2011-02-07 15:59:18Z npblair $
 */
@Controller
@RequestMapping("/admin/cache-manager.html")
public class CacheManagerController {

	private CacheManager cacheManager;

	/**
	 * @param cacheManager the cacheManager to set
	 */
	@Autowired
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
	/**
	 * @return the cacheManager
	 */
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String getCacheStatistics(ModelMap model) {
		
		String [] cacheNames = this.cacheManager.getCacheNames();
		model.addAttribute("cacheNames", cacheNames);
		
		Map<String, Statistics> statisticsMap = new HashMap<String, Statistics>();
		for(String cacheName : cacheNames) {
			Cache cache = this.cacheManager.getCache(cacheName);
			Statistics stats = cache.getStatistics();
			statisticsMap.put(cacheName, stats);
		}
		
		model.addAttribute("statisticsMap", statisticsMap);
		return "admin/cache-statistics";
	}
	
	/**
	 * 
	 * @param model
	 * @param cacheName
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST, params="action=clear")
	public String clearCache(ModelMap model, @RequestParam String cacheName) {
		model.addAttribute("cacheName", cacheName);
		Cache cache = this.cacheManager.getCache(cacheName);
		if(cache != null) {
			cache.removeAll();
			cache.clearStatistics();
			model.addAttribute("clearCacheSuccess", cacheName);
		} 
		
		return "admin/cache-clear-results";
	}
	
}
