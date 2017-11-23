package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/8/4.
 */

public class ProductVideoThumbnailBean extends BaseBean {
    private String thumbnailPath;
    private String thumbnailSize;

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getThumbnailSize() {
        return thumbnailSize;
    }

    public void setThumbnailSize(String thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
    }
}
