// Lock a submit button when clicked from being clicking again and change the value of the submit to a message, while preserving the submits name and value in a hidden input.
// License: http://www.gnu.org/licenses/lgpl.txt
// Homepage: http://blog.leenix.co.uk/2009/09/jquery-plugin-locksubmit-stop-submit.html
// Version 1.03

jQuery.fn.lockSubmit = function(options) {

	//Default text to change submit button too
	var settings = jQuery.extend({
		submitText: null,
		onAddCSS: null,
		onClickCSS: null
	}, options);

	//add CSS to this button
	if(settings.onAddCSS) {	this.addClass(settings.onAddCSS); }

	return this.click(function(e) {		

		//Hide current submit and insert a dummy submit which is disabled. The reason for doing this and not just disabling the normal submit, is that in some browsers the disabled submit will stop the form being submited at all.

		targetselect = jQuery(this);

		targetselect.hide();
		
		//new buttons value
		if(settings.submitText) { var newValue = settings.submitText; } else { var newValue = jQuery(this).val(); }

		//insert hidden field with name and value of submit
		targetselect.after("<input id='dummySubmit' disabled='disabled' type='submit' name='"+jQuery(this).attr("name")+"DUMMY' value='"+newValue+"'>");

		//add onClick CSS
		if(settings.onClickCSS) {
			jQuery("#dummySubmit").addClass(settings.onClickCSS);
		}

		return true;
	});

};

jQuery.fn.lockSubmitReset = function() {
	this.show();
	jQuery("#dummySubmit").remove();
};

