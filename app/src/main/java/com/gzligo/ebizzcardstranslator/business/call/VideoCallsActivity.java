package com.gzligo.ebizzcardstranslator.business.call;

import android.Manifest;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.xgfjyw.webrtcclient.voip.SurfaceViewRenderer;
import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.common.DraggableLayout;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.manager.CommonBeanManager;
import com.gzligo.ebizzcardstranslator.manager.VoipOverlayService;
import com.gzligo.ebizzcardstranslator.persistence.LanguagesBean;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslatorSelectedBean;
import com.gzligo.ebizzcardstranslator.utils.LanguageUtils;
import com.gzligo.ebizzcardstranslator.utils.UIUtils;

import org.webrtc.RendererCommon;

import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;

public class VideoCallsActivity extends BaseActivity<VoiceCallsPresenter> {

    @BindView(R.id.voip_video_fullscreen_view) SurfaceViewRenderer voipVideoFullscreenView;
    @BindView(R.id.voip_video_overlay_view) SurfaceViewRenderer voipVideoOverlayView;
    @BindView(R.id.voip_video_minimize_view) ImageView voipVideoMinimizeView;
    @BindView(R.id.voip_video_contacts_name) TextView voipVideoContactsName;
    @BindView(R.id.voip_video_chronometer) Chronometer voipVideoChronometer;
    @BindView(R.id.voip_video_camera_switch_view) TextView voipVideoCameraSwitchView;
    @BindView(R.id.voip_video_hang_up_view) TextView voipVideoHangUpView;
    @BindView(R.id.voip_video_mute_view) CheckBox voipVideoMuteView;
    @BindView(R.id.voip_video_control_bar_view) LinearLayout voipVideoControlBarView;
    @BindView(R.id.voip_video_control_view) RelativeLayout voipVideoControlView;
    @BindView(R.id.voip_video_container) DraggableLayout voipVideoContainer;
    @BindView(R.id.prompt_tv) TextView promptTv;
    @BindView(R.id.language_tv) TextView languageTv;
    @BindView(R.id.voip_video_reject_view) TextView voipVideoRejectView;
    @BindView(R.id.voip_video_accept_view) TextView voipVideoAcceptView;

    public static final String EXTRA_IS_LOCAL_RENDER_IN_FULLSCREEN = "extra_is_local_render_in_fullscreen";
    private static final int GET_LANGUAGE = 0x33;
    private static final int REJECT_ORDER = 0x32;
    private static final int ACCEPT_ORDER = 0x31;
    private static final int FINISH_ORDER = 0x30;
    private static final int TRANS_OUT = 0x34;
    private static final int TRANS_CANCELLED = 0x35;
    private static final int TRANS_CONNECT_TIME_OUT = 0x36;
    private boolean isLocalRenderInFullscreen = true;
    private VoipManager.VoipAPI voipAPI = VoipManager.get().new VoipAPI();
    private TravelTranslatorSelectedBean travelTranslatorSelectedBean;
    private String comeFrom;
    private String voipToken;
    private String orderId;
    private long timeInfo;
    int w = 1280;
    int h = 720;
    int fps = 30;

    @Override
    public VoiceCallsPresenter createPresenter() {
        return new VoiceCallsPresenter(new VoiceCallsRepository(),this);
    }

    @Override
    public int onLayoutResId() {
        getWindow().addFlags(UIUtils.getVoipWindowFlags());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        return R.layout.activity_video_calls;
    }

    @Override
    public void initData() {
        isLocalRenderInFullscreen = getIntent().getBooleanExtra(EXTRA_IS_LOCAL_RENDER_IN_FULLSCREEN, true);
        travelTranslatorSelectedBean = (TravelTranslatorSelectedBean) getIntent().getSerializableExtra("TRAVEL_TRANS_ORDER");
        comeFrom = getIntent().getExtras().getString("COME_FROM");
        orderId = travelTranslatorSelectedBean.getSessionId();
        VoipManager.get().setVoiceCallClientID(travelTranslatorSelectedBean.getFromUserId());
    }

    @Override
    public boolean supportSlideBack() {
        return false;
    }

