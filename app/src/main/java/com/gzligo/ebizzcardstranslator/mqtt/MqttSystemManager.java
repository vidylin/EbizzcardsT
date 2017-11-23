package com.gzligo.ebizzcardstranslator.mqtt;

import android.content.Intent;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.business.MainCallBack;
import com.gzligo.ebizzcardstranslator.business.call.VideoCallsActivity;
import com.gzligo.ebizzcardstranslator.business.call.VoiceCallsActivity;
import com.gzligo.ebizzcardstranslator.db.manager.OrderDbManager;
import com.gzligo.ebizzcardstranslator.db.manager.TravelTransOrderManager;
import com.gzligo.ebizzcardstranslator.mqtt.callback.MqttSystemCallBack;
import com.gzligo.ebizzcardstranslator.mqtt.protobuf.MQTTProtobufMsg;
import com.gzligo.ebizzcardstranslator.notification.NotificationManager;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTravelTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslatorCancelledBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslatorOutBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslatorSelectedBean;
import com.gzligo.ebizzcardstranslator.utils.CommonUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lwd on 2017/6/8.
 */

public class MqttSystemManager {
    private MqttSystemCallBack mqttSystemCallBack;
    private MqttSystemCallBack mqttSystemVoiceOrderCallBack;
    private MqttSystemCallBack mqttSystemVoiceCallBack;
    private MainCallBack mainCallBack;
    private OrderDbManager orderDbManager;
    private TravelTransOrderManager travelTransOrderManager;

    private MqttSystemManager(){
        orderDbManager = new OrderDbManager();
        travelTransOrderManager = new TravelTransOrderManager();
    }

    private static class Singleton {
        private static MqttSystemManager sInstance = new MqttSystemManager();
    }

    public static MqttSystemManager get() {
        return Singleton.sInstance;
    }

    public void registerMqttSystemCallBack(MqttSystemCallBack mqttSystemCallBack){
        this.mqttSystemCallBack = mqttSystemCallBack;
    }

    public void registerMqttSystemVoiceOrderCallBack(MqttSystemCallBack mqttSystemVoiceOrderCallBack){
        this.mqttSystemVoiceOrderCallBack = mqttSystemVoiceOrderCallBack;
    }

    public void registerMqttSystemVoiceCallBack(MqttSystemCallBack mqttSystemVoiceCallBack){
        this.mqttSystemVoiceCallBack = mqttSystemVoiceCallBack;
    }

    public void registerMainCallBack(MainCallBack mainCallBack){
        this.mainCallBack = mainCallBack;
    }

