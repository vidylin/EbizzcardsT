package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;

import io.reactivex.Observer;

/**
 * Created by Lwd on 2017/7/12.
 */

public class AddBankCardRepository implements IModel {

    public UserBean getUserBean() {
        return CommonBeanManager.getInstance().getUserBean();
    }

    public void requestGetState(boolean isCache, Observer observer) {
        HttpUtils.requestGetState(isCache, observer);
    }

    public void requestResetWithdrawPasswd(String fields, String oldPwd, String token, boolean isCache, Observer observer) {
        HttpUtils.requestResetWithdrawPasswd(fields, oldPwd, token, isCache, observer);
    }

    public void requestValidateSms(String phone, String smsType, boolean isCache, Observer observable) {
        HttpUtils.requestValidateSms(phone, smsType, isCache, observable);
    }

    public void requestValidateWpasswd(String wpasswd, boolean isCache, Observer observer) {
        HttpUtils.requestValidateWpasswd(wpasswd, isCache, observer);
    }
}
