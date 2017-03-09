package org.flywind.cms.pages;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Loop;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.flywind.business.entities.cms.Posts;
import org.flywind.business.services.cms.PostsService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;

public class Search extends AppBase {

	@Property
	@Persist
	private String inputValue;
	
	@Property
	private Posts posts;
	
	@Property
	private List<Posts> postss;
	
	@Inject
	private PostsService postsService;
	
	@InjectComponent
	private Zone dataZone;
	
	@Property
	private String resultRows;
	
	@Property
	private String time;
	
	@OnEvent(component="slist",value=WidgetSymbolConstants.PAGER_LOAD_DATA)
	public void getPostsData(){
		Long d1 = new Date().getTime();
		if(posts == null){
			posts = new Posts();
			posts.setTitle(inputValue);
		}
		
		FPage page = (FPage)request.getAttribute("page");
		page.setSortName("lastUpdateTime");
		page.setSortOrder("DESC");
		postss = postsService.getAllPosts(posts, page,getCurrentLanguage());
		
		String tips = "";
		
		if(inputValue == null){
			tips = "";
		}else{
			tips = "<i class=\"text-primary text-normal\"> "+inputValue+" </i>";
		}
		resultRows = messages.format("search-result-label", tips, page.getRowCount());
		Long d2 = new Date().getTime();
		String endTime = (d2-d1)+"";
		time = messages.format("search-time-label", endTime);
		ajaxResponseRenderer.addRender(dataZone).addCallback(new JavaScriptCallback() {
			
			@Override
			public void run(JavaScriptSupport javascriptSupport) {
				if(inputValue != null){
					javascriptSupport.require("inits/search").invoke("cb").with(inputValue);
				}
			}
		});
	}
	
	public void set(String inputValue){
		this.inputValue = inputValue;
	}
}
