package com.gzligo.ebizzcardstranslator.business.me;

import android.view.View;
import android.widget.ImageView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;

import butterknife.BindView;

/**
 * Created by ZuoJian on 2017/8/7.
 */

public class LanguageConfirmHolder extends BaseHolder<String>{

    @BindView(R.id.item_cers_iv) ImageView mCersIv;

    public LanguageConfirmHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(String data, int position) {
        loadImage(data);
    }

    @Override
    public void onRelease() {

    }

    private void loadImage(String url){
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(HttpUtils.MEDIA_HOST+url)
                .imageView(mCersIv)
                .imgWidth(mCersIv.getWidth())
                .imgHeigth(mCersIv.getHeight())
                .errorPic(R.mipmap.default_head_portrait)
                .isClearMemory(false)
                .build());
    }
}
