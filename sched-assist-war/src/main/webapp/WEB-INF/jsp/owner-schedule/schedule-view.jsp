<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title><spring:message code="application.name"/> - <spring:message code="your.availability.schedule"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>

<link rel="stylesheet" type="text/css" href="<c:url value="/css/schedule-view.css"/>" media="all"/>
<rs:resourceURL var="jquerySmoothnessStyle" value="/rs/jqueryui/1.7.2/theme/smoothness/jquery-ui-1.7.2-smoothness.min.css"/>
<link rel="stylesheet" type="text/css" href="${jquerySmoothnessStyle }" media="all"/>
<rs:resourceURL var="crossIcon" value="/rs/famfamfam/silk/1.3/cross.png"/>

<rs:resourceURL var="jqueryUiPath" value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.min.js"/>
<script type="text/javascript" src="${jqueryUiPath}"></script>
<script type="text/javascript" src="<c:url value="/js/owner-schedule-utils.js"/>"></script>
<script type="text/javascript">
jQuery(document).ready(function(){
	$('#loginline').append('<a class="noscriptlink" href="schedule-noscript.html"><spring:message code="trouble.viewing.this.page"/></a>');
	
	$(".weekday ul").selectable({
		filter: 'li',
		cancel: 'li a',
		stop: function(event, ui) { 
			selectedElements = jQuery('li.ui-selected:visible'); 
			filteredSelectedElements = new Array();
			jQuery.each(selectedElements, function(j, element) {
				if(!$(element).hasClass('storedblock')) {
					filteredSelectedElements.push(element);
				} 
			});
			
			if(filteredSelectedElements.length > 0) {
				// identify the elements that were selected
				startTimeElementId = filteredSelectedElements[0].id;
				endTimeElementId = filteredSelectedElements[0].id;
				if(filteredSelectedElements.length != 1) {
					endTimeElementId = filteredSelectedElements[filteredSelectedElements.length - 1].id;
				}
				// convert startTimeElementId and endTimeElementId into start and end dates
				startTime = convertElementIdToDate(startTimeElementId, currentWeekStart);
				// add 15 to endTimeElementId
				endTime = convertElementIdToDate(add15(endTimeElementId), currentWeekStart);
				// submit block to add.html
				postAddForm(startTime, endTime);
			} 
			// mark the elements unselected
			selectedElements.removeClass('ui-selected');
		}
	});

	$.ajaxSetup({
		"error": function(XMLHttpRequest,textStatus, errorThrown) {   
			if(null != XMLHttpRequest) {
				if(XMLHttpRequest.status == 403) {
					showChangeError("#schedulechangestatus", '<spring:message code="session.timed.out"/>');
				} else if (XMLHttpRequest.status == 503) {
					showChangeError("#schedulechangestatus", '<spring:message code="service.temporarily.unavailable"/>');
				} else if (textStatus == 'parsererror' && XMLHttpRequest.status == 200) {
					showChangeError("#schedulechangestatus", '<spring:message code="session.timed.out"/>'); 
				} else {
					showChangeError("#schedulechangestatus", '<spring:message code="unexpected.error"/>');
				}
			}
		}
	});
		
	// invoke draw on dom ready
	draw(true);
	hintTop = $('.scrollTopHint').position().top;
	$('#currentschedule').scrollTop(hintTop);
});

var currentWeekStart;
var currentWeekEnd;

