package com.gzligo.ebizzcardstranslator.db.manager;

import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.db.BaseDBManager;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import greendao.autogen.bean.ChatMessageBeanDao;

/**
 * Created by Lwd on 2017/6/20.
 */

public class ChatMsgDbManager extends BaseDBManager<ChatMessageBean,Long>{

    @Override
    public AbstractDao<ChatMessageBean, Long> getAbstractDao() {
        return daoSession.getChatMessageBeanDao();
    }

    public List<ChatMessageBean> getChatHisToryMsg(String fromId, String toId,String orderId){
        WhereCondition whereCondition = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.FromId.eq(fromId),
                ChatMessageBeanDao.Properties.FromId.eq(toId));
        WhereCondition whereCondition2 = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.ToId.eq(fromId),
                ChatMessageBeanDao.Properties.ToId.eq(toId));

        WhereCondition[] whereConditions = {whereCondition2,
                ChatMessageBeanDao.Properties.OrderId.eq(orderId)};
        List<ChatMessageBean> lists = queryRaw(whereCondition,whereConditions);
        return lists;
    }

    public int queryUnTranslateMsgNum(String fromId,String toId){
        WhereCondition whereCondition = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.FromId.eq(fromId),
                ChatMessageBeanDao.Properties.FromId.eq(toId));
        WhereCondition whereCondition2 = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.ToId.eq(fromId),
                ChatMessageBeanDao.Properties.ToId.eq(toId));
        WhereCondition whereCondition1 = ChatMessageBeanDao.Properties.MsgIsTrans.eq(false);
        WhereCondition whereCondition3 = ChatMessageBeanDao.Properties.Type.notEq(ChatConstants.COMMON_PRODUCT_CHAT);
        WhereCondition whereCondition4 = ChatMessageBeanDao.Properties.Type.notEq(ChatConstants.COMMON_VIDEO);
        WhereCondition[] condition = {ChatMessageBeanDao.Properties.IsPrivateMessage.eq(false),
                whereCondition,
                whereCondition2,
                whereCondition3,
                whereCondition4,
                ChatMessageBeanDao.Properties.MsgId.notEq(""),
                ChatMessageBeanDao.Properties.MsgId.isNotNull()};
        long number = getQueryBuilder().where(whereCondition1,condition).count();
        return (int)number;
    }

    public boolean insertEndMsg(ChatMessageBean startChat){
        boolean result = false;
        String fromId = startChat.getFromId();
        String toId = startChat.getToId();
        WhereCondition whereCondition = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.FromId.eq(fromId),
                ChatMessageBeanDao.Properties.FromId.eq(toId));
        WhereCondition whereCondition2 = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.ToId.eq(fromId),
                ChatMessageBeanDao.Properties.ToId.eq(toId));
        List<ChatMessageBean> lists =getQueryBuilder().where(whereCondition,whereCondition2).build().list();
        if(null!=lists&&lists.size()>0){
            result = insert(startChat);
        }
        return result;
    }

    public String getLastMsg(ChatMessageBean msg){
        String fromId = msg.getFromId();
        String toId = msg.getToId();
        WhereCondition whereCondition = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.FromId.eq(fromId),
                ChatMessageBeanDao.Properties.FromId.eq(toId));
        WhereCondition whereCondition2 = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.ToId.eq(fromId),
                ChatMessageBeanDao.Properties.ToId.eq(toId));
        WhereCondition[] whereCondition3 = {whereCondition2,ChatMessageBeanDao.Properties.OrderId.notEq("")};
        ChatMessageBean chatMessageBean = getQueryBuilder().where(whereCondition,whereCondition3)
                .orderDesc(ChatMessageBeanDao.Properties.MsgTime).limit(1).unique();
        return chatMessageBean.getOrderId();
    }

    public List<ChatMessageBean> getAllMsgByClientId(String fromId,String toId,String orderId){
        WhereCondition whereCondition = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.FromId.eq(fromId),
                ChatMessageBeanDao.Properties.FromId.eq(toId));
        WhereCondition whereCondition2 = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.ToId.eq(fromId),
                ChatMessageBeanDao.Properties.ToId.eq(toId));
        List<ChatMessageBean> lists;
        if(null!=orderId){
            WhereCondition whereCondition3 = ChatMessageBeanDao.Properties.OrderId.eq(orderId);
            lists = queryRaw(whereCondition,whereCondition2,whereCondition3);
        }else{
            lists = queryRaw(whereCondition,whereCondition2);
        }
        return lists;
    }

    public List<ChatMessageBean> getProductMsgByClientId(String fromId,String toId,int type,String orderId){
        WhereCondition whereCondition = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.FromId.eq(fromId),
                ChatMessageBeanDao.Properties.FromId.eq(toId));
        WhereCondition whereCondition2 = getQueryBuilder().or(
                ChatMessageBeanDao.Properties.ToId.eq(fromId),
                ChatMessageBeanDao.Properties.ToId.eq(toId));
        WhereCondition whereCondition3 = ChatMessageBeanDao.Properties.Type.eq(type);
        List<ChatMessageBean> lists;
        if(null!=orderId){
            WhereCondition whereCondition4 = ChatMessageBeanDao.Properties.OrderId.eq(orderId);
            lists = queryRaw(whereCondition,whereCondition2,whereCondition3,whereCondition4);
        }else{
            lists = queryRaw(whereCondition,whereCondition2,whereCondition3);
        }
        return lists;
    }
}
