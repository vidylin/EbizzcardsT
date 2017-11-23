package com.gzligo.ebizzcardstranslator.business.wallet;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageConfig;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/7/13.
 */

public class MyBankCardHolder extends BaseHolder<CardInfo> {
    @BindView(R.id.bank_img) ImageView bankImg;
    @BindView(R.id.bank_name_tv) TextView bankNameTv;
    @BindView(R.id.bank_name_type) TextView bankNameType;
    @BindView(R.id.bank_num_tv) TextView bankNumTv;
    @BindView(R.id.check_img) ImageView checkImg;

    private int pos;

    public MyBankCardHolder(View itemView, int pos) {
        super(itemView);
        this.pos = pos;
    }

    @Override
    public void setData(CardInfo data, int position) {
        checkImg.setVisibility(position == pos ? View.VISIBLE : View.GONE);
        String bankNum = data.getBank_card_no();
        int len = bankNum.length();
        bankNumTv.setText(len > 4 ? data.getBank_card_no().substring(len - 4, len) : bankNum);
        bankNameTv.setText(data.getBankCardNameZh());
        loadImage(data.getCardPortrait(), bankImg);
    }

    @Override
    public void onRelease() {
    }

    private void loadImage(String url, ImageView imageView) {
        ImageLoader.get().loadImage(AppManager.get().getApplication(), GlideImageConfig
                .builder()
                .url(HttpUtils.MEDIA_HOST + url)
                .imageView(imageView)
                .errorPic(R.mipmap.default_head_portrait)
                .isClearMemory(false)
                .build());
    }
}
