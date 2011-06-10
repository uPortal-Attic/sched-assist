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
<title><spring:message code="application.name.admin"/> - <spring:message code="event.statistics"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">

<c:choose>

<c:when test="${empty eventCounts}">
<p><spring:message code="no.events.for"/> <i><fmt:formatDate value="${command.start}" pattern="MM/dd/yyyy"/> - <fmt:formatDate value="${command.end}" pattern="MM/dd/yyyy"/></i>.</p>
</c:when>
<c:otherwise>
<h3><spring:message code="event.statistics.for"/>&nbsp;<i><fmt:formatDate value="${command.start}" pattern="MM/dd/yyyy"/> - <fmt:formatDate value="${command.end}" pattern="MM/dd/yyyy"/></i></h3>
<p><i><spring:message code="total.events.for.period"/></i>:&nbsp;${rangeTotal}</p>
<table>
<thead>
<tr>
<th><spring:message code="date"/></th>
<th><spring:message code="total.events"/></th>
<th><spring:message code="details"/></th>
</tr>
</thead>
<tbody>

<c:forEach items="${eventCounts}" var="summary">
<tr class="stats-row">
<td><fmt:formatDate value="${summary.date}" pattern="MM/dd/yyyy"/></td>
<td><c:out value="${summary.eventCount}"/></td>
<fmt:formatDate value="${summary.date}" pattern="yyyyMMdd" var="detailsLinkFormat"/>
<td><a href="<c:url value="event-details.html?date=${detailsLinkFormat}"/>"><spring:message code="details"/>&raquo;</a></td>
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