package org.flywind.cms.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.upload.services.MultipartDecoder;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.flywind.widgets.utils.JQueryUtils;

@Path("/upload")
public class EditorUpload {

	@Property
	private UploadedFile file;

	@Inject
	private MultipartDecoder decoder;

	@Inject
	private ApplicationGlobals applicationGlobals;

	@Inject
	private RequestGlobals requestGlobals;

	@POST
	@Produces({ "application/xml", "application/json" })
	public String post() {
		// 文件保存目录路径
		String savePath = applicationGlobals.getServletContext().getRealPath("/uploadImages/editor/") + "\\";

		// 文件保存目录UR
		String saveUrl = getRequest().getContextPath() + "/uploadImages/editor/";

		File uploadDir = new File(savePath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		Map<String, Object> fileInfo = JQueryUtils.getFileInfo(getRequest());

		//String oldFilename = (String) fileInfo.get("filename");// 原文件名
		String fileSuffix = (String) fileInfo.get("suffix");// 文件格式
		
		String filesize = getRequest().getHeader("Content-Length");//获得文件大小
		
		System.out.println("文件大小::::::"+Integer.parseInt(filesize)/1024 + "K");
		if(Integer.parseInt(filesize)/1024 > 100){
			JSONObject json = new JSONObject();
			json.put("err", "上传错误,文件大小不能超过过100K!");
	        return json.toCompactString();
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String newFileName = df.format(new Date()) +"."+ fileSuffix;

		try {
			// 获取上传文件的输入流
			InputStream in = getRequest().getInputStream();
			// 创建一个文件输出流
			FileOutputStream out = new FileOutputStream(savePath + "\\" + newFileName);
			
			// 创建一个缓冲区
			byte[] buffer = new byte[1024];
			// 判断输入流中的数据是否已经读完的标识
			int len = 0;
			// 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
			while ((len = in.read(buffer)) > 0) {
				// 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" +
				// filename)当中
				out.write(buffer, 0, len);
			}
			// 关闭输入流
			in.close();
			out.flush();
			// 关闭输出流
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		String url = saveUrl + newFileName;
		
		try {
			JSONObject json = new JSONObject();  
	        json.put("err", "");
	        json.put("msg", url);
	        return json.toCompactString();
		} catch (Exception e) {
			JSONObject json = new JSONObject();  
	        json.put("err", "上传错误!");
	        json.put("msg", url);
	        return json.toCompactString();
		}
	
	}

	protected final HttpServletRequest getRequest() {
		return requestGlobals.getHTTPServletRequest();
	}
}
