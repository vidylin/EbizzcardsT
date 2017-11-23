package com.gzligo.ebizzcardstranslator.utils;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.business.MainActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lwd on 2017/5/31.
 */

public class PopupWindowUtil {
    private PopupWindow mMorePopupWindow;
    private Unbinder unbinder;
    private IView iView;
    public static final int OFF_LINE_STATE = 0;
    public static final int ON_LINE_STATE = 1;
    public static final int BUSYING_STATE = 2;
    public static final int LEAVE_STATE = 3;

    public PopupWindowUtil(final IView iView, Activity activity, View view) {
        this.iView = iView;
        View content = LayoutInflater.from(activity).inflate(R.layout.popup_dialog, null);
        unbinder = ButterKnife.bind(this, content);
        mMorePopupWindow = new PopupWindow(content, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mMorePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mMorePopupWindow.setOutsideTouchable(true);
        mMorePopupWindow.setTouchable(true);
        mMorePopupWindow.setFocusable(true);
        mMorePopupWindow.setAnimationStyle(R.style.AnimTools);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int width = (int) (ScreenUtils.getScreenHeight(activity) * 0.11);
        mMorePopupWindow.showAsDropDown(view, -width, location[1] - view.getHeight()+20);
        mMorePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (unbinder != null) {
                    unbinder.unbind();
                    unbinder = null;
                }
                Message message = Message.obtain(iView);
                message.what= MainActivity.POPUP_DISMISS;
                message.dispatchToIView();
            }
        });

}

    @OnClick({R.id.free_tv, R.id.busy_tv, R.id.leave_tv, R.id.off_duty})
    void click(View view) {
        Message message = Message.obtain(iView);
        switch (view.getId()) {
            case R.id.free_tv:
                message.obj = ON_LINE_STATE;
                break;
            case R.id.busy_tv:
                message.obj = BUSYING_STATE;
                break;
            case R.id.leave_tv:
                message.obj = LEAVE_STATE;
                break;
            case R.id.off_duty:
                message.obj = OFF_LINE_STATE;
                break;
        }
        message.what= MainActivity.POPUP_MSG;
        message.dispatchToIView();
        mMorePopupWindow.dismiss();
    }

}
