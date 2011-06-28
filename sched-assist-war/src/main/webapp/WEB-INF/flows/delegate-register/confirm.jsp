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
<%@ taglib prefix="available" uri="/available" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title><spring:message code="application.name"/> - <spring:message code="schedule.owner.registration"/> <spring:message code="step" arguments="3,3"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<style type="text/css">
.noteboard {
border:1px dashed #B5D4FE;
padding:1em;
}
</style>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();
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
<h3><spring:message code="schedule.owner.registration"/> <spring:message code="step" arguments="3,3"/></h3>
<p class="info">
<spring:message code="schedule.owner.registration.step3.help.resource"/>
</p>
<ul>
<li><spring:message code="meeting.location"/>:&nbsp;<i><c:out value="${registration.location}"/></i></li>
<li><spring:message code="meeting.title.prefix"/>:&nbsp;<i>${registration.titlePrefix}</i></li>
<li><spring:message code="meeting.duration"/>:&nbsp;<i>${registration.meetingLength} minutes</i></li>
<li><spring:message code="offer.double.length.meeting.option"/>:&nbsp;<i>${registration.allowDoubleLength}</i></li>
<li><spring:message code="earliest.appointment.registration.confirm" arguments="${registration.windowHoursStart}" htmlEscape="false"/></li>
<li><spring:message code="latest.appointment.registration.confirm" arguments="${registration.windowWeeksEnd}" htmlEscape="false"/></li>
<c:choose>
<c:when test="${registration.enableMeetingLimit}">
<li><spring:message code="schedule.owner.registration.confirm.visitor.limit" arguments="${registration.meetingLimitValue}" htmlEscape="false"/></li>
</c:when>
<c:otherwise>
<li><spring:message code="schedule.owner.registration.confirm.visitor.unlimited"/></li>
</c:otherwise>
</c:choose>
<c:if test="${registration.enableEmailReminders}">
<li><spring:message code="schedule.owner.registration.confirm.reminder" arguments="${registration.emailReminderHours}"/>
<c:if test="${registration.emailReminderIncludeOwner}">&nbsp;<i><spring:message code="schedule.owner.registration.confirm.reminder.includeme"/></i></c:if>.</li>
</c:if>
<c:if test="${registration.reflectSchedule}">
<li><spring:message code="schedule.owner.registration.confirm.reflect"/></li>
</c:if>
<li><spring:message code="noteboard.confirm"/>:&nbsp;<br/>
<div class="noteboard">
<c:forEach var="noteboardSentence" items="${registration.noteboardSentences}" varStatus="itemCount">
<span class="noteboardtext"><c:out value="${noteboardSentence}"/></span><br/>
</c:forEach>
</div>
</li>
<c:if test="${registration.scheduleSet }">
<li><spring:message code="schedule.days.of.week"/>:&nbsp;<i><c:out value="${registration.daysOfWeekPhrase }"/></i></li>
<li><spring:message code="schedule.start.time"/>:&nbsp;<i><c:out value="${registration.startTimePhrase }"/></i></li>
<li><spring:message code="schedule.end.time"/>:&nbsp;<i><c:out value="${registration.endTimePhrase }"/></i></li>
<li><spring:message code="schedule.start.date"/>:&nbsp;<i><c:out value="${registration.startDatePhrase }"/></i></li>
<li><spring:message code="schedule.end.date"/>:&nbsp;<i><c:out value="${registration.endDatePhrase }"/></i></li>
</c:if>
</ul>

<form:form commandName="registration">
<input type="hidden" name="execution" value=${flowExecutionKey }"/>
<input type="submit" name="_eventId_confirm" value="Confirm and Create Account for this Resource" /> 
&nbsp;or&nbsp;
<input type="submit" name="_eventId_cancel" value="<spring:message code="cancel.upper"/>" /> 
</form:form>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>