function draw(statusBoxReset) {
	$(".storedblock").each(function(i,element){	
		$(element).removeClass('storedblock');
		$(element).empty();
	});
	retrieveAndRender(currentWeekStart);
	if(statusBoxReset == true) {
		resetStatusBox("#schedulechangestatus", '<spring:message code="availability.schedule.help.dynamic"/>');
	}
};
function retrieveAndRender(date) {
	baseDataUrl = '<c:url value="schedule-data.json" />';
	dataUrl = baseDataUrl;
	if(date != null) {
		dataUrl = baseDataUrl + '?startDate=' + formatYearMonthDay(currentWeekStart);
	}
	showChangeInProgress("#schedulechangestatus", '<spring:message code="retrieving.schedule.data"/>');
	jQuery.getJSON(dataUrl,
			{ },
			function(data) {
				if(data.weekStart && data.weekEnd && data.scheduleBlocks) {
					// 1. update currentWeekStart and currentWeekEnd
					currentWeekStart = new Date(Date.parse(data.weekStart));
					currentWeekEnd = new Date(Date.parse(data.weekEnd));
					// 2. update caption and week navigation handles
					$('#captionText').text( 
							" Week of " + formatShort(currentWeekStart) + " - " + formatShort(currentWeekEnd) + " ");
					$('#sunhead').text('<spring:message code="sunday.short"/> ' + (currentWeekStart.getMonth() + 1) + '/' + currentWeekStart.getDate());
					$('#monhead').text('<spring:message code="monday.short"/> ' + (addDays(currentWeekStart, 1).getMonth() + 1) + '/' + addDays(currentWeekStart, 1).getDate());
					$('#tuehead').text('<spring:message code="tuesday.short"/> ' + (addDays(currentWeekStart, 2).getMonth() + 1) + '/' + addDays(currentWeekStart, 2).getDate());
					$('#wedhead').text('<spring:message code="wednesday.short"/> ' + (addDays(currentWeekStart, 3).getMonth() + 1) + '/' + addDays(currentWeekStart, 3).getDate());
					$('#thuhead').text('<spring:message code="thursday.short"/> ' + (addDays(currentWeekStart, 4).getMonth() + 1) + '/' + addDays(currentWeekStart, 4).getDate());
					$('#frihead').text('<spring:message code="friday.short"/> ' + (addDays(currentWeekStart, 5).getMonth() + 1) + '/' + addDays(currentWeekStart, 5).getDate());
					$('#sathead').text('<spring:message code="saturday.short"/> ' + (addDays(currentWeekStart, 6).getMonth() + 1) + '/' + addDays(currentWeekStart, 6).getDate());
					$('#previousHandle').unbind();
					$('#previousHandle').bind("click", function (e) {
						e.preventDefault();
						currentWeekStart = addDays(currentWeekStart,-7);
						currentWeekEnd = addDays(currentWeekEnd,-7);
						draw(true);
					});
					$('#nextHandle').unbind();
					$('#nextHandle').bind("click", function (e) {
						e.preventDefault();
						currentWeekStart = addDays(currentWeekStart,7);
						currentWeekEnd = addDays(currentWeekEnd,7);
						draw(true);
					});
					
					// 3. update "clear week" link and text 
					$('#clearWeek').attr('href', '<c:url value="clear-week.html"/>?weekOf=' + formatYearMonthDay(currentWeekStart));
					$('#clearWeek').text('Clear week of ' + formatShort(currentWeekStart));

					// 4. render the scheduleBlocks
					jQuery.each(data.scheduleBlocks, function(i,block){	
						blockIds = getBlockIds(block);
						jQuery.each(blockIds, function(j, blockId) {
							// build visitor limit text
							visitorLimit = getBlockVisitorLimit(block);
							visitorLimitText = '';
							if(visitorLimit == 1) {
								visitorLimitText += '<spring:message code="only.one.guest"/>';
							} else {
								visitorLimitText += '<spring:message code="up.to"/> ' + visitorLimit + ' <spring:message code="guests"/>';
							}
							// locate first cell
							cell = $('#' + blockId);
							if(blockIds.length == 1) {
								cell.append(jQuery('<span class="vlimit">' + visitorLimitText + '</span>'));
								blockEnd = add15(blockId);
								destroyHandle = 'destroyHandle-' + blockId + '-' + blockEnd;
								destroyHandleTitle = '<spring:message code="remove.block.from"/> ' + blockId + ' <spring:message code="to"/> ' + blockEnd;
								cell.append(jQuery('<a class="destroyHandle" href="#" title="' + destroyHandleTitle + '" id="' + destroyHandle + '"><img src="<c:out value="${crossIcon}"/>"/></a>'));
								$("#" + destroyHandle).click(function () {
									myId = $(this).attr("id");
									//alert("clicked destroyhandle: " + myId);
									idTokens = myId.split("-");

									startTime = convertElementIdToDate(idTokens[1], currentWeekStart);
									endTime = convertElementIdToDate(idTokens[2], currentWeekStart);
									postRemoveForm(startTime, endTime);
								});
							} else if(j == 0) {
								cell.append(jQuery('<span class="vlimit">' + visitorLimitText + '</span>'));
								blockEnd = add15(blockIds[blockIds.length-1]);
								destroyHandle = 'destroyHandle-' + blockId + '-' + blockEnd;
								destroyHandleTitle = '<spring:message code="remove.block.from"/> ' + blockId + ' <spring:message code="to"/> ' + blockEnd;
								cell.append(jQuery('<a class="destroyHandle" "href="#" title="' + destroyHandleTitle + '" id="' + destroyHandle + '"><img src="<c:out value="${crossIcon}"/>"/></a>'));
								$("#" + destroyHandle).click(function () {
									myId = $(this).attr("id");
									//alert("clicked destroyhandle: " + myId);
									idTokens = myId.split("-");

									startTime = convertElementIdToDate(idTokens[1], currentWeekStart);
									endTime = convertElementIdToDate(idTokens[2], currentWeekStart);
									postRemoveForm(startTime, endTime);
								});
							} 
							cell.append(jQuery('<div class="clearFloats"></div>'));
							cell.addClass('storedblock');
						});
					});
				} else {
					// data was not available
					showChangeError("#schedulechangestatus", '<spring:message code="data.not.available"/>');
				}
			}
	);
};

/**
 * Convert startTime/endTime arguments to correct format and submit to add.html
 */
function postAddForm(startTime, endTime) {
	showChangeInProgress("#schedulechangestatus", '<spring:message code="updating.availability.schedule"/>');
	start = formatDateForBlockForm(startTime);
	end = formatDateForBlockForm(endTime);
	jQuery.post('<c:url value="add-block.html"/>',
			{
		startTimePhrase: start,
		endTimePhrase: end
			},
			function(data) {
				draw(false);
				showChangeSuccess("#schedulechangestatus", '<spring:message code="schedule.successfully.updated.for"/> ' + startTime);
			},
	"json");
};
function postRemoveForm(startTime, endTime) {
	showChangeInProgress("#schedulechangestatus", '<spring:message code="updating.availability.schedule"/>');
	start = formatDateForBlockForm(startTime);
	end = formatDateForBlockForm(endTime);
	jQuery.post('<c:url value="remove-block.html"/>',
			{
		startTimePhrase: start,
		endTimePhrase: end
			},
			function(data) {
				draw(false);
				showChangeSuccess("#schedulechangestatus", '<spring:message code="schedule.successfully.updated.for"/> ' + startTime);
			},
	"json");	
};
</script>
<style type="text/css">
.off-left {
position: absolute;
left: -999px;
width: 990px;
}
.vlimit {
float: left;
font-size: 75%; 
margin: 0px;
padding-left: 0.2em;
color: white;
}
.destroyHandle {
float:right;
}
.clearFloats {
clear:both;
}
.noscriptlink {
float:right;
padding-right:2em;
}
</style>
</head>
<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<noscript>
<div class="alert">
<strong><spring:message code="javascript.required"/></strong>
<p>
<spring:message code="javascript.required.more"/>&nbsp;<a href="<c:url value="schedule-noscript.html"/>"><spring:message code="noscript.version"/></a>.
</p>
</div>
</noscript>
<div id="content" class="main col">
<div class="off-left">
<a title="<spring:message code="non.visual.availability.schedule"/>" href="<c:url value="schedule-noscript.html"/>"><spring:message code="non.visual.availability.schedule"/></a>
</div>
<div id="help" class="info">
<spring:message code="availability.schedule.help.main"/><br/>
<spring:message code="howto.preferences"/>&nbsp;<a href="<c:url value="preferences.html"/>"><spring:message code="preferences"/> &raquo;</a>.<br/>
<spring:message code="howto.availability"/>&nbsp;<a href="<c:url value="builder.html"/>"><spring:message code="build.availability.schedule"/>&raquo;</a>.<br/>
<spring:message code="howto.sharing"/>&nbsp;<a href="<c:url value="sharing.html"/>"><spring:message code="sharing.preferences"/>&raquo;</a>.<br/>
<spring:message code="howto.visitorhistory"/>&nbsp;<a href="<c:url value="visitor-history.html"/>"><spring:message code="visitor.history"/>&raquo;</a>.
</div>
<!-- status box -->
<div id="schedulechangestatus" class="info">
<spring:message code="availability.schedule.help.dynamic"/>
</div>

