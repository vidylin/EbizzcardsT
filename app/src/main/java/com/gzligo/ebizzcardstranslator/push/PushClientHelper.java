package com.gzligo.ebizzcardstranslator.push;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.push.except.ManufacturerPushChannelNotAvailableException;
import com.gzligo.ebizzcardstranslator.push.receiver.fcm.HisirFirebaseInstanceIdService;
import com.gzligo.ebizzcardstranslator.push.receiver.fcm.HisirFirebaseMessagingService;
import com.gzligo.ebizzcardstranslator.push.utils.DialogUtils;
import com.gzligo.ebizzcardstranslator.push.utils.PreferencesUtils;
import com.gzligo.ebizzcardstranslator.push.utils.PushConstants;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;


/**
 * Created by Lwd on 2017/9/5.
 */

public class PushClientHelper {
    private final static String TAG = "PushClientHelper";
    private static OnRegisterPushListener pushListener;
    public final static int HANDLER_CMD_SHOW_PASSTHROUGH_MESSAGE = 1;

    private static String APP_ID;
    private static String APP_KEY;

    private HuaweiApiClient mHuaweiApiClient;
    private static Context mApplication;
    private int CHECK_TIME = 90 * 1000;

    private PushClientHelper() {
    }

    private static class Singleton {
        private static PushClientHelper sInstance = new PushClientHelper();
    }

