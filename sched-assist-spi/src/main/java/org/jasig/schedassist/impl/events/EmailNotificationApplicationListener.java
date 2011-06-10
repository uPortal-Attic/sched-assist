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

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * {@link ApplicationListener} implementation that can send an email
 * to the event participants for {@link AppointmentCreatedEvent}s and {@link AppointmentCancelledEvent}s.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: EmailNotificationApplicationListener.java 3070 2011-02-09 13:53:34Z npblair $
 */
@Component
public class EmailNotificationApplicationListener implements
		ApplicationListener<AbstractAppointmentEvent> {

	protected static final String NEWLINE = System.getProperty("line.separator");
	protected static final String MAILTO_PREFIX = "mailto:";
	protected static final String ORACLE_INVALID_EMAIL_DOMAIN = "@email.invalid";
	
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
	public void onApplicationEvent(AbstractAppointmentEvent event) {
		if(event instanceof AppointmentCreatedEvent) {
			AppointmentCreatedEvent a = (AppointmentCreatedEvent) event;
			sendEmail(a.getOwner(), a.getVisitor(), a.getEvent(), createMessageBody(a.getEvent(), a.getEventDescription()));
		} else if (event instanceof AppointmentCancelledEvent) {
			AppointmentCancelledEvent a = (AppointmentCancelledEvent) event;
			sendEmail(a.getOwner(), a.getVisitor(), a.getEvent(), cancelMessageBody(a.getEvent(), a.getCancelReason()));
		} else if (event instanceof AppointmentJoinedEvent) {
			AppointmentJoinedEvent a = (AppointmentJoinedEvent) event;
			sendEmail(a.getOwner(), a.getVisitor(), a.getEvent(), createMessageBody(a.getEvent(), null));
		} else if (event instanceof AppointmentLeftEvent) {
			AppointmentLeftEvent a = (AppointmentLeftEvent) event;
			sendEmail(a.getOwner(), a.getVisitor(), a.getEvent(), cancelMessageBody(a.getEvent(), null));
		}
	}
	
	/**
	 * 
	 * @param owner
	 * @param visitor
	 * @param event
	 */
	protected void sendEmail(final IScheduleOwner owner, final IScheduleVisitor visitor, final VEvent event, final String messageBody) {
		if(null != mailSender) {
			SimpleMailMessage message = new SimpleMailMessage();
			if(!isEmailAddressValid(owner.getCalendarAccount().getEmailAddress())) {
				message.setFrom(noReplyFromAddress);
				message.setTo(new String[] { visitor.getCalendarAccount().getEmailAddress() });
			} else {
				message.setFrom(owner.getCalendarAccount().getEmailAddress());
				message.setTo(new String[] { owner.getCalendarAccount().getEmailAddress(), visitor.getCalendarAccount().getEmailAddress() });
			}		
			message.setSubject(event.getSummary().getValue());
			message.setText(messageBody);
			
			LOG.debug("sending message: " + message.toString());
			mailSender.send(message);
			LOG.debug("message successfully sent");
		} else {
			LOG.debug("no mailSender set, ignoring sendEmail call");
		}
	}
	
	/**
	 * This method implements the logic to determine whether or not the {@link IScheduleOwner}'s email address is valid
	 * If the owner's email address is blank or ends with {@link #ORACLE_INVALID_EMAIL_DOMAIN},
	 * this method will return false.
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmailAddressValid(String email) {
		if(StringUtils.isBlank(email) || email.endsWith(ORACLE_INVALID_EMAIL_DOMAIN)) {
			return false;
		} else {
			return true;
		}
	}
	/**
	 * 
	 * @param event
	 * @return
	 */
	protected static String createMessageBody(final VEvent event, String eventDescription) {
		StringBuilder messageBody = new StringBuilder();
		messageBody.append("The following meeting has been added to your agenda:");
		messageBody.append(NEWLINE);
		messageBody.append(NEWLINE);
		messageBody.append("Title: ");
		messageBody.append(event.getSummary().getValue());
		messageBody.append(NEWLINE);
		SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
		SimpleDateFormat tf = new SimpleDateFormat("h:mm a");
		messageBody.append(df.format(event.getStartDate().getDate()));
		messageBody.append(NEWLINE);
		messageBody.append("Time: ");
		messageBody.append(tf.format(event.getStartDate().getDate()));
		messageBody.append(" to ");
		messageBody.append(tf.format(event.getEndDate(true).getDate()));
		messageBody.append(NEWLINE);
		messageBody.append("Location: ");
		messageBody.append(event.getLocation().getValue());
		messageBody.append(NEWLINE);
		if(StringUtils.isNotBlank(eventDescription)) {
			messageBody.append("Reason: ");
			messageBody.append(eventDescription);
			messageBody.append(NEWLINE);
		}
		messageBody.append(NEWLINE);
		messageBody.append("This appointment was scheduled via the WiscCal Scheduling Assistant - https://tools.wisccal.wisc.edu/available/");
		return messageBody.toString();
	}
	
	/**
	 * 
	 * @param event
	 * @return
	 */
	protected static String cancelMessageBody(final VEvent event, final String cancelReason) {
		StringBuilder messageBody = new StringBuilder();
		messageBody.append("The following meeting has been removed from your agenda:");
		messageBody.append(NEWLINE);
		messageBody.append(NEWLINE);
		messageBody.append("Title: ");
		messageBody.append(event.getSummary().getValue());
		messageBody.append(NEWLINE);
		SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
		SimpleDateFormat tf = new SimpleDateFormat("h:mm a");
		messageBody.append(df.format(event.getStartDate().getDate()));
		messageBody.append(NEWLINE);
		messageBody.append("Time: ");
		messageBody.append(tf.format(event.getStartDate().getDate()));
		messageBody.append(" to ");
		messageBody.append(tf.format(event.getEndDate(true).getDate()));
		messageBody.append(NEWLINE);
		if(StringUtils.isNotBlank(cancelReason)) {
			messageBody.append("Reason for cancelling: ");
			messageBody.append(cancelReason);
			messageBody.append(NEWLINE);
		}
		messageBody.append(NEWLINE);
		messageBody.append("This appointment was scheduled via the WiscCal Scheduling Assistant - https://tools.wisccal.wisc.edu/available/");
		return messageBody.toString();
	}

}
