package com.gzligo.ebizzcardstranslator.business.account.view.login;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;

import io.reactivex.Observer;

/**
 * Created by Lwd on 2017/7/6.
 */

public class ForgetPhoneRepository implements IModel{

    public void requestValidateSms(String phone, String smsType, boolean isCache, Observer observable){
        HttpUtils.requestValidateSms(phone,smsType,isCache,observable);
    }

    public void requestResetPassword(String phone,String newPwd,String token, boolean isCache, Observer observable){
        HttpUtils.requestResetPassword(phone,newPwd,token,isCache,observable);
    }
}
