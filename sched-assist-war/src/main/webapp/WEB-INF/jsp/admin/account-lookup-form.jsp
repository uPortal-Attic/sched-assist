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
<title><spring:message code="application.name.admin"/> - <spring:message code="account.lookup"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<rs:resourceURL var="jqueryUiCssPath" value="/rs/jqueryui/1.7.2/theme/smoothness/jquery-ui-1.7.2-smoothness.min.css"/>
<link rel="stylesheet" type="text/css" href="${jqueryUriCssPath}" media="all"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery.autocomplete.css"/>" media="all"/>

<c:url var="peopleSearch" value="account-lookup.html">
<c:param name="type" value="people"/>
</c:url>
<c:url var="resourceSearch" value="account-lookup.html">
<c:param name="type" value="resources"/>
</c:url>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.autocomplete.min.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();

	$("#username").autocomplete('${peopleSearch}', {
		width: 320,
		scroll: true,
		scrollHeight: 240,
		selectFirst: false,
		matchContains: true
	});
	$("#resourceName").autocomplete('${resourceSearch}', {
		width: 320,
		scroll: true,
		scrollHeight: 240,
		selectFirst: false,
		matchContains: true
	});
});
</script>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">
<div class="ownerform">
<form:form>
<fieldset>
<legend><spring:message code="account.lookup"/></legend>
<p class="info">
<spring:message code="account.lookup.help"/>
</p>
<div class="formerror"><form:errors path="*"/></div>
<label for="username"><spring:message code="username"/>:</label>&nbsp;
<form:input path="username"/>
<br/>
<br/>
<label for="resourceName"><spring:message code="resource.name"/>:</label>&nbsp;
<form:input path="resourceName"/>
<br/>
<br/>
<label for="ctcalxitemid"><spring:message code="calendaruniqueid"/>:</label>&nbsp;
<form:input path="ctcalxitemid"/>
<br/><br/>
<label for="searchText"><spring:message code="non.interactive.search"/>:</label>
<form:input path="searchText"/>
<br/><br/>
<input type="submit" value="Search"/>
</fieldset>
</form:form>
</div>
<a href="<c:url value="/admin/index.html"/>">&laquo;<spring:message code="return.to.admin.home"/></a>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>