    @Override
    public void initViews() {
        initRenderView();
        voipVideoMuteView.setChecked(voipAPI.isMute());

        voipVideoContactsName.setText(travelTranslatorSelectedBean.getUserName());
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
        voipVideoMuteView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 静音
                voipAPI.mute(isChecked);
                buttonView.setAlpha(isChecked ? 0.3f : 1.0f);
            }
        });
        initVoip();
        switch (comeFrom){
            case "CallOrderFragment":
                connectingView();
                getPresenter().timeStart();
                voipToken = getIntent().getExtras().getString("VOIP_TOKEN");
                VoipManager.get().connect(voipToken);
                break;
            case "System_server":
                waitView();
                break;
            case "VoipOverlayService":
                timeInfo = getIntent().getExtras().getLong("TIME_INFO");
                connectedView();
                break;
        }
    }

    private void initRenderView() {
        voipVideoOverlayView.init(VoipManager.get().eglSharedContext(), null);
        voipVideoOverlayView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        voipVideoFullscreenView.init(VoipManager.get().eglSharedContext(), null);
        voipVideoFullscreenView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);

        voipVideoOverlayView.setZOrderMediaOverlay(true);

        voipVideoOverlayView.setEnableHardwareScaler(true /* enabled */);
        voipVideoFullscreenView.setEnableHardwareScaler(true /* enabled */);

        swapRenders(isLocalRenderInFullscreen);
    }

    public void initVoip() {
        VoipManager.get().openLocalVideoCapturer();
        VoipManager.get().setOnVoipListener(new VoipManager.OnVoipListener() {
            @Override
            public void onRemoteVideoAvailable() {
                // 远端视频源可用时, 交换全屏和小窗口,使全屏窗口显示远端视频,小窗口显示本地视频
                swapRenders(!isLocalRenderInFullscreen);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getPresenter().cancelTimer();
                        getPresenter().keepAlive(VoipManager.get().getSessionID(),VoipManager.get().getVoiceCallClientID());
                        connectedView();
                    }
                });
            }

            @Override
            public void onDisconnected() {
                getPresenter().cancelTimer();
                release();
                finish();
            }
        });
    }

    /**
     * 交换全屏和小窗口显示的视频源(远端/本地)
     *
     * @param swap
     */
    private void swapRenders(boolean swap) {
        this.isLocalRenderInFullscreen = swap;
        VoipManager.get().getLocalProxyRenderer().setTarget(swap ? voipVideoFullscreenView : voipVideoOverlayView);
        VoipManager.get().getRemoteProxyRenderer().setTarget(swap ? voipVideoOverlayView : voipVideoFullscreenView);
        suitMirror();
    }

    private void suitMirror() {
        if (isLocalRenderInFullscreen) {
            voipVideoFullscreenView.setMirror(voipAPI.isInFrontCamera());
            voipVideoOverlayView.setMirror(false);
        } else {
            voipVideoOverlayView.setMirror(voipAPI.isInFrontCamera());
            voipVideoFullscreenView.setMirror(false);
        }
    }

    @OnClick({R.id.voip_video_fullscreen_view,R.id.voip_video_overlay_view,
            R.id.voip_video_camera_switch_view,R.id.voip_video_hang_up_view,
            R.id.voip_video_minimize_view,R.id.voip_video_mute_view,
            R.id.voip_video_accept_view,R.id.voip_video_reject_view})
    void click(View view){
        switch (view.getId()){
            case R.id.voip_video_fullscreen_view:
                voipVideoControlView.setVisibility(voipVideoControlView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.voip_video_overlay_view:
                // 大小窗 本地远端视频源互换
                swapRenders(!isLocalRenderInFullscreen);
                break;
            case R.id.voip_video_camera_switch_view:
                // 切换前/后摄像头
                voipAPI.switchCamera(voipAPI.new OnSwitchCamera() {
                    @Override
                    public void done() {
                        suitMirror();
                    }
                });
                break;
            case R.id.voip_video_hang_up_view:
                // 挂断
                VoipManager.get().releaseRenders();
                VoipManager.get().disconnect();
                getPresenter().requestTravelTuserFinish(orderId,"0");
                break;
            case R.id.voip_video_minimize_view:
                // 视频最小化
                VoipManager.get().releaseRenders();
                finish();
                Intent service = new Intent(getApplicationContext(), VoipOverlayService.class);
                service.putExtra(EXTRA_IS_LOCAL_RENDER_IN_FULLSCREEN, isLocalRenderInFullscreen);
                service.putExtra("TRAVEL_TRANS_ORDER", travelTranslatorSelectedBean);
                service.putExtra(CommonConstants.INFO, voipVideoChronometer.getBase());
                startService(service);
                break;
            case R.id.voip_video_reject_view://拒绝
                getPresenter().requestTravelTuserReject(orderId);
                break;
            case R.id.voip_video_accept_view://接受
                requestPermission();
                break;
        }
    }

    public void release() {
        if (voipVideoFullscreenView != null) {
            voipVideoFullscreenView.release();
            voipVideoFullscreenView = null;
        }

        if (voipVideoOverlayView != null) {
            voipVideoOverlayView.release();
            voipVideoOverlayView = null;
        }
    }

    @Override
    public void onBackPressed() {
        // 禁用back键
    }

    @Override
    protected void onDestroy() {
        release();
        if(voipVideoChronometer.getVisibility()==View.VISIBLE){
            voipVideoChronometer.stop();
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        voipAPI.startCapture(w, h, fps);
    }

    private void waitView(){
        voipVideoRejectView.setVisibility(View.VISIBLE);
        voipVideoCameraSwitchView.setVisibility(View.GONE);
        voipVideoAcceptView.setVisibility(View.VISIBLE);
        voipVideoMuteView.setVisibility(View.GONE);
        voipVideoHangUpView.setVisibility(View.INVISIBLE);
        voipVideoMinimizeView.setVisibility(View.GONE);
    }

    private void connectedView(){
        promptTv.setVisibility(View.GONE);
        voipVideoChronometer.setVisibility(View.VISIBLE);
        if(timeInfo!=0){
            voipVideoChronometer.setBase(timeInfo);
        }else{
            voipVideoChronometer.setBase(SystemClock.elapsedRealtime());
        }
        voipVideoChronometer.start();
        voipVideoRejectView.setVisibility(View.GONE);
        voipVideoCameraSwitchView.setVisibility(View.VISIBLE);
        voipVideoAcceptView.setVisibility(View.GONE);
        voipVideoMuteView.setVisibility(View.VISIBLE);
        voipVideoHangUpView.setVisibility(View.VISIBLE);
        voipVideoMinimizeView.setVisibility(View.VISIBLE);
    }

    private void connectingView(){
        promptTv.setVisibility(View.VISIBLE);
        promptTv.setText(getResources().getString(R.string.voice_calls_connect_voip_server));
        voipVideoRejectView.setVisibility(View.GONE);
        voipVideoCameraSwitchView.setVisibility(View.INVISIBLE);
        voipVideoAcceptView.setVisibility(View.GONE);
        voipVideoMuteView.setVisibility(View.INVISIBLE);
        voipVideoHangUpView.setVisibility(View.VISIBLE);
        voipVideoMinimizeView.setVisibility(View.GONE);
    }

    @Override
    public void handlePresenterCallback(Message message) {
        switch (message.what){
            case GET_LANGUAGE:
                TreeMap<Integer, LanguagesBean> treeMap = (TreeMap<Integer, LanguagesBean>) message.obj;
                String fromLanguage = LanguageUtils.getLanguageName(travelTranslatorSelectedBean.getLanguageIds().get(0),treeMap);
                String toLanguage = LanguageUtils.getLanguageName(travelTranslatorSelectedBean.getLanguageIds().get(1),treeMap);
                languageTv.setText(fromLanguage + " | " + toLanguage);
                break;
            case REJECT_ORDER:
                finish();
                break;
            case ACCEPT_ORDER:
                getPresenter().timeStart();
                String rtcToken = message.str;
                VoipManager.get().connect(rtcToken);
                break;
            case FINISH_ORDER:
                getPresenter().cancelTimer();
                if(null!=voipVideoChronometer){
                    voipVideoChronometer.stop();
                }
                finish();
                break;
            case TRANS_OUT:
                getPresenter().cancelTimer();
                if(null!=voipVideoChronometer){
                    voipVideoChronometer.stop();
                }
                finish();
                break;
            case TRANS_CANCELLED:
                finish();
                break;
            case TRANS_CONNECT_TIME_OUT:
                VoipManager.get().releaseRenders();
                VoipManager.get().disconnect();
                getPresenter().requestTravelTuserFinish(orderId,"0");
                break;
        }
    }

    private void requestPermission() {
        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA};
        requestPermission(1, permissions, new Runnable() {
            @Override
            public void run() {
                connectingView();
                getPresenter().requestTravelTuserAccept(orderId);
            }
        });
    }
}
