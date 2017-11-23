package com.gzligo.ebizzcardstranslator.business.chat.product;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;
import com.gzligo.ebizzcardstranslator.utils.CommonUtils;
import com.gzligo.ebizzcardstranslator.utils.CurrencyTypeUtil;
import com.gzligo.ebizzcardstranslator.utils.ScreenUtils;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/9/8.
 */

public class ProductDetailHeadHolder extends BaseHolder<ProductDetail> {

    @BindView(R.id.product_video_cover) ImageView productVideoCover;
    @BindView(R.id.product_video_down_progress) ProgressBar productVideoDownProgress;
    @BindView(R.id.product_img) ImageView productImg;
    @BindView(R.id.detail_name_txt) TextView detailNameTxt;
    @BindView(R.id.product_detail_price_txt) TextView productDetailPriceTxt;

    public ProductDetailHeadHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(ProductDetail data, int position) {
        int currency = data.currency;
        String currencyType = CurrencyTypeUtil.getCurrencySymbol(AppManager.get().getApplication(),currency);
        detailNameTxt.setText(data.productName);
        productDetailPriceTxt.setText(currencyType+data.price);
        if(data.imageType==1){
            productImg.setVisibility(View.GONE);
            productVideoDownProgress.setVisibility(View.GONE);
            loadImg(data.mediaId,productVideoCover,data.width,data.height);
        }else{
            productImg.setVisibility(View.VISIBLE);
            productVideoDownProgress.setVisibility(View.GONE);
            String url = CommonUtils.formatMediaUrl(data.mediaId);
            loadVideoImg(productVideoCover,url);
        }
    }

    @Override
    public void onRelease() {
    }

    private void loadImg(String url,ImageView imageView,int width,int height){
        int screenWidth = ScreenUtils.getScreenWidth(AppManager.get().getApplication());
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = screenWidth;
        params.height = screenWidth*height/width;
        imageView.setLayoutParams(params);
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(HttpUtils.MEDIA_HOST + url)
                .imageView(imageView)
                .imgWidth(screenWidth)
                .imgHeigth(params.height)
                .isClearMemory(false)
                .build());
    }

    private void loadVideoImg(final ImageView imageView, String url){
        Glide.with(AppManager.get().getApplication()).load(url).thumbnail(0.1f).into(new SimpleTarget<Drawable>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                int width = resource.getIntrinsicWidth();
                int height = resource.getIntrinsicHeight();
                int screenWidth = ScreenUtils.getScreenWidth(AppManager.get().getApplication());
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = screenWidth;
                params.height = screenWidth*height/width;
                imageView.setLayoutParams(params);
                imageView.setBackground(resource);
            }
        });
    }
}
