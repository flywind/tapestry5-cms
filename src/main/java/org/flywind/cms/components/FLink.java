package org.flywind.cms.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.flywind.cms.base.AppBase;

public class FLink extends AppBase {

	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String page;
	
	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String value;
	
	@Property
	@Parameter(value="active",defaultPrefix = BindingConstants.LITERAL)
	private String activeCls;
	
	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String iconCls;
	
	public boolean isCur(){
		return componentResources.getPageName().equals(page);
	}
	
	public boolean isItem(){
		if(iconCls != null){
			return true;
		}
		return false;
	}
}
