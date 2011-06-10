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
<title><spring:message code="application.name.admin"/> - <spring:message code="ad.hoc.relationships.for.owner" arguments="${id}"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<rs:resourceURL var="crossIcon" value="/rs/famfamfam/silk/1.3/cross.png"/>
<style type="text/css">
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
<div id="content" class="main col">

<c:choose>
<c:when test="${empty owner}">
<p><spring:message code="schedule.owner.not.found"/></p>
</c:when>
<c:when test="${empty relationships}">
<p><spring:message code="schedule.owner.no.ad.hoc.relationships"/></p>
</c:when>
<c:otherwise>
<h3><spring:message code="ad.hoc.relationships.for.owner" arguments="${owner.calendarAccount.displayName}"/></h3>
<table>
<thead>
<tr>
<th><spring:message code="schedule.visitor"/> <spring:message code="displayname"/></th>
<th><spring:message code="schedule.visitor"/> <spring:message code="username"/></th>
<th><spring:message code="relationship.description"/></th>
<th><spring:message code="revoke.question"/></th>
</tr>
</thead>
<tbody>
<c:forEach items="${relationships}" var="relationship">
<tr class="account-row">
<td><c:out value="${relationship.visitor.calendarAccount.displayName}"/></td>
<td><c:out value="${relationship.visitor.calendarAccount.username}"/></td>
<td><c:out value="${relationship.description}"/></td>
<td>
<form method="post" class="inline revokeform">
<fieldset class="noborder">
<input type="hidden" name="visitorUsername" value="${relationship.visitor.calendarAccount.username}"/>
<input type="hidden" name="ownerId" value="${relationship.owner.id}"/>
<input type="submit" class="removeButton" value="<spring:message code="revoke"/>"/>
</fieldset>
</form>
</td>
</tr>
</c:forEach>
</tbody>
</table>

</c:otherwise>
</c:choose>

<a href="<c:url value="account-lookup.html"/>">&laquo;<spring:message code="return.to.account.lookup.form"/></a>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>