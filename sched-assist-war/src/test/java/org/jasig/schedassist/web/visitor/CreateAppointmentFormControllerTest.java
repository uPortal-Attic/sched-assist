/**
 * 
 */
package org.jasig.schedassist.web.visitor;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.jasig.schedassist.SchedulingException;
import org.jasig.schedassist.model.InputFormatException;
import org.jasig.schedassist.model.VisibleWindow;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author nblair
 *
 */
public class CreateAppointmentFormControllerTest {

	@Test
	public void testValidateChosenStartTime() throws InputFormatException {
		CreateAppointmentFormController controller = new CreateAppointmentFormController();
		
		VisibleWindow window = VisibleWindow.fromKey("1,1");
		try {
			Date now = new Date();
			controller.validateChosenStartTime(window, now);
			Assert.fail("expected SchedulingException not thrown");
		} catch (SchedulingException e) {
			// expected, success
		}
		
		try {
			controller.validateChosenStartTime(window, DateUtils.addHours(new Date(), 2));
		} catch (SchedulingException e) {
			Assert.fail("expected SchedulingException not thrown for date 1 hour after window start");
		}
		
		try {
			controller.validateChosenStartTime(window, DateUtils.addHours(new Date(), 167));
		} catch (SchedulingException e) {
			Assert.fail("expected SchedulingException not thrown for date 1 hour before window end");
		}
		
		try {
			controller.validateChosenStartTime(window, DateUtils.addHours(new Date(), 169));
			Assert.fail("expected SchedulingException not thrown for date 1 hour after window end");
		} catch (SchedulingException e) {
			// expected, success
		}

	}
}
