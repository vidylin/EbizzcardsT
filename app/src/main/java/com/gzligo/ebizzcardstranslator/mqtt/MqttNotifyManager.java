package com.gzligo.ebizzcardstranslator.mqtt;

import android.text.TextUtils;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.db.manager.ChatMsgDbManager;
import com.gzligo.ebizzcardstranslator.db.manager.RecentContactsDbManager;
import com.gzligo.ebizzcardstranslator.mqtt.callback.MqttNotifyCallBack;
import com.gzligo.ebizzcardstranslator.mqtt.protobuf.MQTTProtobufMsg;
import com.gzligo.ebizzcardstranslator.notification.NotificationManager;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorSelectedBean;
import com.gzligo.ebizzcardstranslator.utils.CommonUtils;
import com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lwd on 2017/6/7.
 */

public class MqttNotifyManager{
    private MqttNotifyCallBack mqttNotifyCallBack;
    private ChatMsgDbManager chatMsgDbManager;
    private RecentContactsDbManager recentContactsDbManager;
    private String userNameStr;

    private MqttNotifyManager(){
        chatMsgDbManager = new ChatMsgDbManager();
        recentContactsDbManager = new RecentContactsDbManager();
    }

    private static class Singleton {
        private static MqttNotifyManager sInstance = new MqttNotifyManager();
    }

    public static MqttNotifyManager get() {
        return Singleton.sInstance;
    }

    public void registerMqttNotifyCallBack(MqttNotifyCallBack mqttNotifyCallBack){
        this.mqttNotifyCallBack = mqttNotifyCallBack;
    }

    public void handlerNotifyMsg(MQTTProtobufMsg.Notify notifyMsg) {
        String notificationMsg = null;
        if(notifyMsg.hasTranslatorSelected()){
            MQTTProtobufMsg.TranslatorSelected translatorSelected = notifyMsg.getTranslatorSelected();
            if (MqttDupMsgUtils.getInstance().duplicate("notify", translatorSelected.getSessionId()+"start")) {
                return;
            }
            TranslatorSelectedBean translatorSelectedBean = new TranslatorSelectedBean();
            translatorSelectedBean.setSessionId(translatorSelected.getSessionId());
            translatorSelectedBean.setFromLangId(translatorSelected.getLanguageIdsList().get(0));
            translatorSelectedBean.setToLangId(translatorSelected.getLanguageIdsList().get(1));
            MQTTProtobufMsg.UserInfo userInfos = translatorSelected.getUsersList().get(0);
            translatorSelectedBean.setFromName(userInfos.getNickname());
            translatorSelectedBean.setFromPortraitId(userInfos.getPortrait());
            translatorSelectedBean.setFromUserId(userInfos.getUid());
            MQTTProtobufMsg.UserInfo userInfos2 = translatorSelected.getUsersList().get(1);
            translatorSelectedBean.setToName(userInfos2.getNickname());
            translatorSelectedBean.setToUserId(userInfos2.getUid());
            translatorSelectedBean.setNotifyTime(System.currentTimeMillis());
            translatorSelectedBean.setToPortraitId(userInfos2.getPortrait());
            translatorSelectedBean.setTranslatorMsg(AppManager.get().getApplication().getResources().getString(R.string.start_translation));
            translatorSelectedBean.setStatus(ChatConstants.SYSTEM_START_TRANSLATION);
            translatorSelectedBean.setUpdateTime(System.currentTimeMillis());
            insertOrUpdateRecentContacts(translatorSelectedBean);
            notificationMsg = AppManager.get().getApplication().getResources().getString(R.string.order_choice);
        }else if(notifyMsg.hasTranslatorOut()){
            MQTTProtobufMsg.TranslatorOut translatorOut = notifyMsg.getTranslatorOut();
            if (MqttDupMsgUtils.getInstance().duplicate("notify", translatorOut.getSessionId()+"end")) {
                return;
            }
            TranslatorSelectedBean translatorSelectedBean = new TranslatorSelectedBean();
            translatorSelectedBean.setSessionId(translatorOut.getSessionId());
            translatorSelectedBean.setFromUserId(translatorOut.getWhoId());
            translatorSelectedBean.setToUserId(translatorOut.getTargetId());
            translatorSelectedBean.setEndTime((long) translatorOut.getTime());
            translatorSelectedBean.setNotifyTime(System.currentTimeMillis());
            translatorSelectedBean.setStatus(ChatConstants.SYSTEM_END_TRANSLATION);
            translatorSelectedBean.setTranslatorMsg(AppManager.get().getApplication().getResources().getString(R.string.end_translation));
            translatorSelectedBean.setUpdateTime(System.currentTimeMillis());
            if(null!=mqttNotifyCallBack){
                mqttNotifyCallBack.handlerNotifyMsg(translatorSelectedBean);
            }
            insertEndChatSystemMsg(translatorSelectedBean);
            insertOrUpdateRecentContacts(translatorSelectedBean);
            notificationMsg = AppManager.get().getApplication().getResources().getString(R.string.order_over);
        }
        if(CommonUtils.isBackground(AppManager.get().getApplication())&&null!=notificationMsg){
            NotificationManager.getInstance().notificationNewMessage(notificationMsg,0);
        }
    }

