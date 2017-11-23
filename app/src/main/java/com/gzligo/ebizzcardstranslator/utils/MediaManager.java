package com.gzligo.ebizzcardstranslator.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Lwd on 2017/6/13.
 */

public class MediaManager {
    private static MediaPlayer mPlayer;
    private static boolean isPause;
    private int position=-1;//播放item
    private boolean isTransVoice = false;

    private MediaManager() {
        mPlayer=new MediaPlayer();
    }

    private static class Singleton {
        private static MediaManager sInstance = new MediaManager();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isTransVoice() {
        return isTransVoice;
    }

    public void setTransVoice(boolean transVoice) {
        isTransVoice = transVoice;
    }

    public static MediaManager getInstance() {
        return Singleton.sInstance;
    }

    public void playSound(String filePathString,
                                  MediaPlayer.OnCompletionListener onCompletionListener,MediaPlayer.OnPreparedListener onPreparedListener) {
        // TODO Auto-generated method stub
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub
                mPlayer.reset();
                return false;
            }
        });
        if(mPlayer.isPlaying()){
            mPlayer.reset();
        }
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(onCompletionListener);
            mPlayer.setOnPreparedListener(onPreparedListener);
            mPlayer.setDataSource(filePathString);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //停止函数
    public void pause(){
        if (mPlayer!=null&&mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause=true;
        }
    }

    //继续
    public void resume() {
        if (mPlayer!=null&&isPause) {
            mPlayer.start();
            isPause=false;
        }
    }

    public void stop() {
        if(isPlaying()){
            mPlayer.stop();
            reset();
        }
    }

    public void release() {
        if (mPlayer!=null) {
            mPlayer.release();
        }
    }

    public void reset() {
        if (mPlayer!=null) {
            mPlayer.reset();
        }
    }

    public boolean isPlaying(){
        if (mPlayer!=null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

}
