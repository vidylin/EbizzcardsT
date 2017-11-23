package com.gzligo.ebizzcardstranslator.business.order;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;

import java.util.List;

/**
 * Created by Lwd on 2017/6/8.
 */

public class OrderAdapter extends BaseAdapter<NewTransOrderBean> {
    private OrderHolder orderHolder;
    private IView iView;

    public OrderAdapter(List<NewTransOrderBean> newTransOrderBeen, IView iView) {
        super(newTransOrderBeen);
        this.iView = iView;
    }

    @Override
    public OrderHolder getHolder(View v, int viewType) {
        orderHolder = new OrderHolder(v, iView);
        return orderHolder;
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.fragment_order_adapter_item;
    }

}
