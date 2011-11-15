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

package org.jasig.schedassist.impl.reminder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.impl.events.EmailNotificationApplicationListener;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IEventUtils;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.Reminders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Default {@link ReminderService} implementation.
 * 
 * @author Nicholas Blair
 * @version $Id: DefaultReminderServiceImpl.java 3070 2011-02-09 13:53:34Z npblair $
 */
@Service
public class DefaultReminderServiceImpl implements ReminderService, Runnable {

	private static final String NEWLINE = System.getProperty("line.separator");
	
	private ReminderDao reminderDao;
	private MailSender mailSender;
	private IEventUtils eventUtils;
	private OwnerDao ownerDao;
	private SchedulingAssistantService schedulingAssistantService;
	private ICalendarAccountDao calendarAccountDao;
	private MessageSource messageSource;
	private String noReplyFromAddress;
	private final Log LOG = LogFactory.getLog(this.getClass());
	
	/**
	 * @param reminderDao the reminderDao to set
	 */
	@Autowired
	public void setReminderDao(ReminderDao reminderDao) {
		this.reminderDao = reminderDao;
	}
	/**
	 * @param mailSender the mailSender to set
	 */
	@Autowired
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	/**
	 * @param eventUtils the eventUtils to set
	 */
	@Autowired
	public void setEventUtils(IEventUtils eventUtils) {
		this.eventUtils = eventUtils;
	}
	/**
	 * @param ownerDao the ownerDao to set
	 */
	@Autowired
	public void setOwnerDao(OwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	/**
	 * @param schedulingAssistantService the schedulingAssistantService to set
	 */
	@Autowired
	public void setSchedulingAssistantService(
			SchedulingAssistantService schedulingAssistantService) {
		this.schedulingAssistantService = schedulingAssistantService;
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(@Qualifier("composite") ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
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

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.ReminderService#createEventReminder(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.ICalendarAccount, org.jasig.schedassist.model.AvailableBlock, net.fortuna.ical4j.model.component.VEvent, java.util.Date)
	 */
	@Override
	public IReminder createEventReminder(IScheduleOwner owner,
			ICalendarAccount recipient, AvailableBlock appointmentBlock, VEvent event, Date sendTime) {
		return reminderDao.createEventReminder(owner, recipient, appointmentBlock, event, sendTime);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.ReminderService#deleteEventReminder(org.jasig.schedassist.impl.reminder.IReminder)
	 */
	@Override
	public void deleteEventReminder(IReminder reminder) {
		reminderDao.deleteEventReminder(reminder);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.ReminderService#getPendingReminders()
	 */
	@Override
	public List<IReminder> getPendingReminders() {
		List<PersistedReminderImpl> persisted = reminderDao.getPendingReminders();
		
		List<IReminder> results = new ArrayList<IReminder>(persisted.size());
		for(PersistedReminderImpl p: persisted) {		
			ReminderImpl reminder = complete(p);
			if(reminder != null) {
				results.add(reminder);
			}
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.ReminderService#getReminder(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.ICalendarAccount, org.jasig.schedassist.model.AvailableBlock)
	 */
	@Override
	public IReminder getReminder(IScheduleOwner owner,
			ICalendarAccount recipient, AvailableBlock appointmentBlock) {
		PersistedReminderImpl persisted = reminderDao.getReminder(owner, recipient, appointmentBlock);
		
		ReminderImpl result = complete(persisted);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.ReminderService#getReminders(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.AvailableBlock)
	 */
	@Override
	public List<IReminder> getReminders(IScheduleOwner owner,
			AvailableBlock appointmentBlock) {
		List<PersistedReminderImpl> persisted = reminderDao.getReminders(owner, appointmentBlock);
		List<IReminder> reminders = new ArrayList<IReminder>();
		int count = persisted.size();
		if(count > 0) {
			// get the event, owner from the first in the list and pass it into overloaded complete method
			ReminderImpl first = complete(persisted.get(0));
			reminders.add(first);
			if(count > 1) {
				// we've already added the first, iterate through the rest
				List<PersistedReminderImpl> rest = persisted.subList(1, count);
				for(PersistedReminderImpl p: rest) {
					reminders.add(complete(p, first.getScheduleOwner(), first.getEvent()));
				}
			}
		}
		return reminders;
	}
	/**
	 * Complete a {@link PersistedReminderImpl} by consulting this instance's
	 * {@link OwnerDao}, {@link ICalendarAccountDao}, and {@link SchedulingAssistantService}.
	 * 
	 * @param p
	 * @return a complete {@link ReminderImpl}
	 */
	protected ReminderImpl complete(PersistedReminderImpl p) {
		if(p == null) {
			return null;
		}
		final IScheduleOwner scheduleOwner = this.ownerDao.locateOwnerByAvailableId(p.getOwnerId());
		final ICalendarAccount recipient = this.calendarAccountDao.getCalendarAccount(p.getRecipientId());
		final VEvent event = this.schedulingAssistantService.getExistingAppointment(p.getTargetBlock(), scheduleOwner);
				
		ReminderImpl reminder = new ReminderImpl(p.getReminderId(), scheduleOwner, recipient, p.getSendTime(), event);
		return reminder;
	}
	/**
	 * Overloaded version of {@link #complete(PersistedReminderImpl)} that can skip the call
	 * to {@link SchedulingAssistantService#getExistingAppointment(AvailableBlock, IScheduleOwner)} and
	 * the call to {@link OwnerDao#locateOwnerByAvailableId(long)} (which
	 * are intentionally never cached, where as the {@link ICalendarAccountDao} methods are).
	 * 
	 * This is really useful in {@link #getReminders(IScheduleOwner, AvailableBlock)} where all of the
	 * returned values have the same event and schedule owner.
	 * 
	 * @param p
	 * @param event
	 * @return a complete {@link ReminderImpl}
	 */
	protected ReminderImpl complete(PersistedReminderImpl p, IScheduleOwner owner, VEvent event) {
		if(p == null) {
			return null;
		}
		final ICalendarAccount recipient = this.calendarAccountDao.getCalendarAccount(p.getRecipientId());
				
		ReminderImpl reminder = new ReminderImpl(p.getReminderId(), owner, recipient, p.getSendTime(), event);
		return reminder;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.ReminderService#processPendingReminders()
	 */
	@Override
	public void processPendingReminders() {
		final String propertyValue = System.getProperty("org.jasig.schedassist.runScheduledTasks", "true");
		if(Boolean.parseBoolean(propertyValue)) {
			final List<IReminder> pending = getPendingReminders();
			final int size = pending.size();
			if(size == 0) {
				return;
			}

			LOG.info("begin processing " + size + " pending reminders");
			for(IReminder reminder : pending) {
				try {
					sendEmail(reminder);
				} finally {
					deleteEventReminder(reminder);
				}
			}
			LOG.info("completed processing " + size + " reminders");
		} else {
			LOG.debug("ignoring processPendingReminders as 'org.jasig.schedassist.runScheduledTasks' set to false");
		}
	}
	
	/**
	 * Send an email message for this {@link IReminder}.
	 * 
	 * @param reminder
	 */
	protected void sendEmail(IReminder reminder) {
		if(shouldSend(reminder)) {
			final IScheduleOwner owner = reminder.getScheduleOwner();
			final ICalendarAccount recipient = reminder.getRecipient();
			final VEvent event = reminder.getEvent();
			Reminders reminderPrefs = owner.getRemindersPreference();
			final boolean includeOwner = reminderPrefs.isIncludeOwner();
			
			SimpleMailMessage message = new SimpleMailMessage();
			if(!EmailNotificationApplicationListener.isEmailAddressValid(owner.getCalendarAccount().getEmailAddress())) {
				message.setFrom(noReplyFromAddress);
			} else {
				message.setFrom(owner.getCalendarAccount().getEmailAddress());
			}	
			
			if(includeOwner) {
				message.setTo(new String[] { owner.getCalendarAccount().getEmailAddress(), recipient.getEmailAddress() });
			} else {
				message.setTo(new String[] { recipient.getEmailAddress() });
			}
			
			message.setSubject("Reminder: " + event.getSummary().getValue());
			final String messageBody = createMessageBody(event, owner);
			message.setText(messageBody);
			
			LOG.debug("sending message: " + message.toString());
			try {
				mailSender.send(message);
				LOG.debug("message successfully sent");
			} catch (MailSendException e) {
				LOG.error("caught MailSendException for " + owner + ", " + recipient + ", " + reminder, e);
			}
			
		} else {
			LOG.debug("skipping sendEmail for reminder that should not be sent: " + reminder);
		}
	}
	
	/**
	 * Verify that this reminder is still valid:
	 * <ul>
	 * <li>Owner and recipient exist.</li>
	 * <li>event still exists.</li>
	 * <li>recipient is attending the event.</li>
	 * </ul>
	 * @param reminder
	 * @return true if this reminder should be sent.
	 */
	protected boolean shouldSend(IReminder reminder) {
		final IScheduleOwner owner = reminder.getScheduleOwner();
		if(owner == null) {
			LOG.debug("owner null, should not send " + reminder);
			return false;
		}
		final ICalendarAccount recipient = reminder.getRecipient();
		if(recipient == null) {
			LOG.debug("recipient null, should not send " + reminder);
			return false;
		}
		final VEvent event = reminder.getEvent();
		if(event == null) {
			LOG.debug("event null, should not send " + reminder);
			return false;
		}
	
		boolean recipientAttending = this.eventUtils.isAttendingAsVisitor(event, recipient);
		if(!recipientAttending) {
			LOG.debug("recipient not attending, should not send " + reminder);
			return false;
		}
		Property attendee = this.eventUtils.getAttendeeForUserFromEvent(event, recipient);
		Parameter partstat = attendee.getParameter(PartStat.PARTSTAT);
		boolean participating = PartStat.ACCEPTED.equals(partstat);
		LOG.debug("last check is participation, value is " + participating + " for " + reminder);
		return participating;
	}
	
	/**
	 * Construct the body of the email reminder message from the specified {@link VEvent}.
	 * 
	 * @param event
	 * @param owner
	 * @return
	 */
	protected String createMessageBody(final VEvent event, IScheduleOwner owner) {
		StringBuilder messageBody = new StringBuilder();
		messageBody.append(this.messageSource.getMessage("reminder.email.introduction", new String[] { owner.getCalendarAccount().getDisplayName() }, null));
		messageBody.append(NEWLINE);
		messageBody.append(NEWLINE);
		Summary summary = event.getSummary();
		if(summary != null) {
			messageBody.append(this.messageSource.getMessage("reminder.email.title", new String[] { summary.getValue() }, null));
			messageBody.append(NEWLINE);
		}
		SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
		SimpleDateFormat tf = new SimpleDateFormat("h:mm a");
		messageBody.append(df.format(event.getStartDate().getDate()));
		messageBody.append(NEWLINE);
		messageBody.append(
				this.messageSource.getMessage("reminder.email.time", 
						new String[] { tf.format(event.getStartDate().getDate()), tf.format(event.getEndDate(true).getDate())}, 
						null));	
		Location location = event.getLocation();
		if(location != null) {
			messageBody.append(NEWLINE);
			messageBody.append(this.messageSource.getMessage("reminder.email.location", new String [] { location.getValue() }, null));
		}
		messageBody.append(NEWLINE);
		messageBody.append(NEWLINE);
		messageBody.append(this.messageSource.getMessage("reminder.email.footer", null, null));
		return messageBody.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Scheduled(fixedDelay=60000)
	@Override
	public void run() {
		processPendingReminders();
	}
}
