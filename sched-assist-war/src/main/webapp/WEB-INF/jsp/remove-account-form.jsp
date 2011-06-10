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
<%-- 
  Copyright 2008-2010 The Board of Regents of the University of Wisconsin System.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title>Scheduling Assistant - Remove My Account</title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();
});
</script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>
<%@ include file="/WEB-INF/jsp/owner-navigation.jsp" %>
<div id="content" class="main col">
<div class="alert">
<p><strong>This form allows you to remove your Scheduling Assistant account and preferences.</strong><br/>
You will still be able to use the Enterprise Calendar, however your Availability Schedule and account preferences will
be removed from the Scheduling Assistant. Any appointments created with the Scheduling Assistant
will remain within your Enterprise Calendar account.</p>
</div>

<div class="ownerform">
<form:form>
<fieldset>
<legend>Remove Scheduling Assistant Account</legend>
<div class="formerror"><form:errors path="*"/></div>
<label for="confirmed">Are you sure you wish to remove this Scheduling Assistant account?&nbsp;</label>
<form:checkbox path="confirmed"/>
<br/>
<br/>
<input type="submit" value="Remove My Account"/>
</fieldset>
</form:form>
</div> <!-- ownerform -->
<a href="schedule.html">&laquo;Keep this account and Return to Availability Schedule</a>

</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>