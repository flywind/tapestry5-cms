package org.flywind.cms.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.utils.FSysUtil;
import org.flywind.business.entities.cms.Discuss;
import org.flywind.business.entities.cms.Posts;
import org.flywind.business.services.cms.DiscussService;
import org.flywind.business.services.cms.PostsService;
import org.flywind.cms.base.AppBase;

public class PostsDetial extends AppBase {
	
	private static final Logger logger = Logger.getLogger(PostsDetial.class);

	@Property
	@PageActivationContext
	private Long id;
	
	@Property
	private Posts posts;
	
	@Property
	private Discuss discuss;
	
	@Property
	private List<Discuss> discusses;
	
	@Property
	private String message;
	
	@Inject
	private DiscussService discussService;
	
	@Inject
	private PostsService postsService;
	
	@Property
	private Long count;
	
	@InjectComponent
	private Form commentForm;
	
	@Property
	@Persist
	private String currentUserName;
	
	@InjectComponent
	private Zone loveZone;
	
	@InjectPage
	private Tags tagPage;
	
	public Object onGo(Long id){
		tagPage.setParams(id);
		return tagPage;
	}
	
	public void onPrepare(){
		if(discuss == null){
			discuss = new Discuss();
		}
	}
	
	public void setupRender(){
		
		if(posts == null){
			posts = postsService.getPostsById(id);
			count = posts.getClicks() + 1;
			posts.setClicks(count);
			postsService.updatePosts(posts);
			discusses = discussService.getDiscussByPostsId(id);	
		}	
		
		if(state.getSysInfo() != null){
			currentUserName = state.getSysInfo().getUser().getUsername();
		}else{
			currentUserName = "guestname";
		}
	}
	
	public void onLike(String userName,Long id){
		if(!(userName.equalsIgnoreCase("guestname"))){
			
			posts = postsService.getPostsById(id);
			if(StringUtils.isNotBlank(posts.getVoter())){
				List<String> ls = FSysUtil.strsToList(posts.getVoter(),null);
				if(FSysUtil.strExist(userName, ls)){
					if(null != ls && ls.size() > 0){
						for(String s : ls){
							if(s.equals(userName)){
								ls.remove(s);
								break;
							}
						}
					}
					posts.setVoter(FSysUtil.listToStrs(ls));
					postsService.updatePosts(posts);
				}else{			
					posts.setVoter(FSysUtil.listToStrs(ls));
					postsService.updatePosts(posts);
				}
				
			}else{
				posts.setVoter(userName);
				postsService.updatePosts(posts);
			}
			posts = postsService.getPostsById(id);
			ajaxResponseRenderer.addRender(loveZone);
		}
	}
	
	public boolean isUserInfo(){
		if(state.getSysInfo() != null){
			return true;
		}
		return false;
	}
	public void onValidateFromCommentForm(){
		if(commentForm.getHasErrors()){
			return;
		};
		
		try {
			discuss.setAuthor(state.getSysInfo().getUser().getUsername());
			discuss.setPostsId(id);
			discuss.initBaseInfo(state.getSysInfo().getUser().getUsername(), state.getSysInfo().getCustomerCode());
			discuss.setMessage(message);
			discussService.createDiscuss(discuss);
		} catch (Exception e) {
			String err = messages.get("create-error");
			logger.error(err+ ": " + e.getMessage());
			alertManager.error(err);
			commentForm.recordError(err);
		}
	}
}
