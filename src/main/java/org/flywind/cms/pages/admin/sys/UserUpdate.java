package org.flywind.cms.pages.admin.sys;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.flywind.business.common.constants.FBaseConstants;
import org.flywind.business.common.constants.FSysConstants;
import org.flywind.business.common.result.Json;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.OrganizationService;
import org.flywind.business.services.sys.UserService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.components.FAjaxUpload;

@RequiresPermissions("user:edit")
@Import(stylesheet = { "context:assets/styles/zTreeStyle/zTreeStyle.css" })
public class UserUpdate extends AppBase {
	
	/**
	 * 记录日志
	 */
	private static final Logger logger = Logger.getLogger(UserUpdate.class);

	@Property
	@Persist
	private User user;

	@Inject
	private UserService userService;

	@InjectComponent("updateForm")
	private Form updateForm;

	@Inject
	private Messages messages;

	@InjectComponent
	private Zone userCheckZone;

	@Inject
	private OrganizationService organizationService;

	@Property
	private String companyName;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private Json json;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private Json roleJson;

	@Property
	private String roles, roleIds;
	
	@Persist(PersistenceConstants.FLASH)
	@Property
	private String message;
	
	@PageActivationContext
	private Long id;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String picUrl;
	
	@Property
	private String oldPic;
	
	@Property
	@Persist
	private String newPicUrl;
	
	@InjectComponent
	private Zone uploadImgZone;
	
	@Property
    private UploadedFile uploadedFile;

	void onPrepareForRender() throws Exception {
		if (updateForm.isValid()) {
			user = userService.findOne(id);	
			oldPic = user.getPicUrl();
		}
	}

	void onPrepareForSubmit() throws Exception {
		user = userService.findOne(id);
	}
	
	void uploadFileFunc(UploadedFile uploadedFile){
		
		String savePath = getSavePath("/uploadImages/uploadData/");
		String saveUrl = getSaveUrl("/uploadImages/uploadData/");
		
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
			userService.updateUserAndRole(user.getId(), roleIds);
		} catch (Exception e) {
			logger.error(messages.get("update-error"), e);
			alertManager.error(messages.get("update-error"));
			
			//阻止表单提交
			updateForm.recordError(messages.get("userUpdateError"));
		}
	}

	void onSuccess() {
		alertManager.success(messages.get("update-success"));
		//return UserList.class;
	}

	
	void onUserCheck(@RequestParameter(value = "param", allowBlank = true) final String userName) throws Exception {
		if (userName != null) {
			boolean userNameExist = userService.checkUserExist(userName,state.getSysInfo().getUser().getCustomerCode());
			// ajax校验 屏蔽原有名称的影响
			User user = userService.findOne(id);
			if (userName.equals(user.getUsername())) {
				userNameExist = false;
			}
			if (!userNameExist && request.isXHR()) {
				ajaxResponseRenderer.addRender(userCheckZone).addCallback(new JavaScriptCallback() {

					@Override
					public void run(JavaScriptSupport javascriptSupport) {
						JSONObject json = new JSONObject();
						json.put(FBaseConstants.ID_STRING, FSysConstants.USER_NAME);
						javascriptSupport.require("userCheck").invoke(FBaseConstants.SUCCESS_STRING).with(json);
					}
				});
			} else {
				ajaxResponseRenderer.addRender(userCheckZone).addCallback(new JavaScriptCallback() {

					@Override
					public void run(JavaScriptSupport javascriptSupport) {
						JSONObject json = new JSONObject();
						json.put(FBaseConstants.ID_STRING, FSysConstants.USER_NAME);
						json.put(FBaseConstants.ERROR_STRING, messages.format("user-already-exist", userName));
						javascriptSupport.require("userCheck").invoke(FBaseConstants.ERROR_STRING).with(json);

					}
				});
			}
		}
	}
	
	public boolean isUpdateLoginUser(){
		User loginUser = state.getSysInfo().getUser();
		if(id==loginUser.getId()){
			return true;
		}else{
			return false;
		}
	}

	
	public void setupRender() {
		User loginuser=state.getSysInfo().getUser();
		//完善user对象
		userService.perfectUser(loginuser);
		json = organizationService.findAllToJsonByUser(loginuser);
		//修改页面，如果是修改当前用户，角色隐藏自己拥有的角色，如果是修改登录用户创建的角色，则显示登录用户自己创建的角色
		if(isUpdateLoginUser()){
			roleJson = userService.findOwnRole(loginuser);
		}else{
			roleJson = userService.findAllRolesToJson(loginuser);
		}
		
	}

	public void afterRender() {
		javaScriptSupport.require("init-userUpdate").invoke("init").with(json.toString());
		javaScriptSupport.require("init-userUpdate").invoke("initRole").with(roleJson.toString());
	}

}
