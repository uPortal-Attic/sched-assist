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
<title><spring:message code="application.name"/> - <spring:message code="advanced.sharing.preferences"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<div id="content" class="main col">

<div id="status" class="success">
<p><spring:message code="advanced.sharing.preferences.updated"/>:</p>
<ul>
<c:if test="${advisorShareWithStudentsOn}">
<li><spring:message code="advisor.share.with.students.enabled"/></li>
</c:if>
<c:if test="${advisorShareWithStudentsOff}">
<li><spring:message code="advisor.share.with.students.disabled"/></li>
</c:if>
<c:if test="${createdPublicProfile}">
<li><spring:message code="advanced.sharing.preferences.profile.created"/>:&nbsp;<a href="<c:url value="/public/profiles/${publicProfileKey}.html"/>"><c:url value="/public/profiles/${publicProfileKey}.html"/></a>.
<spring:message code="advanced.sharing.preferences.profile.search.disclaimer"/></li>
</c:if>
<c:if test="${removedPublicProfile}">
<li><spring:message code="advanced.sharing.preferences.profile.removed"/></li>
</c:if>
<c:if test="${updatedPublicProfile}">
<li><spring:message code="advanced.sharing.preferences.profile.updated"/></li>
</c:if>
<c:if test="${updatedPublicProfileTags}">
<li><spring:message code="advanced.sharing.preferences.profile.tags.updated"/></li>
</c:if>
</ul>
<a href="<c:url value="advanced.html"/>">&laquo;<spring:message code="return.to.advanced.sharing.preferences"/></a>, or <a href="<c:url value="schedule.html"/>">&laquo;<spring:message code="return.to.availability.schedule"/></a>
</div>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>