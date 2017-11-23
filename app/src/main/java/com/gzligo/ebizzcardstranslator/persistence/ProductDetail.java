package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;

/**
 * Created by Lwd on 2017/9/8.
 */

public class ProductDetail implements Serializable{

    public static final int TYPE_IMG_COVER = 0;
    public static final int TYPE_PROPERTIES = 2;
    public static final int TYPE_IMG_DETAIL = 3;
    public static final int TYPE_IMG_DESCRIPTION = 4;

    public int type;
    public String imageUrl;
    public int imageType;
    public ProductExtraInfo.PropertiesBean properties;
    public String productName;
    public String mediaId;
    public int currency;
    public int width;
    public int height;
    public String price;
    public String key;
    public String value;

    public ProductDetail(String mediaId, int type, String imageUrl, int imageType, ProductExtraInfo.PropertiesBean properties, String productName, int currency,
                         int width, int height,String price) {
        this.mediaId = mediaId;
        this.type = type;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
        this.properties = properties;
        this.productName = productName;
        this.currency = currency;
        this.width = width;
        this.height = height;
        this.price = price;
    }
}
