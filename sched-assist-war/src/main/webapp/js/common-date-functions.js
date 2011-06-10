/*
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
 * Constant defining number of milliseconds in a single day.
 */
var MSEC_PER_DAY = 86400000;

/**
 * Helper method to add a number of days to a Date object.
 * 
 * @param date a Javascript Date object
 * @param daysToAdd an integer
 * @return a Javascript Date object
 */
var addDays = function(date, daysToAdd) {
	var laterDate = new Date(date.getTime() + daysToAdd*MSEC_PER_DAY);
	if(laterDate.getHours() == 23) {
		// we hit the switch to standard time
		// cheat and increment by an hour
		laterDate = new Date(laterDate.getTime() + 60*60*1000);
	}
	return laterDate;
};

/**
 * Function to convert a date object into a String
 * that contains a 3 letter abbreviation for the month followed by
 * the numerical day of the month.
 * Example: "Jan 3"
 * 
 * @param date
 * @return
 */
var formatDateShort = function(date) {
	var month = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
	return month[date.getMonth()] + ' ' + date.getDate();
};

/**
 * Function to convert a Date object into a String that represents
 * the Common Date format used by this application.
 * Example: "20080808" (August 8 2008)
 * Example: "20080112" (January 12 2008)
 * 
 * @param date
 * @return
 */
var formatDate = function(date) {
	var monthString = "";
	var monthNumber = date.getMonth() + 1;
	if(monthNumber < 10) {
		monthString = "0";
	}
	monthString += monthNumber;
	var dayString = "";
	if(date.getDate() < 10) {
		dayString = "0";
	}
	dayString += date.getDate();
	var completeString = "";
	completeString += date.getFullYear();
	completeString += monthString;
	completeString += dayString;
	return completeString;
};

/**
 * Similar to formatDate, this function converts a Date object
 * and the id of an element within the schedule table
 * into a String that represents the Common Date+Time format used
 * by this application.
 * Example: "20080808" (August 8 2008, 8:00 AM)
 * Example: "20080112-1400" (January 12 2008, 2:00 PM)
 *
 * @param elementId e
 * @param weekOfDate
 * @return
 */
var formatDateTime = function(elementId, weekOfDate) {
	// method local variable
	var localDate = new Date(weekOfDate);
	//alert('enter calculateTimePhrase, date: ' + localDate);
	
	//calculate the target day from the Element id
	var dayString = elementId.substring(0,3);
	//alert('dayString: ' + dayString);
	if(dayString == "Mon") {
		localDate = addDays(localDate, 1);
	}
	else if(dayString == "Tue") {
		localDate = addDays(localDate, 2);
	}
	else if(dayString == "Wed") {
		localDate = addDays(localDate, 3);
	}
	else if(dayString == "Thu") {
		localDate = addDays(localDate, 4);
	}
	else if(dayString == "Fri") {
		localDate = addDays(localDate, 5);
	}
	else if(dayString == "Sat") {
		localDate = addDays(localDate, 6);
	}
	//alert('localDate after update time: ' + localDate);
	
	var timePhrase = formatDate(localDate) + "-" + elementId.substring(3,7);
	
	//alert('calculated timePhrase: ' + timePhrase);
	return timePhrase;
	
};

/**
 * Convert elementId and weekOfDate into a javascript date object.
 * 
 * @param elementId
 * @param weekOfDate
 * @return
 */
function convertElementIdToDate(elementId, weekOfDate) {
	var localDate = new Date(weekOfDate);

	//calculate the target day from the Element id
	var dayString = elementId.substring(0,3);
	//alert('dayString: ' + dayString);
	if(dayString == "Mon") {
		localDate = addDays(localDate, 1);
	}
	else if(dayString == "Tue") {
		localDate = addDays(localDate, 2);
	}
	else if(dayString == "Wed") {
		localDate = addDays(localDate, 3);
	}
	else if(dayString == "Thu") {
		localDate = addDays(localDate, 4);
	}
	else if(dayString == "Fri") {
		localDate = addDays(localDate, 5);
	}
	else if(dayString == "Sat") {
		localDate = addDays(localDate, 6);
	}
	
	localDate.setHours(elementId.substring(3,5));
	localDate.setMinutes(elementId.substring(5,7));
	
	return localDate;
};