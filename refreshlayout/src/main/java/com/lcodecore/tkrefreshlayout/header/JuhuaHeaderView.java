package com.lcodecore.tkrefreshlayout.header;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;
import com.lcodecore.tkrefreshlayout.R;
import com.lcodecore.tkrefreshlayout.utils.DensityUtil;

/**
 * Created by lcodecore on 2016/10/3.
 */

public class JuhuaHeaderView extends ImageView implements IHeaderView {
    public JuhuaHeaderView(Context context) {
        this(context, null);
    }

    public JuhuaHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JuhuaHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int size = DensityUtil.dp2px(context, 34);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        setLayoutParams(params);
        setBackgroundResource(R.drawable.anim_loading_view);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
        setRotation(fraction * headHeight / maxHeadHeight * 180 * 6);
    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        setRotation(fraction * headHeight / maxHeadHeight * 180 * 6);
    }

    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
        startAnimation();
    }

    @Override
    public void onFinish(OnAnimEndListener animEndListener) {
        animEndListener.onAnimEnd();
    }

    @Override
    public void reset() {
        stopAnimation();
    }

    private void startAnimation() {
        ((AnimationDrawable) getBackground()).start();
    }

    private void stopAnimation() {
        ((AnimationDrawable) getBackground()).stop();
        ((AnimationDrawable) getBackground()).selectDrawable(0);
        setBackgroundDrawable(null);
        setBackgroundResource(R.drawable.anim_loading_view);
    }
}
