package org.flywind.cms.pages.admin.sys;

import java.util.Date;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.Comment;
import org.flywind.business.services.sys.CommentService;
import org.flywind.cms.base.AppBase;

public class CommentEdit extends AppBase {

	@Property
	private Comment item;
	
	@Inject
	private CommentService itemService;
	
	@Property
	@PageActivationContext
	private Long id;
	
	@InjectComponent
	private Form meForm;
	
	public void onPrepare(){
		if(item == null){
			item = new Comment();
		}
		if(meForm.isValid()){
			item = itemService.getById(id);
		}
	}
	
	public Object onSuccess(){
		try {
			item.setLastUpdateTime(new Date());
			itemService.update(item);
			return CommentList.class;
		} catch (Exception e) {
			String error = messages.get("update-error");
			alertManager.error(error);
			meForm.recordError(error);
		}
		
		return this;
	}
}
