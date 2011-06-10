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
<style type="text/css">
.reason {width:auto;}
</style>
<c:set var="n"><portlet:namespace/></c:set>
<rs:resourceURL var="infoIcon" value="/rs/famfamfam/silk/1.3/information.png"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/available-common.css"/>" media="all"/>
<div class="SchedulingAssistant">

<c:choose>
<c:when test="${createAppointmentFormBackingObject.multipleVisitors}">
<div id="helpmesg" class="info">
<spring:message code="join.appointment.help"/>
</div>
</c:when>
<c:otherwise>
<div id="helpmesg" class="info">
<spring:message code="create.appointment.help"/>
</div>
</c:otherwise>
</c:choose>

<fmt:formatDate value="${createAppointmentFormBackingObject.targetBlock.startTime}" type="time" pattern="EEE MMM d, h:mm a" var="startTimeFormatted"/>
<div class="formContainer">
<portlet:actionURL var="actionUrl">
<portlet:param name="_eventId" value="submit-create"/>
<portlet:param name="execution" value="${flowExecutionKey}" />
<portlet:param name="startTime" value="${param.startTime}" />
<portlet:param name="ownerId" value="${param.ownerId}"/>
</portlet:actionURL>
<form:form action="${actionUrl}" method="post" commandName="createAppointmentFormBackingObject">
<fieldset>
<c:choose>

<c:when test="${createAppointmentFormBackingObject.multipleVisitors}"> <%-- begin visitorLimit > 1 --%>
<legend><spring:message code="join.appointment.with" arguments="${targetOwner.calendarAccount.displayName }" argumentSeparator=";"/></legend>
<div class="formerror"><form:errors path="*"/></div>
<form:label path="confirmJoin"><spring:message code="join.appointment.confirm" arguments="${startTimeFormatted}" argumentSeparator=";"/>:&nbsp;</form:label>
<form:checkbox path="confirmJoin" id="confirmJoin"/>
<br/>
<br/>
<input type="submit" value="<spring:message code="join"/>" disabled="disabled"/>
</c:when> <%-- end visitorLimit > 1 --%>

<c:otherwise>
<legend><spring:message code="create.appointment.with" arguments="${targetOwner.calendarAccount.displayName }" argumentSeparator=";"/></legend>
<div class="formerror"><form:errors path="*"/></div>
<p><spring:message code="appointment.datetime"/>:&nbsp;<strong>${startTimeFormatted}</strong></p>
<label for="selectedDuration"><spring:message code="duration"/>:&nbsp;</label>
<c:choose>
<c:when test="${createAppointmentFormBackingObject.doubleLengthAvailable}">
<form:select path="selectedDuration">
<form:options items="${createAppointmentFormBackingObject.meetingDurationsAsList}" />
</form:select>
</c:when>
<c:otherwise>
<c:out value="${createAppointmentFormBackingObject.selectedDuration}"/>&nbsp;<form:hidden path="selectedDuration"/>
</c:otherwise>
</c:choose>
<br/>
<label for="reason"><spring:message code="reason.for.appointment"/>:</label><br/>
<form:textarea rows="3" cols="40" path="reason" cssClass="reason"/>
<br/>
<input type="submit" value="<spring:message code="create"/>"/><input type="reset" value="<spring:message code="reset"/>"/>
</c:otherwise>

</c:choose>
</fieldset>
</form:form>
<portlet:renderURL var="scheduleUrl" windowState="maximized">
	<portlet:param name="_eventId" value="schedule-return"/>
	<portlet:param name="execution" value="${flowExecutionKey}" />
	<portlet:param name="weekStart" value="${currentWeekStart}"/>
	<portlet:param name="ownerId" value="${targetOwner.id}"/>
</portlet:renderURL>
<a href="${scheduleUrl}">&laquo;<spring:message code="return.to.schedule"/></a>
</div> <!--  close formContainer -->
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
	if(${createAppointmentFormBackingObject.multipleVisitors}) {
		if ($.browser.msie) {
			// IE is ridiculous and doesn't fire the change event until you click elsewhere in the page AFTER changing a checkbox or radio
			// so, deal with this by doing what IE should be doing and simulate the change event on the 'click' event
			$('#confirmJoin').click(function() {
				this.blur();
			    this.focus();
			});
		}
		$('#confirmJoin').change(function() {
			if($(this).is(':checked')) {
				$(':submit').removeAttr('disabled');
			} else {
				$(':submit').attr('disabled','disabled');
			}
		});
	}
});
</script>