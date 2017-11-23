package com.gzligo.ebizzcardstranslator.db.manager;

import android.support.v4.util.ArrayMap;

import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.db.BaseDBManager;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorList;
import com.gzligo.ebizzcardstranslator.persistence.TranslatorSelectedBean;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import greendao.autogen.bean.TranslatorSelectedBeanDao;

/**
 * Created by Lwd on 2017/6/20.
 */

public class RecentContactsDbManager extends BaseDBManager<TranslatorSelectedBean,Long> {

    private ArrayMap<String,Integer> arrayMap;
    private  List<TranslatorSelectedBean> translatorSelectedBeanList = new ArrayList<>();

    public ArrayMap<String, Integer> getArrayMap() {
        return arrayMap;
    }

    public List<TranslatorSelectedBean> getTranslatorSelectedBeanList() {
        return translatorSelectedBeanList;
    }

    @Override
    public AbstractDao<TranslatorSelectedBean, Long> getAbstractDao() {
        return daoSession.getTranslatorSelectedBeanDao();
    }

    public TranslatorSelectedBean getRecentContact(String fromId,String toId){
        List<TranslatorSelectedBean> translatorSelectedBean ;
        WhereCondition whereCondition = TranslatorSelectedBeanDao.Properties.FromUserId.eq(fromId);
        WhereCondition whereCondition1 = TranslatorSelectedBeanDao.Properties.ToUserId.eq(toId);
        translatorSelectedBean = queryRaw(whereCondition,whereCondition1);
        if(null==translatorSelectedBean){
            whereCondition = TranslatorSelectedBeanDao.Properties.FromUserId.eq(toId);
            whereCondition1 = TranslatorSelectedBeanDao.Properties.ToUserId.eq(fromId);
            translatorSelectedBean = queryRaw(whereCondition,whereCondition1);
        }
        TranslatorSelectedBean selectedBean = null;
        if(null != translatorSelectedBean&&translatorSelectedBean.size()>0){
            if(translatorSelectedBean.size()==1){
                selectedBean = translatorSelectedBean.get(0);
            }else{
                selectedBean = translatorSelectedBean.get(0);
                for(int i=1;i<translatorSelectedBean.size();i++){
                    delete(translatorSelectedBean.get(i));
                }
            }
        }
        return selectedBean;
    }

    public TranslatorList queryRecentConstantList(){
        WhereCondition condTrue = TranslatorSelectedBeanDao.Properties.IsTranslating.eq(true);
        WhereCondition condFalse = TranslatorSelectedBeanDao.Properties.IsTranslating.eq(false);
        TranslatorList translatorList = new TranslatorList();
        List<TranslatorSelectedBean> list = new ArrayList<>();
        List<TranslatorSelectedBean> listTrue = getAbstractDao()
                .queryBuilder()
                .where(condTrue)
                .orderDesc(TranslatorSelectedBeanDao.Properties.UpdateTime).build().list();
        List<TranslatorSelectedBean> listFalse = getAbstractDao()
                .queryBuilder()
                .where(condFalse)
                .orderDesc(TranslatorSelectedBeanDao.Properties.UpdateTime).build().list();
        translatorSelectedBeanList.clear();
        if(null!=listTrue&&listTrue.size()>0){
            list.addAll(listTrue);
            translatorSelectedBeanList.addAll(listTrue);
        }
        if(null!=listFalse&&listFalse.size()>0){
            list.addAll(listFalse);
            translatorSelectedBeanList.addAll(listFalse);
        }
        translatorList.setTranslatorSelectedBeen(list);
        if(null==arrayMap){
            arrayMap = new ArrayMap<>();
        }else{
            arrayMap.clear();
        }
        int unTransMsgNum = 0;
        if(null!=list&&list.size()>0){
            for(int i=0;i<list.size();i++){
                TranslatorSelectedBean selectedBean = list.get(i);
                if(null!=selectedBean.getUnTransMsg()&&selectedBean.getUnTransMsg()>0){
                    unTransMsgNum = unTransMsgNum+selectedBean.getUnTransMsg();
                }
                arrayMap.put(selectedBean.getSessionId(),i);
            }
        }
        translatorList.setPosition(unTransMsgNum);
        return translatorList;
    }

    public int getUnTransMsgNum(){
        List<TranslatorSelectedBean> translatorSelectedBeanList = getAbstractDao()
                .queryBuilder().build().list();
        int unTransMsgNum = 0;
        if(null!=translatorSelectedBeanList&&translatorSelectedBeanList.size()>0){
            for(TranslatorSelectedBean selectedBean : translatorSelectedBeanList){
                if(null!=selectedBean.getUnTransMsg()&&selectedBean.getUnTransMsg()>0){
                    unTransMsgNum = unTransMsgNum+selectedBean.getUnTransMsg();
                }
            }
        }
        return unTransMsgNum;
    }

