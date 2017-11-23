package com.github.xgfjyw.webrtcclient;

import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.applog.Timber;
import com.github.xgfjyw.libnettools.NetSelect;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoTrack;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hongxiliang2 on 1/7/2016.
 */
public class RTCPeerConnection extends ScriptableObject {
    @Override
    public String getClassName() {
        return "PeerConnection";
    }
    public RTCPeerConnection() {}

    private static final String TAG = "rtcPC";
    private static PeerConnectionFactory mFactory = null;
    private PeerConnection that;
    private MediaConstraints pcConstraints = new MediaConstraints();
    private Boolean connected = false;

    private Function onopen;
    private Function onaddstream;
    private Function ondatachannel;
    private Function onicecandidate;
    private Function oniceconnectionstatechange;
    private Function onremovestream;
    private Function onsignalingstatechange;


    @JSConstructor
    public RTCPeerConnection(NativeObject iceServers) {
        JSCore.print_debug_info("RTCPeerConnection");

        Log.i(TAG, "RTCPeerConnection " + iceServers.toString());
        PeerConnectionFactory factory = factory();

        //necessary options
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));

        PeerConnection.RTCConfiguration cfg = new PeerConnection.RTCConfiguration(createIceServers(iceServers));
        cfg.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        cfg.iceTransportsType = PeerConnection.IceTransportsType.ALL;
        if (iceServers.has("iceTransportPolicy", iceServers)) {
            String policy = (String)iceServers.get("iceTransportPolicy");
            switch (policy) {
                case "none":
                    cfg.iceTransportsType = PeerConnection.IceTransportsType.NONE;
                    break;
                case "relay":
                    cfg.iceTransportsType = PeerConnection.IceTransportsType.RELAY;
                    break;
                case "noHost":
                    cfg.iceTransportsType = PeerConnection.IceTransportsType.NOHOST;
                    break;
                default:
                    cfg.iceTransportsType = PeerConnection.IceTransportsType.ALL;
            }
        }


        /* create a new peerconnection */
        PeerConnection peerConnection = factory.createPeerConnection(cfg, pcConstraints, new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(org.webrtc.PeerConnection.SignalingState signalingState) {
                Timber.i("signalingStateChanged -> " + signalingState.toString());
                if (onsignalingstatechange instanceof Function) {
                    final String state = signalingState.toString();
                    JSCore.runOnSingleThread(new Runnable() {
                        @Override
                        public void run() {
                            JSCore.print_debug_info("onSignalingChange");
                            Context ctx = Context.enter();
                            ctx.setOptimizationLevel(-1);
                            Scriptable scope = JSCore.scope();

                            Object[] args = new Object[]{ state };
                            try {
                                Timber.i("onSignalingChange -> " + onsignalingstatechange);
                                onsignalingstatechange.call(ctx, scope, ctx.newObject(scope), args);

                            } catch (EcmaError ex) {
                                Log.e(TAG, ex.toString());
                            } catch (UnsupportedOperationException ex) {
                                Log.e(TAG, ex.toString());

                            }catch (Exception e) {
                                Timber.e(e);
                            }finally {
                                Context.exit();
                            }
                        }
                    });
                }
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Timber.w("onIceConnectionChange:" + iceConnectionState.toString());
                if (oniceconnectionstatechange instanceof Function) {
                    final String state = iceConnectionState.toString();
                    JSCore.runOnSingleThread(new Runnable() {
                        @Override
                        public void run() {
                            JSCore.print_debug_info("onIceConnectionChange");
                            Context ctx = Context.enter();
                            ctx.setOptimizationLevel(-1);
                            Scriptable scope = JSCore.scope();

                            Object[] args = new Object[]{ state };
                            try {
                                oniceconnectionstatechange.call(ctx, scope, ctx.newObject(scope), args);

                            } catch (EcmaError ex) {
                                Timber.e(TAG + " " + ex.toString());
                            } catch (UnsupportedOperationException ex) {
                                Timber.e(TAG + " " + ex.toString());

                            }catch (Exception e) {
                                Timber.e(e);
                            }finally {
                                Context.exit();
                            }
                        }
                    });
                }

                if (!connected && iceConnectionState == PeerConnection.IceConnectionState.CONNECTED) {
                    connected = true;
                    Intent intent = new Intent();
                    intent.setAction("hisir.voip");
                    intent.putExtra("voip_state", iceConnectionState.ordinal());
                    JSCore.androidContext().sendBroadcast(intent);
                }else if (connected && iceConnectionState == PeerConnection.IceConnectionState.FAILED) {
                    connected = false;
                    Intent intent = new Intent();
                    intent.setAction("hisir.voip");
                    intent.putExtra("voip_state", iceConnectionState.ordinal());
                    JSCore.androidContext().sendBroadcast(intent);
                }
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Timber.i("onIceConnectionReceivingChange: " + b);
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Timber.i( "onIceGatheringChange:" + iceGatheringState.toString());
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                if (onicecandidate instanceof Function) {
                    NativeObject candidate = new NativeObject();
                    candidate.put("candidate", candidate, iceCandidate.sdp);
                    candidate.put("sdpMLineIndex", candidate, iceCandidate.sdpMLineIndex);
                    candidate.put("sdpMid", candidate, iceCandidate.sdpMid);

                    final NativeObject dict = new NativeObject();
                    dict.put("candidate", dict, candidate);

                    JSCore.runOnSingleThread(new Runnable() {
                        @Override
                        public void run() {
                            Context ctx = Context.enter();
                            ctx.setOptimizationLevel(-1);
                            Scriptable scope = JSCore.scope();

                            Object[] args = new Object[]{ dict };
                            try {
                                onicecandidate.call(ctx, scope, ctx.newObject(scope), args);

                            } catch (EcmaError ex) {
                                Log.e(TAG, ex.toString());
                            } catch (UnsupportedOperationException ex) {
                                Log.e(TAG, ex.toString());

                            } finally {
                                Context.exit();
                            }
                        }
                    });
                }
            }

            @Override
            public void onAddStream(final MediaStream mediaStream) {
                Timber.i("get remote stream: " + mediaStream.toString());

                if (mediaStream.videoTracks.size() == 1 && RTCWrapper.videoView != null) {
                    VideoTrack track = mediaStream.videoTracks.getFirst();
                    RTCWrapper.videoView.setRemoteVideoTrack(track);
                }

                if (onaddstream instanceof Function) {
                    final NativeObject dict = new NativeObject();
                    dict.put("stream", dict, new RTCUserMedia(mediaStream));

                    JSCore.runOnSingleThread(new Runnable() {
                        @Override
                        public void run() {
                            Context ctx = Context.enter();
                            ctx.setOptimizationLevel(-1);
                            Scriptable scope = JSCore.scope();

                            Object[] args = new Object[]{ dict };
                            try {
                                onaddstream.call(ctx, scope, ctx.newObject(scope), args);

                            } catch (EcmaError ex) {
                                Log.e(TAG, ex.toString());
                            } catch (UnsupportedOperationException ex) {
                                Log.e(TAG, ex.toString());

                            } finally {
                                Context.exit();
                            }
                        }
                    });
                }
            }

            @Override
            public void onAddTrack(RtpReceiver rr, MediaStream[] tracks) {
                Log.i(TAG, "on add track:" + tracks.toString());
            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Timber.i("onRemoveStream:" + mediaStream.toString());
                if (mediaStream != null) {
//                    int trackIndex;
//                    int streamIndex;
//                    for (int i = 0; i < mediaStream.videoTracks.size(); i++) {
//                        VideoTrack track = mediaStream.videoTracks.get(i);
//                        trackIndex = mMediaStreamTracks.indexOfValue(track);
//                        while (trackIndex >= 0) {
//                            mMediaStreamTracks.removeAt(trackIndex);
//                            trackIndex = mMediaStreamTracks.indexOfValue(track);
//                        }
//                    }
//                    for (int i = 0; i < mediaStream.audioTracks.size(); i++) {
//                        AudioTrack track = mediaStream.audioTracks.get(i);
//                        trackIndex = mMediaStreamTracks.indexOfValue(track);
//                        while (trackIndex >= 0) {
//                            mMediaStreamTracks.removeAt(trackIndex);
//                            trackIndex = mMediaStreamTracks.indexOfValue(track);
//                        }
//                    }
//                    streamIndex = mMediaStreams.indexOfValue(mediaStream);
//                    if (streamIndex >= 0) {
//                        mMediaStreams.removeAt(streamIndex);
//                    }
                }
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.i(TAG, "onDataChannel:" + dataChannel.toString());
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.i(TAG, "onRenegotiationNeeded");
            }


            @Override
            public void onIceCandidatesRemoved(IceCandidate[] var1){}
        });

        that = peerConnection;
    }

    @JSFunction
    public void addStream(RTCUserMedia stream) {
        JSCore.print_debug_info("addStream");

        if (that.addStream(stream.mediaStream())) {
            Timber.i("stream added");
        } else {
            Timber.e("failed to add stream");
        }
    }

    @JSFunction
    public void removeStream(RTCUserMedia stream) {
        JSCore.print_debug_info("onRemoveStream");

        that.removeStream(stream.mediaStream());
    }

    @JSFunction
    public void createOffer(final Function success, final Function failure, NativeObject options) {
        JSCore.print_debug_info("createOffer");
        PeerConnection peerConnection = that;

        if (options != null) {
            if (options.has("IceRestart", options)) {
                pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("IceRestart", "true"));
            }
        }


        Timber.d("RTCPeerConnectionCreateOfferWithObjectID start");
        if (peerConnection != null) {
            peerConnection.createOffer(new SdpObserver() {
                @Override
                public void onCreateSuccess(final SessionDescription sdp) {
                    if (success instanceof Function) {
                        final NativeObject params = new NativeObject();
                        params.put("type", params, sdp.type.canonicalForm());
                        params.put("sdp", params, sdp.description);

                        JSCore.runOnSingleThread(new Runnable() {
                            @Override
                            public void run() {
                                JSCore.print_debug_info("onCreateSuccess");

                                Context ctx = Context.enter();
                                ctx.setOptimizationLevel(-1);
                                Scriptable scope = JSCore.scope();

                                Object[] args = new Object[]{ params };
                                try {
                                    success.call(ctx, scope, ctx.newObject(scope), args);

                                } catch (EcmaError ex) {
                                    Timber.e(ex.toString());
                                } catch (UnsupportedOperationException ex) {
                                    Timber.e(ex.toString());

                                } finally {
                                    Context.exit();
                                }
                            }
                        });
                    }
                }
                @Override
                public void onSetSuccess() {}

                @Override
                public void onCreateFailure(String s) {
                    if (failure instanceof Function) {
                        final String reason = s;
                        JSCore.runOnSingleThread(new Runnable() {
                            @Override
                            public void run() {
                                Context ctx = Context.enter();
                                ctx.setOptimizationLevel(-1);
                                Scriptable scope = JSCore.scope();

                                Object[] args = new Object[]{ reason };
                                try {
                                    failure.call(ctx, scope, ctx.newObject(scope), args);

                                } catch (EcmaError ex) {
                                    Timber.e(ex.toString());
                                } catch (UnsupportedOperationException ex) {
                                    Timber.e(ex.toString());

                                } finally {
                                    Context.exit();
                                }
                            }
                        });
                    }
                }
                @Override
                public void onSetFailure(String s) {}
            }, pcConstraints);

        } else {
            Timber.d("peerConnectionCreateOffer() peerConnection is null");
            if (failure instanceof Function) {
                Context ctx = Context.enter();
                ctx.setOptimizationLevel(-1);
                Scriptable scope = JSCore.scope();

                Object[] args = new Object[]{ "peerConnection is null" };
                try {
                    failure.call(ctx, scope, ctx.newObject(scope), args);

                } catch (EcmaError ex) {
                    Timber.e(ex.toString());
                } catch (UnsupportedOperationException ex) {
                    Timber.e(ex.toString());

                } finally {
                    Context.exit();
                }
            }
        }
        Timber.d("RTCPeerConnectionCreateOfferWithObjectID end");
    }

    @JSFunction
    public void createAnswer(final Function success, final Function failure) {
        JSCore.print_debug_info("createAnswer");
        PeerConnection peerConnection = that;


        Timber.d("RTCPeerConnectionCreateAnswerWithObjectID start");
        if (peerConnection != null) {
            peerConnection.createAnswer(new SdpObserver() {
                @Override
                public void onCreateSuccess(final SessionDescription sdp) {
                    Log.i(TAG, "create answer ok");
                    final NativeObject params = new NativeObject();
                    params.put("type", params, sdp.type.canonicalForm());
                    params.put("sdp", params, sdp.description);

                    if (success instanceof Function) {
                        JSCore.runOnSingleThread(new Runnable() {
                            @Override
                            public void run() {
                                Context ctx = Context.enter();
                                ctx.setOptimizationLevel(-1);
                                Scriptable scope = JSCore.scope();

                                Object[] args = new Object[]{ params };
                                try {
                                    success.call(ctx, scope, ctx.newObject(scope), args);

                                } catch (EcmaError ex) {
                                    Log.e(TAG, ex.toString());
                                } catch (UnsupportedOperationException ex) {
                                    Log.e(TAG, ex.toString());

                                } finally {
                                    Context.exit();
                                }
                            }
                        });
                    }
                }
                @Override
                public void onSetSuccess() {}

                @Override
                public void onCreateFailure(String s) {
                    Timber.e("create answer failed: " + s);
                    if (failure instanceof Function) {
                        final String reason = s;
                        JSCore.runOnSingleThread(new Runnable() {
                            @Override
                            public void run() {
                                Context ctx = Context.enter();
                                ctx.setOptimizationLevel(-1);
                                Scriptable scope = JSCore.scope();

                                Object[] args = new Object[]{reason};
                                try {
                                    failure.call(ctx, scope, ctx.newObject(scope), args);

                                } catch (EcmaError ex) {
                                    Timber.e(ex.toString());
                                } catch (UnsupportedOperationException ex) {
                                    Timber.e(ex.toString());

                                } finally {
                                    Context.exit();
                                }
                            }
                        });
                    }
                }
                @Override
                public void onSetFailure(String s) {}
            }, pcConstraints);
        } else {
            Log.d(TAG, "peerConnectionCreateAnswer() peerConnection is null");
            if (failure instanceof Function) {
                Context ctx = Context.enter();
                ctx.setOptimizationLevel(-1);
                Scriptable scope = JSCore.scope();

                Object[] args = new Object[]{ "peerConnection is null" };
                try {
                    failure.call(ctx, scope, ctx.newObject(scope), args);

                } catch (EcmaError ex) {
                    Timber.e(ex.toString());
                } catch (UnsupportedOperationException ex) {
                    Timber.e(ex.toString());

                } finally {
                    Context.exit();
                }
            }
        }
        Log.d(TAG, "RTCPeerConnectionCreateAnswerWithObjectID end");
    }

    @JSFunction
    public void setLocalDescription(NativeObject sdpMap, final Function success, final Function failure) {
        JSCore.print_debug_info("setLocalDescription");
        PeerConnection peerConnection = that;

        if (peerConnection != null) {
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm((String)sdpMap.get("type")),
                    (String)sdpMap.get("sdp"));

            peerConnection.setLocalDescription(new SdpObserver() {
                @Override
                public void onCreateSuccess(final SessionDescription sdp) {}

                @Override
                public void onSetSuccess() {
                    Log.i(TAG, "set local description ok");
                    if (success instanceof Function) {
                        JSCore.runOnSingleThread(new Runnable() {
                            @Override
                            public void run() {
                                JSCore.print_debug_info("setLocalDescription - onSetSuccess");
                                Context ctx = Context.enter();
                                ctx.setOptimizationLevel(-1);
                                Scriptable scope = JSCore.scope();

                                Object[] args = new Object[]{ };
                                try {
                                    success.call(ctx, scope, ctx.newObject(scope), args);

                                } catch (EcmaError ex) {
                                    Log.e(TAG, ex.toString());
                                } catch (UnsupportedOperationException ex) {
                                    Log.e(TAG, ex.toString());

                                } finally {
                                    Context.exit();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCreateFailure(String s) {}

                @Override
                public void onSetFailure(String s) {
                    Log.i(TAG, "set local description failed: " + s);
                    if (failure instanceof Function) {
                        JSCore.runOnSingleThread(new Runnable() {
                            @Override
                            public void run() {
                                Context ctx = Context.enter();
                                ctx.setOptimizationLevel(-1);
                                Scriptable scope = JSCore.scope();

                                Object[] args = new Object[]{};
                                try {
                                    failure.call(ctx, scope, ctx.newObject(scope), args);

                                } catch (EcmaError ex) {
                                    Log.e(TAG, ex.toString());
                                } catch (UnsupportedOperationException ex) {
                                    Log.e(TAG, ex.toString());

                                } finally {
                                    Context.exit();
                                }
                            }
                        });
                    }
                }
            }, sdp);
        } else {
            Log.d(TAG, "peerConnectionSetLocalDescription() peerConnection is null");
            if (failure instanceof Function) {
                Context ctx = Context.enter();
                ctx.setOptimizationLevel(-1);
                Scriptable scope = JSCore.scope();

                Object[] args = new Object[]{ "peerConnection is null" };
                try {
                    failure.call(ctx, scope, ctx.newObject(scope), args);

                } catch (EcmaError ex) {
                    Timber.e(ex.toString());
                } catch (UnsupportedOperationException ex) {
                    Timber.e(ex.toString());

                } finally {
                    Context.exit();
                }
            }
        }
    }

    @JSFunction
    public void setRemoteDescription(Object sdpMap, final Function success, final Function failure) {
        JSCore.print_debug_info("setRemoteDescription");
        PeerConnection peerConnection = that;

        if (peerConnection != null) {
            if (!(sdpMap instanceof RTCSessionDescription)) {
                Timber.e("setRemoteDescription: sdpMap is not an instanceof _RTCSessionDescription");
                return;
            }
            SessionDescription sdp = ((RTCSessionDescription)sdpMap).sdp();

            peerConnection.setRemoteDescription(new SdpObserver() {
                @Override
                public void onCreateSuccess(final SessionDescription sdp) {
                }

                @Override
                public void onSetSuccess() {
                    Timber.i("set remote description ok");
                    if (success instanceof Function) {
                        JSCore.runOnSingleThread(new Runnable() {
                            @Override
                            public void run() {
                                JSCore.print_debug_info("setRemoteDescription - onSetSuccess");
                                Context ctx = Context.enter();
                                ctx.setOptimizationLevel(-1);
                                Scriptable scope = JSCore.scope();

                                Object[] args = new Object[]{};
                                try {
                                    success.call(ctx, scope, ctx.newObject(scope), args);

                                } catch (EcmaError ex) {
                                    Log.e(TAG, ex.toString());
                                } catch (UnsupportedOperationException ex) {
                                    Log.e(TAG, ex.toString());

                                } finally {
                                    Context.exit();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCreateFailure(String s) {
                }

                @Override
                public void onSetFailure(String s) {
                    Timber.i("set remote description failed: " + s);
                    if (failure instanceof Function) {
                        final String reason = s;
                        JSCore.runOnSingleThread(new Runnable() {
                            @Override
                            public void run() {
                                Context ctx = Context.enter();
                                ctx.setOptimizationLevel(-1);
                                Scriptable scope = JSCore.scope();

                                Object[] args = new Object[]{reason};
                                try {
                                    failure.call(ctx, scope, ctx.newObject(scope), args);

                                } catch (EcmaError ex) {
                                    Log.e(TAG, ex.toString());
                                } catch (UnsupportedOperationException ex) {
                                    Log.e(TAG, ex.toString());

                                } finally {
                                    Context.exit();
                                }
                            }
                        });
                    }
                }
            }, sdp);
        } else {
            Timber.d("peerConnectionSetRemoteDescription() peerConnection is null");
            if (failure instanceof Function) {
                Context ctx = Context.enter();
                ctx.setOptimizationLevel(-1);
                Scriptable scope = JSCore.scope();

                Object[] args = new Object[]{ "peerConnection is null" };
                try {
                    failure.call(ctx, scope, ctx.newObject(scope), args);

                } catch (EcmaError ex) {
                    Log.e(TAG, ex.toString());
                } catch (UnsupportedOperationException ex) {
                    Log.e(TAG, ex.toString());

                } finally {
                    Context.exit();
                }
            }
        }
    }

    @JSFunction
    public void addIceCandidate(Object candidateMap, final Function success, final Function failure) {
        JSCore.print_debug_info("addIceCandidate");

        boolean result = false;
        PeerConnection peerConnection = that;

        if (peerConnection != null) {
            if (!(candidateMap instanceof RTCIceCandidate)) {
                Timber.e("addIceCandidate: candidateMap is not an instanceof RTCIceCandidate");
                return;
            }

            IceCandidate candidate = ((RTCIceCandidate)candidateMap).candidate();
            result = peerConnection.addIceCandidate(candidate);
        } else {
            Timber.i("peerConnectionAddICECandidate() peerConnection is null");
        }


        Context ctx = Context.enter();
        ctx.setOptimizationLevel(-1);
        Scriptable scope = JSCore.scope();

        try {
            if (result) {
                if (success instanceof Function) {
                   Object[] args = new Object[]{};
                    success.call(ctx, scope, ctx.newObject(scope), args);
                }
            } else {
                if (failure instanceof Function) {
                    Object[] args = new Object[]{};
                    failure.call(ctx, scope, ctx.newObject(scope), args);
                }
            }
        } catch (EcmaError ex) {
            Log.e(TAG, ex.toString());
        } catch (UnsupportedOperationException ex) {
            Log.e(TAG, ex.toString());

        } finally {
            Context.exit();
        }
    }

    @JSFunction
    public void close() {
        JSCore.print_debug_info("close");
        Log.w(TAG, "RTCPeerConnection closing");


        /* free javascript objects */
        onopen = null;
        onaddstream = null;
        ondatachannel = null;
        onicecandidate = null;
        oniceconnectionstatechange = null;
        onremovestream = null;
        onsignalingstatechange = null;

        PeerConnection peerConnection = that;
        if (peerConnection != null) {
            peerConnection.dispose();
            that = null;
        } else {
            Timber.d("peerConnectionClose() peerConnection is null");
        }

        if (RTCWrapper.videoView != null)
            RTCWrapper.videoView.unsetRemoteVideoTrack();

        resetAudio();
    }

    @JSSetter
    public void setOnopen(Function func) {
        onopen = func;
    }
    @JSGetter
    public Function getOnopen() {
        return onopen;
    }

    @JSSetter
    public void setOnaddstream(Function func) {
        onaddstream = func;
    }
    @JSGetter
    public Function getOnaddstream() {
        return onaddstream;
    }

    @JSSetter
    public void setOndatachannel(Function func) {
        ondatachannel = func;
    }
    @JSGetter
    public Function getOndatachannel() {
        return ondatachannel;
    }

    @JSSetter
    public void setOnicecandidate(Function func) {
        onicecandidate = func;
    }
    @JSGetter
    public Function getOnicecandidate() {
        return onicecandidate;
    }

    @JSSetter
    public void setOniceconnectionstatechange(Function func) {
        oniceconnectionstatechange = func;
    }
    @JSGetter
    public Function getOniceconnectionstatechange() {
        return oniceconnectionstatechange;
    }

    @JSSetter
    public void setOnremovestream(Function func) {
        onremovestream = func;
    }
    @JSGetter
    public Function getOnremovestream() {
        return onremovestream;
    }

    @JSSetter
    public void setOnsignalingstatechange(Function func) {
        onsignalingstatechange = func;
    }
    @JSGetter
    public Function getOnsignalingstatechange() {
        return onsignalingstatechange;
    }


    private List<PeerConnection.IceServer> createIceServers(NativeObject servers) {
        LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();

        for (Iterator<Object> i = servers.keySet().iterator(); i.hasNext(); ) {
            String k = (String) i.next();
            if (!k.equals("iceServers")) {
                continue;
            }

            NativeArray iceServerArr = (NativeArray) servers.get(k);
            for (int j = 0; j < iceServerArr.size(); j++) {
                NativeObject iceServerMap = (NativeObject) iceServerArr.get(j);

                if (iceServerMap.has("rest", iceServerMap)) {
                    String user = NetSelect.randomString(24);

                    String restfulAddr = iceServerMap.get("rest").toString();
//                    String urlString = restfulAddr + "?username=" + user;
//                    String result = syncHttpGet(urlString);
                    String result = syncTcpRequest(restfulAddr, user);
                    Timber.i("tcp request to " + restfulAddr + " -> " + result);

                    NativeObject obj = jsonParser(result);

                    String uri, username, password;
                    uri = username = password = null;

                    if (obj.has("username", obj))
                        username = (String) obj.get("username", obj);
                    if (obj.has("password", obj))
                        password = (String) obj.get("password", obj);
                    if (obj.has("uris", obj)) {
                        NativeArray uris = (NativeArray) obj.get("uris", obj);
                        if (uris.getLength() > 0) {
                            uri = (String) uris.get(0, uris);
                        }
                    }

                    if (uri != null && username != null && password != null)
                        iceServers.add(new PeerConnection.IceServer(uri, username, password));

                    return iceServers;
                }
            }
        }

        for (Iterator<Object> i = servers.keySet().iterator(); i.hasNext(); ) {
            String k = (String)i.next();
            if (!k.equals("iceServers")) {
                continue;
            }

            NativeArray iceServerArr = (NativeArray) servers.get(k);
            for (int j = 0; j < iceServerArr.size(); j++) {
                NativeObject iceServerMap = (NativeObject)iceServerArr.get(j);

                boolean hasUsernameAndCredential = iceServerMap.has("username", iceServerMap) && iceServerMap.has("credential", iceServerMap);
                if (iceServerMap.has("url", iceServerMap)) {
                    if (hasUsernameAndCredential) {
                        iceServers.add(new PeerConnection.IceServer(
                                (String)iceServerMap.get("url"),
                                (String)iceServerMap.get("username"),
                                (String)iceServerMap.get("credential")));
                    } else {
                        iceServers.add(new PeerConnection.IceServer((String)iceServerMap.get("url")));
                    }

                } else if (iceServerMap.has("urls", iceServerMap)) {
                    Object urlsObj = iceServerMap.get("urls");
                    switch (urlsObj.getClass().getName()) {
                        case "java.lang.String":
                            if (hasUsernameAndCredential) {
                                iceServers.add(new PeerConnection.IceServer(
                                        (String)iceServerMap.get("urls"),
                                        (String)iceServerMap.get("username"),
                                        (String)iceServerMap.get("credential")));
                            } else {
                                iceServers.add(new PeerConnection.IceServer((String)iceServerMap.get("urls")));
                            }
                            break;
                        case "Array":
//                            ReadableArray urls = iceServerMap.getArray("urls");
//                            for (int j = 0; j < urls.size(); j++) {
//                                String url = urls.getString(j);
//                                if (hasUsernameAndCredential) {
//                                    iceServers.add(new PeerConnection.IceServer(url,iceServerMap.getString("username"), iceServerMap.getString("credential")));
//                                } else {
//                                    iceServers.add(new PeerConnection.IceServer(url));
//                                }
//                            }
                            break;
                    }
                }
            }
        }


        Timber.d("ice server: " + iceServers.toString());
        return iceServers;
    }

    private void resetAudio() {
        AudioManager audioManager = (AudioManager) JSCore.androidContext().getSystemService(android.content.Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        audioManager.setMode(AudioManager.MODE_NORMAL);
    }

    public static PeerConnectionFactory factory() {
        if (mFactory == null) {
            mFactory = new PeerConnectionFactory(null);
        }
        return mFactory;
    }

    //public String syncHttpGet(String url) {
    private String syncTcpRequest(String addr, String username) {
        String result = null;
        Socket socket = null;
        try {
            URL url = new URL(addr);
            String host = url.getHost();
            int port = url.getPort();

            socket = new Socket(host, port);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(username);
            writer.flush();

            DataInputStream input = new DataInputStream(socket.getInputStream());
            byte[] buf = new byte[1000];
            socket.setSoTimeout(3000);
            int length = input.read(buf);
            result = new String(buf, 0, length);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
//        HttpURLConnection conn = null;
//        InputStreamReader in = null;
//        try {
//            conn = (HttpURLConnection)new URL(url).openConnection();
//            in = new InputStreamReader(conn.getInputStream());
//            BufferedReader bufferedReader = new BufferedReader(in);
//            StringBuffer strBuffer = new StringBuffer();
//
//            String line = null;
//            while ((line = bufferedReader.readLine()) != null) {
//                strBuffer.append(line);
//            }
//            result = strBuffer.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        return result;
    }

    public NativeObject jsonParser(String jsonString) {
        Object ret = null;
        String jsStr = "JSON.parse('" + jsonString + "');";

        Context ctx = Context.enter();
        ctx.setOptimizationLevel(-1);

        try { ret = ctx.evaluateString(JSCore.scope(), jsStr, "jsonParser", 1, null); }
        catch (Exception e) { e.printStackTrace(); }
        finally { ctx.exit(); }


        if (ret != null && !ret.getClass().getName().equals("org.mozilla.javascript.NativeObject"))
            return null;
        return (NativeObject)ret;
    }
}
