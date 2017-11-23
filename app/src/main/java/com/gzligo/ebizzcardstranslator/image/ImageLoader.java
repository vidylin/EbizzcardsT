package com.gzligo.ebizzcardstranslator.image;

import android.content.Context;

/**
 * Created by xfast on 2017/5/31.
 */

public class ImageLoader {
    private BaseImageLoaderStrategy mStrategy;

    private static class Singleton {
        private static ImageLoader sInstance = new ImageLoader();
    }

    private ImageLoader() {
    }

    public static ImageLoader get() {
        return Singleton.sInstance;
    }

    public <T extends ImageConfig> void init(BaseImageLoaderStrategy<T> strategy) {
        mStrategy = strategy;
    }

    public <T extends ImageConfig> void loadImage(Context context, T config) {
        mStrategy.loadImage(context, config);
    }

    public <T extends ImageConfig> void clear(Context context, T config) {
        mStrategy.clear(context, config);
    }


}
