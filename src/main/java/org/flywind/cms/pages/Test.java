package org.flywind.cms.pages;

import org.apache.tapestry5.annotations.Property;
import org.flywind.cms.base.AppBase;

public class Test extends AppBase {
	
	@Property
	private String content;
	
	
	public void afterRender(){
		javaScriptSupport.require("test").invoke("hello");
	}
}
