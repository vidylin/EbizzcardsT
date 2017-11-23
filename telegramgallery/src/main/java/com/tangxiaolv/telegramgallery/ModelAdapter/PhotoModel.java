package com.tangxiaolv.telegramgallery.ModelAdapter;

/**
 * Created by xfast on 2017/1/11.
 */

import java.io.Serializable;

public class PhotoModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NORMAL = "normal";
    public static final int RESULT_MORE = 11;
    public static final int RESULT_SINGLE = 22;
    public static final int RESULT_SINGLE_VIDEO = 33;
    private String originalPath;
    private boolean isChecked;

    private String thumbnailPath;
    private boolean isUsed;


    private boolean isOriginal;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }

    public PhotoModel(String originalPath, boolean isChecked) {
        super();
        this.originalPath = originalPath;
        this.isChecked = isChecked;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public PhotoModel(String originalPath) {
        this.originalPath = originalPath;
    }

    public PhotoModel() {
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    @Override
    public String toString() {
        return "PhotoModel{" +
                "originalPath='" + originalPath + '\'' +
                ", isChecked=" + isChecked +
                ", thumbnailPath='" + thumbnailPath + '\'' +
                ", isOriginal=" + isOriginal +
                ", description='" + description + '\'' +
                '}';
    }
}