<div id="schedulecaption">
<a id="preview" href="<c:url value="preview.html"/>"><spring:message code="preview"/></a>&nbsp;|&nbsp;<a id="return" href="<c:url value="schedule.html"/>"><spring:message code="return.to.this.week"/></a>&nbsp;|&nbsp;<a id="clearWeek" href="<c:url value="clear-week.html"/>"><spring:message code="clear.this.week"/></a>
<br/>
<a href="#" class="nav" title="show previous week" id="previousHandle">&laquo;</a><span id="captionText"><!-- initially empty --></span><a href="#" class="nav" title="show next week" id="nextHandle">&raquo;</a>
</div>

<div class="dateheader">
<div class="timeheadercolumn"><ul><li class="">Time</li></ul></div>
<div class="dateheadercolumn"><ul><li class="" id="sunhead"><spring:message code="sunday.short"/></li></ul></div>
<div class="dateheadercolumn"><ul><li class="" id="monhead"><spring:message code="monday.short"/></li></ul></div>
<div class="dateheadercolumn"><ul><li class="" id="tuehead"><spring:message code="tuesday.short"/></li></ul></div>
<div class="dateheadercolumn"><ul><li class="" id="wedhead"><spring:message code="wednesday.short"/></li></ul></div>
<div class="dateheadercolumn"><ul><li class="" id="thuhead"><spring:message code="thursday.short"/></li></ul></div>
<div class="dateheadercolumn"><ul><li class="" id="frihead"><spring:message code="friday.short"/></li></ul></div>
<div class="dateheadercolumn"><ul><li class="" id="sathead"><spring:message code="saturday.short"/></li></ul></div>
</div>
<div class="dateheadercolumnclear"><!-- empty --></div>

<div id="currentschedule">
<div class="timecolumn">
	<ul>
	<li class="timecell">12:00 AM</li>
	<li class="timecell">12:15 AM</li>
	<li class="timecell">12:30 AM</li>
	<li class="timecell">12:45 AM</li>
	<li class="timecell">1:00 AM</li>
	<li class="timecell">1:15 AM</li>
	<li class="timecell">1:30 AM</li>
	<li class="timecell">1:45 AM</li>
	<li class="timecell">2:00 AM</li>
	<li class="timecell">2:15 AM</li>
	<li class="timecell">2:30 AM</li>
	<li class="timecell">2:45 AM</li>
	<li class="timecell">3:00 AM</li>
	<li class="timecell">3:15 AM</li>
	<li class="timecell">3:30 AM</li>
	<li class="timecell">3:45 AM</li>
	<li class="timecell">4:00 AM</li>
	<li class="timecell">4:15 AM</li>
	<li class="timecell">4:30 AM</li>
	<li class="timecell">4:45 AM</li>
	<li class="timecell">5:00 AM</li>
	<li class="timecell">5:15 AM</li>
	<li class="timecell">5:30 AM</li>
	<li class="timecell">5:45 AM</li>
	<li class="timecell">6:00 AM</li>
	<li class="timecell">6:15 AM</li>
	<li class="timecell">6:30 AM</li>
	<li class="timecell">6:45 AM</li>
	<li class="timecell scrollTopHint">7:00 AM</li>
	<li class="timecell">7:15 AM</li>
	<li class="timecell">7:30 AM</li>
	<li class="timecell">7:45 AM</li>
	<li class="timecell">8:00 AM</li>
	<li class="timecell">8:15 AM</li>
	<li class="timecell">8:30 AM</li>
	<li class="timecell">8:45 AM</li>
	<li class="timecell">9:00 AM</li>
	<li class="timecell">9:15 AM</li>
	<li class="timecell">9:30 AM</li>
	<li class="timecell">9:45 AM</li>
	<li class="timecell">10:00 AM</li>
	<li class="timecell">10:15 AM</li>
	<li class="timecell">10:30 AM</li>
	<li class="timecell">10:45 AM</li>
	<li class="timecell">11:00 AM</li>
	<li class="timecell">11:15 AM</li>
	<li class="timecell">11:30 AM</li>
	<li class="timecell">11:45 AM</li>
	<li class="timecell">12:00 PM</li>
	<li class="timecell">12:15 PM</li>
	<li class="timecell">12:30 PM</li>
	<li class="timecell">12:45 PM</li>
	<li class="timecell">1:00 PM</li>
	<li class="timecell">1:15 PM</li>
	<li class="timecell">1:30 PM</li>
	<li class="timecell">1:45 PM</li>
	<li class="timecell">2:00 PM</li>
	<li class="timecell">2:15 PM</li>
	<li class="timecell">2:30 PM</li>
	<li class="timecell">2:45 PM</li>
	<li class="timecell">3:00 PM</li>
	<li class="timecell">3:15 PM</li>
	<li class="timecell">3:30 PM</li>
	<li class="timecell">3:45 PM</li>
	<li class="timecell">4:00 PM</li>
	<li class="timecell">4:15 PM</li>
	<li class="timecell">4:30 PM</li>
	<li class="timecell">4:45 PM</li>
	<li class="timecell">5:00 PM</li>
	<li class="timecell">5:15 PM</li>
	<li class="timecell">5:30 PM</li>
	<li class="timecell">5:45 PM</li>
	<li class="timecell">6:00 PM</li>
	<li class="timecell">6:15 PM</li>
	<li class="timecell">6:30 PM</li>
	<li class="timecell">6:45 PM</li>
	<li class="timecell">7:00 PM</li>
	<li class="timecell">7:15 PM</li>
	<li class="timecell">7:30 PM</li>
	<li class="timecell">7:45 PM</li>
	<li class="timecell">8:00 PM</li>
	<li class="timecell">8:15 PM</li>
	<li class="timecell">8:30 PM</li>
	<li class="timecell">8:45 PM</li>
	<li class="timecell">9:00 PM</li>
	<li class="timecell">9:15 PM</li>
	<li class="timecell">9:30 PM</li>
	<li class="timecell">9:45 PM</li>
	<li class="timecell">10:00 PM</li>
	<li class="timecell">10:15 PM</li>
	<li class="timecell">10:30 PM</li>
	<li class="timecell">10:45 PM</li>
	<li class="timecell">11:00 PM</li>
	<li class="timecell">11:15 PM</li>
	<li class="timecell">11:30 PM</li>
	<li class="timecell">11:45 PM</li>
	</ul>
