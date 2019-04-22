package com.pa.fileupload.app.listener;

import com.pa.fileupload.app.utils.FileUploadAppProperties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ZXF on 2019/4/21.
 */
public class FileUploadAppListener implements ServletContextListener {
    public FileUploadAppListener() {
    }

    public void contextInitialized(ServletContextEvent sce) {
        InputStream inputStream=getClass().getClassLoader().getResourceAsStream("/upload.properties");
        Properties properties=new Properties();
        try {
            properties.load(inputStream);
            for (Map.Entry<Object, Object> prop:properties.entrySet()){
                String propertyName= (String) prop.getKey();
                String propertyValue= (String) prop.getValue();
                FileUploadAppProperties.getInstance().addProperty(propertyName,propertyValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {

    }
}
