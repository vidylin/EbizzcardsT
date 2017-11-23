package com.gzligo.ebizzcardstranslator.mqtt;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.protobuf.UnknownFieldSet;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.constants.FileConstants;
import com.gzligo.ebizzcardstranslator.mqtt.protobuf.MQTTProtobufMsg;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.MessageContentFile;
import com.gzligo.ebizzcardstranslator.persistence.MessageContentImage;
import com.gzligo.ebizzcardstranslator.persistence.MessageContentText;
import com.gzligo.ebizzcardstranslator.persistence.MessageContentVideo;
import com.gzligo.ebizzcardstranslator.persistence.MessageContentVoice;
import com.gzligo.ebizzcardstranslator.utils.CommonUtils;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Lwd on 2017/6/7.
 */

public class MQTTMsgUtils {

    public static String buildJason(int type , ChatMessageBean chatMsg) {
        Gson gson = new Gson();
        String jsonString = "";
        switch (type) {
            case ChatConstants.TXT_PRIVATE:
                MessageContentText contentText = new MessageContentText();
                contentText.setBody(chatMsg.getTranslateContent());
                contentText.setPrivateToNickname(chatMsg.getFromName());
                jsonString = gson.toJson(contentText);
                break;
            case ChatConstants.VOICE_PRIVATE:
                MessageContentVoice contentVoice = new MessageContentVoice();
                contentVoice.setDuration(String.valueOf(chatMsg.getTranslateVoiceLong()));
                contentVoice.setPrivateToNickname(chatMsg.getFromName());
                contentVoice.setFileName(chatMsg.getMsgId() + FileConstants.FILE_SUFFIX_AMR);
                contentVoice.setUrl(chatMsg.getTranslateFileUrl());
                jsonString = gson.toJson(contentVoice);
                break;
        }
        return jsonString;
    }