    public static PushClientHelper getInstance(Context context) {
        mApplication = context;
        return Singleton.sInstance;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_CMD_SHOW_PASSTHROUGH_MESSAGE:
                    Toast.makeText(mApplication, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private Runnable checkTokenRun = new Runnable() {
        @Override
        public void run() {
            String huawei = PreferencesUtils.getPrefString(mApplication, PushConstants.SharedPreferences.UP_LOAD_TOKEN_HUAWEI_TOKEN, "");
            String xiaomi = PreferencesUtils.getPrefString(mApplication, PushConstants.SharedPreferences.UP_LOAD_TOKEN_XIAOMI_TOKEN, "");
            String flyme = PreferencesUtils.getPrefString(mApplication, PushConstants.SharedPreferences.UP_LOAD_TOKEN_MEIZU_TOKEN, "");
            String fcm = PreferencesUtils.getPrefString(mApplication, PushConstants.SharedPreferences.UP_LOAD_TOKEN_FCM_TOKEN, "");
            if (TextUtils.isEmpty(huawei) && TextUtils.isEmpty(xiaomi) && TextUtils.isEmpty(flyme) && TextUtils.isEmpty(fcm)) {
                registerPushService();
            } else {
                Log.i(TAG, "checkTokenRun is already get token success, remove Runnable !!!");
                mHandler.removeCallbacks(checkTokenRun);
            }
        }
    };

    /**
     * Register push service, according to manufacturer.
     */
    public boolean registerPushService() {
        String manu = Build.MANUFACTURER.toLowerCase();
        Log.i(TAG, "registerPushService: MANUFACTURER: " + manu);
        boolean isManufacturer = false;
        try {
            switch (manu) {
                case PushConstants.SharedPreferences.UP_LOAD_TOKEN_MEIZHU:
                case PushConstants.SharedPreferences.UP_LOAD_TOKEN_MEIZU_ALIAS:
                    initMeizuPush();
                    isManufacturer = true;
                    break;
                case PushConstants.SharedPreferences.UP_LOAD_TOKEN_HUAWEI:
                    initHuaweiPush();
                    isManufacturer = true;
                    break;
                case PushConstants.SharedPreferences.UP_LOAD_TOKEN_XIAOMI:
                    initXiaomiPush();
                    isManufacturer = true;
                    break;
                default:
                    Log.i(TAG, "registerPushService: default: " + manu);
                    break;
            }
        } catch (ManufacturerPushChannelNotAvailableException mpcnaex) {
            Log.i(TAG, "registerPushService: ManufacturerPushChannelNotAvailableException: " + manu);
        }
        mHandler.postDelayed(checkTokenRun, CHECK_TIME);
        return isManufacturer;
    }

    private void initMeizuPush() {
        APP_ID = mApplication.getString(R.string.flyme_app_id);
        APP_KEY = mApplication.getString(R.string.flyme_app_key);
        if (shouldInit()) {
            com.meizu.cloud.pushsdk.PushManager.register(mApplication, APP_ID, APP_KEY);
        }
    }

    private void initHuaweiPush() throws ManufacturerPushChannelNotAvailableException {
        int errCode = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(mApplication);
        String errMsgStr = getConnectErrMsg4Huawei(errCode);
        Log.i(TAG, String.format("initHuaweiPush, HuaweiMobileServicesAvailable: errCode=%d, message=%s.", errCode, errMsgStr));
        if (errCode == ConnectionResult.SUCCESS) {
            mHuaweiApiClient = buildHuaweiApiClient();
            mHuaweiApiClient.connect();
        } else { // Connection to HMS failed, such as HMS missing or HMS upgrading etc, then using xiaomi push instead.
            showMessageDialog(errCode);
        }
    }

    private void initXiaomiPush() {
        APP_ID = mApplication.getString(R.string.xiaomi_app_id);
        APP_KEY = mApplication.getString(R.string.xiaomi_app_key);
        if (shouldInit()) {
            MiPushClient.registerPush(mApplication.getApplicationContext(), APP_ID, APP_KEY);
        }
    }

    private void initFCMPush() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(mApplication);
        Log.i(TAG, "registerReceiver resultCode: " + resultCode);
        mApplication.startService(new Intent(mApplication, HisirFirebaseInstanceIdService.class));
        mApplication.startService(new Intent(mApplication, HisirFirebaseMessagingService.class));
    }

    private boolean shouldInit() {
        try {
            ActivityManager am = ((ActivityManager) mApplication.getSystemService(Context.ACTIVITY_SERVICE));
            List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
            String mainProcessName = mApplication.getPackageName();
            int myPid = Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info : processInfos) {
                if (info.pid == myPid && TextUtils.equals(mainProcessName, info.processName)) {
                    return true;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerPushService(OnRegisterPushListener listener) {
        this.pushListener = listener;
        boolean result = registerPushService();
        if (!result) {
            initFCMPush();
        }
        return result;
    }

    /**
     * Transform error code to literal message, for huawei.
     *
     * @param errno
     * @return
     */
    private String getConnectErrMsg4Huawei(int errno) {
        String resultStr = "Success";
        if (errno == ConnectionResult.SERVICE_MISSING)
            resultStr = "SERVICE_MISSING";
        else if (errno == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED)
            resultStr = "SERVICE_VERSION_UPDATE_REQUIRED";
        else if (errno == ConnectionResult.SERVICE_DISABLED)
            resultStr = "SERVICE_DISABLED";
        else if (errno == ConnectionResult.SERVICE_INVALID)
            resultStr = "SERVICE_INVALID";

        return resultStr;
    }

    /**
     * Build HMS Client.
     *
     * @return HMS Client.
     */
    private HuaweiApiClient buildHuaweiApiClient() {
        if (mApplication == null)
            return null;

        final Context ctx = mApplication;
        mHuaweiApiClient = new HuaweiApiClient.Builder(ctx).addApi(HuaweiPush.PUSH_API)
                .addConnectionCallbacks(new HuaweiApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected() {
                        Log.i(TAG, "buildHuaweiApiClient, huawei Connected.");
                        new Thread() {
                            @Override
                            public void run() {
                                HuaweiPush.HuaweiPushApi.enableReceiveNormalMsg(mHuaweiApiClient, true);
                                HuaweiPush.HuaweiPushApi.enableReceiveNotifyMsg(mHuaweiApiClient, true);
                                PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(mHuaweiApiClient);
                                tokenResult.setResultCallback(new ResultCallback<TokenResult>() {
                                    @Override
                                    public void onResult(TokenResult result) {
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                // 状态结果通过广播返回
                                                HuaweiPush.HuaweiPushApi.getPushState(mHuaweiApiClient);
                                            }
                                        }.start();
                                    }
                                });
                            }
                        }.start();
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        // Test Code Block start.
                        Log.i(TAG, "buildHuaweiApiClient, huawei onConnectionSuspended, cause: " + cause);
                        int resultFlag = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(ctx);
                        String resultStr = getConnectErrMsg4Huawei(resultFlag);
                        Log.i(TAG, String.format("buildHuaweiApiClient, HuaweiMobileServicesAvailable: errCode=%d, message:%s.", resultFlag, resultStr));
                        // Test Code Block end.
                    }
                })
                .addOnConnectionFailedListener(new HuaweiApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.i(TAG, "buildHuaweiApiClient, ErrorCode: " + connectionResult.getErrorCode());
                        Log.i(TAG, "buildHuaweiApiClient, HuaweiMobileServicesAvailable: " + HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(ctx));
                        // TODO: Add more robust and flexible error dealing code here.
                    }
                })
                .build();
        return mHuaweiApiClient;
    }

    private Dialog pushErrorDialog = null;

    private void showMessageDialog(int errno) {
        final Context ctx = mApplication;
        if (pushErrorDialog != null) {
            pushErrorDialog.dismiss();
            pushErrorDialog = null;
        }
        if (errno == ConnectionResult.SERVICE_MISSING) {
            pushErrorDialog = DialogUtils.showTextMsgDialog(ctx,
                    R.string.hisirpush_cancel,
                    R.string.hisirpush_confirm,
                    new DialogUtils.DialogCallback.OnDialogClickListener() {
                        @Override
                        public void onConfirm() {

                        }
                    },
                    ctx.getString(R.string.hwpush_connect_failed_hms_missing),
                    ctx.getString(R.string.hwpush_connect_failed_hms_missing_recommend)
            );
        } else if (errno == ConnectionResult.SERVICE_DISABLED || errno == ConnectionResult.SERVICE_INVALID) {
            pushErrorDialog = DialogUtils.showTextMsgDialog(ctx,
                    R.string.hisirpush_cancel,
                    R.string.hisirpush_confirm,
                    new DialogUtils.DialogCallback.OnDialogClickListener() {
                        @Override
                        public void onConfirm() {

                        }
                    },
                    ctx.getString(R.string.hwpush_connect_failed_hms_disabled),
                    " or " + ctx.getString(R.string.hwpush_connect_failed_hms_invalid),
                    ctx.getString(R.string.hwpush_connect_failed_hms_disabled_or_invalid_recommend)
            );
        } else if (errno == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            pushErrorDialog = DialogUtils.showTextMsgDialog(ctx,
                    R.string.hisirpush_cancel,
                    R.string.hisirpush_confirm,
                    new DialogUtils.DialogCallback.OnDialogClickListener() {
                        @Override
                        public void onConfirm() {
                            Uri uri = Uri.parse("market://details?id=com.huawei.hwid");
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            try {
                                mApplication.startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    ctx.getString(R.string.hwpush_connect_failed_hms_version_update_required),
                    ctx.getString(R.string.hwpush_connect_failed_hms_version_update_required_recommend)
            );
        }
    }

    public static class PushReceiver extends BroadcastReceiver {

        public PushReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("push_token")) {
                String pushToken = intent.getStringExtra("push_token");
                Log.i(TAG, "PushReceiver onReceive: " + pushToken + ",pushListener: " + pushListener);
                if (pushListener != null) {
                    pushListener.registerPushFinish(pushToken);
                }
            }
        }
    }

    public interface OnRegisterPushListener {
        void registerPushFinish(String pushToken);
    }

}
