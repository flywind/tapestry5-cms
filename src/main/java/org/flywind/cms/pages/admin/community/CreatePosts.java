package org.flywind.cms.pages.admin.community;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.internal.SelectModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.cms.Category;
import org.flywind.business.entities.cms.Posts;
import org.flywind.business.entities.cms.Tag;
import org.flywind.business.services.cms.CategoryService;
import org.flywind.business.services.cms.PostsService;
import org.flywind.business.services.cms.TagService;
import org.flywind.cms.base.AppBase;
import org.flywind.cms.pages.Start;

public class CreatePosts extends AppBase {
	
	private static final Logger logger = Logger.getLogger(CreatePosts.class);
	
	@Property
	private Posts posts;
	
	@Inject
	private PostsService postsService;
	
	@Inject
	private TagService tagService;
	
	@Inject
	private CategoryService categoryService;
	
	@InjectComponent("meForm")
	private Form meForm;
	
	@Property
	@Persist
	private String lang;
	
	public void onPrepare(){
		if (posts == null){
			posts = new Posts();
		}
	}
	
	public void setupRender(){
		lang = getCurrentLanguage();
	}
	
	public SelectModel getCategorys(){
		final List<OptionModel> options = new ArrayList<OptionModel>();
		 
		List<Category>  categorys = categoryService.getAllCategory();
		
		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
			//循环添加到OptionModel
			for(Category c : categorys){
				options.add(new OptionModelImpl(c.getName(),c.getId()+""));
			}
		}else{
			//循环添加到OptionModel
			for(Category c : categorys){
				options.add(new OptionModelImpl(c.getNameEn(),c.getId()+""));
			}
		}
		
		//返回SelectModel
		return new SelectModelImpl(null, options);
	}
	
	public SelectModel getTags(){
		final List<OptionModel> options = new ArrayList<OptionModel>();
		 
		List<Tag>  tags = tagService.getAllTags();
		
		//循环添加到OptionModel
		for(Tag t : tags){
			options.add(new OptionModelImpl(t.getValue(),t.getId()+""));
		}
		
		
		//返回SelectModel
		return new SelectModelImpl(null, options);
	}
	
	public void onValidateFromMeForm(){
		if(meForm.getHasErrors()) return;
		try {
			posts.initBaseInfo(state.getSysInfo().getUser().getUsername(), state.getSysInfo().getCustomerCode());
			posts.setAuthor(state.getSysInfo().getUser().getUsername());
			postsService.createPost(posts);
		} catch (Exception e) {
			String error = messages.get("create-error");
			alertManager.error(error);
			meForm.recordError(error);
			logger.error(e.getMessage());
		}	
	}
	
	public Object onSuccessFromMeForm(){
		return Start.class;
	}

}
