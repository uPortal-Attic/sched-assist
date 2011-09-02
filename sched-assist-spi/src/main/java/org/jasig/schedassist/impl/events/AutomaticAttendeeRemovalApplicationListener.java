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

package org.jasig.schedassist.impl.events;

import java.text.SimpleDateFormat;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * {@link ApplicationListener} for {@link AutomaticAttendeeRemovalEvent}s.
 * Sends email notifications asynchronously when events occur.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AutomaticAttendeeRemovalApplicationListener.java $
 */
@Component
public class AutomaticAttendeeRemovalApplicationListener implements
		ApplicationListener<AutomaticAttendeeRemovalEvent> {
	
	private Log LOG = LogFactory.getLog(this.getClass());
	private MailSender mailSender;
	private MessageSource messageSource;
	private String noReplyFromAddress = "no.reply.wisccal@doit.wisc.edu";
	
	/**
	 * @param mailSender the mailSender to set
	 */
	@Autowired
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	/**
	 * @param messageSource the messageSource to set
	 */
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	/**
	 * @param noReplyFromAddress the noReplyFromAddress to set
	 */
	public void setNoReplyFromAddress(String noReplyFromAddress) {
		this.noReplyFromAddress = noReplyFromAddress;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Async
	@Override
	public void onApplicationEvent(AutomaticAttendeeRemovalEvent event) {
		ICalendarAccount owner = event.getOwner();
		VEvent vevent = event.getEvent();
		Property removedAttendee = event.getRemoved();
		
		SimpleMailMessage message = new SimpleMailMessage();
		if(!EmailNotificationApplicationListener.isEmailAddressValid(owner.getEmailAddress())) {
			message.setFrom(noReplyFromAddress);
		} else {
			message.setFrom(owner.getEmailAddress());
		}
		
		String removedAttendeeEmail = removedAttendee.getValue().substring(EmailNotificationApplicationListener.MAILTO_PREFIX.length());
		message.setTo(removedAttendeeEmail);
		
		Summary summary = vevent.getSummary();
		if(summary != null) {
			message.setSubject(summary.getValue() + " has been updated");
		} else {
			LOG.warn("event is missing summary: " + event);
			message.setSubject("Appointment has been updated");
		}
		message.setText(constructMessageBody(vevent, removedAttendee, owner.getDisplayName()));
		
		LOG.debug("sending message: " + message.toString());
		mailSender.send(message);
		LOG.debug("message successfully sent");
	}

	/**
	 * 
	 * @param event
	 * @param removedAttendee
	 * @param ownerName
	 * @return
	 */
	protected String constructMessageBody(VEvent event, Property removedAttendee, String ownerName) {
		StringBuilder body = new StringBuilder();
		Parameter cn = removedAttendee.getParameter(Cn.CN);
		
		body.append(this.messageSource.getMessage("automatic.attendee.remove.notify.introduction", new String[] { cn.getValue() }, null));

		body.append(EmailNotificationApplicationListener.NEWLINE);
		body.append(EmailNotificationApplicationListener.NEWLINE);
		Summary summary = event.getSummary();
		if(summary != null) {
			body.append(this.messageSource.getMessage("notify.email.title", new String[] { summary.getValue() }, null));
			body.append(EmailNotificationApplicationListener.NEWLINE);
		}
		SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
		SimpleDateFormat tf = new SimpleDateFormat("h:mm a");
		body.append(df.format(event.getStartDate().getDate()));
		body.append(EmailNotificationApplicationListener.NEWLINE);
		body.append(
				this.messageSource.getMessage("notify.email.time", 
						new String[] { tf.format(event.getStartDate().getDate()), tf.format(event.getEndDate(true).getDate())}, 
						null));	
		Location location = event.getLocation();
		if(location != null) {
			body.append(EmailNotificationApplicationListener.NEWLINE);
			body.append(this.messageSource.getMessage("notify.email.location", new String [] { location.getValue() }, null));
		}
		body.append(EmailNotificationApplicationListener.NEWLINE);
		body.append(EmailNotificationApplicationListener.NEWLINE);
		
		body.append(this.messageSource.getMessage("automatic.attendee.remove.notify.footer", null, null));
		
		return body.toString();
	}
}
