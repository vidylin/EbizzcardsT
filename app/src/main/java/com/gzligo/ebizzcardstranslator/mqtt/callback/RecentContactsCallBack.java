package com.gzligo.ebizzcardstranslator.mqtt.callback;

/**
 * Created by Lwd on 2017/6/9.
 */

public interface RecentContactsCallBack<T> {
    void handlerRecentContactsMsg(T recentContactsMsg);
}
