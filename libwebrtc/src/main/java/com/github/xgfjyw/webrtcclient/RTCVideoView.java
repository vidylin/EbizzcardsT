package com.github.xgfjyw.webrtcclient;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.xgfjyw.webrtcclient.voip.VoipCallback;

import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoTrack;

/**
 * Created by hongxiliang2 on 11/9/2017.
 */

public class RTCVideoView /*extends View*/ {
    private final static String TAG = "rtcVideoView";
    private RTCVideoViewCallback callback;

    private EglBase eglBase;
    private CameraVideoCapturer localCapturer;
    private VideoTrack localTrack, remoteTrack;
    private VideoRenderer localVideoRenderer, remoteVideoRenderer;
    private SurfaceViewRenderer localVideoView, remoteVideoView;

    private boolean previewLocal = false;

    public VoipCallback voipCallback;


    public RTCVideoView(VoipCallback voipCallback) {
        this.voipCallback = voipCallback;
    }

    public RTCVideoView(RTCVideoViewCallback cb) {
        callback = cb;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                localVideoView = new SurfaceViewRenderer(callback.getParentContext());
                localVideoView.init(eglBaseContext(), null);
                localVideoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
//              localVideoView.setZOrderMediaOverlay(true);
                localVideoView.setEnableHardwareScaler(true);

                remoteVideoView = new SurfaceViewRenderer(callback.getParentContext());
                remoteVideoView.init(eglBaseContext(), null);
                remoteVideoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
//              remoteVideoView.setZOrderMediaOverlay(true);
                remoteVideoView.setEnableHardwareScaler(true);
            }
        });
    }

    public void close() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (localVideoRenderer != null)
                    unsetLocalVideoTrack();
                if (remoteVideoRenderer != null)
                    unsetRemoteVideoTrack();

                if (localCapturer != null) {
                    try {
                        localCapturer.stopCapture();
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.toString());
                    }

                }

                if (localVideoView != null) {
                    localVideoView.release();
                    localVideoView = null;
                }
                if (remoteVideoView != null) {
                    remoteVideoView.release();
                    remoteVideoView = null;
                }
                if (eglBase != null) {
                    eglBase.release();
                    eglBase = null;
                }

                if (localCapturer != null) {
                    localCapturer.dispose();
                    localCapturer = null;
                }

                RTCWrapper.videoView = null;
            }
        });
    }

    public void setLocalVideoTrack(final VideoTrack track, final CameraVideoCapturer videoCapturer) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                localTrack = track;
                localCapturer = videoCapturer;

                if (previewLocal) {
                    localVideoRenderer = new VideoRenderer(localVideoView);
                    localTrack.addRenderer(localVideoRenderer);
                }
            }
        });
    }

    public void unsetLocalVideoTrack() {
        if (voipCallback != null) {
            voipCallback.onUserMediaClosed();
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (localVideoRenderer != null) {
                    localTrack.removeRenderer(localVideoRenderer);
                    localVideoRenderer = null;
                }

                if (remoteVideoRenderer == null)
                    close();
            }
        });
    }

    public void setRemoteVideoTrack(final VideoTrack track) {
        if (voipCallback != null) {
            voipCallback.onRemoteVideoTrack(track);
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                remoteTrack = track;
                remoteVideoRenderer = new VideoRenderer(remoteVideoView);
                remoteTrack.addRenderer(remoteVideoRenderer);
//                callback.videoDidStart(videoRenderer);
            }
        });
    }

    public void unsetRemoteVideoTrack() {
        if (voipCallback != null) {
            voipCallback.onPeerConnectionClosed();
            return;
        }
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
//                callback.videoDidStop(videoRenderer);
                if (remoteVideoRenderer != null) {
                    remoteTrack.removeRenderer(remoteVideoRenderer);
                    remoteVideoRenderer = null;
                }

                if (localVideoRenderer == null)
                    close();
            }
        });
    }

    public void previewLocalVideo(final boolean yes) {
        if (previewLocal == yes)
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                previewLocal = yes;
                if (previewLocal) {
                    if (localVideoRenderer == null)
                        localVideoRenderer = new VideoRenderer(localVideoView);
                    localTrack.addRenderer(localVideoRenderer);
                } else {
                    localTrack.removeRenderer(localVideoRenderer);
                    localVideoRenderer = null;
                }
            }
        });
    }

    public void switchCamera() {
        if (localCapturer != null) {
            localCapturer.switchCamera(null);
        }
    }

    private EglBase.Context eglBaseContext() {
        if (eglBase == null)
            eglBase = EglBase.create();
        return eglBase.getEglBaseContext();
    }

    private static void runOnUiThread(Runnable block) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block.run();
        } else {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(block);
        }
    }
}
