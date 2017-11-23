package com.gzligo.ebizzcardstranslator.business.order;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.NewTravelTransOrderBean;

import java.util.List;

/**
 * Created by Lwd on 2017/9/13.
 */

public class CallOrderAdapter extends BaseAdapter<NewTravelTransOrderBean> {
    private IView iView;

    public CallOrderAdapter(List<NewTravelTransOrderBean> infos,IView iView) {
        super(infos);
        this.iView = iView;
    }

    @Override
    public BaseHolder<NewTravelTransOrderBean> getHolder(View v, int viewType) {
        return new CallOrderHolder(v,iView);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.fragment_call_order_adapter_item;
    }
}
