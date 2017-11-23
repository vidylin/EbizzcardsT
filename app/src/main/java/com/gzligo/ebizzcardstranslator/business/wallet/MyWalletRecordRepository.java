package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;

import io.reactivex.Observer;

/**
 * Created by Lwd on 2017/7/12.
 */

public class MyWalletRecordRepository implements IModel {

    public void requestGetWithdrawOrder(String until, String since,
                                        String count, boolean isCache, Observer observer) {
        HttpUtils.requestGetWithdrawOrder(until, since, count, isCache, observer);
    }
}
