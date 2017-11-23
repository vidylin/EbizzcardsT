package com.gzligo.ebizzcardstranslator.push.receiver.fcm;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.gzligo.ebizzcardstranslator.push.PushClientHelper;
import com.gzligo.ebizzcardstranslator.push.utils.PreferencesUtils;
import com.gzligo.ebizzcardstranslator.push.utils.PushConstants;

/**
 * Created by Lwd on 2017/9/5.
 */

public class HisirFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private final String TAG = "HisirFirebase";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: fcm push service is create ...");
    }

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "fcm onReceiveRegisterResult: push token: " + refreshToken);
        if(PreferencesUtils.getPrefBoolean(this, PushConstants.SharedPreferences.UP_LOAD_TOKEN_FCM, true)){
            Intent intent = new Intent(this, PushClientHelper.PushReceiver.class);
            intent.setAction("com.ligo.push.register");
            intent.putExtra("push_token", refreshToken);
            sendBroadcast(intent);
            PreferencesUtils.setPrefString(this, PushConstants.SharedPreferences.UP_LOAD_TOKEN_FCM_TOKEN, refreshToken);
            Log.i(TAG, "onToken, saving token to PreferencesUtils.");
        }
    }
}
