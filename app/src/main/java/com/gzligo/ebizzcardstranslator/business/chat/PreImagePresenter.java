package com.gzligo.ebizzcardstranslator.business.chat;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;

/**
 * Created by Lwd on 2017/7/26.
 */

public class PreImagePresenter extends BasePresenter<ChatRepository> {
    private IView iView;

    public PreImagePresenter(ChatRepository model, IView iView) {
        super(model);
        this.iView = iView;
    }
}
