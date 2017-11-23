package com.gzligo.ebizzcardstranslator.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RelativeLayout;


/**
 * Created by YJZ on 2016/8/3 0003.
 */
public class ClipImageLayout extends RelativeLayout {

    private static final String TAG = ClipImageLayout.class.getSimpleName();
    private ClipZoomImageView mZoomImageView;
    private ClipImageBorderView mClipImageView;

    /**
     * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
     */
    private int mHorizontalPadding = 20;

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mZoomImageView = new ClipZoomImageView(context);
        mClipImageView = new ClipImageBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);

        // 计算padding的px
        mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources().getDisplayMetrics());
        // mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        // mClipImageView.setHorizontalPadding(mHorizontalPadding);
    }

    public void setZoomImageView(String path) {
        if (mZoomImageView != null) {
            Bitmap decodeFile = BitmapFactory.decodeFile(path);
            mZoomImageView.setImageBitmap(decodeFile);
        } else {
            Log.e(TAG, "path is null.");
        }
    }

    public void setZoomImageBitmap(Bitmap bitmap) {
        if (mZoomImageView != null) {
            mZoomImageView.setImageBitmap(bitmap);
        } else {
            Log.e(TAG, "bitmap is null.");
        }
    }

    /**
     * 对外公布设置边距的方法,单位为dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip() {
        return mZoomImageView.clip();
    }

}
