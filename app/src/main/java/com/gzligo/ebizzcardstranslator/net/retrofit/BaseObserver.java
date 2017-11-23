package com.gzligo.ebizzcardstranslator.net.retrofit;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Lwd on 2017/8/15.
 */

public abstract class BaseObserver<T> implements Observer<T>{

    @Override
    public void onComplete() {
        //结束菊花，加载完毕
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        //可以弹出菊花，提示正在加载
    }

    @Override
    public void onError(@NonNull Throwable e) {
    }
}
