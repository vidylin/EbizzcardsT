package com.gzligo.ebizzcardstranslator.base.delegate;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.github.xgfjyw.webrtcclient.RTCWrapper;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.UncaughtCrashHandler;
import com.gzligo.ebizzcardstranslator.base.lifecycle.ActivityLifecycleCallbacksImpl;
import com.gzligo.ebizzcardstranslator.business.account.view.login.LoginPresenter;
import com.gzligo.ebizzcardstranslator.business.account.view.login.LoginRepository;
import com.gzligo.ebizzcardstranslator.image.ImageLoader;
import com.gzligo.ebizzcardstranslator.image.glide.GlideImageLoaderStrategy;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.zhy.autolayout.config.AutoLayoutConifg;

/**
 * Created by xfast on 2017/5/25.
 */

public class AppDelegateImpl implements IAppDelegate {

    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacksImpl();

    public AppDelegateImpl(IApp app) {
        AppManager.get().init(app);
    }

    @Override
    public void attachBaseContext() {
        MultiDex.install(AppManager.get().getApplication());
    }

    @Override
    public void onCreate() {
        UncaughtCrashHandler.install();
        AppManager.get().getApplication().registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        ImageLoader.get().init(new GlideImageLoaderStrategy());
        AutoLayoutConifg.getInstance().useDeviceSize();
        LanguageUtils.init(AppManager.get().getApplication());
        new LoginPresenter(new LoginRepository()).autoLogin();
        RTCWrapper.init(AppManager.get().getApplication());
    }

    @Override
    public void onTerminate() {
        AppManager.get().getApplication().unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }
}
