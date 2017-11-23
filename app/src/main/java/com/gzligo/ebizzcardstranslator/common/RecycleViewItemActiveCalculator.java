package com.gzligo.ebizzcardstranslator.common;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.gzligo.ebizzcardstranslator.business.chat.ChatAdapter;
import com.gzligo.ebizzcardstranslator.persistence.ChatMessageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lwd on 2017/8/31.
 */

public class RecycleViewItemActiveCalculator {
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    public RecycleViewItemActiveCalculator(RecyclerView recyclerView, ChatAdapter chatAdapter) {
        this.recyclerView = recyclerView;
        this.chatAdapter = chatAdapter;
    }

    public void calculator(List<ChatMessageBean> messageBeanList){
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager&&null!=messageBeanList&&messageBeanList.size()>0) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            int lastItemPosition = linearManager.findLastVisibleItemPosition();
            int firstItemPosition = linearManager.findFirstVisibleItemPosition();
            for(int i=firstItemPosition;i<=lastItemPosition;i++){
                ChatMessageBean chatMsg = messageBeanList.get(i);
                if(chatMsg.getType()==4||chatMsg.getType()==5){
                    List<String> payloads = new ArrayList<>();
                    payloads.add("PLAY_VIDEO");
                    chatAdapter.notifyItemChanged(i, payloads);
                }
            }
        }
    }
}
