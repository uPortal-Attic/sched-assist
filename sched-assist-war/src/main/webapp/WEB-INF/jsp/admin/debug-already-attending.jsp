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
<rs:resourceURL var="cancelIcon" value="/rs/famfamfam/silk/1.3/calendar_delete.png"/>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title><spring:message code="application.name.admin"/> - <spring:message code="visible.schedule.debug.for" arguments="${owner.calendarAccount.displayName}"/> (<spring:message code="meeting.limit.exceeded"/>)</title>
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
#limitExceededDescription { width:50%;margin-bottom:1em; }
.weekday {margin-bottom:1em;}
.clearFloats {clear:both;}
.killborder {border:0;}
</style>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>

<div id="content" class="main col">
<rs:resourceURL var="emailIcon" value="/rs/famfamfam/silk/1.3/email.png"/>
<div class="info">
<p><spring:message code="schedule.owner"/>:</p>
<ul>
<li><spring:message code="username"/>: <c:out value="${owner.calendarAccount.username }"/></li>
<li><spring:message code="displayname"/>: <c:out value="${owner.calendarAccount.displayName}"/></li>
<li><spring:message code="email.address"/>: <a href="mailto:${owner.calendarAccount.emailAddress}" title="Compose Email"><c:out value="${owner.calendarAccount.emailAddress}"/>&nbsp;<img class="killborder" src="${emailIcon}"/></a></li>
<li><spring:message code="calendaruniqueid"/>: <c:out value="${owner.calendarAccount.calendarUniqueId}"/></li>
<li><spring:message code="calendarloginid"/>: <c:out value="${owner.calendarAccount.calendarLoginId}"/></li>
</ul>
<p><spring:message code="schedule.visitor"/>:</p>
<ul>
<li><spring:message code="username"/>: <c:out value="${visitor.calendarAccount.username }"/></li>
<li><spring:message code="displayname"/>: <c:out value="${visitor.calendarAccount.displayName}"/></li>
<li><spring:message code="email.address"/>: <a href="mailto:${visitor.calendarAccount.emailAddress}" title="Compose Email"><c:out value="${visitor.calendarAccount.emailAddress}"/>&nbsp;<img class="killborder" src="${emailIcon}"/></a></li>
<li><spring:message code="calendaruniqueid"/>: <c:out value="${visitor.calendarAccount.calendarUniqueId}"/></li>
<li><spring:message code="calendarloginid"/>: <c:out value="${visitor.calendarAccount.calendarLoginId}"/></li>
</ul>
</div>

<div id="pageControls">
<ul>
<li><a href="<c:url value="/admin/schedule-debug.html"/>">&laquo;<spring:message code="return.to.schedule.debug.form"/></a></li>
<c:choose>
<c:when test="${highContrast}">
<li><a class="standard" href="<c:url value="view.html?visitorUsername=${visitor.calendarAccount.username}"/>"><spring:message code="standard.view"/></a></li>
<li><a class="refresh" href="<c:url value="view.html?highContrast=true&visitorUsername=${visitor.calendarAccount.username}"/>"><spring:message code="refresh"/></a></li>
</c:when>
<c:otherwise>
<li><a class="increase" href="<c:url value="view.html?highContrast=true&visitorUsername=${visitor.calendarAccount.username}"/>"><spring:message code="high.contrast.view"/></a></li>
<li><a class="refresh" href="<c:url value="view.html?visitorUsername=${visitor.calendarAccount.username}"/>"><spring:message code="refresh"/></a></li>
</c:otherwise>
</c:choose>
<li><a target="new" class="loginlink" title="<spring:message code="calendar.log.in.title"/>" href="/wisccal-login"><spring:message code="calendar.log.in"/>&raquo;</a></li>
</ul>
</div> <!-- end pageControls -->

<div id="noteboard" class="info">
<span style="font-weight: bold;"><spring:message code="a.message.from" arguments="${owner.calendarAccount.displayName}" argumentSeparator=";"/></span><br/>
<span id="noteboardtext"><c:out value="${noteboard}"/></span>
</div>

<div id="currentschedule">
<div class="info" id="limitExceededDescription">
<spring:message code="limit.exceeded.description"/>
</div>
<div class="weekcontainer">
<div class="weekday">
<ul class="scheduleblocks">
<c:forEach items="${attendingList}" var="attending">
<li class="dayhead"><fmt:formatDate value="${attending.startTime}" pattern="EEE M/d" type="date"/></li>
<li title="<spring:message code="cancel.my.appointment"/>" class="attending">
<img alt="" src="${cancelIcon}"/>&nbsp;
<span><fmt:formatDate value="${attending.startTime}" pattern="hh:mm a" type="time"/>&nbsp;-&nbsp;<fmt:formatDate value="${attending.endTime}" pattern="hh:mm a" type="time"/></span>
</li>
</c:forEach>
</ul>
</div> <!-- end weekday -->
</div> <!-- end weekcontainer -->
</div> <!-- end currentschedule div -->
<div class="clearFloats"></div>


<div class="debugContainer">
<h4><spring:message code="schedule.owner.calendar.data"/></h4>
<textarea rows="25" cols="100">${ownerCalendarData}</textarea>
</div>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>