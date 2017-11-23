package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.AccountBean;
import com.gzligo.ebizzcardstranslator.persistence.Bank;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecordBean;

import java.util.List;
import java.util.Map;

/**
 * Created by Lwd on 2017/7/12.
 */

public class MyWalletPresenter extends BasePresenter<MyWalletRepository> {
    private static final int GET_ACCOUNT_INFO = 0x94;
    private static final int GET_CARD_INFO = 0x93;
    private static final int GET_BANK_LIST = 0x89;
    private static final int GET_CARD_INFO_NO = 0x90;
    private IView iView;

    public MyWalletPresenter(MyWalletRepository model, IView iView) {
        super(model);
        this.iView = iView;
    }

    public void requestGetAccount() {
        getModel().requestGetAccount(true, new BaseObserver<AccountBean>() {

            @Override
            public void onNext(AccountBean accountBean) {
                Message message = Message.obtain(iView);
                message.what = GET_ACCOUNT_INFO;
                message.obj = accountBean;
                message.dispatchToIView();
            }

        });
    }

    public void requestGetCardInfo() {
        getModel().requestGetCardInfo(true, new BaseObserver<MyWalletRecordBean<CardInfo>>() {

            @Override
            public void onNext(MyWalletRecordBean<CardInfo> cardInfo) {
                List<CardInfo> cardInfoList = sorting(cardInfo.getList());
                if (null != cardInfoList && cardInfoList.size() > 0) {
                    getModel().setCardInfo(cardInfoList);
                    Message message = Message.obtain(iView);
                    message.what = GET_CARD_INFO;
                    message.obj = cardInfoList.get(0);
                    message.dispatchToIView();
                }else{
                    getModel().setCardInfo(null);
                    Message message = Message.obtain(iView);
                    message.what = GET_CARD_INFO_NO;
                    message.dispatchToIView();
                }
            }

        });
    }

    private List<CardInfo> sorting(List<CardInfo> list) {
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                CardInfo cardInfo = list.get(i);
                if (cardInfo.getStatus() == 0) {
                    list.remove(cardInfo);
                    list.add(0, cardInfo);
                    return list;
                }
            }
        }
        return list;
    }

    public void requestGetBankType() {
        getModel().requestGetBankType(true, new BaseObserver<MyWalletRecordBean<Bank>>() {

            @Override
            public void onNext(MyWalletRecordBean<Bank> myWalletRecordBean) {
                List<Bank> bankList = myWalletRecordBean.getList();
                if (null != bankList && bankList.size() > 0) {
                    if (bankList.get(0) instanceof Bank) {
                        getModel().setBankList(bankList);
                        Message message = Message.obtain(iView);
                        message.what = GET_BANK_LIST;
                        message.dispatchToIView();
                    }
                }
            }

        });
    }

    public Map<Integer, Bank> getBankMap() {
        return getModel().getBankMap();
    }

    public List<CardInfo> getCardInfos() {
        return getModel().getCardInfoList();
    }


}
