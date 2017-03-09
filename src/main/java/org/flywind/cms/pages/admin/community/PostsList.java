package org.flywind.cms.pages.admin.community;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.internal.SelectModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.exception.FException;
import org.flywind.business.entities.base.SysParam;
import org.flywind.business.entities.cms.Category;
import org.flywind.business.entities.cms.Posts;
import org.flywind.business.entities.cms.Technology;
import org.flywind.business.services.cms.CategoryService;
import org.flywind.business.services.cms.PostsService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;

public class PostsList extends AppBase {

	@Property
	@Persist
	private Posts posts;
	
	@Property
	private List<Posts> postses;
	
	@Inject
	private PostsService postsService;
	
	@Inject
	private CategoryService categoryService;
	
	@InjectComponent
	private Zone listZone;
	
	private final static char EQUAL = '=';
	
	private final static char COMMA = ',';
	
	@Property
	private String types;
	
	@Property
	@Persist
	private String delUrl;
	
	@Property
	@Persist
	private int menutype;
	
	@Property
	@Persist
	private String lang;
	
	public boolean isCn(){
		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
			return true;
		}
		return false;
	}
	
	public void onPrepare(){
		if(posts == null){
			posts = new Posts();
		}
		
		if(StringUtils.isEmpty(types)){
			StringBuffer s = new StringBuffer();
			s.append(0).append(EQUAL).append(messages.get("please-select-label")).append(COMMA);
			List<Category>  categorys = categoryService.getAllCategory();
			if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
				for(Category param : categorys){
					s.append(param.getId()).append(EQUAL).append(param.getName()).append(COMMA);
				}
			}else{
				for(Category param : categorys){
					s.append(param.getId()).append(EQUAL).append(param.getNameEn()).append(COMMA);
				}
			}
			
			types = s.toString();
		}
	}
	
	@OnEvent(component = "gridData", value = WidgetSymbolConstants.BOOTSTRAP_TABLE_LOAD_DATA)
	public void findAll() {
		
		FPage page = (FPage) request.getAttribute("page");
		if(posts == null){
			posts = new Posts();
		}
		page.setSortOrder("DESC");
		page.setSortName("lastUpdateTime");
		posts.setCategoryId(Long.parseLong(menutype+""));
		posts.setAuthor(state.getSysInfo().getUser().getUsername());

		postses = postsService.getAllPosts(posts, page, lang);
	}
	
	public void setupRender(){
		delUrl = componentResources.createEventLink("del").toURI();
		lang = getCurrentLanguage();
	}
	
	public void onSuccessFromSearchForm() {
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(listZone);
		}
	}
	
	public void onReset() {
		menutype = 0;
		posts = null;
		ajaxResponseRenderer.addRender(listZone);
	}
	
	public void onDel(List<String> ids) throws Exception{
		try {
			postsService.deletePostsByIds(state.getSysInfo().getUser().getUsername(), ids);
			ajaxResponseRenderer.addRender(listZone);
		} catch (FException e) {
			String errorMsg = messages.get(e.getTypeKey());
			alertManager.error(errorMsg);
		}
	}
}