</div>
	
<div class="weekday" id="SunList">
	<ul>
	<li id="Sun0000" class=""></li>
	<li id="Sun0015" class=""></li>
	<li id="Sun0030" class=""></li>
	<li id="Sun0045" class=""></li>
	<li id="Sun0100" class=""></li>
	<li id="Sun0115" class=""></li>
	<li id="Sun0130" class=""></li>
	<li id="Sun0145" class=""></li>
	<li id="Sun0200" class=""></li>
	<li id="Sun0215" class=""></li>
	<li id="Sun0230" class=""></li>
	<li id="Sun0245" class=""></li>
	<li id="Sun0300" class=""></li>
	<li id="Sun0315" class=""></li>
	<li id="Sun0330" class=""></li>
	<li id="Sun0345" class=""></li>
	<li id="Sun0400" class=""></li>
	<li id="Sun0415" class=""></li>
	<li id="Sun0430" class=""></li>
	<li id="Sun0445" class=""></li>
	<li id="Sun0500" class=""></li>
	<li id="Sun0515" class=""></li>
	<li id="Sun0530" class=""></li>
	<li id="Sun0545" class=""></li>
	<li id="Sun0600" class=""></li>
	<li id="Sun0615" class=""></li>
	<li id="Sun0630" class=""></li>
	<li id="Sun0645" class=""></li>
	<li id="Sun0700" class=""></li>
	<li id="Sun0715" class=""></li>
	<li id="Sun0730" class=""></li>
	<li id="Sun0745" class=""></li>
	<li id="Sun0800" class=""></li>
	<li id="Sun0815" class=""></li>
	<li id="Sun0830" class=""></li>
	<li id="Sun0845" class=""></li>
	<li id="Sun0900" class=""></li>
	<li id="Sun0915" class=""></li>
	<li id="Sun0930" class=""></li>
	<li id="Sun0945" class=""></li>
	<li id="Sun1000" class=""></li>
	<li id="Sun1015" class=""></li>
	<li id="Sun1030" class=""></li>
	<li id="Sun1045" class=""></li>
	<li id="Sun1100" class=""></li>
	<li id="Sun1115" class=""></li>
	<li id="Sun1130" class=""></li>
	<li id="Sun1145" class=""></li>
	<li id="Sun1200" class=""></li>
	<li id="Sun1215" class=""></li>
	<li id="Sun1230" class=""></li>
	<li id="Sun1245" class=""></li>
	<li id="Sun1300" class=""></li>
	<li id="Sun1315" class=""></li>
	<li id="Sun1330" class=""></li>
	<li id="Sun1345" class=""></li>
	<li id="Sun1400" class=""></li>
	<li id="Sun1415" class=""></li>
	<li id="Sun1430" class=""></li>
	<li id="Sun1445" class=""></li>
	<li id="Sun1500" class=""></li>
	<li id="Sun1515" class=""></li>
	<li id="Sun1530" class=""></li>
	<li id="Sun1545" class=""></li>
	<li id="Sun1600" class=""></li>
	<li id="Sun1615" class=""></li>
	<li id="Sun1630" class=""></li>
	<li id="Sun1645" class=""></li>
	<li id="Sun1700" class=""></li>
	<li id="Sun1715" class=""></li>
	<li id="Sun1730" class=""></li>
	<li id="Sun1745" class=""></li>
	<li id="Sun1800" class=""></li>
	<li id="Sun1815" class=""></li>
	<li id="Sun1830" class=""></li>
	<li id="Sun1845" class=""></li>
	<li id="Sun1900" class=""></li>
	<li id="Sun1915" class=""></li>
	<li id="Sun1930" class=""></li>
	<li id="Sun1945" class=""></li>
	<li id="Sun2000" class=""></li>
	<li id="Sun2015" class=""></li>
	<li id="Sun2030" class=""></li>
	<li id="Sun2045" class=""></li>
	<li id="Sun2100" class=""></li>
	<li id="Sun2115" class=""></li>
	<li id="Sun2130" class=""></li>
	<li id="Sun2145" class=""></li>
	<li id="Sun2200" class=""></li>
	<li id="Sun2215" class=""></li>
	<li id="Sun2230" class=""></li>
	<li id="Sun2245" class=""></li>
	<li id="Sun2300" class=""></li>
	<li id="Sun2315" class=""></li>
	<li id="Sun2330" class=""></li>
	<li id="Sun2345" class=""></li>
	</ul>
</div>
	