    public void handlerSystemMsg(MQTTProtobufMsg.System systemMsg){
        if(systemMsg.hasNewTransOrder()){
            MQTTProtobufMsg.NewTransOrder newTransOrder = systemMsg.getNewTransOrder();
            if (MqttDupMsgUtils.getInstance().duplicate("system", newTransOrder.getOrderId())) {
                return;
            }
            NewTransOrderBean newTransOrderBean =new NewTransOrderBean();
            newTransOrderBean.setDesc(newTransOrder.getDesc());
            newTransOrderBean.setDuration(newTransOrder.getDuration());
            newTransOrderBean.setOrderId(newTransOrder.getOrderId());
            newTransOrderBean.setStart(newTransOrder.getStart());
            MQTTProtobufMsg.TransOrderUser transOrderUser = newTransOrder.getUser1();
            newTransOrderBean.setFromName(transOrderUser.getUser().getNickname());
            newTransOrderBean.setFromLangId(transOrderUser.getLanguage());
            newTransOrderBean.setFromPortraitId(transOrderUser.getUser().getPortrait());
            newTransOrderBean.setFromUserId(transOrderUser.getUser().getUid());
            MQTTProtobufMsg.TransOrderUser transOrderUser2 = newTransOrder.getUser2();
            newTransOrderBean.setToName(transOrderUser2.getUser().getNickname());
            newTransOrderBean.setToLangId(transOrderUser2.getLanguage());
            newTransOrderBean.setToPortraitId(transOrderUser2.getUser().getPortrait());
            newTransOrderBean.setToUserId(transOrderUser2.getUser().getUid());
            newTransOrderBean.setEffectiveTime(newTransOrder.getStart()+newTransOrder.getDuration()*1000);
            insertOrder(newTransOrderBean);
            if(CommonUtils.isBackground(AppManager.get().getApplication())){
                NotificationManager.getInstance().notificationNewMessage(
                        AppManager.get().getApplication().getResources().getString(R.string.order_is_new),0);
            }
        }else if(systemMsg.hasNewTravelTransOrder()){
            MQTTProtobufMsg.NewTravelTransOrder newTravelTransOrder =  systemMsg.getNewTravelTransOrder();
            if (MqttDupMsgUtils.getInstance().duplicate("system", newTravelTransOrder.getSessionId())) {
                return;
            }
            NewTravelTransOrderBean newTravelTransOrderBean = new NewTravelTransOrderBean();
            newTravelTransOrderBean.setSession_id(newTravelTransOrder.getSessionId());
            MQTTProtobufMsg.UserInfoOrBuilder userInfoOrBuilder = newTravelTransOrder.getUserInfo();
            newTravelTransOrderBean.setFromUserId(userInfoOrBuilder.getUid());
            newTravelTransOrderBean.setUserName(userInfoOrBuilder.getNickname());
            newTravelTransOrderBean.setPortrait(userInfoOrBuilder.getPortrait());
            newTravelTransOrderBean.setDuration(newTravelTransOrder.getDuration());
            newTravelTransOrderBean.setStartTime(newTravelTransOrder.getStartTime());
            newTravelTransOrderBean.setDesc(newTravelTransOrder.getDesc());
            newTravelTransOrderBean.setEffectTime(newTravelTransOrder.getStartTime()+newTravelTransOrder.getDuration()*1000);
            if(MQTTProtobufMsg.LGTravelTransType.TRAVEL_VOICE == newTravelTransOrder.getType()){
                newTravelTransOrderBean.setTransType(0);
            }else{
                newTravelTransOrderBean.setTransType(1);
            }

            newTravelTransOrderBean.setLanguageFromId(newTravelTransOrder.getLanguageIds(0));
            newTravelTransOrderBean.setLanguageToId(newTravelTransOrder.getLanguageIds(1));
            if(CommonUtils.isBackground(AppManager.get().getApplication())){
                NotificationManager.getInstance().notificationNewMessage(
                        AppManager.get().getApplication().getResources().getString(R.string.order_is_new),0);
            }
            insertTravelTransOrder(newTravelTransOrderBean);
        }else if(systemMsg.hasTravelTranslatorSelected()){
            MQTTProtobufMsg.TravelTranslatorSelected travelTranslatorSelected = systemMsg.getTravelTranslatorSelected();
            if (MqttDupMsgUtils.getInstance().duplicate("system", travelTranslatorSelected.getSessionId())) {
                return;
            }
            TravelTranslatorSelectedBean travelTranslatorSelectedBean = new TravelTranslatorSelectedBean();
            travelTranslatorSelectedBean.setSessionId(travelTranslatorSelected.getSessionId());
            MQTTProtobufMsg.UserInfoOrBuilder userInfoOrBuilder = travelTranslatorSelected.getUserInfo();
            travelTranslatorSelectedBean.setFromUserId(userInfoOrBuilder.getUid());
            travelTranslatorSelectedBean.setUserName(userInfoOrBuilder.getNickname());
            travelTranslatorSelectedBean.setPortrait(userInfoOrBuilder.getPortrait());
            travelTranslatorSelectedBean.setLanguageIds(travelTranslatorSelected.getLanguageIdsList());
            travelTranslatorSelectedBean.setSelectTime(travelTranslatorSelected.getSelectTime());
            if(MQTTProtobufMsg.LGTravelTransType.TRAVEL_VOICE == travelTranslatorSelected.getType()){
                travelTranslatorSelectedBean.setTransType(0);
                Intent intent = new Intent(AppManager.get().getApplication(),VoiceCallsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("TRAVEL_TRANS_ORDER",travelTranslatorSelectedBean);
                intent.putExtra("COME_FROM","System_server");
                AppManager.get().getApplication().startActivity(intent);
            }else{
                travelTranslatorSelectedBean.setTransType(1);
                Intent intent = new Intent(AppManager.get().getApplication(),VideoCallsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("TRAVEL_TRANS_ORDER",travelTranslatorSelectedBean);
                intent.putExtra("COME_FROM","System_server");
                AppManager.get().getApplication().startActivity(intent);
            }
        }else if(systemMsg.hasTravelTranslatorOut()){//旅游翻译官退出
            TravelTranslatorOutBean travelTranslatorOutBean = new TravelTranslatorOutBean();
            MQTTProtobufMsg.TravelTranslatorOut travelTranslatorOut = systemMsg.getTravelTranslatorOut();
            if(MQTTProtobufMsg.LGTravelTransOutReason.TRAVEL_QUIT==travelTranslatorOut.getReason()){
                travelTranslatorOutBean.setReason(0);//翻译主动退出
            }else if(MQTTProtobufMsg.LGTravelTransOutReason.TRAVEL_TIMEOUT==travelTranslatorOut.getReason()){
                travelTranslatorOutBean.setReason(1);//多久时间没消息
            }else if(MQTTProtobufMsg.LGTravelTransOutReason.TRAVEL_END==travelTranslatorOut.getReason()){
                travelTranslatorOutBean.setReason(2);//客户主动结束
            }else if(MQTTProtobufMsg.LGTravelTransOutReason.TRAVEL_NO_BALANCE==travelTranslatorOut.getReason()){
                travelTranslatorOutBean.setReason(3);//没有余额
            }else if(MQTTProtobufMsg.LGTravelTransOutReason.TRAVEL_EXCEPTION==travelTranslatorOut.getReason()){
                travelTranslatorOutBean.setReason(4);//异常情况
            }else if(MQTTProtobufMsg.LGTravelTransOutReason.TRAVEL_REPLACE==travelTranslatorOut.getReason()){
                travelTranslatorOutBean.setReason(5);//被选择替换掉，挤掉
            }
            travelTranslatorOutBean.setSessionId(travelTranslatorOut.getSessionId());
            travelTranslatorOutBean.setDuration(travelTranslatorOut.getDuration());
            travelTranslatorOutBean.setOutTime(travelTranslatorOut.getOutTime());
            if(null!=mqttSystemVoiceCallBack){
                mqttSystemVoiceCallBack.handlerSystemMsg(travelTranslatorOutBean);
            }
        }else if(systemMsg.hasTravelTransOrderCancelled()){
            TravelTranslatorCancelledBean travelTranslatorCancelledBean = new TravelTranslatorCancelledBean();
            MQTTProtobufMsg.TravelTransOrderCancelled travelTransOrderCancelled = systemMsg.getTravelTransOrderCancelled();
            travelTranslatorCancelledBean.setSessionId(travelTransOrderCancelled.getSessionId());
            travelTranslatorCancelledBean.setUid(travelTransOrderCancelled.getUid());
            if(MQTTProtobufMsg.LGTravelCancelType.TRAVEL_INITIATIVE ==travelTransOrderCancelled.getType()){
                travelTranslatorCancelledBean.setCancelType(0);
            }else if(MQTTProtobufMsg.LGTravelCancelType.TRAVEL_CANCEL_TIMEOUT ==travelTransOrderCancelled.getType()){
                travelTranslatorCancelledBean.setCancelType(1);
            }
            if(null!=mqttSystemVoiceCallBack){
                mqttSystemVoiceCallBack.handlerSystemMsg(travelTranslatorCancelledBean);
            }
        }
    }

    private void insertOrder(final NewTransOrderBean newTransOrderBean){
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                boolean result = orderDbManager.insertOrder(newTransOrderBean);
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean o) throws Exception {
                if(null!=mqttSystemCallBack){
                    mqttSystemCallBack.handlerSystemMsg(newTransOrderBean);
                }
                if(null!=mainCallBack){
                    mainCallBack.showOrderNumberRedPoint();
                }
            }
        });
    }

    private void insertTravelTransOrder(final NewTravelTransOrderBean newTransOrderBean){
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                boolean result = travelTransOrderManager.insertOrder(newTransOrderBean);
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean o) throws Exception {
                if(null!=mqttSystemVoiceOrderCallBack){
                    mqttSystemVoiceOrderCallBack.handlerSystemMsg(newTransOrderBean);
                }
                if(null!=mainCallBack){
                    mainCallBack.showOrderNumberRedPoint();
                }
            }
        });
    }
}
