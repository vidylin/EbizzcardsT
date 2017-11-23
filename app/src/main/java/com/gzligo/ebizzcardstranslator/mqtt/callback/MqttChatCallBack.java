package com.gzligo.ebizzcardstranslator.mqtt.callback;

import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslationChatResultBean;

/**
 * Created by Lwd on 2017/6/8.
 */

public interface MqttChatCallBack {
    void handlerChatMsg(ChatMessageBean chatMsg);
    void handlerChatResultMsg(TranslationChatResultBean translationChatResultBean);
    void endTranslateMsg(ChatMessageBean selectedBean);
}
