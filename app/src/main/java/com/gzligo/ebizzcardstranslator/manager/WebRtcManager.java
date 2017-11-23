package com.gzligo.ebizzcardstranslator.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Base64;
import android.util.Log;
import android.widget.Chronometer;

import com.github.xgfjyw.webrtcclient.RTCWrapper;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.common.FloatWindow;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.mqtt.MqttManager;
import com.gzligo.ebizzcardstranslator.utils.RxTimerUtil;

import org.json.JSONObject;

/**
 * Created by Lwd on 2017/9/14.
 */

public class WebRtcManager {
    private static final String TAG = "WebRtcManager";
    private RTCWrapper rtc_instance;
    private String sessionID;
    private Context context;
    private String voiceCallClientID;
    private Chronometer chronometer;
    private FloatWindow floatWindow;
    private VoipReceiver voipReceiver;
    private String jti;

    public String getVoiceCallClientID() {
        return voiceCallClientID;
    }

    public void setVoiceCallClientID(String voiceCallClientID) {
        this.voiceCallClientID = voiceCallClientID;
    }

    private WebRtcManager() {
        context = AppManager.get().getApplication();
    }

    private static class Singleton {
        private static WebRtcManager sInstance = new WebRtcManager();
    }

    public static WebRtcManager getInstance() {
        return Singleton.sInstance;
    }

    public void connectWebRtc(String rtcToken, final OnWebRtcConnectListener listener) {
        if (rtc_instance == null)
            rtc_instance = new RTCWrapper();
        try {
            String token = rtcToken.split("\\.")[1];
            String json = new String(Base64.decode(token, Base64.DEFAULT));
            JSONObject jsonObject = new JSONObject(json);
            jti = jsonObject.getString(CommonConstants.PARAM_JTI);
            sessionID = jsonObject.getString(CommonConstants.PARAM_SID);
            registerVoipRecevier(listener);
            rtc_instance.connect(jti, sessionID, rtcToken,null);
        } catch (Exception e) {
            e.printStackTrace();
            //程序没运行的时候，收到语音通话通知再取消，进入程序之后还是会收到语音通话的指令，此时该会话已经不存在了，返回的token为空，会出现异常
            resetWebRtc();
        }
    }

    /**
     * 如果rtc没有连接成功或者是失败了，这里disconnect的时候会有异常，并且无法捕获此异常
     */
    public void resetWebRtc() {
        RxTimerUtil.cancel();
        if (rtc_instance != null) {
            rtc_instance.disconnect();
            rtc_instance = null;
        }
        sessionID = null;
        if(null!=voipReceiver){
            context.unregisterReceiver(voipReceiver);
            voipReceiver = null;
        }

    }

    public void rememberChronometer(Chronometer chronometer, FloatWindow floatWindow) {
        this.chronometer = chronometer;
        this.floatWindow = floatWindow;
    }

    public Chronometer getServiceChronometer() {
        return chronometer;
    }

    public FloatWindow getServiceFloatWindow() {
        return floatWindow;
    }

    public void mute(boolean yes) {
        rtc_instance.mute(yes);
    }

    public void handUp() {
        resetWebRtc();
    }

    public interface OnWebRtcConnectListener {
        void webRtcConnected();
    }

    public void registerVoipRecevier(OnWebRtcConnectListener listener) {
        voipReceiver = new VoipReceiver(listener);
        IntentFilter filter = new IntentFilter();
        filter.addAction("hisir.voip");
        context.registerReceiver(voipReceiver, filter);
    }

    public class VoipReceiver extends BroadcastReceiver {
        private OnWebRtcConnectListener listener;

        public VoipReceiver(OnWebRtcConnectListener listener) {
            this.listener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int voipState = intent.getIntExtra("voip_state", 0);
            if (voipState == 2) {
                Log.e(TAG, "onReceive voip is connectd success -------------------");
                listener.webRtcConnected();
                RxTimerUtil.interval(60 * 1000, new RxTimerUtil.IRxNext() {
                    @Override
                    public void doNext(long number) {
                        MqttManager.get().travelTransKeepAlive(sessionID,getVoiceCallClientID());
                    }
                });
            } else if (voipState == 4) {
                Log.e(TAG, "onReceive voip is connectd failed -------------------");
                RxTimerUtil.cancel();
                resetWebRtc();
            }
        }
    }
}
