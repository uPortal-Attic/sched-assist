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
<%@ taglib prefix="available" uri="/available" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title><spring:message code="application.name.admin"/> - <spring:message code="visible.schedule.for" arguments="${owner.calendarAccount.displayName}"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>

<link rel="stylesheet" type="text/css" href="<c:url value="/css/visitor-common.css"/>" media="all"/>
<c:choose>
<c:when test="${highContrast}">
<link rel="stylesheet" type="text/css" href="<c:url value="/css/visitor-view-hc.css"/>" media="all"/>
</c:when>
<c:otherwise>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/visitor-view.css"/>" media="all"/>
</c:otherwise>
</c:choose>
<style type="text/css">
.conflict {
background-color: #ffffb0; 
border: 1px solid #aaaa00;
height: 2.5em;
}
#helptext {
float:left;
width:40%;
}
#colorLegend {
float:right;
border:2px solid #A9A9A9;
padding: 8px;
}
#colorLegend ul li{
display:inline;
padding:0.5em 0.5em 0.25em 0.5em;
}
.clearFloats {
clear:both;
}
</style>


<rs:resourceURL var="jqueryUiPath" value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.min.js"/>
<script type="text/javascript" src="${jqueryUiPath}"></script>
<script type="text/javascript" src="<c:url value="/js/owner-schedule-utils.js"/>"></script>
<c:url value="visitor-conflicts.json" var="conflictsUrl"/>
<rs:resourceURL var="dateErrorPng" value="/rs/famfamfam/silk/1.3/date_error.png"/>
<rs:resourceURL var="prevWeekImg" value="/rs/famfamfam/silk/1.3/arrow_left.png"/>
<rs:resourceURL var="nextWeekImg" value="/rs/famfamfam/silk/1.3/arrow_right.png"/>
<rs:resourceURL var="tickPng" value="/rs/famfamfam/silk/1.3/tick.png"/>
<script type="text/javascript">
jQuery(document).ready(function() {
	$('.noteboardtext').each(function(i,element){	
		existing = $(element).text();
		$(element).html(linkify(existing));
	});
	if(!${ownerVisitorSamePerson} && ${visibleSchedule.size} != 0) {

		var helpTextElem = $('#helptext');
		$.ajaxSetup({
    		"error": function(XMLHttpRequest,textStatus, errorThrown) {   
    			if(null != XMLHttpRequest) {
        			helpTextElem.removeClass();
        			helpTextElem.addClass('alert');
    				if(XMLHttpRequest.status >= 500) {
    					helpTextElem.text('<spring:message code="visible.schedule.error.continue"/>');
    				} else {
    					helpTextElem.text('<spring:message code="unexpected.error"/> Status code: ' + XMLHttpRequest.status);
    				}
    			}
    		}
    	});
    	
	showChangeInProgress("#helptext", '<spring:message code="checking.for.conflicts"/>');
	jQuery.getJSON('${conflictsUrl}',
		{ weekStart: '${weekStart}' },
		function(data) {
			helpTextElem.append('Complete!&nbsp;');
			$('<img src="${tickPng}" alt=""/>').appendTo(helpTextElem);
			var hasConflicts = false;
			if(data.conflicts) {
				jQuery.each(data.conflicts, function(i,conflict) {
					elem = $('#' + conflict);
					if(elem.hasClass('free')) {
						elem.removeClass('free');
						elem.addClass('conflict');
						elem.attr('title', '<spring:message code="conflict.help"/>');
						imgElem = $('#' + conflict + ' img');
						imgElem.attr('src', '${dateErrorPng}');
						hasConflicts = true;
					}
				});
			} 
			if(hasConflicts) {
				window.setTimeout(function() {
					showChangeError('#helptext', '<spring:message code="conflict.block.description"/>');
				}, 1500);
			} else {
				window.setTimeout(function() {
					showChangeSuccess('#helptext', '<spring:message code="no.conflicts"/>');
				}, 1500);
			}
		}
	);

	}
});
</script>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">
<noscript>
<div class="alert">
<p><spring:message code="visible.schedule.noscript"/></p>
</div>
</noscript>

