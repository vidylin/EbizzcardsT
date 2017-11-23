package com.gzligo.ebizzcardstranslator.business.order;

import android.util.Log;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.db.manager.OrderDbManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;

import java.util.List;

import greendao.autogen.bean.NewTransOrderBeanDao;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lwd on 2017/6/8.
 */

public class OrderRepository implements IModel {
    private OrderDbManager orderDbManager;

    public OrderRepository() {
        orderDbManager = new OrderDbManager();
    }

    public void requestOrderObtain(String sessionId, boolean isCache, Observer observer) {
        HttpUtils.requesOrderObtaind(sessionId, isCache, observer);
    }

    public Observable<List<NewTransOrderBean>> queryOrderList() {
        return Observable.create(new ObservableOnSubscribe<List<NewTransOrderBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<NewTransOrderBean>> e) throws Exception {
                List<NewTransOrderBean> lists = orderDbManager.queryOrderList();
                Log.e("queryOrderList=", lists.size() + "");
                e.onNext(lists);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteOrder(final NewTransOrderBean newTransOrderBean) {
        Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> e) throws Exception {
                List<NewTransOrderBean> beanList = orderDbManager.getQueryBuilder().where(NewTransOrderBeanDao.Properties.OrderId.eq(newTransOrderBean.getOrderId())).list();
                if (null != beanList && beanList.size() > 0) {
                    for (NewTransOrderBean transOrderBean : beanList) {
                        orderDbManager.delete(transOrderBean);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread()).subscribe();
    }

}
