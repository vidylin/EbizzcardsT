package com.gzligo.ebizzcardstranslator.base;

import android.util.Log;

/**
 * Created by xfast on 2017/7/14.
 */

public class UncaughtCrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "ligo-Crash";
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e(TAG, String.format("thread id=%s name=%s, crash: %s", t.getId(), t.getName(), Log.getStackTraceString(e)));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }
    }

    public static void install() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtCrashHandler());

    }
}
