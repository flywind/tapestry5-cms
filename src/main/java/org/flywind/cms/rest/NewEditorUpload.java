package org.flywind.cms.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.upload.services.MultipartDecoder;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/file")
public class NewEditorUpload {

	@Property
	private UploadedFile file;

	@Inject
	private MultipartDecoder decoder;

	@Inject
	private ApplicationGlobals applicationGlobals;

	@Inject
	private RequestGlobals requestGlobals;
	
	@Inject
	private Request request;

	/**
	 * Summernote 上传图片文件的Rest接收方法
	 * @param input
	 * @return
	 */
	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	@Produces({"application/json"})
	public String post(MultipartFormDataInput input) {
	
		//File save path
		String savePath = applicationGlobals.getServletContext().getRealPath("/uploadImages/editor/") + "\\";

		//File save file to server, back url
		String saveUrl = getRequest().getContextPath() + "/uploadImages/editor/";

		File uploadDir = new File(savePath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		
		//Get file size
		String filesize = getRequest().getHeader("Content-Length");
		
		System.out.println("文件大小::::::"+Integer.parseInt(filesize)/1024 + "K");
		if(Integer.parseInt(filesize)/1024 > 300){
			JSONObject json = new JSONObject();
			json.put("err", "Upload file error!");
			json.put("limitSize", "300K");
	        return json.toCompactString();
		}
		
		//Get file type and suffix,S-File-Type in ajax header
		String fileType = getRequest().getHeader("S-File-Type");
		String fileSuffix = fileType.substring(fileType.lastIndexOf("/")+1);
		
		//Get file name,S-File-Type in ajax header
		/*String fileName = getRequest().getHeader("S-File-Name");;
		String fileName = "";*/

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("filedata");

		for (InputPart inputPart : inputParts) {
			try {
	
				MultivaluedMap<String, String> header = inputPart.getHeaders();
				
				//You can get file name like this:
				//fileName = getFileName(header);
	
				//convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class,null);
	
				byte [] bytes = IOUtils.toByteArray(inputStream);
	
				//constructs upload file path
				/*fileName = savePath + fileName;
				/writeFile(bytes,fileName);*/
				
				//Create new file name
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				String newFileName = df.format(new Date()) +"."+ fileSuffix;
				writeFile(bytes,savePath+newFileName);
	
				JSONObject json = new JSONObject();  
		        json.put("err", "");
		        //json.put("url", saveUrl + fileName);
		        json.put("url", saveUrl + newFileName);
		        return json.toCompactString();
	
			  } catch (IOException e) {
				JSONObject json = new JSONObject();  
		        json.put("err", "Upload file error!");
		        return json.toCompactString();
			  }

		}

		return "unknown";
	
	}
	
	/**
	 * header sample
	 * {
	 * 	Content-Type=[image/png],
	 * 	Content-Disposition=[form-data; name="file"; filename="filename.extension"]
	 * }
	 **/
	//get uploaded filename, is there a easy way in RESTEasy?
	private String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	//save to somewhere
	private void writeFile(byte[] content, String filename) throws IOException {

		File file = new File(filename);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

	}

	protected final HttpServletRequest getRequest() {
		return requestGlobals.getHTTPServletRequest();
	}
	
	/**
	 * 淘宝的KISSY editor 上传图片文件的Rest接收方法
	 * @param input
	 * @return
	 */
	@POST
	@Path("/kissyupload")
	@Consumes("multipart/form-data")
	@Produces({"application/json"})
	public String upload(MultipartFormDataInput input) {

		//File save path
		String savePath = applicationGlobals.getServletContext().getRealPath("/uploadImages/editor/") + "\\";

		//File save file to server, back url
		String saveUrl = getRequest().getContextPath() + "/uploadImages/editor/";

		File uploadDir = new File(savePath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		
		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("Filedata");

		for (InputPart inputPart : inputParts) {
			try {
	
				MultivaluedMap<String, String> header = inputPart.getHeaders();
				
				//You can get file name like this:
				String fileName = getFileName(header);
				String fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
	
				//convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class,null);
	
				byte [] bytes = IOUtils.toByteArray(inputStream);
	
				//constructs upload file path
				/*fileName = savePath + fileName;
				/writeFile(bytes,fileName);*/
				
				//Create new file name
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				String newFileName = df.format(new Date()) +"."+ fileSuffix;
				writeFile(bytes,savePath+newFileName);
	
				JSONObject json = new JSONObject();  
		        json.put("status", "0");
		        json.put("imgUrl", saveUrl + newFileName);
		        return json.toCompactString();
	
			  } catch (IOException e) {
				JSONObject json = new JSONObject(); 
				json.put("status", "1");
		        json.put("error", "Upload file error!");
		        return json.toCompactString();
			  }

		}

		return "unknown";
	}
}
