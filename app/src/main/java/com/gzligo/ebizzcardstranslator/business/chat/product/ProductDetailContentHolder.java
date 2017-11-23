package com.gzligo.ebizzcardstranslator.business.chat.product;

import android.view.View;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;
import com.gzligo.ebizzcardstranslator.persistence.ProductExtraInfo;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/9/8.
 */

public class ProductDetailContentHolder extends BaseHolder<ProductDetail> {

    @BindView(R.id.et_key_text) TextView etKeyText;
    @BindView(R.id.et_value_text) TextView etValueText;

    public ProductDetailContentHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(ProductDetail data, int position) {
        ProductExtraInfo.PropertiesBean properties = data.properties;
        etKeyText.setText(properties.getKey());
        etValueText.setText(properties.getValue());
    }

    @Override
    public void onRelease() {

    }
}
