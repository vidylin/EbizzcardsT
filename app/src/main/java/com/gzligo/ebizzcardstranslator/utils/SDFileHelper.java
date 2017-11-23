package com.gzligo.ebizzcardstranslator.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * Created by Lwd on 2017/6/13.
 */

public class SDFileHelper {
    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;

    public SDFileHelper(String destFileDir,String destFileName){
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    public File saveFile(ResponseBody responseBody,String AESKey) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[20480];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = responseBody.byteStream();
            final long total = responseBody.contentLength();
            long sum = 0;
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            if (!TextUtils.isEmpty(AESKey)) {
                buf = IOUtils.readFully(is);
                buf = new AES(AESKey).aesDecrypt(buf);
                fos.write(buf, 0, buf.length);
                fos.flush();
            } else {//不需要解密
                while ((len = is.read(buf)) != -1) {
                    sum += len;
                    fos.write(buf, 0, len);
                }
                fos.flush();
            }
            return file;
        } finally {
            try {
                responseBody.close();
                if (is != null) is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }
        }
    }
}
