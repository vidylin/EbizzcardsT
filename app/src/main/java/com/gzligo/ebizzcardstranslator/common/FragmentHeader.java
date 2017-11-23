package com.gzligo.ebizzcardstranslator.common;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

import com.gzligo.ebizzcardstranslator.R;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Lwd on 2017/9/26.
 */

public class FragmentHeader extends AutoRelativeLayout {
    @BindView(R.id.radio_button_one) DrawableCenterRadioButton radioButtonOne;
    @BindView(R.id.radio_button_two) DrawableCenterRadioButton radioButtonTwo;
    @BindView(R.id.radio_group) RadioGroup radioGroup;
    @BindView(R.id.iv_tab_bottom_img) TabIndicator ivTabBottomImg;
    private static final int FRAGMENT_ONE = 0;
    private static final int FRAGMENT_TWO = 1;
    private Unbinder unbinder;
    private int from = -1;
    private int to = -1;

    public FragmentHeader(Context context) {
        this(context,null);
    }

    public FragmentHeader(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FragmentHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    private void initViews(Context context) {
        View view = View.inflate(context, R.layout.fragment_head, this);
        unbinder = ButterKnife.bind(view);
        radioButtonOne.setTextColor(getResources().getColor(R.color.blue));
        radioButtonTwo.setTextColor(getResources().getColor(R.color.black));
    }

    public void initEvents(final OnShowFragmentListener onShowFragmentListener){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_one:
                        from = 1;
                        to = 0;
                        ivTabBottomImg.moveX(from, to);
                        radioButtonOne.setTextColor(getResources().getColor(R.color.blue));
                        radioButtonTwo.setTextColor(getResources().getColor(R.color.black));
                        onShowFragmentListener.onShowFragmentListener(FRAGMENT_ONE);
                        break;
                    case R.id.radio_button_two:
                        from = 0;
                        to = 1;
                        ivTabBottomImg.moveX(from, to);
                        radioButtonOne.setTextColor(getResources().getColor(R.color.black));
                        radioButtonTwo.setTextColor(getResources().getColor(R.color.blue));
                        onShowFragmentListener.onShowFragmentListener(FRAGMENT_TWO);
                        break;
                }
            }
        });
    }

    public interface OnShowFragmentListener {
        void onShowFragmentListener(int which);
    }

    public void tabIndicator(){
        if(null!=ivTabBottomImg&&from!=-1&&to!=-1){
            ivTabBottomImg.moveX(from, to);
        }
    }

    public void setHeadName(String[] strings){
        radioButtonOne.setText(strings[0]);
        radioButtonTwo.setText(strings[1]);
    }

    public void destroyUnbinder(){
        unbinder.unbind();
    }
}
