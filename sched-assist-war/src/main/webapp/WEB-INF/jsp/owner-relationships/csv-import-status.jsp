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
<title><spring:message code="application.name"/> - <spring:message code="import.relationships.file.status"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<div id="content" class="main col">

<c:choose>
<c:when test="${submitted}">
<div id="status" class="success">
<p><spring:message code="import.relationships.file.accepted"/>&nbsp;<a href="<c:url value="create-relationships-import.html"/>"><spring:message code="import.relationships.file.refresh"/></a></p>
<a href="<c:url value="schedule.html"/>">&laquo;<spring:message code="return.to.availability.schedule"/></a>
</div>
</c:when>
<c:when test="${processing}">
<div class="inprogress">
<p><spring:message code="import.relationships.file.processing"/>&nbsp;<a href="<c:url value="create-relationships-import.html"/>"><spring:message code="import.relationships.file.refresh"/></a></p>
</div>
</c:when>
<c:otherwise>
<div class="success">
<p><spring:message code="import.relationships.file.complete"/></p>
<c:choose>
<c:when test="${importResult.failureCount == 0}">
<p><spring:message code="import.relationships.file.complete.success" arguments="${importResult.successes}"/></p>
</c:when>
<c:otherwise>
<p><spring:message code="import.relationships.file.complete.problems" arguments="${importResult.successes}"/>:</p>
<ul>
<c:forEach items="${importResult.failuresEntrySet}" var="failure">
<li><spring:message code="line"/>&nbsp;<c:out value="${failure.key}"/>: <c:out value="${failure.value }"/></li>
</c:forEach>
</ul>
<p><a href="<c:url value="create-relationships-import.html?dismiss=true"/>"><spring:message code="dismiss.message"/></a></p>
</c:otherwise>
</c:choose>
</div>
</c:otherwise>

</c:choose> <%-- parent c:choose --%>
<br/>
<a href="<c:url value="schedule.html"/>">&laquo;<spring:message code="return.to.availability.schedule"/></a>&nbsp;|&nbsp;<a href="<c:url value="create-relationships-import.html"/>"><spring:message code="import.relationships.file"/></a>&nbsp;|&nbsp;<a href="<c:url value="advanced.html"/>"><spring:message code="advanced.sharing.preferences"/>&raquo;</a>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>