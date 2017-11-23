package com.gzligo.ebizzcardstranslator.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.zhy.autolayout.AutoFrameLayout;

/**
 * Created by ZuoJian on 2017/5/24.
 */

public class ToolActionBar extends AutoFrameLayout {
    private View mLayout;

    /**
     * close title r3 r2 r1.
     */
    private TextView mTxtClose;
    private TextView mTxtCenterTitle;
    private TextView mTxtRight4;
    private TextView mTxtRight3;
    private TextView mTxtRight2;
    private TextView mTxtRight1;

    private int mIsRight4, mIsRight3, mIsRight2, mIsRight1;

    private int mCenterTitle, mClose;

    public ToolActionBar(Context context) {
        this(context, null, 0);
    }

    public ToolActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolActionBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mLayout = View.inflate(context, R.layout.toolbar, this);
        mTxtClose = (TextView) mLayout.findViewById(R.id.tv_close);
        mTxtCenterTitle = (TextView) mLayout.findViewById(R.id.txt_title);

        mTxtRight1 = (TextView) mLayout.findViewById(R.id.tv_right1);
        mTxtRight2 = (TextView) mLayout.findViewById(R.id.tv_right2);
        mTxtRight3 = (TextView) mLayout.findViewById(R.id.tv_right3);
        mTxtRight4 = (TextView) mLayout.findViewById(R.id.tv_right4);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.APP_ActionBar);

        try {
            final int count = typedArray.getIndexCount();
            for (int i = 0; i < count; i++) {
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    case R.styleable.APP_ActionBar_show_back_icon:
                        setCloseIcon(R.drawable.close_selector);
                        break;
                    case R.styleable.APP_ActionBar_txt_close:
                        mClose = typedArray.getResourceId(attr, -1);
                        break;
                    case R.styleable.APP_ActionBar_txt_center_title:
                        mCenterTitle = typedArray.getResourceId(attr, -1);
                        break;
                    case R.styleable.APP_ActionBar_txt_right1:
                        mIsRight1 = typedArray.getResourceId(attr, R.color.text_color);
                        break;
                    case R.styleable.APP_ActionBar_txt_right2:
                        mIsRight2 = typedArray.getResourceId(attr, -1);
                        break;
                    case R.styleable.APP_ActionBar_txt_right3:
                        mIsRight3 = typedArray.getResourceId(attr, -1);
                        break;
                    case R.styleable.APP_ActionBar_txt_right4:
                        mIsRight4 = typedArray.getResourceId(attr, -1);
                        break;
                }
            }
            isSetting();
        } finally {
            typedArray.recycle();
        }
    }

    public void reset() {
        setCloseTxt(null);
        setTitleTxt(null);
        setRight1Txt(null);
        setRight2Txt(null);
        setRight3Txt(null);
        setRight4Txt(null);
        setCloseIcon(-1);
        setRight1Icon(-1);
        setRight2Icon(-1);
        setRight3Icon(-1);
        setRight4Icon(-1);
    }

    private void isSetting() {
        if (mClose > 0) {
            mTxtClose.setText(getResources().getString(mClose));
        }
        if (mCenterTitle > 0) {
            mTxtCenterTitle.setText(getResources().getString(mCenterTitle));
        }

        if (mIsRight1 > 0) {
            mTxtRight1.setText(getResources().getString(mIsRight1));
        }

        if (mIsRight2 > 0) {
            mTxtRight2.setText(getResources().getString(mIsRight2));
        }

        if (mIsRight3 > 0) {
            mTxtRight3.setText(getResources().getString(mIsRight3));
        }

        if (mIsRight4 > 0) {
            mTxtRight4.setText(getResources().getString(mIsRight4));
        }

    }

    public void setRight1Color(int color) {
        mTxtRight1.setTextColor(color);
    }

    public void setCloseTxt(String txt) {
        mTxtClose.setText(txt);
    }

    public void setCloseTxt(int txt) {
        mTxtClose.setText(txt);
    }

    public void setTitleTxt(String txt) {
        mTxtCenterTitle.setText(txt);
    }

    public void setTitleTxt(int txt) {
        mTxtCenterTitle.setText(txt);
    }

    public void setRight1Txt(String txt) {
        mTxtRight1.setText(txt);
    }

    public void setRight1Txt(int txt) {
        mTxtRight1.setText(txt);
    }

    public void setRight2Txt(String txt) {
        mTxtRight2.setText(txt);
    }

    public void setRight2Txt(int txt) {
        mTxtRight2.setText(txt);
    }

    public void setRight3Txt(String txt) {
        mTxtRight3.setText(txt);
    }

    public TextView getRight4Txt() {
        return mTxtRight4;
    }

    public void setRight4Txt(String txt) {
        mTxtRight4.setText(txt);
    }

    public void setRight3Txt(int txt) {
        mTxtRight3.setText(txt);
    }

    public TextView getCloseTxt() {
        return mTxtClose;
    }

    public void setCloseColor(int color) {
        mTxtClose.setTextColor(getResources().getColor(color));
    }

    public TextView getTitleTxt() {
        return mTxtCenterTitle;
    }

    public TextView getRight3Txt() {
        return mTxtRight3;
    }

    public TextView getRight2Txt() {
        return mTxtRight2;
    }

    public TextView getRight1Txt() {
        return mTxtRight1;
    }


    public void setActionbarCenterTitle(String text) {
        mTxtCenterTitle.setText(text);
    }

    public void setActionbarCenterTitleMaxEms(int maxEms) {
        mTxtCenterTitle.setMaxEms(maxEms);
    }

    public void setActionbarCenterTitle(int left) {
        mTxtCenterTitle.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
    }

    public void setOnActionbarCenterOnClickListener(OnClickListener listener) {
        mTxtCenterTitle.setOnClickListener(listener);
    }

    public void setRight1Visible(boolean visible) {
        mTxtRight1.setVisibility(visible == true ? View.VISIBLE : View.GONE);
    }

    public void setRight2Visible(boolean visible) {
        mTxtRight2.setVisibility(visible == true ? View.VISIBLE : View.GONE);
    }

    public void setCloseIcon(int resId) {
        if (resId < 0) {
            mTxtClose.setCompoundDrawables(null, null, null, null);
        } else {
            mTxtClose.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        }
    }

    public void setCenterTitle(String title) {
        mTxtCenterTitle.setText(title);
    }


    public void setRight1Icon(int left) {
        if (left < 0) {
            mTxtRight1.setCompoundDrawables(null, null, null, null);
        } else {
            mTxtRight1.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
        }
    }

    public void setRight2Icon(int left) {
        if (left < 0) {
            mTxtRight2.setCompoundDrawables(null, null, null, null);
        } else {
            mTxtRight2.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
        }
    }

    public void setRight3Icon(int left) {
        if (left < 0) {
            mTxtRight3.setCompoundDrawables(null, null, null, null);
        } else {
            mTxtRight3.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
        }
    }

    public void setRight4Icon(int left) {
        if (left < 0) {
            mTxtRight4.setCompoundDrawables(null, null, null, null);
        } else {
            mTxtRight4.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
        }
    }

    // -----------------set on click--------------------

    public void setOnCloseClickListener(OnClickListener l) {
        mTxtClose.setOnClickListener(l);
    }

    public void setOnRight1ClickListener(OnClickListener l) {
        mTxtRight1.setOnClickListener(l);
    }

    public void setOnRight2ClickListener(OnClickListener l) {
        mTxtRight2.setOnClickListener(l);
    }

    public void setOnRight3ClickListener(OnClickListener l) {
        mTxtRight3.setOnClickListener(l);
    }

    public void setOnRight4ClickListener(OnClickListener l) {
        mTxtRight4.setOnClickListener(l);
    }
}
