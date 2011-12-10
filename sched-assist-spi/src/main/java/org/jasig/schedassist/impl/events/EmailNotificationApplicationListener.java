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
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
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
	private MessageSource messageSource;
	private String noReplyFromAddress;
	private boolean useOriginalEventDescription;
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
	@Value("${reminder.noReplyFromAddress}")
	public void setNoReplyFromAddress(String noReplyFromAddress) {
		this.noReplyFromAddress = noReplyFromAddress;
	}
	/**
	 * @param useOriginalEventDescription the useOriginalEventDescription to set
	 */
	@Value("${notify.useOriginalEventDescription:false}")
	public void setUseOriginalEventDescription(boolean useOriginalEventDescription) {
		this.useOriginalEventDescription = useOriginalEventDescription;
	}
	/**
	 * @return the useOriginalEventDescription
	 */
	public boolean isUseOriginalEventDescription() {
		return useOriginalEventDescription;
	}
	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Async
	@Override
	public void onApplicationEvent(AbstractAppointmentEvent event) {
		LOG.debug("email notification listener received event " + event);
		if(event instanceof AppointmentCreatedEvent) {
			AppointmentCreatedEvent a = (AppointmentCreatedEvent) event;
			final String messageBody = createMessageBody(a.getEvent(), a.getEventDescription(), a.getOwner());
			sendEmail(a.getOwner(), a.getVisitor(), a.getEvent(), messageBody);
		} else if (event instanceof AppointmentCancelledEvent) {
			AppointmentCancelledEvent a = (AppointmentCancelledEvent) event;
			final String messageBody = cancelMessageBody(a.getEvent(), a.getCancelReason(), a.getOwner());
			sendEmail(a.getOwner(), a.getVisitor(), a.getEvent(), messageBody);
		} else if (event instanceof AppointmentJoinedEvent) {
			AppointmentJoinedEvent a = (AppointmentJoinedEvent) event;
			final String messageBody = createMessageBody(a.getEvent(), null, a.getOwner());
			sendEmail(a.getOwner(), a.getVisitor(), a.getEvent(), messageBody);
		} else if (event instanceof AppointmentLeftEvent) {
			AppointmentLeftEvent a = (AppointmentLeftEvent) event;
			final String messageBody = cancelMessageBody(a.getEvent(), null, a.getOwner());
			sendEmail(a.getOwner(), a.getVisitor(), a.getEvent(), messageBody);
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
			Summary summary = event.getSummary();
			if(summary != null) {
				message.setSubject(summary.getValue());
			} else {
				LOG.warn("event missing summary" + event);
				message.setSubject("Appointment");
			}
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
	 * Construct the body of the email message sent on Create/Join events.
	 * Depends on the {@link MessageSource}.
	 * 
	 * If {@link #isUseOriginalEventDescription()} is true, the {@link String} eventDescription argument will be used;
	 * if false the {@link Description} from the {@link VEvent} will be used.
	 * Setting it to true is useful if the deployer is overriding the IEventUtils implementation to add data to the description
	 * that they don't want sent in the email notification.
	 * 
	 * @param event
	 * @param eventDescription
	 * @param owner
	 * @return the body of the message as a {@link String}
	 */
	protected String createMessageBody(final VEvent event, String eventDescription, IScheduleOwner owner) {
		StringBuilder messageBody = new StringBuilder();
		messageBody.append(this.messageSource.getMessage("notify.email.introduction", new String[] { owner.getCalendarAccount().getDisplayName() }, null));
		messageBody.append(NEWLINE);
		messageBody.append(NEWLINE);
		Summary summary = event.getSummary();
		if(summary != null) {
			messageBody.append(this.messageSource.getMessage("notify.email.title", new String[] { summary.getValue() }, null));
			messageBody.append(NEWLINE);
		}
		SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
		SimpleDateFormat tf = new SimpleDateFormat("h:mm a");
		messageBody.append(df.format(event.getStartDate().getDate()));
		messageBody.append(NEWLINE);
		messageBody.append(
				this.messageSource.getMessage("notify.email.time", 
						new String[] { tf.format(event.getStartDate().getDate()), tf.format(event.getEndDate(true).getDate())}, 
						null));	
		Location location = event.getLocation();
		if(location != null) {
			messageBody.append(NEWLINE);
			messageBody.append(this.messageSource.getMessage("notify.email.location", new String [] { location.getValue() }, null));
		}
		messageBody.append(NEWLINE);
		
		String descriptionToUse = eventDescription;
		if(!isUseOriginalEventDescription()) {
			Description description = event.getDescription();
			if(description != null && StringUtils.isNotBlank(description.getValue())) {
				descriptionToUse = description.getValue();
			}
		}
		
		if(StringUtils.isNotBlank(descriptionToUse)) {
			messageBody.append(this.messageSource.getMessage("notify.email.reason", new String [] { descriptionToUse }, null));
			messageBody.append(NEWLINE);
		}
		messageBody.append(NEWLINE);
		messageBody.append(this.messageSource.getMessage("notify.email.footer", null, null));
		return messageBody.toString();
	}
	
	
	/**
	 * Construct the body of the email message sent on Cancel/Leave events.
	 * Depends on the {@link MessageSource}.
	 * 
	 * @param event
	 * @param cancelReason
	 * @param owner
	 * @return the body of the message as a {@link String}
	 */
	protected String cancelMessageBody(final VEvent event, final String cancelReason, final IScheduleOwner owner) {
		StringBuilder messageBody = new StringBuilder();
		messageBody.append(this.messageSource.getMessage("notify.email.cancel", new String[] { owner.getCalendarAccount().getDisplayName() }, null));
		messageBody.append(NEWLINE);
		messageBody.append(NEWLINE);
		Summary summary = event.getSummary();
		if(summary != null) {
			messageBody.append(this.messageSource.getMessage("notify.email.title", new String[] { summary.getValue() }, null));
			messageBody.append(NEWLINE);
		}
		SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
		SimpleDateFormat tf = new SimpleDateFormat("h:mm a");
		messageBody.append(df.format(event.getStartDate().getDate()));
		messageBody.append(NEWLINE);
		messageBody.append(
				this.messageSource.getMessage("notify.email.time", 
						new String[] { tf.format(event.getStartDate().getDate()), tf.format(event.getEndDate(true).getDate())}, 
						null));	
		messageBody.append(NEWLINE);
		if(StringUtils.isNotBlank(cancelReason)) {
			messageBody.append(this.messageSource.getMessage("notify.email.cancel.reason", new String [] { cancelReason }, null));
			messageBody.append(NEWLINE);
		}
		messageBody.append(NEWLINE);
		messageBody.append(this.messageSource.getMessage("notify.email.footer", null, null));
		return messageBody.toString();
	}

}
