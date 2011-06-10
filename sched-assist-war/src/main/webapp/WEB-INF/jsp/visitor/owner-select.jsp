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
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title><spring:message code="application.name"/> - <spring:message code="schedule.an.appointment"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<rs:resourceURL value="/rs/famfamfam/silk/1.3/bullet_go.png" var="bullet"/>
<style type="text/css">
.toolkitlink    {
padding-left: 25px;
font-weight: bold;
font-size: 120%;
padding-right: 15px;
background: transparent url(${bullet}) no-repeat center right;
}
</style>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">

<c:choose>
<c:when test="${empty relationships}">
<div class="info">
<p><strong><spring:message code="visitor.relationships.empty"/></strong></p>
<br/>
<spring:message code="visitor.relationships.empty.more"/>:<br/>
<a class="toolkitlink" title="WiscCal Scheduling Assistant Public Profiles link" href="<c:url value="/public/advisors.html"/>"><c:url value="/public/advisors.html"/></a>
<%--
<br/>
Or visit the Undergraduate Advising Toolkit at:<br/>
<a class="toolkitlink" title="Undergraduate Advisor Toolkit link" href="http://www.learning.wisc.edu/advising/advisors.asp">http://www.learning.wisc.edu/advising/advisors.asp</a>
--%>
</div>
</c:when>
<c:otherwise>
<div id="help" class="info">
<spring:message code="visitor.relationships.help"/>:
</div>
<ul>
<c:forEach items="${relationships}" var="relationship">
<li><a href="<c:url value="/schedule/${relationship.owner.id}/view.html"/>">${relationship.owner.calendarAccount.displayName}</a> (<c:out value="${relationship.description}"/>)</li>
</c:forEach>
</ul>
</c:otherwise>
</c:choose>
<hr/>
<p><spring:message code="find.other.schedule.owners"/>&nbsp;<a href="<c:url value="/public/index.html"/>"><spring:message code="public.profile.listings"/>&raquo;</a></p>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>