package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.Bank;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;

/**
 * Created by Lwd on 2017/7/12.
 */

public class MyWalletRepository implements IModel {
    private Map<Integer, Bank> bankMap;
    private List<Bank> banks;
    private List<CardInfo> cardInfoList;

    public List<CardInfo> getCardInfoList() {
        return cardInfoList;
    }

    public void setCardInfo(List<CardInfo> cardInfo) {
        this.cardInfoList = cardInfo;
    }

    public List<Bank> getBankList() {
        return banks;
    }

    public Map<Integer, Bank> getBankMap() {
        return bankMap;
    }

    public void setBankList(List<Bank> bankList) {
        if (null == banks) {
            banks = new ArrayList<>();
        } else {
            banks.clear();
        }
        banks.addAll(bankList);
        if (null == bankMap) {
            bankMap = new HashMap<>();
        } else {
            bankMap.clear();
        }
        setBankMap();
    }

    public void requestGetAccount(boolean isCache, Observer observer) {
        HttpUtils.requestGetTaccount(isCache, observer);
    }

    public void requestGetCardInfo(boolean isCache, Observer observer) {
        HttpUtils.requestGetCardInfo(isCache, observer);
    }

    public void requestGetBankType(boolean isCache, Observer observer) {
        HttpUtils.requestGetBankType(isCache, observer);
    }

    private void setBankMap() {
        for (int i = 0; i < banks.size(); i++) {
            Bank bank = banks.get(i);
            bankMap.put(bank.getType_id(), bank);
        }
        CommonBeanManager.getInstance().setBankMap(bankMap);
        setListCardInfo();
    }

    private List<CardInfo> setListCardInfo() {
        List<CardInfo> infoList = new ArrayList<>();
        for (CardInfo cardInfo : cardInfoList) {
            int typeId = cardInfo.getBank_type();
            Bank bank = bankMap.get(typeId);
            cardInfo.setCardPortrait(bank.getPortrait());
            cardInfo.setBankCardNameZh(bank.getZh_name());
            cardInfo.setBankCardNameEn(bank.getEn_name());
            infoList.add(cardInfo);
        }
        cardInfoList.clear();
        cardInfoList.addAll(infoList);
        CommonBeanManager.getInstance().setCardInfoList(cardInfoList);
        return cardInfoList;
    }
}