    public void insertEndChatSystemMsg(final TranslatorSelectedBean selectedBean){
        Observable.create(new ObservableOnSubscribe<ChatMessageBean>() {
            @Override
            public void subscribe(ObservableEmitter<ChatMessageBean> e) throws Exception {
                ChatMessageBean startChat = new ChatMessageBean();
                startChat.setMsgId("");
                startChat.setTranslatorId(getUserName());
                startChat.setToId(selectedBean.getToUserId());
                startChat.setFromId(selectedBean.getFromUserId());
                startChat.setMsgTime(System.currentTimeMillis()+"");
                startChat.setTranslationStatus(0);
                startChat.setTranslateTime(selectedBean.getEndTime());
                startChat.setType(ChatConstants.END_CHAT);
                startChat.setOrderId(selectedBean.getSessionId());
                chatMsgDbManager.insertEndMsg(startChat);
                e.onNext(startChat);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ChatMessageBean>() {
            @Override
            public void accept(@NonNull ChatMessageBean aVoid) throws Exception {
                MqttChatManager.get().endTranslateMsg(aVoid);
            }
        });
    }

    private String getUserName(){
        if(TextUtils.isEmpty(userNameStr)){
            userNameStr = (String) SharedPreferencesUtils.getSharedPreferences(AppManager.get()
                    .getApplication(), CommonConstants.USER_NAME,"",CommonConstants.USER_INFO_PRE_NAME);
        }
        return userNameStr;
    }

    public boolean insertStartChatSystemMsg(final TranslatorSelectedBean orderBean) {
        String userId = (String) SharedPreferencesUtils.getSharedPreferences(AppManager.get()
                .getApplication(), CommonConstants.USER_ID, "", CommonConstants.USER_INFO_PRE_NAME);
        ChatMessageBean startChat = new ChatMessageBean();
        startChat.setMsgId("");
        startChat.setTranslatorId(userId);
        startChat.setToId(orderBean.getToUserId());
        startChat.setFromId(orderBean.getFromUserId());
        startChat.setMsgTime(System.currentTimeMillis() + "");
        startChat.setTranslationStatus(0);
        startChat.setType(ChatConstants.START_CHAT);
        startChat.setOrderId(orderBean.getSessionId());
        return chatMsgDbManager.insert(startChat);
    }

    public void insertOrUpdateRecentContacts(final TranslatorSelectedBean translatorSelectedBean){
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                if(translatorSelectedBean.getStatus()==0){
                    insertStartChatSystemMsg(translatorSelectedBean);
                }
                boolean result = recentContactsDbManager.insertOrUpdate(translatorSelectedBean);
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean o) throws Exception {
                if(null!=mqttNotifyCallBack){
                    mqttNotifyCallBack.handlerNotifyMsg(translatorSelectedBean);
                }
            }
        });
    }
}
