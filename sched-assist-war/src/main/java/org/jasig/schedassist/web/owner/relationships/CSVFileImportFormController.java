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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jasig.schedassist.ICalendarAccountDao;
import org.jasig.schedassist.MutableRelationshipDao;
import org.jasig.schedassist.impl.owner.NotRegisteredException;
import org.jasig.schedassist.impl.visitor.NotAVisitorException;
import org.jasig.schedassist.impl.visitor.VisitorDao;
import org.jasig.schedassist.model.ICalendarAccount;
import org.jasig.schedassist.model.IScheduleOwner;
import org.jasig.schedassist.model.IScheduleVisitor;
import org.jasig.schedassist.model.Relationship;
import org.jasig.schedassist.web.security.CalendarAccountUserDetails;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import au.com.bytecode.opencsv.CSVReader;

/**
 * {@link Controller} that allows a {@link IScheduleOwner} to upload
 * a CSV file containing username-relationship pairs.
 * 
 * Instead of processing the file on submission, a {@link FileImportCallable} is created
 * and submitted to the required {@link ExecutorService}, which will process the file
 * asynchronously.
 * After an upload, if the owner visits this form controller again before the processing is
 * complete they will see a note to that effect. Once the processing is complete, visiting the
 * form will show a status report of the CSV import.
 * 
 * Important Note: The {@link ExecutorService} wired into this class really should be
 * exclusive to this class. This class implements {@link DisposableBean}; within the {@link #destroy()}
 * the {@link ExecutorService#shutdownNow()} method is invoked, which may cause non-deterministic behavior 
 * for any other class that uses the same {@link ExecutorService} instance.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: CSVFileImportFormController.java 2050 2010-04-30 16:01:31Z npblair $
 */
@Controller
@RequestMapping(value={"/owner/create-relationships-import.html","/delegate/create-relationships-import.html"})
public class CSVFileImportFormController implements DisposableBean {

	public static final String IMPORT_FUTURE_NAME = CSVFileImportFormController.class.getPackage().getName() + ".SharingFileImportFormController.IMPORT_FUTURE";

	private String statusViewName = "owner-relationships/csv-import-status";
	private String formViewName = "owner-relationships/csv-import-form";

	private ExecutorService executorService;
	private MutableRelationshipDao mutableRelationshipDao;
	private VisitorDao visitorDao;
	private ICalendarAccountDao calendarAccountDao;
	private String identifyingAttributeName = "uid";
	/**
	 * 
	 * @param identifyingAttributeName
	 */
	@Value("${users.visibleIdentifierAttributeName:uid}")
	public void setIdentifyingAttributeName(String identifyingAttributeName) {
		this.identifyingAttributeName = identifyingAttributeName;
	}
	/**
	 * 
	 * @return the attribute used to commonly uniquely identify an account
	 */
	public String getIdentifyingAttributeName() {
		return identifyingAttributeName;
	}
	/**
	 * @param executorService the executorService to set
	 */
	@Autowired
	public void setExecutorService(final @Qualifier("fileImportExecutorService") ExecutorService executorService) {
		this.executorService = executorService;
	}
	/**
	 * @param mutableRelationshipDao the mutableRelationshipDao to set
	 */
	@Autowired
	public void setMutableRelationshipDao(final MutableRelationshipDao mutableRelationshipDao) {
		this.mutableRelationshipDao = mutableRelationshipDao;
	}
	/**
	 * @param visitorDao the visitorDao to set
	 */
	@Autowired
	public void setVisitorDao(VisitorDao visitorDao) {
		this.visitorDao = visitorDao;
	}
	/**
	 * @param calendarAccountDao the calendarAccountDao to set
	 */
	@Autowired
	public void setCalendarAccountDao(ICalendarAccountDao calendarAccountDao) {
		this.calendarAccountDao = calendarAccountDao;
	}
	/**
	 * Invokes {@link ExecutorService#shutdownNow()} on the configured instance.
	 * 
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	public void destroy() throws Exception {
		executorService.shutdownNow();
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws NotRegisteredException
	 */
	@RequestMapping(method=RequestMethod.POST)
	protected String uploadFile(final ModelMap model, @RequestParam("file") final MultipartFile file, final HttpServletRequest request) throws NotRegisteredException {
		CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		IScheduleOwner owner = currentUser.getScheduleOwner();
		FileImportCallable callable = new FileImportCallable(file, calendarAccountDao, visitorDao, owner, mutableRelationshipDao, identifyingAttributeName);
		Future<CSVFileImportResult> f = executorService.submit(callable);
		request.getSession(true).setAttribute(IMPORT_FUTURE_NAME, f);

		model.addAttribute("submitted", true);
		return statusViewName;
	}

	/**
	 * 
	 * @param request
	 * @param dismiss
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws NotRegisteredException 
	 */
	@RequestMapping(method=RequestMethod.GET)
	@SuppressWarnings("unchecked")
	protected String showForm(final ModelMap model, final HttpServletRequest request, 
			@RequestParam(value="dismiss",required=false,defaultValue="false") final boolean dismiss) throws InterruptedException, ExecutionException, NotRegisteredException {
		//CalendarAccountUserDetails currentUser = (CalendarAccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		//IScheduleOwner owner = currentUser.getScheduleOwner();

		HttpSession currentSession = request.getSession();
		if(null != currentSession) {
			if(dismiss) {
				// remove the future from the session
				currentSession.setAttribute(IMPORT_FUTURE_NAME, null);
			}
			Future<CSVFileImportResult> f = (Future<CSVFileImportResult>) currentSession.getAttribute(IMPORT_FUTURE_NAME);
			if(null != f) {
				if(f.isDone()) {
					CSVFileImportResult importResult = f.get();
					model.addAttribute("processing", false);
					model.addAttribute("importResult", importResult);
					currentSession.setAttribute(IMPORT_FUTURE_NAME, null);
					return statusViewName;
				} else {
					model.addAttribute("processing", true);
					return statusViewName;
				}
			}
		}
		// no upload being processed, display form
		return formViewName;
	}

