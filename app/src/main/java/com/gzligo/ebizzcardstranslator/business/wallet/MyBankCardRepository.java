package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;

import java.util.List;

import io.reactivex.Observer;

/**
 * Created by Lwd on 2017/7/13.
 */

public class MyBankCardRepository implements IModel {

    public void requestGetWithdrawOrder(String cardInfoId, boolean isCache, Observer observer) {
        HttpUtils.requestGetWithdrawOrder(cardInfoId, isCache, observer);
    }

    public List<CardInfo> getListCardInfo() {
        return CommonBeanManager.getInstance().getCardInfoList();
    }
}
