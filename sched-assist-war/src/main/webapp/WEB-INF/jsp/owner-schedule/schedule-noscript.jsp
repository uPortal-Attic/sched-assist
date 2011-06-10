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
<title><spring:message code="application.name"/> - <spring:message code="your.availability.schedule"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>

<rs:resourceURL var="crossIcon" value="/rs/famfamfam/silk/1.3/cross.png"/>
<rs:resourceURL var="errorIcon" value="/rs/famfamfam/silk/1.3/error.png"/>
<style type="text/css">
#plainschedulecaption {
margin-left: auto;
margin-right: auto;
font-weight: bold;
text-align: center;
}
#plainschedule img	{
border: none;
}
.warnlink {
padding-right: 18px;
background: transparent url(${errorIcon}) no-repeat center right;
}
#clearweek {
padding-right: 18px;
background: transparent url(${crossIcon}) no-repeat center right;
}
</style>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<div id="content" class="main col">
<div id="help" class="info">
<spring:message code="availability.schedule.help.noscript"/><br/>
<spring:message code="howto.preferences"/>&nbsp;<a href="<c:url value="preferences.html"/>"><spring:message code="preferences"/> &raquo;</a>.<br/>
<spring:message code="howto.availability"/>&nbsp;<a href="<c:url value="builder.html"/>"><spring:message code="build.availability.schedule"/>&raquo;</a>.<br/>
<spring:message code="howto.sharing"/>&nbsp;<a href="<c:url value="sharing.html"/>"><spring:message code="sharing.preferences"/>&raquo;</a>.<br/>
<spring:message code="howto.visitorhistory"/>&nbsp;<a href="<c:url value="visitor-history.html"/>"><spring:message code="visitor.history"/>&raquo;</a>.
<br/>
<a href="<c:url value="schedule.html"/>"><spring:message code="return.to.dynamic.version"/>&raquo;</a>
</div>

<div id="plainschedulecaption">
<c:url value="schedule-noscript.html" var="prevWeek">
<c:param name="startDate"><fmt:formatDate value="${model.prevWeekStart}" type="date" pattern="yyyyMMdd"/></c:param>
</c:url>
<c:url value="schedule-noscript.html" var="nextWeek">
<c:param name="startDate"><fmt:formatDate value="${model.nextWeekStart}" type="date" pattern="yyyyMMdd"/></c:param>
</c:url>
<a href="${prevWeek}" class="nav" title="<spring:message code="show.previous.week"/>" id="previousHandle">&laquo;</a>
<fmt:formatDate value="${model.weekStart}" type="date" pattern="MMM dd" var="formattedWeekStart"/>
<fmt:formatDate value="${model.weekEnd}" type="date" pattern="MMM dd" var="formattedWeekEnd"/>
<span id="captionText"><spring:message code="week.of" arguments="${formattedWeekStart},${formattedWeekEnd }"/></span>
<a href="${nextWeek}" class="nav" title="<spring:message code="show.next.week"/>" id="nextHandle">&raquo;</a>
</div>
<div id="plainschedule">
<c:choose>
<c:when test="${empty model.scheduleBlocks}">
<p>
<i><spring:message code="availability.schedule.empty"/></i>
</p>
</c:when>
<c:otherwise>
<ul>
<c:forEach items="${model.scheduleBlocks}" var="block">
<li>
<c:url value="remove-block-alternate.html" var="removeBlockUrl">
<c:param name="startTime">
	<fmt:formatDate value="${block.startTime}" type="date" pattern="yyyyMMdd-HHmm"/>
</c:param>
<c:param name="endTime">
	<fmt:formatDate value="${block.endTime}" type="date" pattern="yyyyMMdd-HHmm"/>
</c:param>
</c:url>
<fmt:formatDate value="${block.startTime}" type="time" pattern="EEE MMM dd, h:mm a" var="formattedBlockStartTime"/>
<fmt:formatDate value="${block.endTime}" type="time" pattern="h:mm a" var="formattedBlockEndTime"/>
${ formattedBlockStartTime } to ${ formattedBlockEndTime }&nbsp;
<a href="${removeBlockUrl}" title="<spring:message code="remove.availability.block.for" arguments="${formattedBlockStartTime},${formattedBlockEndTime}"/>">
<img src="${crossIcon}" alt="<spring:message code="remove.availability.block.for" arguments="${formattedBlockStartTime},${formattedBlockEndTime}"/>"/>
</a>
</li>
</c:forEach>
</ul>
<fmt:formatDate value="${model.weekStart}" type="date" pattern="yyyyMMdd" var="weekUrlParam"/>
<c:url var="clearWeekUrl" value="clear-week.html">
<c:param name="weekOf" value="${weekUrlParam}"></c:param>
</c:url>
<p>
<a id="clearweek" href="${clearWeekUrl}"><spring:message code="clear.week.availability.schedule.link" arguments="${formattedWeekStart},${formattedWeekEnd }"/></a>
</p>
</c:otherwise>
</c:choose>
</div>
<p>
<a class="warnlink" href="<c:url value="clear-entire-schedule.html"/>"><spring:message code="clear.entire.availability.schedule"/></a>
&nbsp;|&nbsp;
<a class="warnlink" href="<c:url value="removeAccount.html"/>"><spring:message code="remove.my.account"/></a>
</p>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>