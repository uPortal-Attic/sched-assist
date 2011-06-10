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
<title><spring:message code="application.name"/> - <spring:message code="create.new.relationship"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<div id="content" class="main col">
<div class="ownerform">
<form:form>
<fieldset>
<legend><spring:message code="create.new.relationship"/></legend>
<div id="sharingchangestatus" class="info">
<p><spring:message code="create.new.relationship.help.noscript"/><br/>
<a href="create-adhoc-relationship.html"><spring:message code="return.to.dynamic.version"/>&raquo;</a>
</p>
</div>

<div class="formerror"><form:errors path="*"/></div>

<label for="visitorUsername"><spring:message code="username"/>:&nbsp;</label>
<form:input path="visitorUsername"/>
<br/>
<label for="relationship"><spring:message code="create.new.relationship.description.field"/>:</label><br/>
<form:input path="relationship"/>
<br/><br/>
<input id="submit" type="submit" value="<spring:message code="authorize"/>"/>
</fieldset>
</form:form>
</div> <!-- ownerform -->
<a href="<c:url value="sharing.html"/>">&laquo;<spring:message code="return.to.sharing.form"/></a>, or <a href="<c:url value="schedule.html"/>">&laquo;<spring:message code="return.to.availability.schedule"/></a>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>