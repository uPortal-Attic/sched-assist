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
<title><spring:message code="application.name"/> - <spring:message code="advanced.sharing.preferences"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();
});
</script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<div id="content" class="main col">

<p class="info">
<spring:message code="advanced.sharing.preferences.help"/>
</p>

<div class="ownerform">
<form:form>
<fieldset>
<legend><spring:message code="advanced.sharing.preferences"/></legend>
<div class="formerror"><form:errors path="*"/></div>

<h4><spring:message code="create.public.profile"/></h4>
<label for="createPublicProfile"><spring:message code="create.public.profile.confirm"/>:&nbsp;</label>
<form:checkbox path="createPublicProfile"/>

<h4><spring:message code="public.profile.description"/></h4>
<label for="publicProfileDescription"><spring:message code="public.profile.description.help"/>:</label><br/>
<form:input path="publicProfileDescription" size="40"/>

<h4><spring:message code="public.profile.tags"/></h4>
<label for="publicProfileTags"><spring:message code="public.profile.tags.help"/>:</label><br/>
<form:input path="publicProfileTags" size="40"/>

<c:if test="${command.createPublicProfile && !empty command.publicProfileKey}">
<p>
<spring:message code="your.current.public.profile"/>:&nbsp;<a href="<c:url value="/public/profiles/${command.publicProfileKey}.html"/>"><c:url value="/public/profiles/${command.publicProfileKey}.html"/></a><br/>
</p>
</c:if>

<c:if test="${command.eligibleForAdvisor || command.eligibleForInstructor}">
<hr/>
<h4><spring:message code="automatic.relationships"/></h4>

<c:if test="${command.eligibleForAdvisor}">
<div class="info">
<p><spring:message code="academic.advisor.share.with.students.help"/></p>
</div>
<div class="profileformcomponent">
<label for="advisorShareWithStudents"><spring:message code="academic.advisor.share.with.students.confirm"/>:&nbsp;</label>
<form:checkbox path="advisorShareWithStudents"/>
</div>
</c:if>

<c:if test="${command.eligibleForInstructor}">
<div class="info">
<p><spring:message code="instructor.share.with.students.help"/></p>
</div>
<div class="profileformcomponent">
<label for="instructorShareWithStudents"><spring:message code="instructor.share.with.students.confirm"/>:&nbsp;</label>
<form:checkbox path="instructorShareWithStudents"/>
</div>
</c:if>

</c:if>

<input type="submit" value="<spring:message code="save"/>"/>
</fieldset>
</form:form>
</div> <!-- ownerform -->
<a href="<c:url value="sharing.html"/>">&laquo;<spring:message code="return.to.sharing.form"/></a>, or <a href="<c:url value="schedule.html"/>">&laquo;<spring:message code="return.to.availability.schedule"/></a>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>