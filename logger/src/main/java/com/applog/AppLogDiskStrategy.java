package com.applog;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Loren.Li on 2017/7/20.
 */
public class AppLogDiskStrategy implements LogStrategy {
    public static String TAG = "AppLogDiskStrategy";
    private WriteHandler handler;
    private Context context;
    private int MAX_SIZE = 10;
    private final String FILE_DIR = "AppLog";
    public static String DEFAULT_PATH;

    public AppLogDiskStrategy(Context context) {
        this.context = context;
        initDir();
        handler = new WriteHandler(context.getMainLooper(), DEFAULT_PATH, MAX_SIZE);
    }

    public void initDir(){
        File resultPath = null;
        if (context.getExternalFilesDir(FILE_DIR) != null) {
            resultPath = context.getExternalFilesDir(FILE_DIR);
        } else if (context.getFilesDir() != null) {
            resultPath = context.getFilesDir();
            resultPath = new File(resultPath + File.separator + FILE_DIR);
        } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            resultPath = Environment.getExternalStorageDirectory();
            resultPath = new File(resultPath, FILE_DIR);
        } else if (Environment.getExternalStorageDirectory() != null) {
            resultPath = Environment.getExternalStorageDirectory();
            resultPath = new File(resultPath, FILE_DIR);
        }
        try {
            DEFAULT_PATH = resultPath.getAbsolutePath();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(int i, String tag, String message) {
        switch (i) {
            case Log.DEBUG:
                Log.d(tag, message);
                break;
            case Log.INFO:
                Log.i(tag, message);
                break;
            case Log.ERROR:
                Log.e(tag, message);
                break;
            case Log.WARN:
                Log.w(tag, message);
                break;
        }
        this.handler.sendMessage(this.handler.obtainMessage(i, message));
    }

    static class WriteHandler extends Handler {
        private final String folder;
        private final int maxFileSize;
        private SimpleDateFormat simpleDateFormat;
        private SimpleDateFormat logDateFormat;
        private File logFile;

        WriteHandler(Looper looper, String folder, int maxFileSize) {
            super(looper);
            Log.w(TAG, "WriteHandler 初始化日志文件写入器-------------------------------------"
                    + "\n当前线程：" + looper.getThread() + "\n日志存储文件夹：" + folder + "\n日志文件最大存放量：" + maxFileSize);
            this.folder = folder;
            this.maxFileSize = maxFileSize;
            this.simpleDateFormat = new SimpleDateFormat("yyyyMMdd-");
            this.logDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
        }

        public void handleMessage(Message msg) {
            String content = (String)msg.obj;
            FileWriter fileWriter = null;
            getFileName();
            logFile = this.getLogFile(this.folder, getFileName());
            try {
                fileWriter = new FileWriter(logFile, true);
                this.writeLog(fileWriter, content);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                if(fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        }

        private void writeLog(FileWriter fileWriter, String content) throws IOException {
            if (content.contains("┌")) {
                String heard = content.substring(0, content.length() - 14);
                String one = heard.substring(0, heard.length()/2);
                String two = heard.substring(heard.length()/2, heard.length());
                content = one + logDateFormat.format(new Date()) + two;
            }
            fileWriter.append("\n");
            fileWriter.append(content);
        }

        private File getLogFile(String folderName, String fileName) {
            File folder = new File(folderName);
            if(!folder.exists()) {
                folder.mkdirs();
            }
            File[] logFiles = folder.listFiles();
            if (logFiles != null) {
                if (logFiles.length > maxFileSize) {
                    int size = logFiles.length - maxFileSize;
                    for (int i = 0; i < size; i++) {
                        logFiles[i].delete();
                    }
                }
            }
            File logFile = new File(folderName, fileName);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return logFile;
        }

        public File getCurrentLogFilePath(){
            if (logFile == null) {
                return null;
            }
            return logFile;
        }

        private String getFileName(){
            return simpleDateFormat.format(new Date()) + "logs";
        }
    }

    public File getCurrentLogFilePath(){
        return handler.getCurrentLogFilePath();
    }

    public List<File> getAllLogFiles(int limit){
        try {
            File fileDir = new File(DEFAULT_PATH);
            if (fileDir.exists()) {
                File[] files = fileDir.listFiles();
                List<File> fileList = new ArrayList<>();
                int start = 0;
                if (limit != 0 && files.length > limit) {
                    start = files.length - limit;
                }
                for (int i = start; i < files.length; i++) {
                    fileList.add(files[i]);
                }
                return fileList;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
