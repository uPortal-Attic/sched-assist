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
<rs:resourceURL var="successIcon" value="/rs/famfamfam/silk/1.3/accept.png"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/available-common.css"/>" media="all"/>
<div class="SchedulingAssistant">
<div class="success">
<fmt:formatDate value="${event.startDate.date}" type="time" pattern="h:mm a" var="timeFormatted"/>
<fmt:formatDate value="${event.startDate.date}" type="date" pattern="MM/dd/yyyy" var="dateFormatted"/>
<p>
<spring:message code="create.appointment.success" arguments="${targetOwner.calendarAccount.displayName};${timeFormatted};${dateFormatted};${event.location.value}" argumentSeparator=";"/>
</p>
<c:if test="${targetOwner.remindersPreference.enabled }">
<p>
<spring:message code="create.appointment.reminder" arguments="${targetOwner.calendarAccount.displayName};${targetOwner.remindersPreference.hours}" argumentSeparator=";"/>
</p>
</c:if>
<portlet:renderURL var="returnUrl" windowState="normal">
	<portlet:param name="_eventId" value="home"/>
</portlet:renderURL>
<portlet:renderURL var="scheduleUrl" windowState="maximized">
	<portlet:param name="_eventId" value="schedule-return"/>
	<portlet:param name="execution" value="${flowExecutionKey}" />
	<portlet:param name="weekStart" value="${currentWeekStart}"/>
	<portlet:param name="ownerId" value="${targetOwner.id}"/>
</portlet:renderURL>
<a href="${scheduleUrl}">&laquo;<spring:message code="return.to.schedule"/></a> or <a href="${returnUrl}"><spring:message code="make.appointment.with.someone.else"/>&raquo;</a>
</div> <!-- close success -->
</div> <!-- close SchedulingAssistant -->