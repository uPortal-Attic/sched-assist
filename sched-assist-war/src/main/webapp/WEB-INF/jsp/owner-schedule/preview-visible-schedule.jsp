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
<%@ taglib prefix="available" uri="/available" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title><spring:message code="application.name"/> - <spring:message code="visible.schedule.for" arguments="${owner.calendarAccount.displayName}"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/visitor-common.css"/>" media="all"/>
<c:choose>
<c:when test="${highContrast}">
<link rel="stylesheet" type="text/css" href="<c:url value="/css/visitor-view-hc.css"/>" media="all"/>
</c:when>
<c:otherwise>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/visitor-view.css"/>" media="all"/>
</c:otherwise>
</c:choose>
<style type="text/css">
.clearFloats {
clear:both;
}
</style>
<rs:resourceURL var="prevWeekImg" value="/rs/famfamfam/silk/1.3/arrow_left.png"/>
<rs:resourceURL var="nextWeekImg" value="/rs/famfamfam/silk/1.3/arrow_right.png"/>
<script type="text/javascript">
jQuery(document).ready(function(){
	$('.noteboardtext').each(function(i,element){	
		existing = $(element).text();
		$(element).html(linkify(existing));
	});
});
</script>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<div id="content" class="main col">
<div id="noteboard" class="info">
<span style="font-weight: bold;"><spring:message code="a.message.from" arguments="${owner.calendarAccount.displayName}"/></span><br/>
<c:forEach var="noteboardSentence" items="${noteboardSentences}" varStatus="itemCount">
<span class="noteboardtext"><c:out value="${noteboardSentence}"/></span><br/>
</c:forEach>
</div>

<div id="pageControls">
<ul>
<c:choose>
<c:when test="${highContrast}">
<li><a class="standard" href="<c:url value="preview.html?weekStart=${weekStart}"/>"><spring:message code="standard.view"/></a></li>
</c:when>
<c:otherwise>
<li><a class="increase" href="<c:url value="preview.html?highContrast=true&weekStart=${weekStart}"/>"><spring:message code="high.contrast.view"/></a></li>
</c:otherwise>
</c:choose>
<li><a class="refresh" href="<c:url value="preview.html?highContrast=${highContrast}&weekStart=${weekStart}"/>"><spring:message code="refresh"/></a></li>
<li><a href="<c:url value="schedule.html"/>"><spring:message code="return.to.availability.schedule"/></a></li>
</ul>
</div> <!-- end pageControls -->

<div class="weeknavigation">
<ul>
<c:if test="${not empty prevWeekStart}">
<li><a class="prevWeekLink" title="<spring:message code="week.navigation.prev.title" arguments="${owner.calendarAccount.displayName}"/>" href="<c:url value="preview.html?weekStart=${prevWeekStart}&highContrast=${highContrast}"/>"><img class="killborder" src="${prevWeekImg}" alt=""/>&nbsp;<spring:message code="week.navigation.prev"/></a></li>
</c:if>
<c:if test="${not empty nextWeekStart}">
<li><a class="nextWeekLink" title="<spring:message code="week.navigation.next.title" arguments="${owner.calendarAccount.displayName}"/>" href="<c:url value="preview.html?weekStart=${nextWeekStart}&highContrast=${highContrast}"/>"><spring:message code="week.navigation.next"/>&nbsp;<img class="killborder" src="${nextWeekImg}" alt=""/></a></li>
</c:if>
</ul>
</div>
<div id="currentschedule">
<available:render visibleSchedule="${visibleSchedule}" previewMode="true"></available:render>
</div> <!--  end currentschedule div -->

<div class="clearFloats"></div>

<div class="weeknavigation">
<ul>
<c:if test="${not empty prevWeekStart}">
<li><a class="prevWeekLink" title="<spring:message code="week.navigation.prev.title" arguments="${owner.calendarAccount.displayName}"/>" href="<c:url value="preview.html?weekStart=${prevWeekStart}&highContrast=${highContrast}"/>"><img class="killborder" src="${prevWeekImg}" alt=""/>&nbsp;<spring:message code="week.navigation.prev"/></a></li>
</c:if>
<c:if test="${not empty nextWeekStart}">
<li><a class="nextWeekLink" title="<spring:message code="week.navigation.next.title" arguments="${owner.calendarAccount.displayName}"/>" href="<c:url value="preview.html?weekStart=${nextWeekStart}&highContrast=${highContrast}"/>"><spring:message code="week.navigation.next"/>&nbsp;<img class="killborder" src="${nextWeekImg}" alt=""/></a></li>
</c:if>
</ul>
</div>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>