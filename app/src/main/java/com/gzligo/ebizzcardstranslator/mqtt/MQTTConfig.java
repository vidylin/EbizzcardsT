package com.gzligo.ebizzcardstranslator.mqtt;

/**
 * Created by Lwd on 2017/6/6.
 */

interface MQTTConfig {
    String PRE_CHAT = "chat/";
    String PRE_NOTIFY = "notify/";
    String PRE_SYSTEM = "system/";

    //不需要订阅
    String PRE_MSG_NOTICE = "msgsvr/notice";
    String PRE_NOTICE = "appsvr/notice";

    int CONNECT_TIMEOUT = 30;
    int ALIVE_INTERVAL = 60;
}
