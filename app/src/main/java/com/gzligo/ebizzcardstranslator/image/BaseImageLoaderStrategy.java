package com.gzligo.ebizzcardstranslator.image;

import android.content.Context;

/**
 * Created by xfast on 2017/5/31.
 */

public interface BaseImageLoaderStrategy<T extends ImageConfig> {
    void loadImage(Context ctx, T config);
    void clear(Context ctx, T config);
}
