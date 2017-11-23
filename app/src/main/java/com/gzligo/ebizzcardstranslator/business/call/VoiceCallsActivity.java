package com.gzligo.ebizzcardstranslator.business.call;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.image.transformation.BlurTransformation;
import com.gzligo.ebizzcardstranslator.image.transformation.CustomShapeTransformation;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.manager.WebRtcManager;
import com.gzligo.ebizzcardstranslator.manager.WebRtcService;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslatorSelectedBean;
import com.gzligo.ebizzcardstranslator.utils.GlideUtils;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;

import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

public class VoiceCallsActivity extends BaseActivity<VoiceCallsPresenter> {

    @BindView(R.id.people_img_bg) ImageView peopleImgBg;
    @BindView(R.id.shrink_down_img) ImageView shrinkDownImg;
    @BindView(R.id.call_title_tv) TextView callTitleTv;
    @BindView(R.id.people_img) ImageView peopleImg;
    @BindView(R.id.people_name_tv) TextView peopleNameTv;
    @BindView(R.id.prompt_tv) TextView promptTv;
    @BindView(R.id.language_tv) TextView languageTv;
    @BindView(R.id.call_reject_img) ImageView callRejectImg;
    @BindView(R.id.call_reject_rl) LinearLayout callRejectRl;
    @BindView(R.id.call_accept_img) ImageView callAcceptImg;
    @BindView(R.id.call_accept_rl) LinearLayout callAcceptRl;
    @BindView(R.id.call_mute_img) CheckBox callMuteImg;
    @BindView(R.id.call_mute_rl) LinearLayout callMuteRl;
    @BindView(R.id.call_hang_up_img) ImageView callHangUpImg;
    @BindView(R.id.call_hang_up_rl) LinearLayout callHangUpRl;
    @BindView(R.id.call_hands_free_img) CheckBox callHandsFreeImg;
    @BindView(R.id.call_hands_free_rl) LinearLayout callHandsFreeRl;
    @BindView(R.id.record_voice_time) Chronometer recordVoiceTime;
    private static final int FINISH_ORDER = 0x30;
    private static final int ACCEPT_ORDER = 0x31;
    private static final int REJECT_ORDER = 0x32;
    private static final int GET_LANGUAGE = 0x33;
    private static final int TRANS_OUT = 0x34;
    private static final int TRANS_CANCELLED = 0x35;
    private static final int TRANS_CONNECT_TIME_OUT = 0x36;
    private String orderId;
    private AudioManager audioManager;
    private TravelTranslatorSelectedBean travelTranslatorSelectedBean;
    private String comeFrom;
    private String voipToken;
    private long timeInfo;
    private String userId;

    @Override
    public VoiceCallsPresenter createPresenter() {
        return new VoiceCallsPresenter(new VoiceCallsRepository(), this);
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_voice_calls;
    }

    @Override
    public boolean supportSlideBack() {
        return false;
    }

    @Override
    public void initData() {
        travelTranslatorSelectedBean = (TravelTranslatorSelectedBean) getIntent().getSerializableExtra("TRAVEL_TRANS_ORDER");
        userId = travelTranslatorSelectedBean.getFromUserId();
        comeFrom = getIntent().getExtras().getString("COME_FROM");
        switch (comeFrom){
            case "CallOrderFragment":
                promptTv.setText(getResources().getString(R.string.voice_calls_connect_voip_server));
                voipToken = getIntent().getExtras().getString("VOIP_TOKEN");
                connectedView();
                getPresenter().timeStart();
                connectWebRtcServer(voipToken);
                break;
            case "System_server":
                waitConnectView();
                break;
            case "WebRtcService":
                if (WebRtcManager.getInstance().getServiceFloatWindow() != null) {
                    WebRtcManager.getInstance().getServiceFloatWindow().hide();
                }
                timeInfo = getIntent().getExtras().getLong("TIME_INFO");
                String language = getIntent().getStringExtra("LANGUAGE");
                if (timeInfo == 0) {
                    if (WebRtcManager.getInstance().getServiceChronometer() != null) {
                        timeInfo = WebRtcManager.getInstance().getServiceChronometer().getBase();
                    }
                }
                recordVoiceTime.setVisibility(View.VISIBLE);
                promptTv.setVisibility(View.INVISIBLE);
                recordVoiceTime.setBase(timeInfo);
                recordVoiceTime.start();
                connectedView();
                languageTv.setVisibility(View.VISIBLE);
                languageTv.setText(language);
                shrinkDownImg.setVisibility(View.VISIBLE);
                break;
        }
        orderId = travelTranslatorSelectedBean.getSessionId();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        WebRtcManager.getInstance().setVoiceCallClientID(userId);
    }

    @Override
    public void initViews() {
        GlideUtils.initTransformationImg(peopleImgBg,travelTranslatorSelectedBean.getPortrait(),new BlurTransformation(this, 23, 4),R.mipmap.default_people_img);
        GlideUtils.initTransformationImg(peopleImg,travelTranslatorSelectedBean.getPortrait(),new CustomShapeTransformation(this, 2, !true),R.mipmap.default_people_img);
        peopleNameTv.setText(travelTranslatorSelectedBean.getUserName());
        TreeMap<Integer, LanguagesBean> treeMap = CommonBeanManager.getInstance().getTreeMap();
        if (null == treeMap) {
            getPresenter().getLanguageList();
        } else {
            String fromLanguage = LanguageUtils.getLanguageName(travelTranslatorSelectedBean.getLanguageIds().get(0),treeMap);
            String toLanguage = LanguageUtils.getLanguageName(travelTranslatorSelectedBean.getLanguageIds().get(1),treeMap);
            languageTv.setText(fromLanguage + " | " + toLanguage);
        }
    }

