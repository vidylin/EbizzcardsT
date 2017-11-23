package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;

import io.reactivex.Observer;

/**
 * Created by Lwd on 2017/7/12.
 */

public class CountryAndBankListRepository implements IModel {

    public void requestGetBankType(boolean isCache, Observer observer) {
        HttpUtils.requestGetBankType(isCache, observer);
    }

    //查询省份
    public void requestAddOrderGetState(String stateId, boolean isCache, Observer observer) {
        HttpUtils.requestAddOrderGetState(stateId, isCache, observer);
    }

    //查询城市
    public void requestGetCity(String provId, boolean isCache, Observer observer) {
        HttpUtils.requestGetCity(provId, isCache, observer);
    }

    //查询银行网点
    public void requestGetBank(String bankTypeId, String cityId, boolean isCache, Observer observer) {
        HttpUtils.requestGetBank(bankTypeId, cityId, isCache, observer);
    }
}
