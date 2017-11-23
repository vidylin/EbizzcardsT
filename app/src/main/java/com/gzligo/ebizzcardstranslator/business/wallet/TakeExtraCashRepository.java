package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;

import io.reactivex.Observer;

/**
 * Created by Lwd on 2017/7/14.
 */

public class TakeExtraCashRepository implements IModel {

    public void requestAddWithdrawOrder(String amount, String cardInfoId,
                                        String wpasswd, boolean isCache, Observer observer) {
        HttpUtils.requestAddWithdrawOrder(amount, cardInfoId, wpasswd, isCache, observer);
    }
}
