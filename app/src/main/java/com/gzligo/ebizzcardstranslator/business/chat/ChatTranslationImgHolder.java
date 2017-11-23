package com.gzligo.ebizzcardstranslator.business.chat;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.constants.FileConstants;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;
import com.gzligo.ebizzcardstranslator.persistence.ProductExtraInfo;
import com.gzligo.ebizzcardstranslator.utils.CommonUtils;
import com.gzligo.ebizzcardstranslator.utils.CurrencyTypeUtil;
import com.gzligo.ebizzcardstranslator.utils.GlideUtils;
import com.gzligo.ebizzcardstranslator.utils.ScreenUtils;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import butterknife.BindView;

import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.PRE_IMAGE;

/**
 * Created by Lwd on 2017/6/12.
 */

public class ChatTranslationImgHolder extends BaseHolder<ChatMessageBean> {
    @BindView(R.id.chat_time_tv) TextView chatTimeTv;
    @BindView(R.id.voice_img) ImageView voiceImg;
    @BindView(R.id.un_translate_content_txt_tv) TextView unTranslateContentTxtTv;
    @BindView(R.id.voice_img_translation) ImageView voiceImgTranslation;
    @BindView(R.id.translate_content_txt_tv) TextView translateContentTxtTv;
    @BindView(R.id.ll_msg) LinearLayout llMsg;
    @BindView(R.id.people_img) ImageView peopleImg;
    @BindView(R.id.img_down_progress) ProgressBar imgDownProgress;
    @BindView(R.id.product_line) View productLine;
    @BindView(R.id.product_price_tv) TextView productPriceTv;
    private NewTransOrderBean newTransOrderBean;
    private IView iView;
    private String comeForm;

    public ChatTranslationImgHolder(View itemView, NewTransOrderBean newTransOrderBean, IView iView, String comeForm) {
        super(itemView);
        this.newTransOrderBean = newTransOrderBean;
        this.iView = iView;
        this.comeForm = comeForm;
    }

    @Override
    public void setData(final ChatMessageBean data, final int position) {
        chatTimeTv.setText(TimeUtils.getHM(Long.parseLong(data.getMsgTime())));
        GlideUtils.loadPeopleImage(data.getFromId(), peopleImg,newTransOrderBean);
        longClickHeadPortrait(position,newTransOrderBean.getFromUserId(),peopleImg,iView);
        if (!TextUtils.isEmpty(data.getMsgDescription())) {
            unTranslateContentTxtTv.setVisibility(View.VISIBLE);
            unTranslateContentTxtTv.setText(data.getMsgDescription());
        } else {
            unTranslateContentTxtTv.setVisibility(View.GONE);
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
                String detail = data.getTranslateContent().trim();
                if(null!=detail&&detail.length()>0){
                    translateContentTxtTv.setText(detail);
                    translateContentTxtTv.setTextColor(AppManager.get().getApplication().getResources().getColor(R.color.green));
                }else{
                    translateContentTxtTv.setVisibility(View.GONE);
                }
                break;
        }
        final String filePath = data.getTranslateFilePath();
        if(!TextUtils.isEmpty(filePath)){
            voiceImgTranslation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonUtils.playVoice(position, filePath, voiceImgTranslation,iView);
                }
            });
        }
        voiceImgTranslation.setBackgroundResource(R.mipmap.voice_play_green_three);
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
                Message message = Message.obtain(iView);
                message.what = PRE_IMAGE;
                message.arg1 = position;
                message.dispatchToIView();
            }
        });
        if (!TextUtils.isEmpty(data.getFilePath())) {
            imgDownProgress.setVisibility(View.GONE);
            voiceImg.setVisibility(View.VISIBLE);
            loadImage(data.getFilePath(), voiceImg, data.getImageSize(), data.getTranslateContent(), data.getMsgDescription(),data);
        }else {
            imgDownProgress.setVisibility(View.VISIBLE);
            voiceImg.setVisibility(View.GONE);
            productPriceTv.setVisibility(View.GONE);
        }
        if(ChatConstants.COMMON_PRODUCT_CHAT==data.getTranslateType()){
            translateContentTxtTv.setTextColor(AppManager.get().getApplication().getResources().getColor(R.color.black));
            productLine.setVisibility(View.GONE);
        }else{
            productLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRelease() {
    }

    private void loadImage(String url, final ImageView imageView, final String imgSize, final String textViewStr,
                           final String descTextViewStr, ChatMessageBean chatMsg) {
        int width = ScreenUtils.getImageViewWidth(translateContentTxtTv, unTranslateContentTxtTv, textViewStr, descTextViewStr);
        if(TextUtils.isEmpty(imgSize)){
            if(!TextUtils.isEmpty(url)&&url.endsWith(FileConstants.FILE_SUFFIX_MP4)){
                GlideUtils.getThumbnail(url,width,imageView);
            }else{
                return;
            }
        }else{
            int height = ScreenUtils.getImageViewHeight(imgSize, width);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            params.height = height;
            params.width = width;
            imageView.setLayoutParams(params);
            GlideUtils.showImg(imageView,url,new CustomShapeTransformation(AppManager.get().getApplication(), ChatConstants.CHAT_IMG_SHAPE_TRANS_FORMATION)
                    ,width,height);
        }
        if(chatMsg.getType()==6){
            String extraInfo = chatMsg.getExtraInfo();
            if(!TextUtils.isEmpty(extraInfo)){
                Gson gson = new Gson();
                ProductExtraInfo productExtraInfo = gson.fromJson(extraInfo, ProductExtraInfo.class);
                if(null!=productExtraInfo){
                    ViewGroup.LayoutParams layoutParams = productPriceTv.getLayoutParams();
                    layoutParams.width = imageView.getLayoutParams().width;
                    productPriceTv.setLayoutParams(layoutParams);
                    productPriceTv.setVisibility(View.VISIBLE);
                    String currencyType = CurrencyTypeUtil.getCurrencySymbol(AppManager.get().getApplication(),productExtraInfo.getCurrency_id());
                    productPriceTv.setText(currencyType+" "+productExtraInfo.getPrice());
                }
            }
        }else{
            productPriceTv.setVisibility(View.GONE);
        }
    }

}
