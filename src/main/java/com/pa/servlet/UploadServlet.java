package com.pa.servlet;

import com.sun.org.apache.bcel.internal.generic.FADD;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * Created by ZXF on 2019/4/21.
 */
public class UploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        获取请求信息
//        InputStream inputStream = req.getInputStream();
//        Reader reader = new InputStreamReader(inputStream);
//        BufferedReader bufferedReader = new BufferedReader(reader);
//
//        String str = null;
//        while ((str = bufferedReader.readLine()) != null) {
//            System.out.println(str);
//        }
//        commons-fileupload可以解析请求，得到FileItem对象组成的List,把所有的请求信息都解析成FileItem对象

        DiskFileItemFactory factory = new DiskFileItemFactory();

        factory.setSizeThreshold(1024 * 500);
        File file = new File("d://file");
        factory.setRepository(file);

        ServletFileUpload upload = new ServletFileUpload(factory);

        upload.setSizeMax(1024 * 1024 * 5);
        try {
            List<FileItem> items = upload.parseRequest(req);
            for (FileItem fileItem : items) {
                if (fileItem.isFormField()) {
                    String name = fileItem.getFieldName();
                    String value = fileItem.getString();
                    System.out.println(name + ":" + value);
                } else {
//                    若文件是文件域保存在files
                    String fieldName = fileItem.getFieldName();
                    String fileName = fileItem.getName();
                    String contnetType = fileItem.getContentType();
                    long sizeInBytes = fileItem.getSize();
                    System.out.println(fieldName + ":" + fileName + ":" + contnetType + ":" + sizeInBytes);

                    InputStream inputStream = fileItem.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    fieldName = "d://files//" + fieldName;
                    OutputStream outputStream = new FileOutputStream(fieldName);
                    System.out.println(fileName);
                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                    outputStream.close();
                    inputStream.close();

                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }
}
