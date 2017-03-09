package org.flywind.cms.listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.flywind.business.common.utils.PropertiesUtils;
import org.flywind.cms.util.FileUtils;

public class ConfigLoadListener implements ServletContextListener 
{
	private static final Logger logger = Logger.getLogger(ConfigLoadListener.class);
	
	private static final String fileName = "local.properties";
	private static final String CONF_DIR="conf.dir";
	private static final String CON_DIR_DEVELOP="conf.dir.develop";
    private static final String PROPERTIE_END=".properties";
	private static Properties properties;
	private static Properties config ;
	static {
		properties = new Properties();
		config = new Properties();
		try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		String path = (String) properties.get(CONF_DIR);
		String developPath = (String) properties.get(CON_DIR_DEVELOP);
		logger.debug("config file path is:"+path);
		logger.debug("config file develop path is:"+developPath);
		File dirname = new File(path); 
		if (!dirname.isDirectory()) 
		{
			//目录不存在 
		     dirname = new File(developPath);
		     if(!dirname.isDirectory()){
		    	 return;
		     }else{
		    	 path = developPath;
		     }
		}
		
		List<File> fileList= FileUtils.getinstance().getListFiles(path);
		for(File file:fileList){
			if(file.getName().endsWith(PROPERTIE_END)){
				readFileByLines(file);
			}
		}
		
		PropertiesUtils.setProperties(config);
		PropertiesUtils.getProperties().setProperty("protocolJsVersion", new Date().getTime()+"");
		//自动刷新配置
		//autoReloadConfig(path);
		
	}
	
	/**
	 * 读取配置
	 * @param file
	 */
	private void readFileByLines(File file) {	
        BufferedReader reader = null;
        logger.debug("begin read peoperties file name:"+file.getName());
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	if(tempString.equals("") || tempString.startsWith("#")){
            		continue;
            	}
                int index = tempString.indexOf("=");
                if(index < 0){
                	continue;
                }
                String key = tempString.substring(0, index).trim();
                String value = tempString.substring(index+1, tempString.length()).trim();   
                //将配置项写入JVM系统变量，使spring配置文件可以使用占位符
                logger.debug("read peopertis key:"+key+" value:"+value);
                System.getProperties().setProperty(key, value); 
                config.put(key, value); 
            }
        } catch (IOException e) {
        	logger.error("read file error file name is:"+file.getName()+" ERROR message is:"+e.getMessage());
            e.printStackTrace();
            
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1){
                	e1.printStackTrace();
                }
            }
        }
    }

}
