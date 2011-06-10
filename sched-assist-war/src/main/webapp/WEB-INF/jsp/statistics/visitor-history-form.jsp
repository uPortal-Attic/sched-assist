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
<title><spring:message code="application.name"/> - <spring:message code="visitor.history"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<rs:resourceURL var="jqueryUiCssPath" value="/rs/jqueryui/1.7.2/theme/smoothness/jquery-ui-1.7.2-smoothness.min.css"/>
<link rel="stylesheet" type="text/css" href="${jqueryUiCssPath}" media="all"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery.autocomplete.css"/>" media="all"/>
<rs:resourceURL var="starIcon" value="/rs/famfamfam/silk/1.3/star.png"/>
<style type="text/css">
.removeLink img {
border-style: none;
}
.selectedName   {
font-style: italic;
font-weight: bold;
}
.searchHints {
font-weight: bold;
}
.notyet {
color: #3262DC;
font-style: italic;
}
.matchfound {
color: #55a818;
}
#selectedUser {
background: #fff url(${starIcon}) center no-repeat;
background-position: 15px 50%;
text-align: left;
padding: 5px 20px 5px 45px;
}
#userSearch {
width: 320px;
}
.noscriptlink {
float:right;
padding-right:2em;
}
</style>
<rs:resourceURL var="jqueryUiPath" value="/rs/jqueryui/1.7.2/jquery-ui-1.7.2.min.js"/>
<script type="text/javascript" src="${jqueryUiPath}"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.autocomplete.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function() {
	$('#loginline').append('<a class="noscriptlink" href="visitor-history.html?noscript=true"><spring:message code="trouble.viewing.this.page"/></a>');
	
	$("#startTime").datepicker({ dateFormat: 'mm/dd/yy' });
	$("#endTime").datepicker({ dateFormat: 'mm/dd/yy' });
	$(':submit').lockSubmit();

	$('#selectedUser').hide();
    if($('#visitorUsername').val() != '' && $('#userSearchText').val() != '') {
        highlightSelectedUser($('#visitorUsername').val(), $('#userSearchText').val());
    }
    $('#userSearchText').change(function() {
        if($('#userSearchText').val() != $('#acceptedFullName').text()) {
            $('#submit').attr('disabled', 'disabled');
            $('#selectedUser').hide();
            $('#acceptedFullName').text('');
            $('#acceptedUsername').text('');
        }
    });
    $("#userSearchText").autocomplete('<c:url value="visitor-search.html"/>', {
        width: 320,
        scroll: true,
        scrollHeight: 240,
        selectFirst: false,
        matchContains: true,
        formatItem: function(data, i, n, value) {
            formattedString = data[0] + ' (' + data[1] + ')';
            return formattedString;
        },
        formatResult: function(data, value) {
            return data[0];
        }
    });
    $("#userSearchText").result(function(event, data, formatted) {
        highlightSelectedUser(data[1], formatted);
    });
    function highlightSelectedUser(username, fullName) {
        $('#selectedUser').show();
        $('#acceptedFullName').text(fullName);
        $('#acceptedUsername').text(username);
        $('#visitorUsername').val(username);
        $('#submit').attr('disabled', '');
    }
});
</script>

</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<div id="content" class="main col">
<noscript>
<div class="alert">
<strong><spring:message code="javascript.required"/></strong>
<p>
<spring:message code="javascript.required.more"/>&nbsp;<a href="<c:url value="visitor-history.html?noscript=true"/>"><spring:message code="noscript.version"/></a>.
</p>
</div>
</noscript>

<div class="ownerform">
<form:form commandName="command">
<fieldset>
<legend><spring:message code="visitor.history"/></legend>
<p class="info"><spring:message code="visitor.history.help"/></p>
<div class="formerror"><form:errors path="*"/></div>

<label for="userSearchText"><spring:message code="name"/>&nbsp;or&nbsp;<spring:message code="username"/>:</label>&nbsp;
<form:input path="userSearchText" id="userSearchText" maxlength="64"/>
<br/><br/>
<div id="selectedUser"><span id="acceptedFullName"></span>&nbsp;(<span id="acceptedUsername"></span>)</div>
<label for="startTime"><spring:message code="from.start.date"/>:</label>&nbsp;<form:input path="startTime"/>
<br/>
<br/>
<label for="endTime"><spring:message code="until.end.date"/>:</label>&nbsp;<form:input path="endTime"/>
<br/>
<br/>
<form:hidden path="visitorUsername" id="visitorUsername"/>
<input id="submit" type="submit" value="<spring:message code="search"/>" disabled="disabled"/>
</fieldset>
</form:form>
</div><!-- ownerform -->
<a href="<c:url value="schedule.html"/>">&laquo;<spring:message code="return.to.availability.schedule"/></a>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>