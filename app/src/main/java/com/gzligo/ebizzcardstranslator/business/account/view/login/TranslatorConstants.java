package com.gzligo.ebizzcardstranslator.business.account.view.login;

/**
 * Created by ZuoJian on 2017/5/26.
 */

public class TranslatorConstants {

    public static class Common{
        public static final String FROM = "from";
        public static final String DATA = "data";
        public static final String JSON = "json";
    }

    public static class Login{
        public static final String PHONE_NUMBER = "phone_number";
        public static final String NICKNAME = "nickname";
        public static final String CC_CODE = "cc_code";
        public static final String PHONE = "phone";
        public static final String PASSWORD = "password";
        public static final String CODE = "code";
        public static final String PORTRAIT = "portrait";
        public static final String SMS_TYPE_REGISTER = "0";
        public static final String SMS_TYPE_RESET_PASSWORD = "1";
        public static final String SMS_TYPE_CHANGE_PASSWORD = "2";
        public static final String SMS_TYPE_CHANGE_PHONE = "3";
    }

    public static class SharedPreferences{
        public static final String SETTING_LANGUAGE = "setting_language";
        public static final String LANGUAGE_CH = "language_ch";
        public static final String LANGUAGE_EN = "language_en";
        public static final String NOTIFICATION_SETTING_SOUND = "notification_setting_sound";
        public static final String NOTIFICATION_SETTING_NOTIFY_DETAIL = "notification_setting_notify_detail";
        public static final String NOTIFICATION_SETTING_VIBRATE = "notification_setting_vibrate";
    }
}
