package com.gzligo.ebizzcardstranslator.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.constants.FileConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lwd on 2017/6/13.
 */

public class FileManager {
    private final String TAG = "FileManager";
    private Context context;
    public static FileManager manager;
    private File rootDir = null;
    private File chatDir = null;
    private File chatDirVoice = null;
    private File chatDirImage = null;
    private File chatDirVideo = null;
    private File videoDir = null;
    private File tempDir = null;
    private File chatTempDir = null;
    private File updateDir = null;

    public static FileManager getFileManager(Context context) {
        if (manager == null) {
            manager = new FileManager(context);
        }
        return manager;
    }

    public FileManager(Context context) {
        this.context = context;
        rootDir = initFileDir();
    }

    private File initFileDir() {
        File resultPath = null;
        if (context.getExternalFilesDir(FileConstants.ROOT_PATH) != null) {
            resultPath = context.getExternalFilesDir(FileConstants.ROOT_PATH);
        } else if (context.getFilesDir() != null) {
            resultPath = context.getFilesDir();
            resultPath = new File(resultPath + File.separator + FileConstants.ROOT_PATH);
        } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            resultPath = Environment.getExternalStorageDirectory();
            resultPath = new File(resultPath, FileConstants.ROOT_PATH);
        } else if (Environment.getExternalStorageDirectory() != null) {
            resultPath = Environment.getExternalStorageDirectory();
            resultPath = new File(resultPath, FileConstants.ROOT_PATH);
        }
        return resultPath;
    }

    public void createDir(String user) {
        chatDir = new File(rootDir, FileConstants.CHAT + FileConstants.FILE_SEPARATOR_UNDERLINE + user);
        if (!chatDir.exists()) {
            chatDir.mkdirs();
        }

        updateDir = new File(rootDir, FileConstants.UPDATE_POINT + FileConstants.FILE_SEPARATOR_UNDERLINE + user);
        if (!updateDir.exists()) {
            updateDir.mkdirs();
        }

        tempDir = new File(rootDir,FileConstants.TEMP_VIDEO + FileConstants.FILE_SEPARATOR_UNDERLINE + user );
        if(!tempDir.exists()){
            tempDir.mkdirs();
        }

        chatTempDir = new File(rootDir,FileConstants.CHAT_TEMP_VIDEO + FileConstants.FILE_SEPARATOR_UNDERLINE + user );
        if(!chatTempDir.exists()){
            chatTempDir.mkdirs();
        }

        videoDir = new File(rootDir,FileConstants.HISIR_VIDEO + FileConstants.FILE_SEPARATOR_UNDERLINE + user);
        if(!videoDir.exists()){
            videoDir.mkdirs();
        }

        chatDirVoice = new File(chatDir, FileConstants.CHAT_VOICE);
        chatDirImage = new File(chatDir, FileConstants.CHAT_IMG);
        chatDirVideo = new File(chatDir, FileConstants.CHAT_VIDEO);
        if (!chatDirImage.exists()) {
            chatDirImage.mkdirs();
        }
        if (!chatDirVoice.exists()) {
            chatDirVoice.mkdirs();
        }
        if (!chatDirVideo.exists()) {
            chatDirVideo.mkdirs();
        }
    }

    public File getVideoDir() {
        return videoDir;
    }
    public File getUpdateDir() {
        return updateDir;
    }

    public File getRootDir() {
        return rootDir;
    }

    public File getTempDir() {
        return tempDir;
    }

    public File getChatTempDir() {
        return chatTempDir;
    }

    public File saveChatImage(String msgID, byte[] fileSource) {
        String fileName = msgID + FileConstants.FILE_SEPARATOR_UNDERLINE + TimeUtils.getCurTime();
        String filePath = chatDirImage.getAbsolutePath() + File.separator + fileName;
        return FileUtils.saveFile(filePath, fileSource);
    }

    public File saveAssetChatImage(String msgID, String assetsName) {
        AssetManager am = context.getAssets();
        try {
            InputStream inputStream = am.open(assetsName.replace("asset:///",""));
            String fileName = msgID + FileConstants.FILE_SEPARATOR_UNDERLINE + TimeUtils.getCurTime();
            String filePath = chatDirImage.getAbsolutePath() + File.separator + fileName;
            return FileUtils.streamToFile(filePath, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String readAssetsToString(String assetsName) {
        return readAssetsToString(assetsName, null);
    }

    public String readAssetsToString(String assetsName, String charSet) {
        AssetManager am = context.getAssets();
        try {
            InputStream inputStream = am.open(assetsName.replace("asset:///",""));
            return FileUtils.convertStreamToString(inputStream, charSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File saveChatImageMedium(String msgID, String path) {
        Bitmap medBitmap = FileUtils.compressImageFromFile(320, 480, path);
        return FileUtils.saveBitmap2File(chatDirImage.getAbsolutePath(), medBitmap, msgID + FileConstants.FILE_SUFFIX_MEDIUM_JPG);
    }

    public File saveChatImageThumbnail(String msgID, String path) {
        Bitmap thumBitmap = FileUtils.compressImageFromFile(180, 240, path);
        return FileUtils.saveBitmap2File(chatDirImage.getAbsolutePath(), thumBitmap, msgID + FileConstants.FILE_SUFFIX_THUMBNAIL_JPG);
    }

    public File saveChatVoice(String msgID, byte[] fileSource) {
        String fileName = msgID + FileConstants.FILE_SUFFIX_AMR;
        String filePath = chatDirVoice.getAbsolutePath() + File.separator + fileName;
        return FileUtils.saveFile(filePath, fileSource);
    }

    public String saveChatVoice(String msgID, String voiceFilePath) {
        String fileName = msgID + FileConstants.FILE_SUFFIX_AMR;
        String filePath = chatDirVoice.getAbsolutePath() + File.separator + fileName;
        boolean result = FileUtils.copyFile(voiceFilePath, filePath);
        if (result) {
            return filePath;
        }
        return "";
    }

    public void saveChatFile(String msgID, byte[] fileSource) {
        String fileName = msgID + FileConstants.FILE_SEPARATOR_UNDERLINE + TimeUtils.getCurTime();
        String filePath = chatDir.getAbsolutePath() + File.separator + fileName;
        FileUtils.saveFile(filePath, fileSource);
    }

    public File saveChatBitmap(Bitmap bitmap, String fileName) {
        return FileUtils.saveBitmap2File(chatDir.getAbsolutePath(), bitmap, fileName);
    }

    public void saveFileToGallery(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File appDir = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String suffix = "";
        if (path.contains(".")) {
            suffix = path.substring(path.lastIndexOf("."), path.length());
        }else {
            suffix = ".jpg";
        }
        String newFileName = appDir.getAbsolutePath() + File.separator + TimeUtils.getCurTimeUseName() + suffix;
        if (path.contains("file://")) {
            path = path.replace("file://", "");
        }
        if (FileUtils.copyFile(path, newFileName)) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + newFileName)));
        }
    }

    public String saveVideoToGallery(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File appDir = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String newFileName = appDir.getAbsolutePath() + File.separator + TimeUtils.getCurTimeUseName() + FileConstants.FILE_SUFFIX_MP4;
        boolean isSuccess = FileUtils.copyFile(path, newFileName);
        if(isSuccess){
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + newFileName)));
            return newFileName;
        }
        return null;
    }

    public File getChatDir() {
        return chatDir;
    }

    public File getChatDirVoice() {
        return chatDirVoice;
    }

    public File getChatDirImage() {
        return chatDirImage;
    }

    public File getChatDirVideo() {
        return chatDirVideo;
    }

    public void saveConfigBeanFile(Object object, String fileName) {
        Log.i(TAG, "saveConfigBeanFile: file path: " + rootDir.getAbsolutePath() + ",file name: " + fileName + "," + object);
        if (object == null)
            return;
        FileUtils.saveObject(rootDir.getAbsolutePath(), object, fileName);
    }

    public Observable readConfigBeanFileObservable(final Class object) {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                Object o = FileUtils.readObject(rootDir.getAbsolutePath(), object.getSimpleName().toLowerCase());
                if (null == o){
                    o = new ArrayList<>();
                }
                e.onNext(o);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    public Observable readConfigBeanFileObservableMap(final Class object) {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                Object o = FileUtils.readObject(rootDir.getAbsolutePath(), object.getSimpleName().toLowerCase());
                if (null == o){
                    o = new TreeMap<>();
                }
                e.onNext(o);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    public String saveImageToAlbum(String filePath) {
        String newPath = rootDir.getAbsolutePath() + File.separator + TimeUtils.getSystemTime() + FileConstants.FILE_SUFFIX_JPG;
        FileUtils.copyFile(filePath, newPath, true);
        return newPath;
    }

    public synchronized void fileCopy(File dbFile, File backup) throws IOException {
        // TODO Auto-generated method stub
        FileChannel inChannel = new FileInputStream(dbFile).getChannel();
        FileChannel outChannel = new FileOutputStream(backup).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
}
