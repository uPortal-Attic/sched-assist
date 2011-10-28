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
<title><spring:message code="application.name"/> - <spring:message code="appointment.full"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<style type="text/css">
#status p { margin-bottom: 1em; }
</style>

</head>
<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>

<div id="content" class="main col">
<div id="status" class="alert">
<fmt:formatDate value="${event.startDate.date}" type="time" pattern="h:mm a" var="timeFormatted"/>
<fmt:formatDate value="${event.startDate.date}" type="date" pattern="MM/dd/yyyy" var="dateFormatted"/>
<p>
<c:choose>
<c:when test="${visitorLimit == 1}">
<spring:message code="appointment.full.single" arguments="${owner.calendarAccount.displayName};${timeFormatted};${dateFormatted};${event.location.value}" argumentSeparator=";"/>
</c:when>
<c:otherwise>
<spring:message code="appointment.full.group" arguments="${owner.calendarAccount.displayName};${timeFormatted};${dateFormatted};${event.location.value}" argumentSeparator=";"/>
</c:otherwise>
</c:choose>
</p>
<a href="view.html">&laquo;<spring:message code="return.to.schedule"/></a>, or <a href="/wisccal-login"><spring:message code="log.in.to.calendar.service"/>&raquo;</a>
</div> <!--  end status -->
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>