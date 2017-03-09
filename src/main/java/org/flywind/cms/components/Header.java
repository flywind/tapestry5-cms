package org.flywind.cms.components;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.flywind.business.entities.sys.User;
import org.flywind.cms.base.AppBase;
import org.flywind.cms.pages.Search;

public class Header extends AppBase {

	@Property
	private User user;
	
	@Property
	private String searchValue;
	
	@InjectPage
	private Search page;

	public boolean isSupper(){
		if("admin".equals(state.getSysInfo().getUser().getUsername())){
			return true;
		}
		
		return false;
	}
	
	public void setupRender(){
		if(state.getSysInfo() != null){
			user = state.getSysInfo().getUser();
		}
		
	}
	
	public Object onValidateFromSearchForm(){
		page.set(searchValue);
		return page;
	}
}
