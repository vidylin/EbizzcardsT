package com.github.xgfjyw.webrtcclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.xgfjyw.libnettools.NetSelect;
import com.github.xgfjyw.libnettools.NetworkObserver;
import com.github.xgfjyw.libnettools.NetworkStatusNotificationReceiver;
import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;

import org.webrtc.Logging;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hongxiliang2 on 12/7/2016.
 */
public class RTCWrapper {
    private String roomName;
    private String tag;
    private static final String client_script_url = "https://srv-voip.hisir.net/client.js";
    private static Context context;
    private static Boolean isReady = false;
    public static RTCVideoView videoView = null;

    public void connect(String user, String room, String token, RTCVideoViewCallback callback) {
        if (!isReady) {
            Log.e("error", "VoIP not ready.");
            return;
        }
        if (videoView != null)
            videoView = null;
        if (callback != null)
            videoView = new RTCVideoView(callback);


        roomName = trimString(room);
        tag = "rtc_" + room;

        String step1 = "var rtc_" + roomName + " = connect('" + roomName + "', '" + token + "');";
        String step2 = "rtc_" + roomName + ".name = 'rtc_" + roomName + "';";
        String step3 = "rtc_" + roomName + ".user = '" + user + "';";
        String step4 = "global = this;";

        JSCore.runScript(step1, tag);
        JSCore.runScript(step2, tag);
        JSCore.runScript(step3, tag);
        JSCore.runScript(step4, tag);
    }

    public void disconnect() {
        String step1 = "rtc_" + roomName + ".disconnect()";
        JSCore.runScript(step1, tag);
    }

    public void mute(boolean yes) {
        String step1;
        if (yes)
            step1 = "rtc_" + roomName + ".removeStreams();";
        else
            step1 = "rtc_" + roomName + ".addStreams();";

        JSCore.runScript(step1, tag);
    }

    public static void init(Context globalContext) throws IllegalArgumentException {
        context = globalContext;
        NetworkStatusNotificationReceiver.init(context);
        try {
            JSCore.init(context);
        } catch (Exception ex) {
            throw ex;
        }


        if (checkNetwork())
            return;

        String str = loadDefault();
        if (str != null) {
            JSCore.runScript(str, "rtc.js");
            isReady = true;
        }

        NetworkStatusNotificationReceiver.regObserver(new NetworkObserver() {
            @Override
            public void onNetworkChanged() {
                NetworkStatusNotificationReceiver.unregObserver(this);
                checkNetwork();
            }
        });

//        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO);
    }

    public static void deinit() {
        JSCore.deinit();
    }

    public void switchCamera() {
        if (videoView != null) {
            videoView.switchCamera();
        }
    }



    private void getJavaScriptObject(String name) {
        String step1 = "console.getJavascriptObject(rtc_" + roomName + ");";
        JSCore.runScript(step1, tag);
    }



    private String trimString(String str) {
        return str.replaceAll("-", "");
    }

    private static Boolean checkNetwork() {
        if (NetworkStatusNotificationReceiver.isReachable()) {
            Log.i("Reachability", "Reach to network");
            internalInit();
            return true;

        } else
            return false;
    }

    private static void saveDefault(String str) {
        SharedPreferences preferences = context.getSharedPreferences("rtc.js", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("value", str);
        editor.commit();
    }

    private static String loadDefault() {
        SharedPreferences preferences = context.getSharedPreferences("rtc.js", context.MODE_PRIVATE);
        return preferences.getString("value", null);
    }

    private static void internalInit() {
        new Thread(new Runnable() {
            private static final String TAG = "JavaScriptDownloader";
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL requestUrl = new URL(client_script_url);
                    conn = (HttpURLConnection)requestUrl.openConnection();
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() != 200) {
                        Log.e(TAG, "http request error");
                        return;
                    }


                    InputStream in = conn.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    int len;
                    byte[] buf = new byte[10240];
                    while ((len = in.read(buf)) != -1) {
                        buffer.append(new String(buf, 0, len));
                    }

                    String text = buffer.toString();
                    JSCore.runScript(text, "rtc.js");

                    saveDefault(text);
                    isReady = true;
                } catch (IOException ex) {
                    Log.e(TAG, ex.toString());

                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();


        // test turn server
        new Thread(new Runnable() {
            @Override
            public void run() {
                IResolver[] resolvers = new IResolver[1];
                resolvers[0] = new DnspodFree();
                DnsManager dns = new DnsManager(NetworkInfo.normal, resolvers);
                try {
                    String[] array = dns.query("srv-voip-turn.hisir.net");
                    NetSelect.start(array, new NetSelect.CallBack() {
                        @Override
                        public void done(String json) {
                            String script = "var ice_server_score = JSON.stringify(" + json + ");";
                            Log.i("ice select", script);
                            JSCore.runScript(script, "ice select");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}