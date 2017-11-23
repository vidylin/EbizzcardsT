package com.gzligo.ebizzcardstranslator.business.history;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;

import io.reactivex.Observer;

/**
 * Created by Lwd on 2017/9/15.
 */

public class CallRecordsRepository implements IModel{

    public void requestTranslatorTravelOrderList(String until,String since,String count, Observer observer){
        HttpUtils.requestTranslatorTravelOrderList(until,since,count,observer);
    }
}
