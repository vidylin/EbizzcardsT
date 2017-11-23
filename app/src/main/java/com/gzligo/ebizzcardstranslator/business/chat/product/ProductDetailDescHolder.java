package com.gzligo.ebizzcardstranslator.business.chat.product;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/9/8.
 */

public class ProductDetailDescHolder extends BaseHolder<ProductDetail> {

    @BindView(R.id.et_key_text) TextView etKeyText;
    @BindView(R.id.et_values_text) TextView etValuesText;

    public ProductDetailDescHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(ProductDetail data, int position) {
        String desc = data.mediaId;
        if(TextUtils.isEmpty(desc)){
            etValuesText.setVisibility(View.GONE);
        }else{
            etValuesText.setVisibility(View.VISIBLE);
            etValuesText.setText(data.mediaId);
        }
    }

    @Override
    public void onRelease() {
    }
}
