package com.gzligo.ebizzcardstranslator.constants;

/**
 * Created by Lwd on 2017/6/10.
 */

public class ChatConstants {
    public static final int TXT_PRIVATE = 0;
    public static final int VOICE_PRIVATE = 1;

    public static final int UN_TRANSLATE_MSG = 0;
    public static final int TRANSLATE_MSG = 1;

    public static final int CHAT_IMG_WIDTH = 200;
    public static final int CHAT_MAX_IMG_WIDTH = 375;
    public static final int CHAT_IMG_HEIGHT= 375;
    public static final int CHAT_IMG_SHAPE_TRANS_FORMATION = 5;

    //commonChat
    public static final int COMMON_TXT_CHAT = 0;
    public static final int COMMON_IMG_CHAT = 1;
    public static final int COMMON_FILE_CHAT = 3;
    public static final int COMMON_VOICE_CHAT = 2;
    public static final int COMMON_VIDEO_CHAT = 4;
    public static final int COMMON_VIDEO = 5;
    public static final int COMMON_PRODUCT_CHAT = 6;
    public static final int END_CHAT = 0x81;
    public static final int START_CHAT = 0x80;

    //private Chat
    public static final int PRIVATE_TXT_CHAT = 10;
    public static final int PRIVATE_IMG_CHAT = 11;

    public static final int PRIVATE_VOICE_CHAT = 12;
    public static final int PRIVATE_VIDEO_CHAT = 14;
    public static final String COME_FROM_CHAT = "ChatFragment";

    //TranslatorSelectedBean
    public static final int SYSTEM_START_TRANSLATION = 0;
    public static final int SYSTEM_END_TRANSLATION = 1;

    //HistoryOrderDetailActivity
    public static final String COME_FROM_HISTORY = "HistoryOrderDetailActivity";

    public static final long RE_TRANSLATE_MSG_TIME = 7200000;

}
