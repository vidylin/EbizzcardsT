package com.gzligo.ebizzcardstranslator.net.retrofit;

import android.util.Log;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Lwd on 2017/6/5.
 */

public class HttpLogger implements HttpLoggingInterceptor.Logger{
    @Override
    public void log(String message) {
        Log.e("HttpLogInfo", message);
    }
}
