package com.gzligo.ebizzcardstranslator.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;

/**
 * Created by YJZ on 2016/7/26 0026.
 */
public class TxtSwitch extends RelativeLayout {

    private RelativeLayout mLayoutRl;
    private TextView mTxt;
    private SlideSwitchView mSwitch;
    private String mTxtStr;
    private boolean isVisibleLine;
    private View mLine;

    public TxtSwitch(Context context) {
        this(context, null, 0);

    }

    public TxtSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TxtSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        mLayoutRl = (RelativeLayout) View.inflate(context, R.layout.custom_txt_switch, this);
        mTxt = (TextView) mLayoutRl.findViewById(R.id.custom_txt_switch_txt);
        mSwitch = (SlideSwitchView) mLayoutRl.findViewById(R.id.custom_txt_switch_cs);
        mLine = mLayoutRl.findViewById(R.id.custom_txt_switch_line);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);

        mTxtStr = typedArray.getString(R.styleable.SwitchButton_switch_txt);
        isVisibleLine = typedArray.getBoolean(R.styleable.SwitchButton_switch_line, false);

        if (null != mTxtStr) {
            mTxt.setVisibility(View.VISIBLE);
            mTxt.setText(mTxtStr);
        } else {
            mTxt.setVisibility(View.INVISIBLE);
        }
        mLine.setVisibility(isVisibleLine ? VISIBLE : GONE);
        typedArray.recycle();
    }

    public void setTxtName(int resoureId) {
        mTxt.setText(resoureId);
    }

    public void setTxtName(String name) {
        mTxt.setText(name);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        mSwitch.setOnCheckedChangeListener(listener);
    }

    public void setChecked(boolean checked) {
        mSwitch.setChecked(checked);
    }

    public boolean isChecked() {
        return mSwitch.isChecked();
    }


}
