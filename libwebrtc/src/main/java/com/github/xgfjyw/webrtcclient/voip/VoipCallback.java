package com.github.xgfjyw.webrtcclient.voip;

import org.webrtc.AudioTrack;
import org.webrtc.MediaSource;
import org.webrtc.VideoTrack;

/**
 * Created by xfast on 2017/9/18.
 */

public interface VoipCallback {
    MediaSource onCreateLocalVideoSource();

    VideoTrack onCreateLocalVideoTrack();

    void onLocalAudioTrack(AudioTrack audioTrack);

    void onRemoteVideoTrack(VideoTrack videoTrack);

    void onPeerConnectionClosed();

    void onUserMediaClosed();
}
