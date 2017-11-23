package com.gzligo.ebizzcardstranslator.manager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;

import com.github.xgfjyw.webrtcclient.voip.SurfaceViewRenderer;
import com.gzligo.ebizzcardstranslator.business.call.VideoCallsActivity;
import com.gzligo.ebizzcardstranslator.business.call.VoipManager;
import com.gzligo.ebizzcardstranslator.common.FloatWindow;
import com.gzligo.ebizzcardstranslator.constants.CommonConstants;
import com.gzligo.ebizzcardstranslator.persistence.TravelTranslatorSelectedBean;
import com.gzligo.ebizzcardstranslator.utils.UIUtils;

import org.webrtc.RendererCommon;


/**
 * Created by xfast on 2017/9/15.
 */

public class VoipOverlayService extends Service {

    private FloatWindow floatWindow;
    private TravelTranslatorSelectedBean travelTranslatorSelectedBean;
    private SurfaceViewRenderer render;
    private long chronomaterBase;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        final boolean isLocalRender = intent.getBooleanExtra(VideoCallsActivity.EXTRA_IS_LOCAL_RENDER_IN_FULLSCREEN, true);
        travelTranslatorSelectedBean = (TravelTranslatorSelectedBean) intent.getSerializableExtra("TRAVEL_TRANS_ORDER");
        chronomaterBase = intent.getLongExtra(CommonConstants.INFO, 0);
        render = new SurfaceViewRenderer(this);

        final Chronometer chronometer = new Chronometer(this);
        chronometer.setBase(chronomaterBase);
        chronometer.start();

        @SuppressWarnings("ResourceType")
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(UIUtils.dp2px(this, 81), UIUtils.dp2px(this, 144));
        int margin = UIUtils.dp2px(this, 6);
        lp.setMargins(margin, margin, margin, margin);

        render.setLayoutParams(lp);
        render.init(VoipManager.get().eglSharedContext(), null);
        render.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        render.setZOrderMediaOverlay(true);
        render.setEnableHardwareScaler(true);

        floatWindow = new FloatWindow(this, render);

        // 最小化时, 始终显示远端视频
        VoipManager.get().getRemoteProxyRenderer().setTarget(render);

        floatWindow.show();

        floatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
                chronomaterBase = chronometer.getBase();
                Intent activity = new Intent(getApplicationContext(), VideoCallsActivity.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.putExtra(VideoCallsActivity.EXTRA_IS_LOCAL_RENDER_IN_FULLSCREEN, isLocalRender);
                activity.putExtra("TRAVEL_TRANS_ORDER", travelTranslatorSelectedBean);
                activity.putExtra("COME_FROM", "VoipOverlayService");
                activity.putExtra("TIME_INFO", chronomaterBase);
                startActivity(activity);
            }
        });

        VoipManager.get().setOnVoipListener(new VoipManager.OnVoipListener() {
            @Override
            public void onRemoteVideoAvailable() {

            }

            @Override
            public void onDisconnected() {
                stopSelf();
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        floatWindow.hide();
        VoipManager.get().releaseRenders();
        render.release();
        super.onDestroy();
    }
}
