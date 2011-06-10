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
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.impl.events.AutomaticAppointmentCancellationEvent.Reason;
import org.jasig.schedassist.model.ICalendarAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * {@link ApplicationListener} that sends an email to all the attendees
 * of an event that was automatically cancelled by the Scheduling Assistant.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AutomaticAppointmentCancellationApplicationListener.java $
 */
@Component
public class AutomaticAppointmentCancellationApplicationListener implements 
ApplicationListener<AutomaticAppointmentCancellationEvent> {
	
	private Log LOG = LogFactory.getLog(this.getClass());
	private MailSender mailSender;
	private String noReplyFromAddress = "no.reply.wisccal@doit.wisc.edu";
	
	/**
	 * @param mailSender the mailSender to set
	 */
	@Autowired
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
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
	public void onApplicationEvent(AutomaticAppointmentCancellationEvent event) {
		ICalendarAccount owner = event.getOwner();
		VEvent vevent = event.getEvent();
		
		PropertyList attendeeList = vevent.getProperties(Attendee.ATTENDEE);
		
		SimpleMailMessage message = new SimpleMailMessage();
		List<String> recipients = new ArrayList<String>();
		for(Object o: attendeeList) {
			Property attendee = (Property) o;
			String value = attendee.getValue();
			String email = value.substring(EmailNotificationApplicationListener.MAILTO_PREFIX.length());
			if(EmailNotificationApplicationListener.isEmailAddressValid(email)) {
				recipients.add(email);
			} else {
				LOG.debug("skipping invalid email: " + email);
			}
		}
		message.setTo(recipients.toArray(new String[] {} ));
		
		if(!EmailNotificationApplicationListener.isEmailAddressValid(owner.getEmailAddress())) {
			message.setFrom(noReplyFromAddress);
			
		} else {
			message.setFrom(owner.getEmailAddress());
		}		
		message.setSubject(vevent.getSummary().getValue() + " has been cancelled");
		message.setText(constructMessageBody(vevent, event.getReason(), owner.getDisplayName()));
		
		LOG.debug("sending message: " + message.toString());
		mailSender.send(message);
		LOG.debug("message successfully sent");
	}

	/**
	 * 
	 * @param event
	 * @param reason
	 * @param ownerName
	 * @return
	 */
	protected String constructMessageBody(VEvent event, Reason reason, String ownerName) {
		StringBuilder body = new StringBuilder();
		body.append("WiscCal Scheduling Assistant has automatically cancelled the following appointment: ");
		body.append(EmailNotificationApplicationListener.NEWLINE);
		body.append(EmailNotificationApplicationListener.NEWLINE);
		body.append("Title: ");
		body.append(event.getSummary().getValue());
		body.append(EmailNotificationApplicationListener.NEWLINE);
		SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
		SimpleDateFormat tf = new SimpleDateFormat("h:mm a");
		body.append(df.format(event.getStartDate().getDate()));
		body.append(EmailNotificationApplicationListener.NEWLINE);
		body.append("Time: ");
		body.append(tf.format(event.getStartDate().getDate()));
		body.append(" to ");
		body.append(tf.format(event.getEndDate(true).getDate()));
		body.append(EmailNotificationApplicationListener.NEWLINE);
		body.append("Location: ");
		body.append(event.getLocation().getValue());
		body.append(EmailNotificationApplicationListener.NEWLINE);
		body.append(EmailNotificationApplicationListener.NEWLINE);
		
		body.append("This appointment was automatically cancelled because ");
		if(Reason.OWNER_DECLINED.equals(reason) ) {
			body.append("the Schedule Owner (");
			body.append(ownerName);
			body.append(") specified via WiscCal that they will not be able to attend.");
		} else if(Reason.NO_REMAINING_VISITORS.equals(reason)) {
			body.append("the visiting attendee(s) specified via WiscCal that they will not be able to attend.");
		}
		
		return body.toString();
	}
	

}
