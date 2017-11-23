package com.gzligo.ebizzcardstranslator.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Lwd on 2017/9/13.
 */

public class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();
    public static boolean isNetworkConnected(Context context) {
        try {
            NetworkInfo activeNetwork = null;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(cm == null){
                Log.e("ConnectivityStatus", "isQuickNet,ConnectivityManager==null");
                return false;
            }

            activeNetwork = cm.getActiveNetworkInfo(); /*获得当前使用网络的信息*/

            if (activeNetwork == null || !activeNetwork.isConnected()){//当前无可用连接,或者没有连接,尝试取所有网络再进行判断一次
                NetworkInfo[] allNetworks = cm.getAllNetworkInfo();//取得所有网络
                if(allNetworks != null){//网络s不为null
                    for(int i = 0; i < allNetworks.length; i ++){//遍历每个网络
                        if(allNetworks[i] != null){
                            if(allNetworks[i].isConnected()){//此网络是连接的，可用的
                                activeNetwork = allNetworks[i];
                                break;
                            }
                        }
                    }
                }
            }

            return activeNetwork != null;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return true;
        }
    }
}
