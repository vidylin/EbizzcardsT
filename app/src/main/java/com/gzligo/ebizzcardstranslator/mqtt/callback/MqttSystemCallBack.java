package com.gzligo.ebizzcardstranslator.mqtt.callback;

/**
 * Created by Lwd on 2017/6/8.
 */

public interface MqttSystemCallBack<T> {
    void handlerSystemMsg(T systemMsg);
}
