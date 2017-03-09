package org.flywind.cms.util;

import java.io.BufferedReader;


import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.upload.services.UploadedFile;

/**
 * 文件处理工具类
 *
 */
public class FileUtils {
	private static Logger log = Logger.getLogger(FileUtils.class);
	private static volatile FileUtils instance;

	private FileUtils() {
	}

	public static FileUtils getinstance() {
		if (instance == null) {
			synchronized (FileUtils.class) {
				if (instance == null) {
					instance = new FileUtils();
				}
			}
		}
		return instance;
	}

	/***
	 * 获取指定目录下的所有的文件（不包括文件夹），采用了递归
	 * 
	 * @param obj
	 * @return
	 */
	public List<File> getListFiles(Object obj) {
		File directory = null;
		if (obj instanceof File) {
			directory = (File) obj;
		} else {
			directory = new File(obj.toString());
		}
		ArrayList<File> files = new ArrayList<File>();
		if (directory.isFile()) {
			files.add(directory);
			return files;
		} else if (directory.isDirectory()) {
			File[] fileArr = directory.listFiles();
			for (int i = 0; i < fileArr.length; i++) {
				File fileOne = fileArr[i];
				files.addAll(getListFiles(fileOne));
			}
		}
		return files;
	}

	/**
	 * 按行读取文件内容
	 * 
	 * @param file
	 */
	public void readFileByLines(File file) {
		BufferedReader reader = null;
		try {
			
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				if (tempString.equals("") || tempString.startsWith("#")) {
					continue;
				}

				int index = tempString.indexOf("=");
				String key = tempString.substring(0, index).trim();
				String value = tempString.substring(index + 1, tempString.length()).trim();
				log.info(key+value);
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
	public boolean fileExists(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (file.exists()) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean creatFile(String destFileName) {
		File file = new File(destFileName);
		if (file.exists()) {
			
			return false;
		}
		if (destFileName.endsWith(File.separator)) {
			
			return false;
		}
		// 判断目标文件所在的目录是否存在
		if (!file.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
		
			if (!file.getParentFile().mkdirs()) {
			
				return false;
			}
		}
		// 创建目标文件
		try {
			if (file.createNewFile()) {
				
				return true;
			} else {
				
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
	}

	/**
	 * 从指定文件中读取内容并以文本形式返回
	 * 
	 * @param path
	 * @return
	 */
	public String getFileString(String path) {
		StringBuilder buff = new StringBuilder();
		FileReader fr = null;
		// BufferedReader br = null;
		int readNum = 0;
		int len = 1024;
		char[] chars = new char[len];
		try {
			fr = new FileReader(path);
			// br = new BufferedReader(fr);
			while ((readNum = fr.read(chars, 0, len)) != -1) {
				buff.append(new String(chars, 0, readNum));
			}
		} catch (FileNotFoundException e) {
			log.error("getFileStringNotFound:" + path, e);
		} catch (IOException e) {
			log.error("getFileStringIO:" + path, e);
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
		}
		return buff.toString();
	}

	public byte[] getByteByStream(InputStream is) {
		byte[] datas = null;
		ByteArrayOutputStream baos = null;
		try {
			int size = 1024;
			byte[] tmp = new byte[size];
			baos = new ByteArrayOutputStream();
			for (int rSize = is.read(tmp, 0, size); rSize != -1; rSize = is.read(tmp, 0, size)) {
				baos.write(tmp, 0, rSize);
			}
			datas = baos.toByteArray();
		} catch (IOException e) {
			log.error("getFileStringIO:", e);
			return new byte[0];
		} finally {
			if (baos != null) {
				try {
					baos.close();
					baos = null;
				} catch (IOException e) {
				}
			}
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}
		return datas;
	}

	public List<String> getFileStringForLine(String filePath) {

		List<String> lineList = new ArrayList<String>();
		InputStream is = null;
		BufferedReader br=null;
		try {
			is = new FileInputStream(filePath);
		    br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
			String theWord = null;
			do {
				theWord = br.readLine();
				if (theWord != null && !"".equals(theWord.trim())) {
					lineList.add(theWord);
				}
			} while (theWord != null);

		} catch (IOException e) {
			log.error("getFileStringForLine:" + filePath, e);
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
				if(br !=null){
					br.close();
					br=null;
				}
			} catch (IOException e) {

			}
		}
		return lineList;
	}

	/**
	 * 获取某个文件的大小
	 * 
	 * @param filePath
	 * @return
	 */
	public long getFileSize(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (file.exists()) {
				return file.length();
			}
		} catch (Exception e) {
		}
		return 0;
	}

	public byte[] getFileBytes(String filePath) {
		byte[] bytes = new byte[0];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis = null;
		try {
			int len = 1024;
			int readlen = 0;
			byte[] tmpBytes = new byte[len];
			fis = new FileInputStream(filePath);
			while ((readlen = fis.read(tmpBytes, 0, len)) != -1) {
				baos.write(tmpBytes, 0, readlen);
			}
			bytes = baos.toByteArray();
		} catch (FileNotFoundException e) {
			log.error("getFileBytesNotFound:" + filePath, e);
		} catch (IOException e) {
			log.error("getFileBytesIO:" + filePath, e);
		} finally {
			if (baos != null) {
				try {
					baos.close();
					baos = null;
				} catch (IOException e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (IOException e) {
				}
			}
		}
		return bytes;

	}

	public void saveFile(String filePath, String content) {
		try {
			File f = new File(filePath);
			if (!f.exists()) {
				f.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public int saveFile(String filePath, byte[] bytes) {
		int buflen = 1024;
		byte[] bufBytes = new byte[buflen];
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		FileOutputStream fos = null;
		try {
			createFile(filePath);
			fos = new FileOutputStream(filePath);
			for (int i = bais.read(bufBytes, 0, buflen); i != -1; i = bais.read(bufBytes, 0, buflen)) {
				fos.write(bufBytes, 0, i);
			}
		} catch (FileNotFoundException e) {
			log.error("saveFileNOTFOUND:" + filePath, e);
		} catch (IOException e) {
			log.error("saveFileIO:" + filePath, e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (IOException e) {
				}
			}
			try {
				bais.close();
				bais = null;
			} catch (IOException e) {
			}
		}
		File file = new File(filePath);
		if (file.exists()) {
			file = null;
			return 0;
		} else {
			file = null;
			return -1;
		}
	}

	public void addToFile(String savePath, String content) {
		FileWriter fw = null;
		try {
			createFile(savePath);
			fw = new FileWriter(savePath, true);
			fw.write(content);
		} catch (IOException e) {
			log.error("addToFile:" + savePath, e);
		} finally {
			if (fw != null) {
				try {
					fw.flush();
					fw.close();
					fw = null;
				} catch (Throwable e) {
				}
			}
		}
	}

	public void createFile(String filePath) {
		filePath = filePath.replaceAll("\\\\", "/");
		String parentPath = filePath.substring(0, filePath.lastIndexOf("/"));
		File parentFile = new File(parentPath);
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error("createFile:" + filePath, e);
			}
		}
		file = null;
	}

	public void delFile(File file, long file_time) {
		if (!file.exists()) {
			return;
		}
		long lastModified;
		lastModified = file.lastModified();
		if (file_time == 0) {
			file.delete();
		} else {
			if (lastModified < (new Date().getTime() - file_time)) {
				file.delete();
			}
		}

	}

	public void delDir(String path, long file_time) {
		File dir = new File(path);
		if (dir.exists()) {
			File file = null;
			File[] files = dir.listFiles();
			for (int index = 0; index < files.length; index++) {
				file = files[index];
				if (file.isDirectory()) {
					delDir(file.getAbsolutePath(), file_time);
				} else {
					delFile(file, file_time);
				}
			}
			files = dir.listFiles();
			if (files.length == 0) {
				dir.delete();
			}
			file = null;
			files = null;
		}
		dir = null;
	}

	/**
	 * 从源文件 拷贝到目标文件
	 * 
	 * @param srcpath
	 * @param dstpath
	 * @return 0 表示目标已存在 不拷贝；1表拷贝成功
	 */
	public int copyFile(String srcpath, String dstpath) {
		int result = -9;
		// 检查参数合法
		if (srcpath == null || "".equals(srcpath)) {
			result = -1;
		}

		if (dstpath == null || "".equals(dstpath)) {
			result = -2;
		}

		File file = null;
		try {
			file = new File(dstpath);
			if (file.exists()) {
				return 0;
			}
		} catch (Exception e) {
		}

		OutputStream out = null;
		InputStream in = null;

		try {
			in = new FileInputStream(srcpath);
			out = new FileOutputStream(dstpath);
			int bytesRead = -1;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			result = 1;
		} catch (Exception ex) {
			log.error("Exception : ", ex);
		} finally {
			if (out != null) {
				try {
					out.close();
					out = null;
				} catch (Exception ex) {
					log.error("Exception : ", ex);
				}
			}
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (Exception ex) {
					log.error("Exception : ", ex);
				}
			}
		}
		return result;
	}

	/**
	 * 保存上传的文件到指定的目录
	 * 
	 * @param uploadFile
	 *            <code>File</code> 待上传的文件
	 * @param localeFile
	 *            <code>String</code> 需要保存到的本地文件
	 * 
	 * @return <code>boolean</code> true:上传成功;false:上传失败
	 */
	public static boolean uploadFileToLocale(File uploadFile, String localeFile) {
		boolean ret_bl = false;
		try {
			InputStream in = new FileInputStream(uploadFile);
			ret_bl = copyFile(in, localeFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret_bl;
	}

	public static boolean copyFile(InputStream in, String fileName) {
		boolean ret_bl = false;
		try {
			FileOutputStream os = new FileOutputStream(fileName, false);
			byte[] b = new byte[8 * 1024];
			int length = 0;
			while ((length = in.read(b)) > 0) {
				os.write(b, 0, length);
			}
			os.close();
			in.close();
			ret_bl = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret_bl;
	}
	
	/**
	 * Summernote upload image to server
	 * 
	 * @param file
	 * 		UploadedFile
	 * @param fileName
	 * 		File name
	 * @param fileSize
	 * 		File size
	 * @param fileType
	 * 		File type
	 * @param limitSize
	 * 		Limit file size,Kb
	 * @param savePath
	 * 		File save path
	 * @param saveUrl
	 * 		File save path url
	 * @return
	 * 		JSONObject {err:"Error msg",url:"Upload image path url"}
	 */
	public static final JSONObject Uploadfile(UploadedFile file, String fileName, String fileSize, String fileType, long limitSize, String savePath, String saveUrl) {
		
		File uploadDir = new File(savePath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		
		System.out.println("File size:"+Integer.parseInt(fileSize)/1024 + "K");
		if(Integer.parseInt(fileSize)/1024 > limitSize){
			JSONObject json = new JSONObject();
			json.put("err", "Upload error, file size can not exceed "+ limitSize +"Kb!");
	        return json;
		}
		
		String fileSuffix = fileType.substring(fileType.lastIndexOf("/")+1);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		
		String newFileName = df.format(new Date()) +"."+ fileSuffix;
		
		try {
			
			File copied = new File(savePath, newFileName);
			file.write(copied);
			
			String url = saveUrl + newFileName;
			
			JSONObject json = new JSONObject();  
	        json.put("err", "");
	        json.put("url", url);
	        return json;
			
		} catch (Exception e) {
			JSONObject json = new JSONObject();  
	        json.put("err", "Upload file error!");
	        return json;
		}
			
	}
}
