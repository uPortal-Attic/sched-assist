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
<title><spring:message code="application.name"/> - <spring:message code="public.profiles.advisors" arguments="${titleSuffix}"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<style type="text/css">
#listnav li{
display:inline;
padding-right:5px;
}
</style>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<div id="content" class="main col">

<c:choose>
<c:when test="${empty profileIds}">
<div class="info">
<spring:message code="public.profiles.advisors.none"/><br/>
If you are a UW Student looking for an Undergraduate Advisor, be sure to check the Undergraduate Advisor Toolkit at:<br/>
<a class="toolkitlink" title="Undergraduate Advisor Toolkit link" href="http://www.learning.wisc.edu/advising/advisors.asp">http://www.learning.wisc.edu/advising/advisors.asp</a>
</div>
</c:when>
<c:otherwise>

<hr/>
<div id="help" class="info">
<spring:message code="public.profiles.advisors.help"/>:
</div>
<div id="publiclist">
<ul>
<c:forEach items="${profileIds}" var="profileId">
<li>
<div class="publicperson">
<a href="<c:url value="/public/profiles/${profileId.profileKey }.html"/>"><c:out value="${profileId.ownerDisplayName}"/></a><br/>
</div>
</li>
</c:forEach>
</ul>
</div>

<div id="listnav">
<ul>
<c:if test="${showPrev}">
<c:url value="/public/advisors.html" var="prevLink">
<c:param name="startIndex" value="${showPrevIndex }"/>
</c:url>
<li><a href="${prevLink }">&laquo;<spring:message code="previous"/></a></li>
</c:if>

<li><a href="<c:url value="/public/index.html"/>"><spring:message code="return.to.search.form"/></a></li>

<c:if test="${showNext}">
<c:url value="/public/advisors.html" var="nextLink">
<c:param name="startIndex" value="${showNextIndex }"/>
</c:url>
<li><a href="${nextLink }"><spring:message code="next"/>&raquo;</a></li>
</c:if>
</ul>
</div>
</c:otherwise>
</c:choose>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>