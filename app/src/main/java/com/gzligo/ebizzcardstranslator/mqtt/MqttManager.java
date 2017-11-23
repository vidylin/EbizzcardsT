package com.gzligo.ebizzcardstranslator.mqtt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.applog.Timber;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.business.account.view.login.LoginActivity;
import com.gzligo.ebizzcardstranslator.business.account.view.login.LoginPresenter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.LoginRepository;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.mqtt.protobuf.MQTTProtobufMsg;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslationChatResultBean;
import com.gzligo.ebizzcardstranslator.push.utils.AboutPhoneInfo;
import com.gzligo.ebizzcardstranslator.push.utils.PreferencesUtils;
import com.gzligo.ebizzcardstranslator.push.utils.PushConstants;
import com.gzligo.ebizzcardstranslator.utils.DialogUtils;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils;
import com.gzligo.ebizzcardstranslator.utils.TranslatorCallBack;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

import static com.gzligo.ebizzcardstranslator.utils.SharedPreferencesUtils.getSharedPreferences;

/**
 * Created by Lwd on 2017/6/6.
 */

public class MqttManager implements MqttCallback{
    private static final String TAG = MqttManager.class.getSimpleName();
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions options;
    private String mUserId;
    private String userName;
    private String password;
    private String userNameStr;
    private String reconnectionPwd;
    private String tid;
    private boolean isLogout = false;
    private Context mContext;
    public static SocketState SOCKET_STATE = SocketState.none;

    private MqttManager(){
        mContext = AppManager.get().getApplication();
    }

    private static class Singleton {
        private static MqttManager sInstance = new MqttManager();
    }

    public static MqttManager get() {
        return Singleton.sInstance;
    }

