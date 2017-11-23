package com.gzligo.ebizzcardstranslator.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lwd on 2017/7/7.
 */

public class SharedPreferencesUtils<T> {

    public void putSharedPreferences(final Context context,final Map<String,T> map, final String fileName) {
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                SharedPreferences sharedPreferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for(Map.Entry<String, T> entry : map.entrySet()){
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    String type = value.getClass().getSimpleName();
                    if ("Integer".equals(type)) {
                        editor.putInt(key, (Integer) value);
                    } else if ("Boolean".equals(type)) {
                        editor.putBoolean(key, (Boolean) value);
                    } else if ("String".equals(type)) {
                        editor.putString(key, (String) value);
                    } else if ("Float".equals(type)) {
                        editor.putFloat(key, (Float) value);
                    } else if ("Long".equals(type)) {
                        editor.putLong(key, (Long) value);
                    }
                }
                editor.commit();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void putSharedPreference(final Context context, final String key, final Object value, final String fileName) {
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                SharedPreferences sharedPreferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String type = value.getClass().getSimpleName();
                if ("Integer".equals(type)) {
                    editor.putInt(key, (Integer) value);
                } else if ("Boolean".equals(type)) {
                    editor.putBoolean(key, (Boolean) value);
                } else if ("String".equals(type)) {
                    editor.putString(key, (String) value);
                } else if ("Float".equals(type)) {
                    editor.putFloat(key, (Float) value);
                } else if ("Long".equals(type)) {
                    editor.putLong(key, (Long) value);
                }
                editor.commit();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public static Object getSharedPreferences(Context context, String key,Object defValue,String fileName) {
        String type = defValue.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        if ("Integer".equals(type)) {
            return sharedPreferences.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return sharedPreferences.getBoolean(key, (Boolean) defValue);
        } else if ("String".equals(type)) {
            return sharedPreferences.getString(key, (String) defValue);
        } else if ("Float".equals(type)) {
            return sharedPreferences.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return sharedPreferences.getLong(key, (Long) defValue);
        }
        return null;
    }

}
