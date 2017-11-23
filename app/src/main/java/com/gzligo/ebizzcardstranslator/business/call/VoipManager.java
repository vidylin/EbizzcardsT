package com.gzligo.ebizzcardstranslator.business.call;

import android.content.Context;
import android.util.Base64;

import com.applog.Timber;
import com.github.xgfjyw.webrtcclient.RTCPeerConnection;
import com.github.xgfjyw.webrtcclient.RTCVideoView;
import com.github.xgfjyw.webrtcclient.RTCWrapper;
import com.github.xgfjyw.webrtcclient.voip.VoipCallback;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.utils.RxTimerUtil;

import org.json.JSONObject;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.MediaSource;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;


/**
 * Created by xfast on 2017/9/15.
 */

public class VoipManager {

    private VoipManager() {
        init();
    }
    private static class Singleton {
        private static VoipManager instance = new VoipManager();
    }

    public static VoipManager get() {
        return Singleton.instance;
    }


    private EglBase eglBase;

    private VideoSource localVideoSource;
    private VideoCapturer localVideoCapturer;

    private VideoTrack localVideoTrack;
    private VideoTrack remoteVideoTrack;

    private AudioTrack localAudioTrack;

    private String sessionID;
    private String voiceCallClientID;

    private final VoipManager.ProxyRenderer remoteProxyRenderer = new VoipManager.ProxyRenderer("remoteProxyRenderer");
    private final VoipManager.ProxyRenderer localProxyRenderer = new VoipManager.ProxyRenderer("localProxyRenderer");

    private RTCWrapper rtc_instance;

    private OnVoipListener onVoipListener;

    private boolean isInFrontCamera = true;// 本地视频源,初始时使用前置摄像头

