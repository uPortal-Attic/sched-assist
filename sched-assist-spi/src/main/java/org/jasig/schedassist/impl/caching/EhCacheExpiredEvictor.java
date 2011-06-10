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

package org.jasig.schedassist.impl.caching;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * {@link Runnable} implementation that requires a
 * reference to a {@link CacheManager}.
 * 
 * When {@link #run()} is invoked, {@link Ehcache#evictExpiredElements()}
 * is invoked on each {@link Ehcache} available through the provided
 * {@link CacheManager}.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: EhCacheExpiredEvictor.java 2533 2010-09-13 15:51:13Z npblair $
 */
public class EhCacheExpiredEvictor implements Runnable {

	private Log LOG = LogFactory.getLog(this.getClass());
	
	private CacheManager cacheManager;
	
	/**
	 * @param cacheManager the cacheManager to set
	 */
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Scheduled(fixedDelay=1800000)
	@Override
	public void run() {
		final long startTime = System.currentTimeMillis();
		final String [] cacheNames = this.cacheManager.getCacheNames();
		
		long evictedElements = 0;
		for(String cacheName : cacheNames) {
			final Ehcache cache = this.cacheManager.getEhcache(cacheName);
			
			long preEvictSize = cache.getMemoryStoreSize();
			long evictStart = System.currentTimeMillis();
			cache.evictExpiredElements();
			
			if(LOG.isDebugEnabled()) {
				long evicted = preEvictSize - cache.getMemoryStoreSize();
				evictedElements += evicted;
				LOG.debug("Evicted " + evicted + " elements from cache '" + cacheName + "' in " + (System.currentTimeMillis() - evictStart) + " ms");
			}
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("Evicted " + evictedElements + " elements from " + cacheNames.length + " caches  in " + (System.currentTimeMillis() - startTime) + " ms");
		}
	}

}
