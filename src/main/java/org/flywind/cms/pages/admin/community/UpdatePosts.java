package org.flywind.cms.pages.admin.community;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.internal.SelectModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.flywind.business.entities.cms.Category;
import org.flywind.business.entities.cms.Posts;
import org.flywind.business.entities.cms.Tag;
import org.flywind.business.services.cms.CategoryService;
import org.flywind.business.services.cms.PostsService;
import org.flywind.business.services.cms.TagService;
import org.flywind.cms.base.AppBase;
import org.flywind.cms.pages.Start;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.components.FAjaxUpload;

public class UpdatePosts extends AppBase {
	
	private static final Logger logger = Logger.getLogger(UpdatePosts.class);
	
	@PageActivationContext
	private Long id;
	
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
	private String picUrl;

	@Property
	@Persist
	private String oldPic;

	@Property
	@Persist
	private String newPicUrl;

	@InjectComponent
	private Zone uploadImgZone;
	
	@Persist(PersistenceConstants.FLASH)
	@Property
	private String message;
	
	public void onPrepare(){
		if (posts == null){
			posts = new Posts();
		}
		
		if(meForm.isValid()){
			newPicUrl = null;
			posts = postsService.getPostsById(id);
			oldPic = posts.getPicUrl();
		}
	}
	
	void uploadFileFunc(UploadedFile uploadedFile) {

		String savePath = getSavePath("/uploadImages/contentData/");
		String saveUrl = getSaveUrl("/uploadImages/contentData/");
		
		// 检查目录
		File uploadDir = new File(savePath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		 
		// 检查目录写权限
		if (!uploadDir.canWrite()) {
			System.out.println("上传目录没有写的权限。");
			return;
		}
		
		//保存的目录
		File saveDirFile = new File(saveUrl);
		if (!saveDirFile.exists()) {
			saveDirFile.mkdirs();
		}
		String fileName = uploadedFile.getFileName();
		UUID uuid = UUID.randomUUID();
		String newFileName = "technology" + "_" + uuid + "." + fileName.split("\\" + ".")[1];
		File mapFile = new File(savePath + newFileName);
		uploadedFile.write(mapFile);

		picUrl = saveUrl + newFileName;

	}

	@OnEvent(component = "uploadImage", value = WidgetSymbolConstants.AJAX_UPLOAD)
	void onImageUploadTwo(UploadedFile uploadedFile) {
		uploadFileFunc(uploadedFile);
		newPicUrl = "";
		newPicUrl = picUrl;
		ajaxResponseRenderer.addRender("uploadImgZone", uploadImgZone.getBody());
	}

	// 为了支持ie
	@OnEvent(component = "uploadImage", value = WidgetSymbolConstants.NON_XHR_UPLOAD)
	Object onImageUploadTwoNoAjax(UploadedFile uploadedFile) {
		uploadFileFunc(uploadedFile);
		newPicUrl = "";
		newPicUrl = picUrl;
		final JSONObject result = new JSONObject();
		final JSONObject params = new JSONObject()
				.put("url", componentResources.createEventLink("myEvent", "NON_XHR__UPLOAD").toURI())
				.put("zoneId", "uploadImgZone");

		result.put(FAjaxUpload.UPDATE_ZONE_CALLBACK, params);

		return result;
	}

	void onMyEvent(String messages) {
		message = "文件上传异常: " + messages;
		ajaxResponseRenderer.addRender("uploadImgZone", uploadImgZone.getBody());
	}

	void onUploadException(FileUploadException ex) {
		message = "文件上传异常: " + ex.getMessage();
		ajaxResponseRenderer.addRender("uploadImgZone", uploadImgZone.getBody());
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
			posts.setLastUpdateTime(new Date());
			if(picUrl != null){
				posts.setPicUrl(picUrl);
			}
			postsService.updatePosts(posts);
		} catch (Exception e) {
			String error = messages.get("update-error");
			alertManager.error(error);
			meForm.recordError(error);
			logger.error(e.getMessage());
		}	
	}
	
	public Object onSuccessFromMeForm(){
		return PostsList.class;
	}

}
