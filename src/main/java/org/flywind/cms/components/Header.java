package org.flywind.cms.components;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.flywind.business.common.utils.DateUtil;
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
	
	@Property
	private String ampm;
	
	@Property
	private Date today;
	
	private String week;

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
		GregorianCalendar ca = new GregorianCalendar();  
		if(ca.get(GregorianCalendar.AM_PM) == 0){
			ampm = messages.get("am-label");
		}else{
			ampm = messages.get("pm-label");
		}
		
		today = new Date();
		
		
	}
	
	public String getWeek(){
		String w = DateUtil.getWeekOfDate(new Date());
		switch (w) {
		case "MON":
			return messages.get("mon-label");
		case "TUE":
			return messages.get("tue-label");
		case "WED":
			return messages.get("wed-label");
		case "THU":
			return messages.get("thu-label");
		case "FRI":
			return messages.get("fri-label");
		case "SAT":
			return messages.get("sat-label");
		case "SUN":
			return messages.get("sun-label");

		default:
			return "";
		}
	}
	
	public Object onValidateFromSearchForm(){
		page.set(searchValue);
		return page;
	}
}
