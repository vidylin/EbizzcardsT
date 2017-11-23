package com.gzligo.ebizzcardstranslator.push.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Lwd on 2017/9/8.
 */

public class AboutPhoneInfo {
    private Context mContext;
    private static AboutPhoneInfo aboutPhoneInfo;

    public synchronized static AboutPhoneInfo getAboutPhoneInfo(Context context) {
        if (aboutPhoneInfo == null) {
            aboutPhoneInfo = new AboutPhoneInfo(context);
        }
        return aboutPhoneInfo;
    }

    private AboutPhoneInfo(Context context) {
        mContext = context;
    }

    public StringBuffer getPhoneInfo(){
        TelephonyManager mtm = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
        String imei = mtm.getDeviceId();
        StringBuffer sb = new StringBuffer();
        sb.append(" Phone Model：" + Build.MODEL);
        sb.append("\n System Version：" + android.os.Build.VERSION.RELEASE);
        sb.append("\n Customizer: " + Build.BRAND);
        sb.append("\n Manufacturer: "+Build.MANUFACTURER);
        sb.append("\n IMEI: " + imei);
        sb.append(getRomVersion());
        return sb;
    }

    public String getRomVersion(){
        String strRomVersion = null;
        if (Build.MANUFACTURER.equals("Xiaomi")){
            strRomVersion = "\n Rom Version: "+getRomVersion("ro.build.version.incremental");
        }else if (Build.MANUFACTURER.equals("HUAWEI")){
            strRomVersion = "\n Rom Version: "+getRomVersion("ro.build.version.emui");
        }else if (Build.MANUFACTURER.equals("vivo")){
            strRomVersion = "\n Rom Version: "+getRomVersion("ro.vivo.os.build.display.id");
        }else if (Build.MANUFACTURER.equals("Meizu")){
            strRomVersion = "\n Rom Version: "+getRomVersion("ro.build.display.id");
        }else if (Build.MANUFACTURER.equals("OPPO")){
            strRomVersion = "\n Rom Version: "+getRomVersion("ro.build.version.opporom");
        }else if (Build.MANUFACTURER.equals("samsung")){
            strRomVersion = "\n Rom Version: "+getRomVersion("ro.build.id");
        }else {
            //其他的获取的是安卓的原生的rom版本
            strRomVersion = "\n Rom Version: "+getRomVersion("ro.build.id");
        }
        return strRomVersion;
    }

    public static String getRomInfo(){
        String strRomVersion = null;
        if (Build.MANUFACTURER.equals("Xiaomi")){
            strRomVersion = getRomVersion("ro.build.version.incremental");
        }else if (Build.MANUFACTURER.equals("HUAWEI")){
            strRomVersion = getRomVersion("ro.build.version.emui");
        }else if (Build.MANUFACTURER.equals("vivo")){
            strRomVersion = getRomVersion("ro.vivo.os.build.display.id");
        }else if (Build.MANUFACTURER.equals("Meizu")){
            strRomVersion = getRomVersion("ro.build.display.id");
        }else if (Build.MANUFACTURER.equals("OPPO")){
            strRomVersion = getRomVersion("ro.build.version.opporom");
        }else if (Build.MANUFACTURER.equals("samsung")){
            strRomVersion = getRomVersion("ro.build.id");
        }else {
            //其他的获取的是安卓的原生的rom版本
            strRomVersion = getRomVersion("ro.build.id");
        }
        return strRomVersion;
    }

    private static String getRomVersion(String versionKey){
        String line = null;
        BufferedReader input = null;
        InputStreamReader inputStreamReader = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop "+versionKey);
            inputStreamReader = new InputStreamReader(p.getInputStream());
            input = new BufferedReader(inputStreamReader,1024);
            line = input.readLine();
            input.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            CloseHelper.closeQuietly(inputStreamReader);
            CloseHelper.closeQuietly(input);
        }
        return line;
    }

    public static String getKey() {
        String rom = AboutPhoneInfo.getRomInfo();
        String manufacturer = Build.MANUFACTURER;
        String androidVersion = android.os.Build.VERSION.RELEASE;
        String phoneInfo = manufacturer + "-" + androidVersion + "-" + rom;
        return phoneInfo;
    }
}
