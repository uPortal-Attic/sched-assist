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
<%@ page import="javax.portlet.PortletMode"%>
<rs:resourceURL var="infoIcon" value="/rs/famfamfam/silk/1.3/information.png"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/available-common.css"/>" media="all"/>
<portlet:defineObjects/>
<div class="SchedulingAssistant">
<c:choose>
<c:when test="${empty relationships}">
<div class="info">
<p>
<spring:message code="visitor.relationships.empty"/>
</p>
<spring:message code="visitor.relationships.empty.more"/>:<br/>
<a target="_new_advisor_window" href="${advisorUrl}" title="Opens new window to WiscCal Schedules for UW Academic Advisors">WiscCal Schedules for UW Academic Advisors&raquo;</a>
</div>
</c:when>
<c:otherwise>
<div class="info">
<spring:message code="visitor.relationships.help"/>:
</div>
<ul>
<c:forEach items="${relationships}" var="relationship">
<portlet:renderURL var="renderUrl" windowState="maximized">
	<portlet:param name="execution" value="${flowExecutionKey}" />
	<portlet:param name="_eventId" value="viewSchedule"/>
	<portlet:param name="ownerId" value="${relationship.owner.id}"/>
	<portlet:param name="weekStart" value="0"/>
</portlet:renderURL>
<li><a href="${renderUrl}">${relationship.owner.calendarAccount.displayName}</a>&nbsp;(${relationship.description})</li>
</c:forEach>
</ul>
</c:otherwise>
</c:choose>
<hr/>
<a target="_new_search_window" href="${searchUrl}" title="<spring:message code="search.public.profiles.link.title"/>"><spring:message code="search.public.profiles"/>&raquo;</a><br/>
<a target="_new_sa_window" href="${homeUrl}" title="<spring:message code="log.in.to.scheduling.assistant.link.title"/>"><spring:message code="log.in.to.scheduling.assistant"/>&raquo;</a>
</div> <!--  close SchedulingAssistant -->