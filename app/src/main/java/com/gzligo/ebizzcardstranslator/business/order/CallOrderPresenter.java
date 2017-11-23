package com.gzligo.ebizzcardstranslator.business.order;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.mqtt.MqttSystemManager;
import com.gzligo.ebizzcardstranslator.mqtt.callback.MqttSystemCallBack;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.NewTravelTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.Token;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslatorSelectedBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Lwd on 2017/9/13.
 */

public class CallOrderPresenter extends BasePresenter<CallOrderRepository> implements MqttSystemCallBack {
    private static final int NEW_TRAVEL_TRANSLATOR_ORDER = 0x40;
    private static final int QUERY_TRAVEL_ORDER_LIST = 0x41;
    private static final int OBTAIN_TRAVEL_ORDER_SUCCESS = 0x43;
    private static final int OBTAIN_TRAVEL_ORDER_FAILED = 0x44;
    private IView iView;

    public CallOrderPresenter(CallOrderRepository model, IView iView) {
        super(model);
        MqttSystemManager.get().registerMqttSystemVoiceOrderCallBack(this);
        this.iView = iView;
    }

    public void requestTravelTuserObtain(final NewTravelTransOrderBean data) {
        getModel().requestTravelTuserObtain(data.getSession_id(), new BaseObserver<Token>() {
            @Override
            public void onNext(@NonNull Token token) {
                Message message = Message.obtain(iView);
                if(token.getError()==0){
                    List<Integer> list = new ArrayList<>();
                    list.add(data.getLanguageFromId());
                    list.add(data.getLanguageToId());
                    TravelTranslatorSelectedBean travelTranslatorSelectedBean = new TravelTranslatorSelectedBean();
                    travelTranslatorSelectedBean.setTransType(data.getTransType());
                    travelTranslatorSelectedBean.setSelectTime(data.getStartTime());
                    travelTranslatorSelectedBean.setPortrait(data.getPortrait());
                    travelTranslatorSelectedBean.setUserName(data.getUserName());
                    travelTranslatorSelectedBean.setSessionId(data.getSession_id());
                    travelTranslatorSelectedBean.setFromUserId(data.getFromUserId());
                    travelTranslatorSelectedBean.setLanguageIds(list);
                    message.obj = travelTranslatorSelectedBean;
                    message.str = token.getData().getVoip_token();
                    message.what = OBTAIN_TRAVEL_ORDER_SUCCESS;
                }else{
                    message.what = OBTAIN_TRAVEL_ORDER_FAILED;
                }
                deleteOrder(data);
                message.dispatchToIView();
            }
        });
    }

    public void queryOrderList() {
        getModel().queryOrderList().subscribe(new Consumer<List<NewTravelTransOrderBean>>() {
            @Override
            public void accept(@NonNull List<NewTravelTransOrderBean> newTravelTransOrderBeen) throws Exception {
                Message message = Message.obtain(iView);
                message.what = QUERY_TRAVEL_ORDER_LIST;
                message.obj = newTravelTransOrderBeen;
                message.dispatchToIView();
            }
        });
    }

    public void deleteOrder(NewTravelTransOrderBean newTransOrderBean) {
        getModel().deleteOrder(newTransOrderBean);
    }

    @Override
    public void handlerSystemMsg(Object systemMsg) {
        if(systemMsg instanceof NewTravelTransOrderBean){
            Message message = Message.obtain(iView);
            message.what = NEW_TRAVEL_TRANSLATOR_ORDER;
            message.obj = systemMsg;
            message.dispatchToIView();
        }
    }
}
