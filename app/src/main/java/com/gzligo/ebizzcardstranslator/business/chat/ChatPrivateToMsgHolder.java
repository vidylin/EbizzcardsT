package com.gzligo.ebizzcardstranslator.business.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
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
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.utils.GlideUtils;
import com.gzligo.ebizzcardstranslator.utils.MediaManager;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import butterknife.BindView;

import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.STOP_VOICE;

/**
 * Created by Lwd on 2017/6/19.
 */

public class ChatPrivateToMsgHolder extends BaseHolder<ChatMessageBean> {
    @BindView(R.id.chat_time_tv) TextView chatTimeTv;
    @BindView(R.id.chat_private_content_txt_tv) TextView chatPrivateContentTxtTv;
    @BindView(R.id.voice_img) ImageView voiceImg;
    @BindView(R.id.to_people_tv) TextView toPeopleTv;
    @BindView(R.id.ll_msg) LinearLayout llMsg;
    @BindView(R.id.chat_people_img) ImageView chatPeopleImg;
    @BindView(R.id.to_people_content) TextView toPeopleContent;
    private IView iView;

    public ChatPrivateToMsgHolder(View itemView, IView iView) {
        super(itemView);
        this.iView = iView;
    }

    @Override
    public void setData(final ChatMessageBean data, final int position) {
        chatTimeTv.setText(TimeUtils.getHM(Long.parseLong(data.getMsgTime())));
        SharedPreferences sharedPreferences = AppManager.get()
                .getApplication()
                .getSharedPreferences(CommonConstants.USER_INFO_PRE_NAME, Context.MODE_PRIVATE);
        String url = sharedPreferences.getString(CommonConstants.USER_PORTRAIT_ID, "");
        GlideUtils.initFullScreenImg(chatPeopleImg,url,
                new CustomShapeTransformation(AppManager.get().getApplication(), 39, !true)
                ,R.mipmap.default_head_portrait);
        switch (data.getSendMsgType()) {
            case ChatConstants.VOICE_PRIVATE:
                toPeopleContent.setVisibility(View.GONE);
                voiceImg.setVisibility(View.VISIBLE);
                toPeopleTv.setVisibility(View.VISIBLE);
                chatPrivateContentTxtTv.setVisibility(View.VISIBLE);
                chatPrivateContentTxtTv.setText(data.getTranslateVoiceLong() + "\"");
                toPeopleTv.setText("@" + data.getFromName());
                voiceImg.setBackgroundResource(R.mipmap.voice_play_black_left_three);
                final String filePath = data.getTranslateFilePath();
                if(!TextUtils.isEmpty(filePath)){
                    voiceImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            playVoice(position,filePath , voiceImg);
                        }
                    });
                }
                break;
            case ChatConstants.TXT_PRIVATE:
                toPeopleContent.setVisibility(View.VISIBLE);
                toPeopleTv.setVisibility(View.GONE);
                String userName = "@" + data.getFromName().replaceAll("\r|\n", "");
                voiceImg.setVisibility(View.GONE);
                chatPrivateContentTxtTv.setVisibility(View.GONE);
                String content = AppManager.get().getApplication().getResources().getString(R.string.space) + data.getTranslateContent();
                String str = userName + content.replaceAll("\r|\n", "");
                int bsStart = str.indexOf(userName);
                int bend = bsStart + userName.length();
                SpannableStringBuilder style=new SpannableStringBuilder(str);
                style.setSpan(new BackgroundColorSpan(AppManager.get().getApplication().getResources().getColor(R.color.chat_head_bg)),bsStart,bend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                toPeopleContent.setText(style);
                break;
        }

    }

    @Override
    public void onRelease() {
    }

    private void playVoice(int position, String filePath, final ImageView imageView) {
        imageView.setBackgroundResource(R.drawable.play_voice_right_black);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
        drawable.start();
        boolean isPlaying = MediaManager.getInstance().isPlaying();
        if (isPlaying) {
            int pos = MediaManager.getInstance().getPosition();
            if (pos == position) {
                imageView.setBackgroundResource(R.mipmap.voice_play_black_left_three);
                MediaManager.getInstance().reset();
                MediaManager.getInstance().setPosition(-1);
                return;
            } else {
                Message message = Message.obtain(iView);
                message.what = STOP_VOICE;
                message.obj = pos;
                message.dispatchToIView();
            }
        }
        MediaManager.getInstance().setPosition(position);
        MediaManager.getInstance().playSound(filePath, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                imageView.setBackgroundResource(R.mipmap.voice_play_black_left_three);
                MediaManager.getInstance().reset();
                MediaManager.getInstance().setPosition(-1);
            }
        }, new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
            }
        });
    }
}
