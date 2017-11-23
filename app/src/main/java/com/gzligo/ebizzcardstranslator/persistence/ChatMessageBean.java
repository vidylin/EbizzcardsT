package com.gzligo.ebizzcardstranslator.persistence;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Lwd on 2017/6/7.
 */
@Entity
public class ChatMessageBean extends BaseBean{
    private static final long serialVersionUID = -2090747495754525617L;
    @Id
    private Long id;
    @NotNull
    private String msgId;
    private String content;
    private String translateContent;
    private String fromName;
    @NotNull
    private String fromId;
    private String msgTime;
    @NotNull
    private String toId;
    private String toName;
    @NotNull
    private String translatorId;
    private String translateFileUrl;
    private String translateFilePath;
    private Integer translateVoiceLong =0;
    private String fileUrl;
    private String filePath;
    private String videoThumbnailPath;
    private Integer voiceLong =0;
    private String imageSize;
    private String msgDescription;
    private String originalObjID;
    private String tempStr;
    private String mediumObjID;
    private String thumbnailObjID;
    private String hdKey;
    private String response;
    private String privateToNickname;
    private Boolean isPrivateMessage =false;
    private String privateMsgFromID;
    private String extraUid;
    private Boolean isPrivateMsgFromMe = false;
    private Integer sendMsgType =0;
    private Boolean isChoiceTranslate = false;
    private Integer translationStatus =0;
    private Integer translateType =0;
    private Integer msgStatus =0;
    private Boolean msgIsTrans = false;
    private Boolean isReTrans = false;
    private Boolean isReadVoice = false;
    private Integer type =0;
    private Long translateTime;
    private String extraInfo;
    private String orderId="";
    @Generated(hash = 2042997652)
    public ChatMessageBean(Long id, @NotNull String msgId, String content,
            String translateContent, String fromName, @NotNull String fromId,
            String msgTime, @NotNull String toId, String toName,
            @NotNull String translatorId, String translateFileUrl,
            String translateFilePath, Integer translateVoiceLong, String fileUrl,
            String filePath, String videoThumbnailPath, Integer voiceLong,
            String imageSize, String msgDescription, String originalObjID,
            String tempStr, String mediumObjID, String thumbnailObjID, String hdKey,
            String response, String privateToNickname, Boolean isPrivateMessage,
            String privateMsgFromID, String extraUid, Boolean isPrivateMsgFromMe,
            Integer sendMsgType, Boolean isChoiceTranslate,
            Integer translationStatus, Integer translateType, Integer msgStatus,
            Boolean msgIsTrans, Boolean isReTrans, Boolean isReadVoice,
            Integer type, Long translateTime, String extraInfo, String orderId) {
        this.id = id;
        this.msgId = msgId;
        this.content = content;
        this.translateContent = translateContent;
        this.fromName = fromName;
        this.fromId = fromId;
        this.msgTime = msgTime;
        this.toId = toId;
        this.toName = toName;
        this.translatorId = translatorId;
        this.translateFileUrl = translateFileUrl;
        this.translateFilePath = translateFilePath;
        this.translateVoiceLong = translateVoiceLong;
        this.fileUrl = fileUrl;
        this.filePath = filePath;
        this.videoThumbnailPath = videoThumbnailPath;
        this.voiceLong = voiceLong;
        this.imageSize = imageSize;
        this.msgDescription = msgDescription;
        this.originalObjID = originalObjID;
        this.tempStr = tempStr;
        this.mediumObjID = mediumObjID;
        this.thumbnailObjID = thumbnailObjID;
        this.hdKey = hdKey;
        this.response = response;
        this.privateToNickname = privateToNickname;
        this.isPrivateMessage = isPrivateMessage;
        this.privateMsgFromID = privateMsgFromID;
        this.extraUid = extraUid;
        this.isPrivateMsgFromMe = isPrivateMsgFromMe;
        this.sendMsgType = sendMsgType;
        this.isChoiceTranslate = isChoiceTranslate;
        this.translationStatus = translationStatus;
        this.translateType = translateType;
        this.msgStatus = msgStatus;
        this.msgIsTrans = msgIsTrans;
        this.isReTrans = isReTrans;
        this.isReadVoice = isReadVoice;
        this.type = type;
        this.translateTime = translateTime;
        this.extraInfo = extraInfo;
        this.orderId = orderId;
    }
    @Generated(hash = 1557449535)
    public ChatMessageBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMsgId() {
        return this.msgId;
    }
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTranslateContent() {
        return this.translateContent;
    }
    public void setTranslateContent(String translateContent) {
        this.translateContent = translateContent;
    }
    public String getFromName() {
        return this.fromName;
    }
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
    public String getFromId() {
        return this.fromId;
    }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
    public String getMsgTime() {
        return this.msgTime;
    }
    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }
    public String getToId() {
        return this.toId;
    }
    public void setToId(String toId) {
        this.toId = toId;
    }
    public String getToName() {
        return this.toName;
    }
    public void setToName(String toName) {
        this.toName = toName;
    }
    public String getTranslatorId() {
        return this.translatorId;
    }
    public void setTranslatorId(String translatorId) {
        this.translatorId = translatorId;
    }
    public String getTranslateFileUrl() {
        return this.translateFileUrl;
    }
    public void setTranslateFileUrl(String translateFileUrl) {
        this.translateFileUrl = translateFileUrl;
    }
    public String getTranslateFilePath() {
        return this.translateFilePath;
    }
    public void setTranslateFilePath(String translateFilePath) {
        this.translateFilePath = translateFilePath;
    }
    public Integer getTranslateVoiceLong() {
        return this.translateVoiceLong;
    }
    public void setTranslateVoiceLong(Integer translateVoiceLong) {
        this.translateVoiceLong = translateVoiceLong;
    }
    public String getFileUrl() {
        return this.fileUrl;
    }
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getVideoThumbnailPath() {
        return this.videoThumbnailPath;
    }
    public void setVideoThumbnailPath(String videoThumbnailPath) {
        this.videoThumbnailPath = videoThumbnailPath;
    }
    public Integer getVoiceLong() {
        return this.voiceLong;
    }
    public void setVoiceLong(Integer voiceLong) {
        this.voiceLong = voiceLong;
    }
    public String getImageSize() {
        return this.imageSize;
    }
    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }
    public String getMsgDescription() {
        return this.msgDescription;
    }
    public void setMsgDescription(String msgDescription) {
        this.msgDescription = msgDescription;
    }
    public String getOriginalObjID() {
        return this.originalObjID;
    }
    public void setOriginalObjID(String originalObjID) {
        this.originalObjID = originalObjID;
    }
    public String getTempStr() {
        return this.tempStr;
    }
    public void setTempStr(String tempStr) {
        this.tempStr = tempStr;
    }
    public String getMediumObjID() {
        return this.mediumObjID;
    }
    public void setMediumObjID(String mediumObjID) {
        this.mediumObjID = mediumObjID;
    }
    public String getThumbnailObjID() {
        return this.thumbnailObjID;
    }
    public void setThumbnailObjID(String thumbnailObjID) {
        this.thumbnailObjID = thumbnailObjID;
    }
    public String getHdKey() {
        return this.hdKey;
    }
    public void setHdKey(String hdKey) {
        this.hdKey = hdKey;
    }
    public String getResponse() {
        return this.response;
    }
    public void setResponse(String response) {
        this.response = response;
    }
    public String getPrivateToNickname() {
        return this.privateToNickname;
    }
    public void setPrivateToNickname(String privateToNickname) {
        this.privateToNickname = privateToNickname;
    }
    public Boolean getIsPrivateMessage() {
        return this.isPrivateMessage;
    }
    public void setIsPrivateMessage(Boolean isPrivateMessage) {
        this.isPrivateMessage = isPrivateMessage;
    }
    public String getPrivateMsgFromID() {
        return this.privateMsgFromID;
    }
    public void setPrivateMsgFromID(String privateMsgFromID) {
        this.privateMsgFromID = privateMsgFromID;
    }
    public String getExtraUid() {
        return this.extraUid;
    }
    public void setExtraUid(String extraUid) {
        this.extraUid = extraUid;
    }
    public Boolean getIsPrivateMsgFromMe() {
        return this.isPrivateMsgFromMe;
    }
    public void setIsPrivateMsgFromMe(Boolean isPrivateMsgFromMe) {
        this.isPrivateMsgFromMe = isPrivateMsgFromMe;
    }
    public Integer getSendMsgType() {
        return this.sendMsgType;
    }
    public void setSendMsgType(Integer sendMsgType) {
        this.sendMsgType = sendMsgType;
    }
    public Boolean getIsChoiceTranslate() {
        return this.isChoiceTranslate;
    }
    public void setIsChoiceTranslate(Boolean isChoiceTranslate) {
        this.isChoiceTranslate = isChoiceTranslate;
    }
    public Integer getTranslationStatus() {
        return this.translationStatus;
    }
    public void setTranslationStatus(Integer translationStatus) {
        this.translationStatus = translationStatus;
    }
    public Integer getTranslateType() {
        return this.translateType;
    }
    public void setTranslateType(Integer translateType) {
        this.translateType = translateType;
    }
    public Integer getMsgStatus() {
        return this.msgStatus;
    }
    public void setMsgStatus(Integer msgStatus) {
        this.msgStatus = msgStatus;
    }
    public Boolean getMsgIsTrans() {
        return this.msgIsTrans;
    }
    public void setMsgIsTrans(Boolean msgIsTrans) {
        this.msgIsTrans = msgIsTrans;
    }
    public Boolean getIsReTrans() {
        return this.isReTrans;
    }
    public void setIsReTrans(Boolean isReTrans) {
        this.isReTrans = isReTrans;
    }
    public Boolean getIsReadVoice() {
        return this.isReadVoice;
    }
    public void setIsReadVoice(Boolean isReadVoice) {
        this.isReadVoice = isReadVoice;
    }
    public Integer getType() {
        return this.type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public Long getTranslateTime() {
        return this.translateTime;
    }
    public void setTranslateTime(Long translateTime) {
        this.translateTime = translateTime;
    }
    public String getExtraInfo() {
        return this.extraInfo;
    }
    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
    public String getOrderId() {
        return this.orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

  
}
