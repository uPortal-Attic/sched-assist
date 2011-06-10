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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title>WiscCal Scheduling Assistant - Invalid Request</title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<div id="content" class="main col">
<p class="alert">
Your request is invalid.
<br/>
<br/>
<a href="<c:url value="/"/>">Return to Scheduling Assistant home</a>, or use the back button
to return from whence you came.
<br/>
<br/>
<a href="/wisccal-login">Login to WiscCal&raquo;</a>
</p>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>