<div class="weekday" id="MonList">
	<ul>
	<li id="Mon0000" class=""></li>
	<li id="Mon0015" class=""></li>
	<li id="Mon0030" class=""></li>
	<li id="Mon0045" class=""></li>
	<li id="Mon0100" class=""></li>
	<li id="Mon0115" class=""></li>
	<li id="Mon0130" class=""></li>
	<li id="Mon0145" class=""></li>
	<li id="Mon0200" class=""></li>
	<li id="Mon0215" class=""></li>
	<li id="Mon0230" class=""></li>
	<li id="Mon0245" class=""></li>
	<li id="Mon0300" class=""></li>
	<li id="Mon0315" class=""></li>
	<li id="Mon0330" class=""></li>
	<li id="Mon0345" class=""></li>
	<li id="Mon0400" class=""></li>
	<li id="Mon0415" class=""></li>
	<li id="Mon0430" class=""></li>
	<li id="Mon0445" class=""></li>
	<li id="Mon0500" class=""></li>
	<li id="Mon0515" class=""></li>
	<li id="Mon0530" class=""></li>
	<li id="Mon0545" class=""></li>
	<li id="Mon0600" class=""></li>
	<li id="Mon0615" class=""></li>
	<li id="Mon0630" class=""></li>
	<li id="Mon0645" class=""></li>
	<li id="Mon0700" class=""></li>
	<li id="Mon0715" class=""></li>
	<li id="Mon0730" class=""></li>
	<li id="Mon0745" class=""></li>
	<li id="Mon0800" class=""></li>
	<li id="Mon0815" class=""></li>
	<li id="Mon0830" class=""></li>
	<li id="Mon0845" class=""></li>
	<li id="Mon0900" class=""></li>
	<li id="Mon0915" class=""></li>
	<li id="Mon0930" class=""></li>
	<li id="Mon0945" class=""></li>
	<li id="Mon1000" class=""></li>
	<li id="Mon1015" class=""></li>
	<li id="Mon1030" class=""></li>
	<li id="Mon1045" class=""></li>
	<li id="Mon1100" class=""></li>
	<li id="Mon1115" class=""></li>
	<li id="Mon1130" class=""></li>
	<li id="Mon1145" class=""></li>
	<li id="Mon1200" class=""></li>
	<li id="Mon1215" class=""></li>
	<li id="Mon1230" class=""></li>
	<li id="Mon1245" class=""></li>
	<li id="Mon1300" class=""></li>
	<li id="Mon1315" class=""></li>
	<li id="Mon1330" class=""></li>
	<li id="Mon1345" class=""></li>
	<li id="Mon1400" class=""></li>
	<li id="Mon1415" class=""></li>
	<li id="Mon1430" class=""></li>
	<li id="Mon1445" class=""></li>
	<li id="Mon1500" class=""></li>
	<li id="Mon1515" class=""></li>
	<li id="Mon1530" class=""></li>
	<li id="Mon1545" class=""></li>
	<li id="Mon1600" class=""></li>
	<li id="Mon1615" class=""></li>
	<li id="Mon1630" class=""></li>
	<li id="Mon1645" class=""></li>
	<li id="Mon1700" class=""></li>
	<li id="Mon1715" class=""></li>
	<li id="Mon1730" class=""></li>
	<li id="Mon1745" class=""></li>
	<li id="Mon1800" class=""></li>
	<li id="Mon1815" class=""></li>
	<li id="Mon1830" class=""></li>
	<li id="Mon1845" class=""></li>
	<li id="Mon1900" class=""></li>
	<li id="Mon1915" class=""></li>
	<li id="Mon1930" class=""></li>
	<li id="Mon1945" class=""></li>
	<li id="Mon2000" class=""></li>
	<li id="Mon2015" class=""></li>
	<li id="Mon2030" class=""></li>
	<li id="Mon2045" class=""></li>
	<li id="Mon2100" class=""></li>
	<li id="Mon2115" class=""></li>
	<li id="Mon2130" class=""></li>
	<li id="Mon2145" class=""></li>
	<li id="Mon2200" class=""></li>
	<li id="Mon2215" class=""></li>
	<li id="Mon2230" class=""></li>
	<li id="Mon2245" class=""></li>
	<li id="Mon2300" class=""></li>
	<li id="Mon2315" class=""></li>
	<li id="Mon2330" class=""></li>
	<li id="Mon2345" class=""></li>
	</ul>
</div>
	
<div class="weekday" id="TueList">
	<ul>
	<li id="Tue0000" class=""></li>
	<li id="Tue0015" class=""></li>
	<li id="Tue0030" class=""></li>
	<li id="Tue0045" class=""></li>
	<li id="Tue0100" class=""></li>
	<li id="Tue0115" class=""></li>
	<li id="Tue0130" class=""></li>
	<li id="Tue0145" class=""></li>
	<li id="Tue0200" class=""></li>
	<li id="Tue0215" class=""></li>
	<li id="Tue0230" class=""></li>
	<li id="Tue0245" class=""></li>
	<li id="Tue0300" class=""></li>
	<li id="Tue0315" class=""></li>
	<li id="Tue0330" class=""></li>
	<li id="Tue0345" class=""></li>
	<li id="Tue0400" class=""></li>
	<li id="Tue0415" class=""></li>
	<li id="Tue0430" class=""></li>
	<li id="Tue0445" class=""></li>
	<li id="Tue0500" class=""></li>
	<li id="Tue0515" class=""></li>
	<li id="Tue0530" class=""></li>
	<li id="Tue0545" class=""></li>
	<li id="Tue0600" class=""></li>
	<li id="Tue0615" class=""></li>
	<li id="Tue0630" class=""></li>
	<li id="Tue0645" class=""></li>
	<li id="Tue0700" class=""></li>
	<li id="Tue0715" class=""></li>
	<li id="Tue0730" class=""></li>
	<li id="Tue0745" class=""></li>
	<li id="Tue0800" class=""></li>
	<li id="Tue0815" class=""></li>
	<li id="Tue0830" class=""></li>
	<li id="Tue0845" class=""></li>
	<li id="Tue0900" class=""></li>
	<li id="Tue0915" class=""></li>
	<li id="Tue0930" class=""></li>
	<li id="Tue0945" class=""></li>
	<li id="Tue1000" class=""></li>
	<li id="Tue1015" class=""></li>
	<li id="Tue1030" class=""></li>
	<li id="Tue1045" class=""></li>
	<li id="Tue1100" class=""></li>
	<li id="Tue1115" class=""></li>
	<li id="Tue1130" class=""></li>
	<li id="Tue1145" class=""></li>
	<li id="Tue1200" class=""></li>
	<li id="Tue1215" class=""></li>
	<li id="Tue1230" class=""></li>
	<li id="Tue1245" class=""></li>
	<li id="Tue1300" class=""></li>
	<li id="Tue1315" class=""></li>
	<li id="Tue1330" class=""></li>
	<li id="Tue1345" class=""></li>
	<li id="Tue1400" class=""></li>
	<li id="Tue1415" class=""></li>
	<li id="Tue1430" class=""></li>
	<li id="Tue1445" class=""></li>
	<li id="Tue1500" class=""></li>
	<li id="Tue1515" class=""></li>
	<li id="Tue1530" class=""></li>
	<li id="Tue1545" class=""></li>
	<li id="Tue1600" class=""></li>
	<li id="Tue1615" class=""></li>
	<li id="Tue1630" class=""></li>
	<li id="Tue1645" class=""></li>
	<li id="Tue1700" class=""></li>
	<li id="Tue1715" class=""></li>
	<li id="Tue1730" class=""></li>
	<li id="Tue1745" class=""></li>
	<li id="Tue1800" class=""></li>
	<li id="Tue1815" class=""></li>
	<li id="Tue1830" class=""></li>
	<li id="Tue1845" class=""></li>
	<li id="Tue1900" class=""></li>
	<li id="Tue1915" class=""></li>
	<li id="Tue1930" class=""></li>
	<li id="Tue1945" class=""></li>
	<li id="Tue2000" class=""></li>
	<li id="Tue2015" class=""></li>
	<li id="Tue2030" class=""></li>
	<li id="Tue2045" class=""></li>
	<li id="Tue2100" class=""></li>
	<li id="Tue2115" class=""></li>
	<li id="Tue2130" class=""></li>
	<li id="Tue2145" class=""></li>
	<li id="Tue2200" class=""></li>
	<li id="Tue2215" class=""></li>
	<li id="Tue2230" class=""></li>
	<li id="Tue2245" class=""></li>
	<li id="Tue2300" class=""></li>
	<li id="Tue2315" class=""></li>
	<li id="Tue2330" class=""></li>
	<li id="Tue2345" class=""></li>
	</ul>