    @Override
    public void initEvents() {
    }

    @OnClick({R.id.call_mute_img, R.id.call_hang_up_rl, R.id.call_hands_free_img, R.id.call_accept_rl, R.id.call_reject_rl,R.id.shrink_down_img})
    void click(View view) {
        switch (view.getId()) {
            case R.id.call_mute_img://静音
                boolean isChecked = callMuteImg.isChecked();
                WebRtcManager.getInstance().mute(isChecked);
                break;
            case R.id.call_hang_up_rl://挂断
                getPresenter().requestTravelTuserFinish(orderId,"0");
                break;
            case R.id.call_hands_free_img://免提
                isChecked = callHandsFreeImg.isChecked();
                audioManager.setSpeakerphoneOn(isChecked);
                break;
            case R.id.call_accept_rl://接听
                requestPermission();
                break;
            case R.id.call_reject_rl://拒绝
                getPresenter().requestTravelTuserReject(orderId);
                break;
            case R.id.shrink_down_img:
                Intent intent = new Intent(VoiceCallsActivity.this, WebRtcService.class);
                intent.putExtra("TRAVEL_TRANS_ORDER", travelTranslatorSelectedBean);
                intent.putExtra(CommonConstants.INFO, recordVoiceTime.getBase());
                intent.putExtra("LANGUAGE",languageTv.getText().toString());
                startService(intent);
                finish();
                break;
        }
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what) {
            case FINISH_ORDER:
                recordVoiceTime.stop();
                getPresenter().cancelTimer();
                WebRtcManager.getInstance().handUp();
                finish();
                break;
            case ACCEPT_ORDER:
                String rtcToken = message.str;
                getPresenter().timeStart();
                connectWebRtcServer(rtcToken);
                break;
            case REJECT_ORDER:
                finish();
                break;
            case GET_LANGUAGE:
                TreeMap<Integer, LanguagesBean> treeMap = (TreeMap<Integer, LanguagesBean>) message.obj;
                String fromLanguage = LanguageUtils.getLanguageName(travelTranslatorSelectedBean.getLanguageIds().get(0),treeMap);
                String toLanguage = LanguageUtils.getLanguageName(travelTranslatorSelectedBean.getLanguageIds().get(1),treeMap);
                languageTv.setText(fromLanguage + " | " + toLanguage);
                break;
            case TRANS_OUT:
                if(null!=recordVoiceTime){
                    recordVoiceTime.stop();
                }
                getPresenter().cancelTimer();
                finish();
                break;
            case TRANS_CANCELLED:
                finish();
                break;
            case TRANS_CONNECT_TIME_OUT:
                getPresenter().requestTravelTuserFinish(orderId,"0");
                break;
        }
    }

    private void waitConnectView(){
        shrinkDownImg.setVisibility(View.GONE);
        callAcceptRl.setVisibility(View.VISIBLE);
        callRejectRl.setVisibility(View.VISIBLE);
        callMuteRl.setVisibility(View.GONE);
        callHandsFreeRl.setVisibility(View.GONE);
        callHangUpRl.setVisibility(View.GONE);
        recordVoiceTime.setVisibility(View.GONE);
        promptTv.setVisibility(View.VISIBLE);
        languageTv.setVisibility(View.VISIBLE);
    }

    private void connectedView(){
        callAcceptRl.setVisibility(View.GONE);
        callRejectRl.setVisibility(View.GONE);
        callMuteRl.setVisibility(View.VISIBLE);
        callHandsFreeRl.setVisibility(View.VISIBLE);
        callHangUpRl.setVisibility(View.VISIBLE);
        languageTv.setVisibility(View.GONE);
    }

    private void connectWebRtcServer(String rtcToken){
        WebRtcManager.getInstance().connectWebRtc(rtcToken, new WebRtcManager.OnWebRtcConnectListener() {
            @Override
            public void webRtcConnected() {
                getPresenter().cancelTimer();
                promptTv.setVisibility(View.INVISIBLE);
                recordVoiceTime.setVisibility(View.VISIBLE);
                shrinkDownImg.setVisibility(View.VISIBLE);
                callMuteRl.setVisibility(View.VISIBLE);
                callHandsFreeRl.setVisibility(View.VISIBLE);
                languageTv.setVisibility(View.VISIBLE);
                recordVoiceTime.setBase(SystemClock.elapsedRealtime());
                recordVoiceTime.start();
            }
        });
    }

    public void requestPermission() {
        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
        requestPermission(1, permissions, new Runnable() {
            @Override
            public void run() {
                callAcceptRl.setVisibility(View.GONE);
                callRejectRl.setVisibility(View.GONE);
                callMuteRl.setVisibility(View.GONE);
                callHandsFreeRl.setVisibility(View.GONE);
                callHangUpRl.setVisibility(View.VISIBLE);
                recordVoiceTime.setVisibility(View.GONE);
                promptTv.setVisibility(View.VISIBLE);
                promptTv.setText(getResources().getString(R.string.voice_calls_connect_voip_server));
                getPresenter().requestTravelTuserAccept(orderId);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 禁用back键
    }
}
