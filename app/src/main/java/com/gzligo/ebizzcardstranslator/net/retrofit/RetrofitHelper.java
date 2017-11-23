package com.gzligo.ebizzcardstranslator.net.retrofit;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by Lwd on 2017/5/23.
 */

public class RetrofitHelper {
    private final OkHttpClient mClient;
    private Retrofit mRetrofit;

    private RetrofitHelper() {
        mClient = OkHttpClientHelper.getInstance().getOkHttpClient();
    }

    private static class Singleton {
        private static RetrofitHelper sInstance = new RetrofitHelper();
    }

    public static RetrofitHelper getInstance() {
        return Singleton.sInstance;
    }

    //获取Retrofit对象
    public Retrofit getRetrofit(String url) {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ArbitraryResponseBodyConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(mClient)
                    .build();
        }
        return mRetrofit;
    }
}
