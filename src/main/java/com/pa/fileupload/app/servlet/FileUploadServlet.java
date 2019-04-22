package com.pa.fileupload.app.servlet;

import com.pa.fileupload.app.beans.FileUploadBean;
import com.pa.fileupload.app.db.UploadFileDao;
import com.pa.fileupload.app.exception.InvalidExtNameException;
import com.pa.fileupload.app.utils.FileUploadAppProperties;
import com.pa.fileupload.app.utils.FileUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Created by ZXF on 2019/4/21.
 */
public class FileUploadServlet extends HttpServlet {

    private static final String FILE_PATH = "/WEB-INF/files/";

    private static final String TEMP_DIR = "D:\\upload\\temporary";

    private UploadFileDao uploadFileDao=new UploadFileDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path=null;
        ServletFileUpload upload = getServletFileUpload();
        try {
//            把需要上传的FileItem都放到Map中，键：文件的待存放路径 值对应的FileItem对象
            Map<String, FileItem> uploadFiles = new HashMap<String, FileItem>();
//            解析请求，得到FileItem集合
            List<FileItem> items = upload.parseRequest(req);
//            构建FileUploadBean的集合,同时填充uploadFiles
            List<FileUploadBean> beans = buildFileUploadBeans(items, uploadFiles);
//            校验扩展名
            vaidateExtName(beans);
//            校验文件大小，在解析是已经校验，我们只需要通过异常得到结果
//            进行文件的上传操作
            upload(uploadFiles);
//            上传信息保存在数据库中
            saveBeans(beans);
//            //6. 删除临时文件夹的临时文件
//            FileUtils.delAllFile(TEMP_DIR);

            path="/success.jsp";

        } catch (Exception e) {
            e.printStackTrace();
            path="/upload.jsp";
            req.setAttribute("message",e.getMessage());
        }
        req.getRequestDispatcher(path).forward(req,resp);
    }

    private void saveBeans(List<FileUploadBean> beans) {
        uploadFileDao.save(beans);
    }

    /**
     * 文件上传的 IO 方法.
     *
//     * @param filePath
//     * @param inputStream
     * @throws IOException
     */
    private void upload(Map<String, FileItem> uploadFiles) throws IOException {
        for (Map.Entry<String, FileItem> uploadFile : uploadFiles.entrySet()) {
            String filePath=uploadFile.getKey();
            FileItem item=uploadFile.getValue();

            upload(filePath,item.getInputStream());
        }
    }

    private void upload(String filePath, InputStream inputStream) throws IOException {
        OutputStream outputStream=new FileOutputStream(filePath);
        byte [] buffer=new byte[1024];
        int len=0;
        while ((len=inputStream.read(buffer))!=-1){
            outputStream.write(buffer,0,len);
        }
        inputStream.close();
        outputStream.close();
    }

    /**
     * 校验扩展名是否合法
     * @param beans:
     */
    private void vaidateExtName(List<FileUploadBean> beans) {
        String exts = FileUploadAppProperties.getInstance().getProperty("exts");
        List<String> extList = Arrays.asList(exts.split(","));
        System.out.println(extList);

        for(FileUploadBean bean: beans){
            String fileName = bean.getFileName();
            System.out.println(fileName.indexOf("."));

            String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
            System.out.println(extName);

            if(!extList.contains(extName)){
                throw new InvalidExtNameException(fileName + "文件的扩展名不合法");
            }
        }
    }

    /**
     * 利用传入的 FileItem 的集合, 构建 FileUploadBean 的集合, 同时填充 uploadFiles
     *
     * FileUploadBean 对象封装了: id, fileName, filePath, fileDesc
     * uploadFiles: Map<String, FileItem> 类型, 存放文件域类型的  FileItem. 键: 待保存的文件的名字 ,值: FileItem 对象
     *
     * 构建过程:
     * 1. 对传入 FileItem 的集合进行遍历. 得到 desc 的那个 Map. 键: desc 的 fieldName(desc1, desc2 ...).
     * 值: desc 的那个输入的文本值
     *
     * 2. 对传入 FileItem 的集合进行遍历. 得到文件域的那些 FileItem 对象, 构建对应的 key (desc1 ....) 来获取其 desc.
     * 构建的 FileUploadBean 对象, 并填充 beans 和 uploadFiles
     *
     * @param items
     * @param uploadFiles
     * @return
     * @throws UnsupportedEncodingException
     */
    private List<FileUploadBean> buildFileUploadBeans(List<FileItem> items, Map<String, FileItem> uploadFiles) throws UnsupportedEncodingException {
        List<FileUploadBean> beans = new ArrayList<FileUploadBean>();

        Map<String, String> descs = new HashMap<String, String>();

        for (int i = 0; i < items.size(); i++) {
            FileItem item = items.get(i);

            if (item.isFormField()) {
                //desc1 或 desc2 ...
                String fieldName = item.getFieldName();
                String desc = item.getString("UTF-8");

                descs.put(fieldName, desc);
            }
        }

        for (int i = 0; i < items.size(); i++) {
            FileItem item = items.get(i);
            FileUploadBean bean = null;
            if (!item.isFormField()) {
                String fieldName = item.getFieldName();
                String descName = "desc" + fieldName.substring(fieldName.length() - 1);
                String desc = descs.get(descName);

                //对应文件名
                String fileName = item.getName();
                String filePath = getFilePath(fileName);

                bean = new FileUploadBean(fileName, filePath, desc);
                beans.add(bean);

                uploadFiles.put(bean.getFilePath(), item);
            }
        }

        return beans;
    }
    /**
     * 根据跟定的文件名构建一个随机的文件名
     * 1. 构建的文件的文件名的扩展名和给定的文件的扩展名一致
     * 2. 利用 ServletContext 的 getRealPath 方法获取的绝对路径
     * 3. 利用了 Random 和 当前的系统时间构建随机的文件的名字
     *
     * @param fileName
     * @return
     */
    private String getFilePath(String fileName) {
        String extName = fileName.substring(fileName.lastIndexOf("."));
        Random random = new Random();

        String filePath = getServletContext().getRealPath(FILE_PATH) + "\\" + System.currentTimeMillis() + random.nextInt(100000) + extName;
        return filePath;
    }
    /**
     * 构建 ServletFileUpload 对象
     * 从配置文件中读取了部分属性, 用户设置约束.
     * 该方法代码来源于文档.
     * @return
     */
    private ServletFileUpload getServletFileUpload() {
        String exts = FileUploadAppProperties.getInstance().getProperty("exts");
        String fileMaxSize = FileUploadAppProperties.getInstance().getProperty("file.max.size");
        String totalFileMaxSize = FileUploadAppProperties.getInstance().getProperty("total.file.max.size");

//        System.out.println(exts);

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1024 * 500);
        File file = new File("d://upload/temporary");
        factory.setRepository(file);

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(Long.parseLong(totalFileMaxSize));
        upload.setFileSizeMax(Long.parseLong(fileMaxSize));
        return upload;
    }
}
