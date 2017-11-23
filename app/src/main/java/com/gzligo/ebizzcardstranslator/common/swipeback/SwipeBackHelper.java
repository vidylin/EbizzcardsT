package com.gzligo.ebizzcardstranslator.common.swipeback;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.utils.Preconditions;
import com.gzligo.ebizzcardstranslator.utils.UIUtils;

/**
 * Created by xfast on 2017/5/27.
 */

public class SwipeBackHelper extends Handler {

    private static final String TAG = "SwipeBackHelper";

    private static final String EXTRA_CURRENT_POINT_X = "currentPointX"; //点击事件

    private static final int MSG_ACTION_DOWN = 1; //点击事件
    private static final int MSG_ACTION_MOVE = 2; //滑动事件
    private static final int MSG_ACTION_UP = 3;  //点击结束
    private static final int MSG_SLIDE_CANCEL = 4; //开始滑动，不返回前一个页面
    private static final int MSG_SLIDE_CANCELED = 5;  //结束滑动，不返回前一个页面
    private static final int MSG_SLIDE_PROCEED = 6; //开始滑动，返回前一个页面
    private static final int MSG_SLIDE_FINISHED = 7;//结束滑动，返回前一个页面

    private static final int SHADOW_WIDTH = 50; //px 阴影宽度
    private static final int EDGE_SIZE = 20;  //dp 默认拦截手势区间, 足够小标志着边缘滑动

    private int mEdgeSize;  //px 拦截手势区间
    private boolean mIsSliding; //是否正在滑动
    private boolean mIsSlideAnimPlaying; //滑动动画展示过程中
    private float mDistanceX;  //px 当前滑动距离 （正数或0）
    private float mLastPointX;  //记录手势在屏幕上的X轴坐标

    private boolean mIsSupportSlideBack;
    private int mTouchSlop;
    private boolean mIsInThresholdArea;

    private Activity mSlideActivity;
    private ViewManager mViewManager;
    private FrameLayout mCurrentContentView;
    private AnimatorSet mAnimatorSet;

    public SwipeBackHelper(SlideBackManager slideBackManager) {
        Preconditions.checkNotNull(slideBackManager);
        Preconditions.checkNotNull(slideBackManager.getSlideActivity());

        mSlideActivity = slideBackManager.getSlideActivity();
        mIsSupportSlideBack = slideBackManager.supportSlideBack();
        mCurrentContentView = getContentView(mSlideActivity);
        mViewManager = new ViewManager();

        mTouchSlop = UIUtils.getTouchSlop(mSlideActivity);
        mEdgeSize = UIUtils.dp2px(mSlideActivity, EDGE_SIZE); //滑动拦截事件的区域

    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mIsSupportSlideBack) { //不支持滑动返回，则手势事件交给View处理
            return false;
        }

        if (mIsSlideAnimPlaying) {  //正在滑动动画播放中，直接消费手势事件
            return true;
        }

