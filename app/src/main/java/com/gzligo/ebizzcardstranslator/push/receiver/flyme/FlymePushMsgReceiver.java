package com.gzligo.ebizzcardstranslator.push.receiver.flyme;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.push.PushClientHelper;
import com.gzligo.ebizzcardstranslator.push.utils.PreferencesUtils;
import com.gzligo.ebizzcardstranslator.push.utils.PushConstants;
import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.notification.PushNotificationBuilder;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;

/**
 * Created by Lwd on 2017/9/5.
 */

public class FlymePushMsgReceiver extends MzPushMessageReceiver {

    @Override
    public void onRegister(Context context, String pushId) {

    }

    @Override
    public void onUnRegister(Context context, boolean success) {

    }

    @Override
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {

    }

    @Override
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {
        Log.i(TAG, "onRegisterStatus " + registerStatus);
        if (registerStatus != null) {
            if (PreferencesUtils.getPrefBoolean(context, PushConstants.SharedPreferences.UP_LOAD_TOKEN_MEIZU_UPLOAD, true)) {
                Intent intent = new Intent(context, PushClientHelper.PushReceiver.class);
                intent.setAction("com.ligo.push.register");
                intent.putExtra("push_token", registerStatus.getPushId());
                context.sendBroadcast(intent);
                PreferencesUtils.setPrefString(context, PushConstants.SharedPreferences.UP_LOAD_TOKEN_MEIZU_TOKEN, registerStatus.getPushId());
            }
        }
    }

    @Override
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {

    }

    @Override
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {

    }

    @Override
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {

    }

    @Override
    public void onUpdateNotificationBuilder(PushNotificationBuilder pushNotificationBuilder) {
        pushNotificationBuilder.setmStatusbarIcon(R.mipmap.ic_launcher);
    }
}
