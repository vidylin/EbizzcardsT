package com.gzligo.ebizzcardstranslator.mqtt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.business.chat.ChatActivityCallBack;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.constants.FileConstants;
import com.gzligo.ebizzcardstranslator.db.manager.ChatMsgDbManager;
import com.gzligo.ebizzcardstranslator.db.manager.RecentContactsDbManager;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.mqtt.callback.MqttChatCallBack;
import com.gzligo.ebizzcardstranslator.mqtt.callback.RecentContactsCallBack;
import com.gzligo.ebizzcardstranslator.mqtt.protobuf.MQTTProtobufMsg;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.notification.NotificationManager;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.HisirUserAndEvents;
import com.gzligo.ebizzcardstranslator.persistence.HisirUserInfoBean;
import com.gzligo.ebizzcardstranslator.persistence.ProductBean;
import com.gzligo.ebizzcardstranslator.persistence.ProductVideoThumbnailBean;
import com.gzligo.ebizzcardstranslator.persistence.TranslationChatResultBean;
import com.gzligo.ebizzcardstranslator.utils.CommonUtils;
import com.gzligo.ebizzcardstranslator.utils.FileManager;
import com.gzligo.ebizzcardstranslator.utils.FileUtils;
import com.gzligo.ebizzcardstranslator.utils.SDFileHelper;

import org.json.JSONException;

import java.io.File;
import java.util.List;

import greendao.autogen.bean.ChatMessageBeanDao;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.gzligo.ebizzcardstranslator.constants.FileConstants.FILE_SUFFIX_MP4;

/**
 * Created by Lwd on 2017/6/7.
 */

public class MqttChatManager {
    private MqttChatCallBack mqttChatCallBack;
    private RecentContactsCallBack recentContactsCallBack;
    private ChatActivityCallBack chatActivityCallBack;
    private ChatMsgDbManager chatMsgDbManager;
    private RecentContactsDbManager recentContactsDbManager;
    private Gson gson;
    private MqttChatManager(){
        gson = new Gson();
        chatMsgDbManager = new ChatMsgDbManager();
        recentContactsDbManager = new RecentContactsDbManager();
    }

    private static class Singleton {
        private static MqttChatManager sInstance = new MqttChatManager();
    }

    public static MqttChatManager get() {
        return Singleton.sInstance;
    }

    public void registerMqttChatManagerCallBack(MqttChatCallBack mqttChatCallBack){
        this.mqttChatCallBack = mqttChatCallBack;
    }

    public void registerChatActivityCallBack(ChatActivityCallBack chatActivityCallBack){
        this.chatActivityCallBack = chatActivityCallBack;
    }

    public void registerRecentContactsCallBack(RecentContactsCallBack recentContactsCallBack){
        this.recentContactsCallBack = recentContactsCallBack;
    }