    public void uploadPublishInfo() {
        if (TextUtils.isEmpty(mUserId) || mqttAndroidClient == null || !mqttAndroidClient.isConnected()) {
            return;
        }
        try {
            MqttMessage message = new MqttMessage();
            message.setQos(1);
            MQTTProtobufMsg.Notice.Builder notify = MQTTProtobufMsg.Notice.newBuilder();
            MQTTProtobufMsg.UploadPushInfo.Builder uploadPushInfo = MQTTProtobufMsg.UploadPushInfo.newBuilder();
            uploadPushInfo.setUid(mUserId);
            String phoneName = Build.MANUFACTURER.toLowerCase();
            String token;
            String type;
            switch (phoneName) {
                case "xiaomi": //xiao mi
                    token = PreferencesUtils.getPrefString(mContext, PushConstants.SharedPreferences.UP_LOAD_TOKEN_XIAOMI_TOKEN, "");
                    type = "eBizzcards_xiaomi_t";
                    break;
                case "huawei":// huiwei
                    token = PreferencesUtils.getPrefString(mContext, PushConstants.SharedPreferences.UP_LOAD_TOKEN_HUAWEI_TOKEN, "");
                    type = "eBizzcards_hms_t";
                    break;
                case "meizu": //meizu
                    token = PreferencesUtils.getPrefString(mContext, PushConstants.SharedPreferences.UP_LOAD_TOKEN_MEIZU_TOKEN, "");
                    type = "eBizzcards_flyme_t";
                    break;
                default:
                    token = PreferencesUtils.getPrefString(mContext, PushConstants.SharedPreferences.UP_LOAD_TOKEN_FCM_TOKEN, "");
                    type = "eBizzcards_fcm_t";
                    break;
            }
            if (TextUtils.isEmpty(token)) {
                //厂商推送获取不到token将使用fcm
                token = PreferencesUtils.getPrefString(mContext, PushConstants.SharedPreferences.UP_LOAD_TOKEN_FCM_TOKEN, "");
                type = "eBizzcards_fcm_t";
            }
            if (TextUtils.isEmpty(token)) {
                return;
            }
            String rom = AboutPhoneInfo.getRomInfo();
            String manufacturer = Build.MANUFACTURER;
            String androidVersion = android.os.Build.VERSION.RELEASE;
            String phoneInfo = manufacturer + "-" + androidVersion + "-" + rom;
            uploadPushInfo.setDeviceType(phoneInfo);
            uploadPushInfo.setType(type);
            uploadPushInfo.setDeviceToken(token);
            String currentLanguage = LanguageUtils.currentLanguage(AppManager.get().getApplication());
            String language = "en";
            switch (currentLanguage){
                case TranslatorConstants.SharedPreferences.LANGUAGE_EN:
                    language = "en";
                    break;
                case TranslatorConstants.SharedPreferences.LANGUAGE_CH:
                    language = "zh";
                    break;
            }
            uploadPushInfo.setLanguage(language);
            uploadPushInfo.setIsDetail(true);
            notify.setUploadPushInfo(uploadPushInfo);
            MQTTProtobufMsg.Notice build = notify.build();
            message.setPayload(build.toByteArray());
            mqttAndroidClient.publish(MQTTConfig.PRE_MSG_NOTICE, message, null, new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("onSuccess--:",asyncActionToken.toString());
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("onFailure: ",exception.toString());
                }
            });
        } catch (Exception e) {
            Log.e("Exception=",e.toString());
        }
    }

    public void loginMqttAndroidSever(final String clientId, final String userName, final String passwordToken, final String reconnectionToken){
        tid = clientId;
        setTid();
        if (mqttAndroidClient == null || !mqttAndroidClient.isConnected()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        createMqttClientAndConnect(userName, passwordToken, reconnectionToken, clientId);
                    } catch (MqttException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
            }).start();
        }
    }

    /**
     * 创建mqqt连接，可以在这里对socket进行配置
     */
    private synchronized void createMqttClientAndConnect(String userName, String password, String reconnectionPwd, String clientId) throws MqttException {
        if (mqttAndroidClient == null) {
            mqttAndroidClient = new MqttAndroidClient(AppManager.get().getApplication(), HttpUtils.MQTT_SOCKET, clientId);
        }else {
            //针对部分手机mqtt底层抛出的Invalid ClientHandle异常
            if (TextUtils.isEmpty(mqttAndroidClient.getServerURI()) || TextUtils.isEmpty(mqttAndroidClient.getClientId())) {
                Log.w(TAG, "createMqttClientAndConnect uri or client id is empty!!! uri: " + mqttAndroidClient.getServerURI()
                        + ",client id: " + mqttAndroidClient.getClientId());
                try {
                    mqttAndroidClient.unregisterResources();
                }catch (Exception e) {
                    Log.w(TAG, "createMqttClientAndConnect uri or client id is empty,unregisterResources exception ..." + e.getMessage());
                }
                mqttAndroidClient = null;
                mqttAndroidClient = new MqttAndroidClient(AppManager.get().getApplication(), HttpUtils.MQTT_SOCKET, clientId);
            }
        }
        MqttManager.this.password = password;
        options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setConnectionTimeout(MQTTConfig.CONNECT_TIMEOUT);
        options.setKeepAliveInterval(MQTTConfig.ALIVE_INTERVAL);
        if (!TextUtils.isEmpty(userName)) {
            options.setUserName(userName);
        }
        options.setPassword(TextUtils.isEmpty(password)?reconnectionPwd.toCharArray():password.toCharArray());
        mqttAndroidClient.setCallback(this);
        try {
            connectMqtt(userName, reconnectionPwd, clientId);
        }catch (Exception e) {
            Log.w(TAG, "createMqttClientAndConnect: " + e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        retryLogin();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.e(TAG,"messageArrived="+message.toString());
        try {
            if (topic.startsWith(MQTTConfig.PRE_CHAT)) {
                MQTTProtobufMsg.Msg chatMsg = MQTTProtobufMsg.Msg.parseFrom(message.getPayload());
                MqttChatManager.get().handlerChatMsg(chatMsg);
            } else if (topic.startsWith(MQTTConfig.PRE_NOTIFY)) {
                MQTTProtobufMsg.Notify notifyMsg = MQTTProtobufMsg.Notify.parseFrom(message.getPayload());
                MqttNotifyManager.get().handlerNotifyMsg(notifyMsg);
            } else if (topic.startsWith(MQTTConfig.PRE_SYSTEM)) {
                MQTTProtobufMsg.System systemMsg = MQTTProtobufMsg.System.parseFrom(message.getPayload());
                MqttSystemManager.get().handlerSystemMsg(systemMsg);
            }
        } catch (Exception e) {
            Log.e(TAG, "messageArrived Exception: " + Log.getStackTraceString(e));
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.e("ds","sd");
    }

    private void connectMqtt(final String userName, final String reconnectionPwd, final String clientId) throws Exception {
        if (mqttAndroidClient.isConnected()) {
            Log.i(TAG, "connectMqtt mqttClient is connected.");
            return;
        }
        mqttAndroidClient.connect(options, null, new IMqttActionListener() {
            /**
             * 连接成功之后，需要去订阅几个主题，这里的qos和topic需要对应
             *
             * @param asyncActionToken associated with the action that has completed
             */
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                mUserId = mqttAndroidClient.getClientId();
                MqttManager.this.userName = userName;
                MqttManager.this.reconnectionPwd = reconnectionPwd;
                MqttManager.this.mUserId = clientId;
                new SharedPreferencesUtils<String>().putSharedPreference(AppManager.get().getApplication(),
                                CommonConstants.PASSWORD_TOKEN,reconnectionPwd,CommonConstants.USER_INFO_PRE_NAME);
                new SharedPreferencesUtils<String>().putSharedPreference(AppManager.get().getApplication(),
                        CommonConstants.USER_NAME_MQTT,userName,CommonConstants.USER_INFO_PRE_NAME);
                String[] topics = new String[]{MQTTConfig.PRE_CHAT + mUserId,
                        MQTTConfig.PRE_NOTIFY + mUserId,
                        MQTTConfig.PRE_SYSTEM + mUserId
                };
                int[] Qos = {1, 1, 1};
                try {
                    mqttAndroidClient.subscribe(topics, Qos, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.i(TAG, "MqttClient subscribe success: " + mUserId);
                            isLogout = false;
                            Observable.create(new ObservableOnSubscribe<Object>() {
                                @Override
                                public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                                    uploadPublishInfo();
                                }
                            }).subscribeOn(Schedulers.newThread()).subscribe();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            String exp = Log.getStackTraceString(exception);
                            Log.e(TAG, "MqttClient subscribe " + exp);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "connectMqtt subscribe MqttException: " + e);
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.e("onFailure", "!!!connectMqtt onFailure: " + exception + ", =====================this=" + this);
                if(exception instanceof MqttSecurityException){
                    int errorCode = ((MqttSecurityException) exception).getReasonCode();
                    Log.e("errorCode","-->onFailure===="+errorCode);
                    switch (errorCode){
                        case 4:
                            isLogout = false;
                            try {
                                mqttAndroidClient.disconnect();
                                mqttAndroidClient = null;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String refreshToken = (String) SharedPreferencesUtils.getSharedPreferences(mContext, CommonConstants.REFRESH_TOKEN, "", CommonConstants.USER_INFO_PRE_NAME);
                            new LoginPresenter(new LoginRepository()).requestRefreshToken(refreshToken);
                            break;
                        case 5:
                            isLogout = true;
                            Activity activity = AppManager.get().getTopActivity();
                            logoutDialog(activity);
                            break;
                    }
                }else if(exception instanceof SocketTimeoutException||exception instanceof SocketException){
                    //超时
                    Activity activity = AppManager.get().getTopActivity();
                    if(null!=activity){
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        activity.finish();
                    }
                }
            }
        });
    }

    public void sendMessage(final ChatMessageBean msg) {
        Log.e(TAG, "sendMessage: ");
        if (mqttAndroidClient == null || !mqttAndroidClient.isConnected()) {
            retryLogin();
        }
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                final MQTTProtobufMsg.Translate.Builder translateChat;
                final MQTTProtobufMsg.PrivateChat.Builder privateChat;
                if (!msg.getIsPrivateMessage()) {
                    translateChat = MQTTProtobufMsg.Translate.newBuilder();
                    sendTranslateMsg(msg, translateChat);
                } else {
                    privateChat = MQTTProtobufMsg.PrivateChat.newBuilder();
                    sendPrivateMsg(msg, privateChat);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void retryLogin() {
        if(TextUtils.isEmpty(getUserNameMqtt())||TextUtils.isEmpty(getReconnectionPwd())||TextUtils.isEmpty(getTid())){
            return;
        }
        if((mqttAndroidClient == null || !mqttAndroidClient.isConnected()) && !isLogout){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        createMqttClientAndConnect(getUserNameMqtt(),null,getReconnectionPwd(),getTid());
                    } catch (MqttException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
            }).start();
        }
    }

    private ChatMessageBean sendTranslateMsg(final ChatMessageBean msg, MQTTProtobufMsg.Translate.Builder translate) throws Exception {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(1);
        MQTTProtobufMsg.Msg.Builder sendMsg = MQTTProtobufMsg.Msg.newBuilder();
        translate.setMsgId(msg.getMsgId());
        String content = msg.getTranslateContent();
        switch (msg.getSendMsgType()) {
            case ChatConstants.TXT_PRIVATE:
                content = MQTTMsgUtils.buildJason(ChatConstants.TXT_PRIVATE, msg);
                translate.setType(MQTTProtobufMsg.LGContentType.TEXT);
                break;
            case ChatConstants.VOICE_PRIVATE:
                content = MQTTMsgUtils.buildJason(ChatConstants.VOICE_PRIVATE, msg);
                translate.setType(MQTTProtobufMsg.LGContentType.VOICE);
                break;
        }
        translate.setContent(content);
        translate.setFrom(msg.getFromId());
        translate.setTo(msg.getToId());
        translate.setTranslator(msg.getTranslatorId());
        translate.setTranslatorName(getUserName());
        translate.setIsReTrans(msg.getIsReTrans());
        sendMsg.setTranslate(translate);
        MQTTProtobufMsg.Msg build = sendMsg.build();
        mqttMessage.setPayload(build.toByteArray());
        publishMsg(msg.getToId(), msg, mqttMessage);
        return null;
    }

    private ChatMessageBean sendPrivateMsg(final ChatMessageBean msg, MQTTProtobufMsg.PrivateChat.Builder privateChat) throws Exception {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(1);
        MQTTProtobufMsg.Msg.Builder sendMsg = MQTTProtobufMsg.Msg.newBuilder();
        privateChat.setExtraUid(msg.getExtraUid());
        privateChat.setMsgId(msg.getMsgId());
        privateChat.setFrom(msg.getTranslatorId());
        privateChat.setTo(msg.getFromId());
        privateChat.setFromName(msg.getToName());
        privateChat.setFromTranslator(true);
        String content = msg.getContent();
        switch (msg.getSendMsgType()) {
            case ChatConstants.TXT_PRIVATE:
                content = MQTTMsgUtils.buildJason(ChatConstants.TXT_PRIVATE, msg);
                privateChat.setType(MQTTProtobufMsg.LGContentType.TEXT);
                break;
            case ChatConstants.VOICE_PRIVATE:
                content = MQTTMsgUtils.buildJason(ChatConstants.VOICE_PRIVATE, msg);
                privateChat.setType(MQTTProtobufMsg.LGContentType.VOICE);
                break;
        }
        privateChat.setTime(Long.parseLong(msg.getMsgTime()));
        privateChat.setContent(content);
        sendMsg.setPrivateChat(privateChat);
        MQTTProtobufMsg.Msg build = sendMsg.build();
        mqttMessage.setPayload(build.toByteArray());
        publishMsg(msg.getFromId(), msg, mqttMessage);
        return msg;
    }

    private void publishMsg(String publishTo, final ChatMessageBean msg, MqttMessage mqttMessage) throws Exception {
        mqttAndroidClient.publish(MQTTConfig.PRE_CHAT + publishTo, mqttMessage, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.e(TAG, "onSuccess: " + msg.getMsgId());
                TranslationChatResultBean translationChatResultBean = new TranslationChatResultBean();
                translationChatResultBean.setMsgId(msg.getMsgId());
                translationChatResultBean.setResultStatus( MQTTProtobufMsg.LGMsgStateType.SENT_VALUE);
                MqttChatManager.get().handlerChatResult(translationChatResultBean);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.e(TAG, "onFailure: " + exception);
                TranslationChatResultBean translationChatResultBean = new TranslationChatResultBean();
                translationChatResultBean.setMsgId(msg.getMsgId());
                translationChatResultBean.setResultStatus( MQTTProtobufMsg.LGMsgStateType.SEND_FAIL_VALUE);
                MqttChatManager.get().handlerChatResult(translationChatResultBean);
            }
        });
    }

    public void sendClientReceived(String msgId, String recId, MQTTProtobufMsg.LGMsgStateType status) {
        try {
            Log.i(TAG, "send client rec is: " + recId + "status: " + status.getNumber() + ",msgId: " + msgId);
            MqttMessage message = new MqttMessage();
            message.setQos(1);
            MQTTProtobufMsg.Notify.Builder notify = MQTTProtobufMsg.Notify.newBuilder();
            MQTTProtobufMsg.MsgState.Builder msgState = MQTTProtobufMsg.MsgState.newBuilder();
            msgState.addMsgId(msgId);
            msgState.setFrom(getTid());
            msgState.setState(status);
            msgState.build();
            notify.setMsgState(msgState);
            MQTTProtobufMsg.Notify build = notify.build();
            message.setPayload(build.toByteArray());
            mqttAndroidClient.publish(MQTTConfig.PRE_NOTIFY + recId, message);
        } catch (Exception e) {
            Log.e(TAG, "sendClientReceived Exception: " + Log.getStackTraceString(e));
        }
    }

    public void logout() {//主动登出账户
        isLogout = true;
        if (mqttAndroidClient == null) {
            return;
        }
        try {
            mqttAndroidClient.unregisterResources();
            mqttAndroidClient.close();
            mqttAndroidClient.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "logout Exception: " + Log.getStackTraceString(e));
        } finally {
            mqttAndroidClient = null;
            mUserId = null;
            userName = null;
            password = null;
        }
    }

    private void setTid(){
        new SharedPreferencesUtils<String>().putSharedPreference(AppManager.get().getApplication(),
                CommonConstants.USER_ID,tid,CommonConstants.USER_INFO_PRE_NAME);
    }

    private String getTid(){
        if(TextUtils.isEmpty(tid)){
            tid = (String) getSharedPreferences(AppManager.get()
                    .getApplication(),CommonConstants.USER_ID,"",CommonConstants.USER_INFO_PRE_NAME);
        }
        return tid;
    }

    private String getUserName(){
        if(TextUtils.isEmpty(userNameStr)){
            userNameStr = (String) getSharedPreferences(AppManager.get()
                    .getApplication(),CommonConstants.USER_NAME,"",CommonConstants.USER_INFO_PRE_NAME);
        }
        return userNameStr;
    }

    private String getUserNameMqtt(){
        if(TextUtils.isEmpty(userName)){
            userName = (String) getSharedPreferences(AppManager.get()
                    .getApplication(),CommonConstants.USER_NAME_MQTT,"",CommonConstants.USER_INFO_PRE_NAME);
        }
        return userName;
    }

    private String getReconnectionPwd(){
        if(TextUtils.isEmpty(reconnectionPwd)){
            reconnectionPwd = (String) getSharedPreferences(AppManager.get()
                    .getApplication(),CommonConstants.PASSWORD_TOKEN,"",CommonConstants.USER_INFO_PRE_NAME);
        }
        return reconnectionPwd;
    }

    private void logoutDialog(final Activity activity){
        CommonBeanManager.getInstance().initManager();
        Log.e("logoutDialog---->",activity.toString());
        if(null!=activity){
            final Context context = AppManager.get().getApplication();
            new SharedPreferencesUtils().putSharedPreference(context, CommonConstants.FIRST_LOGIN,true,CommonConstants.USER_INFO_PRE_NAME);
            new SharedPreferencesUtils().putSharedPreference(context, CommonConstants.USER_PWD,"",CommonConstants.USER_INFO_PRE_NAME);

            DialogUtils.showConFirmDialog(activity,context.getResources().getString(R.string.prompt),
                    context.getResources().getString(R.string.prompt_content), context.getResources().getString(R.string.confirm),R.layout.dialog_login_out, new TranslatorCallBack.OnDialogClickListener() {
                        @Override
                        public void onConfirm() {
                            Intent outIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                            outIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(outIntent);
                            activity.finish();
                        }});
        }
    }

    public boolean isLogout() {
        return isLogout;
    }

    public enum SocketState {
        none,
        inConnection,
        disConnection,
        successConnection
    }

    //如果接通了，双方应该在会话过程中每隔1分钟发送TravelTransKeepAlive延时
    public void travelTransKeepAlive(final String sessionId, final String mUserId){
        try {
            MqttMessage message = new MqttMessage();
            message.setQos(1);
            MQTTProtobufMsg.Notice.Builder notice = MQTTProtobufMsg.Notice.newBuilder();
            MQTTProtobufMsg.TravelTransKeepAlive.Builder webRTCContinueNotice = MQTTProtobufMsg.TravelTransKeepAlive.newBuilder();
            webRTCContinueNotice.setSessionId(sessionId);
            webRTCContinueNotice.setUid(tid);
            webRTCContinueNotice.setTranslatorId(mUserId);
            webRTCContinueNotice.build();
            notice.setTravelTransKeepalive(webRTCContinueNotice);
            MQTTProtobufMsg.Notice build = notice.build();
            message.setPayload(build.toByteArray());
            mqttAndroidClient.publish(MQTTConfig.PRE_NOTICE, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e(TAG,"continue voip success"+"tid="+tid+"mUserId="+mUserId+"sessionId="+sessionId);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG,"continue voip onFailure");
                }
            });
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
