package com.gzligo.ebizzcardstranslator.persistence;

/**
 * Created by Lwd on 2017/6/29.
 */

public class MessageContentFile {
    public String url;//除了图片,所有文件消息must //聊天文件上传后服务器返回的url,即ObjectId
    public String fileType;//must //文件类型（pdf,word,excel）
    public String name;//must //文件名称
    public long length;//must //文件大小 byte
    public String descriptions; //文件描述
}
