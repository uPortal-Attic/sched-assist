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
<title><spring:message code="application.name"/> - <spring:message code="search.public.listings"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<div id="content" class="main col">
<div class="info">
<p><spring:message code="search.public.listings.help"/></p>
</div>

<div id="searchbox">
<h3><spring:message code="search.public.listings.title"/></h3>
<form action="http://www.google.com/search" method="get">
<fieldset>
<a href="http://www.google.com/">
<img class="glogo" alt="Google" src="<c:url value="/img/google-Logo_40wht.gif"/>"/>
</a>
<input type="text" value="" maxlength="255" size="25" name="q"/>
<input type="submit" value="Google Search" name="btnG"/>
<input type="hidden" value="strict" name="safe"/>
<input type="hidden" value="${sitesearchValue}" name="sitesearch"/>
<input type="hidden" value="UTF-8" name="ie"/>
<input type="hidden" value="UTF-8" name="oe"/>
</fieldset>
</form>
</div>
<hr/>
<a href="<c:url value="/public/browse.html"/>"><spring:message code="browse.public.listings"/>&raquo;</a>&nbsp;
<a href="<c:url value="/public/advisors.html"/>"><spring:message code="browse.advisor.listings"/>&raquo;</a>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>