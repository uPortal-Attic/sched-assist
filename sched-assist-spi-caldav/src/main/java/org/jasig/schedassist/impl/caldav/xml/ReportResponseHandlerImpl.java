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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.io.input.TeeInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.impl.caldav.CalendarWithURI;
import org.jasig.schedassist.impl.caldav.ReportMethod;


/**
 * StaX based parser for handling the response body of {@link ReportMethod}
 * requests.
 * 
 * This class has a static initializer that sets 
 * {@link CompatibilityHints#KEY_RELAXED_UNFOLDING} to true.
 * 
 * @author Nicholas Blair
 * @version $ Id: ReportResponseHandlerImpl.java $
 */
public class ReportResponseHandlerImpl {

	static {
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
	}
	protected static final String WEBDAV_NS = "DAV:";
	protected static final String HREF = "href";
	protected static final String ETAG = "getetag";
	protected static final String CALDAV_NS = "urn:ietf:params:xml:ns:caldav";
	protected static final String CALENDAR_DATA = "calendar-data";
	protected final Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Extracts a {@link List} of {@link Calendar}s from the {@link InputStream}, if present.
	 * 
	 * @param inputStream
	 * @return a never null, but possibly empty {@link List} of {@link Calendar}s from the {@link InputStream}
	 * @throws XmlParsingException in the event the stream could not be properly parsed
	 */
	public List<CalendarWithURI> extractCalendars(InputStream inputStream) {
		List<CalendarWithURI> results = new ArrayList<CalendarWithURI>();
		ByteArrayOutputStream capturedContent = null;
		XMLInputFactory factory = XMLInputFactory.newInstance();
		try {
			InputStream localReference = inputStream;
			if(log.isDebugEnabled()) {
				capturedContent = new ByteArrayOutputStream();
				localReference = new TeeInputStream(inputStream, capturedContent);
			}
			BufferedInputStream buffered = new BufferedInputStream(localReference);
			buffered.mark(1);
			int firstbyte = buffered.read();
			if(-1 == firstbyte) {
				// short circuit on empty stream
				return results;
			}
			buffered.reset();
			XMLStreamReader parser = factory.createXMLStreamReader(buffered);
			
			String currentUri = null;
			String currentEtag = null;
			for (int eventType = parser.next(); eventType != XMLStreamConstants.END_DOCUMENT; eventType = parser.next()) {
				switch(eventType) {
				case XMLStreamConstants.START_ELEMENT:
					QName name= parser.getName();
					if(isWebdavHrefElement(name)) {
						currentUri = parser.getElementText();
					} else if (isWebdavEtagElement(name)){
						currentEtag = parser.getElementText();
					} else if(isCalendarDataElement(name)) {
						Calendar cal = extractCalendar(parser.getElementText());
						if(cal != null) {
							CalendarWithURI withUri = new CalendarWithURI(cal, currentUri, currentEtag);
							results.add(withUri);
						} else if(log.isDebugEnabled()) {
							log.debug("extractCalendar returned null for " + currentUri + ", skipping");
						}
					}
					break;
				}
			}
			
			if(log.isDebugEnabled()) {
				log.debug("extracted " + results.size() + " calendar from " + capturedContent.toString());
			}
			
		} catch (XMLStreamException e) {
			if(capturedContent != null) {
				log.error("caught XMLStreamException in extractCalendars, captured content: " + capturedContent.toString(), e);
			} else {
				log.error("caught XMLStreamException in extractCalendars, no captured content available", e);
			}
			throw new XmlParsingException("caught XMLStreamException in extractCalendars", e);
		} catch (IOException e) {
			log.error("caught IOException in extractCalendars", e);
			throw new XmlParsingException("caught IOException in extractCalendars", e);
		}

		return results;
	}
	/**
	 * 
	 * @param qname
	 * @return true if the argument is a WebDAV 'getetag' element
	 */
	protected boolean isWebdavEtagElement(QName qname) {
		if(qname == null) {
			return false;
		} else {
			return WEBDAV_NS.equals(qname.getNamespaceURI()) && ETAG.equals(qname.getLocalPart());
		}
	}
	/**
	 * 
	 * @param qname
	 * @return true if the argument is a WebDAV 'href' element
	 */
	protected boolean isWebdavHrefElement(QName qname) {
		if(qname == null) {
			return false;
		} else {
			return WEBDAV_NS.equals(qname.getNamespaceURI()) && HREF.equals(qname.getLocalPart());
		}
	}
	/**
	 * 
	 * @param qname
	 * @return true if the argument is a CalDAV 'calendar-data' element
	 */
	protected boolean isCalendarDataElement(QName qname) {
		if(qname == null) {
			return false;
		} else {
			return CALDAV_NS.equals(qname.getNamespaceURI()) && CALENDAR_DATA.equals(qname.getLocalPart());
		}
	}

	/**
	 * 
	 * @param text
	 * @return a {@link Calendar} from the text, or null if not parseable
	 */
	protected Calendar extractCalendar(String text) {
		CalendarBuilder builder = new CalendarBuilder();
		Calendar result;
		try {
			result = builder.build(new StringReader(text));
			return result;
		} catch (IOException e) {
			log.warn("caught IOException", e);
			return null;
		} catch (ParserException e) {
			log.warn("caught ParserException", e);
			return null;
		}
	}
	
}
