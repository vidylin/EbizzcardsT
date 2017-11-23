package com.gzligo.ebizzcardstranslator.push.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.gzligo.ebizzcardstranslator.push.utils.encryption.AESManager;


public class PreferencesUtils {

    /**
     * set and get String.
     *
     * @author Yjz
     * @Date 2015-11-25
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getPrefString(Context context, String key,String defaultValue) {
        return AESManager.getInstance(context).decryptString(key, defaultValue);
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        String aesValue = settings.getString(key, defaultValue);
//        String value = AESUtils.decrypt(key, aesValue);
//        Log.d("lw", "aes getPrefString aes value: " + aesValue + ",value: " + value + ",default value: " + defaultValue +  ",key: " + key);
//        if (TextUtils.isEmpty(value)) {
//            return defaultValue;
//        }
//        return value;
//        return aesValue;
    }

    public static void setPrefString(Context context, String key, String value) {
        AESManager.getInstance(context).encryptString(key, value);
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        try {
//            if (TextUtils.isEmpty(value)) {
//                settings.edit().putString(key, value).commit();
//                return;
//            }
//            String aesValue = AESUtils.encrypt(key, value);
//            Log.d("lw", "aes setPrefString aes value: " + aesValue + ",value: " + value +  ",key: " + key);
//            settings.edit().putString(key, aesValue).commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        settings.edit().putString(key, value).commit();
    }

    public static void setPrefSer(Context context,final String name){

    }

    /**
     * set and get boolean
     *
     * @author Yjz
     * @Date 2015-11-25
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getPrefBoolean(Context context,  String key, boolean defaultValue) {
//        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        return settings.getBoolean(key, defaultValue);
        return AESManager.getInstance(context).decryptBoolean(key, defaultValue);
    }

    public static void setPrefBoolean(Context context, String key, boolean value) {
//        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        settings.edit().putBoolean(key, value).commit();
        AESManager.getInstance(context).encryptBoolean(key, value);
    }


    /**
     * set and get int
     *
     * @author Yjz
     * @Date 2015-11-25
     * @param context
     * @param key
     * @param value
     */
    public static void setPrefInt(Context context, String key, int value) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        settings.edit().putInt(key, value).commit();
        AESManager.getInstance(context).encryptInteger(key, value);
    }

    public static int getPrefInt(Context context, String key, int defaultValue) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        return settings.getInt(key, defaultValue);
        return AESManager.getInstance(context).decryptInteger(key, defaultValue);
    }

    /**
     * set and get float
     *
     * @author Yjz
     * @Date 2015-11-25
     * @param context
     * @param key
     * @param value
     */
    public static void setPrefFloat(Context context, String key, float value) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        settings.edit().putFloat(key, value).commit();
        AESManager.getInstance(context).encryptFloat(key, value);
    }

    public static float getPrefFloat(Context context, String key, float defaultValue) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        return settings.getFloat(key, defaultValue);
        return AESManager.getInstance(context).decryptFloat(key, defaultValue);
    }

    /***
     * set and get long.
     */
    public static void setPreLong(Context context, String key, long value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putLong(key, value).commit();
    }

    public static long getPrefLong(Context context,  String key, long defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getLong(key, defaultValue);
    }

    /**
     * clear the preferences.
     */
    public static void clearPreference(Context context, SharedPreferences p) {
        Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }
}

