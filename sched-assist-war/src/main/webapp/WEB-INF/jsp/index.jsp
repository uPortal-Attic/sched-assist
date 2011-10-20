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
<title><spring:message code="application.name"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>

<rs:resourceURL var="ownerIcon" value="/rs/famfamfam/silk/1.3/calendar_add.png"/>
<rs:resourceURL var="externalIcon" value="/rs/famfamfam/silk/1.3/bullet_go.png"/>
<style type="text/css">
a:hover {
text-decoration:underline;
}
#briefdescr {
font-weight:bold;
}
#owners	{
font-size:90%;
}
.primarylink{
font-weight:bold;
font-size:125%;
}
#contentclose {
position: absolute;
left: -999px;
width: 990px;
}
.ownerlink {
padding-right: 18px;
background: transparent url(${ownerIcon}) no-repeat center right;
font-weight:bold;
}
.external {
padding-right: 18px;
background: transparent url(${externalIcon}) no-repeat center right;
}
</style>
</head>
<body>

<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>

<div id="content" class="main col">
<div id="briefdescr">
<p>
<spring:message code="brief.description"/>
</p>
</div>

<div id="visitors">
<a class="primarylink" href="<c:url value="/visitor.html"/>"><spring:message code="log.in.to.schedule.appointment"/>&raquo;</a>
<a href="<c:url value="/visitor.html"/>"><img src="<c:url value="/img/available-flow.png"/>" title="Schedule Screenshot" /></a>
</div>

<hr/>

<div id="owners">
<p><spring:message code="schedule.owner.target.groups"/>: <a class="ownerlink" href="<c:url value="/owner/schedule.html"/>"><spring:message code="log.in.to.post.availability"/></a></p>
<br/>
<p><spring:message code="resource.administrators"/>: <a class="ownerlink" href="<c:url value="/delegate-login.html"/>"><spring:message code="log.in.to.post.availability.resource"/></a></p>
<br/>
<p><spring:message code="learn.more"/>:<br/>
<a class="external" href="<spring:message code="learn.more.link"/>"><spring:message code="learn.more.link"/></a></p>
</div>

<div id="contentclose">
<a href="<c:url value="/public/browse.html"/>"><spring:message code="browse.public.listings"/></a>
</div>
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>