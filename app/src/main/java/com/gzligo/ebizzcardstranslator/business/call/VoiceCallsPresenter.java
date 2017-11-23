package com.gzligo.ebizzcardstranslator.business.call;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.manager.WebRtcManager;
import com.gzligo.ebizzcardstranslator.mqtt.MqttManager;
import com.gzligo.ebizzcardstranslator.mqtt.MqttSystemManager;
import com.gzligo.ebizzcardstranslator.mqtt.callback.MqttSystemCallBack;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.Token;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslatorCancelledBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslatorOutBean;
import com.gzligo.ebizzcardstranslator.utils.RxTimerUtil;

import java.util.TreeMap;

import io.reactivex.annotations.NonNull;

/**
 * Created by Lwd on 2017/9/14.
 */

public class VoiceCallsPresenter extends BasePresenter<VoiceCallsRepository> implements MqttSystemCallBack {
    private static final int FINISH_ORDER = 0x30;
    private static final int ACCEPT_ORDER = 0x31;
    private static final int REJECT_ORDER = 0x32;
    private static final int GET_LANGUAGE = 0x33;
    private static final int TRANS_OUT = 0x34;
    private static final int TRANS_CANCELLED = 0x35;
    private static final int TRANS_CONNECT_TIME_OUT = 0x36;
    private IView iView;

    public VoiceCallsPresenter(VoiceCallsRepository model,IView iView) {
        super(model);
        this.iView = iView;
        MqttSystemManager.get().registerMqttSystemVoiceCallBack(this);
    }

    //旅游翻译--结束翻译
    public void requestTravelTuserFinish(String orderId,String reason){
        getModel().requestTravelTuserFinish(orderId,reason, new BaseObserver() {
            @Override
            public void onNext(@NonNull Object o) {
                Message message = Message.obtain(iView);
                message.what = FINISH_ORDER;
                message.dispatchToIView();
            }
        });
    }

    //旅游翻译--接受翻译
    public void requestTravelTuserAccept(String orderId){
        getModel().requestTravelTuserAccept(orderId, new BaseObserver<Token>() {
            @Override
            public void onNext(@NonNull Token token) {
                Message message = Message.obtain(iView);
                message.what = ACCEPT_ORDER;
                message.str = token.getData().getVoip_token();
                message.dispatchToIView();
            }
        });
    }

    //旅游翻译--拒绝翻译
    public void requestTravelTuserReject(String orderId){
        getModel().requestTravelTuserReject(orderId, new BaseObserver() {
            @Override
            public void onNext(@NonNull Object o) {
                Message message = Message.obtain(iView);
                message.what = REJECT_ORDER;
                message.dispatchToIView();
            }
        });
    }

    @Override
    public void handlerSystemMsg(Object systemMsg) {
        if(systemMsg instanceof TravelTranslatorOutBean){
            Message message = Message.obtain(iView);
            message.what = TRANS_OUT;
            message.dispatchToIView();
            if(iView instanceof VoiceCallsActivity){
                WebRtcManager.getInstance().handUp();
            }else{
                VoipManager.get().releaseRenders();
                VoipManager.get().disconnect();
            }
        }else if(systemMsg instanceof TravelTranslatorCancelledBean){
            Message message = Message.obtain(iView);
            message.what = TRANS_CANCELLED;
            message.dispatchToIView();
        }
    }

    public void getLanguageList(){
        CommonBeanManager.getInstance().getLanguageList(new CommonBeanManager.OnLanguageListener() {
            @Override
            public void onGetLanguages(TreeMap<Integer, LanguagesBean> treeMap) {
                Message message = Message.obtain(iView);
                message.what = GET_LANGUAGE;
                message.obj = treeMap;
                message.dispatchToIView();
            }
        });
    }

    public void timeStart(){
        RxTimerUtil.timer(60000, new RxTimerUtil.IRxNext() {
            @Override
            public void doNext(long number) {
                if(iView instanceof VoiceCallsActivity){
                    WebRtcManager.getInstance().handUp();
                }else{
                    VoipManager.get().releaseRenders();
                    VoipManager.get().disconnect();
                }
                Message message = Message.obtain(iView);
                message.what = TRANS_CONNECT_TIME_OUT;
                message.dispatchToIView();
            }
        });
    }

    public void cancelTimer(){
        RxTimerUtil.cancel();
    }

    public void keepAlive(final String sessionID, final String clientId){
        RxTimerUtil.interval(60 * 1000, new RxTimerUtil.IRxNext() {
            @Override
            public void doNext(long number) {
                MqttManager.get().travelTransKeepAlive(sessionID,clientId);
            }
        });
    }
}
