package com.github.xgfjyw.webrtcclient;

import android.util.Log;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;
import org.webrtc.SessionDescription;

/**
 * Created by hongxiliang2 on 1/7/2016.
 */
public class RTCSessionDescription extends ScriptableObject {
    private SessionDescription.Type type;
    private String description;
    private SessionDescription newSdp;

    private static final String TAG = "rtcSessionDescription";

    @Override
    public String getClassName() {
        return "RTCSessionDescription";
    }

    public RTCSessionDescription() { }

    @JSConstructor
    public RTCSessionDescription(NativeObject sdp) {
        Object t = sdp.get("type");
        String s = (String)sdp.get("sdp");

        SessionDescription.Type sdp_type;
        if (t.getClass().getName().equals("java.lang.String")) {
            sdp_type = SessionDescription.Type.fromCanonicalForm((String) t);
        } else {
            Log.e(TAG, "error type: " + t.getClass().getName());
            return;
        }

        newSdp = new SessionDescription(sdp_type, s);
        type = newSdp.type;
        description = newSdp.description;
    }

    @JSGetter
    public SessionDescription.Type getType() {
        return type;
    }
    @JSSetter
    public void setSdpMid(SessionDescription.Type t) {
        type = t;
    }

    @JSGetter
    public String getDescription() {
        return description;
    }
    @JSSetter
    public void setDescription(String s) {
        description = s;
    }

    public SessionDescription sdp() {
        return newSdp;
    }
}
