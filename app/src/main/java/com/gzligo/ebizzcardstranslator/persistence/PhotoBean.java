package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by ZuoJian on 2017/6/22.
 */

public class PhotoBean extends BaseBean {

    private String filePath;
    private String fileName;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
