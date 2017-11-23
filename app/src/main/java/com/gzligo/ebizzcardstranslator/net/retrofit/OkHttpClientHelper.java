package com.gzligo.ebizzcardstranslator.net.retrofit;

import android.text.TextUtils;

import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Lwd on 2017/5/23.
 */

public class OkHttpClientHelper {
    private OkHttpClient mClient;
    private final static long TIMEOUT = 10000;  //超时时间
    private OkHttpClientHelper(){}
    private static class Singleton {
        private static OkHttpClientHelper sInstance = new OkHttpClientHelper();
    }

    public static OkHttpClientHelper getInstance() {
        return Singleton.sInstance;
    }

    public OkHttpClient getOkHttpClient() {
        if (mClient == null) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            mClient = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Request.Builder builder1 = request.newBuilder();
                            if(!TextUtils.isEmpty(HttpUtils.getAccessToken())){
                                builder1.addHeader("Authorization", "Bearer " + HttpUtils.getAccessToken());
                            }
                            Request build = builder1.addHeader("ApiVersion", "1.001").build();
                            return chain.proceed(build);
                        }
                    })
                    .addInterceptor(logInterceptor)
                    .build();
        }
        return mClient;
    }

    public void setClient(){
        mClient = null;
    }
}
