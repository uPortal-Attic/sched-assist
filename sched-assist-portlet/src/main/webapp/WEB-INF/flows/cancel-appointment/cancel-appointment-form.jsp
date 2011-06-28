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

<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<c:set var="n"><portlet:namespace/></c:set>
<rs:resourceURL var="infoIcon" value="/rs/famfamfam/silk/1.3/information.png"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/available-common.css"/>" media="all"/>
<style type="text/css">
.reason{width:auto;}
</style>
<c:choose>
<c:when test="${cancelAppointmentFormBackingObject.multipleVisitors }">
<spring:message code="leave" var="cancelorleave"/>
</c:when>
<c:otherwise>
<spring:message code="cancel" var="cancelorleave"/>
</c:otherwise>
</c:choose>

<div class="SchedulingAssistant">

<div class="info">
<c:choose>
<c:when test="${cancelAppointmentFormBackingObject.multipleVisitors}">
<p><spring:message code="leave.appointment.help"/></p>
</c:when>
<c:otherwise>
<p><spring:message code="cancel.appointment.help"/></p>
</c:otherwise>
</c:choose>
</div>

<fmt:formatDate value="${cancelAppointmentFormBackingObject.targetBlock.startTime}" type="time" pattern="EEE MMM d, h:mm a" var="startTimeFormatted"/>
<div class="formContainer">
<portlet:actionURL var="actionUrl">
<portlet:param name="_eventId" value="submit-cancel"/>
<portlet:param name="execution" value="${flowExecutionKey}" />
<portlet:param name="ownerId" value="${targetOwner.id}"/>
<portlet:param name="startTime" value="${targetAppointmentStartTime}"/>
<portlet:param name="endTime" value="${targetAppointmentEndTime}"/>
</portlet:actionURL>
<form:form action="${actionUrl}" method="post" commandName="cancelAppointmentFormBackingObject">
<fieldset>
<legend>
<c:choose>
<c:when test="${cancelAppointmentFormBackingObject.multipleVisitors }">
<spring:message code="leave.appointment.legend" arguments="${targetOwner.calendarAccount.displayName};${startTimeFormatted}" argumentSeparator=";"/>
</c:when>
<c:otherwise>
<spring:message code="cancel.appointment.legend" arguments="${targetOwner.calendarAccount.displayName};${startTimeFormatted}" argumentSeparator=";"/>
</c:otherwise>
</c:choose>
</legend>
<div class="formerror"><form:errors path="*"/></div>
<form:label path="confirmCancel"><spring:message code="cancelleave.confirm" arguments="${cancelorleave}"/>:&nbsp;</form:label>
<form:checkbox path="confirmCancel" id="confirmCancel"/>
<br/>
<c:if test="${cancelAppointmentFormBackingObject.targetBlock.visitorLimit == 1 }">
<label for="reason"><spring:message code="reason"/>:</label><br/>
<form:textarea rows="3" cols="40" path="reason" cssClass="reason"/>
</c:if>
<br/>
<c:choose>
<c:when test="${cancelAppointmentFormBackingObject.multipleVisitors }">
<input type="submit" value="<spring:message code="leave.appointment.this"/>" disabled="disabled"/>
</c:when>
<c:otherwise>
<input type="submit" value="<spring:message code="cancel.appointment.this"/>" disabled="disabled"/>
</c:otherwise>
</c:choose>
</fieldset>
</form:form>
</div> <!--  close formContainer -->

<portlet:renderURL var="scheduleUrl" windowState="maximized">
	<portlet:param name="_eventId" value="schedule-return"/>
	<portlet:param name="execution" value="${flowExecutionKey}" />
	<portlet:param name="weekStart" value="${currentWeekStart}"/>
	<portlet:param name="ownerId" value="${targetOwner.id}"/>
</portlet:renderURL>
<a href="${scheduleUrl}">&laquo;<spring:message code="return.to.schedule"/></a>
</div> <!-- close SchedulingAssistant -->

<rs:resourceURL var="jqueryPath" value="/rs/jquery/1.3.1/jquery-1.3.1.min.js"/>
<c:url var="lockSubmitPath" value="/js/jquery.lockSubmit.js"/>
<script type="text/javascript" src="${jqueryPath}"></script>
<script type="text/javascript" src="${lockSubmitPath}"></script>
<script type="text/javascript">
var ${n} = ${n} || {};
${n}.jQuery = jQuery.noConflict(true);

${n}.jQuery(function(){
	var $ = ${n}.jQuery;
	$(':submit').lockSubmit();
	if ($.browser.msie) {
		// IE is ridiculous and doesn't fire the change event until you click elsewhere in the page AFTER changing a checkbox or radio
		// so, deal with this by doing what IE should be doing and simulate the change event on the 'click' event
		$('#confirmCancel').click(function() {
			this.blur();
		    this.focus();
		});
	}
	$('#confirmCancel').change(function() {
		if($(this).is(':checked')) {
			$(':submit').removeAttr('disabled');
		} else {
			$(':submit').attr('disabled','disabled');
		}
	});
});
</script>