package com.gzligo.ebizzcardstranslator.business.me;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;

import io.reactivex.Observer;

/**
 * Created by Lwd on 2017/7/7.
 */

public class SettingRepository implements IModel{

    public void requestNotificationClose(boolean isCache, Observer observer){
        HttpUtils.requestNotificationClose(isCache,observer);
    }

    public static void requestTaccountStatusUpdate(String status, boolean isCache, Observer observer) {
        HttpUtils.requesTaccountStatusUpdate(status,isCache,observer);
    }
}
