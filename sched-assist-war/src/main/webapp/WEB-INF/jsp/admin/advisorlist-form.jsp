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
<title><spring:message code="application.name.admin"/> - <spring:message code="relationship.source.admin"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();
});
</script>
<style type="text/css">
#reloadForm {
margin-top: 2px;
}
</style>

</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">
<c:choose>
<c:when test="${empty lastReloadTimestamp}">
<div class="alert">
<spring:message code="relationship.source.reload.not.yet.run"/>
</div>
</c:when>
<c:otherwise>
<div class="info">
<spring:message code="relationship.source.reload.last.executed"/>: <fmt:formatDate value="${lastReloadTimestamp}" pattern="MM/dd/yyyy HH:mm:ss"/>
</div>
</c:otherwise>
</c:choose>

<div id="reloadForm">
<c:url var="triggerReloadUrl" value="relationshipSource.html">
<c:param name="action" value="reload"></c:param>
</c:url>
<form:form action="${triggerReloadUrl }" method="post"> 
<spring:message code="relationship.source.reload.question"/> <input type="submit" value="Reload"/>
</form:form>
</div>

<a href="<c:url value="/admin/index.html"/>">&laquo;<spring:message code="return.to.admin.home"/></a>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>