package com.gzligo.ebizzcardstranslator.business.order;

import android.Manifest;
import android.content.Intent;
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
import com.gzligo.ebizzcardstranslator.business.call.VideoCallsActivity;
import com.gzligo.ebizzcardstranslator.business.call.VoiceCallsActivity;
import com.gzligo.ebizzcardstranslator.persistence.NewTravelTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslatorSelectedBean;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/9/12.
 */

public class CallOrderFragment extends BaseFragment<CallOrderPresenter> {
    private static final int NEW_TRAVEL_TRANSLATOR_ORDER = 0x40;
    private static final int QUERY_TRAVEL_ORDER_LIST = 0x41;
    private static final int ORDER_IS_TIME_OUT = 0x42;
    private static final int OBTAIN_TRAVEL_ORDER_SUCCESS = 0x43;
    private static final int OBTAIN_TRAVEL_ORDER_FAILED = 0x44;
    @BindView(R.id.no_order) LinearLayout noOrder;
    @BindView(R.id.call_order_rv) RecyclerView callOrderRv;
    private List<NewTravelTransOrderBean> travelTransOrderBeans;
    private CallOrderAdapter callOrderAdapter;
    private NewTravelTransOrderBean clickOneData;

    @Override
    public CallOrderPresenter createPresenter() {
        return new CallOrderPresenter(new CallOrderRepository(), this);
    }

    @Override
    public View onLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call_order, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        getPresenter().queryOrderList();
        travelTransOrderBeans = new ArrayList<>();
        callOrderAdapter = new CallOrderAdapter(travelTransOrderBeans,this);
        callOrderRv.setAdapter(callOrderAdapter);
    }

    @Override
    public void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        callOrderRv.setLayoutManager(layoutManager);
    }

    @Override
    public void initEvents() {
        callOrderAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<NewTravelTransOrderBean>() {
            @Override
            public void onItemClick(View view, int viewType, NewTravelTransOrderBean data, int position) {
                clickOneData = data;
                String[] permissions;
                if(data.getTransType()==0){
                    permissions = new String[]{Manifest.permission.RECORD_AUDIO};
                }else{
                    permissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA};
                }
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.requestPermission(permissions);
            }
        });
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case NEW_TRAVEL_TRANSLATOR_ORDER:
                getPresenter().queryOrderList();
                break;
            case QUERY_TRAVEL_ORDER_LIST:
                List<NewTravelTransOrderBean> travelTransOrderBeanList = (List<NewTravelTransOrderBean>) message.obj;
                if (null != travelTransOrderBeanList && travelTransOrderBeanList.size() > 0) {
                    callOrderRv.setVisibility(View.VISIBLE);
                    noOrder.setVisibility(View.GONE);
                    travelTransOrderBeans.clear();
                    travelTransOrderBeans.addAll(travelTransOrderBeanList);
                    callOrderAdapter.notifyDataSetChanged();
                }else{
                    callOrderRv.setVisibility(View.GONE);
                    noOrder.setVisibility(View.VISIBLE);
                }
                break;
            case ORDER_IS_TIME_OUT:
                NewTravelTransOrderBean newTravelTransOrderBean = (NewTravelTransOrderBean) message.obj;
                travelTransOrderBeans.remove(newTravelTransOrderBean);
                callOrderAdapter.notifyDataSetChanged();
                getPresenter().deleteOrder(newTravelTransOrderBean);
                if(travelTransOrderBeans.size()==0){
                    callOrderRv.setVisibility(View.GONE);
                    noOrder.setVisibility(View.VISIBLE);
                }else{
                    callOrderRv.setVisibility(View.VISIBLE);
                    noOrder.setVisibility(View.GONE);
                }
                break;
            case OBTAIN_TRAVEL_ORDER_SUCCESS:
                String voipToken = message.str;
                TravelTranslatorSelectedBean data = (TravelTranslatorSelectedBean) message.obj;
                if(data.getTransType()==0){
                    Intent intent = new Intent(getActivity(),VoiceCallsActivity.class);
                    intent.putExtra("TRAVEL_TRANS_ORDER",data);
                    intent.putExtra("COME_FROM","CallOrderFragment");
                    intent.putExtra("VOIP_TOKEN",voipToken);
                    getActivity().startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(),VideoCallsActivity.class);
                    intent.putExtra("TRAVEL_TRANS_ORDER",data);
                    intent.putExtra("COME_FROM","CallOrderFragment");
                    intent.putExtra("VOIP_TOKEN",voipToken);
                    getActivity().startActivity(intent);
                }

                break;
            case OBTAIN_TRAVEL_ORDER_FAILED:
                DialogUtils.showFailedOrderDialog(getActivity(),
                        R.layout.call_order_failed_dialog);
                break;
        }
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getUnGrabTransOrderNumber();
    }

    public void requestPermission(){
        getPresenter().requestTravelTuserObtain(clickOneData);
        travelTransOrderBeans.remove(clickOneData);
        callOrderAdapter.notifyDataSetChanged();
        if(travelTransOrderBeans.size()==0){
            callOrderRv.setVisibility(View.GONE);
            noOrder.setVisibility(View.VISIBLE);
        }else{
            callOrderRv.setVisibility(View.VISIBLE);
            noOrder.setVisibility(View.GONE);
        }
    }
}
