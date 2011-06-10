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

/**
 * 
 */
package org.jasig.schedassist.impl.caldav.xml;

/**
 * Generic {@link RuntimeException} that indicates an unrecoverable problem occurred parsing
 * the XML in the CalDAV dialect.
 * 
 * @author Nicholas Blair
 * @version $ Id: XmlParsingException.java $
 */
public class XmlParsingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6128972848979017323L;

	/**
	 * 
	 */
	public XmlParsingException() {
	}

	/**
	 * @param message
	 */
	public XmlParsingException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public XmlParsingException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public XmlParsingException(String message, Throwable cause) {
		super(message, cause);
	}

}
