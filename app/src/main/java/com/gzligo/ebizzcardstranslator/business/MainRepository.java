package com.gzligo.ebizzcardstranslator.business;

import android.content.Context;
import android.content.SharedPreferences;

import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.db.manager.OrderDbManager;
import com.gzligo.ebizzcardstranslator.db.manager.TravelTransOrderManager;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTravelTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;

import java.util.List;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lwd on 2017/6/3.
 */

public class MainRepository implements IModel {
    private UserBean userBean;
    private TreeMap<Integer,LanguagesBean> treeMap;
    private OrderDbManager orderDbManager;
    private TravelTransOrderManager travelTransOrderManager;

    public MainRepository() {
        orderDbManager = new OrderDbManager();
        travelTransOrderManager = new TravelTransOrderManager();
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
        CommonBeanManager.getInstance().setUserBean(userBean);
    }

    public TreeMap<Integer, LanguagesBean> getTreeMap() {
        return treeMap;
    }

    public void setTreeMap(TreeMap<Integer, LanguagesBean> treeMap) {
        this.treeMap = treeMap;
        CommonBeanManager.getInstance().setTreeMap(treeMap);
    }

    public static void requesTaccountStatusUpdate(String status, boolean isCache, Observer observer) {
        HttpUtils.requesTaccountStatusUpdate(status,isCache,observer);
    }

    public void getUserBeanInfo(boolean isCache,Observer observer){
        HttpUtils.requestProfileSelf(isCache,observer);
    }

    //保存用户基本信息
    public void saveUserInfo(final UserBean userBean){
        Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> e) throws Exception {
                SharedPreferences sharedPreferences = AppManager.get()
                        .getApplication()
                        .getSharedPreferences(CommonConstants.USER_INFO_PRE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CommonConstants.USER_NAME, userBean.getNickname());
                editor.putString(CommonConstants.USER_PORTRAIT_ID, userBean.getPortrait_id());
                editor.commit();
            }
        }).subscribeOn(Schedulers.newThread()).subscribe();
    }

    public Observable<Integer> getUnGrabTransOrderNumber(){
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                List<NewTransOrderBean> newTransOrderBeanList = orderDbManager.queryOrderList();
                List<NewTravelTransOrderBean> travelTransOrderBeanList = travelTransOrderManager.queryOrderList();
                int count = 0;
                if(null!=newTransOrderBeanList){
                    count = newTransOrderBeanList.size();
                }
                if(null!=travelTransOrderBeanList){
                    count = travelTransOrderBeanList.size()+count;
                }
                e.onNext(count);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }
}
