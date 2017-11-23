package com.gzligo.ebizzcardstranslator.business.chat;

import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;

/**
 * Created by Lwd on 2017/6/13.
 */

public interface ChatCallBack {
    void downLoadFileSuccess(ChatMessageBean chatMessageBean);

    void upLoadFileSuccess(ChatMessageBean chatMessageBean);
}
