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
<title><spring:message code="application.name"/> - <spring:message code="build.availability.schedule"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<rs:resourceURL var="jqueryUiCssPath" value="/rs/jqueryui/1.7.2/theme/smoothness/jquery-ui-1.7.2-smoothness.min.css"/>
<link rel="stylesheet" type="text/css" href="${jqueryUiCssPath}" media="all"/>
<rs:resourceURL var="jqueryUiPath" value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.min.js"/>
<script type="text/javascript" src="${jqueryUiPath}"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(function() {
	$("#startDatePhrase").datepicker({ dateFormat: 'mm/dd/yy' });
	$("#endDatePhrase").datepicker({ dateFormat: 'mm/dd/yy' });
	$(':submit').lockSubmit();
});
</script>

</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<div id="content" class="main col">

<div class="ownerform">
<form:form>
<fieldset>
<legend><spring:message code="build.availability.schedule"/></legend>
<p class="info"><spring:message code="build.availability.schedule.help"/></p>
<div class="formerror"><form:errors path="*"/></div>
<label for="startTimePhrase"><spring:message code="builder.start"/>:</label>&nbsp;<form:input path="startTimePhrase"/>
<br/>
<br/>
<label for="endTimePhrase"><spring:message code="builder.end"/>:</label>&nbsp;<form:input path="endTimePhrase"/>
<br/>
<br/>
<label for="daysOfWeekPhrase"><spring:message code="builder.daysofweek"/>:</label>&nbsp;<form:input path="daysOfWeekPhrase"/>
<br/>
<br/>
<label for="startDatePhrase"><spring:message code="from.start.date"/>:</label>&nbsp;<form:input path="startDatePhrase"/>
<br/>
<br/>
<label for="endDatePhrase"><spring:message code="until.end.date"/>:</label>&nbsp;<form:input path="endDatePhrase"/>
<br/>
<br/>
<label for="visitorsPerAppointment"><spring:message code="visitor.limit"/>:</label>&nbsp;<form:input path="visitorsPerAppointment" cssStyle="width: 2em;"/>
<br/>
<br/>
<label for="meetingLocation"><spring:message code="meeting.location"/>:</label>&nbsp;<form:input path="meetingLocation"/>
<br/>
<br/>
<input type="submit" value="<spring:message code="create"/>"/>
</fieldset>
</form:form>
</div><!-- ownerform -->
<a href="<c:url value="schedule.html"/>">&laquo;<spring:message code="return.to.availability.schedule"/></a>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>