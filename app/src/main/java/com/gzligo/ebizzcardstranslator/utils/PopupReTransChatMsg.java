package com.gzligo.ebizzcardstranslator.utils;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lwd on 2017/6/26.
 */

public class PopupReTransChatMsg {
    @BindView(R.id.tv_re_trans)
    TextView tvReTrans;
    @BindView(R.id.re_trans_img)
    RelativeLayout reTransImg;
    private Unbinder unbinder;
    private PopupWindow reTransChatMsgWindow;
    private OnItemClickListener onItemClickListener;

    public PopupReTransChatMsg(Activity activity,View view) {
        View content = LayoutInflater.from(activity).inflate(R.layout.retranslate_chat_msg, null);
        unbinder = ButterKnife.bind(this, content);
        reTransChatMsgWindow = new PopupWindow(content, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        reTransChatMsgWindow.setBackgroundDrawable(new BitmapDrawable());
        reTransChatMsgWindow.setOutsideTouchable(true);
        reTransChatMsgWindow.setTouchable(true);
        reTransChatMsgWindow.setFocusable(true);
        reTransChatMsgWindow.setAnimationStyle(R.style.AnimTools);
        if (reTransChatMsgWindow.isShowing()) {
            reTransChatMsgWindow.dismiss();
        }

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        reTransChatMsgWindow.showAtLocation(view, Gravity.NO_GRAVITY, view.getWidth()*3/5, location[1]-location[0]*4/5);
        reTransChatMsgWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (unbinder != null) {
                    unbinder.unbind();
                    unbinder = null;
                }
            }
        });
    }

    @OnClick({R.id.re_trans_img})
    void onClick(){
        if (reTransChatMsgWindow.isShowing()) {
            reTransChatMsgWindow.dismiss();
        }
        onItemClickListener.onItemClick();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick();
    }

}