</div>
	
<div class="weekday" id="WedList">
	<ul>
	<li id="Wed0000" class=""></li>
	<li id="Wed0015" class=""></li>
	<li id="Wed0030" class=""></li>
	<li id="Wed0045" class=""></li>
	<li id="Wed0100" class=""></li>
	<li id="Wed0115" class=""></li>
	<li id="Wed0130" class=""></li>
	<li id="Wed0145" class=""></li>
	<li id="Wed0200" class=""></li>
	<li id="Wed0215" class=""></li>
	<li id="Wed0230" class=""></li>
	<li id="Wed0245" class=""></li>
	<li id="Wed0300" class=""></li>
	<li id="Wed0315" class=""></li>
	<li id="Wed0330" class=""></li>
	<li id="Wed0345" class=""></li>
	<li id="Wed0400" class=""></li>
	<li id="Wed0415" class=""></li>
	<li id="Wed0430" class=""></li>
	<li id="Wed0445" class=""></li>
	<li id="Wed0500" class=""></li>
	<li id="Wed0515" class=""></li>
	<li id="Wed0530" class=""></li>
	<li id="Wed0545" class=""></li>
	<li id="Wed0600" class=""></li>
	<li id="Wed0615" class=""></li>
	<li id="Wed0630" class=""></li>
	<li id="Wed0645" class=""></li>
	<li id="Wed0700" class=""></li>
	<li id="Wed0715" class=""></li>
	<li id="Wed0730" class=""></li>
	<li id="Wed0745" class=""></li>
	<li id="Wed0800" class=""></li>
	<li id="Wed0815" class=""></li>
	<li id="Wed0830" class=""></li>
	<li id="Wed0845" class=""></li>
	<li id="Wed0900" class=""></li>
	<li id="Wed0915" class=""></li>
	<li id="Wed0930" class=""></li>
	<li id="Wed0945" class=""></li>
	<li id="Wed1000" class=""></li>
	<li id="Wed1015" class=""></li>
	<li id="Wed1030" class=""></li>
	<li id="Wed1045" class=""></li>
	<li id="Wed1100" class=""></li>
	<li id="Wed1115" class=""></li>
	<li id="Wed1130" class=""></li>
	<li id="Wed1145" class=""></li>
	<li id="Wed1200" class=""></li>
	<li id="Wed1215" class=""></li>
	<li id="Wed1230" class=""></li>
	<li id="Wed1245" class=""></li>
	<li id="Wed1300" class=""></li>
	<li id="Wed1315" class=""></li>
	<li id="Wed1330" class=""></li>
	<li id="Wed1345" class=""></li>
	<li id="Wed1400" class=""></li>
	<li id="Wed1415" class=""></li>
	<li id="Wed1430" class=""></li>
	<li id="Wed1445" class=""></li>
	<li id="Wed1500" class=""></li>
	<li id="Wed1515" class=""></li>
	<li id="Wed1530" class=""></li>
	<li id="Wed1545" class=""></li>
	<li id="Wed1600" class=""></li>
	<li id="Wed1615" class=""></li>
	<li id="Wed1630" class=""></li>
	<li id="Wed1645" class=""></li>
	<li id="Wed1700" class=""></li>
	<li id="Wed1715" class=""></li>
	<li id="Wed1730" class=""></li>
	<li id="Wed1745" class=""></li>
	<li id="Wed1800" class=""></li>
	<li id="Wed1815" class=""></li>
	<li id="Wed1830" class=""></li>
	<li id="Wed1845" class=""></li>
	<li id="Wed1900" class=""></li>
	<li id="Wed1915" class=""></li>
	<li id="Wed1930" class=""></li>
	<li id="Wed1945" class=""></li>
	<li id="Wed2000" class=""></li>
	<li id="Wed2015" class=""></li>
	<li id="Wed2030" class=""></li>
	<li id="Wed2045" class=""></li>
	<li id="Wed2100" class=""></li>
	<li id="Wed2115" class=""></li>
	<li id="Wed2130" class=""></li>
	<li id="Wed2145" class=""></li>
	<li id="Wed2200" class=""></li>
	<li id="Wed2215" class=""></li>
	<li id="Wed2230" class=""></li>
	<li id="Wed2245" class=""></li>
	<li id="Wed2300" class=""></li>
	<li id="Wed2315" class=""></li>
	<li id="Wed2330" class=""></li>
	<li id="Wed2345" class=""></li>
	</ul>
</div>
	
