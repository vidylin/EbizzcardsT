package com.gzligo.ebizzcardstranslator.push.receiver.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Lwd on 2017/9/5.
 */

public class HisirFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("lw", "fcm onMessageReceived: " + remoteMessage.getData());
    }

}
