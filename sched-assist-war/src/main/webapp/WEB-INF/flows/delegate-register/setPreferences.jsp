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
<title><spring:message code="application.name"/> - <spring:message code="schedule.owner.registration"/> <spring:message code="step" arguments="1,3"/></title>
<%@ include file="/WEB-INF/jsp/themes/jasig/head-elements.jsp" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery.lightbox-0.5.css"/>" media="screen"/>
<rs:resourceURL value="/rs/famfamfam/silk/1.3/application_form_magnify.png" var="screenShotIcon"/>
<style type="text/css">
.screenshot {padding-right:18px;background: transparent url(${screenShotIcon}) no-repeat center right;font-style:italic;font-size:105%;}
</style>
<script type="text/javascript" src="<c:url value="/js/jquery.lockSubmit.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.lightbox-0.5.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.jqEasyCharCounter.min.js"/>"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(':submit').lockSubmit();
	$('.screenshot').lightBox({
		overlayBgColor: '#666',
		imageLoading: '<c:url value="/img/lightbox-0.5/lightbox-ico-loading.gif"/>',
		imageBtnClose: '<c:url value="/img/lightbox-0.5/lightbox-btn-close.gif"/>',
		imageBtnPrev: '<c:url value="/img/lightbox-0.5/lightbox-btn-prev.gif"/>',
		imageBtnNext: '<c:url value="/img/lightbox-0.5/lightbox-btn-next.gif"/>',
		imageBlank: '<c:url value="/img/lightbox-0.5/lightbox-blank.gif"/>'
	});
	$('#noteboard').jqEasyCounter({
		'maxChars': 500,
		'maxCharsWarning': 375,
		'msgFontSize': '12px',
		'msgFontColor': '#000',
		'msgTextAlign': 'right',
		'msgWarningColor': '#ff6600',
		'msgAppendMethod': 'insertAfter'             
	});
	
	var originalValue = $('.meetingLimitValue').val();
	if($('.enableMeetingLimit').is(':checked')) {
		displayMeetingLimit();
	} else {
		hideMeetingLimit();
	}
	$('.enableMeetingLimit').change(function() {
		if($(this).is(':checked')) {
			displayMeetingLimit();
		} else {
			hideMeetingLimit();
		}
	});
	function hideMeetingLimit() {
		$('.meetingLimitValueField').hide();
		$('.meetingLimitValue').val(-1);
		$('.meetingLimitValue').attr('disabled', 'disabled');
	}
	function displayMeetingLimit() {
		if(originalValue == -1) {
			$('.meetingLimitValue').val(1);
		} else {
			$('.meetingLimitValue').val(originalValue);
		}
		$('.meetingLimitValue').attr('disabled', '');
		$('.meetingLimitValueField').show();
	}

	if($('.enableEmailReminders').is(':checked')) {
        displayEmailReminders();
    } else {
        hideEmailReminders();
    }
    $('.enableEmailReminders').change(function() {
        if($(this).is(':checked')) {
            displayEmailReminders();
        } else {
            hideEmailReminders();
        }
    });
    function hideEmailReminders() {
        $('.emailRemindersOptions').hide();
    }
    function displayEmailReminders() {
        $('.emailRemindersOptions').show();
    }
});
</script>
</head>

<body>
<%@ include file="/WEB-INF/jsp/themes/jasig/body-start.jsp" %>
<%@ include file="/WEB-INF/jsp/login-info.jsp" %>

<div id="content" class="main col">
<h3><spring:message code="schedule.owner.registration"/> <spring:message code="step" arguments="1,3"/></h3>
<p class="info">
<spring:message code="schedule.owner.registration.step1.help"/>
<br/>
<spring:message code="all.fields.required"/>
</p>
<div class="ownerform">
<form:form commandName="registration">
<fieldset>
<legend><spring:message code="preferences"/></legend>
<div class="formerror"><form:errors path="*"/></div>

<label for="location"><strong><spring:message code="meeting.location"/></strong><br/>(<spring:message code="meeting.location.example"/>):</label>&nbsp;
<form:input path="location" maxlength="100"/>
<br/><br/>