        int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            mLastPointX = ev.getRawX();
            mIsInThresholdArea = mLastPointX >= 0 && mLastPointX <= mEdgeSize;
        }

        if (!mIsInThresholdArea) {  //不满足滑动区域，不做处理
            return false;
        }

        int actionIndex = ev.getActionIndex();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                sendEmptyMessage(MSG_ACTION_DOWN);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (mIsSliding) { //有第二个手势事件加入，而且正在滑动事件中，则直接消费事件
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (actionIndex != 0) { //一旦触发滑动机制，拦截所有其他手指的滑动事件
                    return mIsSliding;
                }

                final float curPointX = ev.getRawX();

                boolean isSliding = mIsSliding;
                if (!isSliding) {
                    if (Math.abs(curPointX - mLastPointX) < mTouchSlop) { //判断是否满足滑动
                        return false;
                    } else {
                        mIsSliding = true;
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putFloat(EXTRA_CURRENT_POINT_X, curPointX);
                Message message = obtainMessage(MSG_ACTION_MOVE);
                message.setData(bundle);
                sendMessage(message);

                if (isSliding != mIsSliding) {
                    MotionEvent cancelEvent = MotionEvent.obtain(ev); //首次判定为滑动需要修正事件：手动修改事件为 ACTION_CANCEL，并通知底层View
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                    mSlideActivity.getWindow().superDispatchTouchEvent(cancelEvent);
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_OUTSIDE:
                if (0 == mDistanceX) { //没有进行滑动
                    mIsSliding = false;
                    sendEmptyMessage(MSG_ACTION_UP);
                    return false;
                }

                if (mIsSliding) {
                    if (0 == actionIndex) { // 取消滑动 或 手势抬起 ，而且手势事件是第一手势，开始滑动动画
                        mIsSliding = false;
                        sendEmptyMessage(MSG_ACTION_UP);
                    }
                    return true;
                }
                break;
            default:
                mIsSliding = false;
                break;
        }
        return false;
    }

    public void finishSwipeImmediately() {
        if (mIsSliding) {
            mViewManager.addCacheView();
            mViewManager.resetPreviousView();
        }

        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }

        removeCallbacksAndMessages(null);

        mSlideActivity = null;
    }

    /**
     * 手动处理滑动事件
     */
    private synchronized void onSliding(float curPointX) {
        final int width = mSlideActivity.getResources().getDisplayMetrics().widthPixels;
        View previewActivityContentView = mViewManager.mPreviousContentView;
        View shadowView = mViewManager.mShadowView;
        View currentActivityContentView = mViewManager.getDisplayView();

        if (previewActivityContentView == null || currentActivityContentView == null || shadowView == null) {
            sendEmptyMessage(MSG_SLIDE_CANCELED);
            return;
        }

        final float distanceX = curPointX - mLastPointX;
        mLastPointX = curPointX;
        mDistanceX = mDistanceX + distanceX;
        if (mDistanceX < 0) {
            mDistanceX = 0;
        }

        previewActivityContentView.setX(-width / 3 + mDistanceX / 3);
        shadowView.setX(mDistanceX - SHADOW_WIDTH);
        currentActivityContentView.setX(mDistanceX);
    }

    /**
     * 开始自动滑动动画
     *
     * @param slideCanceled 是不是要返回（true则不关闭当前页面）
     */
    private void startSlideAnim(final boolean slideCanceled) {
        final View previewView = mViewManager.mPreviousContentView;
        final View shadowView = mViewManager.mShadowView;
        final View currentView = mViewManager.getDisplayView();

        if (previewView == null || currentView == null) {
            return;
        }

        int width = mSlideActivity.getResources().getDisplayMetrics().widthPixels;
        Interpolator interpolator = new DecelerateInterpolator(2f);

        // preview activity's animation
        ObjectAnimator previewViewAnim = new ObjectAnimator();
        previewViewAnim.setInterpolator(interpolator);
        previewViewAnim.setProperty(View.TRANSLATION_X);
        float preViewStart = mDistanceX / 3 - width / 3;
        float preViewStop = slideCanceled ? -width / 3 : 0;
        previewViewAnim.setFloatValues(preViewStart, preViewStop);
        previewViewAnim.setTarget(previewView);

        // shadow view's animation
        ObjectAnimator shadowViewAnim = new ObjectAnimator();
        shadowViewAnim.setInterpolator(interpolator);
        shadowViewAnim.setProperty(View.TRANSLATION_X);
        float shadowViewStart = mDistanceX - SHADOW_WIDTH;
        float shadowViewEnd = slideCanceled ? SHADOW_WIDTH : width + SHADOW_WIDTH;
        shadowViewAnim.setFloatValues(shadowViewStart, shadowViewEnd);
        shadowViewAnim.setTarget(shadowView);

        // current view's animation
        ObjectAnimator currentViewAnim = new ObjectAnimator();
        currentViewAnim.setInterpolator(interpolator);
        currentViewAnim.setProperty(View.TRANSLATION_X);
        float curViewStart = mDistanceX;
        float curViewStop = slideCanceled ? 0 : width;
        currentViewAnim.setFloatValues(curViewStart, curViewStop);
        currentViewAnim.setTarget(currentView);

        // play animation together
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(slideCanceled ? 150 : 300);
        mAnimatorSet.playTogether(previewViewAnim, shadowViewAnim, currentViewAnim);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (slideCanceled) {
                    mIsSlideAnimPlaying = false;
                    previewView.setX(0);
                    shadowView.setX(-SHADOW_WIDTH);
                    currentView.setX(0);
                    sendEmptyMessage(MSG_SLIDE_CANCELED);
                } else {
                    sendEmptyMessage(MSG_SLIDE_FINISHED);
                }
            }
        });
        mAnimatorSet.start();
        mIsSlideAnimPlaying = true;
    }

    private FrameLayout getContentView(Activity activity) {
        return (FrameLayout) activity.findViewById(Window.ID_ANDROID_CONTENT);
    }

    private class ViewManager {
        private Activity mPreviousActivity;
        private View mPreviousContentView;
        private View mShadowView;

        /**
         * Remove view from previous Activity and add into current Activity
         *
         * @return true if view added successfully
         */
        private boolean addViewFromPreviousActivity() {
            if (mCurrentContentView.getChildCount() == 0) {
                mPreviousActivity = null;
                mPreviousContentView = null;
                return false;
            }

            mPreviousActivity = AppManager.get().getPreviousActivity();
            if (mPreviousActivity == null) {
                mPreviousActivity = null;
                mPreviousContentView = null;
                return false;
            }

            //Previous activity not support to be swipeBack...
            if (mPreviousActivity instanceof SlideBackManager &&
                    !((SlideBackManager) mPreviousActivity).canBeSlideBack()) {
                mPreviousActivity = null;
                mPreviousContentView = null;
                return false;
            }

            ViewGroup previousActivityContainer = getContentView(mPreviousActivity);
            if (previousActivityContainer == null || previousActivityContainer.getChildCount() == 0) {
                mPreviousActivity = null;
                mPreviousContentView = null;
                return false;
            }

            mPreviousContentView = previousActivityContainer.getChildAt(0);
            previousActivityContainer.removeView(mPreviousContentView);
            mCurrentContentView.addView(mPreviousContentView, 0);
            return true;
        }

        /**
         * Remove the PreviousContentView at current Activity and put it into previous Activity.
         */
        private void resetPreviousView() {
            if (mPreviousContentView == null) {
                return;
            }

            View view = mPreviousContentView;
            view.setX(0);
            mCurrentContentView.removeView(view);
            mPreviousContentView = null;

            if (mPreviousActivity == null || mPreviousActivity.isFinishing()) {
                return;
            }

            ViewGroup previewContentView = getContentView(mPreviousActivity);
            previewContentView.addView(view);
            mPreviousActivity = null;
        }

        /**
         * add shadow view on the left of content view
         */
        private synchronized void addShadowView() {
            if (mShadowView == null) {
                mShadowView = new ShadowView(mSlideActivity);
                mShadowView.setX(-SHADOW_WIDTH);
            }

            if (mShadowView.getParent() == null) {
                mCurrentContentView.addView(mShadowView, 1, new FrameLayout.LayoutParams(SHADOW_WIDTH, FrameLayout.LayoutParams.MATCH_PARENT));
            } else {
                removeShadowView();
                addShadowView();
            }
        }

        private synchronized void removeShadowView() {
            if (mShadowView == null) {
                return;
            }
            FrameLayout contentView = getContentView(mSlideActivity);
            contentView.removeView(mShadowView);
            mShadowView = null;
        }

        private void addCacheView() {
            PreviousPageView previousPageView = new PreviousPageView(mSlideActivity);
            mCurrentContentView.addView(previousPageView, 0);
            previousPageView.cacheView(mPreviousContentView);
        }

        private View getDisplayView() {
            int index = 0;
            if (mViewManager.mPreviousContentView != null) {
                index = index + 1;
            }

            if (mViewManager.mShadowView != null) {
                index = index + 1;
            }
            return mCurrentContentView.getChildAt(index);
        }
    }

    public interface SlideBackManager {

        Activity getSlideActivity();

        /**
         * 是否支持手势滑动返回
         */
        boolean supportSlideBack();

        /**
         * 能否滑动返回至当前Activity
         */
        boolean canBeSlideBack();

    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_ACTION_DOWN:
                // hide input method
                InputMethodManager inputMethod = (InputMethodManager) mSlideActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = mSlideActivity.getCurrentFocus();
                if (view != null) {
                    inputMethod.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if (!mViewManager.addViewFromPreviousActivity()) return;

                // add shadow view on the left of content view
                mViewManager.addShadowView();

                if (mCurrentContentView.getChildCount() >= 3) {
                    View curView = mViewManager.getDisplayView();
                    if (curView.getBackground() == null) {
                        int color = UIUtils.getWindowBackgroundColor(mSlideActivity);
                        curView.setBackgroundColor(color);
                    }
                }
                break;

            case MSG_ACTION_MOVE:
                final float curPointX = msg.getData().getFloat(EXTRA_CURRENT_POINT_X);
                onSliding(curPointX);
                break;

            case MSG_ACTION_UP:
                final int width = mSlideActivity.getResources().getDisplayMetrics().widthPixels;
                if (mDistanceX == 0) {
                    if (mCurrentContentView.getChildCount() >= 3) {
                        mViewManager.removeShadowView();
                        mViewManager.resetPreviousView();
                    }
                } else if (mDistanceX > width / 4) {
                    sendEmptyMessage(MSG_SLIDE_PROCEED);
                } else {
                    sendEmptyMessage(MSG_SLIDE_CANCEL);
                }
                break;

            case MSG_SLIDE_CANCEL:
                startSlideAnim(true);
                break;

            case MSG_SLIDE_CANCELED:
                mDistanceX = 0;
                mIsSliding = false;
                mViewManager.removeShadowView();
                mViewManager.resetPreviousView();
                break;

            case MSG_SLIDE_PROCEED:
                startSlideAnim(false);
                break;

            case MSG_SLIDE_FINISHED:
                mViewManager.addCacheView();
                mViewManager.removeShadowView();
                mViewManager.resetPreviousView();

                Activity activity = mSlideActivity;
                activity.finish();
                activity.overridePendingTransition(0, 0);
                break;

            default:
                break;
        }
    }
}