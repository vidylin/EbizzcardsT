package com.gzligo.ebizzcardstranslator.business.chat;

import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.db.manager.ChatMsgDbManager;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.UnTransMagNumberBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lwd on 2017/6/22.
 */

public class ChatRepository implements IModel {
    private ChatMsgDbManager chatMsgDbManager;
    private TreeMap<Integer, Integer> unTranslateMsgMap;
    private Map<String, Integer> mapIndex;
    private TreeMap<Integer, NewTransOrderBean> transOrderBeanTreeMap;
    private TreeMap<String, ChatFragment> userChatFragmentMap;

    public TreeMap<String, ChatFragment> getUserChatFragmentMap() {
        return userChatFragmentMap;
    }

    public Map<String, Integer> getMapIndex() {
        return mapIndex;
    }

    public void setUserChatFragmentMap(TreeMap<String, ChatFragment> userChatFragmentMap) {
        this.userChatFragmentMap = userChatFragmentMap;
    }

    public TreeMap<Integer, NewTransOrderBean> setMapIndex(List<NewTransOrderBean> list, int pos) {
        if (null == mapIndex) {
            mapIndex = new HashMap<>();
        } else {
            mapIndex.clear();
        }
        if (null == transOrderBeanTreeMap) {
            transOrderBeanTreeMap = new TreeMap<>();
        } else {
            transOrderBeanTreeMap.clear();
        }
        NewTransOrderBean orderBean = list.get(pos);
        mapIndex.put(orderBean.getFromUserId() + orderBean.getToUserId(), 0);
        transOrderBeanTreeMap.put(0, orderBean);
        for (int i = 0; i < list.size(); i++) {
            if (i != pos) {
                NewTransOrderBean transOrderBean = list.get(i);
                mapIndex.put(transOrderBean.getFromUserId() + transOrderBean.getToUserId(), mapIndex.size());
                transOrderBeanTreeMap.put(transOrderBeanTreeMap.size(), transOrderBean);
            }
        }
        return transOrderBeanTreeMap;
    }

    public TreeMap<Integer, NewTransOrderBean> updateMapIndex(List<NewTransOrderBean> list, int pos) {
        if (null == mapIndex) {
            mapIndex = new HashMap<>();
        } else {
            mapIndex.clear();
        }
        if (null == transOrderBeanTreeMap) {
            transOrderBeanTreeMap = new TreeMap<>();
        } else {
            transOrderBeanTreeMap.clear();
        }
        NewTransOrderBean orderBean = list.get(pos);
        mapIndex.put(orderBean.getFromUserId() + orderBean.getToUserId(), pos);
        transOrderBeanTreeMap.put(pos, orderBean);
        for (int i = 0; i < list.size(); i++) {
            if (i != pos) {
                NewTransOrderBean transOrderBean = list.get(i);
                mapIndex.put(transOrderBean.getFromUserId() + transOrderBean.getToUserId(), i);
                transOrderBeanTreeMap.put(i, transOrderBean);
            }
        }
        return transOrderBeanTreeMap;
    }

    public TreeMap<Integer, Integer> getUnTranslateMsgMap() {
        return unTranslateMsgMap;
    }

    public ChatRepository() {
        chatMsgDbManager = new ChatMsgDbManager();
    }

    public Observable<Boolean> queryUnTranslateMsgNum(final TreeMap<Integer, NewTransOrderBean> treeMap) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                if (null == unTranslateMsgMap) {
                    unTranslateMsgMap = new TreeMap<>();
                }
                for (int i = 0; i < treeMap.size(); i++) {
                    NewTransOrderBean orderBean = treeMap.get(i);
                    int count = chatMsgDbManager.queryUnTranslateMsgNum(orderBean.getFromUserId(), orderBean.getToUserId());
                    unTranslateMsgMap.put(i, count);
                }
                boolean result = true;
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UnTransMagNumberBean> updateUnTransPointNum(final String fromId, final String toId, final int number) {
        return Observable.create(new ObservableOnSubscribe<UnTransMagNumberBean>() {
            @Override
            public void subscribe(ObservableEmitter<UnTransMagNumberBean> e) throws Exception {
                if (null == unTranslateMsgMap) {
                    unTranslateMsgMap = new TreeMap<>();
                }
                Set<String> sets = mapIndex.keySet();
                for (String key : sets) {
                    if (key.contains(fromId) && key.contains(toId)) {
                        UnTransMagNumberBean magNumberBean = new UnTransMagNumberBean();
                        int index = mapIndex.get(key);
                        int num;
                        if (null == unTranslateMsgMap.get(index)) {
                            num = number;
                        } else {
                            num = unTranslateMsgMap.get(index) + number;
                        }
                        unTranslateMsgMap.put(index, num);
                        magNumberBean.setIndex(index);
                        magNumberBean.setNumber(num);
                        e.onNext(magNumberBean);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public TreeMap<Integer, LanguagesBean> getLanguageMap() {
        return CommonBeanManager.getInstance().getTreeMap();
    }
}
