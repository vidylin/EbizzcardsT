package com.gzligo.ebizzcardstranslator.push.utils;

import android.support.annotation.Nullable;

import com.gzligo.ebizzcardstranslator.push.utils.encryption.EncodeUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileUtils {
    public static final String ALGORITHM_MD5 = "MD5";

    public static byte[] streamToByte(InputStream is) {
        ByteArrayOutputStream byteStream = null;
        byte[] imgdata = null;
        try {
            byteStream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                byteStream.write(ch);
            }
            imgdata = byteStream.toByteArray();

            byteStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseHelper.closeQuietly(byteStream);
        }
        return imgdata;
    }

    public static String digestMD5(final String str) {
        try {
            return digest(ALGORITHM_MD5, str, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String digest(String algorithm, String str, @Nullable Charset charset) {
        return digest(algorithm, str.getBytes(charset != null ? charset : Charset.defaultCharset()));
    }

    public static String digest(String algorithm, byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.reset();
            return EncodeUtil.encodeHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("无法初始化" + algorithm + "算法");
        }
    }
}
