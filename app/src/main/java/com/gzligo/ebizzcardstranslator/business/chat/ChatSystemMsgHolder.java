package com.gzligo.ebizzcardstranslator.business.chat;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.AppManager;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;
import com.gzligo.ebizzcardstranslator.utils.TimeUtils;

import butterknife.BindView;

/**
 * Created by Lwd on 2017/6/21.
 */

public class ChatSystemMsgHolder extends BaseHolder<ChatMessageBean> {
    private static final int START_CHAT = 0x80;
    private static final int END_CHAT = 0x81;
    @BindView(R.id.system_msg) TextView systemMsg;
    @BindView(R.id.system_msg_rl) RelativeLayout systemMsgRl;
    @BindView(R.id.chat_year_moth_day_tv) TextView chatYearMothDayTv;
    @BindView(R.id.year_moth_day_ll) LinearLayout yearMothDayLl;
    @BindView(R.id.chat_year_moth_day_bg) RelativeLayout chatYearMothDayBg;

    public ChatSystemMsgHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(ChatMessageBean data, int position) {
        switch (data.getType()) {
            case START_CHAT:
                yearMothDayLl.setVisibility(View.VISIBLE);
                systemMsgRl.setVisibility(View.VISIBLE);
                chatYearMothDayTv.setText(TimeUtils.getYMD(Long.parseLong(data.getMsgTime())));
                systemMsg.setText(AppManager.get().getApplication().getResources().getString(R.string.start_trans));
                break;
            case END_CHAT:
                Long translateTime = data.getTranslateTime();
                if (null != translateTime) {
                    yearMothDayLl.setVisibility(View.VISIBLE);
                    systemMsgRl.setVisibility(View.VISIBLE);
                    chatYearMothDayBg.setVisibility(View.GONE);
                    systemMsg.setText(AppManager.get().getApplication().getResources().getString(R.string.end_trans) + TimeUtils.second2Time(translateTime));
                }
                break;
        }
    }

    @Override
    public void onRelease() {
    }
}
