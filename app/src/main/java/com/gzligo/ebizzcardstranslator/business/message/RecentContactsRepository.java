package com.gzligo.ebizzcardstranslator.business.message;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.db.manager.ChatMsgDbManager;
import com.gzligo.ebizzcardstranslator.db.manager.RecentContactsDbManager;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorList;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorSelectedBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.Observable.create;

/**
 * Created by Lwd on 2017/6/1.
 */

public class RecentContactsRepository implements IModel {
    private RecentContactsDbManager recentContactsDbManager;
    private ChatMsgDbManager chatMsgDbManager;
    private List<TranslatorSelectedBean> selectedBeanList;
    private List<Boolean> isOnline;
    private List<TranslatorSelectedBean> translatorSelectedBeanList = new ArrayList<>();
    private List<NewTransOrderBean> newTransLists = new ArrayList<>();

    public List<NewTransOrderBean> getNewTransLists() {
        return newTransLists;
    }

    public void setTranslatorSelectedBeanList(int position) {
        List<TranslatorSelectedBean> lists = recentContactsDbManager.getTranslatorSelectedBeanList();
        TranslatorSelectedBean translatorSelectedBean = lists.get(position);
        List<TranslatorSelectedBean> list = new ArrayList<>();
        list.addAll(lists);
        list.remove(position);
        list.add(0,translatorSelectedBean);
        translatorSelectedBeanList.clear();
        translatorSelectedBeanList.addAll(list);
    }

    public void resetTranslatorSelectedBeanList(){
        List<TranslatorSelectedBean> lists = recentContactsDbManager.getTranslatorSelectedBeanList();
        translatorSelectedBeanList.clear();
        translatorSelectedBeanList.addAll(lists);
    }

    public RecentContactsRepository() {
        recentContactsDbManager = new RecentContactsDbManager();
        chatMsgDbManager = new ChatMsgDbManager();
    }

    //获取最近和正在联系人
    public Observable<TranslatorList> queryRecentConstants() {
        return create(new ObservableOnSubscribe<TranslatorList>() {
            @Override
            public void subscribe(ObservableEmitter<TranslatorList> e) throws Exception {
                TranslatorList translatorList = recentContactsDbManager.queryRecentConstantList();
                List<TranslatorSelectedBean> list = translatorList.getTranslatorSelectedBeen();
                Log.e("queryRecentConstants", list.size() + "");
                if (null == selectedBeanList) {
                    selectedBeanList = new ArrayList<>();
                    selectedBeanList.addAll(list);
                } else {
                    selectedBeanList.clear();
                    selectedBeanList.addAll(list);
                }
                e.onNext(translatorList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public List<Boolean> getIsOnline() {
        return isOnline;
    }

    public List<NewTransOrderBean> getNewTransOrderBeanList(int pos) {
        if (null == isOnline) {
            isOnline = new ArrayList<>();
        } else {
            isOnline.clear();
        }
        List<NewTransOrderBean> list = new ArrayList<>();
        int count = selectedBeanList.size();
        TranslatorSelectedBean selectedBean = selectedBeanList.get(pos);
        isOnline.add(selectedBean.getIsTranslating() ? true : false);
        for (int i = 0; i < count; i++) {
            TranslatorSelectedBean data = selectedBeanList.get(i);
            NewTransOrderBean newTransOrderBean = new NewTransOrderBean();
            newTransOrderBean.setToUserId(data.getToUserId());
            newTransOrderBean.setToPortraitId(data.getToPortraitId());
            newTransOrderBean.setToLangId(data.getToLangId());
            newTransOrderBean.setToName(data.getToName());
            newTransOrderBean.setFromUserId(data.getFromUserId());
            newTransOrderBean.setFromPortraitId(data.getFromPortraitId());
            newTransOrderBean.setFromLangId(data.getFromLangId());
            newTransOrderBean.setFromName(data.getFromName());
            list.add(newTransOrderBean);
            if (i != pos) {
                isOnline.add(data.getIsTranslating());
            }
        }
        return list;
    }

    public Observable<List<NewTransOrderBean>> getNewTransOrderBeanList(final TranslatorSelectedBean selectedBean) {
        return create(new ObservableOnSubscribe<List<NewTransOrderBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<NewTransOrderBean>> e) throws Exception {
                boolean isContain = false;
                if (null == isOnline) {
                    isOnline = new ArrayList<>();
                } else {
                    isOnline.clear();
                }
                NewTransOrderBean newTransOrderBean = new NewTransOrderBean();
                newTransOrderBean.setToUserId(selectedBean.getToUserId());
                newTransOrderBean.setToPortraitId(selectedBean.getToPortraitId());
                newTransOrderBean.setToLangId(selectedBean.getToLangId());
                newTransOrderBean.setToName(selectedBean.getToName());
                newTransOrderBean.setFromUserId(selectedBean.getFromUserId());
                newTransOrderBean.setFromPortraitId(selectedBean.getFromPortraitId());
                newTransOrderBean.setFromLangId(selectedBean.getFromLangId());
                newTransOrderBean.setFromName(selectedBean.getFromName());
                newTransLists.clear();
                int count = translatorSelectedBeanList.size();
                if(count==0){
                    List<TranslatorSelectedBean> lists = recentContactsDbManager.getTranslatorSelectedBeanList();
                    translatorSelectedBeanList.addAll(lists);
                    count = translatorSelectedBeanList.size();
                }
                String condition = selectedBean.getFromUserId() + selectedBean.getToUserId();
                String condition2 = selectedBean.getToUserId() + selectedBean.getFromUserId();
                for(int i = 0; i < count; i++){
                    TranslatorSelectedBean data = translatorSelectedBeanList.get(i);
                    String userIds = data.getFromUserId() + data.getToUserId();
                    if (condition.equals(userIds)|| condition2.equals(userIds)) {
                        isContain = true;
                        newTransLists.add(newTransOrderBean);
                        isOnline.add(true);
                    }else{
                        NewTransOrderBean orderBean = new NewTransOrderBean();
                        orderBean.setToUserId(data.getToUserId());
                        orderBean.setToPortraitId(data.getToPortraitId());
                        orderBean.setToLangId(data.getToLangId());
                        orderBean.setToName(data.getToName());
                        orderBean.setFromUserId(data.getFromUserId());
                        orderBean.setFromPortraitId(data.getFromPortraitId());
                        orderBean.setFromLangId(data.getFromLangId());
                        orderBean.setFromName(data.getFromName());
                        newTransLists.add(orderBean);
                        isOnline.add(data.getIsTranslating());
                    }
                }
                if(!isContain){
                    newTransLists.add(0,newTransOrderBean);
                    isOnline.add(0,true);
                }
                e.onNext(newTransLists);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public ArrayMap<String, Integer> getTranslatorSelectedBeanMap(){
        return recentContactsDbManager.getArrayMap();
    }

    public Observable getUnTransMsgNum(){
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(recentContactsDbManager.getUnTransMsgNum());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }
}
