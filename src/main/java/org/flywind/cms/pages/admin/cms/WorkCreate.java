package org.flywind.cms.pages.admin.cms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.internal.SelectModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.flywind.business.entities.base.SysParam;
import org.flywind.business.entities.cms.Work;
import org.flywind.business.services.base.SysParamService;
import org.flywind.business.services.cms.WorkService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.components.FAjaxUpload;

@RequiresPermissions("work:add")
public class WorkCreate extends AppBase{

	@Property
	private Work item;
	
	@Inject
	private WorkService itemService;
	
	@InjectComponent
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
	
	@Inject
	private SysParamService sysParamService;
	
	public boolean isCn(){
		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
			return true;
		}
		return false;
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
		String newFileName = "work" + "_" + uuid + "." + fileName.split("\\" + ".")[1];
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
	
	public void onPrepare(){
		if(item == null){
			item = new Work();
		}
		newPicUrl = null;
	}
	
	public void onValidateFromMeForm(){
		if(meForm.getHasErrors()){
			return;
		}
		try {
			item.initBaseInfo(state.getSysInfo().getUser().getUsername(), state.getSysInfo().getCustomerCode());
			item.setType(item.getType());
			item.setSmallImgurl(picUrl);
			itemService.create(item);
			picUrl = null;
		} catch (Exception e) {
			String error = messages.get("link-create-error");
			alertManager.error(error);
			meForm.recordError(error);
		}
	}
	
	public void onSuccess(){
		alertManager.success(messages.get("create-success"));
	}
	
	public SelectModel getTypes() {
		final List<OptionModel> options = new ArrayList<OptionModel>();

		List<SysParam> sysParams = sysParamService.getAllParamByBusinessType(3);
		// 循环添加到OptionModel
		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
			for (SysParam param : sysParams) {
				options.add(new OptionModelImpl(param.getParamValue(), param.getParamKey() + ""));
			}
		}else{
			for (SysParam param : sysParams) {
				options.add(new OptionModelImpl(param.getParamValueEn(), param.getParamKey() + ""));
			}
		}
		
		// 返回SelectModel
		return new SelectModelImpl(null, options);
	}
}
