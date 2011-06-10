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
 * Constant to represent number of milliseconds in 1 day.
 */
var MSEC_PER_DAY = 86400000;

/**
 * Helper function to add a number of days to a javascript Date object.
 * The Date object argument MUST have a time of 00:00.
 * 
 * @param date
 * @param daysToAdd
 * @return
 */
function addDays(date, daysToAdd) {
	var laterDate = new Date(date.getTime() + daysToAdd*MSEC_PER_DAY);
	if(laterDate.getHours() == 23) {
		// we hit the switch to standard time
		// cheat and increment by an hour
		laterDate = new Date(laterDate.getTime() + 60*60*1000);
	}
	return laterDate;
};

/**
 * Convert a String elementId (e.g. Mon0800) to a
 * Date.
 * The second argument is a Javascript Date that represents
 * the beginning of the Week (Sunday, time 00:00).
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

/**
 * Convert a Javascript Date object to a String suitable
 * for use with the owner add/remove block forms.
 * 
 * Example input date: 2:30 PM January 30, 2009
 * function return: 20090130-1430
 * 
 * @param date
 * @return
 */
function formatDateForBlockForm(date) {
	var hourString = "";
	if(date.getHours() < 10) {
		hourString += "0";
	}
	hourString += date.getHours();

	var minuteString = "";
	if(date.getMinutes() < 10) {
		minuteString += "0";
	} 
	minuteString += date.getMinutes();

	var completeString = "";
	completeString += formatYearMonthDay(date);
	completeString += "-";
	completeString += hourString;
	completeString += minuteString;
	return completeString;
};
/**
 * Convert a Javascript Date object into a short
 * String that represents Month and day.
 * 
 * Example input date: January 30, 2009
 * function return: Jan 30
 * 
 * @param date
 * @return
 */
function formatShort(date) {
	var month = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
	return month[date.getMonth()] + ' ' + date.getDate();
};

/**
 * Convert a Javascript Date object to a String
 * representation of "year" "month" "day".
 * 
 * Example input Date: January 30, 2009
 * function return: "20090130"
 * 
 * @param date
 * @return
 */
function formatYearMonthDay(date) {
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
 * This function "adds 15 minutes" to the blockToken argument.
 * Examples:
 * 
 * input: Mon0800; output: Mon0815
 * input: Mon1145; output: Mon1200
 * input: Tue2345; output: Tue2359
 * 
 * @param blockToken
 * @return
 */
function add15(blockToken) {
	blockDayOfWeek = blockToken.substring(0,3);
	blockStartTime = blockToken.substring(3,7);
	
	startTimeAsInt = parseInt(blockStartTime, 10);
	startTimeAsInt += 15;
	minutesOnly = startTimeAsInt % 100;
	if(minutesOnly % 60 == 0) {
		// move up one hour
		startTimeAsInt += 40;
	}
	if(startTimeAsInt == 2400) {
		startTimeAsInt = 2359;
	}
	if(startTimeAsInt < 100) {
		return blockDayOfWeek + '00' + startTimeAsInt;
	} else if (startTimeAsInt >= 100 && startTimeAsInt < 1000) {
		return blockDayOfWeek + '0' + startTimeAsInt;
	} else {
		return blockDayOfWeek + '' + startTimeAsInt;
	}
}
/**
 * This function wraps repeated calls to the 'add15' function.
 * 
 * The first argument is the start blockToken, the second argument
 * is the number of times to repeatedly call add15.
 * 
 * Examples:
 * 
 * input: Mon0800, 4; output: Mon0900
 * input: Tue1200, 12; output: Tue1500
 * 
 * @param blockStartTime
 * @param iterations
 * @return
 */
function mult15(blockToken, iterations) {
	var startTimeCopy = blockToken;
	for(i = 0; i < iterations; i++) {
		startTimeCopy = add15(startTimeCopy);
	}
	return startTimeCopy;
}
/**
 * Function to return an Array of element IDs that corresponds to
 * an element from the "scheduleBlocks" array in the schedule JSON data.
 * 
 * For example, passing in a blockString of 'Thu1300 x 8 x 1'
 * will return an 8 element array:
 * [ 'Thu1300','Thu1315','Thu1330','Thu1345','Thu1400','Thu1415','Thu1430','Thu1445' ]
 * 
 * @param blockString
 * @return
 */
function getBlockIds(blockString) {
	var blockIds = new Array();
	tokens = blockString.split(" x ");
	
	numberBlocks = tokens[1];
	for(i = 0; i < numberBlocks; i++) {
		blockIds[i] = mult15(tokens[0], i);
	}
	return blockIds;
}
/**
 * For example, passing in a blockString of 'Thu1300 x 8 x 1' will return '1'.
 * @param blockString
 * @return
 */
function getBlockVisitorLimit(blockString) {
	var blockIds = new Array();
	tokens = blockString.split(" x ");
	return tokens[2];
}


/**
 * Clear the statusBox element.
 * @return
 */
function clearStatusBox(elementSelector) {
	$(elementSelector).text("");
	$(elementSelector).removeAttr("class");
};
/**
 * Clear content and style from status box, reset to original text.
 * @return
 */
function resetStatusBox(elementSelector, mesg) {
	clearStatusBox(elementSelector);
	$(elementSelector).addClass("info");
	$(elementSelector).text(mesg);
};
function showChangeInProgress(elementSelector, msg) {
	clearStatusBox(elementSelector);
	$(elementSelector).addClass("inprogress");
	$(elementSelector).text(msg);
};
function showChangeSuccess(elementSelector, mesg) {
	clearStatusBox(elementSelector);
	$(elementSelector).addClass("success");
	$(elementSelector).text(mesg);
};
function showChangeError(elementSelector, reason) {
	clearStatusBox(elementSelector);
	$(elementSelector).addClass("error");
	$(elementSelector).text(reason);
};