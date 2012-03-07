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
<title><spring:message code="application.name.admin"/> - <spring:message code="search.results"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">

<c:choose>

<c:when test="${empty results}">
<p><spring:message code="no.results.for" arguments="${command}"/></p>
</c:when>
<c:otherwise>
<h3><spring:message code="search.results"/></h3>
<p><spring:message code="query"/>: <i><c:out value="${command}"/></i></p>
<table>
<thead>
<tr>
<th><spring:message code="displayname"/></th>
<th><spring:message code="username"/></th>
<th><spring:message code="email.address"/></th>
<th><spring:message code="calendaruniqueid"/></th>
<th><spring:message code="details"/></th>
</tr>
</thead>
<tbody>

<c:forEach items="${results}" var="account">
<tr class="account-row">
<td><c:out value="${account.displayName}"/></td>
<td><c:out value="${account.username}"/></td>
<td><c:out value="${account.emailAddress}"/></td>
<td><c:out value="${account.calendarUniqueId}"/></td>
<c:choose>
<c:when test="${account.delegate}">
<c:url var="detailsLink" value="account-details.html">
<c:param name="id" value="${account.calendarUniqueId}"/>
<c:param name="resource"/>
</c:url>
</c:when>
<c:otherwise>
<c:url var="detailsLink" value="account-details.html">
<c:param name="id" value="${account.calendarUniqueId}"/>
</c:url>
</c:otherwise>
</c:choose>
<td><a href="${detailsLink}"><spring:message code="details"/>&raquo;</a></td>
</tr>
</c:forEach>
</tbody>
</table>
</c:otherwise>
</c:choose>
<a href="<c:url value="account-lookup.html"/>">&laquo;<spring:message code="return.to.account.lookup.form"/></a>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>