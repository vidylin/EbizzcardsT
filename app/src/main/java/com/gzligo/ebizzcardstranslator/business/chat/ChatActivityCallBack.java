package com.gzligo.ebizzcardstranslator.business.chat;

import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;

/**
 * Created by Lwd on 2017/6/22.
 */

public interface ChatActivityCallBack {
    void getUnTranslateMsg(ChatMessageBean chatMsg);

    void recordVoicePermission();
}
