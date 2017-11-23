package com.applog;

import android.content.Context;

import java.io.File;
import java.util.List;

/**
 * Created by Loren.Li on 2017/7/20.
 */

public class LogManager {
    static AppLogDiskStrategy appLogDiskStrategy;
    public static void initialize(Context context){
        appLogDiskStrategy = new AppLogDiskStrategy(context);
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodOffset(5)
                .methodCount(3)
                .tag("E-BIZZCARDS")
                .logStrategy(appLogDiskStrategy)
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));
        Timber.plant(new LogTree());
    }

    public static File getCurrentLogFile(){
        try {
            return appLogDiskStrategy.getCurrentLogFilePath();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<File> getLogFilesWithLimit(int limit){
        try {
            return appLogDiskStrategy.getAllLogFiles(limit);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
