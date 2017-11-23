package com.gzligo.ebizzcardstranslator.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.badge.ShortcutBadger;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.business.MainActivity;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.business.chat.ChatActivity;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.push.utils.PreferencesUtils;

import java.util.List;
import java.util.Random;

/**
 * Created by Lwd on 2017/8/1.
 */

public class NotificationManager {

    private Context context;
    private android.app.NotificationManager notificationManager;

    private NotificationManager() {
        this.context = AppManager.get().getApplication();
        notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static class Singleton {
        private static NotificationManager sInstance = new NotificationManager();
    }

    public static NotificationManager getInstance() {
        return Singleton.sInstance;
    }

    public int notificationNewMessage(String message,int type) {
        if (TextUtils.isEmpty(message)){
            return 0;
        }
        Random random=new Random();
        int notificationID = random.nextInt();
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        boolean sound = PreferencesUtils.getPrefBoolean(context, TranslatorConstants.SharedPreferences.NOTIFICATION_SETTING_SOUND, true);
        boolean vibrate = PreferencesUtils.getPrefBoolean(context, TranslatorConstants.SharedPreferences.NOTIFICATION_SETTING_VIBRATE, true);
        int model = -2;
        if (sound && vibrate) {
            model = Notification.DEFAULT_ALL;
        }else if (sound) {
            model = Notification.DEFAULT_SOUND;
        }else if (vibrate){
            model = Notification.DEFAULT_VIBRATE;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        if(type==1){
            if (PreferencesUtils.getPrefBoolean(context, TranslatorConstants.SharedPreferences.NOTIFICATION_SETTING_NOTIFY_DETAIL, true)) {
                builder.setContentText(message);
            }else {
                builder.setContentText(CommonBeanManager.getInstance().getUnReadMsgUserCount()
                        + context.getResources().getString(R.string.notify_close_detail_more_than_contact_send)
                        + CommonBeanManager.getInstance().getUnReadMsgNumber()
                        + context.getResources().getString(R.string.notify_close_detail_send_msg));
            }
        }else{
            builder.setContentText(message);
        }
        if (model != -2) {
            builder.setDefaults(model);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(context.getResources().getString(R.string.app_name));
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setOnlyAlertOnce(false);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        int badgeCount = CommonBeanManager.getInstance().getBadgeCount();
        badgeCount++;
        CommonBeanManager.getInstance().setBadgeCount(badgeCount);
        ShortcutBadger.applyCount(AppManager.get().getApplication(),badgeCount);
        ShortcutBadger.applyCountFixMiui(notification,badgeCount);
        notificationManager.notify(1, notification);
        List<Activity> activityList = AppManager.get().getActivities();
        if(null!=activityList&&activityList.size()>0){
            for(Activity activity : activityList){
                if(activity instanceof ChatActivity){
                    ChatActivity chatActivity = (ChatActivity) activity;
                    chatActivity.finish();
                }
            }
        }
        return notificationID;
    }
}
