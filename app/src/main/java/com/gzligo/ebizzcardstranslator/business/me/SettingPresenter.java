package com.gzligo.ebizzcardstranslator.business.me;

import android.util.Log;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.mqtt.MqttManager;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;

import io.reactivex.annotations.NonNull;

/**
 * Created by Lwd on 2017/7/7.
 */

public class SettingPresenter extends BasePresenter<SettingRepository> {
    private static final int LOGOUT_RESULT = 0x23;

    public SettingPresenter(SettingRepository model) {
        super(model);
    }

    public void logout(final Message message){
        getModel().requestNotificationClose(true, new BaseObserver<HttpResultBean>() {

            @Override
            public void onNext(HttpResultBean o) {
                userStatusUpdate(message);
            }
            @Override
            public void onError(Throwable t) {
                Log.e("d", Log.getStackTraceString(t));
                userStatusUpdate(message);
            }
        });
    }

    private void userStatusUpdate(final Message msg){
        getModel().requestTaccountStatusUpdate("0",true, new BaseObserver<HttpResultBean>() {

            @Override
            public void onNext(HttpResultBean o) {
            }

            @Override
            public void onComplete() {
                CommonBeanManager.getInstance().initManager();
                MqttManager.get().logout();
                msg.what = LOGOUT_RESULT;
                msg.dispatchToIView();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                CommonBeanManager.getInstance().initManager();
                MqttManager.get().logout();
                msg.what = LOGOUT_RESULT;
                msg.dispatchToIView();
            }
        });
    }
}
