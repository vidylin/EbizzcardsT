package com.gzligo.ebizzcardstranslator.image.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.gzligo.ebizzcardstranslator.image.BaseImageLoaderStrategy;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xfast on 2017/5/31.
 */

public class GlideImageLoaderStrategy implements BaseImageLoaderStrategy<GlideImageConfig> {
    @Override
    public void loadImage(Context context, GlideImageConfig config) {
        if (context == null) throw new IllegalStateException("Context is required");
        if (config == null) throw new IllegalStateException("GlideImageConfig is required");
        if (TextUtils.isEmpty(config.getUrl())) throw new IllegalStateException("url is required");
        if (config.getImageView() == null) throw new IllegalStateException("imageview is required");

        RequestBuilder<Drawable> requestBuilder = Glide.with(context)
                .load(config.getUrl())
                .transition(new DrawableTransitionOptions().crossFade(0));

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.NONE);

        if (config.getTransformation() != null) {
            options.transform(config.getTransformation());
        }

        switch (config.getCacheStrategy()) {
            case 0:
                options.diskCacheStrategy(DiskCacheStrategy.ALL);
                break;
            case 1:
                options.diskCacheStrategy(DiskCacheStrategy.NONE);
                break;
            case 2:
                options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                break;
            case 3:
                options.diskCacheStrategy(DiskCacheStrategy.DATA);
                break;
        }

        if (config.getPlaceHolder() != 0) {
            options.placeholder(config.getPlaceHolder());
        }

        if (config.getErrorPic() != 0) {
            options.error(config.getErrorPic());
        }

        if (config.getImgHeigth() != 0 && config.getImgWidth() != 0) {
            options.override(config.getImgWidth(),config.getImgHeigth());
        }

        requestBuilder.apply(options);
        requestBuilder.into(config.getImageView());
    }

    @Override
    public void clear(final Context context, GlideImageConfig config) {
        if (context == null) throw new IllegalStateException("Context is required");
        if (config == null) throw new IllegalStateException("GlideImageConfig is required");

        if (config.getImageViews() != null && config.getImageViews().length > 0) {//取消在执行的任务并且释放资源
            for (ImageView imageView : config.getImageViews()) {
                Glide.with(context).clear(imageView);
            }
        }

        if (config.getTargets() != null && config.getTargets().length > 0) {//取消在执行的任务并且释放资源
            for (Target target : config.getTargets())
                Glide.with(context).clear(target);
        }


        if (config.isClearDiskCache()) {//清除本地缓存
            Observable.just(0)
                    .observeOn(Schedulers.io())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(@NonNull Integer integer) throws Exception {
                            Glide.get(context).clearDiskCache();
                        }
                    });
        }

        if (config.isClearMemory()) {//清除内存缓存
            Glide.get(context).clearMemory();
        }
    }
}