<div class="weekday" id="ThuList">
	<ul>
	<li id="Thu0000" class=""></li>
	<li id="Thu0015" class=""></li>
	<li id="Thu0030" class=""></li>
	<li id="Thu0045" class=""></li>
	<li id="Thu0100" class=""></li>
	<li id="Thu0115" class=""></li>
	<li id="Thu0130" class=""></li>
	<li id="Thu0145" class=""></li>
	<li id="Thu0200" class=""></li>
	<li id="Thu0215" class=""></li>
	<li id="Thu0230" class=""></li>
	<li id="Thu0245" class=""></li>
	<li id="Thu0300" class=""></li>
	<li id="Thu0315" class=""></li>
	<li id="Thu0330" class=""></li>
	<li id="Thu0345" class=""></li>
	<li id="Thu0400" class=""></li>
	<li id="Thu0415" class=""></li>
	<li id="Thu0430" class=""></li>
	<li id="Thu0445" class=""></li>
	<li id="Thu0500" class=""></li>
	<li id="Thu0515" class=""></li>
	<li id="Thu0530" class=""></li>
	<li id="Thu0545" class=""></li>
	<li id="Thu0600" class=""></li>
	<li id="Thu0615" class=""></li>
	<li id="Thu0630" class=""></li>
	<li id="Thu0645" class=""></li>
	<li id="Thu0700" class=""></li>
	<li id="Thu0715" class=""></li>
	<li id="Thu0730" class=""></li>
	<li id="Thu0745" class=""></li>
	<li id="Thu0800" class=""></li>
	<li id="Thu0815" class=""></li>
	<li id="Thu0830" class=""></li>
	<li id="Thu0845" class=""></li>
	<li id="Thu0900" class=""></li>
	<li id="Thu0915" class=""></li>
	<li id="Thu0930" class=""></li>
	<li id="Thu0945" class=""></li>
	<li id="Thu1000" class=""></li>
	<li id="Thu1015" class=""></li>
	<li id="Thu1030" class=""></li>
	<li id="Thu1045" class=""></li>
	<li id="Thu1100" class=""></li>
	<li id="Thu1115" class=""></li>
	<li id="Thu1130" class=""></li>
	<li id="Thu1145" class=""></li>
	<li id="Thu1200" class=""></li>
	<li id="Thu1215" class=""></li>
	<li id="Thu1230" class=""></li>
	<li id="Thu1245" class=""></li>
	<li id="Thu1300" class=""></li>
	<li id="Thu1315" class=""></li>
	<li id="Thu1330" class=""></li>
	<li id="Thu1345" class=""></li>
	<li id="Thu1400" class=""></li>
	<li id="Thu1415" class=""></li>
	<li id="Thu1430" class=""></li>
	<li id="Thu1445" class=""></li>
	<li id="Thu1500" class=""></li>
	<li id="Thu1515" class=""></li>
	<li id="Thu1530" class=""></li>
	<li id="Thu1545" class=""></li>
	<li id="Thu1600" class=""></li>
	<li id="Thu1615" class=""></li>
	<li id="Thu1630" class=""></li>
	<li id="Thu1645" class=""></li>
	<li id="Thu1700" class=""></li>
	<li id="Thu1715" class=""></li>
	<li id="Thu1730" class=""></li>
	<li id="Thu1745" class=""></li>
	<li id="Thu1800" class=""></li>
	<li id="Thu1815" class=""></li>
	<li id="Thu1830" class=""></li>
	<li id="Thu1845" class=""></li>
	<li id="Thu1900" class=""></li>
	<li id="Thu1915" class=""></li>
	<li id="Thu1930" class=""></li>
	<li id="Thu1945" class=""></li>
	<li id="Thu2000" class=""></li>
	<li id="Thu2015" class=""></li>
	<li id="Thu2030" class=""></li>
	<li id="Thu2045" class=""></li>
	<li id="Thu2100" class=""></li>
	<li id="Thu2115" class=""></li>
	<li id="Thu2130" class=""></li>
	<li id="Thu2145" class=""></li>
	<li id="Thu2200" class=""></li>
	<li id="Thu2215" class=""></li>
	<li id="Thu2230" class=""></li>
	<li id="Thu2245" class=""></li>
	<li id="Thu2300" class=""></li>
	<li id="Thu2315" class=""></li>
	<li id="Thu2330" class=""></li>
	<li id="Thu2345" class=""></li>
	</ul>
</div>
	
<div class="weekday" id="FriList">
	<ul>
	<li id="Fri0000" class=""></li>
	<li id="Fri0015" class=""></li>
	<li id="Fri0030" class=""></li>
	<li id="Fri0045" class=""></li>
	<li id="Fri0100" class=""></li>
	<li id="Fri0115" class=""></li>
	<li id="Fri0130" class=""></li>
	<li id="Fri0145" class=""></li>
	<li id="Fri0200" class=""></li>
	<li id="Fri0215" class=""></li>
	<li id="Fri0230" class=""></li>
	<li id="Fri0245" class=""></li>
	<li id="Fri0300" class=""></li>
	<li id="Fri0315" class=""></li>
	<li id="Fri0330" class=""></li>
	<li id="Fri0345" class=""></li>
	<li id="Fri0400" class=""></li>
	<li id="Fri0415" class=""></li>
	<li id="Fri0430" class=""></li>
	<li id="Fri0445" class=""></li>
	<li id="Fri0500" class=""></li>
	<li id="Fri0515" class=""></li>
	<li id="Fri0530" class=""></li>
	<li id="Fri0545" class=""></li>
	<li id="Fri0600" class=""></li>
	<li id="Fri0615" class=""></li>
	<li id="Fri0630" class=""></li>
	<li id="Fri0645" class=""></li>
	<li id="Fri0700" class=""></li>
	<li id="Fri0715" class=""></li>
	<li id="Fri0730" class=""></li>
	<li id="Fri0745" class=""></li>
	<li id="Fri0800" class=""></li>
	<li id="Fri0815" class=""></li>
	<li id="Fri0830" class=""></li>
	<li id="Fri0845" class=""></li>
	<li id="Fri0900" class=""></li>
	<li id="Fri0915" class=""></li>
	<li id="Fri0930" class=""></li>
	<li id="Fri0945" class=""></li>
	<li id="Fri1000" class=""></li>
	<li id="Fri1015" class=""></li>
	<li id="Fri1030" class=""></li>
	<li id="Fri1045" class=""></li>
	<li id="Fri1100" class=""></li>
	<li id="Fri1115" class=""></li>
	<li id="Fri1130" class=""></li>
	<li id="Fri1145" class=""></li>
	<li id="Fri1200" class=""></li>
	<li id="Fri1215" class=""></li>
	<li id="Fri1230" class=""></li>
	<li id="Fri1245" class=""></li>
	<li id="Fri1300" class=""></li>
	<li id="Fri1315" class=""></li>
	<li id="Fri1330" class=""></li>
	<li id="Fri1345" class=""></li>
	<li id="Fri1400" class=""></li>
	<li id="Fri1415" class=""></li>
	<li id="Fri1430" class=""></li>
	<li id="Fri1445" class=""></li>
	<li id="Fri1500" class=""></li>
	<li id="Fri1515" class=""></li>
	<li id="Fri1530" class=""></li>
	<li id="Fri1545" class=""></li>
	<li id="Fri1600" class=""></li>
	<li id="Fri1615" class=""></li>
	<li id="Fri1630" class=""></li>
	<li id="Fri1645" class=""></li>
	<li id="Fri1700" class=""></li>
	<li id="Fri1715" class=""></li>
	<li id="Fri1730" class=""></li>
	<li id="Fri1745" class=""></li>
	<li id="Fri1800" class=""></li>
	<li id="Fri1815" class=""></li>
	<li id="Fri1830" class=""></li>
	<li id="Fri1845" class=""></li>
	<li id="Fri1900" class=""></li>
	<li id="Fri1915" class=""></li>
	<li id="Fri1930" class=""></li>
	<li id="Fri1945" class=""></li>
	<li id="Fri2000" class=""></li>
	<li id="Fri2015" class=""></li>
	<li id="Fri2030" class=""></li>
	<li id="Fri2045" class=""></li>
	<li id="Fri2100" class=""></li>
	<li id="Fri2115" class=""></li>
	<li id="Fri2130" class=""></li>
	<li id="Fri2145" class=""></li>
	<li id="Fri2200" class=""></li>
	<li id="Fri2215" class=""></li>
	<li id="Fri2230" class=""></li>
	<li id="Fri2245" class=""></li>
	<li id="Fri2300" class=""></li>
	<li id="Fri2315" class=""></li>
	<li id="Fri2330" class=""></li>
	<li id="Fri2345" class=""></li>
	</ul>
