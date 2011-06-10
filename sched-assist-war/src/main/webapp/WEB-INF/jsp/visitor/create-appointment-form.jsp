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
<c:choose>
<c:when test="${command.multipleVisitors}">
<title><spring:message code="application.name"/> - <spring:message code="join.appointment.with" arguments="${owner.calendarAccount.displayName }" argumentSeparator=";"/></title>
</c:when>
<c:otherwise>
<title><spring:message code="application.name"/> - <spring:message code="create.appointment.with" arguments="${owner.calendarAccount.displayName }" argumentSeparator=";"/></title>
</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>

<style type="text/css">
#content: {
margin-top: 5px;
margin-left: 5px;
}
#helpmesg	{
margin-bottom: 10px;
}
#formContainer {
width: 475px;
}
form div {
clear: both;
margin-bottom: 18px;
overflow: hidden;
}
form fieldset {
border: #E5E5E5 2px solid;
clear: both;
margin-bottom: 9px;
overflow: hidden;
padding: 9px 18px;
}
form legend {
background: #E5E5E5;
color: #262626;
margin-bottom: 9px;
padding: 2px 11px;
}
</style>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();
	if(${command.multipleVisitors}) {
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
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">

<c:choose>
<c:when test="${command.multipleVisitors}">
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

<fmt:formatDate value="${command.targetBlock.startTime}" type="time" pattern="EEE MMM d, h:mm a" var="startTimeFormatted"/>
<div id="formContainer">
<form:form>
<fieldset>
<c:choose>

<c:when test="${command.multipleVisitors}"> <%-- begin visitorLimit > 1 --%>
<legend><spring:message code="join.appointment.with" arguments="${owner.calendarAccount.displayName }" argumentSeparator=";"/></legend>
<div class="formerror"><form:errors path="*"/></div>
<form:label path="confirmJoin" >
<spring:message code="join.appointment.confirm" arguments="${startTimeFormatted}"/>:&nbsp;</form:label>
<form:checkbox path="confirmJoin" id="confirmJoin"/>
<br/>
<br/>
<input type="submit" value="Join" disabled="disabled" />
</c:when> <%-- end visitorLimit > 1 --%>

<c:otherwise> <%-- begin otherwise (visitorLimit == 1) --%>
<legend><spring:message code="create.appointment.with" arguments="${owner.calendarAccount.displayName }" argumentSeparator=";"/></legend>
<div class="formerror"><form:errors path="*"/></div>
<p><spring:message code="appointment.datetime"/>:&nbsp;<strong>${startTimeFormatted }</strong></p>
<form:label path="selectedDuration"><spring:message code="duration"/>:&nbsp;</form:label>
<c:choose>
<c:when test="${command.doubleLengthAvailable}">
<form:select path="selectedDuration">
<form:options items="${command.meetingDurationsAsList}" />
</form:select>
</c:when>
<c:otherwise>
<c:out value="${command.selectedDuration}"/>&nbsp;<form:hidden path="selectedDuration"/>
</c:otherwise>
</c:choose>
<br/>
<form:label path="reason"><spring:message code="reason.for.appointment"/>:</form:label><br/>
<form:textarea rows="3" cols="40" path="reason"/>
<br/>
<input type="submit" value="<spring:message code="create"/>"/><input type="reset" value="<spring:message code="reset"/>"/>
</c:otherwise> <%-- end otherwise (visitorLimit == 1) --%>

</c:choose>
</fieldset>
</form:form>
<a href="view.html">&laquo;<spring:message code="return.to.schedule"/></a>
</div> <!--  end formContainer -->

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>