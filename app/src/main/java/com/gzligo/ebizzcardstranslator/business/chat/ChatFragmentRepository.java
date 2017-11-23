package com.gzligo.ebizzcardstranslator.business.chat;

import android.util.Log;

import com.google.gson.Gson;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.constants.FileConstants;
import com.gzligo.ebizzcardstranslator.db.manager.ChatMsgDbManager;
import com.gzligo.ebizzcardstranslator.db.manager.RecentContactsDbManager;
import com.gzligo.ebizzcardstranslator.mqtt.MqttChatManager;
import com.gzligo.ebizzcardstranslator.mqtt.MqttManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.ChatMsgProperty;
import com.gzligo.ebizzcardstranslator.persistence.HttpResultBean;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;
import com.gzligo.ebizzcardstranslator.persistence.ProductExtraInfo;
import com.gzligo.ebizzcardstranslator.utils.FileManager;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import greendao.autogen.bean.ChatMessageBeanDao;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Lwd on 2017/6/9.
 */

public class ChatFragmentRepository implements IModel {
    private ChatMessageBean chatMessageBean;
    private ChatCallBack chatCallBack;
    private ChatMsgDbManager chatMsgDbManager;
    private RecentContactsDbManager recentContactsDbManager;
    private int translateWhich = -1;
    private Map<String, Boolean> isChoiceMsgToTrans;

    private List<ChatMessageBean> allChatMsg = new ArrayList<>();
    private TreeMap<Integer,Boolean> unTransMsgPositions = new TreeMap<>();
    private TreeMap<String, Integer> chatMsgIndex = new TreeMap<>();
    private List<ChatMsgProperty> chatMsgProperties;
    private List<List<ProductDetail>> pLists;
    private boolean isReTranslating = false;

    public boolean isReTranslating() {
        return isReTranslating;
    }

    public void setReTranslating(boolean reTranslating) {
        isReTranslating = reTranslating;
    }

    public List<List<ProductDetail>> getProductDetails() {
        return pLists;
    }

    public List<ChatMsgProperty> getChatMsgProperties() {
        return chatMsgProperties;
    }

    public void setChatMsgIndex(TreeMap<String, Integer> chatMsgIndex) {
        this.chatMsgIndex = chatMsgIndex;
    }

    public TreeMap<String, Integer> getChatMsgIndex() {
        return chatMsgIndex;
    }

    public void setAllChatMsg(List<ChatMessageBean> allChatMsg) {
        this.allChatMsg = allChatMsg;
    }

    public List<ChatMessageBean> getAllChatMsg() {
        return allChatMsg;
    }

    public TreeMap<Integer, Boolean> getUnTransMsgPositions() {
        return unTransMsgPositions;
    }

    public void setUnTransMsgPositions(TreeMap<Integer, Boolean> unTransMsgPositions) {
        this.unTransMsgPositions = unTransMsgPositions;
    }

    public int getTranslateWhich() {
        int size = unTransMsgPositions.size();
        if(size>0){
            translateWhich = unTransMsgPositions.firstKey();
            return translateWhich;
        }else{
            translateWhich = -1;
        }
        return translateWhich;
    }

    public void setTranslateWhich(int translateWhich) {
        this.translateWhich = translateWhich;
    }

    public ChatFragmentRepository() {
        chatMsgDbManager = new ChatMsgDbManager();
        recentContactsDbManager = new RecentContactsDbManager();
    }

    public void registerChatCallBack(ChatCallBack chatCallBack) {
        this.chatCallBack = chatCallBack;
        isChoiceMsgToTrans = new HashMap<>();
    }

    public void sentTranslationChat(final ChatMessageBean chatMessageBean) {
        MqttManager.get().sendMessage(chatMessageBean);
        if (!chatMessageBean.getIsPrivateMessage()) {
            isChoiceMsgToTrans.clear();
        }
    }

