package com.gzligo.ebizzcardstranslator.utils;

import android.content.Context;

import com.gzligo.ebizzcardstranslator.R;

/**
 * Created by Lwd on 2017/7/12.
 */

public class CurrencyTypeUtil {

    public static String getCurrencySymbol(Context context, int currencyType){
        String currencyTypeStr="";
        switch (currencyType){
            case 1:
                currencyTypeStr = context.getResources().getString(R.string.currency_zh);
                break;
            case 2:
                currencyTypeStr = context.getResources().getString(R.string.currency_en);
                break;
            case 18:
                currencyTypeStr = context.getResources().getString(R.string.currency_in);
                break;
        }
        return currencyTypeStr;
    }
}
