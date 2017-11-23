package com.gzligo.ebizzcardstranslator.business.chat;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;
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
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.STOP_VOICE;

/**
 * Created by Lwd on 2017/6/12.
 */

public class ChatTranslationVoiceHolder extends BaseHolder<ChatMessageBean> {
    @BindView(R.id.chat_time_tv) TextView chatTimeTv;
    @BindView(R.id.receive_voice_img) ImageView receiveVoiceImg;
    @BindView(R.id.voice_time_tv) TextView voiceTimeTv;
    @BindView(R.id.voice_ll) LinearLayout voiceLl;
    @BindView(R.id.voice_img) ImageView voiceImg;
    @BindView(R.id.un_translate_content_txt_tv) TextView unTranslateContentTxtTv;
    @BindView(R.id.voice_img_translation) ImageView voiceImgTranslation;
    @BindView(R.id.translate_content_txt_tv) TextView translateContentTxtTv;
    @BindView(R.id.ll_msg) LinearLayout llMsg;
    @BindView(R.id.people_img) ImageView peopleImg;
    private IView iView;
    private NewTransOrderBean newTransOrderBean;
    private String comeForm;
    private ImageView clickImageView;

    public ChatTranslationVoiceHolder(View itemView, NewTransOrderBean newTransOrderBean, IView iView, String comeForm) {
        super(itemView);
        this.newTransOrderBean = newTransOrderBean;
        this.iView = iView;
        this.comeForm = comeForm;
    }

    @Override
    public void setData(final ChatMessageBean data, final int position) {
        voiceLl.setVisibility(View.VISIBLE);
        voiceImg.setVisibility(View.GONE);
        chatTimeTv.setText(TimeUtils.getHM(Long.parseLong(data.getMsgTime())));
        voiceTimeTv.setText(String.valueOf(data.getVoiceLong()) + "\"");
        voiceTimeTv.setTextColor(AppManager.get().getApplication().getResources().getColor(R.color.text_color));
        GlideUtils.loadPeopleImage(data.getFromId(), peopleImg,newTransOrderBean);
        longClickHeadPortrait(position,newTransOrderBean.getFromUserId(),peopleImg,iView);
        if(data.getIsReadVoice()){
            receiveVoiceImg.setBackgroundResource(R.mipmap.voice_play_black_three);
        }else{
            receiveVoiceImg.setBackgroundResource(R.mipmap.voice_play_green_three);
        }
        MediaManager.getInstance().setTransVoice(false);
        final String filePath = data.getFilePath();
        if(!TextUtils.isEmpty(filePath)){
            receiveVoiceImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (receiveVoiceImg == clickImageView) {
                        MediaManager.getInstance().setTransVoice(false);
                    } else {
                        MediaManager.getInstance().setTransVoice(true);
                        clickImageView = receiveVoiceImg;
                    }
                    sentMsg(CLICK_VOICE_MSG,position);
                    playVoice(position, filePath, receiveVoiceImg);
                }
            });
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
        final String translateFilePath = data.getTranslateFilePath();
        if(!TextUtils.isEmpty(filePath)){
            voiceImgTranslation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (voiceImgTranslation == clickImageView) {
                        MediaManager.getInstance().setTransVoice(false);
                    } else {
                        MediaManager.getInstance().setTransVoice(true);
                        clickImageView = voiceImgTranslation;
                    }
                    playVoice(position, translateFilePath, voiceImgTranslation);
                }
            });
        }

        if (data.getIsReTrans()) {
            llMsg.setBackgroundResource(R.drawable.chat_re_translation_show);
        } else {
            if(data.getIsChoiceTranslate()){
                llMsg.setBackgroundResource(R.drawable.chat_choice_translate_display_left);
            }else{
                llMsg.setBackgroundResource(R.drawable.chat_translate_display_left);
            }
        }
        reTranslateMsg(llMsg,position,iView,data,comeForm);
    }

    @Override
    public void onRelease() {
    }

    private void playVoice(int position, String filePath, final ImageView imageView) {
        boolean isPlaying = MediaManager.getInstance().isPlaying();
        if (isPlaying) {
            int pos = MediaManager.getInstance().getPosition();
            if (pos == position) {
                if (imageView == receiveVoiceImg) {
                    AnimationDrawable drawable = (AnimationDrawable) voiceImgTranslation.getBackground();
                    drawable.stop();
                    voiceImgTranslation.setBackgroundResource(R.mipmap.voice_play_green_three);
                } else {
                    AnimationDrawable drawable = (AnimationDrawable) receiveVoiceImg.getBackground();
                    drawable.stop();
                    receiveVoiceImg.setBackgroundResource(R.mipmap.voice_play_black_three);
                }
                if (!MediaManager.getInstance().isTransVoice()) {
                    MediaManager.getInstance().reset();
                    MediaManager.getInstance().setPosition(-1);
                    MediaManager.getInstance().setTransVoice(false);
                    return;
                } else {

                    MediaManager.getInstance().reset();
                    MediaManager.getInstance().setPosition(-1);
                    MediaManager.getInstance().setTransVoice(true);
                }
            } else {
                sentMsg(STOP_VOICE,pos);
            }
        }
        if (imageView == receiveVoiceImg) {
            imageView.setBackgroundResource(R.drawable.play_voice_left_black);
        }else{
            imageView.setBackgroundResource(R.drawable.play_voice_left_green);
        }
        AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
        drawable.start();
        MediaManager.getInstance().setPosition(position);
        MediaManager.getInstance().playSound(filePath, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (imageView == receiveVoiceImg) {
                    imageView.setBackgroundResource(R.mipmap.voice_play_black_three);
                } else {
                    imageView.setBackgroundResource(R.mipmap.voice_play_green_three);
                }
                MediaManager.getInstance().reset();
                MediaManager.getInstance().setPosition(-1);
                MediaManager.getInstance().setTransVoice(false);
            }
        }, new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

            }
        });
    }

    private void sentMsg(int what,int pos){
        Message message = Message.obtain(iView);
        message.what = what;
        message.arg1 = pos;
        message.dispatchToIView();
    }
}
