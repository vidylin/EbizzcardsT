package com.gzligo.ebizzcardstranslator.business.account.view.login;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;

/**
 * Created by Lwd on 2017/7/6.
 */

public class ForgetPhonePresenter extends BasePresenter<ForgetPhoneRepository>{
    private static final int COUNTRY_CODE = 0x50;
    private static final int RESET_PWD_SUCCESS = 0x51;

    public ForgetPhonePresenter(ForgetPhoneRepository model) {
        super(model);
    }

    public void requestValidateSms(final Message message){
        getModel().requestValidateSms((String) message.objs[0],(String)message.objs[1], true, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                if(errorMessageBean.getError()==0){
                    message.what = COUNTRY_CODE;
                    message.obj = errorMessageBean;
                    message.dispatchToIView();
                }else {
                    message.obj = errorMessageBean;
                    message.what = COUNTRY_CODE;
                    message.dispatchToIView();
                }
            }

        });
    }

    public void requestResetPassword(final Message message){
        getModel().requestResetPassword((String) message.objs[0],(String)message.objs[1],(String)message.objs[2], true, new BaseObserver<HttpResultBean>() {

            @Override
            public void onNext(HttpResultBean o) {
                if(o.getError()==0){
                    message.what = RESET_PWD_SUCCESS;
                    message.dispatchToIView();
                }
            }

        });
    }
}
