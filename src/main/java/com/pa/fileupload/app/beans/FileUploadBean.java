package com.pa.fileupload.app.beans;

/**
 * Created by ZXF on 2019/4/21.
 */
public class FileUploadBean {
    private Integer id;
    // 文件名
    private String fileName;
    // 文件的路径
    private String filePath;
    // 文件的描述
    private String fileDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc;
    }

    public FileUploadBean(String fileName, String filePath, String fileDesc) {
        super();
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileDesc = fileDesc;
    }

    public FileUploadBean() {
        // TODO Auto-generated constructor stub
    }
}
