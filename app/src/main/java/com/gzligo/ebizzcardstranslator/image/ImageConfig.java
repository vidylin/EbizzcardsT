package com.gzligo.ebizzcardstranslator.image;

import android.widget.ImageView;

/**
 * Created by xfast on 2017/5/31.
 */

public class ImageConfig {
    protected String url;
    protected ImageView imageView;
    protected int placeholder;
    protected int errorPic;
    protected int imgWidth;
    protected int imgHeigth;


    public String getUrl() {
        return url;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getPlaceHolder() {
        return placeholder;
    }

    public int getErrorPic() {
        return errorPic;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public int getImgHeigth() {
        return imgHeigth;
    }
}
