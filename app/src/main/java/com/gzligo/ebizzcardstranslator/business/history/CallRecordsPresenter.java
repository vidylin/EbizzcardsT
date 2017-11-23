package com.gzligo.ebizzcardstranslator.business.history;

import com.gzligo.ebizzcardstranslator.base.mvp.BasePresenter;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.net.retrofit.BaseObserver;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslateBean;

import java.util.TreeMap;

import io.reactivex.annotations.NonNull;

/**
 * Created by Lwd on 2017/9/15.
 */

public class CallRecordsPresenter extends BasePresenter<CallRecordsRepository>{
    private IView iView;

    public CallRecordsPresenter(CallRecordsRepository model,IView iView) {
        super(model);
        this.iView = iView;
    }

    public void requestTranslatorTravelOrderList(String until, String since, String count, final int actionType){
        getModel().requestTranslatorTravelOrderList(until, since, count, new BaseObserver<TravelTranslateBean>() {
            @Override
            public void onNext(@NonNull TravelTranslateBean travelTranslateBean) {
                Message message = Message.obtain(iView);
                message.what = actionType;
                message.obj = travelTranslateBean;
                message.dispatchToIView();
            }
        });
    }

    public TreeMap<Integer, LanguagesBean> getTreeMap() {
        return CommonBeanManager.getInstance().getTreeMap();
    }
}
