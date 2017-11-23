package com.gzligo.ebizzcardstranslator.base.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.mvp.IView;
import com.gzligo.ebizzcardstranslator.base.mvp.Message;
import com.gzligo.ebizzcardstranslator.constants.ChatConstants;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.utils.ButterKnifeUtils;
import com.gzligo.ebizzcardstranslator.utils.PopupReTransChatMsg;
import com.zhy.autolayout.utils.AutoUtils;

import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.LONG_CLICK_HEAD_PORTRAIT;
import static com.gzligo.ebizzcardstranslator.business.chat.ChatFragment.RE_TRANS_CHAT_MSG;

/**
 * Created by xfast on 2017/5/31.
 */

public abstract class BaseHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {
    private OnClickListener mOnClickListener = null;

    public BaseHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        AutoUtils.autoSize(itemView);
        ButterKnifeUtils.bindTarget(this, itemView);
    }

    public abstract void setData(T data, int position);

    public abstract void onRelease();

    @Override
    public void onClick(View view) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(view, getLayoutPosition());
        }
    }

    public interface OnClickListener {
        void onClick(View view, int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    public void longClickHeadPortrait(final int pos, final String userId, ImageView imageView, final IView iView){
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Message msg = Message.obtain(iView);
                msg.what = LONG_CLICK_HEAD_PORTRAIT;
                msg.arg1 = pos;
                msg.obj = userId;
                msg.dispatchToIView();
                return false;
            }
        });
    }

    public void reTranslateMsg(final LinearLayout linearLayout, final int position,
                               final IView iView, final ChatMessageBean data, final String comeForm){
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!data.getIsPrivateMessage() && !data.getIsReTrans() && data.getMsgIsTrans()
                        && comeForm.equals(ChatConstants.COME_FROM_CHAT)) {
                    long msgTime = Long.parseLong(data.getMsgTime());
                    long nowTime = System.currentTimeMillis();
                    if(nowTime-msgTime < ChatConstants.RE_TRANSLATE_MSG_TIME){
                        new PopupReTransChatMsg(AppManager.get().getPreviousActivity(), view).setOnItemClickListener(new PopupReTransChatMsg.OnItemClickListener() {
                            @Override
                            public void onItemClick() {
                                linearLayout.setBackgroundResource(R.drawable.chat_choice_translate_display_left);
                                Message message = Message.obtain(iView);
                                message.what = RE_TRANS_CHAT_MSG;
                                message.arg1 = position;
                                message.dispatchToIView();
                            }
                        });
                    }
                }
                return false;
            }
        });
    }
}
