package com.gzligo.ebizzcardstranslator.business.chat;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.mqtt.protobuf.MQTTProtobufMsg;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.utils.CommonUtils;
import com.gzligo.ebizzcardstranslator.utils.GlideUtils;
import com.gzligo.ebizzcardstranslator.utils.MediaPlayerCallbackUtils;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;
import com.waynell.videolist.visibility.items.ListItem;
import com.waynell.videolist.widget.TextureVideoView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.PRE_VIDEO;

/**
 * Created by Lwd on 2017/6/12.
 */

public class ChatTranslationVideoHolder extends BaseHolder<ChatMessageBean> implements ListItem {
    @BindView(R.id.chat_time_tv) TextView chatTimeTv;
    @BindView(R.id.people_img) ImageView peopleImg;
    @BindView(R.id.video_player) TextureVideoView videoPlayer;
    @BindView(R.id.voice_img_translation) ImageView voiceImgTranslation;
    @BindView(R.id.translate_content_txt_tv) TextView translateContentTxtTv;
    @BindView(R.id.ll_msg) LinearLayout llMsg;
    @BindView(R.id.video_down_progress) ProgressBar videoDownProgress;
    @BindView(R.id.play_video_img) ImageView playVideoImg;
    @BindView(R.id.pre_img) ImageView preImageView;
    @BindView(R.id.video_line) View videoLine;
    private String videoLocalPath;
    private IView iView;
    private NewTransOrderBean newTransOrderBean;
    private String comeForm;
    private Map<String, Bitmap> videoCoverMap;
    private String videoSize;

    public ChatTranslationVideoHolder(View itemView, NewTransOrderBean newTransOrderBean, IView iView, String comeForm) {
        super(itemView);
        this.newTransOrderBean = newTransOrderBean;
        this.iView = iView;
        this.comeForm = comeForm;
        if (null == videoCoverMap) {
            videoCoverMap = new HashMap<>();
        }
    }

    @Override
    public void setData(final ChatMessageBean data, final int position) {
        chatTimeTv.setText(TimeUtils.getHM(Long.parseLong(data.getMsgTime())));
        videoLocalPath = data.getFilePath();
        videoSize = data.getImageSize();
        chatTimeTv.setText(TimeUtils.getHM(Long.parseLong(data.getMsgTime())));
        GlideUtils.loadPeopleImage(data.getFromId(), peopleImg,newTransOrderBean);
        longClickHeadPortrait(position,newTransOrderBean.getFromUserId(),peopleImg,iView);
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
        if(MQTTProtobufMsg.LGContentType.VIDEO_VALUE == data.getType()){
            translateContentTxtTv.setVisibility(View.GONE);
            videoLine.setVisibility(View.GONE);
        }else{
            translateContentTxtTv.setVisibility(View.VISIBLE);
            videoLine.setVisibility(View.VISIBLE);
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
        llMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPlayer.stop();
                Message message = Message.obtain(iView);
                message.what = PRE_VIDEO;
                message.arg1 = position;
                message.dispatchToIView();
            }
        });

        String videoThumbnailPath = data.getVideoThumbnailPath();
        if(TextUtils.isEmpty(videoLocalPath)){
            videoDownProgress.setVisibility(View.VISIBLE);
            if(null==videoThumbnailPath){
                preImageView.setVisibility(View.GONE);
            }else{
                preImageView.setVisibility(View.VISIBLE);
                CommonUtils.loadVideoImage(videoThumbnailPath,preImageView,data.getImageSize(),videoPlayer);
            }
        }else {
            preImageView.setVisibility(View.GONE);
        }
        playVideo();
    }

    @Override
    public void onRelease() {
    }

    @Override
    public void setActive(View view, int i) {
    }

    @Override
    public void deactivate(View view, int i) {
        videoPlayer.stop();
    }

    private void videoListener(final TextureVideoView textureVideoView) {
        textureVideoView.setMediaPlayerCallback(new MediaPlayerCallbackUtils() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                try{
                    textureVideoView.mute();
                }catch (IllegalStateException e){
                    Log.e("IllegalStateException=",e.toString());
                    textureVideoView.start();
                    textureVideoView.stop();
                }
            }

            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                videoDownProgress.setVisibility(View.GONE);
                preImageView.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                textureVideoView.stop();
                return false;
            }
        });

    }

    public void playVideo(){
        if (null != videoLocalPath&&!videoPlayer.isPlaying()) {
            playVideoImg.setVisibility(View.GONE);
            videoDownProgress.setVisibility(View.VISIBLE);
            videoPlayer.setVideoPath(videoLocalPath);
            videoPlayer.start();
            videoListener(videoPlayer);
            if(null!=videoSize){
                CommonUtils.loadVideoSize(preImageView,videoSize,videoPlayer);
            }
        }
    }
}
