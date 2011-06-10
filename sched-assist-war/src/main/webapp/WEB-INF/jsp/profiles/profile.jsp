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
<title><spring:message code="application.name"/> - <spring:message code="public.profile.for" arguments="${profile.publicProfileId.ownerDisplayName}" argumentSeparator=";"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<style type="text/css">
.ownerInfo {
	padding-left: 2em;
	padding-bottom: 1em;
}
</style>
<script type="text/javascript">
jQuery(document).ready(function(){
	$('.noteboardtext').each(function(i,element){	
		existing = $(element).text();
		$(element).html(linkify(existing));
	});
});
</script>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<div id="content" class="main col">

<div class="publicperson">
<h3>${profile.publicProfileId.ownerDisplayName}</h3>
<h4><spring:message code="about.me"/></h4>
<div class="ownerInfo">
<c:out value="${profile.description}"/>
</div>
<h4><spring:message code="noteboard"/></h4>
<div class="ownerInfo">
<c:forEach var="noteboardSentence" items="${profile.ownerNoteboardSentences}" varStatus="itemCount">
<span class="noteboardtext"><c:out value="${noteboardSentence}"/></span><br/>
</c:forEach>
</div>
<a href="<c:url value="/schedule/${profile.publicProfileId.profileKey}/view.html"/>"><spring:message code="view.my.availability.schedule"/></a>
</div>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>