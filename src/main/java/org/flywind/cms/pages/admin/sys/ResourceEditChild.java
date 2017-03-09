package org.flywind.cms.pages.admin.sys;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.exception.FException;
import org.flywind.business.entities.sys.Resource;
import org.flywind.business.services.sys.ResourceService;
import org.flywind.cms.base.AppBase;
@RequiresPermissions("resource:edit")
public class ResourceEditChild extends AppBase {

	/**
	 * 记录日志
	 */
	private static final Logger logger = Logger.getLogger(ResourceEditChild.class);

	@Property
	@PageActivationContext
	private Long resourceId;
	
	/**
	 * 当前资源对象
	 */
	@Property
	private Resource resource;
	
	/**
	 * 资源类型
	 */
	@Property
	private String type;
	
	@Inject
	private ResourceService resourceService;
	
	
	/**
	 * 类初始化时执行
	 */
	public void onPrepare(){
		if(null == resource){
			resource = new Resource();
		}
	}
	
	/**
	 * 页面渲染前执行
	 */
	public void setupRender(){
		resource = resourceService.getResourceById(resourceId);
	}
	
	/**
	 * 表单提交时执行
	 * 
	 * @return
	 *        目录页面对象
	 */
	public Object onSuccessFromEditForm(){
		try {
			String username = state.getSysInfo().getUser().getUsername();
			resource.setId(resourceId);
			resource.setLastUpdatePerson(username);
			resource.setLastUpdateTime(new Date());
			resourceService.updateResource(resource);
		} catch (FException e) {
			String msg = messages.get(e.getTypeKey());
			alertManager.warn(msg);
			logger.error(msg, e);
			return this;
		} catch (Exception e) {
			logger.error("", e);
		}
		return ResourceList.class;
	}
}
