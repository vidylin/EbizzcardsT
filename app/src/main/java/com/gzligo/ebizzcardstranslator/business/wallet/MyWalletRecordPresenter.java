package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecord;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecordBean;

/**
 * Created by Lwd on 2017/7/12.
 */

public class MyWalletRecordPresenter extends BasePresenter<MyWalletRecordRepository> {
    private static final int GET_MY_WALLET_RECORD_SUCCESS = 0x90;

    public MyWalletRecordPresenter(MyWalletRecordRepository model) {
        super(model);
    }

    public void requestGetWithdrawOrder(final Message message, final int type) {
        getModel().requestGetWithdrawOrder((String) message.objs[0], (String) message.objs[1], (String) message.objs[2]
                , true, new BaseObserver<MyWalletRecordBean<MyWalletRecord>>() {

                    @Override
                    public void onNext(MyWalletRecordBean<MyWalletRecord> myWalletRecordBean) {
                        message.what = GET_MY_WALLET_RECORD_SUCCESS;
                        message.arg1 = type;
                        message.obj = myWalletRecordBean;
                        message.dispatchToIView();
                    }

                });
    }
}
