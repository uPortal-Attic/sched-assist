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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<div id="logininfo">
<p id="loginline"><spring:message code="you.are.logged.in.as"/>&nbsp;<span class="userid"><security:authentication property="principal.activeDisplayName"/></span>.&nbsp;&nbsp;&nbsp;
<security:authorize ifAllGranted="ROLE_AVAILABLE_ADMINISTRATOR">
<a href="<c:url value="/admin/index.html"/>"><spring:message code="scheduling.assistant.admin"/>&raquo;</a>&nbsp;
</security:authorize>
<security:authorize ifAnyGranted="ROLE_DELEGATE_OWNER,ROLE_DELEGATE_REGISTER">
<a href="<c:url value="/delegate_switch_exit"/>"><spring:message code="log.out.delegate"/>&raquo;</a>
</security:authorize>
<security:authorize ifAnyGranted="ROLE_OWNER,ROLE_VISITOR">
<a href="<c:url value="/logout.html"/>"><spring:message code="log.out"/>&raquo;</a>
</security:authorize>
</p>
</div>