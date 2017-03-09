package org.flywind.cms.pages;

import java.util.List;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.cms.Posts;
import org.flywind.business.services.cms.PostsService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;

public class Tags extends AppBase {
	
	@Property
	@Persist
	private Long tagid;

	@Property
	private Posts posts;
	
	@Property
	private List<Posts> postss;
	
	@Inject
	private PostsService postsService;
	
	@InjectComponent
	private Zone dataZone;
	
	public void setParams(Long id){
		this.tagid = id;
	}
	
	@OnEvent(component="plist",value=WidgetSymbolConstants.PAGER_LOAD_DATA)
	public void getPostsData(){
		
		if(posts == null){
			posts = new Posts();
			posts.setTagId(tagid);
		}
		FPage page = (FPage)request.getAttribute("page");
		page.setSortName("lastUpdateTime");
		page.setSortOrder("DESC");
		postss = postsService.getAllPosts(posts, page,getCurrentLanguage());
		
		System.out.println(page.getRowCount());
		ajaxResponseRenderer.addRender(dataZone);
		
		
	}
}
