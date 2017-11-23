package com.gzligo.ebizzcardstranslator.utils;

import android.content.Context;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.constants.ChatConstants;

import java.lang.reflect.Method;

/**
 * Created by Lwd on 2017/6/1.
 */

public class ScreenUtils {
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;

    }

    public static  int getBottomStatusHeight(Context context){
        int totalHeight = getDpi(context);
        int contentHeight = getScreenHeight(context);
        return totalHeight  - contentHeight;
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getDpi(Context context){
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi=displayMetrics.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        }
        return dpi;
    }

    public static int getImageViewHeight(String imgSize,int width){
        String[] imageSizes = imgSize.split("\\*");
        int height = 0;
        if(null!=imageSizes&&imageSizes.length==2){
            int w = Integer.parseInt(imageSizes[0]);
            int h = Integer.parseInt(imageSizes[1]);
            height = width*h/w;
        }
        return height;
    }

    public static int getImageViewWidth(TextView textView,TextView descTextView,String textViewStr,String descTextViewStr){
        TextPaint textPaint = textView.getPaint();
        int textWidth = (null!=textViewStr)?(int) textPaint.measureText(textViewStr):0;
        TextPaint descTextPaint = descTextView.getPaint();
        int descTextWidth = (null!=descTextViewStr)?(int) descTextPaint.measureText(descTextViewStr):0;
        int w = textWidth>descTextWidth?textWidth:descTextWidth;
        if(w > ChatConstants.CHAT_MAX_IMG_WIDTH){
            return ChatConstants.CHAT_MAX_IMG_WIDTH;
        }else{
            if(w>ChatConstants.CHAT_IMG_WIDTH){
                return w;
            }else{
                return ChatConstants.CHAT_IMG_WIDTH;
            }
        }
    }

    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
