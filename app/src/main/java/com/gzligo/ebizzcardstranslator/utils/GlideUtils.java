package com.gzligo.ebizzcardstranslator.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;

/**
 * Created by Lwd on 2017/9/19.
 */

public class GlideUtils {

    public static void initFullScreenImg(final ImageView imageView, String path
            , BitmapTransformation bitmapTransformation,int defaultView){
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(HttpUtils.MEDIA_HOST + path)
                .imageView(imageView)
                .errorPic(defaultView)
                .cacheStrategy(0)
                .transformation(new CircleCrop())
                .build());

    }

    public static void initTransformationImg(final ImageView imageView, String path
            , BitmapTransformation bitmapTransformation,int defaultView){
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(HttpUtils.MEDIA_HOST + path)
                .imageView(imageView)
                .errorPic(defaultView)
                .cacheStrategy(0)
                .transformation(bitmapTransformation)
                .build());
    }

    public static void loadPeopleImage(String fromId, ImageView imageView, NewTransOrderBean newTransOrderBean) {
        String newFromId = newTransOrderBean.getFromUserId();
        String url;
        if (newFromId.equals(fromId)) {
            url = newTransOrderBean.getFromPortraitId();
        } else {
            url = newTransOrderBean.getToPortraitId();
        }
        initTransformationImg(imageView,url,new CustomShapeTransformation(AppManager.get().getApplication(), 39),R.mipmap.default_head_portrait);
    }

    public static void showImg(final ImageView imageView, String path
            , BitmapTransformation bitmapTransformation, int width, int height){
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(path)
                .imageView(imageView)
                .imgWidth(width)
                .imgHeigth(height)
                .cacheStrategy(0)
                .isClearMemory(false)
                .transformation(bitmapTransformation)
                .build());
    }

    public static void getThumbnail(String url, final int width, final ImageView imageView){
        Glide.with(AppManager.get().getApplication())
                .load(url)
                .thumbnail(0.1f).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                int height = ScreenUtils.getImageViewHeight(w+"*"+h, width);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                params.height = height;
                params.width = width;
                imageView.setLayoutParams(params);
                imageView.setImageDrawable(resource);
            }
        });
    }

}
