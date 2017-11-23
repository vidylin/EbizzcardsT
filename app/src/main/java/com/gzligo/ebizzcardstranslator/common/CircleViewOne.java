package com.gzligo.ebizzcardstranslator.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.gzligo.ebizzcardstranslator.R;

/**
 * Created by Lwd on 2017/9/1.
 */

public class CircleViewOne extends View {
    /**
     * 颜色
     */
    private int mFirstColor;
    /**
     * 圆弧的宽度
     */
    private int mCircleWidth;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 圆弧的度数
     */
    private int mProgress;

    /**
     * 圆弧绘制的速度
     */
    private int mSpeed;

    /**
     * 是否继续绘制
     */
    private boolean isDrawCircle = true;

    public CircleViewOne(Context context) {
        this(context, null);
    }

    public CircleViewOne(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 获取自定义控件的一些值
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CircleViewOne(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleView, defStyleAttr, 0);
        for (int i = 0; i < a.getIndexCount(); i++) {
            switch (a.getIndex(i)) {
                case R.styleable.CircleView_firstColor:
                    mFirstColor = a.getColor(a.getIndex(i), Color.WHITE);
                    break;
                case R.styleable.CircleView_speed:
                    mSpeed = a.getInt(a.getIndex(i), 166);
                    break;
                case R.styleable.CircleView_circleWidth:
                    mCircleWidth = a.getDimensionPixelOffset(a.getIndex(i), (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_PX, 2, getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int center = getWidth() / 2;
        int radius = center - mCircleWidth / 2;
        mPaint.setStrokeWidth(mCircleWidth); // 设置圆环的宽度
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.STROKE); // 设置空心
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius); // 用于定义的圆弧的形状和大小的界限
        mPaint.setColor(mFirstColor); // 设置圆环的颜色
        canvas.drawArc(oval, -90, mProgress, false, mPaint); // 根据进度画圆弧
    }

    public void start(){
        isDrawCircle = true;
        new Thread() {
            @Override
            public void run() {
                while (isDrawCircle) {
                    mProgress++;
                    if (mProgress == 361) {
                        isDrawCircle = false;
                    } else {
                        isDrawCircle = true;
                        postInvalidate();
                        try {
                            Thread.sleep(mSpeed); //通过传递过来的速度参数来决定线程休眠的时间从而达到绘制速度的快慢
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    public void stop(){
        isDrawCircle = false;
        release();
    }

    public void release(){
        mProgress =  0;
        mSpeed = 166;
    }

    public void initSpeed(int voiceTime){
        mSpeed = (voiceTime*1000)/360;
        mSpeed = mSpeed-2;
    }

}
