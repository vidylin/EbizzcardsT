package com.gzligo.ebizzcardstranslator.utils;

import android.util.Log;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lwd on 2017/6/1.
 */

public class TimeUtils {

    public static String getYMD(long time) {
        if (time == 0) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String fromTime = sdf.format(time);
        return fromTime;
    }

    public static String getHM(long time) {
        if (time == 0) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String fromTime = sdf.format(time);
        return fromTime;
    }

    public static String getHMS(long time) {
        String nowTime = new SimpleDateFormat("HH:mm:ss").format(new Date(time));
        return nowTime;
    }

    public static String getNowTime(){
        return String.valueOf(Long.valueOf(System.currentTimeMillis()));
    }

    public static String getyMd(long time){
        if (time == 0){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fromTime = sdf.format(time);
        return fromTime;
    }

    public static String getyM(long time){
        if (time == 0){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String fromTime = sdf.format(time);
        return fromTime;
    }

    public static String getMdHm(long time){
        if (time == 0){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        String fromTime = sdf.format(time);
        return fromTime;
    }

    public static String getyMdHms(long time){
        if (time == 0){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromTime = sdf.format(time);
        return fromTime;
    }

    public static String getyMdHmss(long time){
        if (time == 0){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String fromTime = sdf.format(time);
        return fromTime;
    }



    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        Log.d("MathUtils","string = " + string);
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static String getCurTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String mRefreshTime = formatter.format(curDate);
        return mRefreshTime;
    }

    public static String getCurTimeUseName(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        String mRefreshTime = formatter.format(curDate);
        return mRefreshTime;
    }

    public static String getSystemTime() {
        SimpleDateFormat format = null;
        String outTime;
        format = new SimpleDateFormat("yyyyMMddHHmmss");
        outTime = format.format(new Date());
        return outTime;
    }

    public static String formatTime(int miss){
        String mm=(miss % 3600)/60>9?(miss % 3600)/60+"":"0"+(miss % 3600)/60;
        String ss=(miss % 3600) % 60>9?(miss % 3600) % 60+"":"0"+(miss % 3600) % 60;
        return mm+":"+ss;
    }

    public static String second2Time(long time) {
        String timeStr;
        long hour;
        long minute;
        long second;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60000;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time/1000 - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(long i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getDoubleDigit(int price) {
        float amount = (float)price/100;
        DecimalFormat df = new DecimalFormat("0.00");
        String amountStr =  df.format(amount);
        return amountStr;
    }

    public static String getTravelTransTime(long startTime,long endTime){
        if (startTime == 0|| endTime==0){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String startTimeStr = sdf.format(startTime);
        String endTimeStr = sdf.format(endTime);
        String[] endTimes = endTimeStr.split(" ");
        return startTimeStr+"-"+endTimes[1];
    }

    public static String getMinute(int time){
        String timeStr;
        int result = time/60;
        if(result==0){
            timeStr = 1+AppManager.get().getApplication().getResources().getString(R.string.video_calls_time);
        }else{
            timeStr = result + AppManager.get().getApplication().getResources().getString(R.string.video_calls_time);
        }
        return timeStr;
    }
}
