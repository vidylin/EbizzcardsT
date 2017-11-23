package com.gzligo.ebizzcardstranslator.push.utils.encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Loren.Li on 2017/5/24.
 */

public class AESManager {
    public volatile static AESManager sInstance;

    public static AESManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AESManager(context);
        }
        return sInstance;
    }

    private AESEncryptor aesEncryptor;
    private EncryptPrefHelper encryptPrefHelper;

    public AESManager(Context context) {
        String key = "1234567890123456";
        aesEncryptor = new AESEncryptor.Builder().setKey(key.getBytes()).buildQuietly();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        encryptPrefHelper = new EncryptPrefHelper(settings, aesEncryptor, false);
    }

    public void encryptString(String key, String value) {
        encryptPrefHelper.putEncryptedString(key, value);
    }

    public String decryptString(String key, String defaultValue) {
        return encryptPrefHelper.getDecryptedString(key, defaultValue);
    }

    public void encryptBoolean(String key, boolean value) {
        encryptPrefHelper.putEncryptedBoolean(key, value);
    }

    public boolean decryptBoolean(String key, boolean defaultValue) {
        return encryptPrefHelper.getDecryptedBoolean(key, defaultValue);
    }

    public void encryptFloat(String key, float value) {
        encryptPrefHelper.putEncryptedFloat(key, value);
    }

    public float decryptFloat(String key, float defaultValue) {
        return encryptPrefHelper.getDecryptedFloat(key, defaultValue);
    }

    public void encryptInteger(String key, int value) {
        encryptPrefHelper.putEncryptedInt(key, value);
    }

    public int decryptInteger(String key, int defaultValue) {
        return encryptPrefHelper.getDecryptedInt(key, defaultValue);
    }
}
