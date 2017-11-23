package com.gzligo.ebizzcardstranslator.utils;

import android.media.MediaPlayer;

import com.waynell.videolist.widget.TextureVideoView;

/**
 * Created by Lwd on 2017/10/9.
 */

public abstract class MediaPlayerCallbackUtils implements TextureVideoView.MediaPlayerCallback{

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {

    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }
}
