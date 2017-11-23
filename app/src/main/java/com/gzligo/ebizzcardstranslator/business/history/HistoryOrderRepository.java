package com.gzligo.ebizzcardstranslator.business.history;

import com.google.gson.Gson;
import com.gzligo.ebizzcardstranslator.base.mvp.IModel;
import com.gzligo.ebizzcardstranslator.db.manager.ChatMsgDbManager;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.ChatMsgProperty;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;
import com.gzligo.ebizzcardstranslator.persistence.ProductExtraInfo;

import java.util.ArrayList;
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
 * Created by Lwd on 2017/5/27.
 */

public class HistoryOrderRepository implements IModel {
    private ChatMsgDbManager chatMsgDbManager;
    private List<ChatMsgProperty> chatMsgProperties;
    private List<List<ProductDetail>> pLists;

    public List<List<ProductDetail>> getProductDetails() {
        return pLists;
    }

    public List<ChatMsgProperty> getChatMsgProperties() {
        return chatMsgProperties;
    }

    public HistoryOrderRepository() {
        chatMsgDbManager = new ChatMsgDbManager();
    }

    public static void requestTranslatorOrderList(String until, String since, String count, boolean isCache, Observer observer) {
        HttpUtils.requestTranslatorOrderList(until, since, count, isCache, observer);
    }

    public Observable<List<ChatMessageBean>> getChatHisToryMsg(final String fromId, final String toId, final String orderId) {
        return Observable.create(new ObservableOnSubscribe<List<ChatMessageBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChatMessageBean>> emitter) throws Exception {
                List<ChatMessageBean> lists = chatMsgDbManager.getChatHisToryMsg(fromId, toId, orderId);
                emitter.onNext(lists);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public TreeMap<Integer, LanguagesBean> getTreeMap() {
        return CommonBeanManager.getInstance().getTreeMap();
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

    public Observable<Integer> getProductMsg(final String fromId, final String toId, final String msgId, final String orderId){
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                List<ChatMessageBean> allMsg = chatMsgDbManager.getProductMsgByClientId(fromId, toId,6,orderId);
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
