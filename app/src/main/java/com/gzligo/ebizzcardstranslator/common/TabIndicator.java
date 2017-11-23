package com.gzligo.ebizzcardstranslator.common;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.utils.ScreenUtils;

/**
 * Created by Lwd on 2017/9/26.
 */

public class TabIndicator extends android.support.v7.widget.AppCompatImageView{
    private int lineWidth;
    private int offset;

    public TabIndicator(Context context) {
        super(context);
    }

    public TabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int screenW = ScreenUtils.getScreenWidth(AppManager.get().getApplication());
        int height = getLayoutParams().height;
        lineWidth = screenW / 2;
        offset = (screenW / 2 - lineWidth) / 2;
        setMeasuredDimension(lineWidth,height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        setImageMatrix(matrix);
    }

    public void moveX(int from, int to) {
        int one = offset * 2 + lineWidth;
        float fromX = one * from;
        float toX = one * to;
        Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
        animation.setFillAfter(true);
        animation.setDuration(100);
        startAnimation(animation);
    }
}
