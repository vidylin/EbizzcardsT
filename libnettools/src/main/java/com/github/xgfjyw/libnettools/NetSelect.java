package com.github.xgfjyw.libnettools;

import android.util.Log;

import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hongxiliang2 on 14/9/2016.
 */
public class NetSelect {
    static {
        System.loadLibrary("netselect");
    }

    public static void start(final String[] servers, final CallBack callback) {
        final Map<String, Integer> dict = new HashMap<>();

        final DispatchGroup group = new DispatchGroup();
        for (int i = 0; i < servers.length; i++) {
            final Integer idx = i;
            group.add(new Runnable() {
                @Override
                public void run() {
                    int score = test(servers[idx], 1443);
                    synchronized (group) {
                        dict.put(servers[idx], score);
                    }
                }
            });
        }

        group.finish(new Runnable() {
            @Override
            public void run() {
                Log.i("NetSelect", "network test finished");
                JSONObject json = new JSONObject(dict);
                callback.done(json.toString());
            }
        });
    }

    public interface CallBack {
        void done(String json);
    }

    private static native int test(String host, int port);
    public static native String randomString(int len);
}

