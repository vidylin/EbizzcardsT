package com.gzligo.ebizzcardstranslator.business.call;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;

import io.reactivex.Observer;

/**
 * Created by Lwd on 2017/9/14.
 */

public class VoiceCallsRepository implements IModel{

    //旅游翻译--结束翻译
    public void requestTravelTuserFinish(String orderId,String reason, Observer observer ){
        HttpUtils.requestTravelTuserFinish(orderId,reason,observer);
    }

    //旅游翻译--接受翻译
    public void requestTravelTuserAccept(String orderId, Observer observer ){
        HttpUtils.requestTravelTuserAccept(orderId,observer);
    }

    //旅游翻译--拒绝翻译
    public void requestTravelTuserReject(String orderId, Observer observer ){
        HttpUtils.requestTravelTuserReject(orderId,observer);
    }
}
