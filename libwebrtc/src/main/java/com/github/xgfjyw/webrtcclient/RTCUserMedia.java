package com.github.xgfjyw.webrtcclient;

import android.util.Log;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSStaticFunction;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaSource;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.LinkedList;

/**
 * Created by hongxiliang2 on 25/8/2016.
 */
public class RTCUserMedia extends ScriptableObject {
    @Override
    public String getClassName() {
        return "_getUserMedia";
    }
    public RTCUserMedia() {}

    private static final String TAG = "rtcUserMedia";
    private MediaStream stream;
    private LinkedList<MediaSource> mediaSources = new LinkedList<MediaSource>();

    @JSConstructor
    public RTCUserMedia(Scriptable constraints, Function success, Function failure) {
        JSCore.print_debug_info("RTCUserMedia");

        if (!PeerConnectionFactory.initializeAndroidGlobals(JSCore.androidContext().getApplicationContext(), true, true, true))
            Log.i(TAG, "initializeAndroidGlobals failed");
        else
            Log.i(TAG, "initializeAndroidGlobals ok");


        Context ctx = Context.enter();
        ctx.setOptimizationLevel(-1);
        try {
            if (createMediaStream(constraints)) {
                Object[] args = new Object[]{ this };
                success.call(ctx, JSCore.scope(), ctx.newObject(JSCore.scope()), args);
            } else {
                Object[] args = new Object[]{ "create media failed" };
                failure.call(ctx, JSCore.scope(), ctx.newObject(JSCore.scope()), args);
            }

        } catch (EcmaError ex) {
            Log.e(TAG, ex.toString());
        } catch (UnsupportedOperationException ex) {
            Log.e(TAG, ex.toString());

        } finally {
            Context.exit();
        }
    }

    public RTCUserMedia(MediaStream stream) {
        this.stream = stream;
    }

