package com.gzligo.ebizzcardstranslator.common;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by zhangxin on 2016/11/30
 */
public class FloatWindow extends FrameLayout implements OnTouchListener {

    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;

    private boolean mIsRight;
    private float mTouchStartX;
    private float mTouchStartY;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean mDraging;
    private boolean mShowLoader = true;

    private View mView;
    private OnClickListener onClickListener;

    public FloatWindow(Context context, @LayoutRes int floatViewResId) {
        super(context);
        init(context, floatViewResId);
    }

    public FloatWindow(Context context, View contentView) {
        super(context);
        init(context, createFloatView(contentView));
    }

    private void init(Context context, @LayoutRes int resId) {
        init(context, createFloatView(LayoutInflater.from(getContext()).inflate(resId, null)));
    }

    private void init(Context context, View contentView) {
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        this.mWmParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWmParams.gravity = Gravity.TOP | Gravity.LEFT;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        mWmParams.x = 0;
        mWmParams.y = mScreenHeight / 5;

        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.WRAP_CONTENT;
        mView = contentView;
        addView(mView);
        mWindowManager.addView(this, mWmParams);

        hide();
    }

    public View getContentView() {
        return mView;
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        int oldX = mWmParams.x;
        int oldY = mWmParams.y;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                if (mIsRight) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if (mIsRight) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
        }
        mWindowManager.updateViewLayout(this, mWmParams);
    }

    private View createFloatView(View contentView) {
        final View rootFloatView = contentView;
        rootFloatView.setOnTouchListener(this);
        rootFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDraging) {
                    if (onClickListener != null) {
                        onClickListener.onClick(v);
                    }
                }
            }
        });
        rootFloatView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        return rootFloatView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                mDraging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
//                if (Math.abs(mTouchStartX - mMoveStartX) > 10 && Math.abs(mTouchStartY - mMoveStartY) > 10) {
                mDraging = true;
                mWmParams.x = (int) (x - mTouchStartX);
                mWmParams.y = (int) (y - mTouchStartY);
                mWindowManager.updateViewLayout(this, mWmParams);
                return false;
//                }

//                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (mWmParams.x + mView.getWidth() / 2 > mScreenWidth / 2) {
                    mWmParams.x = mScreenWidth;
                    mIsRight = true;
                } else {
                    mIsRight = false;
                    mWmParams.x = 0;
                }
                mWindowManager.updateViewLayout(this, mWmParams);
                mTouchStartX = mTouchStartY = 0;
                break;
        }
        return false;
    }


    private void removeFloatView() {
        try {
            mWindowManager.removeView(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void hide() {
        setVisibility(View.GONE);
    }

    public void show() {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
            if (mShowLoader) {
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                mShowLoader = false;
            }
        }
    }

    public void destroy() {
        hide();
        removeFloatView();
    }

}

