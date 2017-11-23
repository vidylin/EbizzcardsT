package com.gzligo.ebizzcardstranslator.base.mvp;

/**
 * Created by xfast on 2017/5/25.
 */

public interface IView {
    /**
     * 处理presenter的回调消息, 同handler原理
     */
    void handlePresenterCallback(Message message);
}
