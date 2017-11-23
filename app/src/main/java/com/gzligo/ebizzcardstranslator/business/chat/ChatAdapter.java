package com.gzligo.ebizzcardstranslator.business.chat;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.waynell.videolist.visibility.items.ListItem;
import com.waynell.videolist.visibility.scroll.ItemsProvider;

import java.util.List;

/**
 * Created by Lwd on 2017/6/9.
 */

public class ChatAdapter extends BaseAdapter<ChatMessageBean> implements ItemsProvider {
    private static final int START_CHAT = 0x80;
    private static final int END_CHAT = 0x81;
    private static final int UN_TRANSLATION_TXT = 0;
    private static final int UN_TRANSLATION_IMAGE = 1;
    private static final int UN_TRANSLATION_VOICE = 2;
    private static final int UN_TRANSLATION_SHORT_VIDEO = 4;
    private static final int UN_TRANSLATION_VIDEO = 5;
    private static final int PRODUCT_CHAT_MSG = 6;
    private static final int PRIVATE_CHAT_TO_MSG = 16;

    private static final int TRANSLATION_TXT = 10;
    private static final int TRANSLATION_IMAGE = 11;
    private static final int TRANSLATION_VOICE = 12;
    private static final int TRANSLATION_VIDEO = 14;

    private List<ChatMessageBean> chatMessageBeanList;
    private IView iView;
    private RecyclerView chatRecyclerView;
    private NewTransOrderBean newTransOrderBean;
    private String comeForm;

    public ChatAdapter(List<ChatMessageBean> chatMessageBeanList, IView iView, RecyclerView chatRecyclerView, NewTransOrderBean newTransOrderBean, String comeForm) {
        super(chatMessageBeanList);
        this.chatMessageBeanList = chatMessageBeanList;
        this.iView = iView;
        this.chatRecyclerView = chatRecyclerView;
        this.newTransOrderBean = newTransOrderBean;
        this.comeForm = comeForm;
    }

