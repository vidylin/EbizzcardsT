package com.github.xgfjyw.webrtcclient;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;
import org.webrtc.IceCandidate;

/**
 * Created by hongxiliang2 on 1/7/2016.
 */
public class RTCIceCandidate extends ScriptableObject {
    private String sdpMid;
    private int sdpMLineIndex;
    private String sdp;
    private IceCandidate candidate;

    @Override
    public String getClassName() {
        return "RTCIceCandidate";
    }

    public RTCIceCandidate() { }

    @JSConstructor
    public RTCIceCandidate(NativeObject data) {
        candidate = new IceCandidate(
                (String)data.get("sdpMid"),
                (Integer)data.get("sdpMLineIndex"),
                (String)data.get("candidate"));

        sdpMid = candidate.sdpMid;
        sdpMLineIndex = candidate.sdpMLineIndex;
        sdp = candidate.sdp;
    }

    @JSGetter
    public String getSdpMid() {
        return sdpMid;
    }
    @JSSetter
    void setSdpMid(String s) {
        sdpMid = s;
    }

    @JSGetter
    public int getSdpMLineIndex() {
        return sdpMLineIndex;
    }
    @JSSetter
    void setSdpMLineIndex(int i) {
        sdpMLineIndex = i;
    }

    @JSGetter
    public String getSdp() {
        return sdp;
    }
    @JSSetter
    void setSdp(String s) {
        sdp = s;
    }

    public IceCandidate candidate() {
        return candidate;
    }
}