	/**
	 * {@link Callable} implementation that processes a {@link ScheduleOwner}'s {@link MultipartFile}
	 * upload as a CSV, creating {@link Relationship}s for each valid line.
	 * 
	 * {@link #call()} returns an object that contains a status report.
	 *
	 * @author Nicholas Blair, nblair@doit.wisc.edu
	 * @version $Id: CSVFileImportFormController.java 2050 2010-04-30 16:01:31Z npblair $
	 */
	static class FileImportCallable implements Callable<CSVFileImportResult> {

		private MultipartFile file;
		private ICalendarAccountDao calendarAccountDao;
		private VisitorDao visitorDao;
		private IScheduleOwner scheduleOwner;
		private MutableRelationshipDao mutableRelationshipDao;
		private final ModifyAdhocRelationshipFormBackingObjectValidator validator;
		private final String identifyingAttributeName;

		/**
		 * @param fileData
		 */
		public FileImportCallable(final MultipartFile file, final ICalendarAccountDao calendarAccountDao,
				final VisitorDao visitorDao, final IScheduleOwner scheduleOwner, 
				final MutableRelationshipDao mutableRelationshipDao, final String identifyingAttributeName) {
			this.file = file;
			this.visitorDao = visitorDao;
			this.calendarAccountDao = calendarAccountDao;
			this.scheduleOwner = scheduleOwner;
			this.mutableRelationshipDao = mutableRelationshipDao;
			validator = new ModifyAdhocRelationshipFormBackingObjectValidator(this.calendarAccountDao);
			this.identifyingAttributeName = identifyingAttributeName;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		public CSVFileImportResult call() throws Exception {
			CSVFileImportResult result = new CSVFileImportResult();
			List<ModifyAdhocRelationshipFormBackingObject> fileData = parseUploadedFile(file, result);
			int lineNumber = 1;
			for(ModifyAdhocRelationshipFormBackingObject fbo : fileData) {

				Errors errors = new DirectFieldBindingResult(fbo, "command");
				validator.validate(fbo, errors);
				if(errors.hasErrors()) {
					for(Object o: errors.getAllErrors()) {
						FieldError error = (FieldError) o;
						if("visitor.notfound".equals(error.getCode())) {
							result.storeFailure(lineNumber, fbo.getVisitorUsername(), error.getDefaultMessage());
						} else {
							result.storeFailure(lineNumber, error.getDefaultMessage());
						}
					}
				} else {
					ICalendarAccount visitorUser = calendarAccountDao.getCalendarAccount(identifyingAttributeName, fbo.getVisitorUsername());
					if(null == visitorUser) {
						result.storeFailure(lineNumber, fbo.getVisitorUsername(), "Account not eligible for WiscCal");
					}
					try {
						IScheduleVisitor visitor = visitorDao.toVisitor(visitorUser);

						Relationship r = mutableRelationshipDao.createRelationship(scheduleOwner, visitor, fbo.getRelationship());
						if(null != r) {
							result.incrementSuccess();
						}	
					} catch (NotAVisitorException e) {
						result.storeFailure(lineNumber, fbo.getVisitorUsername(), "Account not eligible for WiscCal Scheduling Assistant");
					}
				}

				lineNumber++;
			}
			return result;
		}

		/**
		 * Processes the {@link MultipartFile} input with {@link CSVReader}, returning
		 * each valid row as an entry in the returned Map (key=username, value=relationship description).
		 * 
		 * @param file
		 * @param importResult
		 * @return
		 * @throws IOException 
		 */
		private List<ModifyAdhocRelationshipFormBackingObject> parseUploadedFile(MultipartFile file, CSVFileImportResult importResult) throws IOException {
			Set<String> usernames = new HashSet<String>();
			List<ModifyAdhocRelationshipFormBackingObject> results = new ArrayList<ModifyAdhocRelationshipFormBackingObject>();
			CSVReader lineReader = new CSVReader(new InputStreamReader(file.getInputStream()));
			String [] tokens = lineReader.readNext();
			int lineNumber = 1;
			while(null != tokens) {
				if(tokens.length == 2) {
					if(usernames.contains(tokens[0])) {
						importResult.storeFailure(lineNumber, tokens[0], "Duplicate username found, using latest relationship description: " + tokens[1]);
					} else {
						usernames.add(tokens[0]);
					}
					ModifyAdhocRelationshipFormBackingObject fbo = new ModifyAdhocRelationshipFormBackingObject();
					fbo.setRelationship(tokens[1]);
					fbo.setVisitorUsername(tokens[0]);
					results.add(fbo);
				} else {
					importResult.storeFailure(lineNumber, "Line does not match expected format (netid, description)");
				}
				tokens = lineReader.readNext();
				lineNumber++;
			}
			return results;
		}

	}

}