    @Override
    public BaseHolder getHolder(View v, int viewType) {
        switch (viewType) {
            case UN_TRANSLATION_TXT:
                return new ChatUnTranslationTxtHolder(v, iView, newTransOrderBean, comeForm);
            case TRANSLATION_TXT:
                return new ChatTranslationTxtHolder(v, newTransOrderBean, iView, comeForm);
            case UN_TRANSLATION_IMAGE:
                return new ChatUnTranslationImgHolder(v, iView, newTransOrderBean, comeForm);
            case PRODUCT_CHAT_MSG:
            case TRANSLATION_IMAGE:
                return new ChatTranslationImgHolder(v, newTransOrderBean, iView, comeForm);
            case UN_TRANSLATION_VOICE:
                return new ChatUnTranslationVoiceHolder(v, iView, newTransOrderBean, comeForm);
            case TRANSLATION_VOICE:
                return new ChatTranslationVoiceHolder(v, newTransOrderBean, iView, comeForm);
            case UN_TRANSLATION_SHORT_VIDEO:
                return new ChatUnTranslationVideoHolder(v, iView, newTransOrderBean, comeForm);
            case UN_TRANSLATION_VIDEO:
                return new ChatTranslationVideoHolder(v, newTransOrderBean, iView, comeForm);
            case TRANSLATION_VIDEO:
                return new ChatTranslationVideoHolder(v, newTransOrderBean, iView, comeForm);
            case PRIVATE_CHAT_TO_MSG:
                return new ChatPrivateToMsgHolder(v, iView);
            case START_CHAT:
            case END_CHAT:
                return new ChatSystemMsgHolder(v);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageBean chatMessageBean = chatMessageBeanList.get(position);
        if (chatMessageBean.getIsPrivateMessage() && chatMessageBean.getIsPrivateMsgFromMe()) {
            return PRIVATE_CHAT_TO_MSG;
        } else {
            if (chatMessageBean.getTranslationStatus() == 0) {
                switch (chatMessageBean.getType()) {
                    case UN_TRANSLATION_TXT:
                        return UN_TRANSLATION_TXT;
                    case UN_TRANSLATION_IMAGE:
                        return UN_TRANSLATION_IMAGE;
                    case UN_TRANSLATION_VOICE:
                        return UN_TRANSLATION_VOICE;
                    case UN_TRANSLATION_SHORT_VIDEO:
                    case UN_TRANSLATION_VIDEO:
                        return UN_TRANSLATION_SHORT_VIDEO;
                    case START_CHAT:
                        return START_CHAT;
                    case END_CHAT:
                        return END_CHAT;
                }
            } else {
                switch (chatMessageBean.getTranslateType()) {
                    case UN_TRANSLATION_TXT:
                        return TRANSLATION_TXT;
                    case PRODUCT_CHAT_MSG:
                    case UN_TRANSLATION_IMAGE:
                        return TRANSLATION_IMAGE;
                    case UN_TRANSLATION_VOICE:
                        return TRANSLATION_VOICE;
                    case UN_TRANSLATION_SHORT_VIDEO:
                    case UN_TRANSLATION_VIDEO:
                        return TRANSLATION_VIDEO;
                }
            }
        }
        return -1;
    }

    @Override
    public int getLayoutResId(int viewType) {
        switch (viewType) {
            case UN_TRANSLATION_TXT:
                return R.layout.chat_left_untranslation_left_txt;
            case UN_TRANSLATION_IMAGE:
                return R.layout.chat_left_untranslation_item_img;
            case UN_TRANSLATION_VOICE:
                return R.layout.chat_left_untranslation_item_img;
            case UN_TRANSLATION_SHORT_VIDEO:
                return R.layout.chat_left_untranslation_item_video;
            case UN_TRANSLATION_VIDEO:
                return R.layout.chat_left_translation_item_video;
            case TRANSLATION_TXT:
                return R.layout.chat_left_translation_item_txt;
            case PRODUCT_CHAT_MSG:
            case TRANSLATION_IMAGE:
                return R.layout.chat_left_translation_item_img;
            case TRANSLATION_VOICE:
                return R.layout.chat_left_translation_item_img;
            case TRANSLATION_VIDEO:
                return R.layout.chat_left_translation_item_video;
            case PRIVATE_CHAT_TO_MSG:
                return R.layout.chat_right_private_txt;
            case START_CHAT:
            case END_CHAT:
                return R.layout.chat_year_moth_day;
        }
        return -1;
    }

    @Override
    public ListItem getListItem(int i) {
        RecyclerView.ViewHolder holder = chatRecyclerView.findViewHolderForAdapterPosition(i);
        if (holder instanceof ListItem) {
            return (ListItem) holder;
        }
        return null;
    }

    @Override
    public int listItemSize() {
        return chatMessageBeanList.size();
    }

    public void updateChatMessageBeanList(List<ChatMessageBean> chatMessageBeanList) {
        this.chatMessageBeanList = chatMessageBeanList;
    }

    @Override
    public void onBindViewHolder(BaseHolder<ChatMessageBean> holder, int position, List<Object> payloads) {
        if(!payloads.isEmpty()){
            String string = payloads.get(0).toString().replace("[", "").replace("]", "");
            switch (string){
                case "CHANGED":
                    ChatMessageBean data = chatMessageBeanList.get(position);
                    if(holder instanceof ChatUnTranslationTxtHolder){
                        ChatUnTranslationTxtHolder chatUnTranslationTxtHolder = (ChatUnTranslationTxtHolder) holder;
                        changedCheckBox(chatUnTranslationTxtHolder.chatCheck,data);
                        if (data.getIsChoiceTranslate()) {
                            chatUnTranslationTxtHolder.llMsg.setBackgroundResource(R.drawable.chat_choice_translate_display_left);
                        } else {
                            chatUnTranslationTxtHolder.llMsg.setBackgroundResource(R.drawable.chat_un_choice_translate_display_left);
                        }
                    } else if(holder instanceof ChatUnTranslationImgHolder){
                        ChatUnTranslationImgHolder chatUnTranslationImgHolder = (ChatUnTranslationImgHolder) holder;
                        changedCheckBox(chatUnTranslationImgHolder.chatCheck,data);
                    }else if(holder instanceof ChatUnTranslationVoiceHolder){
                        ChatUnTranslationVoiceHolder unTranslationVoiceHolder = (ChatUnTranslationVoiceHolder) holder;
                        changedCheckBox(unTranslationVoiceHolder.chatCheck,data);
                        if(ChatConstants.COMMON_VOICE_CHAT == data.getTranslateType()){
                            unTranslationVoiceHolder.privateUserVoiceName.setVisibility(View.GONE);
                            if(data.getIsChoiceTranslate()){
                                unTranslationVoiceHolder.llMsg.setBackgroundResource(R.drawable.chat_choice_translate_display_left);
                            }else{
                                unTranslationVoiceHolder.llMsg.setBackgroundResource(R.drawable.chat_un_choice_translate_display_left);
                            }
                        }
                    }else if(holder instanceof ChatUnTranslationVideoHolder){
                        ChatUnTranslationVideoHolder chatUnTranslationVideoHolder = (ChatUnTranslationVideoHolder) holder;
                        changedCheckBox(chatUnTranslationVideoHolder.chatCheck,data);
                    }
                    break;
                case "PLAY_VIDEO":
                    if(holder instanceof ChatUnTranslationVideoHolder){
                        ChatUnTranslationVideoHolder chatUnTranslationTxtHolder = (ChatUnTranslationVideoHolder) holder;
                        chatUnTranslationTxtHolder.playVideo();
                    }else if(holder instanceof ChatTranslationVideoHolder){
                        ChatTranslationVideoHolder chatTranslationVideoHolder = (ChatTranslationVideoHolder) holder;
                        chatTranslationVideoHolder.playVideo();
                    }
                    break;
                case "CHANGED_RE_TRANS_BG":
                    if(holder instanceof ChatTranslationTxtHolder){
                        ChatTranslationTxtHolder chatTranslationTxtHolder = (ChatTranslationTxtHolder) holder;
                        chatTranslationTxtHolder.llMsg.setBackgroundResource(R.drawable.chat_translate_display_left);
                    }else if(holder instanceof ChatTranslationVideoHolder){
                        ChatTranslationVideoHolder chatTranslationVideoHolder = (ChatTranslationVideoHolder) holder;
                        chatTranslationVideoHolder.llMsg.setBackgroundResource(R.drawable.chat_translate_display_left);
                    }else if(holder instanceof ChatTranslationVoiceHolder){
                        ChatTranslationVoiceHolder chatTranslationVoiceHolder = (ChatTranslationVoiceHolder) holder;
                        chatTranslationVoiceHolder.llMsg.setBackgroundResource(R.drawable.chat_translate_display_left);
                    }else if(holder instanceof ChatTranslationImgHolder){
                        ChatTranslationImgHolder chatTranslationImgHolder = (ChatTranslationImgHolder) holder;
                        chatTranslationImgHolder.llMsg.setBackgroundResource(R.drawable.chat_translate_display_left);
                    }
                    break;
            }

        }else{
            onBindViewHolder(holder, position);
        }
    }

    private void changedCheckBox(CheckBox checkBox, ChatMessageBean data){
        if (data.getIsChoiceTranslate()) {
            Drawable img_off;
            Resources res = AppManager.get().getApplication().getResources();
            img_off = res.getDrawable(R.mipmap.chat_untranslated_press);
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            checkBox.setCompoundDrawables(img_off, null, null, null);
            checkBox.setClickable(false);
            checkBox.setChecked(true);
        } else {
            Drawable img_off;
            Resources res = AppManager.get().getApplication().getResources();
            img_off = res.getDrawable(R.mipmap.chat_untranslated_normal);
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            checkBox.setCompoundDrawables(img_off, null, null, null);
            checkBox.setClickable(true);
            checkBox.setChecked(false);
        }
    }
}
