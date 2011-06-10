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
<title><spring:message code="application.name"/> - <spring:message code="authorization.failed"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>

<rs:resourceURL var="goIcon" value="/rs/famfamfam/silk/1.3/bullet_go.png"/>
<style type="text/css">
.enhance {font-size:125%;font-weight:bold;}
.go {padding-right:18px;background:transparent url(${goIcon}) no-repeat center right;}
</style>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>

<div id="content" class="main col">

<div class="alert">
<h4><spring:message code="authorization.failed"/></h4>
<p><spring:message code="authorization.failed.help"/></p>

<security:authorize access="hasRole('ROLE_DELEGATE_REGISTER')">
<p class="enhance">
<spring:message code="has.role.delegate.register"/>:<br/>
<a class="go" href="<c:url value="/delegate-register.html"/>"><spring:message code="resource.register.begin"/></a>.
</p>
</security:authorize>
<security:authorize access="hasRole('ROLE_DELEGATE_OWNER')">
<p class="enhance">
<spring:message code="has.role.delegate.owner"/>:<br/>
<a class="go" href="<c:url value="/delegate/schedule.html"/>"><spring:message code="resource.manage.availability"/></a>
</p>
</security:authorize>

<security:authorize access="hasRole('ROLE_VISITOR')">
<security:authorize access="hasRole('ROLE_REGISTER')">
<p class="enhance">
<spring:message code="has.role.register"/>,&nbsp; 
<a href="<c:url value="/register.html"/>"><spring:message code="schedule.owner.register.begin"/></a>.
</p>
</security:authorize>

<p><spring:message code="has.role.visitor"/>:</p>
<ul>
<li><a href="<c:url value="/visitor.html"/>"><spring:message code="make.an.appointment"/></a></li>
<security:authorize access="hasRole('ROLE_OWNER')">
<li><a href="<c:url value="/owner/schedule.html"/>"><spring:message code="manage.your.availability"/></a></li>
</security:authorize>
<security:authorize access="hasRole('ROLE_DELEGATE_LOGIN')">
<li><a href="<c:url value="/delegate-login.html"/>"><spring:message code="log.in.as.resource"/></a></li>
</security:authorize>
</ul>
</security:authorize>
</div>

<a href="<c:url value="/"/>">&laquo;<spring:message code="return.to.home"/></a>, or <a href="<c:url value="/logout.html"/>"><spring:message code="log.out"/>&raquo;</a>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>