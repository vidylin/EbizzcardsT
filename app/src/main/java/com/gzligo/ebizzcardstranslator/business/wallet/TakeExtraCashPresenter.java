package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;

/**
 * Created by Lwd on 2017/7/14.
 */

public class TakeExtraCashPresenter extends BasePresenter<TakeExtraCashRepository> {
    private static final int TAKE_EXTRA_CASH = 0x78;
    private static final int TAKE_EXTRA_CASH_PWD_ERROR = 0x77;
    private static final int TAKE_EXTRA_CASH_TIMES_MORE = 0x76;

    public TakeExtraCashPresenter(TakeExtraCashRepository model) {
        super(model);
    }

    public void requestAddWithdrawOrder(final Message message) {
        getModel().requestAddWithdrawOrder((String) message.objs[0], (String) message.objs[1], (String) message.objs[2],
                 true, new BaseObserver<HttpResultBean>() {

                    @Override
                    public void onNext(HttpResultBean httpResultBean) {
                        switch (httpResultBean.getError()) {
                            case 0:
                                message.what = TAKE_EXTRA_CASH;
                                message.dispatchToIView();
                                break;
                            default:
                                switch (httpResultBean.getMessage()) {
                                    case "wpasswd not match":
                                        message.what = TAKE_EXTRA_CASH_PWD_ERROR;
                                        message.dispatchToIView();
                                        break;
                                    case "withdraw exceed times":
                                        message.what = TAKE_EXTRA_CASH_TIMES_MORE;
                                        message.dispatchToIView();
                                        break;
                                }
                                break;
                        }
                    }

                });
    }
}