<div id="pageControls">
<ul>
<li><a href="<c:url value="/visitor.html"/>">&laquo;<spring:message code="make.appointment.with.someone.else"/></a></li>
<c:choose>
<c:when test="${highContrast}">
<li><a class="standard" href="<c:url value="view.html?weekStart=${weekStart}"/>"><spring:message code="standard.view"/></a></li>
</c:when>
<c:otherwise>
<li><a class="increase" href="<c:url value="view.html?highContrast=true&weekStart=${weekStart}"/>"><spring:message code="high.contrast.view"/></a></li>
</c:otherwise>
</c:choose>
<li><a class="refresh" href="<c:url value="view.html?highContrast=${highContrast}&weekStart=${weekStart}"/>"><spring:message code="refresh"/></a></li>
<li><a target="new" class="loginlink" title="<spring:message code="calendar.log.in.title"/>" href="/wisccal-login"><spring:message code="calendar.log.in"/>&raquo;</a></li>
</ul>
</div> <!-- end pageControls -->
<div id="noteboard" class="info">
<span style="font-weight: bold;"><spring:message code="a.message.from" arguments="${owner.calendarAccount.displayName}" argumentSeparator=";"/></span><br/>
<c:forEach var="noteboardSentence" items="${noteboardSentences}" varStatus="itemCount">
<span class="noteboardtext"><c:out value="${noteboardSentence}"/></span><br/>
</c:forEach>
</div>


<c:if test="${visibleSchedule.size != 0 && !ownerVisitorSamePerson}">
<div id="helplegendcontainer">
<div id="helptext"><!-- initially empty --></div>
<div id="colorLegend">
<rs:resourceURL var="freePng" value="/rs/famfamfam/silk/1.3/calendar_add.png"/>
<rs:resourceURL var="busyPng" value="/rs/famfamfam/silk/1.3/delete.png"/>
<rs:resourceURL var="attendPng" value="/rs/famfamfam/silk/1.3/calendar_delete.png"/>
<ul>
<li><strong><spring:message code="legend"/></strong></li>
<li class="free"><img src="${freePng}"/><acronym title="<spring:message code="available.shortdescription"/>"><spring:message code="available"/></acronym></li>
<li class="conflict"><img src="${dateErrorPng}"/><acronym title="<spring:message code="conflict.shortdescription"/>"><spring:message code="conflict"/></acronym></li>
<li class="busy"><img src="${busyPng}"/><acronym title="<spring:message code="busy.shortdescription"/>"><spring:message code="busy"/></acronym></li>
<li class="attending"><img src="${attendPng}"/><acronym title="<spring:message code="attending.shortdescription"/>"><spring:message code="attending"/></acronym></li>
</ul>
</div>
</div> <!-- close helplegendcontainer -->
</c:if>

<c:if test="${ownerVisitorSamePerson}">
<div class="alert">
<spring:message code="owner.visitor.same.person.help"/>
</div>
</c:if>

<div class="weeknavigation clearFloats">
<ul>
<c:if test="${not empty prevWeekStart}">
<li><a class="prevWeekLink" title="<spring:message code="week.navigation.prev.title" arguments="${owner.calendarAccount.displayName}"/>" href="<c:url value="view.html?weekStart=${prevWeekStart}&highContrast=${highContrast}"/>"><img class="killborder" src="${prevWeekImg}" alt=""/>&nbsp;<spring:message code="week.navigation.prev"/></a></li>
</c:if>
<c:if test="${not empty nextWeekStart}">
<li><a class="nextWeekLink" title="<spring:message code="week.navigation.next.title" arguments="${owner.calendarAccount.displayName}"/>" href="<c:url value="view.html?weekStart=${nextWeekStart}&highContrast=${highContrast}"/>"><spring:message code="week.navigation.next"/>&nbsp;<img class="killborder" src="${nextWeekImg}" alt=""/></a></li>
</c:if>
</ul>
</div>
<div id="currentschedule">
<available:render visibleSchedule="${visibleSchedule}" previewMode="${ownerVisitorSamePerson}"></available:render>
</div> <!--  end currentschedule div -->

<div class="clearFloats"></div>

<div class="weeknavigation">
<ul>
<c:if test="${not empty prevWeekStart}">
<li><a class="prevWeekLink" title="<spring:message code="week.navigation.prev.title" arguments="${owner.calendarAccount.displayName}"/>" href="<c:url value="view.html?weekStart=${prevWeekStart}&highContrast=${highContrast}"/>"><img class="killborder" src="${prevWeekImg}" alt=""/>&nbsp;<spring:message code="week.navigation.prev"/></a></li>
</c:if>
<c:if test="${not empty nextWeekStart}">
<li><a class="nextWeekLink" title="<spring:message code="week.navigation.next.title" arguments="${owner.calendarAccount.displayName}"/>" href="<c:url value="view.html?weekStart=${nextWeekStart}&highContrast=${highContrast}"/>"><spring:message code="week.navigation.next"/>&nbsp;<img class="killborder" src="${nextWeekImg}" alt=""/></a></li>
</c:if>
</ul>
</div>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>