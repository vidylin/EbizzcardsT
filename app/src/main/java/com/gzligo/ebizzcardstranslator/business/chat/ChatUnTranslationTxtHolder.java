package com.gzligo.ebizzcardstranslator.business.chat;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.utils.GlideUtils;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import butterknife.BindView;

import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.ON_CHOICE_ITEM_TO_TRANS;

/**
 * Created by Lwd on 2017/6/9.
 */

public class ChatUnTranslationTxtHolder extends BaseHolder<ChatMessageBean> {
    @BindView(R.id.chat_time_tv) TextView chatTimeTv;
    @BindView(R.id.voice_img) ImageView voiceImg;
    @BindView(R.id.un_translate_content_txt_tv) TextView unTranslateContentTxtTv;
    @BindView(R.id.ll_msg) LinearLayout llMsg;
    @BindView(R.id.chat_ll) LinearLayout chatLl;
    @BindView(R.id.report_check) CheckBox reportCheck;
    @BindView(R.id.chat_check) CheckBox chatCheck;
    @BindView(R.id.people_img) ImageView peopleImg;
    @BindView(R.id.from_people_tv) TextView fromPeopleTv;
    private IView iView;
    private NewTransOrderBean newTransOrderBean;
    private String comeForm;

    public ChatUnTranslationTxtHolder(View itemView, IView iView, NewTransOrderBean newTransOrderBean, String comeForm) {
        super(itemView);
        this.iView = iView;
        this.newTransOrderBean = newTransOrderBean;
        this.comeForm = comeForm;
    }

    @Override
    public void setData(final ChatMessageBean data, final int position) {
        chatTimeTv.setText(TimeUtils.getHM(Long.parseLong(data.getMsgTime())));
        if (data.getMsgIsTrans()) {
            chatCheck.setVisibility(View.VISIBLE);
        } else {
            chatCheck.setVisibility(View.GONE);
        }
        GlideUtils.loadPeopleImage(data.getFromId(), peopleImg,newTransOrderBean);
        longClickHeadPortrait(position,newTransOrderBean.getFromUserId(),peopleImg,iView);
        switch (data.getTranslateType()) {
            case ChatConstants.COMMON_TXT_CHAT:
                chatCheck.setVisibility(View.VISIBLE);
                fromPeopleTv.setVisibility(View.GONE);
                chatCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choiceItem(position, data.getMsgId());
                        llMsg.setBackgroundResource(R.drawable.chat_choice_translate_display_left);
                    }
                });
                if (data.getIsChoiceTranslate()) {
                    Drawable img_off;
                    Resources res = AppManager.get().getApplication().getResources();
                    img_off = res.getDrawable(R.mipmap.chat_untranslated_press);
                    img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                    chatCheck.setCompoundDrawables(img_off, null, null, null);
                    llMsg.setBackgroundResource(R.drawable.chat_choice_translate_display_left);
                    chatCheck.setChecked(true);
                    chatCheck.setClickable(false);
                } else {
                    Drawable img_off;
                    Resources res = AppManager.get().getApplication().getResources();
                    img_off = res.getDrawable(R.mipmap.chat_untranslated_normal);
                    img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                    chatCheck.setCompoundDrawables(img_off, null, null, null);
                    llMsg.setBackgroundResource(R.drawable.chat_un_choice_translate_display_left);
                    chatCheck.setChecked(false);
                    chatCheck.setClickable(true);
                }
                unTranslateContentTxtTv.setText(data.getContent());
                break;
            case ChatConstants.PRIVATE_TXT_CHAT:
                chatCheck.setVisibility(View.GONE);
                fromPeopleTv.setVisibility(View.GONE);
                String userName = "@" + data.getPrivateToNickname().replaceAll("\r|\n", "");
                String str = userName + data.getContent().replaceAll("\r|\n", "");
                int bsStart = str.indexOf(userName);
                int bend = bsStart + userName.length();
                SpannableStringBuilder style=new SpannableStringBuilder(str);
                style.setSpan(new BackgroundColorSpan(AppManager.get().getApplication().getResources().getColor(R.color.chat_head_bg)),bsStart,bend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                unTranslateContentTxtTv.setText(style);
                break;
        }
        if (comeForm.equals(ChatConstants.COME_FROM_HISTORY)) {
            chatCheck.setVisibility(View.GONE);
        }
    }

    private void choiceItem(int pos, String msgId) {
        Message msg = Message.obtain(iView);
        msg.what = ON_CHOICE_ITEM_TO_TRANS;
        msg.objs = new String[]{pos + "", msgId};
        msg.dispatchToIView();
    }

    @Override
    public void onRelease() {
    }
}
