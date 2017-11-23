package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;

import java.util.List;

/**
 * Created by Lwd on 2017/7/13.
 */

public class MyBankCardPresenter extends BasePresenter<MyBankCardRepository> {
    private static final int DELETE_BANK_CARD = 0x40;

    public MyBankCardPresenter(MyBankCardRepository model) {
        super(model);
    }

    public void requestGetWithdrawOrder(final Message message) {
        getModel().requestGetWithdrawOrder((String) message.objs[0], true, new BaseObserver() {

            @Override
            public void onNext(Object o) {
                message.what = DELETE_BANK_CARD;
                message.dispatchToIView();
            }

        });
    }

    public List<CardInfo> getListCardInfo() {
        return getModel().getListCardInfo();
    }
}