    public ChatMessageBean buildSendPrivateMsg(int seconds, String fileUrl, ChatMessageBean msg, int type,String content) {
        ChatMessageBean chatMessageBean = new ChatMessageBean();
        switch (type) {
            case ChatConstants.TXT_PRIVATE:
                chatMessageBean.setSendMsgType(ChatConstants.TXT_PRIVATE);
                break;
            case ChatConstants.VOICE_PRIVATE:
                chatMessageBean.setTranslateVoiceLong(seconds);
                String filePath = FileManager.getFileManager(AppManager.get().getApplication()).saveChatVoice(msg.getMsgId(), fileUrl);
                chatMessageBean.setTranslateFilePath(filePath);
                chatMessageBean.setTranslateFileUrl(fileUrl);
                chatMessageBean.setTranslationStatus(ChatConstants.UN_TRANSLATE_MSG);
                chatMessageBean.setSendMsgType(ChatConstants.VOICE_PRIVATE);
                break;
        }
        String msgId = UUID.randomUUID().toString();
        chatMessageBean.setOrderId(msg.getOrderId());
        chatMessageBean.setTranslateContent(content);
        chatMessageBean.setMsgId(msgId);
        chatMessageBean.setIsPrivateMsgFromMe(true);
        chatMessageBean.setIsPrivateMessage(true);
        chatMessageBean.setMsgIsTrans(true);
        chatMessageBean.setTranslationStatus(ChatConstants.UN_TRANSLATE_MSG);
        chatMessageBean.setFromId(msg.getFromId());
        chatMessageBean.setToName(msg.getToName());
        chatMessageBean.setFromName(msg.getFromName());
        chatMessageBean.setExtraUid(msg.getToId());
        chatMessageBean.setToId(msg.getToId());
        chatMessageBean.setTranslatorId(msg.getTranslatorId());
        chatMessageBean.setContent(content);
        chatMessageBean.setIsChoiceTranslate(false);
        chatMessageBean.setMsgTime(Long.valueOf(System.currentTimeMillis()).toString());
        insertOtherMsg(chatMessageBean);
        updateRecentMsg(chatMessageBean);
        return chatMessageBean;
    }

    public ChatMessageBean buildSendTranslateMsg(int seconds, String fileUrl, ChatMessageBean msg, int type) {
        ChatMessageBean chatMessageBean = msg;
        switch (type) {
            case ChatConstants.TXT_PRIVATE:
                msg.setTranslationStatus(ChatConstants.TRANSLATE_MSG);
                msg.setSendMsgType(ChatConstants.TXT_PRIVATE);
                break;
            case ChatConstants.VOICE_PRIVATE:
                msg.setSendMsgType(ChatConstants.VOICE_PRIVATE);
                msg.setTranslateVoiceLong(seconds);
                String msgId = msg.getMsgId();
                String filePath = FileManager.getFileManager(AppManager.get().getApplication()).saveChatVoice(msgId, fileUrl);
                msg.setTranslateFilePath(filePath);
                msg.setTranslateFileUrl(fileUrl);
                msg.setTranslationStatus(ChatConstants.TRANSLATE_MSG);
                break;
        }
        msg.setIsChoiceTranslate(false);
        msg.setMsgIsTrans(true);
        updateChatMsg(msg,chatMessageBean);
        return msg;
    }

    public ChatMessageBean getChatMessageBean() {
        return chatMessageBean;
    }

    public void setChatMessageBean(ChatMessageBean chatMessageBean) {
        this.chatMessageBean = chatMessageBean;
    }

