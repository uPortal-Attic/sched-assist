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
<c:set var="n"><portlet:namespace/></c:set>
<rs:resourceURL var="infoIcon" value="/rs/famfamfam/silk/1.3/information.png"/>
<rs:resourceURL var="exclIcon" value="/rs/famfamfam/silk/1.3/exclamation.png"/>
<rs:resourceURL var="delIcon" value="/rs/famfamfam/silk/1.3/calendar_delete.png"/>
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
}
.SchedulingAssistant .currentschedule ul li span {
vertical-align: top;
}
.SchedulingAssistant .currentschedule ul li img {
border: 0;
}
.SchedulingAssistant .currentschedule ul li.dayhead	{
background-color: #ebebeb;
border: 1px solid #7a7a7a;
font-size: 16px;
text-align: center;
}
.SchedulingAssistant .currentschedule ul li.attending {
background-color: #6c8bdd;
border: 1px solid #0000aa;
height: 2.5em;
}
.SchedulingAssistant .currentschedule ul li.attending a {
color: #fff;
}
.SchedulingAssistant .weekday {
float: left;
width: 140px;
margin-right: 2px;
}
.SchedulingAssistant .weekcontainer	{
clear: both;
}
.SchedulingAssistant .limitExceededDescription { width:50%;margin-bottom:1em; }
.SchedulingAssistant .weekday {margin-bottom:1em;}
.SchedulingAssistant .info {width:50%;}
</style>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.3.1/jquery-1.3.1.min.js"/>"></script>
<rs:resourceURL var="linkify" value="/js/linkify.js"/>
<script type="text/javascript" src="${linkify}"></script>
<script type="text/javascript">
    var ${n} = ${n} || {};
    ${n}.jQuery = jQuery.noConflict();

    ${n}.jQuery(function(){
    	var $ = ${n}.jQuery;

    	$('.noteboardtext').each(function(i,element){	
    		existing = $(element).text();
    		$(element).html(linkify(existing));
    	});
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

<div class="info">
<span style="font-weight: bold;"><spring:message code="a.message.from" arguments="${owner.calendarAccount.displayName}"/>:</span><br/>
<c:forEach items="${ownerNoteboardSentences }" var="sentence">
<span class="noteboardtext"><c:out value="${sentence}"/></span><br/>
</c:forEach>
</div>

<div class="currentschedule">
<div class="info limitExceededDescription">
<spring:message code="limit.exceeded.description"/>
</div>
<div class="weekday">
<ul>
<c:forEach items="${visibleSchedule.attendingList}" var="attending">
<li class="dayhead"><fmt:formatDate value="${attending.startTime}" pattern="EEE M/d" type="date"/></li>
<fmt:formatDate value="${attending.startTime}" pattern="yyyyMMdd-HHmm" var="startTime" type="time"/>
<fmt:formatDate value="${attending.endTime}" pattern="yyyyMMdd-HHmm" var="endTime" type="time"/>
<portlet:renderURL var="cancelUrl">
<portlet:param name="_eventId" value="cancel"/>
<portlet:param name="execution" value="${flowExecutionKey}" />
<portlet:param name="startTime" value="${startTime}"/>
<portlet:param name="endTime" value="${endTime}"/>
<portlet:param name="ownerId" value="${owner.id}"/>
</portlet:renderURL>
<li title="<spring:message code="cancel.my.appointment"/>" class="attending">
<a href="${cancelUrl}">
<img alt="" src="${delIcon}"/>&nbsp;
<span><fmt:formatDate value="${attending.startTime}" pattern="hh:mm a" type="time"/>&nbsp;-&nbsp;<fmt:formatDate value="${attending.endTime}" pattern="hh:mm a" type="time"/></span>
</a>
</li>
</c:forEach>
</ul>
</div>
</div> <!--  end currentschedule div -->

</div> <!-- content -->