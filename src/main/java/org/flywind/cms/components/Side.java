package org.flywind.cms.components;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.cms.Posts;
import org.flywind.business.services.cms.PostsService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.core.dao.FPage;

public class Side extends AppBase {

	@Property
	private Posts shareposts,jobposts;
	
	@Inject
	private PostsService postsService;
	
	public List<Posts> getFindShare(){
		if(shareposts == null){
			shareposts = new Posts();
			shareposts.setIsHome(Boolean.TRUE);
			shareposts.setCategoryId(Long.parseLong("2"));
		}
		FPage page = new FPage();
		page.setPageNumber(1);
		page.setPageSize(8);
		page.setSortName("clicks");
		page.setSortOrder("DESC");
		return postsService.getAllPosts(shareposts, page, getCurrentLanguage());
	}
	
	public List<Posts> getFindJob(){
		if(jobposts == null){
			jobposts = new Posts();
			jobposts.setCategoryId(Long.parseLong("1"));
		}
		FPage page = new FPage();
		page.setPageNumber(1);
		page.setPageSize(8);
		page.setSortName("clicks");
		page.setSortOrder("DESC");
		return postsService.getAllPosts(jobposts, page, getCurrentLanguage());
	}
	
	public URL onLinka() throws MalformedURLException {
		String url = toUrl(getCurrentLanguage(),"tapestrystarti");
        return new URL(url);
    }
	
	public URL onLinkb() throws MalformedURLException {
		String url = toUrl(getCurrentLanguage(),"tapestrystartone");
        return new URL(url);
    }
	
	public URL onLinkc() throws MalformedURLException {
		String url = toUrl(getCurrentLanguage(),"tapestrycms");
        return new URL(url);
    }
	
	public URL onLinkd() throws MalformedURLException {
		String url = toUrl(getCurrentLanguage(),"tapestryzzl");
        return new URL(url);
    }
	
	public URL onLinke() throws MalformedURLException {
		String url = toUrl(getCurrentLanguage(),"tapestryff");
        return new URL(url);
    }
	
	public URL onLinkf() throws MalformedURLException {
		String url = toUrl(getCurrentLanguage(),"tapestrystartii");
        return new URL(url);
    }
	
	public static String toUrl(String lang, String page) {
		String url = "";
		if("zh-cn".equalsIgnoreCase(lang)){
			url = "http://cms.flywind.org/zh_CN/"+page;
		}else{
			url = "http://cms.flywind.org/en/"+page;
		}
		return url;
	}
}
