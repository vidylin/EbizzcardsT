package com.gzligo.ebizzcardstranslator.manager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.business.call.VoiceCallsActivity;
import com.gzligo.ebizzcardstranslator.common.FloatWindow;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslatorSelectedBean;

/**
 * Created by Lwd on 2017/9/14.
 */

public class WebRtcService extends Service {

    private TravelTranslatorSelectedBean travelTranslatorSelectedBean;
    private long chronomaterBase = 0;
    private FloatWindow floatWindow;
    private boolean isClickOver = false;
    private Chronometer chronometer;
    private WebRtcManager webRtcManager;
    private String language;

    public WebRtcService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        travelTranslatorSelectedBean = (TravelTranslatorSelectedBean) intent.getSerializableExtra("TRAVEL_TRANS_ORDER");
        language = intent.getStringExtra("LANGUAGE");
        chronomaterBase = intent.getLongExtra(CommonConstants.INFO, 0);

        floatWindow = new FloatWindow(this, R.layout.chat_voice_float_view);
        floatWindow.show();
        chronometer = (Chronometer) floatWindow.getContentView().findViewById(R.id.chat_voice_float_time_c);
        chronometer.setBase(chronomaterBase);
        chronometer.start();

        webRtcManager = WebRtcManager.getInstance();
        webRtcManager.rememberChronometer(chronometer, floatWindow);

        floatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isClickOver = true;
                stopVoip();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("lw", "onDestroy web rtc service destroy... is click over: " + isClickOver);
        if (!isClickOver) {
            stopVoip();
            webRtcManager.resetWebRtc();
        }
        isClickOver = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void stopVoip(){
        if (isClickOver) {
            Intent activityIntent = new Intent(WebRtcService.this, VoiceCallsActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityIntent.putExtra("TRAVEL_TRANS_ORDER", travelTranslatorSelectedBean);
            activityIntent.putExtra("COME_FROM", "WebRtcService");
            activityIntent.putExtra("TIME_INFO", chronometer.getBase());
            activityIntent.putExtra("LANGUAGE",language);
            startActivity(activityIntent);
        }
        floatWindow.hide();
        stopSelf();
    }
}
