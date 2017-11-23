package com.gzligo.ebizzcardstranslator.constants;

/**
 * Created by Lwd on 2017/7/5.
 */

public class CommonConstants {
    public static final String USER_INFO_PRE_NAME = "user_info";
    public static final String USER_NAME = "user_name";
    public static final String USER_NAME_MQTT = "user_name_mq";
    public static final String USER_ID = "user_id";
    public static final String USER_PORTRAIT_ID = "portrait_id";
    public static final String USER_COUNTRY_NAME = "user_country_name";
    public static final String USER_COUNTRY_ID = "user_country_id";
    public static final String USER_PHONE = "user_phone";
    public static final String USER_PWD = "user_pwd";
    public static final String FIRST_LOGIN = "first_login";
    public static final String USER_DB_NAME = "user_db_name";
    public static final String PASSWORD_TOKEN = "passwordToken";

    public static final String UD_ID = "udid";
    public static final String PARAM_JTI = "jti";
    public static final String PARAM_SID = "sid";
    public static final String INFO = "obj_info";
    public static final String REFRESH_TOKEN = "refresh_token";

    public static class ACacheContacts {
        private static final int SECOND_IN_SECOND = 1;
        private static final int MINUTE_IN_SECOND = SECOND_IN_SECOND * 60;
        private static final int HOUR_IN_SECOND = MINUTE_IN_SECOND * 60;
        private static final int DAY_IN_SECOND = HOUR_IN_SECOND * 24;
        public static final int ACCESS_TOKEN_EXPIRED = 30 * DAY_IN_SECOND;
        public static final String LOGIN_ACCESS_TOKEN = "loginAccessToken";//三十天有效期
    }
}
