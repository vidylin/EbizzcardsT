package com.gzligo.ebizzcardstranslator.common;

import android.media.MediaRecorder;
import android.os.Handler;

import com.gzligo.ebizzcardstranslator.base.AppManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by Lwd on 2017/6/15.
 */

public class AudioManager {

    public static final int MSG_ERROR_AUDIO_RECORD = -4;//录音的时候出错
    private MediaRecorder mRecorder;
    private String mCurrentFilePathString;
    private Handler handler;
    private static final String TEMP_PATH = "temp.amr";

    private AudioManager() {
    }

    private static class Singleton {
        private static AudioManager sInstance = new AudioManager();
    }

    public static AudioManager getInstance() {
        return Singleton.sInstance;
    }

    public void setHandle(Handler handler) {
        this.handler = handler;
    }

    public interface AudioStageListener {
        void wellPrepared();
        void onStop(String filePath);
    }

    public AudioStageListener mListener;

    public void setOnAudioStageListener(AudioStageListener listener) {
        mListener = listener;
    }

    // 准备方法
    @SuppressWarnings("deprecation")
    public void prepareAudio() {
        try {
            File dir = new File(AppManager.get().getApplication().getFilesDir().getAbsolutePath(), "tempVoice");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, TEMP_PATH);
            mCurrentFilePathString = file.getAbsolutePath();
            mRecorder = new MediaRecorder();
            mRecorder.setOutputFile(mCurrentFilePathString);
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
            mRecorder.start();
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (handler != null) {
                handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (handler != null) {
                handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (handler != null) {
                handler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
            }
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (mRecorder == null)
            return;
        try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }catch (RuntimeException e){
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            File file = new File(mCurrentFilePathString);
            if (file.exists()){
                file.delete();
            }
            mCurrentFilePathString = null;
        }
        mListener.onStop(mCurrentFilePathString);
    }

    /**
     * 取消录音
     */
    public void cancelRecord(){
        if (mCurrentFilePathString != null) {
            File file = new File(mCurrentFilePathString);
            if (file.exists()){
                file.delete();
            }
            mCurrentFilePathString = null;
        }
        if (mRecorder == null)
            return;
        try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }catch (RuntimeException e){
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

}
