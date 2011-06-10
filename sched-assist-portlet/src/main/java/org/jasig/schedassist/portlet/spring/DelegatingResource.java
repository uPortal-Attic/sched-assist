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

package org.jasig.schedassist.portlet.spring;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.springframework.core.io.Resource;

/**
 * {@link Resource} implementation that delegates to the
 * {@link Resource} provided in the constructor.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegatingResource.java $
 */
public class DelegatingResource implements Resource {

	private final Resource resource;
	
	/**
	 * 
	 * @param resource
	 */
	public DelegatingResource(Resource resource) {
		this.resource = resource;
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.io.InputStreamSource#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return this.resource.getInputStream();
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.io.Resource#createRelative(java.lang.String)
	 */
	@Override
	public Resource createRelative(String arg0) throws IOException {
		return this.resource.createRelative(arg0);
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.io.Resource#exists()
	 */
	@Override
	public boolean exists() {
		return this.resource.exists();
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.io.Resource#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.resource.getDescription();
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.io.Resource#getFile()
	 */
	@Override
	public File getFile() throws IOException {
		return this.resource.getFile();
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.io.Resource#getFilename()
	 */
	@Override
	public String getFilename() {
		return this.resource.getFilename();
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.io.Resource#getURI()
	 */
	@Override
	public URI getURI() throws IOException {
		return this.resource.getURI();
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.io.Resource#getURL()
	 */
	@Override
	public URL getURL() throws IOException {
		return this.resource.getURL();
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.io.Resource#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return this.resource.isOpen();
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.io.Resource#isReadable()
	 */
	@Override
	public boolean isReadable() {
		return this.resource.isReadable();
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.io.Resource#lastModified()
	 */
	@Override
	public long lastModified() throws IOException {
		return this.resource.lastModified();
	}

}
