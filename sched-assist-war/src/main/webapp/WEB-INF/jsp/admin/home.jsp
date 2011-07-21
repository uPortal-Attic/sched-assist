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
<title><spring:message code="application.name.admin"/> - <spring:message code="home"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">

<ul>
<li><a href="<c:url value="account-lookup.html"/>"><spring:message code="account.lookup"/></a></li>
<li><a href="<c:url value="events-summary.html"/>"><spring:message code="appointment.event.statistics"/></a></li>
<li><a href="<c:url value="relationshipSource.html"/>"><spring:message code="relationship.source.admin"/></a></li>
<li><a href="<c:url value="reflection-service.html"/>"><spring:message code="reflection.service.admin"/></a></li>
<li><a href="<c:url value="schedule-debug.html"/>"><spring:message code="visible.schedule.debug"/></a></li>
<li><a href="<c:url value="cache-manager.html"/>"><spring:message code="cache.manager"/></a></li>
</ul>
<p>Value of 'org.jasig.schedassist.runScheduledTasks' for this instance: ${runScheduledTasks}</p>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>