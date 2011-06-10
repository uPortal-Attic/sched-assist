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

<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib prefix="sched-assist" uri="/sched-assist" %>
<rs:resourceURL var="infoIcon" value="/rs/famfamfam/silk/1.3/information.png"/>
<rs:resourceURL var="exclIcon" value="/rs/famfamfam/silk/1.3/exclamation.png"/>
<rs:resourceURL var="dateErrorPng" value="/rs/famfamfam/silk/1.3/date_error.png"/>
<c:url var="progressIcon" value="/img/spinner.gif"/>

<c:set var="n"><portlet:namespace/></c:set>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/available-common.css"/>" media="all"/>
<style type="text/css">
.SchedulingAssistant .weekday a:link, .SchedulingAssistant .weekday a:visited, .SchedulingAssistant .weekday a:hover,.SchedulingAssistant .weekday a:active {
color: #000000 !important;
font-weight: normal;
}
.SchedulingAssistant .weekday a:link, .SchedulingAssistant .weekday a:visited, .SchedulingAssistant .weekday a:active {
text-decoration: none !important;
}
.SchedulingAssistant .weekday a:hover {
text-decoration: underline !important;
}
.SchedulingAssistant a.returnLink {
color: #990000;
text-decoration: underline;
font-size: 120%;
}
.SchedulingAssistant .legend {
font: 10pt sans-serif;
font-weight: bold;
}
.SchedulingAssistant .none-available {
font: 12pt sans-serif;
font-style: italic;
}
.SchedulingAssistant .currentschedule {
margin-top: 4px;
}
.SchedulingAssistant .currentschedule ul {
list-style-type: none;
margin: 0px;
padding: 0px;
}
.SchedulingAssistant .currentschedule ul li {
list-style: none;
margin-bottom: 1px;
font: 10px sans-serif;
border-radius: 0.25em;
-moz-border-radius: 0.25em;
-webkit-border-radius: 0.25em;
}
.SchedulingAssistant .currentschedule ul li span {
vertical-align: top;
}
.SchedulingAssistant .currentschedule ul li img {
border: 0;
}
.SchedulingAssistant .free {
background-color: #b0ffb0;
border: 1px solid #00aa00;
height: 2.5em;
}
.SchedulingAssistant .busy {
background-color: #ffb0b0;
border: 1px solid #aa0000;
height: 2.5em;
}
.SchedulingAssistant .currentschedule ul li.dayhead	{
background-color: #ebebeb;
border: 1px solid #7a7a7a;
font-size: 16px;
text-align: center;
}
.SchedulingAssistant .attending {
background-color: #6c8bdd;
border: 1px solid #0000aa;
height: 2.5em;
}
.SchedulingAssistant .attending a {
color: #fff;
}
.SchedulingAssistant .conflict {
background-color: #ffffb0;
border: 1px solid #aaaa00;
height: 2.5em;
}
.SchedulingAssistant .weekday {
float: left;
width: 140px;
margin-right: 2px;
}
.SchedulingAssistant .weekcontainer	{
clear: both;
padding-top: 1em;
}
.SchedulingAssistant .weeknavigation {
font-weight:bold;
font-size:125%;
}
.SchedulingAssistant .weeknavigation ul {
margin-left: 0px;
padding-left: 5px;
list-style-type: none;
}
.SchedulingAssistant .weeknavigation ul li {
display: inline;
padding-right: 10px;
}
.SchedulingAssistant .clearFloats { clear:both; }
.SchedulingAssistant .info {width:40%;}
.SchedulingAssistant .alert {width:40%;}
.SchedulingAssistant .success {width:40%;}
#${n}helptext { margin-top: 2px;float:left;width:40%;}
#${n}colorLegend {float:right;border:2px solid #A9A9A9;padding: 0px 2px 0px 2px;}
#${n}colorLegend ul li{display:inline; padding:0.5em 0.5em 0.25em 0.5em;}
.SchedulingAssistant .clearFloats {clear:both;}
</style>
<c:url value="/ajax/visitor-conflicts.json" var="visitorConflictsUrl"/>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.3.1/jquery-1.3.1.min.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jqueryui/1.6rc6/jquery-ui-1.6rc6.min.js"/>"></script>
<rs:resourceURL var="linkify" value="/js/linkify.js"/>
<script type="text/javascript" src="${linkify}"></script>
<rs:resourceURL var="tickPng" value="/rs/famfamfam/silk/1.3/tick.png"/>
<script type="text/javascript">
    var ${n} = ${n} || {};
    ${n}.jQuery = jQuery.noConflict();

    ${n}.jQuery(function(){
    	var $ = ${n}.jQuery;

    	$('.noteboardtext').each(function(i,element){	
    		existing = $(element).text();
    		$(element).html(linkify(existing));
    	});
    	
    	var helpTextElem = $('#${n}helptext');
    	
    	var loadConflicts = function() {	
        	helpTextElem.addClass('inprogress');
        	helpTextElem.text('<spring:message code="checking.for.conflicts"/>');
    		 $.ajax({
              	url: '${visitorConflictsUrl}',
              	data: { weekStart: '${weekStart}', ownerId: '${owner.id}' },
              	type: "GET",
             	dataType: "json",
             	async: false,
             	success: function(data) {
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
        					// show conflicts help
        					helpTextElem.removeClass('inprogress');
        					helpTextElem.addClass('alert');
        					helpTextElem.text('<spring:message code="conflict.block.description"/>');
                     	}, 1500);
    				} else {
        				window.setTimeout(function() {
        					// hide conflicts help
        					helpTextElem.removeClass('inprogress');
        					helpTextElem.addClass('success');
        					helpTextElem.text('<spring:message code="no.conflicts"/>');
        				}, 1500);
    				}
              	}
    		 });
    	};

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
    	if(!${ownerVisitorSamePerson} && ${visibleSchedule.size} != 0) {
    		loadConflicts();
    	}
    });
