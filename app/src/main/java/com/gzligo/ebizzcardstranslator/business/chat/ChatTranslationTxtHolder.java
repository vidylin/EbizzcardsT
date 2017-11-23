package com.gzligo.ebizzcardstranslator.business.chat;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.utils.CommonUtils;
import com.gzligo.ebizzcardstranslator.utils.GlideUtils;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/6/10.
 */

public class ChatTranslationTxtHolder extends BaseHolder<ChatMessageBean> {
    @BindView(R.id.chat_year_moth_day_tv) TextView chatYearMothDayTv;
    @BindView(R.id.year_moth_day_ll) LinearLayout yearMothDayLl;
    @BindView(R.id.chat_time_tv) TextView chatTimeTv;
    @BindView(R.id.voice_img) ImageView voiceImg;
    @BindView(R.id.un_translate_content_txt_tv) TextView unTranslateContentTxtTv;
    @BindView(R.id.voice_img_translation) ImageView voiceImgTranslation;
    @BindView(R.id.translate_content_txt_tv) TextView translateContentTxtTv;
    @BindView(R.id.ll_msg) LinearLayout llMsg;
    @BindView(R.id.people_img) ImageView peopleImg;
    private NewTransOrderBean newTransOrderBean;
    private IView iView;
    private String comeForm;

    public ChatTranslationTxtHolder(View itemView, NewTransOrderBean newTransOrderBean, IView iView, String comeForm) {
        super(itemView);
        this.newTransOrderBean = newTransOrderBean;
        this.iView = iView;
        this.comeForm = comeForm;
    }

    @Override
    public void setData(final ChatMessageBean data, final int position) {
        unTranslateContentTxtTv.setText(data.getContent());
        chatTimeTv.setText(TimeUtils.getHM(Long.parseLong(data.getMsgTime())));
        GlideUtils.loadPeopleImage(data.getFromId(), peopleImg,newTransOrderBean);
        longClickHeadPortrait(position,newTransOrderBean.getFromUserId(),peopleImg,iView);
        if (data.getIsReTrans()) {
            llMsg.setBackgroundResource(R.drawable.chat_re_translation_show);
        } else {
            if(data.getIsChoiceTranslate()){
                llMsg.setBackgroundResource(R.drawable.chat_choice_translate_display_left);
            }else{
                llMsg.setBackgroundResource(R.drawable.chat_translate_display_left);
            }
        }
        switch (data.getSendMsgType()) {
            case ChatConstants.VOICE_PRIVATE:
                voiceImgTranslation.setVisibility(View.VISIBLE);
                translateContentTxtTv.setText(data.getTranslateVoiceLong() + "\"");
                voiceImgTranslation.setBackgroundResource(R.mipmap.voice_play_green_three);
                translateContentTxtTv.setTextColor(AppManager.get().getApplication().getResources().getColor(R.color.green));
                break;
            case ChatConstants.TXT_PRIVATE:
                translateContentTxtTv.setVisibility(View.VISIBLE);
                voiceImgTranslation.setVisibility(View.GONE);
                translateContentTxtTv.setText(data.getTranslateContent());
                translateContentTxtTv.setTextColor(AppManager.get().getApplication().getResources().getColor(R.color.green));
                break;
        }
        voiceImgTranslation.setBackgroundResource(R.mipmap.voice_play_green_three);
        final String filePath = data.getTranslateFilePath();
        if(!TextUtils.isEmpty(filePath)){
            voiceImgTranslation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonUtils.playVoice(position, filePath, voiceImgTranslation,iView);
                }
            });
        }
        reTranslateMsg(llMsg,position,iView,data,comeForm);
    }

    @Override
    public void onRelease() {
    }
}
