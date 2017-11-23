package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/6/9.
 */

public class MessageContentImage extends BaseBean{
    private boolean original;
    private String originalObjId;
    private long imgLength;
    private String mediumObjId;
    private String fileName;
    private int thumbLength;
    private String hdKey;
    private String width;
    private String height;
    private String thumbnailObjId;
    private long hdLenght;
    private String descriptions;
    private String privateToNickname;
    private boolean isPublicImage;

    public boolean getIsPublicImage() {
        return isPublicImage;
    }

    public void setIsPublicImage(boolean isPublicImage) {
        this.isPublicImage = isPublicImage;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public long getImgLength() {
        return imgLength;
    }

    public void setImgLength(long imgLength) {
        this.imgLength = imgLength;
    }

    public String getOriginalObjId() {
        return originalObjId;
    }

    public void setOriginalObjId(String originalObjId) {
        this.originalObjId = originalObjId;
    }

    public String getMediumObjId() {
        return mediumObjId;
    }

    public void setMediumObjId(String mediumObjId) {
        this.mediumObjId = mediumObjId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public int getThumbLength() {
        return thumbLength;
    }

    public void setThumbLength(int thumbLength) {
        this.thumbLength = thumbLength;
    }

    public long getHdLenght() {
        return hdLenght;
    }

    public void setHdLenght(long hdLenght) {
        this.hdLenght = hdLenght;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getThumbnailObjId() {
        return thumbnailObjId;
    }

    public void setThumbnailObjId(String thumbnailObjId) {
        this.thumbnailObjId = thumbnailObjId;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getPrivateToNickname() {
        return privateToNickname;
    }

    public void setPrivateToNickname(String privateToNickname) {
        this.privateToNickname = privateToNickname;
    }

    public String getHdKey() {
        return hdKey;
    }

    public void setHdKey(String hdKey) {
        this.hdKey = hdKey;
    }

    @Override
    public String toString() {
        return "MessageContentImage{" +
                "original=" + original +
                ", originalObjId='" + originalObjId + '\'' +
                ", imgLength=" + imgLength +
                ", mediumObjId='" + mediumObjId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", thumbLength=" + thumbLength +
                ", hdKey='" + hdKey + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", thumbnailObjId='" + thumbnailObjId + '\'' +
                ", hdLenght=" + hdLenght +
                ", descriptions='" + descriptions + '\'' +
                ", privateToNickname='" + privateToNickname + '\'' +
                ", isPublicImage='" + isPublicImage + '\'' +
                '}';
    }
}
