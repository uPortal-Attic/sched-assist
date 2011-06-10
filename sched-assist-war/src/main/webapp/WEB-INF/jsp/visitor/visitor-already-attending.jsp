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
<rs:resourceURL var="cancelIcon" value="/rs/famfamfam/silk/1.3/calendar_delete.png"/>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title><spring:message code="application.name"/> -  <spring:message code="visible.schedule.for" arguments="${owner.calendarAccount.displayName}"/> (<spring:message code="meeting.limit.exceeded"/>)</title>
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
#limitExceededDescription { width:50%;margin-bottom:1em; }
.weekday {margin-bottom:1em;}
.clearFloats {clear:both;}
</style>
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
<div id="content" class="main col">

<div id="pageControls">
<ul>
<li><a href="<c:url value="/visitor.html"/>">&laquo;<spring:message code="make.appointment.with.someone.else"/></a></li>
<c:choose>
<c:when test="${highContrast}">
<li><a class="standard" href="<c:url value="view.html"/>"><spring:message code="standard.view"/></a></li>
<li><a class="refresh" href="<c:url value="view.html?highContrast=true"/>"><spring:message code="refresh"/></a></li>
</c:when>
<c:otherwise>
<li><a class="increase" href="<c:url value="view.html?highContrast=true"/>"><spring:message code="high.contrast.view"/></a></li>
<li><a class="refresh" href="<c:url value="view.html"/>"><spring:message code="refresh"/></a></li>
</c:otherwise>
</c:choose>
<li><a target="new" class="loginlink" title="<spring:message code="calendar.log.in.title"/>" href="/wisccal-login"><spring:message code="calendar.log.in"/>&raquo;</a></li>
</ul>
</div> <!-- end pageControls -->

<div id="noteboard" class="info">
<span style="font-weight: bold;"><spring:message code="a.message.from" arguments="${owner.calendarAccount.displayName}" argumentSeparator=";"/></span><br/>
<c:forEach var="noteboardSentence" items="${noteboardSentences}" varStatus="itemCount">
<span class="noteboardtext"><c:out value="${noteboardSentence}"/></span><br/>
</c:forEach>
</div>

<div id="currentschedule">
<div class="info" id="limitExceededDescription">
<spring:message code="limit.exceeded.description"/>
</div>
<div class="weekcontainer">
<div class="weekday">
<ul class="scheduleblocks">
<c:forEach items="${attendingList}" var="attending">
<li class="dayhead"><fmt:formatDate value="${attending.startTime}" pattern="EEE M/d" type="date"/></li>
<fmt:formatDate value="${attending.startTime}" pattern="yyyyMMdd-HHmm" var="startTime" type="time"/>
<fmt:formatDate value="${attending.endTime}" pattern="yyyyMMdd-HHmm" var="endTime" type="time"/>
<c:url var="cancelUrl" value="cancel.html">
<c:param name="startTime" value="${startTime}"></c:param>
<c:param name="endTime" value="${endTime}"></c:param>
</c:url>
<li title="<spring:message code="cancel.my.appointment"/>" class="attending">
<a href="<c:url value="${cancelUrl}"/>">
<img alt="" src="${cancelIcon}"/>&nbsp;
<span><fmt:formatDate value="${attending.startTime}" pattern="hh:mm a" type="time"/>&nbsp;-&nbsp;<fmt:formatDate value="${attending.endTime}" pattern="hh:mm a" type="time"/></span>
</a>
</li>
</c:forEach>
</ul>
</div> <!-- end weekday -->
</div> <!-- end weekcontainer -->
</div> <!-- end currentschedule div -->
<div class="clearFloats"></div>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>