package com.gzligo.ebizzcardstranslator.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Lwd on 2017/6/14.
 */

public class KeyBoardUtils {

    public static void hideKeyBoard(Context context, View view) {
        if(isShowing(context,view)){
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
        }
    }

    public static void showKeyBoard(Context context, View view) {
        if(!isShowing(context,view)){
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.findFocus();
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static boolean isShowing(Context context, View v){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        if (imm.hideSoftInputFromWindow(v.getWindowToken(), 0)) {
            imm.showSoftInput(v, 0);
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSoftShowing(Activity context) {
        int screenHeight = context.getWindow().getDecorView().getHeight();
        Rect rect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return screenHeight - rect.bottom != 0;
    }

}
