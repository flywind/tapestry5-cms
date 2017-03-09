package org.flywind.cms.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.ExceptionReporter;

public class MyExceptionReport implements ExceptionReporter {
	
	@Property
	private Throwable exception;
	
	@Property
	private boolean unauthorized = false;

	public void reportException(Throwable exception) {
		if(exception.getMessage().contains("Subject does not have")){
			unauthorized = true;
		}
		this.exception = exception;
	}
	
}
