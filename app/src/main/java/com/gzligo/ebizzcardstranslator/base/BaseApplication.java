package com.gzligo.ebizzcardstranslator.base;

import android.app.Application;
import android.content.Context;

import com.gzligo.ebizzcardstranslator.base.delegate.AppDelegateImpl;
import com.gzligo.ebizzcardstranslator.base.delegate.IApp;
import com.gzligo.ebizzcardstranslator.base.delegate.IAppDelegate;

/**
 * Created by xfast on 2017/5/22.
 */

public class BaseApplication extends Application implements IApp {

    private IAppDelegate mDelegate;

    public BaseApplication() {
        mDelegate = new AppDelegateImpl(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mDelegate.attachBaseContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDelegate.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mDelegate.onTerminate();
    }

    @Override
    public Application getApplication() {
        return this;
    }

}
