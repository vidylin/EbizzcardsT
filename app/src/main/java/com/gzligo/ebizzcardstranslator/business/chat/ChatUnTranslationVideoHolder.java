package com.gzligo.ebizzcardstranslator.business.chat;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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

import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.ON_CHOICE_ITEM_TO_TRANS;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.PRE_VIDEO;

/**
 * Created by Lwd on 2017/6/12.
 */

public class ChatUnTranslationVideoHolder extends BaseHolder<ChatMessageBean> implements ListItem {
    @BindView(R.id.chat_time_tv) TextView chatTimeTv;
    @BindView(R.id.people_img) ImageView peopleImg;
    @BindView(R.id.video_player) TextureVideoView videoPlayer;
    @BindView(R.id.ll_msg) LinearLayout llMsg;
    @BindView(R.id.report_check) CheckBox reportCheck;
    @BindView(R.id.chat_check) CheckBox chatCheck;
    @BindView(R.id.private_to_nick_name_tv) TextView privateToNickNameTv;
    @BindView(R.id.video_down_progress) ProgressBar videoDownProgress;
    @BindView(R.id.pre_img) ImageView preImageView;
    @BindView(R.id.play_video_img) ImageView playVideoImg;
    private String videoLocalPath;
    private IView iView;
    private NewTransOrderBean newTransOrderBean;
    private String comeForm;
    private Map<String, Bitmap> videoCoverMap;
    private String videoSize;
    private String videoThumbnailPath;

    public ChatUnTranslationVideoHolder(View itemView, IView iView, NewTransOrderBean newTransOrderBean, String comeForm) {
        super(itemView);
        this.iView = iView;
        this.newTransOrderBean = newTransOrderBean;
        this.comeForm = comeForm;
        if (null == videoCoverMap) {
            videoCoverMap = new HashMap<>();
        }
    }

    @Override
    public void setData(final ChatMessageBean data, final int position) {
        videoThumbnailPath = data.getVideoThumbnailPath();
        videoSize = data.getImageSize();
        if (!TextUtils.isEmpty(data.getFilePath())) {
            videoLocalPath = data.getFilePath();
        }else{
            videoDownProgress.setVisibility(View.VISIBLE);
            preImageView.setVisibility(View.VISIBLE);
            if(null==videoSize){
                CommonUtils.loadVideoImage(data.getFileUrl(),preImageView,data.getImageSize(),videoPlayer);
            }else{
                CommonUtils.loadVideoImage(videoThumbnailPath,preImageView,data.getImageSize(),videoPlayer);
            }
        }
        chatTimeTv.setText(TimeUtils.getHM(Long.parseLong(data.getMsgTime())));
        GlideUtils.loadPeopleImage(data.getFromId(), peopleImg,newTransOrderBean);
        longClickHeadPortrait(position,newTransOrderBean.getFromUserId(),peopleImg,iView);
        if (!data.getMsgIsTrans()) {
            chatCheck.setVisibility(View.VISIBLE);
        } else {
            chatCheck.setVisibility(View.GONE);
        }
        if (data.getIsChoiceTranslate()) {
            Drawable img_off;
            Resources res = AppManager.get().getApplication().getResources();
            img_off = res.getDrawable(R.mipmap.chat_untranslated_press);
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            chatCheck.setCompoundDrawables(img_off, null, null, null);
            chatCheck.setClickable(false);
            chatCheck.setChecked(true);
        } else {
            Drawable img_off;
            Resources res = AppManager.get().getApplication().getResources();
            img_off = res.getDrawable(R.mipmap.chat_untranslated_normal);
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            chatCheck.setCompoundDrawables(img_off, null, null, null);
            chatCheck.setClickable(true);
            chatCheck.setChecked(false);
        }
        switch (data.getTranslateType()) {
            case ChatConstants.COMMON_VIDEO_CHAT:
                privateToNickNameTv.setVisibility(View.GONE);
                chatCheck.setVisibility(View.VISIBLE);
                chatCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choiceItem(position, data.getMsgId());
                    }
                });
                break;
            case ChatConstants.PRIVATE_VIDEO_CHAT:
                privateToNickNameTv.setVisibility(View.VISIBLE);
                privateToNickNameTv.setText("@" + data.getPrivateToNickname());
                chatCheck.setVisibility(View.GONE);
                break;
        }
        videoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPlayer.stop();
                preImageView.setVisibility(View.VISIBLE);
                Message message = Message.obtain(iView);
                message.what = PRE_VIDEO;
                message.arg1 = position;
                message.dispatchToIView();
            }
        });
        if (comeForm.equals(ChatConstants.COME_FROM_HISTORY)) {
            chatCheck.setVisibility(View.GONE);
        }
        playVideo();
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
                videoPlayer.stop();
                return false;
            }
        });

    }

    public void playVideo(){
        if (null != videoLocalPath&&!videoPlayer.isPlaying()) {
            videoListener(videoPlayer);
            playVideoImg.setVisibility(View.GONE);
            videoPlayer.setVideoPath(videoLocalPath);
            videoPlayer.start();
            videoDownProgress.setVisibility(View.VISIBLE);
            if(null!=videoSize){
                CommonUtils.loadVideoSize(preImageView,videoSize,videoPlayer);
            }
        }
    }
}
