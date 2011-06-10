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

<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<rs:resourceURL var="infoIcon" value="/rs/famfamfam/silk/1.3/information.png"/>
<style type="text/css">
.SchedulingAssistant	{
	margin: 2px;
}
.SchedulingAssistant .info {
	background: #f8fafc url(${infoIcon}) center no-repeat;
	background-position: 15px 50%;
	text-align: left;
	padding: 5px 20px 5px 45px;
	border: 2px solid #b5d4fe;
}
</style>
<div class="SchedulingAssistant">
<div class="info">
<p><spring:message code="appointment.doesnt.exist"/></p>
<portlet:renderURL var="scheduleUrl" windowState="maximized">
	<portlet:param name="availableAction" value="viewSchedule"/>
	<portlet:param name="ownerId" value="${param.ownerId}"/>
</portlet:renderURL>
<p><a href="${scheduleUrl}"><spring:message code="return.to.schedule"/></a></p>
</div>
</div> <!-- close SchedulingAssistant -->