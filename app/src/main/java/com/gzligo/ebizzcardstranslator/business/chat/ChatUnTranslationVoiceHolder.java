package com.gzligo.ebizzcardstranslator.business.chat;

import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.TextUtils;
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
import com.gzligo.ebizzcardstranslator.utils.MediaManager;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import butterknife.BindView;

import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.CLICK_VOICE_MSG;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.ON_CHOICE_ITEM_TO_TRANS;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.STOP_VOICE;

/**
 * Created by Lwd on 2017/6/12.
 */

public class ChatUnTranslationVoiceHolder extends BaseHolder<ChatMessageBean> {
    @BindView(R.id.chat_time_tv) TextView chatTimeTv;
    @BindView(R.id.people_img) ImageView peopleImg;
    @BindView(R.id.voice_img) ImageView voiceImg;
    @BindView(R.id.voice_time_tv) TextView voiceTimeTv;
    @BindView(R.id.voice_ll) LinearLayout voiceLl;
    @BindView(R.id.receive_img) ImageView receiveImg;
    @BindView(R.id.un_translate_content_txt_tv) TextView unTranslateContentTxtTv;
    @BindView(R.id.ll_msg) LinearLayout llMsg;
    @BindView(R.id.report_check) CheckBox reportCheck;
    @BindView(R.id.chat_check) CheckBox chatCheck;
    @BindView(R.id.private_user_voice_name) TextView privateUserVoiceName;
    private IView iView;
    private NewTransOrderBean newTransOrderBean;
    private String comeForm;

    public ChatUnTranslationVoiceHolder(View itemView, IView iView, NewTransOrderBean newTransOrderBean, String comeForm) {
        super(itemView);
        this.iView = iView;
        this.newTransOrderBean = newTransOrderBean;
        this.comeForm = comeForm;
    }

    @Override
    public void setData(final ChatMessageBean data, final int position) {
        chatTimeTv.setText(TimeUtils.getHM(Long.parseLong(data.getMsgTime())));
        voiceLl.setVisibility(View.VISIBLE);
        receiveImg.setVisibility(View.GONE);
        voiceTimeTv.setText(String.valueOf(data.getVoiceLong()) + "\"");
        voiceTimeTv.setTextColor(AppManager.get().getApplication().getResources().getColor(R.color.text_color));
        if (!data.getMsgIsTrans()) {
            chatCheck.setVisibility(View.VISIBLE);
        } else {
            chatCheck.setVisibility(View.GONE);
        }
        GlideUtils.loadPeopleImage(data.getFromId(), peopleImg,newTransOrderBean);
        longClickHeadPortrait(position,newTransOrderBean.getFromUserId(),peopleImg,iView);
        Drawable img_off;
        Resources res = AppManager.get().getApplication().getResources();
        if (data.getIsChoiceTranslate()) {
            img_off = res.getDrawable(R.mipmap.chat_untranslated_press);
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            chatCheck.setCompoundDrawables(img_off, null, null, null);
            chatCheck.setChecked(true);
            chatCheck.setClickable(false);
        } else {
            img_off = res.getDrawable(R.mipmap.chat_untranslated_normal);
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            chatCheck.setCompoundDrawables(img_off, null, null, null);
            chatCheck.setClickable(true);
            chatCheck.setChecked(false);
        }
        if(data.getIsReadVoice()){
            voiceImg.setBackgroundResource(R.mipmap.voice_play_black_three);
        }else{
            voiceImg.setBackgroundResource(R.mipmap.voice_play_green_three);
        }

        final String filePath = data.getFilePath();
        if(!TextUtils.isEmpty(filePath)){
            voiceImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sentMsg(CLICK_VOICE_MSG,position);
                    playVoice(position, filePath, voiceImg);
                }
            });
        }

        switch (data.getTranslateType()) {
            case ChatConstants.COMMON_VOICE_CHAT:
                privateUserVoiceName.setVisibility(View.GONE);
                if(data.getIsChoiceTranslate()){
                    llMsg.setBackgroundResource(R.drawable.chat_choice_translate_display_left);
                }else{
                    llMsg.setBackgroundResource(R.drawable.chat_un_choice_translate_display_left);
                }
                chatCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choiceItem(position, data.getMsgId());
                    }
                });
                break;
            case ChatConstants.PRIVATE_VOICE_CHAT:
                privateUserVoiceName.setVisibility(View.VISIBLE);
                privateUserVoiceName.setText("@" + data.getPrivateToNickname());
                chatCheck.setVisibility(View.GONE);
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

    private void playVoice(int position, String filePath, final ImageView imageView) {
        imageView.setBackgroundResource(R.drawable.play_voice_left_black);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
        drawable.start();
        boolean isPlaying = MediaManager.getInstance().isPlaying();
        if (isPlaying) {
            int pos = MediaManager.getInstance().getPosition();
            if (pos == position) {
                imageView.setBackgroundResource(R.mipmap.voice_play_black_three);
                MediaManager.getInstance().reset();
                MediaManager.getInstance().setPosition(-1);
                return;
            } else {
                sentMsg(STOP_VOICE,pos);
            }
        }
        MediaManager.getInstance().setPosition(position);
        MediaManager.getInstance().playSound(filePath, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                imageView.setBackgroundResource(R.mipmap.voice_play_black_three);
                MediaManager.getInstance().reset();
                MediaManager.getInstance().setPosition(-1);
            }
        }, new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
            }
        });
    }

    public void sentMsg(int what,int pos){
        Message message = Message.obtain(iView);
        message.what = what;
        message.arg1 = pos;
        message.dispatchToIView();
    }
}
