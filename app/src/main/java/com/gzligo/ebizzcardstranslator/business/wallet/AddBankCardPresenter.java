package com.gzligo.ebizzcardstranslator.business.wallet;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.Country;
import com.gzligo.ebizzcardstranslator.persistence.ErrorMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;
import com.gzligo.ebizzcardstranslator.persistence.MyWalletRecordBean;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;

/**
 * Created by Lwd on 2017/7/12.
 */

public class AddBankCardPresenter extends BasePresenter<AddBankCardRepository> {
    private static final int GET_COUNTRY = 0x10;
    private static final int SETTING_PWD = 0x86;
    private static final int MSG_CODE = 0x84;
    private static final int VALIDATE_PWD_SUCCESS = 0x83;
    private static final int VALIDATE_PWD_ERROR = 0x82;
    private IView iView;

    public AddBankCardPresenter(AddBankCardRepository model, IView iView) {
        super(model);
        this.iView = iView;
    }

    public UserBean getUserBeanInfo() {
        return getModel().getUserBean();
    }

    public void requestGetState() {
        getModel().requestGetState(true, new BaseObserver<MyWalletRecordBean<Country>>() {

            @Override
            public void onNext(MyWalletRecordBean<Country> myWalletRecordBean) {
                Message message = Message.obtain(iView);
                message.what = GET_COUNTRY;
                message.obj = myWalletRecordBean;
                message.dispatchToIView();
            }

        });
    }

    public void requestResetWithdrawPasswd(String fields, String oldPwd, String token) {
        getModel().requestResetWithdrawPasswd(fields, oldPwd, token, true, new BaseObserver<HttpResultBean>() {

            @Override
            public void onNext(HttpResultBean httpResultBean) {
                if (0 == httpResultBean.getError()) {
                    Message message = Message.obtain(iView);
                    message.what = SETTING_PWD;
                    message.dispatchToIView();
                }

            }

        });
    }

    public void requestValidateSms(final Message message) {
        getModel().requestValidateSms((String) message.objs[0], (String) message.objs[1], true, new BaseObserver<ErrorMessageBean>() {

            @Override
            public void onNext(ErrorMessageBean errorMessageBean) {
                if (errorMessageBean.getError() == 0) {
                    message.what = MSG_CODE;
                    message.dispatchToIView();
                }
            }

        });
    }

    public void requestValidateWpasswd(String wpasswd) {
        getModel().requestValidateWpasswd(wpasswd, true, new BaseObserver<HttpResultBean>() {

            @Override
            public void onNext(HttpResultBean httpResultBean) {
                switch (httpResultBean.getMessage()) {
                    case "success":
                        Message message = Message.obtain(iView);
                        message.what = VALIDATE_PWD_SUCCESS;
                        message.dispatchToIView();
                        break;
                    case "wpasswd not match":
                        message = Message.obtain(iView);
                        message.what = VALIDATE_PWD_ERROR;
                        message.dispatchToIView();
                        break;
                }
            }

        });
    }
}