    public void requestUploadGeneral(final ChatMessageBean chatMessageBean,boolean isCache) {
        if (!chatMessageBean.getIsPrivateMessage()) {
            isChoiceMsgToTrans.clear();
        }
        String fileName = chatMessageBean.getMsgId() + FileConstants.FILE_SUFFIX_AMR;
        HttpUtils.requestUploadGeneral(chatMessageBean.getTranslateFileUrl(), fileName, isCache, new Observer<HttpResultBean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(HttpResultBean httpResultBean) {
                chatMessageBean.setTranslateFileUrl(httpResultBean.getObj_id());
                MqttManager.get().sendMessage(chatMessageBean);
                insertMsg(chatMessageBean);
                chatCallBack.upLoadFileSuccess(chatMessageBean);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("onError-->", "requestUploadGeneral="+e.toString());
            }

            @Override
            public void onComplete() {
            }
        });
    }

    public Observable<ChatMessageBean> insertMsg(final ChatMessageBean cmb) {
        return Observable.create(new ObservableOnSubscribe<ChatMessageBean>() {
            @Override
            public void subscribe(ObservableEmitter<ChatMessageBean> e) throws Exception {
                boolean result = chatMsgDbManager.insert(cmb);
                Log.e("insertMsg", result + "");
                if (isChoiceMsgToTrans.size() == 0&&(cmb.getType()==0||cmb.getType()==2)) {
                    cmb.setIsChoiceTranslate(true);
                    isChoiceMsgToTrans.put(cmb.getMsgId(), true);
                }
                e.onNext(cmb);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public void insertOtherMsg(final ChatMessageBean cmb) {
        Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> e) throws Exception {
                boolean result = chatMsgDbManager.insert(cmb);
            }
        }).subscribeOn(Schedulers.newThread()).subscribe();
    }

    public void updateChatMsg(final ChatMessageBean chatMsg, final ChatMessageBean chatMsgOld) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                chatMsgDbManager.update(chatMsg);
                int unTransNumber = chatMsgDbManager.queryUnTranslateMsgNum(chatMsg.getFromId(), chatMsg.getToId());
                recentContactsDbManager.updateUnTranslateMsgNumber(chatMsgOld,unTransNumber,true);
                e.onNext(unTransNumber);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                if(!chatMsgOld.getIsReTrans()){
                    MqttChatManager.get().responseMsg(chatMsgOld);
                }
            }
        });
    }

    public void updateRecentMsg(final ChatMessageBean chatMsg){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                int unTransNumber = chatMsgDbManager.queryUnTranslateMsgNum(chatMsg.getFromId(), chatMsg.getToId());
                recentContactsDbManager.updateUnTranslateMsgNumber(chatMsg,unTransNumber,true);
                e.onNext(22);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                MqttChatManager.get().responseMsg(chatMsg);
            }
        });
    }

    public void updateChatMsg(final ChatMessageBean chatMsg) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                chatMsgDbManager.update(chatMsg);
            }
        }).subscribeOn(Schedulers.newThread()).subscribe();
    }

    public Observable<List<ChatMessageBean>> queryChatMsg(final int number, final int currentPage, final String fromId, final String toId, final String index) {
        return Observable.create(new ObservableOnSubscribe<List<ChatMessageBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChatMessageBean>> e) throws Exception {
                WhereCondition whereCondition = chatMsgDbManager.getQueryBuilder().or(
                        ChatMessageBeanDao.Properties.FromId.eq(fromId),
                        ChatMessageBeanDao.Properties.FromId.eq(toId));
                WhereCondition whereCondition2 = chatMsgDbManager.getQueryBuilder().or(
                        ChatMessageBeanDao.Properties.ToId.eq(fromId),
                        ChatMessageBeanDao.Properties.ToId.eq(toId));
                long page = chatMsgDbManager.getPages(number, whereCondition, whereCondition2);
                if (currentPage <= page) {
                    Property property = ChatMessageBeanDao.Properties.MsgTime;
                    List<ChatMessageBean> lists = chatMsgDbManager.loadPages(currentPage, number, whereCondition, property, whereCondition2);
                    Collections.reverse(lists);
                    lists.addAll(allChatMsg);
                    allChatMsg.clear();
                    allChatMsg.addAll(lists);
                    unTransMsgPositions.clear();
                    for (int i = 0; i < allChatMsg.size(); i++) {
                        ChatMessageBean chatMsg = allChatMsg.get(i);
                        String msgId = chatMsg.getMsgId();
                        if (null!=chatMsg&&null!=msgId&&msgId.length() > 0 && !chatMsg.getIsPrivateMessage() && !chatMsg.getMsgIsTrans()
                                && chatMsg.getType() != ChatConstants.COMMON_PRODUCT_CHAT
                                &&(chatMsg.getType()==0||chatMsg.getType()==2)) {
                            if(unTransMsgPositions.size()==0){
                                unTransMsgPositions.put(i,true);
                                chatMsg.setIsChoiceTranslate(true);
                            }else{
                                unTransMsgPositions.put(i,false);
                                chatMsg.setIsChoiceTranslate(false);
                            }
                        }
                        if(null!=msgId&&chatMsg.getMsgId().length() > 0){
                            chatMsgIndex.put(chatMsg.getMsgId(),i);
                        }
                    }
                    e.onNext(allChatMsg);
                } else {
                    List<ChatMessageBean> lists = new ArrayList<>();
                    e.onNext(lists);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public List<ChatMessageBean> changeChoiceItem(List<ChatMessageBean> list, String msgId) {
        for (ChatMessageBean chatMsg : list) {
            if (!chatMsg.getIsPrivateMessage() && !chatMsg.getMsgIsTrans()) {
                if (msgId.equals(chatMsg.getMsgId())) {
                    chatMsg.setIsChoiceTranslate(true);
                    isChoiceMsgToTrans.clear();
                    isChoiceMsgToTrans.put(chatMsg.getMsgId(), true);
                } else {
                    chatMsg.setIsChoiceTranslate(false);
                }
            }
        }
        return list;
    }

    public void cleanAllChatMsg(){
        allChatMsg.clear();
    }

    public Observable<Integer> getAllMsgByClientId(final String fromId, final String toId, final String msgId, final String orderId) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                List<ChatMessageBean> allMsg = chatMsgDbManager.getAllMsgByClientId(fromId, toId, orderId);
                if(null==chatMsgProperties){
                    chatMsgProperties = new ArrayList<>();
                }else{
                    chatMsgProperties.clear();
                }
                int pos = 0;
                for (int i = 0; i < allMsg.size(); i++) {
                    ChatMessageBean chatMsg = allMsg.get(i);
                    ChatMsgProperty chatMsgProperty = new ChatMsgProperty();
                    chatMsgProperty.setFilePath(chatMsg.getFilePath());
                    chatMsgProperty.setFileUrl(chatMsg.getFileUrl());
                    chatMsgProperty.setVideoThumbnailPath(chatMsg.getVideoThumbnailPath());
                    switch (chatMsg.getType()) {
                        case 1:
                        case 11:
                            chatMsgProperty.setType(1);
                            String chatMsgId = chatMsg.getMsgId();
                            String filePath = chatMsg.getFilePath();
                            if (null != filePath) {
                                if (chatMsgId.equals(msgId)) {
                                    pos = chatMsgProperties.size();
                                }
                                chatMsgProperties.add(chatMsgProperty);
                            }
                            break;
                        case 4:
                        case 5:
                        case 14:
                            chatMsgId = chatMsg.getMsgId();
                            filePath = chatMsg.getFilePath();
                            chatMsgProperty.setType(2);
                            if (null != filePath) {
                                if (chatMsgId.equals(msgId)) {
                                    pos = chatMsgProperties.size();
                                }
                                chatMsgProperties.add(chatMsgProperty);
                            }
                            break;
                    }
                }
                e.onNext(pos);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Integer> getProductMsg(final String fromId, final String toId,final String msgId){
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                List<ChatMessageBean> allMsg = chatMsgDbManager.getProductMsgByClientId(fromId, toId,6,null);
                int pos = 0;
                if(null==pLists){
                    pLists = new ArrayList<>();
                }else{
                    pLists.clear();
                }
                for(ChatMessageBean chatMessageBean : allMsg){
                    List<ProductDetail> productDetails = new ArrayList<>();
                    String extraInfo = chatMessageBean.getExtraInfo();
                    Gson gson = new Gson();
                    ProductExtraInfo productExtraInfo = gson.fromJson(extraInfo, ProductExtraInfo.class);
                    List<ProductExtraInfo.DetailsBean> detailsBeanList = productExtraInfo.getDetails();
                    List<ProductDetail> detailLists = new ArrayList<>();
                    for(int i=0;i<detailsBeanList.size();i++){
                        ProductExtraInfo.DetailsBean detailsBean = detailsBeanList.get(i);
                        int type = detailsBean.getDetail_type();
                        String mediaId;
                        int width;
                        int height;
                        if(type==1){
                            ProductExtraInfo.DetailsBean.PictureBean pictureBean = detailsBean.getPicture();
                            mediaId = pictureBean.getPicture_id();
                            width = pictureBean.getWidth();
                            height = pictureBean.getHeight();
                        }else{
                            ProductExtraInfo.DetailsBean.MediaBean mediaBean = detailsBean.getMedia();
                            mediaId = mediaBean.getMedia_id();
                            width = mediaBean.getWidth();
                            height = mediaBean.getHeight();
                        }
                        int productDetailType;
                        if(detailLists.size()==0){
                            productDetailType = ProductDetail.TYPE_IMG_COVER;
                        }else{
                            productDetailType = ProductDetail.TYPE_IMG_DETAIL;
                        }
                        ProductDetail productDetail = new ProductDetail(mediaId,productDetailType,null,type,null,productExtraInfo.getName()
                                ,productExtraInfo.getCurrency_id(),width,height,productExtraInfo.getPrice());
                        detailLists.add(productDetail);
                    }
                    List<ProductDetail> propertiesLists = new ArrayList<>();
                    List<ProductExtraInfo.PropertiesBean> propertiesBeanList = productExtraInfo.getProperties();
                    if(null!=propertiesBeanList){
                        for(int i=0;i<propertiesBeanList.size();i++){
                            ProductDetail productDetail = new ProductDetail(null,ProductDetail.TYPE_PROPERTIES,null,0,propertiesBeanList.get(i),null
                                    ,0,0,0,null);
                            propertiesLists.add(productDetail);
                        }
                    }
                    ProductDetail productDesc = new ProductDetail(productExtraInfo.getDescription(),ProductDetail.TYPE_IMG_DESCRIPTION,null,0,null,null
                            ,0,0,0,null);
                    productDetails.add(detailLists.get(0));
                    productDetails.addAll(propertiesLists);
                    productDetails.add(productDesc);
                    for(int i=0;i<detailLists.size();i++){
                        if(i>0){
                            productDetails.add(detailLists.get(i));
                        }
                    }
                    pLists.add(productDetails);
                    if(msgId.equals(chatMessageBean.getMsgId())){
                        pos = pLists.size();
                    }
                }
                e.onNext(pos);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }
}
