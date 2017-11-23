package com.gzligo.ebizzcardstranslator.business.order;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.db.manager.TravelTransOrderManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.NewTravelTransOrderBean;

import java.util.List;

import greendao.autogen.bean.NewTravelTransOrderBeanDao;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lwd on 2017/9/13.
 */

public class CallOrderRepository implements IModel {
    private TravelTransOrderManager travelTransOrderManager;

    public CallOrderRepository() {
        travelTransOrderManager = new TravelTransOrderManager();
    }

    public void requestTravelTuserObtain(String orderId, Observer observer) {
        HttpUtils.requestTravelTuserObtain( orderId, observer);
    }

    public Observable<List<NewTravelTransOrderBean>> queryOrderList() {
        return Observable.create(new ObservableOnSubscribe<List<NewTravelTransOrderBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<NewTravelTransOrderBean>> e) throws Exception {
                List<NewTravelTransOrderBean> lists = travelTransOrderManager.queryOrderList();
                e.onNext(lists);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteOrder(final NewTravelTransOrderBean newTransOrderBean) {
        Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> e) throws Exception {
                List<NewTravelTransOrderBean> beanList = travelTransOrderManager.getQueryBuilder().where(NewTravelTransOrderBeanDao.Properties.Session_id.eq(newTransOrderBean.getSession_id())).list();
                if (null != beanList && beanList.size() > 0) {
                    for (NewTravelTransOrderBean transOrderBean : beanList) {
                        travelTransOrderManager.delete(transOrderBean);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread()).subscribe();
    }
}
