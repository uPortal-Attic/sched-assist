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
<title><spring:message code="application.name"/> - <spring:message code="log.in"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<style type="text/css">
#loginForm{ margin-top: 1em; padding: 1em;}
</style>
</head>

<body>

<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>

<div id="content" class="main col">
<c:choose>
<c:when test="${param.login_error == 1}">
<div class="alert">
<spring:message code="authentication.failed.login"/>
</div>
</c:when>
<c:otherwise>
<div class="info">
<spring:message code="authentication.message"/>
</div>
</c:otherwise>
</c:choose>

<c:url value="/security_check" var="login"/>
<div id="loginForm">
<form action="${login}" method="post">
<fieldset>
<label for="j_username"><spring:message code="username"/>:&nbsp;</label><input name="j_username" type="text"/><br/>
<label for="j_username"><spring:message code="password"/>:&nbsp;</label><input name="j_password" type="password"/><br/>
<input type="submit" name="submit" value="Login"/>
</fieldset>
</form>
</div>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>