    public void handlerChatMsg(MQTTProtobufMsg.Msg chatMsg) {
        if(chatMsg.hasCommonChat()){
            MQTTProtobufMsg.CommonChat commonChat = chatMsg.getCommonChat();
            if (MqttDupMsgUtils.getInstance().duplicate("chat", commonChat.getMsgId())) {
                return;
            }
            try {
                ChatMessageBean chatMessageBean = MQTTMsgUtils.parseCommonMsg(commonChat,gson);
                MqttManager.get().sendClientReceived(chatMessageBean.getMsgId(), chatMessageBean.getFromId(), MQTTProtobufMsg.LGMsgStateType.RECEIVED);
                downLoadingFile(chatMessageBean,AppManager.get().getApplication(),true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(chatMsg.hasPrivateChat()){
            MQTTProtobufMsg.PrivateChat privateChat = chatMsg.getPrivateChat();
            if (MqttDupMsgUtils.getInstance().duplicate("chat", privateChat.getMsgId())) {
                return;
            }
            try {
                ChatMessageBean chatMessageBean = MQTTMsgUtils.parsePrivateChatMsg(AppManager.get().getApplication(),privateChat,gson);
                MqttManager.get().sendClientReceived(chatMessageBean.getMsgId(), chatMessageBean.getFromId(), MQTTProtobufMsg.LGMsgStateType.RECEIVED);
                downLoadingFile(chatMessageBean,AppManager.get().getApplication(),false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void downLoadingFile(final ChatMessageBean chatMessage, Context context,boolean isCommonMsg){
        FileManager fileManager = FileManager.getFileManager(context);
        String url = "";
        String fileDir = null;
        final String hdKey = chatMessage.getHdKey();
        String fileName = null;
        String notificationMsg = "";
        switch (chatMessage.getType()){
            case MQTTProtobufMsg.LGContentType.TEXT_VALUE:
                insertOtherMsg(chatMessage);
                notificationMsg = chatMessage.getFromName()+(isCommonMsg?"":context.getResources().getString(R.string.at_you))
                        +":"+chatMessage.getContent();
                break;
            case MQTTProtobufMsg.LGContentType.IMAGE_VALUE:
                //图片
                fileName = chatMessage.getMsgId()+chatMessage.getFromId() + FileConstants.FILE_SUFFIX_JPG;
                url = CommonUtils.formatMediaUrl(chatMessage.getMediumObjID());
                fileDir = fileManager.getChatDirImage().getAbsolutePath();
                notificationMsg = chatMessage.getFromName()+(isCommonMsg?"":context.getResources().getString(R.string.at_you))
                        +(isCommonMsg?context.getResources().getString(R.string.chat_picture)
                        :context.getResources().getString(R.string.chat_msg_image));
                chatMessage.setFileUrl(url);
                break;
            case MQTTProtobufMsg.LGContentType.VOICE_VALUE:
                //语音
                fileName = chatMessage.getMsgId()+chatMessage.getFromId() + FileConstants.FILE_SUFFIX_AMR;
                url = CommonUtils.formatMediaUrl(chatMessage.getFileUrl());
                fileDir = fileManager.getChatDirVoice().getAbsolutePath();
                notificationMsg = chatMessage.getFromName()+(isCommonMsg?"":context.getResources().getString(R.string.at_you))
                        +(isCommonMsg?context.getResources().getString(R.string.chat_voice)
                        :context.getResources().getString(R.string.chat_msg_voice));
                chatMessage.setFileUrl(url);
                break;
            case MQTTProtobufMsg.LGContentType.FILE_VALUE:
                break;
            case MQTTProtobufMsg.LGContentType.SHORT_VIDEO_VALUE:
            case MQTTProtobufMsg.LGContentType.VIDEO_VALUE:
                //视频
                fileName = chatMessage.getMsgId()+chatMessage.getFromId() + FILE_SUFFIX_MP4;
                url = CommonUtils.formatMediaUrl(chatMessage.getFileUrl());
                fileDir = fileManager.getChatDirVideo().getAbsolutePath();
                notificationMsg = chatMessage.getFromName()+(isCommonMsg?"":context.getResources().getString(R.string.at_you))
                        +(isCommonMsg?context.getResources().getString(R.string.chat_video)
                        :context.getResources().getString(R.string.chat_msg_micro_video));
                chatMessage.setFileUrl(url);
                break;
            case MQTTProtobufMsg.LGContentType.PRODUCT_VALUE://product
                Gson gson = new Gson();
                ProductBean productBean = gson.fromJson(chatMessage.getExtraInfo(), ProductBean.class);

                chatMessage.setTranslateContent(productBean.getName());
                List<ProductBean.DetailsBean> detailsBeanList = productBean.getDetails();
                ProductBean.DetailsBean.PictureBean pictureBean = detailsBeanList.get(0).getPicture();
                ProductBean.DetailsBean.MediaBean mediaBean = detailsBeanList.get(0).getMedia();
                if(null!=pictureBean){
                    fileName = chatMessage.getMsgId()+chatMessage.getFromId() + FileConstants.FILE_SUFFIX_JPG;
                    url = CommonUtils.formatMediaUrl(productBean.getDetails().get(0).getPicture().getPicture_id());
                }else if(null!=mediaBean){
                    fileName = chatMessage.getMsgId()+chatMessage.getFromId() + FileConstants.FILE_SUFFIX_MP4;
                    url = CommonUtils.formatMediaUrl(mediaBean.getMedia_id());
                }
                fileDir = fileManager.getChatDirImage().getAbsolutePath();
                notificationMsg = chatMessage.getFromName()+(isCommonMsg?"":context.getResources().getString(R.string.at_you))
                        +(isCommonMsg?context.getResources().getString(R.string.chat_product)
                        :context.getResources().getString(R.string.chat_msg_product));
                chatMessage.setFileUrl(url);
                break;
            default:
                //other
                url = CommonUtils.formatMediaUrl(chatMessage.getMediumObjID());
                fileDir = fileManager.getChatDirVoice().getAbsolutePath();
                chatMessage.setFileUrl(url);
                break;
        }
        sendNotification(notificationMsg,chatMessage.getFromId());
        if (TextUtils.isEmpty(url)) {
            return;
        }
        final String filePath = fileName;
        final String dirFilePath = fileDir;
        insertOtherMsg(chatMessage);
        if(null!=filePath){
            if(chatMessage.getType()==MQTTProtobufMsg.LGContentType.SHORT_VIDEO_VALUE
                    ||chatMessage.getType()==MQTTProtobufMsg.LGContentType.VIDEO_VALUE
                    ||(chatMessage.getType()==MQTTProtobufMsg.LGContentType.PRODUCT_VALUE&&url.endsWith(FILE_SUFFIX_MP4))){
                String videoPath = filePath.replace(FILE_SUFFIX_MP4,FileConstants.FILE_SUFFIX_JPG);
                getVideoThumbnailBean(url,dirFilePath,videoPath,chatMessage);
                if(chatMessage.getType()==MQTTProtobufMsg.LGContentType.PRODUCT_VALUE&&url.endsWith(FILE_SUFFIX_MP4)){
                    return;
                }
            }
            Log.e("--downLoadingFile--","开始下载文件......");
            final String finalUrl = url;
            HttpUtils.downLoadingFile(url,new Function<ResponseBody, ObservableSource<ProductVideoThumbnailBean>>() {
                @Override
                public ObservableSource<ProductVideoThumbnailBean> apply(ResponseBody responseBody) throws Exception {
                    Log.e("--downLoadingFile--","文件已下载完成.....");
                    File file = new SDFileHelper(dirFilePath,filePath).saveFile(responseBody,hdKey);
                    ProductVideoThumbnailBean productVideoThumbnailBean = new ProductVideoThumbnailBean();
                    if(chatMessage.getType()==MQTTProtobufMsg.LGContentType.PRODUCT_VALUE){
                        if(!finalUrl.endsWith(FILE_SUFFIX_MP4)){
                            Bitmap bitmap= BitmapFactory.decodeFile(file.getPath());
                            if(null!=bitmap){
                                productVideoThumbnailBean.setThumbnailSize(bitmap.getWidth()+"*"+bitmap.getHeight());
                            }
                        }
                    }else{
                        if(chatMessage.getType()!=MQTTProtobufMsg.LGContentType.SHORT_VIDEO_VALUE
                                &&chatMessage.getType()!=MQTTProtobufMsg.LGContentType.VIDEO_VALUE
                                &&MQTTProtobufMsg.LGContentType.VOICE_VALUE!=chatMessage.getType()){
                            Bitmap bitmap= BitmapFactory.decodeFile(file.getPath());
                            if(null!=bitmap){
                                productVideoThumbnailBean.setThumbnailSize(bitmap.getWidth()+"*"+bitmap.getHeight());
                            }
                        }
                    }
                    productVideoThumbnailBean.setThumbnailPath(file.getPath());
                    return Observable.just(productVideoThumbnailBean);
                }
            },new Consumer<ProductVideoThumbnailBean>() {
                @Override
                public void accept(final ProductVideoThumbnailBean p) throws Exception {
                    if(null!=p){
                        if(p.getThumbnailPath().endsWith(FILE_SUFFIX_MP4)){
                            Glide.with(AppManager.get().getApplication())
                                    .load(p.getThumbnailPath())
                                    .thumbnail(0.1f).into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                    BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                                    Bitmap bitmap = bitmapDrawable.getBitmap();
                                    int width = bitmap.getWidth();
                                    int height = bitmap.getHeight();
                                    chatMessage.setFilePath(p.getThumbnailPath());
                                    chatMessage.setImageSize(width+""+"*"+height);
                                    updateOtherMsg(chatMessage);
                                }
                            });
                        }else{
                            chatMessage.setFilePath(p.getThumbnailPath());
                            chatMessage.setImageSize(p.getThumbnailSize());
                            updateOtherMsg(chatMessage);
                        }
                    }
                }
            });
        }
    }

    //获取产品为视频格式的缩略图
    private void getVideoThumbnailBean(final String url, final String dirFilePath, final String destFileName, final ChatMessageBean chatMessage){
        HttpUtils.requestVideoThumbnail(url, new SimpleTarget() {
            @Override
            public void onResourceReady(final Object resource, Transition transition) {
                Observable.create(new ObservableOnSubscribe<ChatMessageBean>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<ChatMessageBean> e) throws Exception {
                        Bitmap bitmap = (Bitmap) resource;
                        if (null != bitmap) {
                            String filePath = FileUtils.saveBitmap(dirFilePath, destFileName, bitmap);
                            if (!TextUtils.isEmpty(filePath)) {
                                if (chatMessage.getType() == MQTTProtobufMsg.LGContentType.PRODUCT_VALUE) {
                                    chatMessage.setFilePath(filePath);
                                } else {
                                    chatMessage.setVideoThumbnailPath(filePath);
                                }
                                chatMessage.setImageSize(bitmap.getWidth() + "*" + bitmap.getHeight());
                            }
                        }
                        e.onNext(chatMessage);
                        e.onComplete();
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ChatMessageBean>() {
                    @Override
                    public void accept(@NonNull ChatMessageBean chatMessageBean) throws Exception {
                        if (!TextUtils.isEmpty(chatMessageBean.getVideoThumbnailPath()) || !TextUtils.isEmpty(chatMessageBean.getFilePath())) {
                            updateOtherMsg(chatMessageBean);
                        }
                    }
                });

            }
        });
    }

    public void updateOtherMsg(final ChatMessageBean cmb){
        Observable.create(new ObservableOnSubscribe<ChatMessageBean>() {
            @Override
            public void subscribe(ObservableEmitter<ChatMessageBean> e) throws Exception {
                ChatMessageBean chatMessageBean = chatMsgDbManager.getQueryBuilder().where(ChatMessageBeanDao.Properties.MsgId.eq(cmb.getMsgId())).limit(1).unique();
                chatMessageBean.setFilePath(cmb.getFilePath());
                chatMessageBean.setImageSize(cmb.getImageSize());
                chatMessageBean.setVideoThumbnailPath(cmb.getVideoThumbnailPath());
                chatMsgDbManager.update(chatMessageBean);
                e.onNext(chatMessageBean);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ChatMessageBean>() {
            @Override
            public void accept(@NonNull ChatMessageBean chatMsg) throws Exception {
                if(null!=mqttChatCallBack){
                    mqttChatCallBack.handlerChatMsg(chatMsg);
                }
            }
        });
    }

    public void insertOtherMsg(final ChatMessageBean cmb){
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                boolean result = false;
                if(cmb.getIsPrivateMessage()){
                    String orderId = chatMsgDbManager.getLastMsg(cmb);
                    cmb.setOrderId(orderId);
                    chatMsgDbManager.insert(cmb);
                }else{
                    if(TextUtils.isEmpty(cmb.getOrderId())){
                        String orderId = chatMsgDbManager.getLastMsg(cmb);
                        cmb.setOrderId(orderId);
                    }
                    chatMsgDbManager.insert(cmb);
                }
                int unTransNumber = 0;
                if (!cmb.getIsPrivateMessage() && cmb.getMsgId().length() > 0 && cmb.getType() != MQTTProtobufMsg.LGContentType.PRODUCT_VALUE
                        && cmb.getType() != ChatConstants.COMMON_VIDEO) {
                    unTransNumber = chatMsgDbManager.queryUnTranslateMsgNum(cmb.getFromId(), cmb.getToId());
                    Log.e("unTransNumber=",unTransNumber+"");
                }
                recentContactsDbManager.updateUnTranslateMsgNumber(cmb,unTransNumber,false);
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aVoid) throws Exception {
                if(null!=mqttChatCallBack){
                    mqttChatCallBack.handlerChatMsg(cmb);
                }
                if(null!=recentContactsCallBack){
                    recentContactsCallBack.handlerRecentContactsMsg(cmb);
                }
                if(null!=chatActivityCallBack){
                    chatActivityCallBack.getUnTranslateMsg(cmb);
                }
            }
        });
    }

    public void handlerChatResult(TranslationChatResultBean translationChatResultBean){
        if(mqttChatCallBack!=null){
            mqttChatCallBack.handlerChatResultMsg(translationChatResultBean);
        }
    }

    public void endTranslateMsg(ChatMessageBean selectedBean){
        if(mqttChatCallBack!=null){
            mqttChatCallBack.endTranslateMsg(selectedBean);
        }
    }

    public void responseMsg(ChatMessageBean chatMsg){
        if(null!=recentContactsCallBack){
            recentContactsCallBack.handlerRecentContactsMsg(chatMsg);
        }
        if(null!=chatActivityCallBack){
            chatActivityCallBack.getUnTranslateMsg(chatMsg);
        }
    }

    private void sendNotification(String msg,String fromId){
        List l = AppManager.get().getActivities();
        if(msg.length()>0&&!CommonBeanManager.getInstance().isChatting()){
            CommonBeanManager.getInstance().setUnReadMsgMap(fromId);
            NotificationManager.getInstance().notificationNewMessage(msg,1);
        }
    }

    //获取hisir用户信息
    public void requestHisirUserInfo(final String hisirUserIdOne, String hisirUserIdTwo, final ChatMessageBean chatMsg){
        HttpUtils.requestZipHisirUserInfo(hisirUserIdOne, hisirUserIdTwo, new BiFunction<HisirUserInfoBean,HisirUserInfoBean,HisirUserAndEvents>() {
            @Override
            public HisirUserAndEvents apply(@NonNull HisirUserInfoBean hisirUserInfoBean1, @NonNull HisirUserInfoBean hisirUserInfoBean2) throws Exception {
                HisirUserAndEvents hisirUserAndEvents = new HisirUserAndEvents(hisirUserInfoBean1,hisirUserInfoBean2);
                String fromId = chatMsg.getFromId();
                String fromPortrait;
                String toPortrait;
                if(hisirUserIdOne.equals(fromId)){
                    fromPortrait = hisirUserInfoBean1.getData().getPortrait();
                    toPortrait = hisirUserInfoBean2.getData().getPortrait();
                }else{
                    fromPortrait = hisirUserInfoBean2.getData().getPortrait();
                    toPortrait = hisirUserInfoBean1.getData().getPortrait();
                }
                recentContactsDbManager.insertTranslatorSelected(chatMsg,fromPortrait,toPortrait);
                return hisirUserAndEvents;
            }
        }).subscribe(new Consumer<HisirUserAndEvents>() {
            @Override
            public void accept(@NonNull HisirUserAndEvents hisirUserAndEvents) throws Exception {
                Log.e("hisirUserAndEvents","-------->"+hisirUserAndEvents);
            }
        });
    }

    private void isExitTransUsers(final String fromId, final String toId){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                recentContactsDbManager.getRecentContact(fromId,toId);
            }
        });
    }
}
