package com.gzligo.ebizzcardstranslator.common;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Lwd on 2017/6/27.
 */

public class PreImageViewPager extends ViewPager {
    public PreImageViewPager(Context context) {
        super(context);
    }

    public PreImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
