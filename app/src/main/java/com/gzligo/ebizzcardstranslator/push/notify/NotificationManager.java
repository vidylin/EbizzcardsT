package com.gzligo.ebizzcardstranslator.push.notify;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.gzligo.ebizzcardstranslator.R;

import java.util.Map;


public class NotificationManager {

    private final String TAG = "NotificationManager";
    private static NotificationManager manager;
    public static final String NOTIFY_ACTION = "com.hisir.receiver.notification";
    private Context context;
    private android.app.NotificationManager notificationManager;
    private Intent intent;
    private int NOTIFY_ID = 0;
    public static final int NOTIFY_SYSTEM_ID = 999;
    private final int MAX_NOTIFY = 500;

    private Map<String, Integer> notifyIDCache;

    public static NotificationManager getNotificationManager(Context context) {
        if (manager == null) {
            manager = new NotificationManager(context);
        }
        return manager;
    }

    public NotificationManager(Context context) {
        this.context = context;
        notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public int notificationNewMessage(String message) {
        return notificationNewMessage(0, message);
    }

    public int notificationNewMessage(int notificationID, String message) {
        if (TextUtils.isEmpty(message)){
            return 0;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getResources().getString(R.string.app_name));
        builder.setContentText(message);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        builder.setOnlyAlertOnce(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        notificationManager.notify(notificationID, builder.build());
        return notificationID;
    }

    public void clearNotification(int notifyID) {
        try {
            notificationManager.cancel(notifyID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
