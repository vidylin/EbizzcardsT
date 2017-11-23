package com.gzligo.ebizzcardstranslator.business.order;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseFragment;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.business.MainActivity;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/5/24.
 */

public class OrderFragment extends BaseFragment<OrderPresenter> {
    public static final int NEW_TRANSLATOR_ORDER = 0x11;
    public static final int ORDER_OBTAIN_SUCCESS = 0x12;
    public static final int ORDER_OBTAIN_FAILED = 0x13;
    public static final int ORDER_OBTAIN_ROBBED = 0x14;
    public static final int ORDER_DELETE = 0x15;
    private static final int QUERY_ORDER_LIST = 0x16;
    private OrderAdapter orderAdapter;
    private List<NewTransOrderBean> newTransOrderBeanList;
    @BindView(R.id.no_order) LinearLayout noOrder;
    @BindView(R.id.order_rv) RecyclerView orderRv;

    @Override
    public OrderPresenter createPresenter() {
        return new OrderPresenter(new OrderRepository(), OrderFragment.this);
    }

    @Override
    public View onLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (null == newTransOrderBeanList) {
            newTransOrderBeanList = new ArrayList<>();
        }
        if (orderAdapter == null) {
            orderAdapter = new OrderAdapter(newTransOrderBeanList, this);
            orderRv.setAdapter(orderAdapter);
        } else {
            orderAdapter.notifyDataSetChanged();
        }
        getPresenter().queryOrderList();
    }

    @Override
    public void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        orderRv.setLayoutManager(layoutManager);
    }

    @Override
    public void initEvents() {
        orderAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<NewTransOrderBean>() {
            @Override
            public void onItemClick(View view, int viewType, NewTransOrderBean data, int position) {
                OrderHolder orderHolder = (OrderHolder) orderRv.getChildViewHolder(view);
                orderHolder.orderTimeTv.stop();
                getPresenter().requestOrderObtain(Message.obtain(OrderFragment.this,
                        new String[]{data.getOrderId()}), true, position, data);
            }
        });
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case NEW_TRANSLATOR_ORDER:
                getPresenter().queryOrderList();
                break;
            case ORDER_OBTAIN_SUCCESS:
                NewTransOrderBean data = (NewTransOrderBean) message.obj;
                if (newTransOrderBeanList.contains(data)) {
                    newTransOrderBeanList.remove(data);
                }
                orderAdapter.notifyDataSetChanged();
                break;
            case ORDER_OBTAIN_ROBBED:
            case ORDER_OBTAIN_FAILED:
                final NewTransOrderBean data1 = (NewTransOrderBean) message.obj;
                if (newTransOrderBeanList.contains(data1)) {
                    newTransOrderBeanList.remove(data1);
                    orderAdapter.notifyDataSetChanged();
                }
                DialogUtils.showFailedOrderDialog(getActivity(),
                        R.layout.call_order_failed_dialog);
                break;
            case ORDER_DELETE:
                data = (NewTransOrderBean) message.obj;
                if (newTransOrderBeanList.contains(data)) {
                    newTransOrderBeanList.remove(data);
                }
                getPresenter().deleteOrder(data);
                orderAdapter.notifyDataSetChanged();
                break;
            case QUERY_ORDER_LIST:
                List<NewTransOrderBean> list = (List<NewTransOrderBean>) message.obj;
                newTransOrderBeanList.clear();
                newTransOrderBeanList.addAll(list);
                orderAdapter.notifyDataSetChanged();
                break;
        }
        showWhich();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getPresenter().getUnGrabTransOrderNumber(mainActivity);
    }

    private void showWhich() {
        if (null != newTransOrderBeanList && newTransOrderBeanList.size() > 0) {
            noOrder.setVisibility(View.GONE);
            orderRv.setVisibility(View.VISIBLE);
        } else {
            noOrder.setVisibility(View.VISIBLE);
            orderRv.setVisibility(View.GONE);
        }
    }

}
