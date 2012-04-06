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
<title><spring:message code="application.name.admin"/> - <spring:message code="account.details" arguments="${id}"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<rs:resourceURL var="tickIcon" value="/rs/famfamfam/silk/1.3/tick.png"/>
<rs:resourceURL var="crossIcon" value="/rs/famfamfam/silk/1.3/cross.png"/>
<rs:resourceURL var="starIcon" value="/rs/famfamfam/silk/1.3/star.png"/>
<rs:resourceURL var="emailIcon" value="/rs/famfamfam/silk/1.3/email.png"/>
<rs:resourceURL var="errorIcon" value="/rs/famfamfam/silk/1.3/exclamation.png"/>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">
<c:choose>
<c:when test="${empty calendarAccount}">
<p><spring:message code="no.results"/></p>
</c:when>
<c:otherwise>
<h3><spring:message code="account.details" arguments="${id}"/></h3>

<ul>

<c:choose>
<c:when test="${isDelegate}">
<li><img src="${starIcon}"/>&nbsp;<spring:message code="resource.account"/></li>
</c:when>
<c:otherwise>
<li><spring:message code="username"/>: <c:out value="${calendarAccount.username }"/></li>
</c:otherwise>
</c:choose>
<li><spring:message code="displayname"/>: <c:out value="${calendarAccount.displayName}"/></li>
<li><spring:message code="email.address"/>: <a href="mailto:${calendarAccount.emailAddress}" title="Compose Email"><c:out value="${calendarAccount.emailAddress}"/>&nbsp;<img src="${emailIcon}"/></a></li>
<li><spring:message code="calendaruniqueid"/>: <c:out value="${calendarAccount.calendarUniqueId}"/></li>
<li><spring:message code="calendarloginid"/>: <c:out value="${calendarAccount.calendarLoginId}"/></li>
<c:if test="${hasDistinguishedName}">
<li><spring:message code="distinguished.name"/>: ${calendarAccount.distinguishedName}</li>
</c:if>

<li><spring:message code="raw.attributes"/>:
<ul>
<c:forEach items="${calendarAccountAttributes}" var="attribute">
<li><c:out value="${attribute.key}"/>: <c:out value="${attribute.value}"/></li>
</c:forEach>
</ul>
</li>
<c:if test="${isAdvisor}">
<li><img src="${starIcon}"/>&nbsp;<spring:message code="account.is.an.academic.advisor"/></li>
</c:if>
<c:if test="${isInstructor}">
<li><img src="${starIcon}"/>&nbsp;<spring:message code="account.is.an.instructor"/></li>
</c:if>
</ul>

<c:choose>
<c:when test="${isVisitor}">
<p><img src="${tickIcon}"/>&nbsp;<spring:message code="account.is.eligible.visitor"/>:</p>
<ul>
<c:url value="relationships-for-visitor.html" var="visitorRelationshipsUrl">
<c:param name="id" value="${calendarAccount.calendarUniqueId}"/>
</c:url>
<li><a href="${visitorRelationshipsUrl }"><spring:message code="view.relationships"/>&raquo;</a></li>
</ul>
</c:when>
<c:otherwise>
<p><img src="${crossIcon}"/>&nbsp;<spring:message code="account.not.eligible.visitor"/></p>
</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${not empty owner}">
<p><img src="${tickIcon}"/>&nbsp;<spring:message code="account.is.registered.owner"/>:</p>
<ul>
<li><spring:message code="owner.id"/>: <c:out value="${owner.id}"/></li>
<li><spring:message code="preferences"/>:
<ul>
<c:forEach items="${ownerPreferences}" var="entry">
<li><c:out value="${entry.key.displayName}"/> (<c:out value="${entry.key.key}"/>): <c:out value="${entry.value}"/></li>
</c:forEach>
</ul>
</li>
<c:url value="relationships-for-owner.html" var="ownerRelationshipsUrl">
<c:param name="id" value="${owner.id}"/>
</c:url>
<li><a href="${ownerRelationshipsUrl }"><spring:message code="view.owners.adhoc.relationships"/>&raquo;</a></li>
<c:if test="${not empty publicProfile}">
<li><img src="${starIcon}"/>&nbsp;<spring:message code="account.has.public.profile"/>:
<ul>
<li><spring:message code="id"/>: <c:out value="${publicProfile.publicProfileId.profileKey}"/></li>
<li><spring:message code="description"/>: <c:out value="${publicProfile.description}"/></li>
<li><a href="/available/public/profiles/${publicProfile.publicProfileId.profileKey}.html"><spring:message code="profile.link"/>&raquo;</a></li>
</ul>
</li>
</c:if>
</ul>
</c:when>
<c:otherwise>
<p><img src="${crossIcon}"/>&nbsp;<spring:message code="account.not.registered.owner"/></p>
</c:otherwise>
</c:choose>

</c:otherwise>
</c:choose>

<a href="<c:url value="account-lookup.html"/>">&laquo;<spring:message code="return.to.account.lookup.form"/></a>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>