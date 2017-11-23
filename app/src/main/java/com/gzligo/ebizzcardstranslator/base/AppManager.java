package com.gzligo.ebizzcardstranslator.base;

import android.app.Activity;
import android.app.Application;

import com.gzligo.ebizzcardstranslator.base.delegate.IApp;
import com.gzligo.ebizzcardstranslator.utils.Preconditions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xfast on 2017/5/25.
 */

public class AppManager {
    private List<Activity> mActivityList = new LinkedList<>();
    private Activity mTopActivity;
    private IApp mIApp;

    private static class Singleton {
        private static AppManager sInstance = new AppManager();
    }

    private AppManager() {
    }

    public static AppManager get() {
        return Singleton.sInstance;
    }

    public void init(IApp iApp) {
        if (mIApp == null) {
            mIApp = Preconditions.checkNotNull(iApp);
        }
    }

    public Application getApplication() {
        return mIApp.getApplication();
    }

    public IApp getIApp() {
        return mIApp;
    }

    public void addActivity(Activity activity) {
        synchronized (AppManager.class) {
            if (!mActivityList.contains(activity)) {
                mActivityList.add(activity);
            }
        }
    }

    public void removeActivity(Activity activity) {
        synchronized (AppManager.class) {
            if (mActivityList.contains(activity)) {
                mActivityList.remove(activity);
            }
        }
    }

    public List<Activity> getActivities() {
        return Collections.unmodifiableList(mActivityList);
    }

    public Activity getTopActivity() {
        return mTopActivity;
    }

    public void setTopActivity(Activity activity) {
        mTopActivity = activity;
    }

    public Activity getPreviousActivity() {
        int count = mActivityList.size();
        if (count < 2) {
            return null;
        }
        return mActivityList.get(count - 2);
    }

    public void clearTopActivity(Activity activity) {
        if (mTopActivity == activity) {
            mTopActivity = null;
        }
    }

}