    public static ChatMessageBean parseCommonMsg(MQTTProtobufMsg.CommonChat commonChat, Gson gson) throws JSONException {
        ChatMessageBean msg = new ChatMessageBean();
        msg.setMsgId(commonChat.getMsgId());
        MQTTProtobufMsg.LGContentType type = commonChat.getType();
        String msgContent = "";
        String translator = commonChat.getTranslator();
        int contentType = 0;
        msg.setIsPrivateMessage(false);
        UnknownFieldSet unknownFieldSet = commonChat.getUnknownFields();
        if (unknownFieldSet != null && unknownFieldSet.hasField(9)) {
            Map<Integer,UnknownFieldSet.Field> field = unknownFieldSet.asMap();
            if (field != null&&field.size()>0) {
                byte[] bytes = field.get(9).getLengthDelimitedList().get(0).toByteArray();
                msg.setOrderId(new String(bytes));
            }
        }
        switch (type) {
            case TEXT:
                MessageContentText contentText = gson.fromJson(commonChat.getContent(), MessageContentText.class);
                msgContent = contentText.getBody();
                contentType = MQTTProtobufMsg.LGContentType.TEXT_VALUE;
                msg.setTranslationStatus(0);
                msg.setTranslateType(ChatConstants.COMMON_TXT_CHAT);
                break;
            case IMAGE:
                msgContent = AppManager.get().getApplication().getString(R.string.chat_msg_image);
                contentType = MQTTProtobufMsg.LGContentType.IMAGE_VALUE;
                MessageContentImage contentImage = gson.fromJson(commonChat.getContent(), MessageContentImage.class);
                msg.setOriginalObjID(CommonUtils.formatMediaUrl(contentImage.getOriginalObjId()));
                msg.setImageSize(contentImage.getWidth() + "*" + contentImage.getHeight());
                if (contentImage.getIsPublicImage()){
                    msg.setTempStr(String.valueOf(contentImage.getIsPublicImage()));
                    msg.setMediumObjID(msg.getOriginalObjID());
                    msg.setThumbnailObjID(msg.getOriginalObjID());
                }else{
                    msg.setMediumObjID(CommonUtils.formatMediaUrl(contentImage.getMediumObjId()));
                    msg.setThumbnailObjID(CommonUtils.formatMediaUrl(contentImage.getThumbnailObjId()));
                    msg.setHdKey(contentImage.getHdKey());
                }
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("mediumObjId", contentImage.getOriginalObjId());
                    jsonObject.put("originalObjId", contentImage.getOriginalObjId());
                    jsonObject.put("thumbnailObjId", contentImage.getThumbnailObjId());
                    msg.setResponse(jsonObject.toString());
                }catch (Exception e) {
                }
                if (!TextUtils.isEmpty(contentImage.getDescriptions())){
                    msg.setMsgDescription(contentImage.getDescriptions());
                }
                msg.setTranslationStatus(0);
                msg.setTranslateType(ChatConstants.COMMON_IMG_CHAT);
                break;
            case VOICE:
                msgContent = AppManager.get().getApplication().getString(R.string.chat_msg_voice);
//                msg.setTranslateVoicePath(jsonObject.getString(HisirConstants.HTTPS.PARAM_URL));
                contentType = MQTTProtobufMsg.LGContentType.VOICE_VALUE;
                MessageContentVoice contentVoice = gson.fromJson(commonChat.getContent(), MessageContentVoice.class);
                msg.setFileUrl(contentVoice.getUrl());
                String duration = contentVoice.getDuration();
                double dur = 0.0;
                try {
                    dur = Double.valueOf(duration);
                }catch (Exception e){
                    e.printStackTrace();
                    dur = 0.0;
                }
                msg.setVoiceLong((int)dur);
                msg.setTranslationStatus(0);
                msg.setTranslateType(ChatConstants.COMMON_VOICE_CHAT);
                break;
            case SHORT_VIDEO:
                msgContent = AppManager.get().getApplication().getString(R.string.chat_msg_micro_video);
                contentType = MQTTProtobufMsg.LGContentType.SHORT_VIDEO_VALUE;
                MessageContentVideo contentVideo = gson.fromJson(commonChat.getContent(), MessageContentVideo.class);
                msg.setFileUrl(CommonUtils.formatMediaUrl(contentVideo.getUrl()));
                msg.setVoiceLong((int) contentVideo.getDuration());
                msg.setImageSize(contentVideo.getLength());
                msg.setMsgDescription(contentVideo.getDescription());
                msg.setTranslationStatus(0);
                msg.setTranslateType(ChatConstants.COMMON_VIDEO_CHAT);
                break;
            case VIDEO:
                msgContent = AppManager.get().getApplication().getString(R.string.chat_msg_video);
                contentType = MQTTProtobufMsg.LGContentType.VIDEO_VALUE;
                contentVideo = gson.fromJson(commonChat.getContent(), MessageContentVideo.class);
                msg.setFileUrl(CommonUtils.formatMediaUrl(contentVideo.getUrl()));
                msg.setVoiceLong((int) contentVideo.getDuration());
                msg.setImageSize(contentVideo.getLength());
                msg.setMsgDescription(contentVideo.getDescription());
                msg.setTranslationStatus(1);
                msg.setTranslateType(ChatConstants.COMMON_VIDEO);
                break;
            case PRODUCT:
                msgContent = AppManager.get().getApplication().getString(R.string.chat_msg_product);
                contentType = MQTTProtobufMsg.LGContentType.PRODUCT_VALUE;
                msg.setExtraInfo(commonChat.getContent());
                msg.setTranslationStatus(1);
                msg.setTranslateType(ChatConstants.COMMON_PRODUCT_CHAT);
                break;
            case FILE:
                msgContent = AppManager.get().getApplication().getString(R.string.chat_msg_file);
                contentType = MQTTProtobufMsg.LGContentType.FILE_VALUE;
                MessageContentFile contentFile = gson.fromJson(commonChat.getContent(), MessageContentFile.class);
                msg.setFileUrl(contentFile.url);
                msg.setTranslationStatus(1);
                msg.setTranslateType(ChatConstants.COMMON_FILE_CHAT);
                break;
        }
        msg.setTranslatorId(translator);
        msg.setFromName(commonChat.getFromName());
        msg.setMsgStatus(MQTTProtobufMsg.LGMsgStateType.RECEIVED_VALUE);
        msg.setContent(msgContent);
        msg.setFromId(commonChat.getFrom());
        msg.setToId(commonChat.getTo());
        msg.setType(contentType);
        msg.setMsgTime(commonChat.getTime()*1000+"");
        commonChat.getUnknownFields();
        return msg;
    }

    public static ChatMessageBean parsePrivateChatMsg(Context context, MQTTProtobufMsg.PrivateChat privateChat, Gson gson)
            throws JSONException{
        ChatMessageBean msg = new ChatMessageBean();
        msg.setMsgId(privateChat.getMsgId());
        msg.setIsPrivateMessage(true);
        String msgContent = "";
        String description = "";
        String privateToNickName = "";
        MQTTProtobufMsg.LGContentType type = privateChat.getType();
        switch (type) {
            case TEXT:
                MessageContentText contentText = gson.fromJson(privateChat.getContent(), MessageContentText.class);
                msgContent = contentText.getBody();
                privateToNickName = contentText.getPrivateToNickname();
                msg.setTranslateType(ChatConstants.PRIVATE_TXT_CHAT);
                break;
            case IMAGE:
                msgContent = context.getString(R.string.chat_msg_image);
                MessageContentImage contentImage = gson.fromJson(privateChat.getContent(), MessageContentImage.class);
                privateToNickName = contentImage.getPrivateToNickname();
                msg.setOriginalObjID(CommonUtils.formatMediaUrl((contentImage.getOriginalObjId())));
                msg.setMediumObjID(CommonUtils.formatMediaUrl((contentImage.getMediumObjId())));
                msg.setThumbnailObjID(CommonUtils.formatMediaUrl((contentImage.getThumbnailObjId())));
                msg.setImageSize(contentImage.getWidth() + "*" + contentImage.getHeight());
                msg.setMsgDescription(contentImage.getDescriptions());
                msg.setHdKey(contentImage.getHdKey());
                msg.setTranslateType(ChatConstants.PRIVATE_IMG_CHAT);
                break;
            case VOICE:
                MessageContentVoice contentVoice = gson.fromJson(privateChat.getContent(), MessageContentVoice.class);
                privateToNickName = contentVoice.getPrivateToNickname();
                msg.setFileUrl(CommonUtils.formatMediaUrl(contentVoice.getUrl()));
                try {
                    msg.setVoiceLong(Integer.valueOf(contentVoice.getDuration()));
                }catch (Exception e){
                    float floatLong = Float.valueOf(contentVoice.getDuration());
                    msg.setVoiceLong((int) floatLong);
                }
                msgContent = context.getString(R.string.chat_msg_voice);
                msg.setTranslateType(ChatConstants.PRIVATE_VOICE_CHAT);
                break;
            case SHORT_VIDEO:
            case VIDEO:
                msgContent = context.getString(R.string.chat_msg_micro_video);
                MessageContentVideo contentVideo = gson.fromJson(privateChat.getContent(), MessageContentVideo.class);
                privateToNickName = contentVideo.getPrivateToNickname();
                msg.setFileUrl(CommonUtils.formatMediaUrl(contentVideo.getUrl()));
                msg.setVoiceLong((int) contentVideo.getDuration());
                msg.setImageSize(String.valueOf(contentVideo.getLength()));
                msg.setMsgDescription(description);
                msg.setTranslateType(ChatConstants.PRIVATE_VIDEO_CHAT);
                break;
        }
        msg.setPrivateToNickname(privateToNickName);
        msg.setFromId(privateChat.getFrom());
        msg.setToId(privateChat.getExtraUid());
        msg.setFromName(privateChat.getFromName());
        msg.setTranslatorId(privateChat.getTo());
        msg.setExtraUid(privateChat.getExtraUid());
        msg.setToName(privateToNickName);
        msg.setContent(msgContent);
        msg.setPrivateMsgFromID(privateChat.getFrom());
        msg.setMsgStatus(MQTTProtobufMsg.LGMsgStateType.RECEIVED_VALUE);
        msg.setType(type.getNumber());
        msg.setMsgTime(TimeUtils.getNowTime());
        return msg;
    }

}