    public String getVoiceCallClientID() {
        return voiceCallClientID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setVoiceCallClientID(String voiceCallClientID) {
        this.voiceCallClientID = voiceCallClientID;
    }

    private void init() {
        PeerConnectionFactory.initializeAndroidGlobals(androidContext(), true);
        EglBase.Context eglSharedContext = eglSharedContext();
        pcFactory().setVideoHwAccelerationOptions(eglSharedContext, eglSharedContext);
    }

    public void setOnVoipListener(OnVoipListener listener) {
        onVoipListener = listener;
    }

    public void releaseRenders() {
        if (localProxyRenderer != null) {
            localProxyRenderer.setTarget(null);
        }

        if (remoteProxyRenderer != null) {
            remoteProxyRenderer.setTarget(null);
        }
    }

    public void disconnect() {
        RxTimerUtil.cancel();
        if (rtc_instance != null) {
            rtc_instance.disconnect();
            rtc_instance = null;
        }
        releaseRenders();
        releaseLocalVideoCapturer();
        localVideoSource = null;
        localVideoTrack = null;
        remoteVideoTrack = null;
        localAudioTrack = null;
        isInFrontCamera = true;
    }

    public synchronized void connect(String rtcToken) {
        if (rtc_instance == null) {
            rtc_instance = new RTCWrapper();
        } else {
            return;//connected
        }
        try {
            String token = rtcToken.split("\\.")[1];
            String json = new String(Base64.decode(token, Base64.DEFAULT));
            JSONObject jsonObject = new JSONObject(json);
            String jti = jsonObject.getString(CommonConstants.PARAM_JTI);
            sessionID = jsonObject.getString(CommonConstants.PARAM_SID);

            rtc_instance.connect(jti, sessionID, rtcToken,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
//            rtc_instance.connect(jti, sessionID, rtcToken, null);

        rtc_instance.videoView = new RTCVideoView(new VoipCallback() {
            @Override
            public MediaSource onCreateLocalVideoSource() {
                return localVideoSource;
            }

            @Override
            public VideoTrack onCreateLocalVideoTrack() {
                return localVideoTrack;
            }

            @Override
            public void onLocalAudioTrack(AudioTrack audioTrack) {
                localAudioTrack = audioTrack;
            }

            @Override
            public void onRemoteVideoTrack(VideoTrack videoTrack) {
                remoteVideoTrack = videoTrack;
                remoteVideoTrack.addRenderer(new VideoRenderer(remoteProxyRenderer));
                if (onVoipListener != null) {
                    onVoipListener.onRemoteVideoAvailable();
                }
            }

            @Override
            public void onPeerConnectionClosed() {

            }

            @Override
            public void onUserMediaClosed() {

                disconnect();

                if (onVoipListener != null) {
                    onVoipListener.onDisconnected();
                    onVoipListener = null;
                }
            }
        });

    }

    public ProxyRenderer getLocalProxyRenderer() {
        return localProxyRenderer;
    }

    public ProxyRenderer getRemoteProxyRenderer() {
        return remoteProxyRenderer;
    }

    public PeerConnectionFactory pcFactory() {
        return RTCPeerConnection.factory();
    }

    public EglBase.Context eglSharedContext() {
        if (eglBase == null) {
            eglBase = EglBase.create();
        }
        return eglBase.getEglBaseContext();
    }

    public Context androidContext() {
        return AppManager.get().getApplication();
    }

    private void releaseLocalVideoCapturer() {
        if (localVideoCapturer == null) {
            return;
        }
        try {
            localVideoCapturer.stopCapture();
        } catch (InterruptedException e) {
            Timber.e(e);
        }
        localVideoCapturer.dispose();
        localVideoCapturer = null;
    }

    public void openLocalVideoCapturer() {
        if (localVideoCapturer != null) {
            return;
        }
        localVideoCapturer = openCameraCapture();
        localVideoSource = pcFactory().createVideoSource(localVideoCapturer);
        localVideoTrack = pcFactory().createVideoTrack("ARDAMSv0-android", localVideoSource);
        localVideoTrack.setEnabled(true);

        localVideoTrack.addRenderer(new VideoRenderer(localProxyRenderer));
    }

    private VideoCapturer openCameraCapture() {
        Context appContext = AppManager.get().getApplication();
        CameraEnumerator enumerator = Camera2Enumerator.isSupported(appContext) ? new Camera2Enumerator(appContext) : new Camera1Enumerator(true);
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Timber.d("Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName) && isInFrontCamera) {
                Timber.d("Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Timber.d("Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Timber.d("Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }


    public interface OnVoipListener {
        void onRemoteVideoAvailable();

        void onDisconnected();
    }


    public static class ProxyRenderer implements VideoRenderer.Callbacks {

        private VideoRenderer.Callbacks target;

        private String name;

        public ProxyRenderer(String name) {
            this.name = name;
        }

        public ProxyRenderer(VideoRenderer.Callbacks target) {
            this.target = target;
        }

        @Override
        public synchronized void renderFrame(VideoRenderer.I420Frame frame) {
            if (target == null) {
                Timber.d("Dropping frame in proxy because target is null.");
                VideoRenderer.renderFrameDone(frame);
                return;
            }

            target.renderFrame(frame);
        }

        public synchronized void setTarget(VideoRenderer.Callbacks target) {
            this.target = target;
        }

        public synchronized VideoRenderer.Callbacks getTarget() {
            return this.target;
        }
    }


    class VoipAPI {

        public abstract class OnSwitchCamera {
            public abstract void done();
        }

        /**
         * Switch camera to the next valid camera id. This can only be called while the camera is running.
         * This function can be called from any thread.
         */
        void switchCamera(final OnSwitchCamera onSwitchCamera) {
            if (localVideoCapturer != null && localVideoCapturer instanceof CameraVideoCapturer) {
                ((CameraVideoCapturer) localVideoCapturer).switchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
                    @Override
                    public void onCameraSwitchDone(boolean isFrontCamera) {
                        isInFrontCamera = isFrontCamera;
                        onSwitchCamera.done();
                    }

                    @Override
                    public void onCameraSwitchError(String errorDescription) {
                        Timber.e("switchCameraError: %s", errorDescription);
                    }
                });
            }
        }

        /**
         * Start capturing frames in a format that is as close as possible to |width| x |height| and
         * |framerate|.
         */
        void startCapture(int width, int height, int framerate) {
            if (localVideoCapturer != null) {
                localVideoCapturer.startCapture(width, height, framerate);
            }
        }

        boolean isInFrontCamera() {
            return isInFrontCamera;
        }

        boolean isMute() {
            if (localAudioTrack != null) {
                return !localAudioTrack.enabled();
            }
            return false;
        }

        void mute(boolean mute) {
            if (localAudioTrack != null) {
                localAudioTrack.setEnabled(!mute);
            }
        }
    }
}

