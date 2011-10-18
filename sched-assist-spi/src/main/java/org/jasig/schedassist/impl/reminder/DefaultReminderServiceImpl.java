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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.SchedulingAssistantService;
import org.jasig.schedassist.impl.events.EmailNotificationApplicationListener;
import org.jasig.schedassist.impl.owner.OwnerDao;
import org.jasig.schedassist.model.AvailableBlock;
import org.jasig.schedassist.model.AvailableBlockBuilder;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.Reminders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.mail.smtp.SMTPAddressFailedException;

/**
 * Default {@link ReminderService} implementation.
 * 
 * @author Nicholas Blair
 * @version $Id: DefaultReminderServiceImpl.java 3070 2011-02-09 13:53:34Z npblair $
 */
@Service
public class DefaultReminderServiceImpl implements ReminderService, Runnable {

	private static final String NEWLINE = System.getProperty("line.separator");
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private MailSender mailSender;
	private DataFieldMaxValueIncrementer reminderIdSequence;
	private OwnerDao ownerDao;
	private SchedulingAssistantService schedulingAssistantService;
	private ICalendarAccountDao calendarAccountDao;
	private MessageSource messageSource;
	private String noReplyFromAddress = "no.reply.wisccal@doit.wisc.edu";
	private final Log LOG = LogFactory.getLog(this.getClass());
	/**
	 * 
	 * @param ds
	 */
	@Autowired
	public void setDataSource(DataSource ds) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(ds);
	}
	/**
	 * @param mailSender the mailSender to set
	 */
	@Autowired
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	/**
	 * @param reminderIdSequence the reminderIdSequence to set
	 */
	@Autowired
	public void setReminderIdSequence(
			@Qualifier("reminders") DataFieldMaxValueIncrementer reminderIdSequence) {
		this.reminderIdSequence = reminderIdSequence;
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
	public void setNoReplyFromAddress(String noReplyFromAddress) {
		this.noReplyFromAddress = noReplyFromAddress;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.ReminderService#createEventReminder(org.jasig.schedassist.model.IScheduleOwner, org.jasig.schedassist.model.ICalendarAccount, org.jasig.schedassist.model.AvailableBlock, net.fortuna.ical4j.model.component.VEvent, java.util.Date)
	 */
	@Override
	@Transactional
	public IReminder createEventReminder(IScheduleOwner owner,
			ICalendarAccount recipient, AvailableBlock appointmentBlock, VEvent event, Date sendTime) {
		
		long newReminderId = this.reminderIdSequence.nextLongValue();
		int rows = this.simpleJdbcTemplate.update("insert into reminders (reminder_id,owner_id,recipient,event_start,event_end,send_time) values (?,?,?,?,?,?)",
				newReminderId,
				owner.getId(),
				recipient.getUsername(),
				appointmentBlock.getStartTime(),
				appointmentBlock.getEndTime(),
				sendTime);
		
		if(rows == 1) {
			ReminderImpl reminder = new ReminderImpl(newReminderId, owner, recipient, sendTime, event);
			return reminder;
		} else {
			LOG.error("failed to store reminder for " + owner + ", " + recipient);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.ReminderService#deleteEventReminder(org.jasig.schedassist.impl.reminder.IReminder)
	 */
	@Override
	@Transactional
	public void deleteEventReminder(IReminder reminder) {
		int rows = this.simpleJdbcTemplate.update("delete from reminders where reminder_id=?", reminder.getReminderId());
		if(LOG.isDebugEnabled()) {
			LOG.debug("delete " + reminder + ", rows affected=" + rows);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jasig.schedassist.impl.reminder.ReminderService#getPendingReminders()
	 */
	@Override
	public List<IReminder> getPendingReminders() {
		Date now = new Date();
		List<PersistedReminderImpl> persisted = this.simpleJdbcTemplate.query(
				"select * from reminders where send_time <= ?", 
				new PersistedReminderImplRowMapper(), 
				now);
		
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
		List<PersistedReminderImpl> persisted = this.simpleJdbcTemplate.query(
				"select * from reminders where owner_id=? and recipient=? and event_start=? and event_end=?", 
				new PersistedReminderImplRowMapper(), 
				owner.getId(),
				recipient.getUsername(),
				appointmentBlock.getStartTime(),
				appointmentBlock.getEndTime());
		
		PersistedReminderImpl p = DataAccessUtils.singleResult(persisted);
		ReminderImpl result = complete(p);
		return result;
	}

	/**
	 * Complete a {@link PersistedReminderImpl} by consulting this instance's
	 * {@link OwnerDao}, {@link ICalendarAccountDao}, and P@link AvailableService}.
	 * 
	 * @param p
	 * @return
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
				sendEmail(reminder);
				deleteEventReminder(reminder);
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
		final IScheduleOwner owner = reminder.getScheduleOwner();
		final ICalendarAccount recipient = reminder.getRecipient();
		final VEvent event = reminder.getEvent();
		if(null != owner && null != recipient && null != event) {
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
				if(e.contains(SMTPAddressFailedException.class)) {
					LOG.warn("failed to send reminder message and will not retry as recipient's email address is invalid: " + recipient);
				} else {
					LOG.error("unexpected MailSendException for " + owner + ", " + recipient);
					// rethrow as this could be due to temporary unavailbility of remote SMTP server
					throw e;
				}
			}
			
		} else {
			LOG.debug("skipping send email for reminder with null elements: " + reminder);
		}
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

	/**
	 * Represents a persisted {@link IReminder}.
	 * {@link #getRecipient()}, {@link #getScheduleOwner()}, and {@link #getEvent()} 
	 * intentionally always return null.
	 * 
	 * @see DefaultReminderServiceImpl#complete(PersistedReminderImpl)
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: DefaultReminderServiceImpl.java 3070 2011-02-09 13:53:34Z npblair $
	 */
	protected static class PersistedReminderImpl implements IReminder {

		private long reminderId;
		private long ownerId;
		private String recipientId;
		private Date sendTime;
		private Date blockStartTime;
		private Date blockEndTime;
			
		/**
		 * @return the reminderId
		 */
		public long getReminderId() {
			return reminderId;
		}
		/**
		 * @param reminderId the reminderId to set
		 */
		public void setReminderId(long reminderId) {
			this.reminderId = reminderId;
		}
		/**
		 * @return the ownerId
		 */
		public long getOwnerId() {
			return ownerId;
		}
		/**
		 * @param ownerId the ownerId to set
		 */
		public void setOwnerId(long ownerId) {
			this.ownerId = ownerId;
		}
		/**
		 * @return the recipientId
		 */
		public String getRecipientId() {
			return recipientId;
		}
		/**
		 * @param recipientId the recipientId to set
		 */
		public void setRecipientId(String recipientId) {
			this.recipientId = recipientId;
		}
		/**
		 * @return the sendTime
		 */
		public Date getSendTime() {
			return sendTime;
		}
		/**
		 * @param sendTime the sendTime to set
		 */
		public void setSendTime(Date sendTime) {
			this.sendTime = sendTime;
		}
		/**
		 * @return the blockStartTime
		 */
		public Date getBlockStartTime() {
			return blockStartTime;
		}
		/**
		 * @param blockStartTime the blockStartTime to set
		 */
		public void setBlockStartTime(Date blockStartTime) {
			this.blockStartTime = blockStartTime;
		}
		/**
		 * @return the blockEndTime
		 */
		public Date getBlockEndTime() {
			return blockEndTime;
		}
		/**
		 * @param blockEndTime the blockEndTime to set
		 */
		public void setBlockEndTime(Date blockEndTime) {
			this.blockEndTime = blockEndTime;
		}
		/**
		 * 
		 * @return the {@link AvailableBlock} 
		 */
		public AvailableBlock getTargetBlock() {
			return AvailableBlockBuilder.createBlock(blockStartTime, blockEndTime);
		}
		
		
		@Override
		public IScheduleOwner getScheduleOwner() {
			return null;
		}
		
		@Override
		public ICalendarAccount getRecipient() {
			return null;
		}
		
		@Override
		public VEvent getEvent() {
			return null;
		}
	}
	/**
	 * {@link RowMapper} for persisted reminders.
	 *
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: DefaultReminderServiceImpl.java 3070 2011-02-09 13:53:34Z npblair $
	 */
	static class PersistedReminderImplRowMapper implements RowMapper<PersistedReminderImpl> {

		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		@Override
		public PersistedReminderImpl mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			PersistedReminderImpl result = new PersistedReminderImpl();
			result.setBlockEndTime(rs.getTimestamp("event_end"));
			result.setBlockStartTime(rs.getTimestamp("event_start"));
			result.setOwnerId(rs.getLong("owner_id"));
			result.setRecipientId(rs.getString("recipient"));
			result.setReminderId(rs.getLong("reminder_id"));
			result.setSendTime(rs.getTimestamp("send_time"));
			return result;
		}
	}
}
