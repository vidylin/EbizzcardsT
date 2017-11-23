package com.gzligo.ebizzcardstranslator.business.chat.product;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;

import java.util.List;

/**
 * Created by Lwd on 2017/9/8.
 */

public class ProductDetailAdapter extends BaseAdapter<ProductDetail> {

    private List<ProductDetail> mDetailList;

    public ProductDetailAdapter(List<ProductDetail> mDetailList) {
        super(mDetailList);
        this.mDetailList = mDetailList;
    }

    @Override
    public int getItemViewType(int position) {
        return mDetailList.get(position).type;
    }

    @Override
    public BaseHolder<ProductDetail> getHolder(View v, int viewType) {
        switch (viewType) {
            case ProductDetail.TYPE_IMG_COVER:
                return new ProductDetailHeadHolder(v);
            case ProductDetail.TYPE_PROPERTIES:
                return new ProductDetailContentHolder(v);
            case ProductDetail.TYPE_IMG_DETAIL:
                return new ProductDetailImageHolder(v);
            case ProductDetail.TYPE_IMG_DESCRIPTION:
                return new ProductDetailDescHolder(v);
            default:
                return new ProductDetailHeadHolder(v);
        }
    }

    @Override
    public int getLayoutResId(int viewType) {
        switch (viewType) {
            case ProductDetail.TYPE_IMG_COVER:
                return R.layout.product_adapter_item_head;
            case ProductDetail.TYPE_PROPERTIES:
                return R.layout.product_adapter_item_content;
            case ProductDetail.TYPE_IMG_DETAIL:
                return R.layout.product_adapter_item_image;
            case ProductDetail.TYPE_IMG_DESCRIPTION:
                return R.layout.product_adapter_item_detail;
            default:
                return R.layout.product_adapter_item_head;
        }
    }
}
