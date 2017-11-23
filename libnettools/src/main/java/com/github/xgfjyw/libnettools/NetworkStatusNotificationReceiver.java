package com.github.xgfjyw.libnettools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by hongxiliang2 on 28/9/2016.
 */

public class NetworkStatusNotificationReceiver extends BroadcastReceiver {
    private static String curNetwork = "unknown";
    private static final String networkUnavailable = "__none__";
    private final static HashSet<NetworkObserver> observers = new HashSet<NetworkObserver>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            detectNetworkInfo(context);
        }
    }

    public static Boolean isReachable() {
        if (curNetwork.equals(networkUnavailable))
            return false;

        return true;
    }

    /* get the identity of current network environment */
    private static void detectNetworkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();

        if (activeInfo != null) {
            switch (activeInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    setNetwork(activeInfo.getTypeName());
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    setNetwork(wifiInfo.getSSID());
                    break;
                default:
                    Log.i("unknown network type", activeInfo.getType() + "");
            }
        } else {
            setNetwork(networkUnavailable);
        }
    }

    /* evoke callbacks when network changed */
    private static void setNetwork(String name) {
        if (!name.equals(curNetwork)) {
            Log.w("network",  curNetwork + " -> " + name);
            curNetwork = name;

            if (!name.equals(networkUnavailable)) {
                for (NetworkObserver observer : observers) {
                    observer.onNetworkChanged();
                }
            }
        }
    }

    /* obtain network identity on the time app starts */
    public static void init(Context context) {
        detectNetworkInfo(context);
    }

    /* register callbacks */
    public static void regObserver(NetworkObserver observer) {
        observers.add(observer);
    }

    /* unregister callbacks */
    public static void unregObserver(NetworkObserver observer) {
        observers.remove(observer);
    }
}