    public void updateUnTranslateMsgNumber(final ChatMessageBean chatMessageBean,int unTransNumber,boolean isTrans) {
        TranslatorSelectedBean selectedBean = getRecentContact(chatMessageBean.getFromId(),chatMessageBean.getToId());
        if (null == selectedBean) {
            selectedBean = new TranslatorSelectedBean();
            selectedBean.setIsTranslating(chatMessageBean.getIsChoiceTranslate());
            selectedBean.setNotifyTime(System.currentTimeMillis());
            selectedBean.setUnTransMsg(1);
            selectedBean.setTranslatorMsg(chatMessageBean.getContent());
            selectedBean.setFromUserId(chatMessageBean.getFromId());
            selectedBean.setToUserId(chatMessageBean.getToId());
            selectedBean.setFromName(chatMessageBean.getFromName());
            selectedBean.setToName(chatMessageBean.getToName());
            selectedBean.setSessionId(chatMessageBean.getOrderId());
            getAbstractDao().insert(selectedBean);
        } else {
            if(isTrans){
                selectedBean.setTranslatorMsg(chatMessageBean.getTranslateContent());
            }else{
                selectedBean.setTranslatorMsg(chatMessageBean.getContent());
            }
            selectedBean.setNotifyTime(System.currentTimeMillis());
            if (!chatMessageBean.getIsPrivateMessage() && chatMessageBean.getMsgId().length() > 0 && chatMessageBean.getType() != 6
                    && chatMessageBean.getType() != ChatConstants.COMMON_VIDEO) {
                selectedBean.setUnTransMsg(unTransNumber);
            }
            selectedBean.setUpdateTime(System.currentTimeMillis());
            getAbstractDao().update(selectedBean);
        }
    }

    public boolean insertOrUpdate(TranslatorSelectedBean translatorSelectedBean){
        String fromId = translatorSelectedBean.getFromUserId();
        String toId = translatorSelectedBean.getToUserId();
        TranslatorSelectedBean selectedBean = getRecentContact(fromId, toId);
        boolean result;
        if (null != selectedBean) {
            if (translatorSelectedBean.getStatus() == ChatConstants.SYSTEM_START_TRANSLATION) {
                int unTransMsgNumber = 0;
                if (null != selectedBean.getUnTransMsg()) {
                    unTransMsgNumber = selectedBean.getUnTransMsg();
                }
                delete(selectedBean);
                translatorSelectedBean.setUnTransMsg(unTransMsgNumber);
                translatorSelectedBean.setIsTranslating(true);
                result = insert(translatorSelectedBean);
            } else {
                selectedBean.setNotifyTime(translatorSelectedBean.getNotifyTime());
                selectedBean.setTranslatorMsg(translatorSelectedBean.getTranslatorMsg());
                selectedBean.setEndTime(translatorSelectedBean.getEndTime());
                selectedBean.setUpdateTime(translatorSelectedBean.getUpdateTime());
                selectedBean.setIsTranslating(false);
                result = update(selectedBean);
            }
        } else {
            if (translatorSelectedBean.getStatus() == ChatConstants.SYSTEM_START_TRANSLATION) {
                translatorSelectedBean.setUpdateTime(System.currentTimeMillis());
                translatorSelectedBean.setIsTranslating(true);
                result = insert(translatorSelectedBean);
            } else {
                result = false;
            }
        }
        return result;
    }

    public void insertTranslatorSelected(final ChatMessageBean chatMessageBean,String fromPortrait,String toPortrait){
        TranslatorSelectedBean selectedBean = new TranslatorSelectedBean();
        selectedBean.setIsTranslating(chatMessageBean.getIsChoiceTranslate());
        selectedBean.setNotifyTime(System.currentTimeMillis());
        selectedBean.setUnTransMsg(1);
        selectedBean.setTranslatorMsg(chatMessageBean.getContent());
        selectedBean.setFromUserId(chatMessageBean.getFromId());
        selectedBean.setToUserId(chatMessageBean.getToId());
        selectedBean.setFromName(chatMessageBean.getFromName());
        selectedBean.setToName(chatMessageBean.getToName());
        selectedBean.setSessionId(chatMessageBean.getOrderId());
        selectedBean.setFromPortraitId(fromPortrait);
        selectedBean.setToPortraitId(toPortrait);
        getAbstractDao().insert(selectedBean);
    }

}
