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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="rs" uri="http://www.jasig.org/resource-server" %><%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="wrap">
  <div id="home">
    <div id="header">
      <div class="skip"><a href="#content" accesskey="S">Skip to main content</a></div>

	  <rs:resourceURL var="logo" value="/themes/jasig/images/schedAssistLogo-1.png"/>
      <a id="crest" href="http://www.jasig.org/"><img src="${logo}" alt="Scheduling Assistant Logo" /></a>
	  
      <div id="siteTitle">
        <h1><a href="<c:url value="/"/>"><span>Scheduling Assistant</span></a></h1>
      </div>

      <ul id="globalnav">
      	<li id="uwsearch"><a href="http://www.jasig.org/bedework/">Bedework</a></li>
        <li id="last_tool"><a href="http://www.jasig.org/">Jasig</a></li>
      </ul>
    </div>

    <div id="shell">
      
      