</div>
	
<div class="weekday" id="SatList">
	<ul>
	<li id="Sat0000" class=""></li>
	<li id="Sat0015" class=""></li>
	<li id="Sat0030" class=""></li>
	<li id="Sat0045" class=""></li>
	<li id="Sat0100" class=""></li>
	<li id="Sat0115" class=""></li>
	<li id="Sat0130" class=""></li>
	<li id="Sat0145" class=""></li>
	<li id="Sat0200" class=""></li>
	<li id="Sat0215" class=""></li>
	<li id="Sat0230" class=""></li>
	<li id="Sat0245" class=""></li>
	<li id="Sat0300" class=""></li>
	<li id="Sat0315" class=""></li>
	<li id="Sat0330" class=""></li>
	<li id="Sat0345" class=""></li>
	<li id="Sat0400" class=""></li>
	<li id="Sat0415" class=""></li>
	<li id="Sat0430" class=""></li>
	<li id="Sat0445" class=""></li>
	<li id="Sat0500" class=""></li>
	<li id="Sat0515" class=""></li>
	<li id="Sat0530" class=""></li>
	<li id="Sat0545" class=""></li>
	<li id="Sat0600" class=""></li>
	<li id="Sat0615" class=""></li>
	<li id="Sat0630" class=""></li>
	<li id="Sat0645" class=""></li>
	<li id="Sat0700" class=""></li>
	<li id="Sat0715" class=""></li>
	<li id="Sat0730" class=""></li>
	<li id="Sat0745" class=""></li>
	<li id="Sat0800" class=""></li>
	<li id="Sat0815" class=""></li>
	<li id="Sat0830" class=""></li>
	<li id="Sat0845" class=""></li>
	<li id="Sat0900" class=""></li>
	<li id="Sat0915" class=""></li>
	<li id="Sat0930" class=""></li>
	<li id="Sat0945" class=""></li>
	<li id="Sat1000" class=""></li>
	<li id="Sat1015" class=""></li>
	<li id="Sat1030" class=""></li>
	<li id="Sat1045" class=""></li>
	<li id="Sat1100" class=""></li>
	<li id="Sat1115" class=""></li>
	<li id="Sat1130" class=""></li>
	<li id="Sat1145" class=""></li>
	<li id="Sat1200" class=""></li>
	<li id="Sat1215" class=""></li>
	<li id="Sat1230" class=""></li>
	<li id="Sat1245" class=""></li>
	<li id="Sat1300" class=""></li>
	<li id="Sat1315" class=""></li>
	<li id="Sat1330" class=""></li>
	<li id="Sat1345" class=""></li>
	<li id="Sat1400" class=""></li>
	<li id="Sat1415" class=""></li>
	<li id="Sat1430" class=""></li>
	<li id="Sat1445" class=""></li>
	<li id="Sat1500" class=""></li>
	<li id="Sat1515" class=""></li>
	<li id="Sat1530" class=""></li>
	<li id="Sat1545" class=""></li>
	<li id="Sat1600" class=""></li>
	<li id="Sat1615" class=""></li>
	<li id="Sat1630" class=""></li>
	<li id="Sat1645" class=""></li>
	<li id="Sat1700" class=""></li>
	<li id="Sat1715" class=""></li>
	<li id="Sat1730" class=""></li>
	<li id="Sat1745" class=""></li>
	<li id="Sat1800" class=""></li>
	<li id="Sat1815" class=""></li>
	<li id="Sat1830" class=""></li>
	<li id="Sat1845" class=""></li>
	<li id="Sat1900" class=""></li>
	<li id="Sat1915" class=""></li>
	<li id="Sat1930" class=""></li>
	<li id="Sat1945" class=""></li>
	<li id="Sat2000" class=""></li>
	<li id="Sat2015" class=""></li>
	<li id="Sat2030" class=""></li>
	<li id="Sat2045" class=""></li>
	<li id="Sat2100" class=""></li>
	<li id="Sat2115" class=""></li>
	<li id="Sat2130" class=""></li>
	<li id="Sat2145" class=""></li>
	<li id="Sat2200" class=""></li>
	<li id="Sat2215" class=""></li>
	<li id="Sat2230" class=""></li>
	<li id="Sat2245" class=""></li>
	<li id="Sat2300" class=""></li>
	<li id="Sat2315" class=""></li>
	<li id="Sat2330" class=""></li>
	<li id="Sat2345" class=""></li>
	</ul>
</div>

</div>  <!-- end currentschedule div -->

<br/>
<a class="warnlink" href="<c:url value="clear-entire-schedule.html"/>"><spring:message code="clear.entire.availability.schedule"/></a>
&nbsp;|&nbsp;
<a class="warnlink" href="<c:url value="removeAccount.html"/>"><spring:message code="remove.my.account"/></a>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>