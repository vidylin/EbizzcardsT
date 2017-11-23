package com.gzligo.ebizzcardstranslator.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;



/**
 * 在tag中声明"draggable"/"undraggable"
 * <p>
 * Created by xfast on 2017/2/17.
 */

public class DraggableLayout extends FrameLayout {

    private static final int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;


    private ViewDragHelper dragHelper;
    private View dragView;


    public DraggableLayout(Context context) {
        this(context, null);
    }

    public DraggableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        final ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        dragHelper = ViewDragHelper.create(this, viewConfiguration.getScaledTouchSlop(), new ViewDragHelper.Callback() {

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == dragView;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
                int maxBottomX = getMeasuredHeight() - child.getMeasuredHeight() - mlp.bottomMargin;
                return Math.min(Math.max(top, mlp.topMargin), maxBottomX);
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
                int maxRightX = getMeasuredWidth() - child.getMeasuredWidth() - mlp.rightMargin;
                return Math.min(Math.max(left, mlp.leftMargin), maxRightX);
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight() - ((MarginLayoutParams) child.getLayoutParams()).topMargin - ((MarginLayoutParams) child.getLayoutParams()).bottomMargin;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth() - ((MarginLayoutParams) child.getLayoutParams()).leftMargin - ((MarginLayoutParams) child.getLayoutParams()).rightMargin;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                MarginLayoutParams mlp = (MarginLayoutParams) releasedChild.getLayoutParams();
                int maxRightX = getMeasuredWidth() - releasedChild.getMeasuredWidth() - mlp.rightMargin;
                dragHelper.settleCapturedViewAt(xvel > 0 ? maxRightX : mlp.leftMargin, releasedChild.getTop());
                invalidate();
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dragView = findViewWithTag("draggable");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        dragHelper.processTouchEvent(ev);
        return dragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING;
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutChildren(left, top, right, bottom, false /* no force left gravity */);
    }

    private void layoutChildren(int left, int top, int right, int bottom, boolean forceLeftGravity) {
        final int count = getChildCount();

        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
//                if (child == dragView) {
//                    child.layout(child.getLeft(), child.getTop(), child.getLeft() + child.getWidth(), child.getTop() + child.getHeight());
//                    continue;
//                }
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft;
                int childTop;

                int gravity = lp.gravity;
                if (gravity == LayoutParams.UNSPECIFIED_GRAVITY) {
                    gravity = DEFAULT_CHILD_GRAVITY;
                }

                final int layoutDirection = getLayoutDirection();
                final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection) & Gravity.HORIZONTAL_GRAVITY_MASK;
                final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

                switch (absoluteGravity) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = parentLeft + (parentRight - parentLeft - width) / 2 + lp.leftMargin - lp.rightMargin;
                        break;
                    case Gravity.RIGHT:
                        if (!forceLeftGravity) {
                            childLeft = parentRight - width - lp.rightMargin;
                            break;
                        }
                    case Gravity.LEFT:
                    default:
                        childLeft = parentLeft + lp.leftMargin;
                }

                switch (verticalGravity) {
                    case Gravity.TOP:
                        childTop = parentTop + lp.topMargin;
                        break;
                    case Gravity.CENTER_VERTICAL:
                        childTop = parentTop + (parentBottom - parentTop - height) / 2 + lp.topMargin - lp.bottomMargin;
                        break;
                    case Gravity.BOTTOM:
                        childTop = parentBottom - height - lp.bottomMargin;
                        break;
                    default:
                        childTop = parentTop + lp.topMargin;
                }

                if (isChildPopulated(child)) {
                    childLeft = child.getLeft();
                    childTop = child.getTop();
                }

                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
        }
    }

    private boolean isChildPopulated(View child) {
        return child.getRight() > 0 && child.getBottom() > 0;
    }
}
