/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// Lock a submit button when clicked from being clicking again and change the value of the submit to a message, while preserving the submits name and value in a hidden input.
// License: http://www.gnu.org/licenses/lgpl.txt
// Homepage: http://blog.leenix.co.uk/2009/09/jquery-plugin-locksubmit-stop-submit.html
// Version 1.03
;(function($) {

jQuery.fn.lockSubmit = function(options) {

	//Default text to change submit button too
	var settings = $.extend({
		submitText: null,
		onAddCSS: null,
		onClickCSS: null
	}, options);

	//add CSS to this button
	if(settings.onAddCSS) {	this.addClass(settings.onAddCSS); }

	return this.click(function(e) {		

		//Hide current submit and insert a dummy submit which is disabled. The reason for doing this and not just disabling the normal submit, is that in some browsers the disabled submit will stop the form being submited at all.

		targetselect = $(this);

		targetselect.hide();
		
		//new buttons value
		if(settings.submitText) { var newValue = settings.submitText; } else { var newValue = $(this).val(); }

		//insert hidden field with name and value of submit
		targetselect.after("<input id='dummySubmit' disabled='disabled' type='submit' name='"+$(this).attr("name")+"DUMMY' value='"+newValue+"'>");

		//add onClick CSS
		if(settings.onClickCSS) {
			$("#dummySubmit").addClass(settings.onClickCSS);
		}

		return true;
	});

};

jQuery.fn.lockSubmitReset = function() {
	this.show();
	$("#dummySubmit").remove();
};

})(jQuery);
