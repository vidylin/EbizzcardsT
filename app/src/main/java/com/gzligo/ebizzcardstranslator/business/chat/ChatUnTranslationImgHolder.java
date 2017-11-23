package com.gzligo.ebizzcardstranslator.business.chat;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
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
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.utils.GlideUtils;
import com.gzligo.ebizzcardstranslator.utils.ScreenUtils;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import butterknife.BindView;

import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.ON_CHOICE_ITEM_TO_TRANS;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.PRE_IMAGE;

/**
 * Created by Lwd on 2017/6/12.
 */

public class ChatUnTranslationImgHolder extends BaseHolder<ChatMessageBean> {
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
    @BindView(R.id.private_user_img_name) TextView privateUserImgName;
    @BindView(R.id.img_down_progress) ProgressBar imgDownProgress;
    private IView iView;
    private NewTransOrderBean newTransOrderBean;
    private String comeForm;

    public ChatUnTranslationImgHolder(View itemView, IView iView, NewTransOrderBean newTransOrderBean, String comeForm) {
        super(itemView);
        this.iView = iView;
        this.newTransOrderBean = newTransOrderBean;
        this.comeForm = comeForm;
    }

    @Override
    public void setData(final ChatMessageBean data, final int position) {
        chatTimeTv.setText(TimeUtils.getHM(Long.parseLong(data.getMsgTime())));
        voiceLl.setVisibility(View.GONE);
        if (!data.getMsgIsTrans()) {
            chatCheck.setVisibility(View.VISIBLE);
        } else {
            chatCheck.setVisibility(View.GONE);
        }
        GlideUtils.loadPeopleImage(data.getFromId(), peopleImg,newTransOrderBean);
        longClickHeadPortrait(position,newTransOrderBean.getFromUserId(),peopleImg,iView);
        if (!TextUtils.isEmpty(data.getFilePath())) {
            receiveImg.setVisibility(View.VISIBLE);
            loadImage(data.getFilePath(), receiveImg, data.getImageSize());
            imgDownProgress.setVisibility(View.GONE);
        }else{
            receiveImg.setVisibility(View.GONE);
            imgDownProgress.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(data.getMsgDescription())) {
            unTranslateContentTxtTv.setVisibility(View.VISIBLE);
            unTranslateContentTxtTv.setText(data.getMsgDescription());
        } else {
            unTranslateContentTxtTv.setVisibility(View.GONE);
        }
        switch (data.getTranslateType()) {
            case ChatConstants.COMMON_IMG_CHAT:
                privateUserImgName.setVisibility(View.GONE);
                Drawable img_off;
                Resources res = AppManager.get().getApplication().getResources();
                if (data.getIsChoiceTranslate()) {
                    img_off = res.getDrawable(R.mipmap.chat_untranslated_press);
                    img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                    chatCheck.setCompoundDrawables(img_off, null, null, null);
                    chatCheck.setClickable(false);
                    chatCheck.setChecked(true);
                } else {
                    img_off = res.getDrawable(R.mipmap.chat_untranslated_normal);
                    img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                    chatCheck.setCompoundDrawables(img_off, null, null, null);
                    chatCheck.setClickable(true);
                    chatCheck.setChecked(false);
                }
                chatCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choiceItem(position, data.getMsgId());
                    }
                });
                break;
            case ChatConstants.PRIVATE_IMG_CHAT:
                privateUserImgName.setVisibility(View.VISIBLE);
                privateUserImgName.setText("@" + data.getPrivateToNickname());
                chatCheck.setVisibility(View.GONE);
                break;
        }

        receiveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = Message.obtain(iView);
                message.what = PRE_IMAGE;
                message.arg1 = position;
                message.dispatchToIView();
            }
        });
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

    private void loadImage(String url, final ImageView imageView, String imgSize) {
        int width = ChatConstants.CHAT_IMG_WIDTH;
        int height = ScreenUtils.getImageViewHeight(imgSize, width);
        GlideUtils.showImg(imageView,url,new CustomShapeTransformation(AppManager.get().getApplication(), ChatConstants.CHAT_IMG_SHAPE_TRANS_FORMATION)
                ,width,height);
    }
}