<label for="titlePrefix"><strong><spring:message code="meeting.title.prefix"/></strong><br/>(<spring:message code="meeting.title.prefix.example"/>):</label>&nbsp;
<form:input path="titlePrefix" maxlength="100"/>
<br/><br/>

<label for="noteboard"><strong><spring:message code="noteboard"/></strong><br/>
<spring:message code="noteboard.help"/></label>&nbsp;<br/>
<form:textarea path="noteboard" rows="6" cols="60" id="noteboard"/>
<br/><br/>

<label for="meetingLength"><strong><spring:message code="meeting.duration"/></strong><br/>(<spring:message code="in.minutes"/>):</label>&nbsp;
<form:input path="meetingLength" cssStyle="width: 3em;" maxlength="3"/><br/>
<label for="allowDoubleLength"><spring:message code="offer.double.length.meeting.option"/>:</label>&nbsp;
<form:checkbox path="allowDoubleLength"/>
<br/><br/>

<label for="defaultVisitorsPerAppointment"><strong><spring:message code="default.visitors.per.appointment"/></strong><br/>
<spring:message code="default.visitors.per.appointment.help"/>:</label>&nbsp;
<form:input path="defaultVisitorsPerAppointment" cssStyle="width: 2em;" maxlength="2"/>
<br/>
<br/>

<h4><spring:message code="availability.window"/></h4>
<label for="windowHoursStart"><spring:message code="earliest.appointment.is"/></label>&nbsp;
<form:input path="windowHoursStart" cssStyle="width: 3em;" maxlength="3"/>&nbsp;<spring:message code="earliest.appointment.is.suffix"/>
<br/>
<label for="windowWeeksEnd"><spring:message code="latest.appointment.is"/></label>&nbsp;
<form:input path="windowWeeksEnd" cssStyle="width: 2em;" maxlength="2"/>&nbsp;<spring:message code="latest.appointment.is.suffix"/>
<br/>
<label for="enableMeetingLimit"><spring:message code="visitors.limited.meetings.confirm"/>:</label>&nbsp;
<form:checkbox cssClass="enableMeetingLimit" path="enableMeetingLimit"/><br/>
<div class="meetingLimitValueField">
<label for="meetingLimitValue"><spring:message code="visitors.limited.meetings.number"/>:</label>&nbsp;
<form:input cssClass="meetingLimitValue" path="meetingLimitValue" cssStyle="width: 2em;" maxlength="2"/>
</div>

<h4><spring:message code="email.reminders"/></h4>
<label for="enableEmailReminders"><spring:message code="email.reminders.confirm"/>:</label>&nbsp;
<form:checkbox cssClass="enableEmailReminders" path="enableEmailReminders"/><br/>
<div class="emailRemindersOptions">
<label for="emailReminderIncludeOwner"><spring:message code="email.reminders.include.me"/>:</label>&nbsp;
<form:checkbox cssClass="emailReminderIncludeOwner" path="emailReminderIncludeOwner"/><br/>
<label for="emailReminderHours"><spring:message code="email.reminders.number.hours"/>:</label>&nbsp;
<form:input cssClass="emailReminderHours" path="emailReminderHours" cssStyle="width: 2em;" maxlength="2"/>
</div>
<br/>

<h4><spring:message code="reflect.my.schedule"/></h4>
<p><spring:message code="reflect.my.schedule.help"/><br/>
<a href="<c:url value="/img/reflection-example.png"/>" class="screenshot"><spring:message code="reflect.my.schedule.example"/></a><br/>
<label for="reflectSchedule"><spring:message code="reflect.my.schedule.confirm"/>:&nbsp;</label>
<form:checkbox path="reflectSchedule" id="reflectSchedule"/>
</p>

<input type="submit" name="_eventId_submit" value="<spring:message code="schedule.owner.registration.save.and.proceed" arguments="2"/>"/>
</fieldset>
</form:form>
</div> <!-- close ownerForm -->
</div> <!--  content -->

<%@ include file="/WEB-INF/jsp/themes/jasig/body-end.jsp" %>
</body>
</html>