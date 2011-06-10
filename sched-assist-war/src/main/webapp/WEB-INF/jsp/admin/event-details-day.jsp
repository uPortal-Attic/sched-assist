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
<title><spring:message code="application.name.admin"/> - <spring:message code="daily.event.details"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">

<c:choose>

<c:when test="${empty date}">
<p><spring:message code="no.date.selected"/></p>
</c:when>
<c:when test="${empty events}">
<p><spring:message code="no.events.for"/> <i><fmt:formatDate value="${date}" pattern="MM/dd/yyyy"/></i>.</p>
</c:when>
<c:otherwise>
<h3><spring:message code="event.details.for"/> <i><fmt:formatDate value="${date}" pattern="MM/dd/yyyy"/></i></h3>

<table>
<thead>
<tr>
<th><spring:message code="event.timestamp"/></th>
<th><spring:message code="owner.id"/></th>
<th><spring:message code="schedule.owner"/>&nbsp;<spring:message code="username"/></th>
<th><spring:message code="schedule.visitor"/>&nbsp;<spring:message code="username"/></th>
<th><spring:message code="event.type"/></th>
<th><spring:message code="event.starttime"/></th>
<th><spring:message code="event.id"/></th>
</tr>
</thead>
<tbody>

<c:forEach items="${events}" var="event">
<tr class="stats-row">
<td><fmt:formatDate value="${event.eventTimestamp}" pattern="MM/dd/yyyy HH:mm:ss"/></td>
<td>${event.ownerId }</td>
<td>
<c:choose>
<c:when test="${ not empty event.scheduleOwner}">
<a title="<spring:message code="schedule.owner.details"/>" href="<c:url value="account-details.html?id=${event.scheduleOwner.calendarAccount.calendarUniqueId}"/>">${event.scheduleOwner.calendarAccount.username}</a>
</c:when>
<c:otherwise>
<spring:message code="not.available"/>
</c:otherwise>
</c:choose>
</td>
<td>${event.visitorId }</td>
<td>${event.eventType }</td>
<td><fmt:formatDate value="${event.appointmentStartTime}" pattern="MM/dd/yyyy HH:mm"/></td>
<td>${event.eventId }</td>
</tr>
</c:forEach>
</tbody>
</table>
</c:otherwise>
</c:choose>
<a href="<c:url value="events-summary.html"/>">&laquo;<spring:message code="return.to.event.statistics.form"/></a>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>