package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.Bank;
import com.gzligo.ebizzcardstranslator.persistence.BankPoint;
import com.gzligo.ebizzcardstranslator.persistence.City;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecordBean;
import com.gzligo.ebizzcardstranslator.persistence.Province;

/**
 * Created by Lwd on 2017/7/12.
 */

public class CountryAndBankListPresenter extends BasePresenter<CountryAndBankListRepository> {
    private IView iView;
    private static final int GET_BANK_LIST = 0x89;
    private static final int GET_PROVINCE_LIST = 0x88;
    private static final int GET_CITY_LIST = 0x87;
    private static final int GET_BANK_POINT_LIST = 0x86;

    public CountryAndBankListPresenter(CountryAndBankListRepository model, IView iView) {
        super(model);
        this.iView = iView;
    }

    public void requestGetBankType() {
        getModel().requestGetBankType(true, new BaseObserver<MyWalletRecordBean<Bank>>() {

            @Override
            public void onNext(MyWalletRecordBean<Bank> myWalletRecordBean) {
                Message message = Message.obtain(iView);
                message.what = GET_BANK_LIST;
                message.obj = myWalletRecordBean.getList();
                message.dispatchToIView();
            }

        });
    }

    //查询省份
    public void requestAddOrderGetState(String stateId) {
        getModel().requestAddOrderGetState(stateId, true, new BaseObserver<MyWalletRecordBean<Province>>() {

            @Override
            public void onNext(MyWalletRecordBean<Province> provinceMyWalletRecordBean) {
                Message message = Message.obtain(iView);
                message.what = GET_PROVINCE_LIST;
                message.obj = provinceMyWalletRecordBean.getList();
                message.dispatchToIView();
            }

        });
    }

    //查询城市
    public void requestGetCity(String provId) {
        getModel().requestGetCity(provId, true, new BaseObserver<MyWalletRecordBean<City>>() {

            @Override
            public void onNext(MyWalletRecordBean<City> cityMyWalletRecordBean) {
                Message message = Message.obtain(iView);
                message.what = GET_CITY_LIST;
                message.obj = cityMyWalletRecordBean.getList();
                message.dispatchToIView();
            }

        });
    }

    //查询银行网点
    public void requestGetBank(String bankTypeId, String cityId) {
        getModel().requestGetBank(bankTypeId, cityId, true, new BaseObserver<MyWalletRecordBean<BankPoint>>() {

            @Override
            public void onNext(MyWalletRecordBean<BankPoint> bankPointMyWalletRecordBean) {
                Message message = Message.obtain(iView);
                message.what = GET_BANK_POINT_LIST;
                message.obj = bankPointMyWalletRecordBean.getList();
                message.dispatchToIView();
            }

        });
    }
}
