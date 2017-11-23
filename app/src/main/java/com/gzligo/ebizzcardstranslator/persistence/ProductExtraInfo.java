package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lwd on 2017/8/30.
 */

public class ProductExtraInfo implements Serializable{

    /**
     * createUserID : 1631
     * create_time : 1500707792373
     * currency_id : 1
     * details : [{"detail_type":1,"picture":{"height":750,"picture_id":"group1/M00/02/5B/CgAAC1lba0SAIvYeAAIITJ4qSMA074.jpg","width":750}},{"detail_type":1,"picture":{"height":400,"picture_id":"group1/M00/02/5B/CgAAC1lba0OAHXG3AADovRag4ck556.jpg","width":395}},{"detail_type":1,"picture":{"height":1125,"picture_id":"group1/M00/02/5B/CgAAC1lba0OAS7q7AADa9Y8oZ6M616.jpg","width":750}},{"detail_type":1,"picture":{"height":1150,"picture_id":"group1/M00/02/5B/CgAAC1lba0SAWuO3AAHNdc_lzKs490.jpg","width":750}},{"detail_type":1,"picture":{"height":1094,"picture_id":"group1/M00/02/5B/CgAAC1lba0SAL3L0AADhDxp5srE670.jpg","width":750}}]
     * is_deleted : 0
     * latest_time : 1500707792373
     * name : 七仙女古装服装舞蹈服演出古装表演服汉服女装影楼古装仙女古装
     * original_language_id : 1
     * price : 70.0
     * product_id : 5972fbd0a702920001fb9ab2
     * properties : [{"key":"颜色分类","value":"蓝儿,青儿,黄儿,紫儿,绿儿,头饰70元"},{"key":"尺码","value":"定做尺寸不加钱,S,M,L,XL"},{"key":"上市年份季节","value":"2015年秋季"}]
     * quantity : 0
     */

    private String createUserID;
    private long create_time;
    private int currency_id;
    private int is_deleted;
    private long latest_time;
    private String name;
    private int original_language_id;
    private String price;
    private String product_id;
    private int quantity;
    private String description;
    private List<DetailsBean> details;
    private List<PropertiesBean> properties;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public long getLatest_time() {
        return latest_time;
    }

    public void setLatest_time(long latest_time) {
        this.latest_time = latest_time;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<DetailsBean> getDetails() {
        return details;
    }

    public void setDetails(List<DetailsBean> details) {
        this.details = details;
    }

    public List<PropertiesBean> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertiesBean> properties) {
        this.properties = properties;
    }

    public static class DetailsBean implements Serializable{
        /**
         * detail_type : 1
         * picture : {"height":750,"picture_id":"group1/M00/02/5B/CgAAC1lba0SAIvYeAAIITJ4qSMA074.jpg","width":750}
         */

        private int detail_type;
        private PictureBean picture;
        private MediaBean media;

        public MediaBean getMedia() {
            return media;
        }

        public void setMedia(MediaBean media) {
            this.media = media;
        }

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

        public static class PictureBean implements Serializable{
            /**
             * height : 750
             * picture_id : group1/M00/02/5B/CgAAC1lba0SAIvYeAAIITJ4qSMA074.jpg
             * width : 750
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

        public static class MediaBean implements Serializable{

            /**
             * media_id : group1/M00/00/67/CgAAz1hY8geAYLVGAAh1vFPA49Q332.mp4
             * width : 360
             * height : 480
             * length : 0
             * duration : 5
             */

            private String media_id;
            private int width;
            private int height;
            private int length;
            private int duration;

            public String getMedia_id() {
                return media_id;
            }

            public void setMedia_id(String media_id) {
                this.media_id = media_id;
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

            public int getLength() {
                return length;
            }

            public void setLength(int length) {
                this.length = length;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }
        }
    }

    public static class PropertiesBean implements Serializable{
        /**
         * key : 颜色分类
         * value : 蓝儿,青儿,黄儿,紫儿,绿儿,头饰70元
         */

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
