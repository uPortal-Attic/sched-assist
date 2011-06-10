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


package org.jasig.schedassist.web.owner.relationships;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Bean to store the results of a sharing file import submission.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CSVFileImportResult.java 1934 2010-04-16 17:07:57Z npblair $
 */
public class CSVFileImportResult {

	private int successes = 0;
	private Map<Integer, String> failures = new TreeMap<Integer, String>();
	
	void incrementSuccess() {
		successes++;
	}
	void storeFailure(int lineNumber, String value) {
		this.failures.put(lineNumber, value);
	}
	void storeFailure(int lineNumber, String value, String reason) {
		this.failures.put(lineNumber, combineValueAndReason(value, reason));
	}
	private String combineValueAndReason(String value, String reason) {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(value);
		b.append("] ");
		b.append(reason);
		return b.toString();
	}
	/**
	 * @return the successes
	 */
	public int getSuccesses() {
		return successes;
	}
	/**
	 * @return a reference to the failure map
	 */
	public Map<Integer, String> getFailures() {
		return failures;
	}
	/**
	 * 
	 * @see {@link Map#entrySet()}
	 * @return a reference to the failures map entry set
	 */
	public Set<Map.Entry<Integer, String>> getFailuresEntrySet() {
		return failures.entrySet();
	}
	/**
	 * 
	 * @return a count of the number of failures
	 */
	public int getFailureCount() {
		return failures.size();
	}
}
