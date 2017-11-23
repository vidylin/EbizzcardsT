package com.gzligo.ebizzcardstranslator.business.chat;

import android.view.View;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseHolder;
import com.gzligo.ebizzcardstranslator.persistence.NewTransOrderBean;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Lwd on 2017/6/29.
 */

public class ChatActivityAdapter extends BaseAdapter<NewTransOrderBean> {
    private TreeMap<Integer, Integer> integerTreeMap;
    private List<Boolean> onLineLists;
    private int choiceWhich;

    public void setChoiceWhich(int choiceWhich) {
        this.choiceWhich = choiceWhich;
    }

    public ChatActivityAdapter(List<NewTransOrderBean> orderBeanList, TreeMap<Integer, Integer> integerTreeMap, List<Boolean> onLineLists, int choiceItem) {
        super(orderBeanList);
        this.integerTreeMap = integerTreeMap;
        this.onLineLists = onLineLists;
        this.choiceWhich = choiceItem;
    }

    @Override
    public ChatActivityHolder getHolder(View v, int viewType) {
        return new ChatActivityHolder(v, integerTreeMap, onLineLists, choiceWhich);
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.trans_group_item;
    }

    @Override
    public void onBindViewHolder(BaseHolder<NewTransOrderBean> holder, int position, List<Object> payloads) {
        ChatActivityHolder chatActivityHolder = (ChatActivityHolder) holder;
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
            if (position == choiceWhich) {
                chatActivityHolder.transGroupsItem.setBackgroundResource(R.color.white);
            } else {
                chatActivityHolder.transGroupsItem.setBackgroundResource(R.color.chat_head_bg);
            }
        } else {
            String string = payloads.get(0).toString().replace("[", "").replace("]", "");
            switch (string) {
                case "choice":
                    chatActivityHolder.transGroupsItem.setBackgroundResource(R.color.white);
                    break;
                case "unChoice":
                    chatActivityHolder.transGroupsItem.setBackgroundResource(R.color.chat_head_bg);
                    break;
                default:
                    int count = Integer.parseInt(string);
                    if (count <= 0) {
                        chatActivityHolder.peopleImgRl.hiddenBadge();
                    } else {
                        chatActivityHolder.peopleImgRl.showTextBadge(string);
                    }
                    break;
            }
        }
    }

    public void updateOnLineLists( List<Boolean> onLineLists){
        this.onLineLists = onLineLists;
    }
}
