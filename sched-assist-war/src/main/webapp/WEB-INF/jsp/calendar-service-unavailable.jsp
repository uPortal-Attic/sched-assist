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
<title>WiscCal Scheduling Assistant - Calendar Service Unavailable</title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
</head>
<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<div id="content" class="main col">
<div class="alert">
<p><i>WiscCal's Oracle Calendar server is temporarily unavailable; please try again later.</i></p>

<p>If you see this message during a Saturday morning, the WiscCal team has taken the Oracle Calendar Server offline for
required regular maintenance.</p>

<p>For all other times, please see the <a target="_new_hd" href="http://helpdesk.wisc.edu">DoIT Help Desk</a> for the latest news.</p>
<p>We apologize for any inconvenience.</p>
</div> 
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>   
</body>
</html>