package org.flywind.cms.pages.admin.community;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.UserService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.components.FAjaxUpload;

public class Settings extends AppBase{
	
	private static final Logger logger = Logger.getLogger(Settings.class);

	@Property
	@Persist
	private User user;

	@Inject
	private UserService userService;

	@InjectComponent("updateForm")
	private Form updateForm;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String picUrl;
	
	@Property
	private String oldPic;
	
	@Property
	@Persist
	private String newPicUrl;
	
	@Persist(PersistenceConstants.FLASH)
	@Property
	private String message;
	
	@InjectComponent
	private Zone uploadImgZone;
	
	@Property
    private UploadedFile uploadedFile;

	void onPrepareForRender() throws Exception {
		if (updateForm.isValid()) {
			user = userService.findByUsername(state.getSysInfo().getUser().getUsername());	
			oldPic = user.getPicUrl();
		}
	}

	void onPrepareForSubmit() throws Exception {
		user = userService.findByUsername(state.getSysInfo().getUser().getUsername());
	}
	
	void uploadFileFunc(UploadedFile uploadedFile){
		
		String savePath = getSavePath("/uploadImages/head/");
		String saveUrl = getSaveUrl("/uploadImages/head/");
		
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
		String newFileName = user.getUsername() + "_" + uuid + "." + fileName.split("\\" + ".")[1];
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
	
	//为了支持ie
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
    
    void onMyEvent(String messages){
    	message = "文件上传异常: " + messages;
        ajaxResponseRenderer.addRender("uploadImgZone", uploadImgZone.getBody());
    }
    
    void onUploadException(FileUploadException ex) {
        message = "文件上传异常: " + ex.getMessage();
        ajaxResponseRenderer.addRender("uploadImgZone", uploadImgZone.getBody());
    }

	void onValidateFromUpdateForm() {
		if (updateForm.getHasErrors()) {
			return;
		}

		try {
			user.setLastUpdatePerson(state.getSysInfo().getUser().getUsername());
			user.setLastUpdateTime(new Date());
			user.setPicUrl(picUrl);
			userService.updateUser(user);
		} catch (Exception e) {
			logger.error(messages.get("update-error"), e);
			alertManager.error(messages.get("update-error"));
			
			//阻止表单提交
			updateForm.recordError(messages.get("userUpdateError"));
		}
	}
	
	public void onSuccessFromUpdateForm(){
		alertManager.error(messages.get("update-success"));
	}
	
}
