package com.applog;

import android.util.Log;

/**
 * Created by Loren.Li on 2017/7/20.
 */

public class LogTree extends Timber.DebugTree{

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE) {
            return;
        }
        String logMessage = tag + ": " + message;
        switch (priority) {
            case Log.DEBUG:
                Logger.d(logMessage);
                break;
            case Log.INFO:
                Logger.i(logMessage);
                break;
            case Log.WARN:
                Logger.w(logMessage);
                break;
            case Log.ERROR:
                Logger.e(logMessage);
                break;
        }
    }
}
