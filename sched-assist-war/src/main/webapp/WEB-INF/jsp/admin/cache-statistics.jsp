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
<title><spring:message code="application.name.admin"/> - <spring:message code="cache.statistics"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">

<c:choose>

<c:when test="${empty cacheNames}">
<p><spring:message code="no.caches.found"/></p>
</c:when>
<c:otherwise>
<h3><spring:message code="cache.statistics"/></h3>

<table>
<thead>
<tr>
<th><spring:message code="cache.name"/></th>
<th><spring:message code="cache.name"/></th>
<th><spring:message code="hits"/></th>
<th><spring:message code="misses"/></th>
<th><spring:message code="clear.question"/></th>
</tr>
</thead>
<tbody>

<c:forEach items="${cacheNames}" var="cacheName">
<c:set var="statistics" value="${statisticsMap[cacheName]}"></c:set>
<tr class="stats-row">
<td>${cacheName}</td>
<td>${statistics.objectCount}</td>
<td>${statistics.cacheHits}</td>
<td>${statistics.cacheMisses}</td>
<c:url value="cache-manager.html" var="clearUrl">
<c:param name="action" value="clear"/>
<c:param name="cacheName" value="${cacheName }"/>
</c:url>
<td><form action="${clearUrl }" method="post"><input type="submit" value="Clear"/></form></td>
</tr>
</c:forEach>
</tbody>
</table>
</c:otherwise>
</c:choose>

<a href="<c:url value="/admin/index.html"/>">&laquo;<spring:message code="return.to.admin.home"/></a>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>