</script>


<%-- START content --%>
<portlet:defineObjects/>
<div class="SchedulingAssistant">
<portlet:renderURL var="returnUrl" windowState="normal">
<portlet:param name="_eventId" value="done"/>
<portlet:param name="execution" value="${flowExecutionKey}" />
</portlet:renderURL>
<a class="returnLink" href="${returnUrl}">&laquo;<spring:message code="make.appointment.with.someone.else"/></a>

<noscript>
<div class="alert">
<spring:message code="visible.schedule.noscript"/>
</div>
</noscript>

<div class="info">
<span style="font-weight: bold;">A message from (<c:out value="${owner.calendarAccount.displayName}"/>):</span><br/>
<c:forEach items="${ownerNoteboardSentences }" var="sentence">
<span class="noteboardtext"><c:out value="${sentence}"/></span><br/>
</c:forEach>
</div>

<c:if test="${visibleSchedule.size != 0 && !ownerVisitorSamePerson}">
<div id="${n}helplegendcontainer">
<div id="${n}helptext"><!-- initially empty --></div>
<div id="${n}colorLegend">
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
</div>
</c:if>

<c:if test="${ownerVisitorSamePerson}">
<div class="alert">
<spring:message code="owner.visitor.same.person.help"/>
</div>
</c:if>

<rs:resourceURL var="prevWeekImg" value="/rs/famfamfam/silk/1.3/arrow_left.png"/>
<rs:resourceURL var="nextWeekImg" value="/rs/famfamfam/silk/1.3/arrow_right.png"/>
<div class="weeknavigation clearFloats">
<ul>
<c:if test="${not empty requestConstraints.prevWeekIndex}">
<portlet:renderURL var="prevWeekUrl" windowState="maximized">
	<portlet:param name="_eventId" value="refresh"/>
	<portlet:param name="ownerId" value="${owner.id}"/>
	<portlet:param name="weekStart" value="${requestConstraints.prevWeekIndex}"/>
	<portlet:param name="execution" value="${flowExecutionKey}" />
</portlet:renderURL>
<li><a title="<spring:message code="week.navigation.prev.title" arguments="${owner.calendarAccount.displayName}"/>" href="${prevWeekUrl}"><img class="killborder" src="${prevWeekImg}" alt=""/>&nbsp;<spring:message code="week.navigation.prev"/></a></li>
</c:if>
<c:if test="${not empty requestConstraints.nextWeekIndex}">
<portlet:renderURL var="nextWeekUrl" windowState="maximized">
	<portlet:param name="_eventId" value="refresh"/>
	<portlet:param name="ownerId" value="${owner.id}"/>
	<portlet:param name="weekStart" value="${requestConstraints.nextWeekIndex}"/>
	<portlet:param name="execution" value="${flowExecutionKey}" />
</portlet:renderURL>
<li><a title="<spring:message code="week.navigation.next.title" arguments="${owner.calendarAccount.displayName}"/>" href="${nextWeekUrl}"><spring:message code="week.navigation.next"/>&nbsp;<img class="killborder" src="${nextWeekImg}" alt=""/></a></li>
</c:if>
</ul>
</div>

<div class="currentschedule">
<sched-assist:render visibleSchedule="${visibleSchedule}" previewMode="${ownerVisitorSamePerson}" flowExecutionKey="${flowExecutionKey}"></sched-assist:render>
</div> <!--  close currentschedule -->

<div class="clearFloats"></div>

<div class="weeknavigation">
<ul>
<c:if test="${not empty requestConstraints.prevWeekIndex}">
<portlet:renderURL var="prevWeekUrl" windowState="maximized">
	<portlet:param name="ownerId" value="${owner.id}"/>
	<portlet:param name="weekStart" value="${requestConstraints.prevWeekIndex}"/>
	<portlet:param name="execution" value="${flowExecutionKey}" />
</portlet:renderURL>
<li><a title="<spring:message code="week.navigation.prev.title" arguments="${owner.calendarAccount.displayName}"/>" href="${prevWeekUrl}"><img class="killborder" src="${prevWeekImg}" alt=""/>&nbsp;<spring:message code="week.navigation.prev"/></a></li>
</c:if>
<c:if test="${not empty requestConstraints.nextWeekIndex}">
<portlet:renderURL var="nextWeekUrl" windowState="maximized">
	<portlet:param name="ownerId" value="${owner.id}"/>
	<portlet:param name="weekStart" value="${requestConstraints.nextWeekIndex}"/>
	<portlet:param name="execution" value="${flowExecutionKey}" />
</portlet:renderURL>
<li><a title="<spring:message code="week.navigation.next.title" arguments="${owner.calendarAccount.displayName}"/>" href="${nextWeekUrl}"><spring:message code="week.navigation.next"/>&nbsp;<img class="killborder" src="${nextWeekImg}" alt=""/></a></li>
</c:if>
</ul>
</div>

</div> <!-- close SchedulingAssistant -->
