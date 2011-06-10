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
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title><spring:message code="application.name"/> - <spring:message code="visitor.history.for" arguments="${command.visitorUsername}"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<style type="text/css">
.unknown {font-style: italic;}
#results {margin-top: 10px; margin-bottom:10px;}
</style>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<div id="content" class="main col">
<h4><spring:message code="visitor.history.for" arguments="${command.visitorUsername}"/></h4>

<div id="visitorInfo" class="info">
<c:choose>
	<c:when test="${empty visitorAccount}">
		<span class="unknown"><spring:message code="visitor.history.no.results" arguments="${command.visitorUsername}"/></span>
	</c:when>
	<c:otherwise>
		<p><spring:message code="visitor.information"/>:</p>
		<ul>
			<li><spring:message code="username"/>:&nbsp;<c:out value="${visitorAccount.username}" /></li>
			<li><spring:message code="name"/>:&nbsp;<c:out value="${visitorAccount.displayName}" /></li>
			<li><spring:message code="email.address"/>:&nbsp;<c:out value="${visitorAccount.emailAddress}" /></li>
		</ul>
	</c:otherwise>
</c:choose>
</div>
<div id="results">

<c:choose>
<c:when test="${empty events}">
<p><spring:message code="visitor.history.no.activity"/></p>
</c:when>
<c:otherwise>
<table summary="<spring:message code="scheduling.assistant.history"/>" class="history">
	<thead>
		<tr>
			<th class="eventtype-header"><acronym title="<spring:message code="activity.type.help"/>"><spring:message code="activity.type"/></acronym></th>
			<th class="eventtimestamp-header"><acronym title="<spring:message code="activity.timestamp.help"/>"><spring:message code="activity.timestamp"/></acronym></th>
			<th class="appointmentstart-header"><acronym title="<spring:message code="appointment.start.time.help"/>"><spring:message code="appointment.start.time"/></acronym></th>
		</tr>
	</thead>
	<tbody id="body-template">
		<c:forEach items="${events}" var="event">
			<tr>
				<td><c:out value="${event.eventType}" /></td>
				<td><fmt:formatDate value="${event.eventTimestamp}" type="time" pattern="MM/dd/yyyy h:mm:ss a"/></td>
				<td><fmt:formatDate value="${event.appointmentStartTime}" type="time" pattern="MM/dd/yyyy h:mm:ss a"/></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</c:otherwise>
</c:choose>

</div>

<a href="<c:url value="visitor-history.html"/>">&laquo;<spring:message code="return.to.visitor.history.form"/></a>, or <a href="<c:url value="schedule.html"/>">&laquo;<spring:message code="return.to.availability.schedule"/></a>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>