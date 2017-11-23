package com.gzligo.ebizzcardstranslator.push.receiver.huawei;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gzligo.ebizzcardstranslator.push.PushClientHelper;
import com.gzligo.ebizzcardstranslator.push.utils.PreferencesUtils;
import com.gzligo.ebizzcardstranslator.push.utils.PushConstants;
import com.huawei.hms.support.api.push.PushReceiver;

/**
 * Created by Lwd on 2017/9/5.
 */

public class HWPushMessageReceiver extends PushReceiver{
    private final String TAG = "HWPushReceiver_tam";

    public final static String KEY_BELONG_ID = "belongId";

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        String belongId = extras.getString(KEY_BELONG_ID);
        String content = "onToken, get token and belongId successful, token = " + token + ",belongId = " + belongId;
        Log.i(TAG, content);

        if (PreferencesUtils.getPrefBoolean(context, PushConstants.SharedPreferences.UP_LOAD_TOKEN_HUAWEI_UPLOAD, true)) {
             Intent intent = new Intent(context, PushClientHelper.PushReceiver.class);
            intent.setAction("com.ligo.push.register");
            intent.putExtra("push_token", belongId);
            context.sendBroadcast(intent);
            PreferencesUtils.setPrefString(context, PushConstants.SharedPreferences.UP_LOAD_TOKEN_HUAWEI_TOKEN, token);
            Log.i(TAG, "onToken, saving token to PreferencesUtils.");
        }
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        if (msg == null)
            return false;
        try {
            String message = new String(msg, "UTF-8");
            Log.i(TAG, "onPushMsg, receive a push pass-by message： " + message);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }


    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String content = "onEvent, receive extented notification message: " + extras.getString(BOUND_KEY.pushMsgKey);
            Log.i(TAG, content);
        }
        super.onEvent(context, event, extras);
    }

    @Override
    public void onPushState(Context context, boolean pushState) {
        try {
            String content = "onPushState, The current push status: " + (pushState ? "Connected" : "Disconnected");
            Log.i(TAG, content);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
