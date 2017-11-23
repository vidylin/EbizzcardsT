package com.gzligo.ebizzcardstranslator.manager;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 * Created by xfast on 2017/9/18.
 */

public class MockVoipServer {
    private static String token;
    private static CountDownLatch countDownLatch;

    public static String token() {
        token = null;
        countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection conn = null;
                StringBuffer buffer;
                try {
                    String server = "srv-voip.hisir.net";
                    url = new URL("http://" + /*RTCWrapper.SERVER*/server + ":3034/token");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() != 200) {
                        Log.e("getToken", "http request error");
                    }

                    InputStream in = conn.getInputStream();
                    buffer = new StringBuffer();

                    int len;
                    byte[] buf = new byte[4096];
                    while ((len = in.read(buf)) != -1) {
                        buffer.append(new String(buf, 0, len));
                    }
                    token = buffer.toString();
                } catch (IOException ex) {
                    Log.e("getToken", ex.toString());
                    return;

                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                    countDownLatch.countDown();
                }
            }
        }).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Log.e("getToken", e.toString());
        }
        return token;
    }
}
