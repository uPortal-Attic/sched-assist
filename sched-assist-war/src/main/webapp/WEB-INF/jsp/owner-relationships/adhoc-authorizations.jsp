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
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title><spring:message code="application.name"/> - <spring:message code="sharing.preferences"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>

<rs:resourceURL var="crossIcon" value="/rs/famfamfam/silk/1.3/cross.png"/>
<rs:resourceURL var="personIcon" value="/rs/famfamfam/silk/1.3/user_red.png"/>
<style type="text/css">
#sharingNav { margin-bottom:1em; }
#authorizedVisitors { margin-top:1em; }
#authorizedVisitors li { padding:2px; list-style-image:url(${personIcon});}
.removeButton {
-moz-background-clip:border;
-moz-background-inline-policy:continuous;
-moz-background-origin:padding;
background:#C3C3C3 url(${crossIcon}) no-repeat scroll 1px 50%;
padding:1px;
text-align:right;
width:6em;
}
form.inline { display:inline; margin:0px; padding:0px;}
fieldset.noborder { display:inline; border:0px; margin:0px; padding:0px; vertical-align:bottom;}
.person { vertical-align:top; }
</style>
<script type="text/javascript">
$(function() {
	$('.revokeform').each(function(index, elem) {
		$(elem).submit(function(event) {
			event.preventDefault();
			var confirmed = confirm('<spring:message code="confirm.revoke.relationship"/>');
			if(confirmed) {
				$(elem).unbind();
				$(elem).submit();
			}
		});
	});
});
</script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<div id="content" class="main col">

<div id="sharingNav">
<a href="<c:url value="schedule.html"/>">&laquo;<spring:message code="return.to.availability.schedule"/></a>&nbsp;|&nbsp;<a href="<c:url value="create-relationships-import.html"/>"><spring:message code="import.relationships.file"/></a>&nbsp;|&nbsp;<a href="<c:url value="advanced.html"/>"><spring:message code="advanced.sharing.preferences"/>&raquo;</a>
</div>

<div class="info">
<p><spring:message code="sharing.preferences.help"/></p><br/>
<a href="<c:url value="create-adhoc-relationship.html"/>"><spring:message code="create.new.relationship"/>&raquo;</a>
</div>
<div id="authorizedVisitors">
<c:choose>
<c:when test="${empty relationships}">
<span><i><spring:message code="no.relationships"/></i></span>
</c:when>
<c:otherwise>
<span><spring:message code="existing.relationships"/>:</span>
<ul id="authorizedVisitorsList">
<c:forEach items="${relationships}" var="relationship">
<li>
<span class="person"><c:out value="${relationship.visitor.calendarAccount.displayName}"/>&nbsp;(<c:out value="${relationship.description}"/>)</span>&nbsp;
<c:url value="destroy-adhoc-relationship.html" var="revokeUrl"/>
<form method="post" action="${revokeUrl}" class="inline revokeform">
<fieldset class="noborder">
<input type="hidden" name="visitorUsername" value="${relationship.visitor.calendarAccount.username}"/>
<input type="submit" class="removeButton" value="<spring:message code="revoke"/>"/>
</fieldset>
</form>
</li>
</c:forEach>
</ul>
</c:otherwise>
</c:choose>
</div>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>