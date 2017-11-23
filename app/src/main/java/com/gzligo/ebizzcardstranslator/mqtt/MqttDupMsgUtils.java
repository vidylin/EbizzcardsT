package com.gzligo.ebizzcardstranslator.mqtt;

import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by Lwd on 2017/8/1.
 */

public class MqttDupMsgUtils {
    private static final String TAG = MqttDupMsgUtils.class.getSimpleName();
    private LruCache dupMsg;

    private MqttDupMsgUtils() {
        dupMsg = new LruCache(5000);
    }

    private static class Singleton {
        private static MqttDupMsgUtils sInstance = new MqttDupMsgUtils();
    }

    public static MqttDupMsgUtils getInstance() {
        return Singleton.sInstance;
    }

    public boolean duplicate(String topic, String msgHashCode) {
        // FIXME 临时消息去重
        boolean enableDup = true;
        String hash = topic + ":/" + msgHashCode;
        Object value = dupMsg.get(hash);
        if (enableDup && value != null && TextUtils.equals((String) value, hash)) {// dup message
            Log.e(TAG, "topic: " + topic + ", duplicated message: " + value);
            return true;
        }
        dupMsg.put(hash, hash);
        return false;
    }
}
