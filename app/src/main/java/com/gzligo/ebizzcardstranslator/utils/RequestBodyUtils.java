package com.gzligo.ebizzcardstranslator.utils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Lwd on 2017/6/8.
 */

public class RequestBodyUtils {

    public static RequestBody getRequestBody(String param){
        return RequestBody.create(MediaType.parse("multipart/form-data"), param);
    }

    public static RequestBody getFileRequestBody(String path){
        File file = new File(path);
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }

    public static MultipartBody.Part getMultipartBody(String path, String filename,String key){
        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData(key,filename, requestFile);
        return body;
    }

    public static MultipartBody.Part getMultipartBody(String path,String key){
        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData(key,file.getName(), requestFile);
        return body;
    }
}
