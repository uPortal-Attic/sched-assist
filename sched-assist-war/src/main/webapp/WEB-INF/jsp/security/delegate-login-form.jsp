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
<title><spring:message code="application.name"/> - <spring:message code="log.in.as.resource"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>

<rs:resourceURL var="jqueryUiCssPath" value="/rs/jqueryui/1.7.2/theme/smoothness/jquery-ui-1.7.2-smoothness.css"/>
<link rel="stylesheet" type="text/css" href="${jqueryUiCssPath}" media="all"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery.autocomplete.css"/>" media="all"/>
<style type="text/css">
#lookupform { margin-top: 4px; }
</style>

<rs:resourceURL var="jqueryUiPath" value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.min.js"/>
<script type="text/javascript" src="${jqueryUiPath}"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.autocomplete.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$("#delegateName").autocomplete('<c:url value="/delegate-search.html"/>', {
		width: 320,
		scroll: true
	});
	$(':submit').lockSubmit();
});
</script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<div id="content" class="main col">
<h3><spring:message code="log.in.as.resource"/></h3>

<div class="info">
<p><spring:message code="resource.login.help"/></p>
<br/>
<p><spring:message code="resource.login.help.more" htmlEscape="false"/></p>
</div>

<div id="lookupform">
<form action="<c:url value="/delegate_switch_user"/>" method="post">
<fieldset>
<label for="delegateName"><spring:message code="resource.name"/>:</label>&nbsp;
<input type="text" id="delegateName" name="j_username"/>
<input type="submit" value="<spring:message code="log.in"/>" />
</fieldset>
</form>
</div>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>