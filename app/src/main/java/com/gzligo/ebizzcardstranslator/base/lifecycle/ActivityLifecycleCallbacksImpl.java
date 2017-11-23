package com.gzligo.ebizzcardstranslator.base.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.gzligo.ebizzcardstranslator.badge.ShortcutBadger;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.delegate.ActivityDelegateImpl;
import com.gzligo.ebizzcardstranslator.base.delegate.IActivity;
import com.gzligo.ebizzcardstranslator.base.delegate.IActivityDelegate;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;

/**
 * Created by xfast on 2017/5/25.
 */

public class ActivityLifecycleCallbacksImpl implements Application.ActivityLifecycleCallbacks {

    private FragmentLifecycleCallbacksImpl mFragmentLifecycleCallbacks;
    private int mFinalCount;

    public ActivityLifecycleCallbacksImpl() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        AppManager.get().addActivity(activity);
        initDelegate(activity);

        IActivityDelegate delegate = getDelegate(activity);
        if (delegate != null) {
            delegate.onCreate(savedInstanceState);
        }

        if (activity instanceof FragmentActivity) {
            if (mFragmentLifecycleCallbacks == null) {
                mFragmentLifecycleCallbacks = new FragmentLifecycleCallbacksImpl();
            }
            ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks, true);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mFinalCount++;
        if(mFinalCount==1){
            CommonBeanManager.getInstance().setBadgeCount(0);
            ShortcutBadger.applyCount(activity,0);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        AppManager.get().setTopActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        AppManager.get().clearTopActivity(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mFinalCount--;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        AppManager.get().removeActivity(activity);

        if (activity instanceof FragmentActivity) {
            if (mFragmentLifecycleCallbacks != null) {
                ((FragmentActivity) activity).getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks);
            }
        }

        IActivityDelegate delegate = getDelegate(activity);
        if (delegate != null) {
            delegate.onDestroy();
        }
    }


    private void initDelegate(Activity activity) {
        if (activity instanceof IActivity) {
            IActivityDelegate delegate = getDelegate(activity);
            if (delegate == null) {
                delegate = new ActivityDelegateImpl((IActivity) activity);
                ((IActivity) activity).setIDelegate(delegate);
            }
        }
    }

    private IActivityDelegate getDelegate(Activity activity) {
        IActivityDelegate delegate = null;
        if (activity instanceof IActivity) {
            delegate = ((IActivity) activity).getIDelegate();
        }
        return delegate;
    }
}
