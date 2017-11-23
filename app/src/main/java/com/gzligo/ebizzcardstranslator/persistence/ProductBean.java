package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lwd on 2017/6/30.
 */

public class ProductBean extends BaseBean{

    private static final long serialVersionUID = 6916988483687982013L;
    /**
     * createUserID : 8076
     * create_time : 1493107951234
     * currency_id : 1
     * details : [{"detail_type":1,"picture":{"height":686,"picture_id":"group1/M00/01/58/CgAAa1j_BOyAVFcDAAEioTob-qc071.jpg","width":1024}}]
     * is_deleted : 0
     * name : uuuu
     * original_language_id : 1
     * price : 33.0
     * product_id : 58ff04ef4cfe5c0001e2cba0
     * properties : []
     */

    private String createUserID;
    private long create_time;
    private int currency_id;
    private int is_deleted;
    private String name;
    private int original_language_id;
    private String price;
    private String product_id;
    private List<DetailsBean> details;
    private List<?> properties;

    public String getCreateUserID() {
        return createUserID;
    }

    public void setCreateUserID(String createUserID) {
        this.createUserID = createUserID;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public int getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(int currency_id) {
        this.currency_id = currency_id;
    }

    public int getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOriginal_language_id() {
        return original_language_id;
    }

    public void setOriginal_language_id(int original_language_id) {
        this.original_language_id = original_language_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public List<DetailsBean> getDetails() {
        return details;
    }

    public void setDetails(List<DetailsBean> details) {
        this.details = details;
    }

    public List<?> getProperties() {
        return properties;
    }

    public void setProperties(List<?> properties) {
        this.properties = properties;
    }

    public static class DetailsBean {
        /**
         * detail_type : 1
         * picture : {"height":686,"picture_id":"group1/M00/01/58/CgAAa1j_BOyAVFcDAAEioTob-qc071.jpg","width":1024}
         */

        private int detail_type;
        private PictureBean picture;
        private MediaBean media;

        public int getDetail_type() {
            return detail_type;
        }

        public void setDetail_type(int detail_type) {
            this.detail_type = detail_type;
        }

        public PictureBean getPicture() {
            return picture;
        }

        public void setPicture(PictureBean picture) {
            this.picture = picture;
        }

        public MediaBean getMedia() {
            return media;
        }

        public void setMedia(MediaBean media) {
            this.media = media;
        }

        public static class PictureBean {
            /**
             * height : 686
             * picture_id : group1/M00/01/58/CgAAa1j_BOyAVFcDAAEioTob-qc071.jpg
             * width : 1024
             */

            private int height;
            private String picture_id;
            private int width;

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public String getPicture_id() {
                return picture_id;
            }

            public void setPicture_id(String picture_id) {
                this.picture_id = picture_id;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }
        }

        public static class MediaBean {
            /**
             * media_id : group1/M00/01/7D/CgAAC1kJkPSAEFQMABNhECtrhSM503.mp4
             * length : 1270032
             * width : 360
             * height : 480
             * duration : 13
             */

            private String media_id;
            private int length;
            private int width;
            private int height;
            private int duration;

            public String getMedia_id() {
                return media_id;
            }

            public void setMedia_id(String media_id) {
                this.media_id = media_id;
            }

            public int getLength() {
                return length;
            }

            public void setLength(int length) {
                this.length = length;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }
        }
    }
}
