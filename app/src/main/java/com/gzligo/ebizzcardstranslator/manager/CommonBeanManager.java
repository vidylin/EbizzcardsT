package com.gzligo.ebizzcardstranslator.manager;

import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.persistence.Bank;
import com.gzligo.ebizzcardstranslator.persistence.BankPoint;
import com.gzligo.ebizzcardstranslator.persistence.CardInfo;
import com.gzligo.ebizzcardstranslator.persistence.City;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.MyApplicationBean;
import com.gzligo.ebizzcardstranslator.persistence.Province;
import com.gzligo.ebizzcardstranslator.persistence.UserBean;
import com.gzligo.ebizzcardstranslator.utils.FileManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Lwd on 2017/7/14.
 */

public class CommonBeanManager {
    private UserBean userBean;
    private TreeMap<Integer,LanguagesBean> treeMap;
    private List<CardInfo> cardInfoList;
    private Map<Integer,Bank> bankMap;
    private List<MyApplicationBean> auditList;
    private int badgeCount;
    private Province province;
    private City city;
    private BankPoint bankPoint;
    private Map<String,Integer> unReadMsgMap = new HashMap<>();
    private boolean isChatting = false;

    private CommonBeanManager() {
    }

    private static class Singleton {
        private static CommonBeanManager sInstance = new CommonBeanManager();
    }

    public static CommonBeanManager getInstance() {
        return Singleton.sInstance;
    }

    public boolean isChatting() {
        return isChatting;
    }

    public void setChatting(boolean chatting) {
        isChatting = chatting;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public TreeMap<Integer, LanguagesBean> getTreeMap() {
        return treeMap;
    }

    public void setTreeMap(TreeMap<Integer, LanguagesBean> treeMap) {
        this.treeMap = treeMap;
    }

    public List<CardInfo> getCardInfoList() {
        return cardInfoList;
    }

    public void setCardInfoList(List<CardInfo> cardInfoList) {
        this.cardInfoList = cardInfoList;
    }

    public Map<Integer, Bank> getBankMap() {
        return bankMap;
    }

    public void setBankMap(Map<Integer, Bank> bankMap) {
        this.bankMap = bankMap;
    }

    public List<MyApplicationBean> getAuditList() {
        return auditList;
    }

    public void setAuditList(List<MyApplicationBean> auditList) {
        this.auditList = auditList;
    }

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public BankPoint getBankPoint() {
        return bankPoint;
    }

    public void setBankPoint(BankPoint bankPoint) {
        this.bankPoint = bankPoint;
    }

    public void initManager(){
        userBean = null;
        badgeCount = 0;
        if(null!=treeMap){
            treeMap.clear();
        }
        if(null!=cardInfoList){
            cardInfoList.clear();
        }
        if(null!=bankMap){
            bankMap.clear();
        }
        if (auditList!=null){
            auditList.clear();
        }
    }

    public void initMyWalletInfo(){
        if(null!=province){
            province = null;
        }
        if(null!=city){
            city = null;
        }
        if(null!=bankPoint){
            bankPoint = null;
        }
    }

    public interface OnLanguageListener {
        void onGetLanguages(TreeMap<Integer,LanguagesBean> treeMap);
    }

    public void getLanguageList(final OnLanguageListener onLanguageListener){
        FileManager.getFileManager(AppManager.get().getApplication())
                .readConfigBeanFileObservableMap(LanguagesBean.class).subscribe(new Consumer<TreeMap<Integer,LanguagesBean>>() {
            @Override
            public void accept(@NonNull TreeMap<Integer,LanguagesBean> treeMap) throws Exception {
                if(null!=treeMap&&treeMap.size()>0){
                    setTreeMap(treeMap);
                    onLanguageListener.onGetLanguages(treeMap);
                }
            }
        });
    }

    public void setUnReadMsgMap(String fromId){
        if(unReadMsgMap.containsKey(fromId)){
            int number = unReadMsgMap.get(fromId);
            unReadMsgMap.put(fromId,number+1);
        }else{
            unReadMsgMap.put(fromId,1);
        }
    }

    public int getUnReadMsgNumber(){
        Set<String> keySet = unReadMsgMap.keySet();
        int count = 0;
        if(null!=keySet&&keySet.size()>0){
            for(String key : keySet){
                count = count+unReadMsgMap.get(key);
            }
        }
        return count;
    }

    public int getUnReadMsgUserCount(){
        return unReadMsgMap.size();
    }

    public void readMsg(){
        unReadMsgMap.clear();
    }
}
