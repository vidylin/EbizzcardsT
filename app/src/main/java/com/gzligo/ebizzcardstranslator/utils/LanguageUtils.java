package com.gzligo.ebizzcardstranslator.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.business.account.view.login.TranslatorConstants;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;

import java.util.Locale;
import java.util.TreeMap;

/**
 * Created by ZuoJian on 2017/8/22.
 */

public class LanguageUtils {

    public static void init(Context context) {
        String language = (String) SharedPreferencesUtils.getSharedPreferences(context, TranslatorConstants.SharedPreferences.SETTING_LANGUAGE,
                TranslatorConstants.SharedPreferences.LANGUAGE_CH,TranslatorConstants.SharedPreferences.SETTING_LANGUAGE);
        if (language != null) {
            switchLanguage(context, language);
        }
    }

    public static void switchLanguage(Context context, String language) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();

        if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
            config.setLocale(Locale.SIMPLIFIED_CHINESE);
        } else if (language.equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)) {
            config.setLocale(Locale.ENGLISH);
        }
        context.getResources().updateConfiguration(config, dm);
        new SharedPreferencesUtils<String>().putSharedPreference(context, TranslatorConstants.SharedPreferences.SETTING_LANGUAGE, language,
                TranslatorConstants.SharedPreferences.SETTING_LANGUAGE);
    }

    public static String getLanguage(Context context) {
        String language = (String) SharedPreferencesUtils.getSharedPreferences(context, TranslatorConstants.SharedPreferences.SETTING_LANGUAGE,
                TranslatorConstants.SharedPreferences.LANGUAGE_CH, TranslatorConstants.SharedPreferences.SETTING_LANGUAGE);
        if (language == null) {
            Locale locale = context.getResources().getConfiguration().locale;
            if(locale.getLanguage().equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)
                    || locale.getLanguage().equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)){
                return locale.getLanguage();
            }
            return TranslatorConstants.SharedPreferences.LANGUAGE_EN;
        }
        return language;
    }

    public static String currentLanguage(Context context){
        return (String) SharedPreferencesUtils.getSharedPreferences(context, TranslatorConstants.SharedPreferences.SETTING_LANGUAGE,
                TranslatorConstants.SharedPreferences.LANGUAGE_CH, TranslatorConstants.SharedPreferences.SETTING_LANGUAGE);
    }

    public static String getLanguageName(int language,TreeMap<Integer, LanguagesBean> languagesBeanTreeMap){
        String fromLanguage = "";
        if (getLanguage(AppManager.get().getApplication()).equals(TranslatorConstants.SharedPreferences.LANGUAGE_CH)) {
            fromLanguage = languagesBeanTreeMap.get(language).getZh_name();
        } else if (getLanguage(AppManager.get().getApplication()).equals(TranslatorConstants.SharedPreferences.LANGUAGE_EN)) {
            fromLanguage = languagesBeanTreeMap.get(language).getEn_name();
        }
        return fromLanguage;
    }
}