    private boolean createMediaStream(Scriptable constraints) {
        PeerConnectionFactory mFactory = RTCPeerConnection.factory();
        MediaStream mediaStream = mFactory.createLocalMediaStream("ARDAMS-android");

        constraints = new NativeObject();
        if (RTCWrapper.videoView != null)
            constraints.put("video", constraints, true);
        constraints.put("audio", constraints, true);


        boolean ok = false;
        if (constraints.has("video", constraints)) {
            Object video = constraints.get("video", constraints);
            VideoSource videoSource = null;

            switch (video.getClass().getName()) {
                case "java.lang.Boolean"://Boolean:
                    boolean useVideo = (boolean) video;
                    if (useVideo) {
                        if (RTCWrapper.videoView != null && RTCWrapper.videoView.voipCallback != null) {
                            mediaSources.add(RTCWrapper.videoView.voipCallback.onCreateLocalVideoSource());
                            mediaStream.addTrack(RTCWrapper.videoView.voipCallback.onCreateLocalVideoTrack());
                        }
                    }
//                    if (useVideo) {
//                        CameraEnumerator enumerator = null;
//                        CameraVideoCapturer videoCapturer = null;
//                        if (Camera2Enumerator.isSupported(JSCore.androidContext()))
//                            enumerator = new Camera2Enumerator(JSCore.androidContext());
//                        else
//                            enumerator = new Camera1Enumerator();
//
//                        final String[] deviceDescs = enumerator.getDeviceNames();
//                        for (String deviceDesc : deviceDescs) {
//                            videoCapturer = enumerator.createCapturer(deviceDesc, null);
//                            if (videoCapturer != null)
//                                break;
//                        }
//
//                        /*if (videoCapturer == null) {
//                            throw new Exception("initialize camera failed.");
//                        }*/
//
////                        String name = CameraEnumerationAndroid.getNameOfFrontFacingDevice();
////                        VideoCapturerAndroid v = VideoCapturerAndroid.create(name, new VideoCapturerAndroid.CameraEventsHandler() {
////                            @Override
////                            public void onCameraError(String error) {
////                                Log.i(TAG, "onCameraError: " + error);
////                            }
////
////                            @Override
////                            public void onCameraFreezed(String message) {
////                                Log.i(TAG, "onCameraFreezed: " + message);
////                            }
////
////                            @Override
////                            public void onCameraOpening(int var) {
////                                Log.i(TAG, "onCameraOpening: " + var);
////                            }
////
////                            @Override
////                            public void onFirstFrameAvailable() {
////                                Log.i(TAG, "onFirstFrameAvailable");
////                            }
////
////                            @Override
////                            public void onCameraClosed() {
////                                Log.i(TAG, "onCameraClosed");
////                            }
////                        });
//                        MediaConstraints videoConstraints = new MediaConstraints();
//                        videoSource = mFactory.createVideoSource(videoCapturer);
//                        mediaSources.add(videoSource);
//                        int w = 320;
//                        int h = 240;
//                        int fps = 15;
//                        videoCapturer.startCapture(w, h, fps);
//
//                        VideoTrack localVideoTrack = mFactory.createVideoTrack("ARDAMSv0-android", videoSource);
//                        localVideoTrack.setEnabled(true);
//                        RTCWrapper.videoView.setLocalVideoTrack(localVideoTrack, videoCapturer);
//
//                        mediaStream.addTrack(localVideoTrack);
//                    }
                    break;
                case "org.mozilla.javascript.NativeObject":
                    NativeObject useVideoMap = (NativeObject) video;
                    if (useVideoMap.containsKey("optional")) {
//                        if (useVideoMap.getType("optional") == ReadableType.Array) {
//                            ReadableArray options = useVideoMap.getArray("optional");
//                            for (int i = 0; i < options.size(); i++) {
//                                if (options.getType(i) == ReadableType.Map) {
//                                    ReadableMap option = options.getMap(i);
//                                    if (option.hasKey("sourceId") && option.getType("sourceId") == ReadableType.String) {
//                                        videoSource = mFactory.createVideoSource(getVideoCapturerById(Integer.parseInt(option.getString("sourceId"))), videoConstraints);
//                                    }
//                                }
//                            }
//                        }
                    }
                    break;

//                videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(320)));
//                videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(240)));
//                videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(15)));
//                videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(1)));
            }


            /*if (videoSource != null) {
                VideoTrack videoTrack = mFactory.createVideoTrack("ARDAMSv0", videoSource);
                mediaStream.addTrack(videoTrack);
                Log.i("dfs", "video track added");
                ok = true;
            }*/
        }

        if (constraints.has("audio", constraints)) {
            Object audio = constraints.get("audio", constraints);
            if (!(audio instanceof Boolean))
                ok = false;

            boolean useAudio = (boolean) audio;
            if (useAudio) {
                MediaConstraints audioConstarints = new MediaConstraints();
                audioConstarints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "true"));
                audioConstarints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
                audioConstarints.mandatory.add(new MediaConstraints.KeyValuePair("echoCancellation", "true"));
                audioConstarints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation2", "true"));
                audioConstarints.mandatory.add(new MediaConstraints.KeyValuePair("googDAEchoCancellation", "true"));

                AudioSource audioSource = mFactory.createAudioSource(audioConstarints);
                mediaSources.add(audioSource);
                AudioTrack audioTrack = mFactory.createAudioTrack("ARDAMSa0-android", audioSource);



                if (RTCWrapper.videoView != null && RTCWrapper.videoView.voipCallback != null) {
                    RTCWrapper.videoView.voipCallback.onLocalAudioTrack(audioTrack);
                }



                mediaStream.addTrack(audioTrack);
                Log.i(TAG, "audio track added");
                ok = true;
            }
        }

        if (ok)
            stream = mediaStream;
        return ok;
    }

    public MediaStream mediaStream() {
        return stream;
    }

    @JSFunction
    public void close() {
        Log.w(TAG, "userMedia closing");

        if (RTCWrapper.videoView != null)
            RTCWrapper.videoView.unsetLocalVideoTrack();

        if (stream != null) {
            for (AudioTrack track : stream.audioTracks) {
                track.setEnabled(false);
//                track.setState(MediaStreamTrack.State.ENDED);
                stream.removeTrack(track);
                track.dispose();
            }

            for (VideoTrack track : stream.videoTracks) {
                track.setEnabled(false);
//                track.setState(MediaStreamTrack.State.ENDED);
                stream.removeTrack(track);
                track.dispose();
            }

            for (MediaSource mediaSource : mediaSources) {
                mediaSource.dispose();
            }
        } else {
            Log.d(TAG, "userMedia.close() stream is null");
        }
    }

    /* not used, to be delete */
    @JSStaticFunction
    public static void addVideoStream(final RTCUserMedia stream) {
        Log.i(TAG, "add video stream object " + stream.getClass().getName());
        if (!stream.getClass().getName().equals("com.github.xgfjyw.webrtcclient.RTCUserMedia")) {
            return;
        }
    }
}